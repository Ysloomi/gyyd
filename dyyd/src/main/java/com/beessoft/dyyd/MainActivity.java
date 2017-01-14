package com.beessoft.dyyd;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.check.AskLeaveActivity;
import com.beessoft.dyyd.check.CollectActivity;
import com.beessoft.dyyd.dailywork.MyMileageActivity;
import com.beessoft.dyyd.dailywork.MyWorkActivity;
import com.beessoft.dyyd.dailywork.NoteActivity;
import com.beessoft.dyyd.dailywork.NoteQueryActivity;
import com.beessoft.dyyd.dailywork.PhotoActivity;
import com.beessoft.dyyd.dailywork.WorkLocationActivity;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.update.UpdateManager;
import com.beessoft.dyyd.utils.AlarmUtils;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.company_txt)
    TextView companyTxt;
    @BindView(R.id.depart_txt)
    TextView departTxt;
    @BindView(R.id.name_txt)
    TextView nameTxt;
    @BindView(R.id.tel_txt)
    TextView telTxt;
    @BindView(R.id.check_img)
    ImageView checkImg;
    @BindView(R.id.time_txt)
    TextView timeTxt;
    @BindView(R.id.check_txt)
    TextView checkTxt;
    @BindView(R.id.check_btn)
    Button checkBtn;
    @BindView(R.id.mywork_btn)
    Button myworkBtn;
    @BindView(R.id.visit_btn)
    Button visitBtn;
    @BindView(R.id.askleave_btn)
    Button askleaveBtn;
    @BindView(R.id.info_collect_btn)
    Button infoCollectBtn;
    @BindView(R.id.check_collect_btn)
    Button checkCollectBtn;
    @BindView(R.id.location_btn)
    Button locationBtn;
    @BindView(R.id.note_btn)
    Button noteBtn;
    @BindView(R.id.mileage_btn)
    Button mileageBtn;

    private ArrayList<String> typeList = new ArrayList<>();
    private UpdateManager mUpdateManager;

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private LocationClient mLocationClient;

    interface Type {
        String CHECKIN = "activity_checkin";
        String CHECKIN_UNTAP = "activity_checkin_untap";
        String CHECKOUT = "activity_checkout";
        String CHECKOUT_UNTAP = "activity_checkout_untap";
        String CHECKQUERY = "activity_checkquery";
        String CHECKQUERY_UNTAP = "activity_checkquery_untap";

        String REACH = "activity_reach";
        String REACH_UNTAP = "activity_reach_untap";
        String LEAVE = "activity_leave";
        String LEAVE_UNTAP = "activity_leave_untap";
        String VISITQUERY = "activity_visitquery";
        String VISITQUERY_UNTAP = "activity_visitquery_untap";
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_actions, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_share:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, ShareActivity.class);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(false);// 使导航栏出现返回按钮

        context = MainActivity.this;

        mUpdateManager = new UpdateManager(this);//判断是否应该升级
        mUpdateManager.checkUpdate(false);//是否弹出版本更新信息

        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        companyTxt.setText("单位:" + PreferenceUtil.readString(context, "dw"));
        departTxt.setText("部门:" + PreferenceUtil.readString(context, "cdepname"));
        nameTxt.setText("姓名:" + PreferenceUtil.readString(context, "name"));
        telTxt.setText("电话:" + PreferenceUtil.readString(context, "tel"));


//        GetInfo.getButtonRole(context, checkInButton, "1", "checkin");
//        GetInfo.getButtonRole(context, checkOutButton, "1", "checkout");
//        GetInfo.getButtonRole(context, visitReachBtn, "2", "reach");
//        GetInfo.getButtonRole(context, visitLeaveBtn, "2", "leave");
        if (!"0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))
                && !"0".equals(PreferenceUtil.readString(context, "rolebuttoncode7"))) {
            Drawable drawableTopCheckIn = context.getResources().getDrawable(R.drawable.main_check_untap);
            checkBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCheckIn, null, null);
            checkBtn.setTextColor(0xffc8c8c8);
        }

        if (!"0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))
                && !"0".equals(PreferenceUtil.readString(context, "rolebuttoncode11"))) {
            Drawable drawableTopCheckIn = context.getResources().getDrawable(R.drawable.main_visit_untap);
            visitBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCheckIn, null, null);
            visitBtn.setTextColor(0xffc8c8c8);
        }
        GetInfo.getButtonRole(context, checkCollectBtn, "3", "");
        GetInfo.getButtonRole(context, askleaveBtn, "4", "");
        GetInfo.getButtonRole(context, infoCollectBtn, "5", "");
        GetInfo.getButtonRole(context, myworkBtn, "6", "");
