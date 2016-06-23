package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.check.QueryMapListActivity;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.PhotoUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteDealActivity extends BaseActivity implements View.OnClickListener {

    private TextView locationTxt;
    private TextView ifInsideTxt;
    private TextView departText;
    private TextView nameText;
    private TextView addrText;
    private TextView startText;
    private TextView endText;
    private TextView planText;

    private LinearLayout locationLl;
    private LinearLayout ifInsideLl;
    private LinearLayout outReasonLl;
    private LinearLayout questionLl;
    private LinearLayout adviseLl;
    private LinearLayout reasonLl;
    private LinearLayout effectLl;
    private RelativeLayout photoRl;

    private EditText questionEdt;
    private EditText adviseEdt;
    private EditText reasonEdt;
    private EditText effectEdt;
    private EditText outReasonEdt;

    private String from;
    private String state;
    private String addr;
    private String addrCode;
    private String rtCode;
    private String question;
    private String advise;

    private Note note = new Note();

    public static final int PHOTO_CODE = 5;
    // 创建Bitmap对象
    private Bitmap bitmap;
    private String uploadBuffer = null;
    private ImageView photoImage;
    private String imgPath = "";

    private String longitude, latitude, location,type;
    private Thread mThread;
    private LocationClient mLocationClient;
    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识
    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private boolean ifLocation = false;

    private String leavetype="";

    List<HashMap<String,String>> pins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_deal);

        if(savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))){
//            Logger.e("拍摄异常，获取原来的shot_path");
            imgPath = savedInstanceState.getString("imgPath");
        }

        context = NoteDealActivity.this;
        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;


        Bundle b = getIntent().getExtras();
        note = b.getParcelable("note");
        from = b.getString("from");
        state = b.getString("state");
        addr = b.getString("addr");
        addrCode = b.getString("addrCode");
        rtCode = b.getString("rtCode");

        initView();
        initData();
    }

    private void initView() {

        locationTxt = (TextView) findViewById(R.id.txt_location);
        ifInsideTxt = (TextView) findViewById(R.id.txt_ifinside);

        departText = (TextView) findViewById(R.id.txt_depart);
        nameText = (TextView) findViewById(R.id.txt_name);
        addrText = (TextView) findViewById(R.id.txt_addr);
        startText = (TextView) findViewById(R.id.edt_start);
        endText = (TextView) findViewById(R.id.edt_end);
        planText = (TextView) findViewById(R.id.txt_plan);

        locationLl = (LinearLayout) findViewById(R.id.ll_location);
        ifInsideLl= (LinearLayout) findViewById(R.id.ll_ifinside);
        outReasonLl= (LinearLayout) findViewById(R.id.ll_out_reason);
        questionLl = (LinearLayout) findViewById(R.id.ll_question);
        adviseLl = (LinearLayout) findViewById(R.id.ll_advise);
        reasonLl = (LinearLayout) findViewById(R.id.ll_reason);
        effectLl = (LinearLayout) findViewById(R.id.ll_effect);
        photoRl = (RelativeLayout) findViewById(R.id.rl_photo);


        questionEdt = (EditText) findViewById(R.id.edt_question);
        adviseEdt = (EditText) findViewById(R.id.edt_advise);
        reasonEdt = (EditText) findViewById(R.id.edt_reason);
        effectEdt = (EditText) findViewById(R.id.edt_effect);
        outReasonEdt = (EditText) findViewById(R.id.edt_out_reason);

        photoImage = (ImageView) findViewById(R.id.img_photo);

        photoImage.setOnClickListener(this);
        findViewById(R.id.txt_take_photo).setOnClickListener(this);
        findViewById(R.id.txt_refresh).setOnClickListener(this);
        findViewById(R.id.txt_map).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }


    private void initData() {

        departText.setText(note.getDepart());
        nameText.setText(note.getName());
        startText.setText(note.getStart());
        endText.setText(note.getEnd());
        planText.setText(note.getPlan());

        if ("result".equals(from)){
            if ("未走访".equals(state)){
                questionLl.setVisibility(View.GONE);
                adviseLl.setVisibility(View.GONE);
                reasonLl.setVisibility(View.VISIBLE);
                effectLl.setVisibility(View.GONE);
                setTitle("未走访处理");
            }else if ("已走访".equals(state)){
                questionLl.setVisibility(View.VISIBLE);
                adviseLl.setVisibility(View.VISIBLE);
                reasonLl.setVisibility(View.GONE);
                effectLl.setVisibility(View.GONE);
                setTitle("已走访处理");
            }
            locationLl.setVisibility(View.GONE);
            ifInsideLl.setVisibility(View.GONE);
            photoRl.setVisibility(View.GONE);
        }else if ("effect".equals(from)){
            question = getIntent().getStringExtra("question");
            advise = getIntent().getStringExtra("advise");
            questionLl.setVisibility(View.VISIBLE);
            adviseLl.setVisibility(View.VISIBLE);
            questionEdt.setText(question);
            questionEdt.setKeyListener(null);
            questionEdt.setBackground(getResources().getDrawable(R.drawable.textshape_grey));
            adviseEdt.setText(advise);
            adviseEdt.setKeyListener(null);
            adviseEdt.setBackground(getResources().getDrawable(R.drawable.textshape_grey));
            reasonLl.setVisibility(View.GONE);
            effectLl.setVisibility(View.VISIBLE);
            locationLl.setVisibility(View.GONE);
            ifInsideLl.setVisibility(View.GONE);
            photoRl.setVisibility(View.GONE);
            setTitle("成效跟踪处理");
        }else if ("reach".equals(from)){
            locationLl.setVisibility(View.VISIBLE);
            ifInsideLl.setVisibility(View.VISIBLE);
            questionLl.setVisibility(View.GONE);
            adviseLl.setVisibility(View.GONE);
            reasonLl.setVisibility(View.GONE);
            effectLl.setVisibility(View.GONE);
            photoRl.setVisibility(View.VISIBLE);
            setTitle("到达现场处理");
            getAddrLocation();
        }else if ("leave".equals(from)){
            locationLl.setVisibility(View.VISIBLE);
            ifInsideLl.setVisibility(View.VISIBLE);
            questionLl.setVisibility(View.VISIBLE);
            adviseLl.setVisibility(View.VISIBLE);
            reasonLl.setVisibility(View.GONE);
            effectLl.setVisibility(View.GONE);
            photoRl.setVisibility(View.VISIBLE);
            setTitle("离开现场处理");
            getAddrLocation();
        }
        addrText.setText(addr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_photo:
                if (!Tools.isEmpty(imgPath)) {
                    PhotoHelper.openPictureDialog(context, imgPath);
                }
                break;
            case R.id.txt_take_photo:
                if (Tools.isSDCardExit()) {
                    takePhoto();
                } else {
                    ToastUtil.toast(context, "内存卡不存在不能拍照");
                }
                break;
            case R.id.txt_refresh:
                getAddrLocation();
                break;
            case R.id.txt_map:
                if ("未采集".equals(leavetype)){
                    ToastUtil.toast(context,"未采集不可查地图");
                    return;
                }
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
            case R.id.btn_submit:
                final String quesioton = questionEdt.getText().toString();
                final String advise = adviseEdt.getText().toString();
                final String reason = reasonEdt.getText().toString();
                final String effect = effectEdt.getText().toString();
                final String addr = locationTxt.getText().toString();
                final String outReason = outReasonEdt.getText().toString();
                if ("result".equals(from)) {
                    if ("未走访".equals(state)) {
                        if (!Tools.isEmpty(reason)) {
                            ProgressDialogUtil.showProgressDialog(context);
                            saveData(quesioton, advise, reason, effect,addr,outReason);
                        } else {
                            ToastUtil.toast(context, "请填写原因");
                        }
                    } else {
                        if (!Tools.isEmpty(quesioton) && !Tools.isEmpty(advise)) {
                            ProgressDialogUtil.showProgressDialog(context);
                            saveData(quesioton, advise, reason, effect,addr,outReason);
                        } else {
                            ToastUtil.toast(context, "请填写问题以及措施");
                        }
                    }
                } else if ("effect".equals(from)) {
                    if (!Tools.isEmpty(effect)) {
                        ProgressDialogUtil.showProgressDialog(context);
                        saveData(quesioton, advise, reason, effect,addr,outReason);
                    } else {
                        ToastUtil.toast(context, "请填写成效跟踪");
                    }
                } else if ("reach".equals(from)) {
                    if (!Tools.isEmpty(addr)&&!"正在定位...".equals(addr)&&!Tools.isEmpty(leavetype)){
                        if ("否".equals(leavetype)){
                            if (TextUtils.isEmpty(outReason)){
                                ToastUtil.toast(context,"请填写不在有效范围原因");
                            }else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setTitle("不再有效范围是否确认提交")
//                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
                                            ProgressDialogUtil.showProgressDialog(context);
                                            saveData(quesioton, advise, reason, effect,addr,outReason);
//                                        }
//                                    }).setNegativeButton("否",null);
//                            builder.show();
                            }
                        }else{
                            ProgressDialogUtil.showProgressDialog(context);
                            saveData(quesioton, advise, reason, effect,addr,outReason);
                        }
                    }else{
                        ToastUtil.toast(context,"请等待定位");
                    }
                }else if ("leave".equals(from)) {
                    if (!Tools.isEmpty(addr) && !"正在定位...".equals(addr)&&!Tools.isEmpty(leavetype)) {
                        if ("否".equals(leavetype)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("不再有效范围是否确认提交")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ProgressDialogUtil.showProgressDialog(context);
                                            saveData(quesioton, advise, reason, effect,addr,"");
                                        }
                                    }).setNegativeButton("否",null);
                            builder.show();
                        }else{
                            ProgressDialogUtil.showProgressDialog(context);
                            saveData(quesioton, advise, reason, effect,addr,outReason);
                        }
                    } else {
                        ToastUtil.toast(context, "请等待定位");
                    }
                }
                break;
        }
    }


    private void saveData(String quesioton, String advise,
                          String reason, String effect,String addr,String outReason) {

//        String httpUrl = User.mainurl+"notePad/MyNoteServlet";
        String httpUrl ="";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
//        try {
//            addr = URLEncoder.encode(addr,"UTF-8");
//            plan = URLEncoder.encode(plan,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace(System.out);
//        }
        parameters_userInfo.put("Inter", "app");
        parameters_userInfo.put("type", from);
        if ("result".equals(from)){
            if ("未走访".equals(state)){
                httpUrl = User.mainurl+"notePad/MyNoteServlet";
                parameters_userInfo.put("tmp", "unfinish");
                parameters_userInfo.put("problem",reason );
                parameters_userInfo.put("id", note.getId());
                parameters_userInfo.put("rdcode", note.getRdCode());
                parameters_userInfo.put("opinion", advise);
                parameters_userInfo.put("ccuscode", addrCode);
            }else{
//                parameters_userInfo.put("tmp", "finish");
//                parameters_userInfo.put("problem",quesioton );
                parameters_userInfo.put("opinion", advise);
                httpUrl = User.mainurl+"app/StateUpdate";
                parameters_userInfo.put("rtcode", rtCode);
                parameters_userInfo.put("problem",quesioton );
                parameters_userInfo.put("opinion", advise);
            }
        }else if ("effect".equals(from)){
            httpUrl = User.mainurl+"notePad/MyNoteServlet";
            parameters_userInfo.put("rtcode", rtCode);
            parameters_userInfo.put("effect", effect);
        }else if ("reach".equals(from)){
            httpUrl = User.mainurl+"app/StateUpdate";
            parameters_userInfo.put("id", note.getId());
            parameters_userInfo.put("rdcode", note.getRdCode());
            parameters_userInfo.put("ccuscode", addrCode);
            parameters_userInfo.put("lat", latitude);
            parameters_userInfo.put("lng", longitude);
            parameters_userInfo.put("addr", addr);
            parameters_userInfo.put("image", uploadBuffer);
            parameters_userInfo.put("inside", leavetype);
            parameters_userInfo.put("reason", outReason);
        }else if ("leave".equals(from)){
            httpUrl = User.mainurl+"app/StateUpdate";
            parameters_userInfo.put("rtcode", rtCode);
            parameters_userInfo.put("id", note.getId());
            parameters_userInfo.put("rdcode", note.getRdCode());
            parameters_userInfo.put("ccuscode", addrCode);
            parameters_userInfo.put("problem",quesioton);
            parameters_userInfo.put("opinion", advise);
            parameters_userInfo.put("lat", latitude);
            parameters_userInfo.put("lng", longitude);
            parameters_userInfo.put("addr", addr);
            parameters_userInfo.put("image", uploadBuffer);
            parameters_userInfo.put("inside", leavetype);
        }

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            if (code==0) {
                                ToastUtil.toast(context, "上传成功");
                                finish();
                            } else {
                                ToastUtil.toast(context, "请重试");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            ProgressDialogUtil.closeProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//		Log.i(TAG, "onSaveInstanceState,conversation="+conversationinfo.hashCode());
//		outState.putSerializable("conversation", conversationinfo);
        if(!TextUtils.isEmpty(imgPath)){
            outState.putString("imgPath", imgPath);
        }
//		outState.putSerializable("targetId", conversationinfo.getTargetId());
        super.onSaveInstanceState(outState);
    }


    public void takePhoto() {
        imgPath = Tools.getSDPath() + "/dyyd/photo.jpg";
        // 必须确保文件夹路径存在，否则拍照后无法完成回调
        File vFile = new File(imgPath);
//        if (vFile.exists()) {
//            vFile.delete();
//        }
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
                        bitmap = PhotoUtil.imageEncode(imageFile,true);
                        photoImage.setImageBitmap(bitmap);
                        uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
                        imgPath = "";
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void visitServer_getaddr(String longitude, String latitude) {
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
                            location = obj.getString("formatted_address");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(context, "distance.db")) {
            locationTxt.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
            longitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);
            visitServer_getaddr(longitude, latitude);
            mThread.start();// 线程启动
            distanceHelper.close();
        } else {
            locationTxt.setText("正在定位...");
            if (!mLocationClient.isStarted()) {
                Gps.GPS_do(mLocationClient, 1100);
            } else {
                ifLocation = true;
            }
            mThread.start();// 线程启动
        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {// run()在新的线程中运行
            int sleepcount = 1500;
            if (!Gps.exist(context, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                if (!ifLocation) {
                    sleepcount = 3300;
                }
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
            if (!Gps.exist(context, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                location = myApp.getAddr();
                longitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (Tools.isEmpty(location)) {
                    visitServer_getaddr(longitude, latitude);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.out);
                    }
                }
                // 未签到时，关闭location服务
                mLocationClient.stop();
                if (Tools.isEmpty(location)) {
                    mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                } else {
                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                }
            } else {
                if (Tools.isEmpty(location)) {
                    mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                } else {
                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                }
            }
        }
    };


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {// 此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    locationTxt.setText("[" + type + "]" + location);// textView显示从定位获取到的地址
                    ProgressDialogUtil.showProgressDialog(context);
                    getIfInside();
                    break;
                case MSG_FAILURE:
                    locationTxt.setText("请重新定位");
                    break;
            }
        }
    };



    private void getIfInside() {

        String httpUrl = User.mainurl + "app/StateUpdate";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("ccuscode", addrCode);
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("type", "fw");

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code==0) {
//                                JSONArray array = dataJson.getJSONArray("list");
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject obj = array.getJSONObject(i);
                                    String lat = dataJson.getString("lat");
                                    String lng = dataJson.getString("lng");
                                    int scope = dataJson.getInt("fw");

                                //签到点位置获取
//                            if (dataJson.getString("typecode").equals("0")) {
//                                JSONArray arrayJwd = dataJson.getJSONArray("jwdlist");
//                                for (int j = 0; j < arrayJwd.length(); j++) {
//                                    JSONObject obj = arrayJwd.getJSONObject(j);
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("latitude",lat);
                                    map.put("longitude",lng);
                                    map.put("fw",scope+"");
                                    pins.add(map);
//                                }
//                            }
                                    if (Tools.isEmpty(lat)||"0".equals(lat)){
                                        leavetype = "未采集";
                                        ifInsideTxt.setText(leavetype);
                                    }else{
                                        if (!Tools.isEmpty(latitude)){
                                            LatLng p1 = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
                                            LatLng p2 = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
                                            double distance = Math.ceil(DistanceUtil. getDistance(p1, p2));
                                            distance = Math.ceil(distance);
                                            leavetype = distance < scope ? "是" : "否" ;
                                            ifInsideTxt.setText(leavetype);
                                            if ("否".equals(leavetype)&&"reach".equals(from)){
                                                outReasonLl.setVisibility(View.VISIBLE);
                                            }else{
                                                outReasonLl.setVisibility(View.GONE);
                                            }
                                        }else{
                                            ToastUtil.toast(context,"请先等待获取位置信息");
                                        }
                                    }
//                                }
                            }else{
                                leavetype = "未采集";
                                ifInsideTxt.setText(leavetype);
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
                        ToastUtil.toast(context,"网络连接错误");
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }
}
