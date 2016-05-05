package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.Logger;
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

import java.util.ArrayList;
import java.util.List;

public class VisitReachActivity extends BaseActivity {

	private LocationClient mLocationClient;

	private String customer, person, aim,type,location,
			customerType,examineResultString, longtitude, latitude,
			addr;
	private String customerLat="";
	private String customerLng="";
	private String customerCode="";
	private String leavetype="";

	private TextView addrText;
	private TextView insideText;
	private EditText customerEdit;
	private EditText personEdit;
	private EditText aimEdit;

	private TextView getCustomerTxt;

	private LinearLayout insideLl;

	private Thread mThread;
	private Spinner typeSpinner;

	private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
	private static final int MSG_FAILURE = 1;// 获取定位失败的标识
	private static final int GET_CUSTOMER = 2;// 获取定位失败的标识

	private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
	private boolean ifLocation = false;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
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

	@SuppressLint("ClickableViewAccessibility")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reach);

		context = VisitReachActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);
		// 声明百度定位sdk的构造函数
		mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

		initView();

		Gps gps = new Gps(this);
		gps.openGPSSettings(this);
		if (Gps.exist(VisitReachActivity.this, "distance.db")) {
			Gps.GPS_do(mLocationClient, 8000);
		}

		getCustomerType();
		getAddrLocation();

		String a = PreferenceUtil.readString(context,"aim");
		if (!"".equals(a)) {
			aimEdit.setText(a);
		}
	}

	public void initView() {

		insideLl = (LinearLayout) findViewById(R.id.inside_ll);
		addrText = (TextView) findViewById(R.id.location_text);
		insideText = (TextView) findViewById(R.id.inside_tv);
		getCustomerTxt = (TextView) findViewById(R.id.txt_get_customer);

		customerEdit= (EditText) findViewById(R.id.visit_customer);
		personEdit= (EditText) findViewById(R.id.visit_person);
		aimEdit = (EditText) findViewById(R.id.visit_aim);
		typeSpinner = (Spinner) findViewById(R.id.type_spinner);

		getCustomerTxt.setOnClickListener(onClickListener);

		findViewById(R.id.txt_preserve).setOnClickListener(onClickListener);
		findViewById(R.id.txt_refresh).setOnClickListener(onClickListener);
		findViewById(R.id.btn_submit).setOnClickListener(onClickListener);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.txt_get_customer:
					customer = customerEdit.getText().toString();
					Intent intent = new Intent();
					intent.setClass(context,CustomerActivity.class);
					intent.putExtra("name",customer);
					startActivityForResult(intent,GET_CUSTOMER);
					break;
				case R.id.txt_preserve:
					aim = aimEdit.getText().toString();
					// 记住目的信息
					PreferenceUtil.write(context,"aim",aim);
					ToastUtil.toast(context, "保存成功");
					break;
				case R.id.txt_refresh:
					getAddrLocation();
					break;
				case R.id.btn_submit:
					customer = customerEdit.getText().toString();
					person = personEdit.getText().toString();
					aim = aimEdit.getText().toString();
					location = addrText.getText().toString();
					examineResultString = "";
					if (customerType.equals("请选择")) {
						ToastUtil.toast(context, "请选择客户类别");
					} else {
						if (TextUtils.isEmpty(customerType.trim()) ||
								TextUtils.isEmpty(customer.trim())
								|| TextUtils.isEmpty(person.trim())
								|| TextUtils.isEmpty(aim.trim())
								|| TextUtils.isEmpty(location.trim())) {
							ToastUtil.toast(context, "数据不能为空");
						} else if("正在定位...".equals(location)){
							ToastUtil.toast(context, "请等待位置刷新");
						} else {
							if ("渠道类".equals(customerType)&&Tools.isEmpty(leavetype)){
								ToastUtil.toast(context,"请重新选择客户，等待有效范围的判定");
							}else{
								confirmInfo();
							}
						}
					}
					break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK){
			if (data !=null){
				customerEdit.setText(data.getStringExtra("name"));
				customerLat = data.getStringExtra("lat");
				customerLng = data.getStringExtra("lng");
				customerCode = data.getStringExtra("ccuscode");//客户编码
				int scope = Integer.valueOf(data.getStringExtra("scope"));
				if (Tools.isEmpty(customerLat)){
					leavetype = "未采集";
					insideText.setText(leavetype);
				}else{
					if (!Tools.isEmpty(latitude)){
						LatLng p1 = new LatLng(Double.valueOf(latitude),Double.valueOf(longtitude));
						LatLng p2 = new LatLng(Double.valueOf(customerLat),Double.valueOf(customerLng));
						double distance = DistanceUtil. getDistance(p1, p2);
						leavetype = distance < scope ? "是" : "否" ;
						insideText.setText(leavetype);
					}else{
						ToastUtil.toast(context,"请先等待获取位置信息");
					}
				}
			}
		}
	}

	public void confirmInfo() {
		if ("否".equals(leavetype)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("不再有效范围是否确认提交")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProgressDialogUtil.showProgressDialog(context);
							visitServer(customerType, customer, person, aim, location);
						}
					}).setNegativeButton("否", null);
			builder.show();
		} else {
			ProgressDialogUtil.showProgressDialog(context);
			visitServer(customerType, customer, person, aim, location);
		}
	}

	public void getAddrLocation() {
		mThread = new Thread(runnable);
		if (Gps.exist(VisitReachActivity.this, "distance.db")) {
			addrText.setText("正在定位...");
			distanceHelper = new DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
			longtitude = Gps.getJd(distanceHelper);
			latitude = Gps.getWd(distanceHelper);
			type = Gps.getType(distanceHelper);
			visitServer_getaddr(longtitude, latitude);
			mThread.start();// 线程启动
			distanceHelper.close();
		} else {
			addrText.setText("正在定位...");
			if (!mLocationClient.isStarted()) {
				Gps.GPS_do(mLocationClient, 1100);
			}else{
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
				addr = myApp.getaddr();
				longtitude = myApp.getjd();
				latitude = myApp.getwd();
				type = myApp.getType();
				if (addr == null) {
					visitServer_getaddr(longtitude,latitude);
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
					// 向ui线程发送MSG_SUCCESS标识
					mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
				}
			}
		}
	};

	private void getCustomerType() {

		String httpUrl = User.mainurl + "sf/startvisit";

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
//							String msg = dataJson.getString("msg");
							if (code==0){
								JSONArray arrayType = dataJson.getJSONArray("typelist");
								List<String> list = new ArrayList<>();
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									list.add(obj.getString("type"));
								}
								ArrayAdapter<String> adapterType = new ArrayAdapter<>(
										context,
										R.layout.spinner_item,
										list);
								typeSpinner.setAdapter(adapterType);
								typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
									@Override
									public void onItemSelected(AdapterView<?> parent, View view,
															   int position, long id) {
										customerType = parent.getItemAtPosition(position).toString();
										getListView();
									}

									@Override
									public void onNothingSelected(AdapterView<?> parent) {
										// 这个一直没有触发，我也不知道什么时候被触发。
										// 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
									}
								});
