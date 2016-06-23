package com.beessoft.dyyd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.igexin.sdk.PushManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private Context context;
    private LocationClient mLocationClient;
    private String mac;

    private ImageButton imgBtn;
    private CheckBox savePassword;
    private EditText nameEdt, passEdt;
    private TextView versionTxt;
    private String IMSI;
    private String user, pass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;

        versionTxt = (TextView) findViewById(R.id.version_text);
        savePassword = (CheckBox) findViewById(R.id.remember_password);
        nameEdt = (EditText) findViewById(R.id.editText1);
        passEdt = (EditText) findViewById(R.id.editText2);
        imgBtn = (ImageButton) findViewById(R.id.imageButton);

        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        mac = GetInfo.getIMEI(LoginActivity.this);

        versionTxt.setText(User.version + User.getVersionName(context));

        Gps gps = new Gps(context);
        gps.openGPSSettings(context);
        if (Gps.exist(LoginActivity.this, "distance.db")) {
            Gps.GPS_do(mLocationClient, 8000);
        }

        IMSI = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getSubscriberId();

        nameEdt.setText(PreferenceUtil.readString(context, "username"));
        Boolean isCheck = PreferenceUtil.readBoolean(context, "isCheck");
        // 判断记住密码多选框的状态
        if (isCheck) {
            // 设置默认是记录密码状态
            savePassword.setChecked(true);
            passEdt.setText(PreferenceUtil.readString(context, "password"));
        }

        // 监听记住密码多选框按钮事件
        savePassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (savePassword.isChecked()) {
                    PreferenceUtil.write(context, "isCheck", true);
                } else {
                    PreferenceUtil.write(context, "isCheck", false);
                }
            }
        });

        imgBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                user = nameEdt.getText().toString();
                pass = passEdt.getText().toString();
                ProgressDialogUtil.showProgressDialog(context);
                visitServer_login(user, pass, mac);
            }
        });
    }


//    public void registerXGPush(String username) {
//        // 开启logcat输出，方便debug，发布时请关闭
//        XGPushConfig.enableDebug(this, false);
//        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
//        // XGIOperateCallback)带callback版本
//        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
//        // 具体可参考详细的开发指南
//        // 传递的参数为ApplicationContextx
//        Context mContext = getApplicationContext();
////		Log.d("TPush", "注册账户：" + username);
//        String name = "*";
//        if (!Tools.isEmpty(username)) {
//            name = username;
//        }
//        XGPushManager.registerPush(mContext, name, new XGIOperateCallback() {
//            @Override
//            public void onSuccess(Object data, int flag) {
//
////				Log.d("TPush", "注册成功，设备token为：" + data);
//            }
//
//            @Override
//            public void onFail(Object data, int errCode, String msg) {
////				Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
//            }
//        });
//    }

    private void visitServer_login(String usercode, String ipass, String mac) {

        String httpUrl = User.mainurl + "app/app_login";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("usercode", usercode);
        parameters_userInfo.put("ipass", ipass);
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("sim", IMSI);
        parameters_userInfo.put("version", User.getVersionCode(context) + "");

        client_request.get(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            switch (dataJson.getString("code")) {
                                case "0":
                                    PreferenceUtil.write(context, "username", user);
                                    if (savePassword.isChecked()) {
                                        PreferenceUtil.write(context, "password", pass);
                                    }
//                                    registerXGPush(user);
//								String role = dataJson.getString("role");
//								PreferenceUtil.write(context, "role", role);
                                    int ifCheck = dataJson.getInt("kq");//0考，1不
                                    if (0 == ifCheck) {
                                        PreferenceUtil.write(context, "ifCheck", true);
                                    } else {
                                        PreferenceUtil.write(context, "ifCheck", false);
                                    }
                                    if (dataJson.has("sf")) {
                                        int ifSf = dataJson.getInt("sf");//0什邡，1不是
                                        if (0 == ifSf) {
                                            PreferenceUtil.write(context, "ifSf", true);
                                        } else {
                                            PreferenceUtil.write(context, "ifSf", false);
                                        }
                                    }
                                    if (dataJson.has("ifgps")) {
                                        int ifgps = dataJson.getInt("ifgps");//ifgps 0 允许室外签到 1不允许
                                        if (0 == ifgps) {
                                            PreferenceUtil.write(context, "ifgps", true);
                                        } else {
                                            PreferenceUtil.write(context, "ifgps", false);
                                        }
                                    }
                                    if (dataJson.has("rolecode")) {
                                        int roleCode = dataJson.getInt("rolecode");//权限控制
                                        if (roleCode == 0) {
                                            JSONArray array = dataJson.getJSONArray("roleList");
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject object = array.getJSONObject(i);
                                                String buttonCode = object.getString("code");
                                                String role1 = object.getString("role");
                                                PreferenceUtil.write(context, "rolebuttoncode" + buttonCode, role1);
                                            }
                                        }
                                    }
                                    CrashReport.setUserId(user);
                                    visitServer_PersonInfo();
                                    PushManager.getInstance().bindAlias(context,user);
                                    break;
                                case "1":
                                    ToastUtil.toast(context, "账号或密码错误");
                                    break;
                                case "2":
                                    ToastUtil.toast(context, "身份验证成功");
                                    break;
                                case "3":
                                    ToastUtil.toast(context, "用户名或密码为空");
                                    break;
                                case "4":
                                    ToastUtil.toast(context, "设备信息非法，请与管理员联系");
                                    break;
                                case "5":
                                    ToastUtil.toast(context, "用户不存在");
                                    break;
                                case "6":
                                    ToastUtil.toast(context, "版本不正确，请删除重新下载");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        error.printStackTrace(System.out);
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }

    private void visitServer_PersonInfo() {

        String httpUrl = User.mainurl + "app/personal_info";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("cmaker", mac);
        parameters_userInfo.put("usercode", GetInfo.getUserName(context));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            PreferenceUtil.write(context, "dw", dataJson.getString("dw"));
                            PreferenceUtil.write(context, "cdepname", dataJson.getString("cdepname"));
                            PreferenceUtil.write(context, "name", dataJson.getString("name"));
                            PreferenceUtil.write(context, "tel", dataJson.getString("tel"));
                            PreferenceUtil.write(context, "sim", dataJson.getString("sim"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Intent intent = new Intent();
                            intent.setClass(context, MainActivity.class);
                            startActivity(intent);
                            ToastUtil.toast(context, "登陆成功");
                        }
                    }
                });
    }
}
