package com.beessoft.dyyd.check;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Special;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.db.SfydDB;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.PhotoUtil;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class SpecialUpActivity extends BaseActivity implements View.OnClickListener {

    private TextView startText;
    private TextView endText;
    private TextView requireText;
    private TextView addrText;
    private TextView finishText;
    private EditText resultEdit;

    private Button nextBtn;
    private Button lastBtn;
    private Button submitBtn;

    private String longtitude, latitude, addr, type;

    private String projectId;
    private String shopId;

    private int currentId=1;
    private int totalId;

    private String location;
    private String result;
    private Thread mThread;

    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类

    private LocationClient mLocationClient;

    private Special special = new Special();

    // 照片
    public static final int ALBUM_CODE = 4;
    private static final int PHOTO_CODE = 5;
    private String uploadBuffer = "";
    private ImageView photoImage;
    private String imgPath = "";
    private Bitmap bitmap = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 此方法在ui线程运行
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
        setContentView(R.layout.activity_special_up);

        context = SpecialUpActivity.this;
        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;


        projectId = getIntent().getStringExtra("projectId");
        shopId = getIntent().getStringExtra("shopId");
//        map = (HashMap<String, String>) getIntent().getSerializableExtra("map");

        initView();

        special = SfydDB.getInstance(context).loadCheck(projectId,shopId,currentId+"");
        if (special == null){
            String startTime = DateUtil.getDateLoca();
            PreferenceUtil.write(context,projectId+shopId,startTime);
            ProgressDialogUtil.showProgressDialog(context);
            getData();
            // 获取定位地址
            getAddrLocation();
        }else{
            startText.setText(special.getBegin());
            endText.setText(special.getEnd());
            requireText.setText(special.getRemarks());
            resultEdit.setText(special.getResult());
            uploadBuffer = special.getPhoto();
            addrText.setText(special.getAddr());
            latitude = special.getWd();
            longtitude = special.getJd();
            if (Tools.isEmpty(special.getAddr())){
                getAddrLocation();
            }
            Bitmap bitmap = PhotoUtil.decodeBase64(uploadBuffer);
            photoImage.setImageBitmap(bitmap);
            totalId =SfydDB.getInstance(context).checkNum(projectId,shopId);
            finishText.setText("已经完成"+currentId+"/"+totalId);
            if (totalId==1){
                nextBtn.setVisibility(View.GONE);
                lastBtn.setVisibility(View.GONE);
                submitBtn.setVisibility(View.VISIBLE);
            }else{
                setBtnVis();
            }
        }
    }


    public void initView() {

        startText = (TextView) findViewById(R.id.start);
        endText = (TextView) findViewById(R.id.end);
        requireText = (TextView) findViewById(R.id.require);
        addrText = (TextView) findViewById(R.id.location_text);
        finishText = (TextView) findViewById(R.id.txt_finish);

        photoImage = (ImageView) findViewById(R.id.photo_image);

        resultEdit = (EditText) findViewById(R.id.result);

        nextBtn =(Button) findViewById(R.id.btn_next);
        lastBtn =(Button) findViewById(R.id.btn_last);
        submitBtn =(Button) findViewById(R.id.btn_submit);

        findViewById(R.id.refresh_iv).setOnClickListener(this);
        findViewById(R.id.photo_iv).setOnClickListener(this);
        findViewById(R.id.template_iv).setOnClickListener(this);
        photoImage.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        lastBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(context, "distance.db")) {
            addrText.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(
                    getApplicationContext(), "distance.db", 2);
            longtitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);
            visitServer_getaddr();
            mThread.start();// 线程启动
            distanceHelper.close();
        } else {
            addrText.setText("正在定位...");
            Gps.GPS_do(mLocationClient, 1100);
            mThread.start();// 线程启动
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {// run()在新的线程中运行
            int sleepcount = 1600;
            if (!Gps.exist(SpecialUpActivity.this, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                sleepcount = 3300;
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Gps.exist(SpecialUpActivity.this, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                addr = myApp.getAddr();
                longtitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (addr == null) {
                    visitServer_getaddr();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_iv:
                getAddrLocation();
                break;
            case R.id.photo_iv:
                String[] items = {"相机", "从手机相册中选择"};
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setItems(items, onselect).create();
                alertDialog.show();
                break;
            case R.id.template_iv:
                if (!Tools.isEmpty(special.getModelPhoto())) {
                    PhotoHelper.openPictureDialog(context, special.getModelPhoto(),
                            LocationApplication.imageLoader, LocationApplication.options);
                }
                break;
            case R.id.photo_image:
                if (!Tools.isEmpty(imgPath)) {
                    PhotoHelper.openPictureDialog(context, imgPath);
                }
                break;
            case R.id.btn_next:
                setInfo("next");
                setBtnVis();
                break;
            case R.id.btn_last:
                setInfo("last");
                setBtnVis();
                break;
            case R.id.btn_submit:
                setInfo("submit");
                break;
        }
    }

    private void setInfo(String type) {
        location = addrText.getText().toString();
        result = resultEdit.getText().toString();
        if (Tools.isEmpty(uploadBuffer)) {
            ToastUtil.toast(context, "请先照相");
        } else if (TextUtils.isEmpty(result.trim())) {
            ToastUtil.toast(context, "请填写反馈意见");
        } else {
            ProgressDialogUtil.showProgressDialog(context);
            special.setJd(longtitude);
            special.setWd(latitude);
            special.setAddr(location);
            special.setPhoto(uploadBuffer);
            special.setResult(result);
            SfydDB.getInstance(context).updateCheck(special);
            if (!"submit".equals(type)){
                if ("next".equals(type)) {
                    currentId = special.getId() + 1;
                } else {
                    currentId = special.getId() - 1;
                }
                special = SfydDB.getInstance(context).loadCheck(projectId,shopId,currentId + "");
                startText.setText(special.getBegin());
                endText.setText(special.getEnd());
                requireText.setText(special.getRemarks());
                addrText.setText(special.getAddr());
                finishText.setText("已经完成"+currentId+"/"+totalId);
                latitude = special.getWd();
                longtitude = special.getJd();
                if (Tools.isEmpty(special.getAddr())) {
                    getAddrLocation();
                }
                uploadBuffer = special.getPhoto();
                Bitmap bitmap = PhotoUtil.decodeBase64(uploadBuffer);
                if (bitmap == null) {
                    photoImage.setImageResource(android.R.color.transparent);
                } else {
                    photoImage.setImageBitmap(bitmap);
                }
                resultEdit.setText(special.getResult());
                ProgressDialogUtil.closeProgressDialog();
            }else{
                visitServer();
            }
        }
    }

    private void setBtnVis() {
        if (currentId==totalId){
            submitBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
            lastBtn.setVisibility(View.VISIBLE);
        }else if (currentId==1){
            submitBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
            lastBtn.setVisibility(View.GONE);
        }else{
            submitBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
            lastBtn.setVisibility(View.VISIBLE);
        }
    }

    DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int position) {
            Intent intent = new Intent();
            switch (position) {
                case 0:
                    if (Tools.isSDCardExit()) {
                        imgPath = Tools.getSDPath() + "/sfyd/photo.jpg";
                        takePhoto(intent);
                    } else {
                        ToastUtil.toast(context, "内存卡不存在不能拍照");
                    }
                    break;
                case 1:
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, ALBUM_CODE);
                    break;
                default:
                    break;
            }
        }
    };


    private void takePhoto(Intent intent) {
        // 必须确保文件夹路径存在，否则拍照后无法完成回调
        File vFile = new File(imgPath);
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        PreferenceUtil.write(context, "photopath", imgPath);
        Uri uri = Uri.fromFile(vFile);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
        // 打开新的activity，这里是系统摄像头
        startActivityForResult(intent, PHOTO_CODE);
    }

    // 相机返回处理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            uploadBuffer = "";
            switch (requestCode) {
                case PHOTO_CODE:
                    imgPath = PreferenceUtil.readString(context, "photopath");
                    if (!Tools.isEmpty(imgPath)) {
                        File imageFile = new File(imgPath);
                        bitmap = PhotoUtil.imageEncode(imageFile, true);
                        photoImage.setImageBitmap(bitmap);
                        uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
                    }
                    break;
                case ALBUM_CODE:
                    if (data != null) {
                        // 得到图片的全路径
                        Uri uri = data.getData();
                        imgPath = Tools.getRealPathFromURI(context, uri);
                        File imageFile = new File(imgPath);
                        bitmap = PhotoUtil.imageEncode(imageFile, false);
                        photoImage.setImageBitmap(bitmap);
                        uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void getData() {
        String httpUrl = User.mainurl+ "call/chkMain" ;
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);

        parameters_userInfo.put("ccuscode", shopId);
        parameters_userInfo.put("id", projectId);
        parameters_userInfo.put("type", "2");
        parameters_userInfo.put("sf", ifSf);

        AsyncHttpClient client_request = new AsyncHttpClient();
//		Logger.e(httpUrl+"?"+parameters_userInfo);
        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//                            Logger.e("subject>>"+dataJson);
                            int code = dataJson.getInt("code");
                            if (code ==1) {
                                ToastUtil.toast(context,"没有相关信息");
                            } else if (code ==0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                totalId = array.length();
                                for (int j = 0; j < totalId; j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    Special special = new Special();
                                    special.setId(j+1);
                                    special.setProjectId(projectId);
                                    special.setShopId(shopId);
                                    special.setSubjectId(obj.getString("blid"));
                                    special.setName(obj.getString("detailsName"));
                                    special.setRemarks(obj.getString("remarks"));
                                    special.setBegin(obj.getString("begintime"));
                                    special.setEnd(obj.getString("endtime"));
                                    special.setModelPhoto(User.mainurl +obj.getString("model"));
                                    special.setJd("");
                                    special.setWd("");
                                    special.setAddr("");
                                    special.setPhoto("");
                                    special.setResult("");
                                    SfydDB.getInstance(context).saveCheck(special);
                                }
                                special = SfydDB.getInstance(context).loadCheck(projectId,shopId,currentId+"");
                                if (special !=null){
                                    startText.setText(special.getBegin());
                                    endText.setText(special.getEnd());
                                    requireText.setText(special.getRemarks());
                                    finishText.setText("已经完成"+currentId+"/"+totalId);
                                    if (totalId==1){
                                        nextBtn.setVisibility(View.GONE);
                                        lastBtn.setVisibility(View.GONE);
                                        submitBtn.setVisibility(View.VISIBLE);
                                    }else{
                                        setBtnVis();
                                    }
                                }
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


    private void visitServer() {
        String httpUrl = User.mainurl + "call/upApp";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        List<Special> specials = SfydDB.getInstance(context).loadChecks(projectId,shopId);
        Gson gson = new Gson();
        String a = gson.toJson(specials);
        String start = PreferenceUtil.readString(context,projectId+shopId);
        String end = DateUtil.getDateLoca();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("json", Escape.escape(a));
        parameters_userInfo.put("start", start);
        parameters_userInfo.put("end", end);
//        parameters_userInfo.put("blid", id+"");
//        parameters_userInfo.put("addr", Escape.escape(location));
//        parameters_userInfo.put("jd", longtitude);
//        parameters_userInfo.put("wd", latitude);
//        parameters_userInfo.put("imageFile", uploadBuffer);
//        parameters_userInfo.put("remarks", Escape.escape(result));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                ToastUtil.toast(context, "数据上传成功");
                                SfydDB.getInstance(context).deleteByProjectAndShop(projectId,shopId);
                                finish();
                            } else {
                                ToastUtil.toast(context, "请重新上传");
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
        parameters_userInfo.put("location", latitude + "," + longtitude);
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
}
