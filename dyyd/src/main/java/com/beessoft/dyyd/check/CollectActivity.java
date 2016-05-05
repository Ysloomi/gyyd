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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.beessoft.dyyd.utils.Constant;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
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

public class CollectActivity extends BaseActivity {

	private LinearLayout companyLl;
	private LinearLayout departLl;
	private LinearLayout areaLl;
	private LinearLayout shopLl;
	private LinearLayout unitLl;

	private TextView addrText;
	private EditText unitEdt;
	private ImageView photoImage;

	private Spinner typeSpn;
	private Spinner companySpn;
	private Spinner departSpn;
	private Spinner areaSpn;
	private Spinner shopSpn;
	
	private ArrayList<String> typeList = new ArrayList<String>();
	private ArrayList<String> companyList = new ArrayList<String>();
	private ArrayList<String> compantIds = new ArrayList<String>();
	private ArrayList<String> departList = new ArrayList<String>();
	private ArrayList<String> departIds = new ArrayList<String>();
	private ArrayList<String> areaList = new ArrayList<String>();
	private ArrayList<String> areaIds = new ArrayList<String>();
	private ArrayList<String> shopList = new ArrayList<String>();
	private ArrayList<String> shopIds = new ArrayList<String>();
	
	private String longitude;
	private String latitude;
	private String addr;
	private String type;
	private String shopId;
	private String from;
	private String customerType;

	private int iflag = 0;
	//照相
	private Bitmap bitmap;
	private String imgPath;
	private String uploadBuffer = null;
	public static final int PHOTO_CODE = 5;
	
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
		if(savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))){
//			Logger.e("拍摄异常，获取原来的shot_path");
			imgPath = savedInstanceState.getString("imgPath");
		}
		context= CollectActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);
		mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
		
		initView();
		// 获取定位地址
		getAddrLocation();
