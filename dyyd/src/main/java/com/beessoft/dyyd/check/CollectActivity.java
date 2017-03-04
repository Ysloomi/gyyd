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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.Constant;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class CollectActivity extends BaseActivity
        implements OnClickListener {

    private LinearLayout companyLl;
    private LinearLayout departLl;
    private LinearLayout areaLl;
    private LinearLayout shopLl;
    private LinearLayout unitLl;
    private LinearLayout business;
    private LinearLayout lname;
    private LinearLayout operator;
    private LinearLayout ll_box;
    private LinearLayout manyName;
    private LinearLayout vAdress;
    private LinearLayout vZgdm;
    private LinearLayout vConUser;

    private TextView addrText;
    private EditText unitEdt;
    private ImageView photoImage;
    private ImageView photoImage2;
    private EditText et_box;
    private TextView box_name;

    private Spinner typeSpn;
    private Spinner companySpn;
    private Spinner departSpn;
    private Spinner areaSpn;
    private Spinner shopSpn;
    private Spinner businessSpn;
    private Spinner plotSpn;
    private Spinner operatorSpn;
    private MySpinner manyPlotSpan;

    private ArrayList<String> typeList = new ArrayList<String>();
    private ArrayList<String> companyList = new ArrayList<String>();
    private ArrayList<String> compantIds = new ArrayList<String>();
    private ArrayList<String> departList = new ArrayList<String>();
    private ArrayList<String> departIds = new ArrayList<String>();
    private ArrayList<String> areaList = new ArrayList<String>();
    private ArrayList<String> areaIds = new ArrayList<String>();
    private ArrayList<String> shopList = new ArrayList<String>();
    private ArrayList<String> shopIds = new ArrayList<String>();
    private ArrayList<String> business_list = new ArrayList<String>();
    private ArrayList<String> businessIds = new ArrayList<String>();
    private ArrayList<String> plot_list = new ArrayList<String>();
    private ArrayList<String> plotIds = new ArrayList<String>();
    private ArrayList<String> operator_list = new ArrayList<String>();
    private String[] m;

    private String longitude;
    private String latitude;
    private String addr;
    private String type;
    private String shopId;
    private String from;
    private String customerType;
    private String operatorType;
    private String cdepperson;
    private String manyCdepperson;
    private String plotName = new String();
    private String sqId;  //社区编码

    private String adress;
    private String zgdm;
    private String conUser;

    private EditText etAdress;
    private EditText etZgdm;
    private EditText etConUser;

    private TextView takePhoto1;
    private TextView takePhoto2;

    private int iflag = 0;
    //照相
    private Bitmap bitmap;
    private String imgPath;
    private String imgPath2;
    private String uploadBuffer = null;
    private String uploadBuffer2 = null;
    public static final int PHOTO_CODE = 1;
    public static final int PHOTO_CODE2 = 2;

    private LocationClient mLocationClient;
    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private Thread mThread;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.LOCATION_SUCCESS:
                    addrText.setText("[" + type + "]" + addr);// textView显示从定位获取到的地址
                    break;
                case Constant.LOCATION_FAIL:
                    addrText.setText("请重新定位");
                    break;
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))) {
//			Logger.e("拍摄异常，获取原来的shot_path");
            imgPath = savedInstanceState.getString("imgPath");
        }
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath2"))) {
//			Logger.e("拍摄异常，获取原来的shot_path");
            imgPath2 = savedInstanceState.getString("imgPath2");
        }
        context = CollectActivity.this;
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        initView();
        // 获取定位地址
        getAddrLocation();
