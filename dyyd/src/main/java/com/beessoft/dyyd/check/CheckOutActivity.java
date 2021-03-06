package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.CheckOutDatabaseHelper;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.AlarmUtils;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckOutActivity extends BaseActivity implements View.OnClickListener {

    private LocationClient mLocationClient;
    private TextView addrText, mileageText, typeText;
    private TextView insideText;
    private TextView visitText;
    private EditText summaryEdit, planEdit;
    private String yesterday, summary, plan, type;
    private String longitude, latitude, addr;
    private Thread mThread;
    private String distance, distanceSum, distanceCar;

    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识
    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private CheckOutDatabaseHelper checkOutkHelper; // 数据库帮助类
    private SQLiteDatabase checkOutDb;

    List<HashMap<String,String>> pins = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {// 此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    addrText.setText("[" + type + "]" + addr);// textView显示从定位获取到的地址
                    // 获取拜访信息，以及当日里程等信息
                    getSummary();
                    break;
                case MSG_FAILURE:
                    addrText.setText("请重新定位");
                    break;
            }
        }
    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //条用基类的方法，以便调出系统菜单（如果有的话）
//        super.onCreateOptionsMenu(menu);
//        menu.add(0, Constant.YESTERDAY, 0, "上个工作日计划").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        //返回值为“true”,表示菜单可见，即显示菜单
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (GetInfo.getIfSf(context)) {
//            menu.setGroupVisible(0, true);
//        } else {
//            menu.setGroupVisible(0, false);
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case Constant.YESTERDAY:
//                openYesterday();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        context = CheckOutActivity.this;

        initView();

        // 检查Gps是否打开
        Gps gps = new Gps(this);
        gps.openGPSSettings(this);
        // 判断是否签到那就启动
        if (Gps.exist(CheckOutActivity.this, "distance.db")) {
            Gps.GPS_do(mLocationClient, 8000);
        }
        // 获取定位
        getAddrLocation();

        if (Gps.exist(CheckOutActivity.this, "checkout.db")) {
            Cursor cursor = checkOutDb.rawQuery("select *from checkout_table", null);
            if (cursor.moveToLast()) {
                summaryEdit.setText(cursor.getString(1));
                summaryEdit.setSelection(cursor.getString(1).length());//移动光标到文本最后的位置
                planEdit.setText(cursor.getString(2));
                planEdit.setSelection(cursor.getString(2).length());
            }
            cursor.close();
        }
    }

    public void initView() {

        visitText = (TextView) findViewById(R.id.txt_visit);

        addrText = (TextView) findViewById(R.id.location_text);
        mileageText = (TextView) findViewById(R.id.distance_text);
        typeText = (TextView) findViewById(R.id.type_text);
        insideText = (TextView) findViewById(R.id.txt_inside);

        summaryEdit = (EditText) findViewById(R.id.summary_text);
        planEdit = (EditText) findViewById(R.id.plan_text);

        LinearLayout mileageLl = (LinearLayout) findViewById(R.id.ll_mileage);

//        if (GetInfo.getIfSf(context)) {
//            mileageLl.setVisibility(View.VISIBLE);
//            summaryEdit.setHint("书写的内容包含但不限于：拟拜访的商家名称和拜访的原因" +
//                    "（上门检查、上门培训、酬金沟通、指标传达、帮扶整改），拟组织开展的促销活动，拟拜访的用户等。");
//        }

        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        checkOutkHelper = new CheckOutDatabaseHelper(CheckOutActivity.this, "checkout.db", 1);
        checkOutDb = checkOutkHelper.getReadableDatabase();

        findViewById(R.id.txt_preserve).setOnClickListener(this);
        findViewById(R.id.txt_refresh).setOnClickListener(this);
        findViewById(R.id.txt_map).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(CheckOutActivity.this, "distance.db")) {
            addrText.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(
                    getApplicationContext(), "distance.db", 1);
            longitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);
            getAddr();
            mThread.start();// 线程启动
            distanceHelper.close();
        } else {
            addrText.setText("正在定位...");
            Gps.GPS_do(mLocationClient, 1100);
            mThread.start();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int sleepcount = 1500;
            if (!Gps.exist(CheckOutActivity.this, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                sleepcount = 3300;
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Gps.exist(CheckOutActivity.this, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                addr = myApp.getAddr();
                longitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (TextUtils.isEmpty(addr)) {
                    getAddr();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(addr)) {
                    mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                } else {
                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                }
            } else {
                if (TextUtils.isEmpty(addr)) {
                    mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                } else {
                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                }
            }
        }
    };


//    private void openYesterday() {
//
//        final TextView inputServer = new TextView(this);
//        inputServer.setBackgroundResource(R.drawable.bigtext_bg);
//        inputServer.setText(yesterday);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("昨日计划").setView(inputServer);
//        builder.setPositiveButton("确认", null);
//        builder.show();
//    }


    private void getSummary() {

        String httpUrl = User.mainurl + "sf/offwork";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("sf", ifSf);

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            pins.clear();
                            String visitInfo = "";

                            if (!"".equals(dataJson.getString("yday"))) {
                                visitInfo = "[上级" + dataJson.getString("yday")
                                        + "号意见]"
                                        + dataJson.getString("veropinion")
                                        + "\n";
                            }
                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
                                visitInfo = getVisit(dataJson, visitInfo);
                            } else if (code.equals("1")) {
                                ToastUtil.toast(context, "没有当天拜访记录");
                            } else if (code.equals("4")) {
                                visitInfo = getVisit(dataJson, visitInfo);
                                ToastUtil.toast(context, "签到待审批，不能签退");
                            } else if ("5".equals(dataJson.getString("code"))) {
                                ToastUtil.toast(context, "签到待审批，不能签退");
                            }

//                            summary = summaryEdit.getText().toString();
//                            if (TextUtils.isEmpty(summary.trim())) {
//                                summaryEdit.setText(text1);// 今日总结唯空的时候赋值给文本框
//                                int textLength = text1.length();
//                                summaryEdit.setSelection(textLength, textLength);//将光标移到最后
//                            }
                            visitText.setText(visitInfo);

                            distanceSum = dataJson.getString("kmsum");
                            distance = dataJson.getString("kmval");
                            distanceCar = dataJson.getString("kmcar");
                            String exp = "";
                            if ("0".equals(dataJson.getString("outflag"))) {
                                exp = "离开区域时间:" + dataJson.getString("outtime")
                                        + ",当日里程" + distanceSum + "Km,有效里程"
                                        + distance + "Km,派车里程" + distanceCar
                                        + "Km";
                            } else if ("-1".equals(dataJson
                                    .getString("outflag"))) {
                                exp = "离开区域时间:无，当日里程" + distanceSum + "Km,有效里程"
                                        + distance + "Km,派车里程" + distanceCar
                                        + "Km";
                            }

                            mileageText.setText(exp);

                            typeText.setText(dataJson.getString("type"));

                            yesterday = dataJson.getString("ytomplan");
                            if (dataJson.has("ifout")) {
                                int inside = dataJson.getInt("ifout");
                                String a = inside == 0 ? "是" : "否";
                                insideText.setText(a);
                            }


                            //签到点位置获取
//                            if (dataJson.getString("typecode").equals("0")) {
                            JSONArray arrayJwd = dataJson.getJSONArray("jwdlist");
                            for (int j = 0; j < arrayJwd.length(); j++) {
                                JSONObject obj = arrayJwd.getJSONObject(j);
                                HashMap<String,String> map = new HashMap<>();
                                map.put("latitude",obj.getString("lat"));
                                map.put("longitude",obj.getString("lng"));
                                map.put("fw",obj.getString("fw"));
                                pins.add(map);
                            }
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public String getVisit(JSONObject dataJson, String text1)
                            throws JSONException {
                        String text = "";
                        JSONArray array = dataJson.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            text = object.getString("visit");

                            text1 += Tools.chineseNum[i] + "、" + text + "\n";
                        }
                        return text1;
                    }
                });
    }

    private void saveData(String location) {
        String httpUrl = User.mainurl + "sf/offwork_save";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("todsummary", Escape.escape(summary));
        parameters_userInfo.put("tomplan", Escape.escape(plan));
        parameters_userInfo.put("addr", Escape.escape(location));
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("km", distance);
        parameters_userInfo.put("kmsum", distanceSum);
        parameters_userInfo.put("kmcar", distanceCar);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
//                                if (GetInfo.getIfSf(context)) {
//                                    ToastUtil.toast(context, "提交成功" + "今日距离为" + distance + "公里,请提醒上级审批");
//                                } else {
                                    ToastUtil.toast(context, "提交成功");
//                                }
                                // 删除distance.db数据库
                                deleteDatabase("distance.db");
                                // 删除checkout.db数据库
                                deleteDatabase("checkout.db");
                                mLocationClient.stop();
                                AlarmUtils.cancelAlarm(context);
                                finish();
                            } else if (code.equals("4")) {
                                ToastUtil.toast(context, "签到待审批，不能签退");
                            } else if (code.equals("5")) {
//                                if (GetInfo.getIfSf(context)) {
//                                    ToastUtil.toast(context, "提交成功,在有效范围外签退" + "今日距离为" + distance + "公里,请提醒上级审批");
//                                } else {
                                    ToastUtil.toast(context, "提交成功,在有效范围外签退");
//                                }
                                // 删除distance.db数据库
                                deleteDatabase("distance.db");
                                // 删除checkout.db数据库
                                deleteDatabase("checkout.db");
                                mLocationClient.stop();
                                AlarmUtils.cancelAlarm(context);
                                finish();
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


//    private void saveDy(String location) {
//        String httpUrl = User.dyMainurl + "sf/offwork_save";
//        AsyncHttpClient client_request = new AsyncHttpClient();
//        RequestParams parameters_userInfo = new RequestParams();
//        parameters_userInfo.put("mac", mac);
//        parameters_userInfo.put("usercode", username);
//        parameters_userInfo.put("todsummary", Escape.escape(summary));
//        parameters_userInfo.put("tomplan", Escape.escape(plan));
//        parameters_userInfo.put("addr", Escape.escape(location));
//        parameters_userInfo.put("jd", longitude);
//        parameters_userInfo.put("wd", latitude);
//        parameters_userInfo.put("km", distance);
//        parameters_userInfo.put("kmsum", distanceSum);
//        parameters_userInfo.put("kmcar", distanceCar);
//        parameters_userInfo.put("sf", ifSf);
//
//        client_request.post(httpUrl, parameters_userInfo,
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(String response) {
//                        try {
//                            JSONObject dataJson = new JSONObject(response);
//                            String code = dataJson.getString("code");
//                            if (code.equals("0")) {
//
//                            } else if (code.equals("4")) {
//
//                            } else if (code.equals("5")) {
//
//                            } else {
//                                ToastUtil.toast(context, getResources().getString(R.string.dy_wrong_mes));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            ProgressDialogUtil.closeProgressDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable error, String data) {
//                        error.printStackTrace(System.out);
//                        ProgressDialogUtil.closeProgressDialog();
//                    }
//
//                });
//    }

    public void getAddr() {
        String httpUrl = "http://api.map.baidu.com/geocoder/v2/";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("ak", "jfPNMgVWhuLSzggtryKGSchd");
        parameters_userInfo.put("callback", "renderReverse");
        parameters_userInfo.put("location", latitude + "," + longitude);
        parameters_userInfo.put("output", "json");
        parameters_userInfo.put("pois", "0");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            JSONObject obj = dataJson.getJSONObject("result");
                            addr = obj.getString("formatted_address");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkOutkHelper.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_preserve:
                summary = summaryEdit.getText().toString();
                plan = planEdit.getText().toString();
                checkOutDb.execSQL("insert into checkout_table values(null,?,?)", new String[]{summary, plan});
                ToastUtil.toast(context, "保存成功");
                break;
            case R.id.txt_refresh:
                getAddrLocation();
                break;
            case R.id.txt_map:
                if (pins.size() > 0){
                    Intent intent = new Intent();
                    intent.setClass(context,QueryMapListActivity.class);
                    intent.putExtra("pin",(Serializable) pins);
                    intent.putExtra("jd",longitude);
                    intent.putExtra("wd",latitude);
                    startActivity(intent);
                }else {
                    ToastUtil.toast(context,"等待位置判断再查看");
                }
                break;
            case R.id.btn_confirm:
                summary = summaryEdit.getText().toString();
                plan = planEdit.getText().toString();
                final String location = addrText.getText().toString();
//                String noticeNum = PreferenceUtil.readString(context, "noticenum");
//                if (GetInfo.getIfSf(context)) {
//                    if ("0".equals(noticeNum)) {
//                        if (TextUtils.isEmpty(summary.trim())
//                                || TextUtils.isEmpty(plan.trim())
//                                || TextUtils.isEmpty(location.trim())) {
//                            ToastUtil.toast(context, "数据不能为空");
//                        } else if (plan.trim().length() < 30) {
//                            ToastUtil.toast(context, "明日计划字数不足30个字，请填写");
//                        } else {
//                            String a = insideText.getText().toString();
//                            if ("否".equals(a)) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                builder.setTitle("不再有效范围是否确认提交")
//                                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                ProgressDialogUtil.showProgressDialog(context);
//                                                saveData(location);
////                                                if (GetInfo.getIfSf(context))
////                                                    saveDy(location);
//                                            }
//                                        }).setNegativeButton("否", null);
//                                builder.show();
//                            } else {
//                                ProgressDialogUtil.showProgressDialog(context);
//                                saveData(location);
////                                if (GetInfo.getIfSf(context))
////                                    saveDy(location);
//                            }
//                        }
//                    } else {
//                        ToastUtil.toast(context, "有未读通知，不能签退");
//                    }
//                } else {
                    String a = insideText.getText().toString();
                    if ("否".equals(a)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("不再有效范围是否确认提交")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ProgressDialogUtil.showProgressDialog(context);
                                        saveData(location);
//                                        if (GetInfo.getIfSf(context))
//                                            saveDy(location);
                                    }
                                }).setNegativeButton("否", null);
                        builder.show();
                    } else {
                        ProgressDialogUtil.showProgressDialog(context);
                        saveData(location);
//                        if (GetInfo.getIfSf(context))
//                            saveDy(location);
                    }
//                }
                break;
        }
    }
}