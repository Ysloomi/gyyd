package com.beessoft.dyyd.dailywork;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.Escape;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NewApi")
public class PhotoActivity extends BaseActivity {


    @BindView(R.id.location_txt)
    TextView locationTxt;
    @BindView(R.id.phototype_sp)
    Spinner phototypeSp;
    @BindView(R.id.photo_img)
    ImageView photoImg;
    @BindView(R.id.require_txt)
    TextView requireTxt;
    @BindView(R.id.context_txt)
    EditText contextTxt;

    private Thread mThread;
    private String longtitude, latitude, addr, type, imageType;

    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类

    private LocationClient mLocationClient;
    private ArrayList<String> requireList;

    //照相
    private Bitmap bitmap;
    private String imgPath;
    private String uploadBuffer = null;
    public static final int PHOTO_CODE = 5;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            // 此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    locationTxt.setText("[" + type + "]" + addr);// textView显示从定位获取到的地址
                    break;
                case MSG_FAILURE:
                    locationTxt.setText("请重新定位");
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                startActivity(new Intent(context,
                        PhotoQueryListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);

        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))) {
            imgPath = savedInstanceState.getString("imgPath");
        }
        context = PhotoActivity.this;
        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        requireList = new ArrayList<String>();

        // 获取定位地址
        getAddrLocation();
        // 获取
        getData();

        photoImg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PhotoHelper.openPictureDialog(PhotoActivity.this);
            }
        });

        phototypeSp.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageType = parent.getSelectedItem().toString();
                requireTxt.setText(requireList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    // 返回处理
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            uploadBuffer = "";
            switch (requestCode) {
                case PHOTO_CODE:
                    if (imgPath != null) {
                        File imageFile = new File(imgPath);
                        bitmap = PhotoUtil.imageEncode(imageFile, true);
                        photoImg.setImageBitmap(bitmap);
                        uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(PhotoActivity.this, "distance.db")) {
            locationTxt.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(
                    getApplicationContext(), "distance.db", 1);
            longtitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);

            visitServer_getaddr(longtitude, latitude);

            mThread.start();// 线程启动

            distanceHelper.close();
        } else {
            locationTxt.setText("正在定位...");
            Gps.GPS_do(mLocationClient, 1100);
            mThread.start();// 线程启动
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {// run()在新的线程中运行
            int sleepcount = 1600;
            if (!Gps.exist(PhotoActivity.this, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                sleepcount = 3300;
            }
            try {
                Thread.sleep(sleepcount);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Gps.exist(PhotoActivity.this, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                addr = myApp.getAddr();
                longtitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (addr == null) {
                    visitServer_getaddr(longtitude, latitude);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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

    private void getData() {

        String httpUrl = User.mainurl + "app/getimgtype";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                JSONArray arrayType = dataJson.getJSONArray("list");
                                ArrayList<String> list = new ArrayList<String>();
                                list.add("点击获取");
                                requireList.add("");
                                for (int j = 0; j < arrayType.length(); j++) {
                                    JSONObject obj = arrayType.getJSONObject(j);
                                    list.add(obj.get("name").toString());
                                    requireList.add(obj.getString("yq"));
                                }
                                // 现实数组在system里面需要启动Arrays.deepToString(string)
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        PhotoActivity.this,
                                        R.layout.item_spinner,
                                        list);
                                phototypeSp.setAdapter(adapter);
                            } else if (1 == code) {
                                Toast.makeText(PhotoActivity.this, "没有图片类型",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void saveData(String addr, String imageType, String contextLoca) {

        String httpUrl = User.mainurl + "app/save_image";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("cmaker", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("addr", Escape.escape(addr));
        parameters_userInfo.put("jd", longtitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("image", uploadBuffer);
        parameters_userInfo.put("imgtype", Escape.escape(imageType));
        parameters_userInfo.put("context", Escape.escape(contextLoca));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {

                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                ToastUtil.toast(context, "数据上传成功");
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

    @OnClick({R.id.refresh_txt, R.id.take_photo_txt, R.id.submit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh_txt:
                getAddrLocation();
                break;
            case R.id.take_photo_txt:
                if (Tools.isSDCardExit()) {
                    takePhoto();
                } else {
                    ToastUtil.toast(context, "内存卡不存在不能拍照");
                }
                break;
            case R.id.submit_btn:
                String location = locationTxt.getText().toString();
                String contextLoca = contextTxt.getText().toString();
                if ("请重新定位".equals(location)||"正在定位...".equals(location)) {
                    ToastUtil.toast(context, "请刷新定位再提交");
                } else if (uploadBuffer == null) {
                    ToastUtil.toast(context, "请先照相再上传");
                } else if ("点击获取".equals(imageType)) {
                    ToastUtil.toast(context, "请选择图片类型再上传");
                } else if (TextUtils.isEmpty(contextLoca.trim())) {
                    ToastUtil.toast(context, "请填写照片说明再上传");
                } else {
                    ProgressDialogUtil.showProgressDialog(context);
                    saveData(location, imageType, contextLoca);
                }
                break;
        }
    }
}
