package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.update.UpdateManager;
import com.beessoft.dyyd.utils.AlarmReceiver;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.PhotoUtil;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class CheckInActivity extends BaseActivity {

    private LocationClient mLocationClient;
    private String location, explain="", iclass="", type="", longitude,
            latitude, addr, flag="", journey="";
    private TextView addrText, ifInsideText;
    private EditText reasonEditText;

    private int code = 5;

    private Thread mThread;

    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识


    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private UpdateManager mUpdateManager;
    private AutoCompleteTextView notInsideAutoCompleteText;
    private AutoCompleteTextView typeAutoCompleteText;

    private Chronometer chronometer;

    // 照片
    private static final int PHOTO_CODE = 5;
    private String uploadBuffer = "";
    private ImageView photoImage;
    private String imgPath = "";
    private Bitmap bitmap = null;

    private int ifgps;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    if (ifgps==0){
                        if ("Gps".equals(type)) {
                            addrText.setText("[" + type + "]" + addr);
                            visitServer_get(longitude, latitude);
                        } else {
                            addrText.setText("无GPS信号，请刷新定位");
                        }
                    }else{
                        addrText.setText("[" + type + "]" + addr);
                        visitServer_get(longitude, latitude);
                    }
                    break;
                case MSG_FAILURE:
                    addrText.setText("请重新定位");
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        if(savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))){
//            Logger.e("拍摄异常，获取原来的shot_path");
            imgPath = savedInstanceState.getString("imgPath");
        }
        context = CheckInActivity.this;
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
        mac = GetInfo.getIMEI(context);

        ifgps = PreferenceUtil.readInt(context,"ifgps");

        addrText = (TextView) findViewById(R.id.location_text);
        ifInsideText = (TextView) findViewById(R.id.ifinside_text);
        reasonEditText = (EditText) findViewById(R.id.explain_text);

        notInsideAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.class_text);
        typeAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.journey_type);

        photoImage = (ImageView) findViewById(R.id.checkin_image);
        chronometer = (Chronometer) findViewById(R.id.chronometer1);

        // 设置不弹出键盘
        notInsideAutoCompleteText.setInputType(InputType.TYPE_NULL);
        typeAutoCompleteText.setInputType(InputType.TYPE_NULL);

        mUpdateManager = new UpdateManager(this);
        mUpdateManager.checkUpdate(false);//判断是否应该升级,是否显示说明

        Gps gps = new Gps(this);
        gps.openGPSSettings(this);
        if (Gps.exist(CheckInActivity.this, "distance.db")) {
            Gps.GPS_do(mLocationClient, 8000);
        }

        // 定位
        getAddrLocation();

        // setFormat设置用于显示的格式化字符串。
        // 格式化字符串:如果指定，计时器将根据这个字符串来显示，替换字符串中第一个“%s”为当前"MM:SS"或 "H:MM:SS"格式的时间显示。
        chronometer.setFormat("%s");

        findViewById(R.id.checkin_location).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ifInsideText.setText("");
                addrText.setText("正在定位...");
                mThread = new Thread(runnable);
                mThread.start();
            }
        });

        findViewById(R.id.checkin_photo).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            @Override
            public void onClick(View v) {
                if (Tools.isSDCardExit()) {
                    takePhoto();
                } else {
                    ToastUtil.toast(context, "内存卡不存在不能拍照");
                }
            }
        });

        photoImage.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isEmpty(imgPath)) {
                    PhotoHelper.openPictureDialog(context, imgPath);
                }
            }
        });

        findViewById(R.id.checkin_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flag == "1") {
                    location = addrText.getText().toString();
                    explain = reasonEditText.getText().toString();
                    iclass = notInsideAutoCompleteText.getText().toString();
                    journey = typeAutoCompleteText.getText().toString();
//                    if (!TextUtils.isEmpty(journey.trim())) {
                        if (ifgps == 1) {
                            postData();
                        } else if (ifgps == 0) {
                            if ("Gps".equals(type)) {
                                postData();
                            } else {
                                ToastUtil.toast(context, "无GPS信号，请刷新定位");
                            }
                        }
//                    } else {
//                        ToastUtil.toast(context, "请选择出行方式");
//                    }
                } else {
                    ToastUtil.toast(context, "请等待，有效范围判断");
                }
            }
        });
        notInsideAutoCompleteText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                notInsideAutoCompleteText.showDropDown();// 显示下拉列表
                return false;
            }
        });
        typeAutoCompleteText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                typeAutoCompleteText.showDropDown();// 显示下拉列表
                return false;
            }
        });
    }

    private void postData() {
        if (code == 0) {//正常
            ProgressDialogUtil.showProgressDialog(context);
            visitServer(location, iclass, explain, journey);
        } else if (code == 1) {//非有效范围
            if (!Tools.isEmpty(uploadBuffer)) {
                if (TextUtils.isEmpty(explain.trim())
                        || TextUtils.isEmpty(iclass.trim())) {
                    ToastUtil.toast(context, "请填写数据，再上传");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("不再有效范围是否确认提交")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDialogUtil.showProgressDialog(context);
                            visitServer(location, iclass, explain, journey);
                        }
                    }).setNegativeButton("否",null);
                    builder.show();
                }
            } else {
                ToastUtil.toast(context, "请先拍照，再提交");
            }
        } else if (code == 2) {
            ToastUtil.toast(context, "无人员信息，不可签到");
        } else if (code == 3) {//迟到
            if (!Tools.isEmpty(uploadBuffer)) {
                if (TextUtils.isEmpty(explain.trim())) {
                    ToastUtil.toast(context, "请填写数据，再上传");
                } else {
                    ProgressDialogUtil.showProgressDialog(context);
                    visitServer(location, iclass, explain, journey);
                }
            } else {
                ToastUtil.toast(context, "请先拍照，再提交");
            }
        }
    }

    public void takePhoto() {
        imgPath = Tools.getSDPath() + "/dyyd/photo.jpg";
        // 必须确保文件夹路径存在，否则拍照后无法完成回调
        File vFile = new File(imgPath);
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        Uri uri = Uri.fromFile(vFile);
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
        // 打开新的activity，这里是系统摄像头
        startActivityForResult(intent, PHOTO_CODE);

    }

    // 相机返回处理
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            uploadBuffer = "";
            switch (requestCode) {
                case PHOTO_CODE:
                    if (!Tools.isEmpty(imgPath)) {
                        File imageFile = new File(imgPath);
                        bitmap = PhotoUtil.imageEncode(imageFile);
                        photoImage.setImageBitmap(bitmap);
                        uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
                        imgPath = "";
                    }else{
                        ToastUtil.toast(context,"相机问题");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public void getAddrLocation() {
        flag = "0";
        mThread = new Thread(runnable);
        addrText.setText("正在定位...");
        Gps.GPS_do(mLocationClient, 1100);
        mThread.start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int sleepcount = 5500;
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocationApplication myApp = (LocationApplication) getApplication();
            addr = myApp.getaddr();
            longitude = myApp.getjd();
            latitude = myApp.getwd();
            type = myApp.getType();
            if (addr == null) {
                visitServer_getaddr();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (addr == null) {
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
            } else {
                mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
            }
        }
    };

    private void doalarm() {
        // 定义闹钟参数
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(this, AlarmReceiver.class);
        in.setAction("Test");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, in, 0);

        // 设置闹钟为循环模式，每两分钟触发一次
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                1000 * 60, pi); // Millisec * Second * Minute
    }

    private void visitServer_get(String longitude, String latitude) {
        String httpUrl = User.mainurl + "sf/startwork";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//                            Logger.e(dataJson.toString());
                            String codeA = dataJson.getString("code");
                            if (codeA.equals("0")) {
                                ifInsideText.setText("是");
                                code = 0;
                                notInsideAutoCompleteText.setKeyListener(null);
                                reasonEditText.setKeyListener(null);
                                notInsideAutoCompleteText.setBackgroundResource(R.drawable.unedit_text_bg);
                                notInsideAutoCompleteText.setHint(null);
                                reasonEditText.setBackgroundResource(R.drawable.unedit_text_bg);
                            } else if (codeA.equals("1")) {
                                ifInsideText.setText("否");
                                code = 1;
                                JSONArray array = dataJson.getJSONArray("list");
                                List<String> list = new ArrayList<String>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    list.add(obj.get("name").toString());
                                }
                                com.beessoft.dyyd.utils.ArrayAdapter<String> adapter = new com.beessoft.dyyd.utils.ArrayAdapter<String>(
                                        CheckInActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        list);
                                notInsideAutoCompleteText.setAdapter(adapter);
                                notInsideAutoCompleteText.setHint("点击获取");
                            } else if (codeA.equals("2")) {
                                ifInsideText.setText("未登记");
                                code = 2;
                            } else if (codeA.equals("3")) {
                                ifInsideText.setText("是");
                                notInsideAutoCompleteText.setKeyListener(null);
                                notInsideAutoCompleteText.setBackgroundResource(R.drawable.unedit_text_bg);
                                notInsideAutoCompleteText.setHint(null);
                                code = 3;
                            }

                            if (dataJson.getString("typecode").equals("0")) {
                                JSONArray array1 = dataJson.getJSONArray("typelist");
                                List<String> list1 = new ArrayList<String>();
                                for (int j = 0; j < array1.length(); j++) {
                                    JSONObject obj1 = array1.getJSONObject(j);
                                    list1.add(obj1.get("name").toString());
                                }
                                com.beessoft.dyyd.utils.ArrayAdapter<String> adapter2 = new com.beessoft.dyyd.utils.ArrayAdapter<String>(
                                        CheckInActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        list1);
                                typeAutoCompleteText.setAdapter(adapter2);
                                typeAutoCompleteText.setHint("点击获取");
                            } else {
                                ToastUtil.toast(context, "没有出行方式");
                            }