//        GetInfo.getButtonRole(context, che, "7", "");
        GetInfo.getButtonRole(context, locationBtn, "8", "");
        GetInfo.getButtonRole(context, noteBtn, "9", "");
//        GetInfo.getButtonRole(context, visitqueryBtn, "11", "");
        GetInfo.getButtonRole(context, mileageBtn, "12", "");
    }

    @Override
    // 点返回时不退出 ，亦可不用（因service已保持在后台运行）
    // 改写返回键事件监听，使得back键功能类似home键，让Acitivty退至后台时不被系统销毁，代码如下：
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(
                new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent
                    .setComponent(new ComponentName(ai.packageName, ai.name));
            startActivitySafely(startIntent);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.check_btn, R.id.mywork_btn, R.id.visit_btn, R.id.askleave_btn,
            R.id.info_collect_btn, R.id.check_collect_btn, R.id.location_btn, R.id.note_btn, R.id.mileage_btn})
    public void onClick(View view) {
        typeList.clear();
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.check_btn:
                if (!"0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))
                        && !"0".equals(PreferenceUtil.readString(context, "rolebuttoncode7"))) {
                    ToastUtil.toast(context, "无权限");
                } else {
                    if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))) {
                        typeList.add(Type.CHECKIN);
                        typeList.add(Type.CHECKOUT);
                    } else {
                        typeList.add(Type.CHECKIN_UNTAP);
                        typeList.add(Type.CHECKOUT_UNTAP);
                    }
                    if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode7"))) {
                        typeList.add(Type.CHECKQUERY);
                    } else {
                        typeList.add(Type.CHECKQUERY_UNTAP);
                    }
                    DialogActivity.navToDialog(context, typeList);
                }
                break;
            case R.id.mywork_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode6"))) {
                    intent.setClass(context, MyWorkActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.visit_btn:
                if (!"0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))
                        && !"0".equals(PreferenceUtil.readString(context, "rolebuttoncode11"))) {
                    ToastUtil.toast(context, "无权限");
                } else {
                    if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))) {
                        typeList.add(Type.REACH);
                        typeList.add(Type.LEAVE);
                    } else {
                        typeList.add(Type.REACH_UNTAP);
                        typeList.add(Type.LEAVE_UNTAP);
                    }
                    if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode11"))) {
                        typeList.add(Type.VISITQUERY);
                    } else {
                        typeList.add(Type.VISITQUERY_UNTAP);
                    }
                    DialogActivity.navToDialog(context, typeList);
                }
                break;
            case R.id.askleave_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode4"))) {
                    intent.setClass(context, AskLeaveActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.info_collect_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode5"))) {
                    intent.setClass(context, PhotoActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.check_collect_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode3"))) {
                    intent.setClass(context, CollectActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.location_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode8"))) {
                    intent.setClass(context, WorkLocationActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.note_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode9"))) {
                    if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode10"))) {
                        intent.setClass(context, NoteActivity.class);
                    } else {
                        intent.setClass(context, NoteQueryActivity.class);
                    }
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.mileage_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                    intent.setClass(context, MyMileageActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        visitServer();
    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/startwork_do";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String code = dataJson.getString("icount");
                            if ("0".equals(code)) {
                                checkImg.setImageResource(R.drawable.main_uncheckin);
                                checkTxt.setText("未签到");
                            } else if ("1".equals(code)) {
                                if (!Gps.exist(context, "distance.db")) {
                                    distanceHelper = new DistanceDatabaseHelper(context.getApplicationContext(), "distance.db", 1);
                                    String time = DateUtil.getDateLoca();
                                    distanceHelper
                                            .getReadableDatabase()
                                            .execSQL(
                                                    "insert into distance_table values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                                    new String[]{time, "0",
                                                            "0", "0", "0",
                                                            "0", "", "0", "0"});
                                    distanceHelper.close();
                                    Gps.GPS_do(mLocationClient, 8000);// 启动百度定位的8秒轮询
                                    AlarmUtils.doalarm(context);
                                }
                                checkImg.setImageResource(R.drawable.main_checkin);
                                checkTxt.setText("已签到");
                            } else if ("2".equals(code)) {
                                checkImg.setImageResource(R.drawable.main_checkin);
                                checkTxt.setText("已签退");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
