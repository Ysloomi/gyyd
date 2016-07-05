package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.ReachCustomer;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.ArrayAdapter;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VisitReachActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private LocationClient mLocationClient;

    private String customer, person, aim, type, location,
            customerType, longitude, latitude, addr;
    private List<ReachCustomer> reachCustomers = new ArrayList<>();

    private String from;
    //	private String customerLat="";
//	private String customerLng="";
    private String customerCode = "";
//	private int customerScope;
//	private String leavetype="";

    private TextView addrText;
//    private TextView insideText;
//    private EditText customerEdit;
    private Spinner customerSpn;
    private EditText personEdit;
    private EditText aimEdit;

//    private TextView getCustomerTxt;

//    private LinearLayout insideLl;

    private Thread mThread;
    private Spinner typeSpinner;

    private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
    private static final int MSG_FAILURE = 1;// 获取定位失败的标识
    private static final int GET_CUSTOMER = 2;// 获取定位失败的标识

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类

    private boolean ifLocation = false;
    private boolean ifGetCustomer = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {// 此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    addrText.setText("[" + type + "]" + addr);// textView显示从定位获取到的地址
//				if (!TextUtils.isEmpty(from)){
//					if (Tools.isEmpty(customerLat)||"0".equals(customerLat)){
//						leavetype = "未采集";
//						insideText.setText(leavetype);
//					}else{
//						if (!Tools.isEmpty(latitude)){
//							LatLng p1 = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
//							LatLng p2 = new LatLng(Double.valueOf(customerLat),Double.valueOf(customerLng));
//							double distance = DistanceUtil. getDistance(p1, p2);
//							leavetype = distance < customerScope ? "是" : "否" ;
//							insideText.setText(leavetype);
//						}else{
//							ToastUtil.toast(context,"请先等待获取位置信息");
//						}
//					}
//				}
                    if (ifGetCustomer)
                        getCustomer();
                    break;
                case MSG_FAILURE:
                    addrText.setText("请重新定位");
                    break;
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reach);

        context = VisitReachActivity.this;
        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
        from = getIntent().getStringExtra("from");

        initView();

        Gps gps = new Gps(this);
        gps.openGPSSettings(this);
        if (Gps.exist(VisitReachActivity.this, "distance.db")) {
            Gps.GPS_do(mLocationClient, 8000);
        }

        getAddrLocation();

//		if (!TextUtils.isEmpty(from)){
//			customer = getIntent().getStringExtra("name");
//			customerCode = getIntent().getStringExtra("customercode");
//			customerLat = getIntent().getStringExtra("lat");
//			customerLng= getIntent().getStringExtra("lng");
//			customerScope = getIntent().getIntExtra("scope",0);
//			customerEdit.setText(customer);
//		}

        String a = PreferenceUtil.readString(context, "aim");
        if (!"".equals(a)) {
            aimEdit.setText(a);
        }
    }

    public void initView() {

//        insideLl = (LinearLayout) findViewById(R.id.inside_ll);
        addrText = (TextView) findViewById(R.id.location_text);
//        insideText = (TextView) findViewById(R.id.inside_tv);
//        getCustomerTxt = (TextView) findViewById(R.id.txt_get_customer);

//		customerEdit= (EditText) findViewById(R.id.visit_customer);
        customerSpn = (Spinner) findViewById(R.id.visit_customer);
        personEdit = (EditText) findViewById(R.id.visit_person);
        aimEdit = (EditText) findViewById(R.id.visit_aim);
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);

