package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.Logger;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.PhotoUtil;
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
	
	private Context context;
	private TextView addrText;
	private ImageView imageView;
	private Spinner blockSpinner;
	private Spinner streetSpinner;
	private Spinner communitySpinner;
	private Spinner shopSpinner;
	
	private ArrayList<String> blockList = new ArrayList<String>();
	private ArrayList<String> blockIds = new ArrayList<String>();
	private ArrayList<String> streetList = new ArrayList<String>();
	private ArrayList<String> streetIds = new ArrayList<String>();
	private ArrayList<String> communityList = new ArrayList<String>();
	private ArrayList<String> communityIds = new ArrayList<String>();
	private ArrayList<String> shopList = new ArrayList<String>();
	private ArrayList<String> shopIds = new ArrayList<String>();
	
	private String longitude;
	private String latitude;
	private String addr;
	private String type;
	private String shopId;
	private String mId;
	private String from;
	private String mac;
	private int iflag = 0;
	//照相
	private Bitmap bitmap;
	private String imgPath;
	private String uploadBuffer = null;
	public static final int PHOTO_CODE = 5;
	
	private LocationClient mLocationClient;
	private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
	private Thread mThread;
	private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
	private static final int MSG_FAILURE = 1;// 获取定位失败的标识
	
	private ProgressDialog progressDialog;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
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
		setContentView(R.layout.activity_collect);
		if(savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))){
//			Log.i(TAG, "拍摄异常，获取原来的shot_path");
			Logger.e("拍摄异常，获取原来的shot_path");
			imgPath = savedInstanceState.getString("imgPath");
		}
		context= CollectActivity.this;
		mac = GetInfo.getIMEI(context);
		mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
		
		initView();
		// 获取定位地址
		getAddrLocation();