//								examineUrl = dataJson.getString("checkbz");// 检查的网址
//								ifExamine = dataJson.getString("checkflag");// 是否检查，0不检查，1检查,2不弹web
							}
//							ToastUtil.toast(context,msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void visitServer(String customerType, String customer, String person, String aim, String location) {

		String httpUrl = User.mainurl + "sf/startvisit_save";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("cus", Escape.escape(customer));
		parameters_userInfo.put("visitperson", Escape.escape(person));
		parameters_userInfo.put("visitgoal", Escape.escape(aim));
		parameters_userInfo.put("addr", Escape.escape(location));
		parameters_userInfo.put("jd", longtitude);
		parameters_userInfo.put("wd", latitude);
		parameters_userInfo.put("ccuscode", customerCode);
		parameters_userInfo.put("type", Escape.escape(customerType));
		parameters_userInfo.put("checkresult",Escape.escape(examineResultString));
		parameters_userInfo.put("leavetype",Escape.escape(leavetype));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code == 0) {
								ToastUtil.toast(context, "到达现场数据上传成功");
								String reachTime = GetInfo.getDate();
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

	public void visitServer_getaddr(String longitude,String latitude) {
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

	private void getListView() {
		if (customerType.equals("集团类")) {
			getCustomerTxt.setVisibility(View.GONE);
			insideLl.setVisibility(View.GONE);
			customerEdit.setHint("请输入");
		} else if (customerType.equals("渠道类")) {
			getCustomerTxt.setVisibility(View.VISIBLE);
			insideLl.setVisibility(View.VISIBLE);
			customerEdit.setHint("请输入关键字搜索");
		}
	}
}