//        getCustomerTxt.setOnClickListener(onClickListener);

        findViewById(R.id.txt_preserve).setOnClickListener(this);
        findViewById(R.id.txt_refresh).setOnClickListener(this);
        findViewById(R.id.txt_map).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        List<String> list = new ArrayList<>();
        list.add("渠道商家");
        list.add("政企单位");

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                context,
                R.layout.item_spinner,
                list);
        typeSpinner.setAdapter(adapterType);
        typeSpinner.setOnItemSelectedListener(this);
        customerSpn.setOnItemSelectedListener(this);
    }

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode==RESULT_OK){
//			if (data !=null){
//				customerEdit.setText(data.getStringExtra("name"));
//				customerLat = data.getStringExtra("lat");
//				customerLng = data.getStringExtra("lng");
//				customerCode = data.getStringExtra("ccuscode");//客户编码
//				customerScope = Integer.valueOf(data.getStringExtra("scope"));
//				if (Tools.isEmpty(customerLat)||"0".equals(customerLat)){
//					leavetype = "未采集";
//					insideText.setText(leavetype);
//				}else{
//					if (!Tools.isEmpty(latitude)){
//						LatLng p1 = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
//						LatLng p2 = new LatLng(Double.valueOf(customerLat),Double.valueOf(customerLng));
//						double distance = DistanceUtil. getDistance(p1, p2);
//						leavetype = distance < customerScope ? "是" : "否" ;
//						insideText.setText(leavetype);
//					}else{
//						ToastUtil.toast(context,"请先等待获取位置信息");
//					}
//				}
//			}else {
//				leavetype = "未采集";
//				insideText.setText(leavetype);
//			}
//		}
//	}

    public void confirmInfo() {
//		if ("否".equals(leavetype)) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(context);
//			builder.setTitle("不再有效范围是否确认提交")
//					.setPositiveButton("是", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							ProgressDialogUtil.showProgressDialog(context);
//							saveData(customerType, customer, person, aim, location);
////							if (GetInfo.getIfSf(context))
////								saveDy(customerType, customer, person, aim, location);
//						}
//					}).setNegativeButton("否", null);
//			builder.show();
//		} else {
        ProgressDialogUtil.showProgressDialog(context);
        saveData(customer, person, aim, location);
//			if (GetInfo.getIfSf(context))
//				saveDy(customerType, customer, person, aim, location);
//		}
    }

    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(context, "distance.db")) {
            addrText.setText("正在定位...");
            distanceHelper = new DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
            longitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            type = Gps.getType(distanceHelper);
            visitServer_getaddr(longitude, latitude);
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
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void run() {// run()在新的线程中运行
            int sleepcount = 1500;
            if (!Gps.exist(VisitReachActivity.this, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                if (!ifLocation) {
                    sleepcount = 3300;
                }
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Gps.exist(VisitReachActivity.this, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                addr = myApp.getAddr();
                longitude = myApp.getJd();
                latitude = myApp.getWd();
                type = myApp.getType();
                if (TextUtils.isEmpty(addr)) {
                    visitServer_getaddr(longitude, latitude);
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

    private void getCustomer() {

        String httpUrl = User.mainurl + "sf/startvisit";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("type", customerType);
        parameters_userInfo.put("sf", ifSf);

//        Logger.e(httpUrl + "?" + parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            reachCustomers.clear();
                            int code = dataJson.getInt("code");
                            List<String> list = new ArrayList<>();
//							String msg = dataJson.getString("msg");
                            if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    ReachCustomer reachCustomer = new ReachCustomer();
                                    list.add(obj.getString("ccusname"));
                                    reachCustomer.setName(obj.getString("ccusname"));
                                    reachCustomer.setCode(obj.getString("ccuscode"));
//                                    reachCustomer.setAim(obj.getString("visitgoal"));
                                    reachCustomers.add(reachCustomer);
                                }
                            } else
                                ToastUtil.toast(context, "周围无客户或客户未采集");

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    context,
                                    R.layout.item_spinner,
                                    list);
                            customerSpn.setAdapter(adapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
//                            if (!TextUtils.isEmpty(from)) {
//                                typeSpinner.setSelection(1, true);
//                            }
                        }
                    }
                });
    }

    private void saveData(String customer, String person, String aim, String location) {

        String httpUrl = User.mainurl + "sf/startvisit_save";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("cus", Escape.escape(customer));
        parameters_userInfo.put("visitperson", Escape.escape(person));
        parameters_userInfo.put("visitgoal", Escape.escape(aim));
        parameters_userInfo.put("addr", Escape.escape(location));
        parameters_userInfo.put("jd", longitude);
        parameters_userInfo.put("wd", latitude);
        parameters_userInfo.put("ccuscode", customerCode);
        parameters_userInfo.put("type", customerType);
//        parameters_userInfo.put("checkresult", Escape.escape(""));
//        parameters_userInfo.put("leavetype", Escape.escape(""));
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                ToastUtil.toast(context, "到达现场数据上传成功");
                                String reachTime = DateUtil.getDate();
                                PreferenceUtil.write(context, "reachTime", reachTime);//保存到达时间
                                PreferenceUtil.write(context, "aim", "");//上传成功后清除
                                finish();
                            } else if (code == 1) {
                                ToastUtil.toast(context, "当前数据不能保存：尚有到达现场后，没有离开的拜访");
                            } else if (code == 2) {
                                ToastUtil.toast(context, "渠道类客户不存在，不能保存，请检查");
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


//	private void saveDy(String customerType, String customer, String person, String aim, String location) {
//
//		String httpUrl = User.mainurl + "sf/startvisit_save";
//
//		AsyncHttpClient client_request = new AsyncHttpClient();
//		RequestParams parameters_userInfo = new RequestParams();
//
//		parameters_userInfo.put("mac", mac);
//		parameters_userInfo.put("usercode", username);
//		parameters_userInfo.put("cus", Escape.escape(customer));
//		parameters_userInfo.put("visitperson", Escape.escape(person));
//		parameters_userInfo.put("visitgoal", Escape.escape(aim));
//		parameters_userInfo.put("addr", Escape.escape(location));
//		parameters_userInfo.put("jd", longitude);
//		parameters_userInfo.put("wd", latitude);
//		parameters_userInfo.put("ccuscode", customerCode);
//		parameters_userInfo.put("type", Escape.escape(customerType));
//		parameters_userInfo.put("checkresult",Escape.escape(examineResultString));
//		parameters_userInfo.put("leavetype",Escape.escape(leavetype));
//		parameters_userInfo.put("sf", ifSf);
//
//		client_request.post(httpUrl, parameters_userInfo,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(String response) {
//						try {
//							JSONObject dataJson = new JSONObject(response);
//							int code = dataJson.getInt("code");
//							if (code == 0) {
//
//							}  else {
//								ToastUtil.toast(context, getResources().getString(R.string.dy_wrong_mes));
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							ProgressDialogUtil.closeProgressDialog();
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable error, String data) {
//						error.printStackTrace(System.out);
//						ProgressDialogUtil.closeProgressDialog();
//					}
//				});
//	}

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.type_spinner:
                customerType = (position + 1) + "";
                if (ifGetCustomer)
                    getCustomer();
                ifGetCustomer = true;
                break;
            case R.id.visit_customer:
                customer = reachCustomers.get(position).getName();
                customerCode = reachCustomers.get(position).getCode();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//				case R.id.txt_get_customer:
//					if (TextUtils.isEmpty(customerType)||customerType.equals("请选择")) {
//						ToastUtil.toast(context, "请选择客户类别");
//					} else {
//						customer = customerEdit.getText().toString();
//						String a = "政企单位".equals(customerType)?"1":"";
//						Intent intent = new Intent();
//						intent.setClass(context, CustomerActivity.class);
//						intent.putExtra("name", customer);
//						intent.putExtra("type", a);
//						startActivityForResult(intent, GET_CUSTOMER);
//					}
//					break;
            case R.id.txt_preserve:
                aim = aimEdit.getText().toString();
                // 记住目的信息
                PreferenceUtil.write(context, "aim", aim);
                ToastUtil.toast(context, "保存成功");
                break;
            case R.id.txt_refresh:
                getAddrLocation();
                break;
            case R.id.btn_submit:
//                    customer = customerEdit.getText().toString();
                person = personEdit.getText().toString();
                aim = aimEdit.getText().toString();
                location = addrText.getText().toString();
                if (TextUtils.isEmpty(customer.trim())
                        || TextUtils.isEmpty(person.trim())
                        || TextUtils.isEmpty(aim.trim())) {
                    ToastUtil.toast(context, "数据不能为空");
                } else if ("正在定位...".equals(location)) {
                    ToastUtil.toast(context, "请等待位置刷新");
                } else {
//							if ("渠道类".equals(customerType)&&Tools.isEmpty(leavetype)){
//								ToastUtil.toast(context,"请重新选择客户，等待有效范围的判定");
//							}else{
                    confirmInfo();
//							}
                }

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
        }
    }

//	private void getListView() {
//		if (customerType.equals("集团类")) {
//			getCustomerTxt.setVisibility(View.GONE);
//			insideLl.setVisibility(View.GONE);
//			customerEdit.setHint("请输入");
//		} else if (customerType.equals("渠道类")) {
//			getCustomerTxt.setVisibility(View.VISIBLE);
//			insideLl.setVisibility(View.VISIBLE);
//			customerEdit.setHint("请输入关键字搜索");
//		}
//	}
}