//		type  操作类型(0 一级  1 二级  2 三级 3 四级)
//		 name 上级名（第一级不传）
		getData("0","");
	}

	public void initView() {

		companyLl = (LinearLayout) findViewById(R.id.ll_company);
		departLl = (LinearLayout) findViewById(R.id.ll_depart);
		areaLl = (LinearLayout) findViewById(R.id.ll_area);
		shopLl = (LinearLayout) findViewById(R.id.ll_shop);
		unitLl = (LinearLayout) findViewById(R.id.ll_unit);

		addrText = (TextView) findViewById(R.id.location_text);
		unitEdt = (EditText) findViewById(R.id.edt_unit);

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

		photoImage = (ImageView) findViewById(R.id.photo_image);
		photoImage.setOnClickListener(clickListener);


		findViewById(R.id.txt_refresh).setOnClickListener(clickListener);
		findViewById(R.id.txt_take_photo).setOnClickListener(clickListener);
		findViewById(R.id.txt_get_customer).setOnClickListener(clickListener);
		findViewById(R.id.btn_submit).setOnClickListener(clickListener);


		typeList.add("请选择");
		typeList.add("渠道商家");
		typeList.add("政企单位");

		reloadSpinner(typeSpn, typeList);
	}
	
	OnClickListener clickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
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
				case R.id.txt_take_photo:
					if (Tools.isSDCardExit()) {
						takePhoto();
					} else {
						ToastUtil.toast(context, "内存卡不存在不能拍照");
					}
					break;
				case R.id.btn_submit:
					addr = addrText.getText().toString();
					if (TextUtils.isEmpty(uploadBuffer)) {
						ToastUtil.toast(context, "请先照相再上传");
					} else if (Tools.isEmpty(shopId)) {
						ToastUtil.toast(context, "请选择");
					} else if ("Gps".equals(type) || "Wifi".equals(type)) {
						ProgressDialogUtil.showProgressDialog(context);
						visitServer();
					} else {
						ToastUtil.toast(context, "请刷新位置信息到Gps或Wifi再提交");
					}
					break;
				case R.id.photo_image:
					String imagePath = Tools.getSDPath() + "/dyyd/photo.jpg";
					if (!Tools.isEmpty(imagePath)) {
						PhotoHelper.openPictureDialog(CollectActivity.this, imagePath);
					}
					break;
				default:
					break;
			}
		}
	};
	
	OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String name ="";
			switch (parent.getId()) {
				case R.id.spn_type:
					customerType = typeList.get(position);
					if ("政企单位".equals(customerType)){
						companyLl.setVisibility(View.GONE);
						departLl.setVisibility(View.GONE);
						areaLl.setVisibility(View.GONE);
						shopLl.setVisibility(View.GONE);
						unitLl.setVisibility(View.VISIBLE);
						from = "jt";
					} else if ("渠道商家".equals(customerType)) {
						companyLl.setVisibility(View.VISIBLE);
						departLl.setVisibility(View.VISIBLE);
						areaLl.setVisibility(View.VISIBLE);
						shopLl.setVisibility(View.VISIBLE);
						unitLl.setVisibility(View.GONE);
					} else {
						companyLl.setVisibility(View.GONE);
						departLl.setVisibility(View.GONE);
						areaLl.setVisibility(View.GONE);
						shopLl.setVisibility(View.GONE);
						unitLl.setVisibility(View.GONE);
					}
					break;
				case R.id.block_spinner:
					name = companyList.get(position);
					if (!"请选择".equals(name)) {
						String locaId = "";
						if (compantIds.size() != 1) {
							locaId = compantIds.get(position - 1);
						} else {
							locaId = compantIds.get(0);
						}
						shopId = locaId;
						from = "fgs";
						getData("1", locaId);
					} else if ("请选择".equals(name) || "".equals(name)) {
						departList.clear();
						reloadSpinner(departSpn, departList);
						shopId = "";
					}
					break;
				case R.id.street_spinner:
					name = departList.get(position);
					if (!"请选择".equals(name)) {
						String locaId = departIds.get(position - 1);
						shopId = locaId;
						from = "fj";
						getData("2", locaId);
					} else if ("请选择".equals(name) || "".equals(name)) {
						areaList.clear();
						reloadSpinner(areaSpn, areaList);
					}
					break;
				case R.id.community_spinner:
					name = areaList.get(position);
					if (!"请选择".equals(name)) {
						String locaId = areaIds.get(position - 1);
						getData("3", locaId);
					} else if ("请选择".equals(name) || "".equals(name)) {
						shopList.clear();
						reloadSpinner(shopSpn, shopList);
					}
					break;
				case R.id.shop_spinner:
					name = shopList.get(position);
					if (!"请选择".equals(name)) {
						shopId = shopIds.get(position - 1);
						from = "yyt";
//					Log.e("main", "shopId>>>>"+shopId);
					} else if ("请选择".equals(name)) {
						shopId = "";
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
		switch (requestCode) {
			case PHOTO_CODE:
				uploadBuffer = "";
				if (!Tools.isEmpty(imgPath)) {
					File imageFile = new File(imgPath);
					bitmap = PhotoUtil.imageEncode(imageFile);
					photoImage.setImageBitmap(bitmap);
					uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
					imgPath="";
				}
				break;
			case Constant.GET_CUSTOMER:
				if (data !=null){
					unitEdt.setText(data.getStringExtra("name"));
					shopId = data.getStringExtra("ccuscode");//客户编码
				}else{
					shopId ="";
				}
				break;
			default:
				break;
			}
		}
	}


	@SuppressLint("HandlerLeak")
	public void getAddrLocation() 
	{
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
		public void run() 
		{
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
				addr = myApp.getaddr();
				longitude = myApp.getjd();
				latitude = myApp.getwd();
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

	private void getData(final String type,final  String id) {
		
		String httpUrl = User.mainurl + "sf/CdepUnion";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		 parameters_userInfo.put("type", type);
		 parameters_userInfo.put("cdepperson", id);
		 parameters_userInfo.put("mac", mac);
		 parameters_userInfo.put("usercode", username);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
//							Log.e("main", dataJson.toString());
							String code = dataJson.getString("code");
							if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								if("0".equals(type))
								{
									companyList.clear();
									compantIds.clear();
									if(arrayType.length()>1){
										companyList.add("请选择");
									}
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										companyList.add(obj.getString("ccusname"));
										compantIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(companySpn, companyList);
								}else if("1".equals(type)){
									departList.clear();
									departIds.clear();
									departList.add("请选择");
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										departList.add(obj.getString("ccusname"));
										departIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(departSpn, departList);
								}else if("2".equals(type)){
									areaList.clear();
									areaIds.clear();
									areaList.add("请选择");
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										areaList.add(obj.getString("ccusname"));
										areaIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(areaSpn, areaList);
								}else if("3".equals(type)){
									shopList.clear();
									shopIds.clear();
									shopList.add("请选择");
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										shopList.add(obj.getString("ccusname"));
										shopIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(shopSpn,shopList);
								}
							} else if ("1".equals(code)) {
//								ToastUtil.toast(context, "没有数据");
								if("3".equals(type)) {
									shopId = id;
									from = "yyt";
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
		parameters_userInfo.put("cdepperson",shopId);
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("fj", from);

		client_request.post(httpUrl, parameters_userInfo,new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if("0".equals(code)){
								AlertDialog.Builder  builder = new AlertDialog.Builder(context)
								.setTitle("该点已有位置信息确定重新提交？")
								.setNegativeButton("取消", null)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										ProgressDialogUtil.showProgressDialog(context);
										upServer();
									}	
								});
								builder.show();
							}
							else if("-1".equals(code))	
							{ 
								upServer();
							}else{
								ToastUtil.toast(context,"无权限");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}finally {
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
		
		parameters_userInfo.put("addr", Escape.escape(addr));
		parameters_userInfo.put("jd", longitude);
		parameters_userInfo.put("wd", latitude);
		parameters_userInfo.put("image", uploadBuffer);
		parameters_userInfo.put("type", "1");
		parameters_userInfo.put("cdepperson",shopId);
		parameters_userInfo.put("mac", GetInfo.getIMEI(context));
		parameters_userInfo.put("fj", from);
		
		client_request.post(httpUrl, parameters_userInfo,new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if ("0".equals(code)) {
								ToastUtil.toast(context, "数据上传成功");
								finish();
							}  else {
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
	
	public void reloadSpinner(Spinner spinner,ArrayList<String> list) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(
				CollectActivity.this,
				R.layout.spinner_item,
				list);
		spinner.setAdapter(adapter);
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
}