//							String confirmDay = dataJson.getString("conday");
//							if (!"0".equals(confirmDay)) {
//								Toast.makeText(
//										CheckInActivity.this,
//										dataJson.getString("conday")
//												+ "日未确认日志，有效里程"
//												+ dataJson.getString("mykm")
//												+ "公里未计算", Toast.LENGTH_SHORT)
//										.show();
//							}

                            int stoppedMilliseconds = 0;

                            String chronoText = dataJson.getString("now");
                            String array[] = chronoText.split(":");
                            if (array.length == 2) {
                                stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
                                        + Integer.parseInt(array[1]) * 1000;
                            } else if (array.length == 3) {
                                stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000
                                        + Integer.parseInt(array[1]) * 60 * 1000
                                        + Integer.parseInt(array[2]) * 1000;
                            }

                            chronometer.setBase(SystemClock.elapsedRealtime()
                                    - stoppedMilliseconds);
                            chronometer.start();

                            flag = "1";// 判断信息读取完成
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void visitServer(String location, String iclass, String explain, String journey) {

        String httpUrl = User.mainurl + "sf/startwork_save";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("addr", Escape.escape(location));
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("image", uploadBuffer);
        parameters_userInfo.put("iclass", Escape.escape(iclass));
        parameters_userInfo.put("cmemo", Escape.escape(explain));
        parameters_userInfo.put("type", Escape.escape(journey));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
                                // 删除distance.db数据库
                                deleteDatabase("distance.db");
                                distanceHelper = new DistanceDatabaseHelper(
                                        getApplicationContext(), "distance.db", 1);
                                String time = DateUtil.getDateLoca();
                                distanceHelper
                                        .getReadableDatabase()
                                        .execSQL(
                                                "insert into distance_table values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                                new String[]{time, longitude,
                                                        latitude, "0", "0",
                                                        "0", type, "0", "0"});
                                distanceHelper.close();
                                Gps.GPS_do(mLocationClient, 8000);// 启动百度定位的8秒轮询
                                doalarm();
                                String cdFlag = dataJson.getString("cdflag");
                                if ("1".equals(cdFlag)) {
                                    ToastUtil.toast(context, "签到成功,当日迟到");
                                } else {
                                    ToastUtil.toast(context, "签到成功");
                                }
                                finish();
                            } else if (dataJson.getString("code").equals("2")) {
                                ToastUtil.toast(context, "当日已签到");
                            } else {
                                ToastUtil.toast(context, "请重新提交");
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


    public void visitServer_getaddr() {
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
    protected void onSaveInstanceState(Bundle outState) {
        if(!TextUtils.isEmpty(imgPath)){
            outState.putString("imgPath", imgPath);
        }
        super.onSaveInstanceState(outState);
    }

}