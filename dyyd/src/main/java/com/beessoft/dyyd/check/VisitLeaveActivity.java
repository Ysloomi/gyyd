package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.ArrayAdapter;
import com.beessoft.dyyd.utils.Escape;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitLeaveActivity extends BaseActivity implements View.OnClickListener {

    private LocationClient mLocationClient;

    private LinearLayout typeLl;
    private LinearLayout questionLl;

    private TextView customerText, personText, aimText, addrText, reachTimeTxt, reachLocationText;
    private TextView insideText;

    private Spinner typeSpn;
    private EditText resultEdit;
    private EditText questionEdit;

    private String customer, result, type;
    private String customerType;
    private String questionType;
    private String questionTypeCode;
    private String customerCode = "";
    private String longitude, latitude, addr;
    private String startid;

    private List<String> questionCodes = new ArrayList<>();

    public static final int PHOTO_CODE = 5;
    // 创建Bitmap对象
    private Bitmap bitmap;
    private String uploadBuffer = null;
    private ImageView photoImage;
    private String imgPath = "";

    private Thread mThread;

    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类

    private boolean ifLocation = false;

    List<HashMap<String, String>> pins = new ArrayList<>();
    String leavetype = "";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {// 此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    addrText.setText("[" + type + "]" + addr);// textView显示从定位获取到的地址
                    // 获取已拜访客户信息
                    getInfo();
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
        setContentView(R.layout.activity_leave);
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))) {
//            Logger.e("拍摄异常，获取原来的shot_path");
            imgPath = savedInstanceState.getString("imgPath");
        }
        context = VisitLeaveActivity.this;

        initView();

        if (Gps.exist(VisitLeaveActivity.this, "distance.db")) {
            Gps.GPS_do(mLocationClient, 8000);
        }
        getAddrLocation();

        reachTimeTxt.setText("到达时间:" + PreferenceUtil.readString(context, "reachTime"));

        String a = PreferenceUtil.readString(context, "result");
        if (!"".equals(a)) {
            resultEdit.setText(a);
        }
    }

    public void initView() {
        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        typeLl = (LinearLayout) findViewById(R.id.ll_question_type);
        questionLl = (LinearLayout) findViewById(R.id.ll_question);

        typeSpn = (Spinner) findViewById(R.id.spn_type);

        customerText = (TextView) findViewById(R.id.visitleave_customer);
        personText = (TextView) findViewById(R.id.visitleave_person);
        aimText = (TextView) findViewById(R.id.visitleave_aim);
        addrText = (TextView) findViewById(R.id.location_text);
        reachTimeTxt = (TextView) findViewById(R.id.reachtime_text);
        reachLocationText = (TextView) findViewById(R.id.reachlocation_text);
        insideText = (TextView) findViewById(R.id.inside_tv);

        resultEdit = (EditText) findViewById(R.id.visitleave_result);
        questionEdit = (EditText) findViewById(R.id.edt_question);

        photoImage = (ImageView) findViewById(R.id.img_photo);

        photoImage.setOnClickListener(this);
        findViewById(R.id.txt_preserve).setOnClickListener(this);
        findViewById(R.id.txt_take_photo).setOnClickListener(this);
        findViewById(R.id.txt_refresh).setOnClickListener(this);
        findViewById(R.id.txt_map).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(VisitLeaveActivity.this, "distance.db")) {
            addrText.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(
                    getApplicationContext(), "distance.db", 1);
            longitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);
            getAddr(longitude, latitude);
            mThread.start();// 线程启动
            distanceHelper.close();
        } else {
            addrText.setText("正在定位...");
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
            if (!Gps.exist(VisitLeaveActivity.this, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                if (!ifLocation) {
                    sleepcount = 3300;
                }
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
            if (!Gps.exist(VisitLeaveActivity.this, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                addr = myApp.getAddr();
                longitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (TextUtils.isEmpty(addr)) {
                    getAddr(longitude, latitude);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.out);
                    }
                }
                // 未签到时，关闭location服务
                mLocationClient.stop();
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

    private void getInfo() {

        String httpUrl = User.mainurl + "sf/offvisit";

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
                            int code = dataJson.getInt("code");
                            questionCodes.clear();
                            if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                JSONObject obj = array.getJSONObject(0);
                                startid = obj.getString("startid");
                                customerText.setText(obj.getString("ccusname"));
                                personText.setText(obj.getString("visitperson"));
                                aimText.setText(obj.getString("visitgoal"));
                                reachLocationText.setText(obj.getString("siadd"));
                                customerCode = obj.getString("ccuscode");
                                customerType = obj.getString("custype");
                                if ("政企单位".equals(customerType)) {
                                    typeLl.setVisibility(View.VISIBLE);
                                    questionLl.setVisibility(View.VISIBLE);
                                }
                                JSONArray arrayQuestin = dataJson.getJSONArray("qtlist");
                                List<String> listQuestin = new ArrayList<>();
                                listQuestin.add("填写问题，请选择类型");
                                questionCodes.add("100");
                                for (int j = 0; j < arrayQuestin.length(); j++) {
                                    JSONObject objQ = arrayQuestin.getJSONObject(j);
                                    listQuestin.add(objQ.getString("qtname"));
                                    questionCodes.add(objQ.getString("qtcode"));
                                }
                                ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                                        context,
                                        R.layout.item_spinner,
                                        listQuestin);
                                typeSpn.setAdapter(adapterType);
                                typeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                        if (position > 0) {
                                            questionType = parent.getItemAtPosition(position).toString();
                                            questionTypeCode = questionCodes.get(position);
                                        } else {
                                            questionType = "";
                                            questionTypeCode = "";
                                        }
//										getListView();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // 这个一直没有触发，我也不知道什么时候被触发。
                                        // 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
                                    }
                                });
                                pins.clear();
                                //签到点位置获取