//		type  操作类型(0、1 一级  2 二级  3 三级 4 四级 5 四级)
//		 name 上级名（第一级不传）
        getData("0", "");
    }

    public void initView() {

        companyLl = (LinearLayout) findViewById(R.id.ll_company);
        departLl = (LinearLayout) findViewById(R.id.ll_depart);
        areaLl = (LinearLayout) findViewById(R.id.ll_area);
        shopLl = (LinearLayout) findViewById(R.id.ll_shop);
        unitLl = (LinearLayout) findViewById(R.id.ll_unit);
        //新增的选择
        business = (LinearLayout) findViewById(R.id. ll_business);
        lname = (LinearLayout) findViewById(R.id.ll_name);
        operator = (LinearLayout) findViewById(R.id.ll_operator);
        ll_box = (LinearLayout) findViewById(R.id.ll_box);
        manyName = (LinearLayout) findViewById(R.id.many_name);

        vAdress = (LinearLayout) findViewById(R.id.adress);
        vZgdm = (LinearLayout) findViewById(R.id.zgdm);
        vConUser = (LinearLayout) findViewById(R.id.con_user);


        takePhoto1 = (TextView) findViewById(R.id.take_photo1);
        takePhoto2 = (TextView) findViewById(R.id.take_photo2);
        takePhoto1.setOnClickListener(this);
        takePhoto2.setOnClickListener(this);


        et_box = (EditText) findViewById(R.id.box_et);
        box_name = (TextView) findViewById(R.id.box_name);
        addrText = (TextView) findViewById(R.id.location_text);
        unitEdt = (EditText) findViewById(R.id.edt_unit);

        etAdress = (EditText) findViewById(R.id.tv_adress);
        etZgdm = (EditText) findViewById(R.id.tv_zgdm);
        etConUser = (EditText) findViewById(R.id.tv_con_user);

        typeSpn = (Spinner) findViewById(R.id.spn_type);
        typeSpn.setOnItemSelectedListener(itemSelectedListener);
        companySpn = (Spinner) findViewById(R.id.block_spinner);
        companySpn.setOnItemSelectedListener(itemSelectedListener);
        departSpn = (Spinner) findViewById(R.id.street_spinner);
        departSpn.setOnItemSelectedListener(itemSelectedListener);
        areaSpn = (Spinner) findViewById(R.id.community_spinner);
        areaSpn.setOnItemSelectedListener(itemSelectedListener);
        shopSpn = (Spinner) findViewById(R.id.shop_spinner);
        shopSpn.setOnItemSelectedListener(itemSelectedListener);
        //Yelo 新增小区和营业厅、运营商
        plotSpn = (Spinner) findViewById(R.id.name_spinner);
        plotSpn.setOnItemSelectedListener(itemSelectedListener);
        businessSpn = (Spinner) findViewById(R.id.business_spinner);
        businessSpn.setOnItemSelectedListener(itemSelectedListener);
        operatorSpn = (Spinner) findViewById(R.id.operator_spinner);
        operatorSpn.setOnItemSelectedListener(itemSelectedListener);
        manyPlotSpan = (MySpinner) findViewById(R.id.many_plot_Spinner);



        photoImage = (ImageView) findViewById(R.id.photo_image);
        photoImage.setOnClickListener(this);
        photoImage2 = (ImageView) findViewById(R.id.photo_image2);
        photoImage2.setOnClickListener(this);


        findViewById(R.id.txt_refresh).setOnClickListener(this);
        findViewById(R.id.txt_map).setOnClickListener(this);
        findViewById(R.id.txt_get_customer).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);