//		type  操作类型(0 一级  1 二级  2 三级 3 四级)
//		 name 上级名（第一级不传）
		getData("0","");
	}

	public void initView() 
	{
		addrText = (TextView) findViewById(R.id.location_text);
		
		blockSpinner = (Spinner) findViewById(R.id.block_spinner);
		blockSpinner.setOnItemSelectedListener(itemSelectedListener);
		streetSpinner = (Spinner) findViewById(R.id.street_spinner);
		streetSpinner.setOnItemSelectedListener(itemSelectedListener);
		communitySpinner = (Spinner) findViewById(R.id.community_spinner);
		communitySpinner.setOnItemSelectedListener(itemSelectedListener);
		shopSpinner = (Spinner) findViewById(R.id.shop_spinner);
		shopSpinner.setOnItemSelectedListener(itemSelectedListener);
		
		imageView = (ImageView) findViewById(R.id.photo_image);
		imageView.setOnClickListener(clickListener);
		
		findViewById(R.id.refresh_iv).setOnClickListener(clickListener);
		findViewById(R.id.photo_photo).setOnClickListener(clickListener);
		findViewById(R.id.photo_confirm).setOnClickListener(clickListener);
	}
	
	OnClickListener clickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
			case R.id.refresh_iv:
				getAddrLocation();
				break;
			case R.id.photo_photo:
				if (Tools.isSDCardExit()) 
				{
					imgPath = Tools.getSDPath() + "/dyyd/photo.jpg";
					takePhoto();
				} else {
					ToastUtil.toast(context, "内存卡不存在不能拍照");
				}
				break;
			case R.id.photo_confirm:
				addr = addrText.getText().toString();
				if (uploadBuffer == null)
				{
					ToastUtil.toast(context, "请先照相再上传");
				}
				else if (Tools.isEmpty(shopId))
				{
					ToastUtil.toast(context, "请选择到营业厅");
				}
				else if("Gps".equals(type) || "Wifi".equals(type))
				{
					progressDialog = ProgressDialog.show(CollectActivity.this,"载入中...", "请等待...", true, true);
					visitServer();
				}
				else 
				{
					ToastUtil.toast(context, "请刷新位置信息到Gps或Wifi再提交");
				}
				break;
			case R.id.photo_image:	
				if(!Tools.isEmpty(imgPath))
				{
					PhotoHelper.openPictureDialog(CollectActivity.this,imgPath);
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
			case R.id.block_spinner:
				name = blockList.get(position);
				if(!"请选择".equals(name))
				{
					String locaId = "";
					if(blockIds.size()!=1){
						locaId = blockIds.get(position - 1);
					}else{
						locaId = blockIds.get(0);
					}
					shopId = locaId;
					from = "fgs";
					getData("1", locaId);
				}else if("请选择".equals(name)||"".equals(name)){
					streetList.clear();
					reloadSpinner(streetSpinner, streetList);
					shopId= "";
				}
				break;
			case R.id.street_spinner:
				name = streetList.get(position);
				if(!"请选择".equals(name))
				{	
					String locaId = streetIds.get(position - 1);
					shopId = locaId;
					from = "fj";
					getData("2", locaId);
				}else if("请选择".equals(name)||"".equals(name)){
					communityList.clear();
					reloadSpinner(communitySpinner,communityList);
				}
				break;
			case R.id.community_spinner:
				name = communityList.get(position);
				if(!"请选择".equals(name))
				{
					String locaId = communityIds.get(position - 1);
					getData("3", locaId);
				}else if("请选择".equals(name)||"".equals(name)){
					shopList.clear();
					reloadSpinner(shopSpinner,shopList);
				}
				break;
			case R.id.shop_spinner:
				name = shopList.get(position);
				if(!"请选择".equals(name))
				{
					shopId = shopIds.get(position - 1);
					from = "yyt";
//					Log.e("main", "shopId>>>>"+shopId);
				}else if("请选择".equals(name)){
					shopId= "";
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
				if (!Tools.isEmpty(imgPath)) {
					File imageFile = new File(imgPath);
					bitmap = PhotoUtil.imageEncode(imageFile);
					imageView.setImageBitmap(bitmap);
					uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
					imgPath="";
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
			visitServer_getaddr(context, longitude, latitude);
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
					sleepcount = 6600;
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
				if (addr == null) {
					visitServer_getaddr(context, longitude, latitude);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (addr == null) {
					mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
					return;
				} else {
					// 向ui线程发送MSG_SUCCESS标识
					mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
				}
			} else {
				if (addr == null) {
					mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
					return;
				} else {
					mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
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

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
//							Log.e("main", dataJson.toString());
							String code = dataJson.getString("code");
							if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								if("0".equals(type))
								{
									blockList.clear();
									blockIds.clear();
									if(arrayType.length()>1){
										blockList.add("请选择");
									}
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										blockList.add(obj.getString("ccusname"));
										blockIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(blockSpinner,blockList);
								}else if("1".equals(type)){
									streetList.clear();
									streetIds.clear();
									streetList.add("请选择");
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										streetList.add(obj.getString("ccusname"));
										streetIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(streetSpinner,streetList);
								}else if("2".equals(type)){
									communityList.clear();
									communityIds.clear();
									communityList.add("请选择");
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										communityList.add(obj.getString("ccusname"));
										communityIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(communitySpinner,communityList);
								}else if("3".equals(type)){
									shopList.clear();
									shopIds.clear();
									shopList.add("请选择");
									for (int j = 0; j < arrayType.length(); j++) {
										JSONObject obj = arrayType.getJSONObject(j);
										shopList.add(obj.getString("ccusname"));
										shopIds.add(obj.getString("ccuscode"));
									}
									reloadSpinner(shopSpinner,shopList);
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
										progressDialog = ProgressDialog.show(CollectActivity.this,"载入中...", "请等待...", true, false);
										upServer();
									}	
								});
								builder.show();
								progressDialog.dismiss();
							}
							else if("-1".equals(code))	
							{ 
								upServer();
							}else{
								ToastUtil.toast(context,"无权限");
								progressDialog.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						progressDialog.dismiss();
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
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
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
							progressDialog.dismiss();
						}
					}
					
					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						progressDialog.dismiss();
					}
				});
	}

	public void visitServer_getaddr(Context context, String longitude,
			String latitude) {
		String httpUrl = "http://api.map.baidu.com/geocoder/v2/";

		AsyncHttpClient client_request = new AsyncHttpClient();

//		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
//		client_request.setCookieStore(myCookieStore);

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
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							JSONObject obj = dataJson.getJSONObject("result");
							addr = obj.getString("formatted_address");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
	
	public void reloadSpinner(Spinner spinner,ArrayList<String> list) {
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(
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