//                            if (dataJson.getString("typecode").equals("0")) {
                                String lat ="";
                                String lng  ="";
                                int scope = 0 ;
                                JSONArray arrayJwd = dataJson.getJSONArray("jwdjson");
                                for (int j = 0; j < arrayJwd.length(); j++) {
                                    JSONObject obj1 = arrayJwd.getJSONObject(j);
                                    HashMap<String,String> map = new HashMap<>();
                                    lat = obj1.getString("lat");
                                    lng = obj1.getString("lng");
                                    scope = obj1.getInt("fw");
                                    map.put("latitude",lat);
                                    map.put("longitude",lng);
                                    map.put("fw",scope+"");
                                    pins.add(map);
                                }
                                LatLng p1 = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                                LatLng p2 = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                                double distance = Math.ceil(DistanceUtil.getDistance(p1, p2));
                                distance = Math.ceil(distance);
                                leavetype = distance < scope ? "是" : "否";
                                insideText.setText(leavetype);

//                            }
//								//将光标移到最后
//								String text1= obj.getString("checkresult");
//								resultEdit.setText(text1 + "\n");
//								String text2= text1+"\n";
//								int textLength = text2.length();
//								resultEdit.setSelection(textLength, textLength);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void saveData(String person, String aim, String location, String question) {

        String httpUrl = User.mainurl + "sf/offvisit_save";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("addr", Escape.escape(location));
        parameters_userInfo.put("cus", Escape.escape(customer));
        parameters_userInfo.put("visitperson", Escape.escape(person));
        parameters_userInfo.put("visitgoal", Escape.escape(aim));
        parameters_userInfo.put("visitresult", Escape.escape(result));
        parameters_userInfo.put("image", uploadBuffer);
        parameters_userInfo.put("startid", startid);
        parameters_userInfo.put("ccuscode", customerCode);
        parameters_userInfo.put("question", Escape.escape(question));
        parameters_userInfo.put("questiontype", questionTypeCode);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("inside", Escape.escape(leavetype));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                PreferenceUtil.write(context, "result", "");
                                ToastUtil.toast(context, "离开现场数据上报成功");
                                finish();
                            } else if (code == 1) {
                                ToastUtil.toast(context, "已提交，请勿重复提交");
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

//    private void saveDy(String person, String aim, String location, String question) {
//
//        String httpUrl = User.dyMainurl + "sf/offvisit_save";
//
//        AsyncHttpClient client_request = new AsyncHttpClient();
//        RequestParams parameters_userInfo = new RequestParams();
//
//        parameters_userInfo.put("mac", mac);
//        parameters_userInfo.put("usercode", username);
//        parameters_userInfo.put("jd", longitude);
//        parameters_userInfo.put("wd", latitude);
//        parameters_userInfo.put("addr", Escape.escape(location));
//        parameters_userInfo.put("cus", Escape.escape(customer));
//        parameters_userInfo.put("visitperson", Escape.escape(person));
//        parameters_userInfo.put("visitgoal", Escape.escape(aim));
//        parameters_userInfo.put("visitresult", Escape.escape(result));
//        parameters_userInfo.put("image", uploadBuffer);
//        parameters_userInfo.put("startid", startid);
//        parameters_userInfo.put("ccuscode", customerCode);
//        parameters_userInfo.put("question",  Escape.escape(question));
//        parameters_userInfo.put("questiontype", questionTypeCode);
//        parameters_userInfo.put("sf", ifSf);
//
//        client_request.post(httpUrl, parameters_userInfo,
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(String response) {
//                        try {
//                            JSONObject dataJson = new JSONObject(response);
//                            int code = dataJson.getInt("code");
//                            if (code == 0) {
//
//                            } else if (code == 1) {
//                                ToastUtil.toast(context, "已提交，请勿重复提交");
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
//                });
//    }

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
                        bitmap = PhotoUtil.imageEncode(imageFile, true);
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

    public void getAddr(String longitude, String latitude) {
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
        if (!TextUtils.isEmpty(imgPath)) {
            outState.putString("imgPath", imgPath);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_preserve:
                result = resultEdit.getText().toString();
                PreferenceUtil.write(context, "result", result);
                ToastUtil.toast(context, "保存成功");
                break;
            case R.id.img_photo:
                String imgPath = Tools.getSDPath() + "/dyyd/photo.jpg";
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
                if (pins.size() > 0) {
                    Intent intent = new Intent();
                    intent.setClass(context, QueryMapListActivity.class);
                    intent.putExtra("pin", (Serializable) pins);
                    intent.putExtra("jd", longitude);
                    intent.putExtra("wd", latitude);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "等待位置判断再查看");
                }
                break;
            case R.id.btn_submit:
                customer = customerText.getText().toString();
                String person = personText.getText().toString();
                String aim = aimText.getText().toString();
                result = resultEdit.getText().toString();
                String location = addrText.getText().toString();
                String question = questionEdit.getText().toString();
                if (TextUtils.isEmpty(uploadBuffer)) {
                    ToastUtil.toast(context, "请先照相再上传");
                } else if (TextUtils.isEmpty(result.trim())) {
                    ToastUtil.toast(context, "数据不能为空");
                } else if ("正在定位...".equals(location)) {
                    ToastUtil.toast(context, "请等待位置刷新");
                } else {
                    if (!TextUtils.isEmpty(question.trim())) {
                        if (TextUtils.isEmpty(questionType)) {
                            ToastUtil.toast(context, "请选择问题类型");
                        } else {
                            ProgressDialogUtil.showProgressDialog(context);
                            saveData(person, aim, location, question);
//                            if (GetInfo.getIfSf(context))
//                                saveData(person,aim,location,question);
                        }
                    } else {
                        ProgressDialogUtil.showProgressDialog(context);
                        saveData(person, aim, location, question);
//                        if (GetInfo.getIfSf(context))
//                            saveData(person,aim,location,question);
                    }
                }
                break;
        }
    }
}