//        typeList.add("请选择");

        typeList.add("小区/社区");
        typeList.add("渠道商家");
        typeList.add("光配箱");
        typeList.add("光交箱");

        reloadSpinner(typeSpn, typeList);

        operator_list.add("中国移动");
        operator_list.add("中国联通");
        operator_list.add("中国电信");
        reloadSpinner(operatorSpn, operator_list);
    }
    //点击类型清除。1级
    private void clear1(){
        m = null;
        companyList.clear();
        compantIds.clear();
        reloadSpinner(companySpn,companyList);

        departList.clear();
        departIds.clear();
        reloadSpinner(departSpn,departList);

        plot_list.clear();
        plotIds.clear();
        reloadSpinner(plotSpn,plot_list);

        areaList.clear();
        areaIds.clear();
        reloadSpinner(areaSpn,areaList);

        business_list.clear();
        businessIds.clear();
        reloadSpinner(businessSpn,business_list);


    }
    //点击选择分公司清除。等级2
    private void clear2(){
        m = null;
        departList.clear();
        departIds.clear();
        reloadSpinner(departSpn,departList);

        areaList.clear();
        areaIds.clear();
        reloadSpinner(areaSpn,areaList);

        plot_list.clear();
        plotIds.clear();
        reloadSpinner(plotSpn,plot_list);

        business_list.clear();
        businessIds.clear();
        reloadSpinner(businessSpn,business_list);
    }
    //点击选择分局清除。等级3
    private void clear3(){
        m = null;
        areaList.clear();
        areaIds.clear();
        reloadSpinner(areaSpn,areaList);

        plot_list.clear();
        plotIds.clear();
        reloadSpinner(plotSpn,plot_list);

        business_list.clear();
        businessIds.clear();
        reloadSpinner(businessSpn,business_list);
    }
    //点击选择社区清除。等级4
    private void clear4(){
        m = null;
        plot_list.clear();
        plotIds.clear();
        reloadSpinner(plotSpn,plot_list);
    }



    OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String name = "";
            switch (parent.getId()) {
                case R.id.spn_type:
                    customerType = typeList.get(position);
                    clear1();
                    if ("渠道商家".equals(customerType)) {
                        from = "yyt";
                        companyLl.setVisibility(View.VISIBLE);
                        departLl.setVisibility(View.VISIBLE);
                        business.setVisibility(View.VISIBLE);
                        areaLl.setVisibility(View.GONE);
                        ll_box.setVisibility(View.GONE);
                        unitLl.setVisibility(View.GONE);
                        operator.setVisibility(View.GONE);
                        lname.setVisibility(View.GONE);
                        shopLl.setVisibility(View.GONE);
                        manyName.setVisibility(View.GONE);
                        vAdress.setVisibility(View.GONE);
                        vConUser.setVisibility(View.GONE);
                        vZgdm.setVisibility(View.GONE);
                    } else if ("小区/社区".equals(customerType)) {
                        from = "xq";
                        companyLl.setVisibility(View.VISIBLE);
                        departLl.setVisibility(View.VISIBLE);
                        areaLl.setVisibility(View.VISIBLE);
                        lname.setVisibility(View.VISIBLE);
                        ll_box.setVisibility(View.GONE);
                        shopLl.setVisibility(View.GONE);
                        unitLl.setVisibility(View.GONE);
                        business.setVisibility(View.GONE);
                        operator.setVisibility(View.GONE);
                        manyName.setVisibility(View.GONE);
                        vAdress.setVisibility(View.GONE);
                        vConUser.setVisibility(View.GONE);
                        vZgdm.setVisibility(View.GONE);


                    } else if ("光交箱".equals(customerType)) {
                        operatorSpn.setSelection(0);
                        from = "gjx";
                        manyPlotSpan.setText("");
                        manyPlotSpan.setEnabled(false);

                        companyLl.setVisibility(View.VISIBLE);
                        departLl.setVisibility(View.VISIBLE);
                        areaLl.setVisibility(View.VISIBLE);
                        lname.setVisibility(View.GONE);
                        manyName.setVisibility(View.VISIBLE);
                        ll_box.setVisibility(View.VISIBLE);
                        operator.setVisibility(View.VISIBLE);
                        shopLl.setVisibility(View.GONE);
                        unitLl.setVisibility(View.GONE);
                        business.setVisibility(View.GONE);
                        vAdress.setVisibility(View.VISIBLE);
                        vConUser.setVisibility(View.GONE);
                        vZgdm.setVisibility(View.VISIBLE);

                        et_box.setText("");
                        et_box.setHint("请输入光交箱代码");
                        box_name.setText("光交箱");

                    }else if ("光配箱".equals(customerType)) {
                        operatorSpn.setSelection(0);
                        from = "gpx";

                        companyLl.setVisibility(View.VISIBLE);
                        departLl.setVisibility(View.VISIBLE);
                        areaLl.setVisibility(View.VISIBLE);
                        lname.setVisibility(View.VISIBLE);
                        ll_box.setVisibility(View.VISIBLE);
                        lname.setVisibility(View.VISIBLE);
                        operator.setVisibility(View.VISIBLE);
                        vAdress.setVisibility(View.VISIBLE);
                        vConUser.setVisibility(View.VISIBLE);
                        vZgdm.setVisibility(View.VISIBLE);
                        shopLl.setVisibility(View.GONE);
                        unitLl.setVisibility(View.GONE);
                        business.setVisibility(View.GONE);
                        manyName.setVisibility(View.GONE);

                        et_box.setText("");
                        et_box.setHint("请输入光配箱代码");
                        box_name.setText("光配箱");
                    }else {
                        companyLl.setVisibility(View.GONE);
                        departLl.setVisibility(View.GONE);
                        areaLl.setVisibility(View.GONE);
                        shopLl.setVisibility(View.GONE);
                        unitLl.setVisibility(View.GONE);
                        business.setVisibility(View.GONE);
                        lname.setVisibility(View.GONE);
                        ll_box.setVisibility(View.GONE);
                        operator.setVisibility(View.GONE);
                        vAdress.setVisibility(View.GONE);
                        vConUser.setVisibility(View.GONE);
                        vZgdm.setVisibility(View.GONE);
                    }
                    getData("0", "");
                    break;
                case R.id.block_spinner:
                    clear2();
                    name = companyList.get(position);
                    if (!"请选择".equals(name)) {
                        String locaId = "";
                        if (compantIds.size() != 1) {
                            locaId = compantIds.get(position - 1);
                        } else {
                            locaId = compantIds.get(0);
                        }
                        shopId = locaId;
                        //from = "fgs";
                        getData("2", locaId);
                    } else if ("请选择".equals(name) || "".equals(name)) {
                        departList.clear();
                        reloadSpinner(departSpn, departList);
                        shopId = "";
                    }
                    break;
                case R.id.street_spinner:
                    clear3();
                    name = departList.get(position);
                    if (!"请选择".equals(name)) {
                        String locaId = departIds.get(position - 1);
                        shopId = locaId;
                        //from = "fj";
                        getData("3", locaId);
                        if (customerType.equals("渠道商家")){
                            getData("5",locaId);
                        }else {
                            getData("3",locaId);
                        }
                    } else if ("请选择".equals(name) || "".equals(name)) {
                        if (customerType.equals("渠道商家")){
                            business_list.clear();
                            reloadSpinner(businessSpn, business_list);
                        }else {
                            areaList.clear();
                            reloadSpinner(areaSpn, areaList);
                        }
                    }
                    if (customerType.equals("光交箱")){
                        manyPlotSpan.setText("");
                        manyPlotSpan.setEnabled(false);
                    }
                    break;
                case R.id.community_spinner:
                    clear4();
                    name = areaList.get(position);
                    if (position!=0){
                        sqId = areaIds.get(position-1);
                    }
                    if (!"请选择".equals(name)) {
                        if (customerType.equals("光交箱")){
                            manyPlotSpan.setEnabled(true);
                        }
                        String locaId = areaIds.get(position - 1);
                        getData("4",locaId);

                    } else if ("请选择".equals(name) || "".equals(name)) {

                        plot_list.clear();
                        reloadSpinner(plotSpn, plot_list);
                    }

                    break;
                case R.id.name_spinner:
                    plotName = plot_list.get(position);
                    cdepperson = plotIds.get(position);
                    break;
                case R.id.business_spinner:
                    cdepperson = businessIds.get(position);
                    break;

                case R.id.shop_spinner:
                    name = shopList.get(position);
                    if (!"请选择".equals(name)) {
                        shopId = shopIds.get(position - 1);
                        //from = "yyt";
//					Log.e("main", "shopId>>>>"+shopId);
                    } else if ("请选择".equals(name)) {
                        shopId = "";
                    }
                    break;
                case R.id.operator_spinner:
                    operatorType = operator_list.get(position);
                    if (operatorType.equals("中国移动")&&(customerType.equals("光交箱")||customerType.equals("光配箱"))){
                        vZgdm.setVisibility(View.VISIBLE);
                    }else if (operatorType.equals("中国电信") || operatorType.equals("中国联通")){
                        vZgdm.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


    public void takePhoto(int i) {

        File vFile;
        if (i==1){
            uploadBuffer = null;
            imgPath = Tools.getSDPath() + "/dyyd/photo.jpg";
            vFile = new File(imgPath);
        }else {
            uploadBuffer2 = null;
            imgPath2 = Tools.getSDPath() + "/dyyd/photo2.jpg";
            vFile = new File(imgPath2);
        }

        // 必须确保文件夹路径存在，否则拍照后无法完成回调

        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        Uri uri = Uri.fromFile(vFile);
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
        // 打开新的activity，这里是系统摄像头
        if (i==1){
            startActivityForResult(intent, PHOTO_CODE);
        }else {
            startActivityForResult(intent, PHOTO_CODE2);
        }

    }


    // 返回处理,两张照片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case PHOTO_CODE:
                    uploadBuffer = "";
                    if (!Tools.isEmpty(imgPath)) {
                        File imageFile = new File(imgPath);
                        bitmap = PhotoUtil.imageEncode(imageFile, true);
                        photoImage.setImageBitmap(bitmap);
                        uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
                        imgPath = "";
                    }
                    break;
                case PHOTO_CODE2:
                    uploadBuffer2 = "";
                    if (!Tools.isEmpty(imgPath2)) {
                        File imageFile = new File(imgPath2);
                        bitmap = PhotoUtil.imageEncode(imageFile, true);
                        photoImage2.setImageBitmap(bitmap);
                        uploadBuffer2 = PhotoUtil.encodeTobase64(bitmap);
                        imgPath2 = "";
                    }
                    break;
                case Constant.GET_CUSTOMER:
                    if (data != null) {
                        unitEdt.setText(data.getStringExtra("name"));
                        shopId = data.getStringExtra("ccuscode");//客户编码
                    } else {
                        shopId = "";
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
        if (Gps.exist(context, "distance.db")) {
            addrText.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
            longitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);
            getaddr(longitude, latitude);
            mThread.start();// 线程启动
            distanceHelper.close();// 关闭数据库
        } else {
            addrText.setText("正在定位...");
            if (!mLocationClient.isStarted()) {
                Gps.GPS_do(mLocationClient, 1100);
            } else {
                iflag = 1;
            }
            mThread.start();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int sleepcount = 1500;
            if (!Gps.exist(context, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                if (iflag == 0) {
                    sleepcount = 4400;
                }
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Gps.exist(context, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                addr = myApp.getAddr();
                longitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (Tools.isEmpty(addr)) {
                    getaddr(longitude, latitude);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (Tools.isEmpty(addr)) {
                    mHandler.obtainMessage(Constant.LOCATION_FAIL).sendToTarget();
                } else {
                    mHandler.obtainMessage(Constant.LOCATION_SUCCESS).sendToTarget();
                }
            } else {
                if (Tools.isEmpty(addr)) {
                    mHandler.obtainMessage(Constant.LOCATION_FAIL).sendToTarget();
                } else {
                    mHandler.obtainMessage(Constant.LOCATION_SUCCESS).sendToTarget();
                }
            }
        }
    };

    private void getData(final String type, final String id) {

        String httpUrl = User.mainurl + "sf/CdepUnion";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("type", type);
        parameters_userInfo.put("cdepperson", id);
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//							Log.e("=====", dataJson.toString());
                            String code = dataJson.getString("code");
                            if ("0".equals(code)) {
                                companyList.clear();
                                compantIds.clear();
                                JSONArray arrayType = dataJson.getJSONArray("list");
                                if ("0".equals(type)) {
                                    if (arrayType.length() > 1) {
                                        companyList.add("请选择");
                                    }
                                    for (int j = 0; j < arrayType.length(); j++) {
                                        JSONObject obj = arrayType.getJSONObject(j);
                                        companyList.add(obj.getString("ccusname"));
                                        compantIds.add(obj.getString("ccuscode"));
                                    }
                                    reloadSpinner(companySpn, companyList);
                                } else if ("2".equals(type)) {

                                    departList.add("请选择");
                                    for (int j = 0; j < arrayType.length(); j++) {
                                        JSONObject obj = arrayType.getJSONObject(j);
                                        departList.add(obj.getString("ccusname"));
                                        departIds.add(obj.getString("ccuscode"));
                                    }
                                    reloadSpinner(departSpn, departList);
                                } else if ("3".equals(type)) {
                                    areaList.add("请选择");
                                    for (int j = 0; j < arrayType.length(); j++) {
                                        JSONObject obj = arrayType.getJSONObject(j);
                                        areaList.add(obj.getString("ccusname"));
                                        areaIds.add(obj.getString("ccuscode"));
                                    }
                                    reloadSpinner(areaSpn, areaList);
                                } /*else if ("3".equals(type)) {
                                    shopList.clear();
                                    shopIds.clear();
                                    shopList.add("请选择");
                                    for (int j = 0; j < arrayType.length(); j++) {
                                        JSONObject obj = arrayType.getJSONObject(j);
                                        shopList.add(obj.getString("ccusname"));
                                        shopIds.add(obj.getString("ccuscode"));
                                    }
                                    reloadSpinner(shopSpn, shopList);
                                }*/else if ("4".equals(type)) {
                                    plot_list.add("请选择");
                                    plotIds.add("");
                                    for (int j = 0; j < arrayType.length(); j++) {
                                        JSONObject obj = arrayType.getJSONObject(j);
                                        plot_list.add(obj.getString("ccusname"));
                                        plotIds.add(obj.getString("ccuscode"));
                                        reloadSpinner(plotSpn, plot_list);
                                    }
                                    if (customerType.equals("光交箱")){
                                        //获取到小区的数据以后转换为m数组
                                        manyPlotSpan.setEnabled(true);
                                        m = (String[])plot_list.toArray(new String[plot_list.size()]);
                                        manyPlotSpan.initContent(m);
                                    }

                                }else if ("5".equals(type)) {
                                    business_list.add("请选择");
                                    businessIds.add("");
                                    for (int j = 0; j < arrayType.length(); j++) {
                                        JSONObject obj = arrayType.getJSONObject(j);
                                        business_list.add(obj.getString("ccusname"));
                                        businessIds.add(obj.getString("ccuscode"));
                                    }
                                    reloadSpinner(businessSpn, business_list);
                                }
                            }
                        else if ("1".equals(code)) {
//								ToastUtil.toast(context, "没有数据");
                                if ("3".equals(type)) {
                                    shopId = id;
                                    //from = "yyt";
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/save_jwcj";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("type", "0");
        parameters_userInfo.put("cdepperson", cdepperson);
       // parameters_userInfo.put("cdepperson", shopId);
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("fj", from);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String code = object.getString("code");
                    if ("0".equals(code)) {
                       if ((customerType.equals("光交箱") || customerType.equals("光配箱"))){
                           upServer();
                       }else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                   .setTitle("该点已有位置信息确定重新提交？")
                                   .setNegativeButton("取消", null)
                                   .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           ProgressDialogUtil.showProgressDialog(context);
                                           upServer();
//                                        if (GetInfo.getIfSf(context))
//                                            saveDy();
                                       }
                                   });
                           builder.show();
                       }
                    } else if ("-1".equals(code)) {
                        upServer();
//                        if (GetInfo.getIfSf(context))
//                            saveDy();
                    } else {
                        ToastUtil.toast(context, "无权限");
                    }
                } catch (JSONException e) {
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


    private void upServer() {

        String httpUrl = User.mainurl + "sf/save_jwcj";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        switch (customerType){
            case "小区/社区":
                parameters_userInfo.put("addr", Escape.escape(addr));
                parameters_userInfo.put("jd", longitude);
                parameters_userInfo.put("wd", latitude);
                parameters_userInfo.put("image", uploadBuffer+","+uploadBuffer2);
                parameters_userInfo.put("type", "2");
                parameters_userInfo.put("mac", mac);
                parameters_userInfo.put("usercode", username);
                parameters_userInfo.put("fj", "xq");        //上传的类型
                parameters_userInfo.put("cdepperson",cdepperson);
                parameters_userInfo.put("sq", sqId);              //社区
                break;
            case "渠道商家":
                parameters_userInfo.put("addr", Escape.escape(addr));
                parameters_userInfo.put("jd", longitude);
                parameters_userInfo.put("wd", latitude);
                parameters_userInfo.put("image", uploadBuffer+","+uploadBuffer2);
                parameters_userInfo.put("type", "2");
                parameters_userInfo.put("mac", mac);
                parameters_userInfo.put("usercode", username);
                parameters_userInfo.put("fj", "yyt");        //上传的类型
                parameters_userInfo.put("cdepperson",cdepperson);//小区

                break;
            case "光交箱":
                String[] sa=manyPlotSpan.getText().toString().trim().split(",");
                manyCdepperson = "";
                for (int i = 0; i < plot_list.size(); i++) {
                    for (int j = 0; j < sa.length; j++) {
                        if (plot_list.get(i).equals(sa[j])){
                            manyCdepperson = manyCdepperson + plotIds.get(i)+",";
                        }
                    }
                }
                parameters_userInfo.put("addr", Escape.escape(addr));
                parameters_userInfo.put("jd", longitude);
                parameters_userInfo.put("wd", latitude);
                parameters_userInfo.put("image", uploadBuffer+","+uploadBuffer2);
                parameters_userInfo.put("type", "2");
                parameters_userInfo.put("srea", Escape.escape(adress));
                parameters_userInfo.put("ccuscode", Escape.escape(zgdm));
                parameters_userInfo.put("mac", mac);
                parameters_userInfo.put("usercode", username);
                parameters_userInfo.put("fj", "gjx");        //上传的类型
                //parameters_userInfo.put("sf", ifSf);
                parameters_userInfo.put("cdepperson",Escape.escape(et_box.getText().toString().trim()));
                parameters_userInfo.put("operator",Escape.escape(operatorType));   //运营商
                parameters_userInfo.put("xq",manyCdepperson);//所属小区编码，多个
                parameters_userInfo.put("sq",sqId);//所属社区编码
                break;
            case "光配箱":
                parameters_userInfo.put("inumber", Escape.escape(conUser));
                parameters_userInfo.put("srea", Escape.escape(adress));
                parameters_userInfo.put("ccuscode", Escape.escape(zgdm));
                parameters_userInfo.put("addr", Escape.escape(addr));
                parameters_userInfo.put("jd", longitude);
                parameters_userInfo.put("wd", latitude);
                parameters_userInfo.put("image", uploadBuffer+","+uploadBuffer2);
                parameters_userInfo.put("type", "2");
                parameters_userInfo.put("mac", mac);
                parameters_userInfo.put("usercode", username);
                parameters_userInfo.put("fj", "gpx");        //上传的类型
                //parameters_userInfo.put("sf", ifSf);
                parameters_userInfo.put("cdepperson",Escape.escape(et_box.getText().toString().trim()));
                parameters_userInfo.put("operator",Escape.escape(operatorType));   //运营商
                parameters_userInfo.put("xq",cdepperson);   //所属小区编码，一个
                parameters_userInfo.put("sq",sqId);//所属社区编码，多个
                break;
            default:
                break;
        }
        /*parameters_userInfo.put("addr", Escape.escape(addr));
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("image", uploadBuffer);
        parameters_userInfo.put("type", "2");
        if (customerType .equals("光交箱") || customerType .equals("光配箱")){
            parameters_userInfo.put("cdepperson",et_box.getText().toString().trim());
        }else {
            parameters_userInfo.put("cdepperson",cdepperson);
        }

        //parameters_userInfo.put("cdepperson", shopId);
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("fj", from);        //上传的类型
        parameters_userInfo.put("sf", ifSf);


        parameters_userInfo.put("operator",operatorType);       //运营商
      //  parameters_userInfo.put("sq",);             //所属社区编码*/



        client_request.post(httpUrl, parameters_userInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataJson = new JSONObject(response);
                    String code = dataJson.getString("code");
                    if ("0".equals(code)) {
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


//    private void saveDy() {
//
//        String httpUrl = User.dyMainurl + "sf/save_jwcj";
//
//        AsyncHttpClient client_request = new AsyncHttpClient();
//        RequestParams parameters_userInfo = new RequestParams();
//
//        parameters_userInfo.put("addr", Escape.escape(addr));
//        parameters_userInfo.put("jd", longitude);
//        parameters_userInfo.put("wd", latitude);
//        parameters_userInfo.put("image", uploadBuffer);
//        parameters_userInfo.put("type", "1");
//        parameters_userInfo.put("cdepperson", shopId);
//        parameters_userInfo.put("mac", mac);
//        parameters_userInfo.put("usercode", username);
//        parameters_userInfo.put("fj", from);
//        parameters_userInfo.put("sf", ifSf);
//
//        client_request.post(httpUrl, parameters_userInfo, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//                try {
//                    JSONObject dataJson = new JSONObject(response);
//                    String code = dataJson.getString("code");
//                    if ("0".equals(code)) {
//
//                    } else {
//                        ToastUtil.toast(context, getResources().getString(R.string.dy_wrong_mes));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    ProgressDialogUtil.closeProgressDialog();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable error, String data) {
//                error.printStackTrace(System.out);
//                ProgressDialogUtil.closeProgressDialog();
//            }
//        });
//    }

    public void getaddr(String longitude, String latitude) {
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

    public void reloadSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                CollectActivity.this,
                R.layout.item_spinner,
                list);
        spinner.setAdapter(adapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//		Log.i(TAG, "onSaveInstanceState,conversation="+conversationinfo.hashCode());
//		outState.putSerializable("conversation", conversationinfo);
        if (!TextUtils.isEmpty(imgPath)) {
            outState.putString("imgPath", imgPath);
        }
        if (!TextUtils.isEmpty(imgPath2)) {
            outState.putString("imgPath2", imgPath2);
        }
//		outState.putSerializable("targetId", conversationinfo.getTargetId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_get_customer:
                if (TextUtils.isEmpty(customerType) || customerType.equals("请选择")) {
                    ToastUtil.toast(context, "请选择客户类别");
                } else {
                    String customer = unitEdt.getText().toString();
                    Intent intent = new Intent();
                    intent.setClass(context, CustomerActivity.class);
                    intent.putExtra("name", customer);
                    intent.putExtra("type", "1");
                    startActivityForResult(intent, Constant.GET_CUSTOMER);
                }
                break;
            case R.id.txt_refresh:
                getAddrLocation();
                break;
            case R.id.txt_map:
                if (!TextUtils.isEmpty(latitude)) {
                    Intent intent = new Intent();
                    intent.setClass(context, QueryMapActivity.class);
                    intent.putExtra("jd", longitude);
                    intent.putExtra("wd", latitude);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "请等待位置加载");
                }
                break;

            case R.id.btn_submit:
                addr = addrText.getText().toString();

                //详细地址、资管代码、接入用户
                adress = etAdress.getText().toString().trim();
                zgdm = etZgdm.getText().toString().trim();
                conUser = etConUser.getText().toString().trim();
                String box = et_box.getText().toString().trim();

                if (TextUtils.isEmpty(uploadBuffer)) {
                    ToastUtil.toast(context, "请先照相再上传");
                } else if (Tools.isEmpty(shopId)) {
                    ToastUtil.toast(context, "请选择");
                } else if (customerType.equals("光交箱") && operatorType.equals("中国移动") &&
                        null==plotName||plotName.equals("请选择")){
                    ToastUtil.toast(context,"请选择小区");
                }else if((customerType.equals("光交箱")||customerType.equals("光配箱"))&& adress.isEmpty()){
                    ToastUtil.toast(context,"请输入详细地址");
                }else if((customerType.equals("光交箱")||customerType.equals("光配箱"))&&(!operatorType.equals("中国移动"))&&
                        box.isEmpty()){
                    ToastUtil.toast(context,"请输入光交箱代码");
                }else if(customerType.equals("光配箱")&&conUser.isEmpty()){
                    ToastUtil.toast(context,"请输入光配箱接入用户");
                } else if ("Gps".equals(type) || "Wifi".equals(type)) {
                    ProgressDialogUtil.showProgressDialog(context);
                    visitServer();
                }else if (null == plotName || plotName.equals("请选择")){
                    ToastUtil.toast(context,"请选择小区");
                }
                else {
                    ToastUtil.toast(context, "请刷新位置信息到Gps或Wifi再提交");
                }
                break;
            case R.id.photo_image:
                String imagePath = Tools.getSDPath() + "/dyyd/photo.jpg";
                if (!Tools.isEmpty(imagePath)) {
                    PhotoHelper.openPictureDialog(CollectActivity.this, imagePath);
                }
                break;
            case R.id.photo_image2:
                String image2Path = Tools.getSDPath() + "/dyyd/photo2.jpg";
                if (!Tools.isEmpty(image2Path)) {
                    PhotoHelper.openPictureDialog(CollectActivity.this, image2Path);
                }
                break;
            case R.id.take_photo1:
                if (Tools.isSDCardExit()){
                    takePhoto(1);
                }else {
                    ToastUtil.toast(context,"内存卡不存在不能拍照");
                }
                break;
            case R.id.take_photo2:
                if (Tools.isSDCardExit()){
                    if (uploadBuffer == null){
                        ToastUtil.toast(context,"请先选择照片1");
                    }else {
                        takePhoto(2);
                    }

                }else {
                    ToastUtil.toast(context,"内存卡不存在不能拍照");
                }
                break;

            default:
                break;
        }
    }
}
