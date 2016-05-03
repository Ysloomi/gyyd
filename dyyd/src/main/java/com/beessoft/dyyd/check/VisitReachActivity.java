package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

	private String customer, person, aim, location, type,
			customerType, iclass, examineResultString, longtitude, latitude,
			addr, examineUrl, ifExamine;
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

	private Boolean haveSpinner = true;
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

//	@Override

//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.visit_actions, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		Intent intent = new Intent();
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//			return true;
//		case R.id.action_material:
//			intent.setClass(VisitReachActivity.this, WorkBookActivity.class);
//			startActivity(intent);
//			return true;
//		case R.id.action_target:
//			intent.setClass(VisitReachActivity.this, BranchTargetActivity.class);
//			startActivity(intent);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@SuppressLint("ClickableViewAccessibility")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visitreach);

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

		visitServe_Customer();
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
					if(haveSpinner){
						customer = customerEdit.getText().toString();
						person = personEdit.getText().toString();
						aim = aimEdit.getText().toString();
						location = addrText.getText().toString();
						customerType = typeSpinner.getSelectedItem().toString();
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
							} else {
//							if ("0".equals(ifExamine)) {
								if ("渠道类".equals(customerType)&&Tools.isEmpty(leavetype)){
									ToastUtil.toast(context,"请重新选择客户，等待有效范围的判定");
								}else{
									confirmInfo();
								}

//							} else {
//								inputIfExamineDialog();
//							}
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

	private void inputIfExamineDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				VisitReachActivity.this);
		builder.setTitle("提示").setMessage("是否检查").setCancelable(false)
				.setPositiveButton("检查", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						inputExamineDialog();
					}
				}).setNegativeButton("不检查", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						confirmInfo();
					}
		}).show();

	}

	@SuppressLint({ "InflateParams", "SetJavaScriptEnabled" })
	private void inputExamineDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.examine, null);

		final EditText editText1 = (EditText) view
				.findViewById(R.id.examine_text);
		final WebView webView = (WebView) view.findViewById(R.id.examine_web);
		final ProgressBar progressBar = (ProgressBar) view
				.findViewById(R.id.examine_progressBar);

		final AlertDialog myDialog = new AlertDialog.Builder(
				VisitReachActivity.this).setView(view)
				.setPositiveButton("确认", null).setNegativeButton("取消", null)
				.setCancelable(false).create();

		if ("1".equals(ifExamine)) {
			String url = User.mainurl + examineUrl;
			// webview定义参数
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setAppCacheEnabled(true);
			webSettings.setDatabaseEnabled(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setGeolocationDatabasePath("/data/data/<my-app>");
			webSettings.setGeolocationEnabled(true);
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
			webSettings.setAppCacheMaxSize(8 * 1024 * 1024);
			webSettings.setAllowFileAccess(true);
			webSettings.setSaveFormData(true);
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

			// 显示web
			webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			webView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					Message msg = new Message();
					msg.what = 200;
					msg.obj = newProgress;
					// 进度条到100时，自动消失
					if (newProgress >= 100) {
						progressBar.setVisibility(View.GONE);
					}
				}
			});

			webView.loadUrl(url);

			// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
			// 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
			webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
					view.loadUrl(url);
					// 记得消耗掉这个事件。给不知道的朋友再解释一下，
					// Android中返回True的意思就是到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
					return true;
				}
			});
			editText1.setHint("请输入检查结果");
		} else if ("2".equals(ifExamine)) {
			webView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			myDialog.setTitle("请输入检查结果");
		}

		myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button button = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						examineResultString = editText1.getText().toString();
						if ("".equals(examineResultString)) {
							Toast.makeText(VisitReachActivity.this, "请填写检查结果",
									Toast.LENGTH_SHORT).show();
						} else {
							confirmInfo();
							myDialog.dismiss();
						}
					}
				});
				Button button1 = myDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
				button1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						confirmInfo();
						myDialog.dismiss();
					}
				});
			}
		});
		myDialog.show();
	}

	public void confirmInfo() {
		if ("否".equals(leavetype)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("不再有效范围是否确认提交")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProgressDialogUtil.showProgressDialog(context);
							visitServer(customerType, customer, person, aim, longtitude, latitude, location);
						}
					}).setNegativeButton("否", null);
			builder.show();
		} else {
			ProgressDialogUtil.showProgressDialog(context);
			visitServer(customerType, customer, person, aim, longtitude, latitude, location);
		}
	}

	public void getAddrLocation() {
		mThread = new Thread(runnable);
		if (Gps.exist(VisitReachActivity.this, "distance.db")) {
			addrText.setText("正在定位...");
			distanceHelper = new DistanceDatabaseHelper(
					getApplicationContext(), "distance.db", 1);
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

	private void visitServe_Customer() {

		String httpUrl = User.mainurl + "sf/startvisit";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							int code = dataJson.getInt("code");
//							String msg = dataJson.getString("msg");
							if (code==0){
								JSONArray arrayType = dataJson.getJSONArray("typelist");
								List<String> list = new ArrayList<String>();
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									list.add(obj.getString("type"));
									haveSpinner= true;
								}
								ArrayAdapter<String> adapterType = new ArrayAdapter<String>(
										context,
										R.layout.spinner_item,
										list);
								typeSpinner.setAdapter(adapterType);
								typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
									@Override
									public void onItemSelected(AdapterView<?> parent, View view,
															   int position, long id) {
										iclass = parent.getItemAtPosition(position).toString();
										getListView();
									}

									@Override
									public void onNothingSelected(AdapterView<?> parent) {
										// 这个一直没有触发，我也不知道什么时候被触发。
										// 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
									}
								});
								examineUrl = dataJson.getString("checkbz");// 检查的网址
								ifExamine = dataJson.getString("checkflag");// 是否检查，0不检查，1检查,2不弹web
							}
//							ToastUtil.toast(context,msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
	}

	private void visitServer(String customerType, String customer, String person, String aim,
			String longitude, String latitude, String location) {

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
		parameters_userInfo.put("type", Escape.escape(customerType));
		parameters_userInfo.put("checkresult",Escape.escape(examineResultString));
		parameters_userInfo.put("leavetype",Escape.escape(leavetype));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
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
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							JSONObject obj = dataJson.getJSONObject("result");
							addr = obj.getString("formatted_address");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 根据选择的类别来进行分类
	 */
	private void getListView() {
		if (iclass.equals("集团类")) {
			getCustomerTxt.setVisibility(View.GONE);
			insideLl.setVisibility(View.GONE);
			customerEdit.setHint("请输入");
		} else if (iclass.equals("渠道类")) {
			getCustomerTxt.setVisibility(View.VISIBLE);
			insideLl.setVisibility(View.VISIBLE);
			customerEdit.setHint("请输入关键字搜索");
		}
	}
}