package com.beessoft.dyyd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.Logger;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

public class LoginActivity extends Activity {
	
	private Context context;
	private LocationClient mLocationClient;
	private String Mac;

	private ImageButton imgBtn;
	private CheckBox savePassword;
	private EditText editText1, editText2;
	private TextView textView1;
	private String IMSI;
	private String user, pass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		context=this;
		
		textView1 = (TextView) findViewById(R.id.version_text);
		savePassword = (CheckBox) findViewById(R.id.remember_password);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		imgBtn = (ImageButton) findViewById(R.id.imageButton);

		// 声明百度定位sdk的构造函数
		mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

		Mac = GetInfo.getIMEI(LoginActivity.this);

		textView1.setText(User.version + User.getVersionName(this));

		Gps gps = new Gps(context);
		gps.openGPSSettings(context);
		if (Gps.exist(LoginActivity.this, "distance.db")) {
			Gps.GPS_do(mLocationClient, 8000);
		}

		IMSI = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE))
				.getSubscriberId();

		editText1.setText(PreferenceUtil.readString(context, "username"));
		Boolean isCheck = PreferenceUtil.readBoolean(context, "isCheck");
		// 判断记住密码多选框的状态
		if (isCheck) {
			// 设置默认是记录密码状态
			savePassword.setChecked(true);
			editText2.setText(PreferenceUtil.readString(context, "password"));
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
				user = editText1.getText().toString();
				pass = editText2.getText().toString();
				ProgressDialogUtil.showProgressDialog(context);
				visitServer_login(user, pass, Mac);
			}
		});
	}


	public void registerXGPush(String username) {
		// 开启logcat输出，方便debug，发布时请关闭
		XGPushConfig.enableDebug(this, false);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
		// XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContextx
		Context mContext = getApplicationContext();
//		Log.d("TPush", "注册账户：" + username);
		String name = "*";
		if (!Tools.isEmpty(username)) {
			name = username;
		}
		XGPushManager.registerPush(mContext, name, new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {

//				Log.d("TPush", "注册成功，设备token为：" + data);
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
//				Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
			}
		});
	}

	private void visitServer_login(String usercode,String ipass, String mac) {
		String httpUrl = User.mainurl + "app/app_login";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("usercode", usercode);
		parameters_userInfo.put("ipass", ipass);
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("sim", IMSI);
		parameters_userInfo.put("version", User.getVersionCode(context)+"");

		client_request.get(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
//							Logger.e(dataJson.toString());
							switch (dataJson.getString("code")) {
							case "0":
								PreferenceUtil.write(context, "username", user);
								if (savePassword.isChecked()) {
									PreferenceUtil.write(context, "password", pass);
								}
								registerXGPush(user);
								String role = dataJson.getString("role");
								PreferenceUtil.write(context, "role", role);
								String ifCheck = dataJson.getString("kq");//0考，1不
								PreferenceUtil.write(context, "ifCheck", ifCheck);

								int ifgps = dataJson.getInt("ifgps");//ifgps 0 允许室外签到 1不允许
								PreferenceUtil.write(context, "ifgps", ifgps);
								CrashReport.setUserId(user);
								visitServer_PersonInfo();
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
		parameters_userInfo.put("cmaker", Mac);
		parameters_userInfo.put("usercode", GetInfo.getUserName(context));
		
		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							PreferenceUtil.write(context, "dw", dataJson.getString("cdepname"));
							PreferenceUtil.write(context, "cdepname", dataJson.getString("dw"));
							PreferenceUtil.write(context, "name", dataJson.getString("name"));
							PreferenceUtil.write(context, "tel", dataJson.getString("tel"));
							PreferenceUtil.write(context, "sim", dataJson.getString("sim"));
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							ToastUtil.toast(context, "登陆成功");
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this,MainActivity.class);
							startActivity(intent);
						}
					}
				});
	}
}
