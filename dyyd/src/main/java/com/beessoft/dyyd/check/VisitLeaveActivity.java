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
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.ArrayAdapter;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.Logger;
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

public class VisitLeaveActivity extends BaseActivity implements View.OnClickListener {

    private LocationClient mLocationClient;

    private LinearLayout typeLl;
    private LinearLayout questionLl;

    private TextView customerText, personText, aimText, addrText, reachTimeTxt, reachLocationText;

    private Spinner typeSpn ;
    private EditText resultEdit;
    private EditText questionEdit;

    private String customer,result, type;
    private String customerType;
    private String questionType;
    private String customerCode = "";
    private String longitude, latitude, addr;
    private String startid;

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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {// 此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    addrText.setText("[" + type + "]" + addr);// textView显示从定位获取到的地址
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
//			Log.i(TAG, "拍摄异常，获取原来的shot_path");
            Logger.e("拍摄异常，获取原来的shot_path");
            imgPath = savedInstanceState.getString("imgPath");
        }
        context = VisitLeaveActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        initView();

        if (Gps.exist(VisitLeaveActivity.this, "distance.db")) {
            Gps.GPS_do(mLocationClient, 8000);
        }
        getAddrLocation();
        // 获取已拜访客户信息
        getInfo();

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

        resultEdit = (EditText) findViewById(R.id.visitleave_result);
        questionEdit = (EditText) findViewById(R.id.edt_question);

        photoImage = (ImageView) findViewById(R.id.img_photo);

        photoImage.setOnClickListener(this);
        findViewById(R.id.txt_preserve).setOnClickListener(this);
        findViewById(R.id.txt_take_photo).setOnClickListener(this);
        findViewById(R.id.txt_refresh).setOnClickListener(this);
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
                addr = myApp.getaddr();
                longitude = myApp.getjd();
                latitude = myApp.getwd();
                type = myApp.getType();
                if (addr == null) {
                    getAddr(longitude, latitude);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.out);
                    }
                }
                // 未签到时，关闭location服务
                mLocationClient.stop();
                if (addr == null) {
                    mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                } else {
                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                }
            } else {
                if (addr == null) {
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

        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                startid = dataJson.getString("startid");
                                customerText.setText(dataJson.getString("ccusname"));
                                personText.setText(dataJson.getString("visitperson"));
                                aimText.setText(dataJson.getString("visitgoal"));
                                reachLocationText.setText(dataJson.getString("siadd"));
                                customerCode = dataJson.getString("ccuscode");
                                customerType = dataJson.getString("custype");
                                if ("政企单位".equals(customerType)){
                                    typeLl.setVisibility(View.VISIBLE);
                                    questionLl.setVisibility(View.VISIBLE);
                                }
                                JSONArray array = dataJson.getJSONArray("list");
                                List<String> list = new ArrayList<>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    list.add(obj.getString("type"));
                                }
                                ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                                        context,
                                        R.layout.spinner_item,
                                        list);
                                typeSpn.setAdapter(adapterType);
                                typeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                        questionType = parent.getItemAtPosition(position).toString();
//										getListView();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // 这个一直没有触发，我也不知道什么时候被触发。
                                        // 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
                                    }
                                });
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

    private void visitServer(String person,String aim,String location,String question) {

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
        parameters_userInfo.put("question",  Escape.escape(question));
        parameters_userInfo.put("questiontype", questionType);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                PreferenceUtil.write(context, "result", "");
                                ToastUtil.toast(context,"离开现场数据上报成功");
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
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void getAddr(String longitude, String latitude) {
        String httpUrl = "http://api.activity_map.baidu.com/geocoder/v2/";

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
//		Log.i(TAG, "onSaveInstanceState,conversation="+conversationinfo.hashCode());
//		outState.putSerializable("conversation", conversationinfo);
        if (!TextUtils.isEmpty(imgPath)) {
            outState.putString("imgPath", imgPath);
        }
//		outState.putSerializable("targetId", conversationinfo.getTargetId());
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
                    if (!"请选择".equals(questionType)){
                        if (TextUtils.isEmpty(question.trim())) {
                            ToastUtil.toast(context, "请填写问题");
                        }else {
                            ProgressDialogUtil.showProgressDialog(context);
                            visitServer(person,aim,location,question);
                        }
                    }else{
                        ProgressDialogUtil.showProgressDialog(context);
                        visitServer(person,aim,location,question);
                    }

                }
                break;
        }
    }
}