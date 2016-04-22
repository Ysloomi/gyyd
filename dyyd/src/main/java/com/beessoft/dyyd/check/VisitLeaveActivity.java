package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

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
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class VisitLeaveActivity extends BaseActivity {

	private LocationClient mLocationClient;
	private Button button1, button2, button3, button4;
	private TextView customerText, personText, aimText, addrText, textView5,
			textView6, reachLocationText;
	private EditText resultEdit;
	private String mac, customer, person, aim, location, result, type;
	public static final int PHOTO_CODE = 5;
	// 创建Bitmap对象
	private Bitmap bitmap;
	private String uploadBuffer = null;
	private String startid;
	private ImageView photoImage;
	private String imgPath = "";
	private String customerCode = "";

	private ProgressDialog progressDialog;
	private String longitude, latitude, addr;

	private Thread mThread;

	private static final int MSG_SUCCESS = 0;// 获取定位成功的标识
	private static final int MSG_FAILURE = 1;// 获取定位失败的标识

	private DistanceDatabaseHelper distanceHelper; // 数据库帮助类

	private boolean ifLocation = false;

	private Context context;

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
//
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
//			intent.setClass(VisitLeaveActivity.this, WorkBookActivity.class);
//			startActivity(intent);
//			return true;
//		case R.id.action_target:
//			intent.setClass(VisitLeaveActivity.this, BranchTargetActivity.class);
//			startActivity(intent);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visitleave);
		if(savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("imgPath"))){
//			Log.i(TAG, "拍摄异常，获取原来的shot_path");
			Logger.e("拍摄异常，获取原来的shot_path");
			imgPath = savedInstanceState.getString("imgPath");
		}
		context = VisitLeaveActivity.this;
		mac = GetInfo.getIMEI(context);

		initView();

		// 获取已拜访客户信息
		visitServe_GetInfo();

		textView5.setText("到达时间:"+ PreferenceUtil.readString(context,"reachTime"));

		getAddrLocation();

		if (Gps.exist(VisitLeaveActivity.this, "distance.db")) {
			Gps.GPS_do(mLocationClient, 8000);
		}
		String a = PreferenceUtil.readString(context,"result");
		if (!"".equals(a)) {
			resultEdit.setText(a);
		}

		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				result = resultEdit.getText().toString();
				PreferenceUtil.write(context, "result", result);
				ToastUtil.toast(context,"保存成功");
			}
		});

		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getAddrLocation();
			}
		});

		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				customer = customerText.getText().toString();
				person = personText.getText().toString();
				aim = aimText.getText().toString();
				result = resultEdit.getText().toString();
				location = addrText.getText().toString();
				if (uploadBuffer == null) {
					ToastUtil.toast(context, "请先照相再上传");
				} else if (TextUtils.isEmpty(result.trim())
						|| TextUtils.isEmpty(location.trim())) {
					ToastUtil.toast(context,"数据不能为空");
				} else {
					// 显示ProgressDialog
					progressDialog = ProgressDialog.show(context, "载入中...", "请等待...", true,
							true);
					visitServer(customer, person,
							aim, result, longitude, latitude, location);
				}
			}
		});

		button4.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				if (Tools.isSDCardExit()) {
					imgPath = Tools.getSDPath() + "/dyyd/photo.jpg";
					takePhoto();
				} else {
					ToastUtil.toast(context, "内存卡不存在不能拍照");
				}
			}
		});

		photoImage.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Tools.isEmpty(imgPath)) {
					PhotoHelper.openPictureDialog(context, imgPath);
				}
			}
		});
	}

	public void initView() {
		// 声明百度定位sdk的构造函数
		mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
		customerText = (TextView) findViewById(R.id.visitleave_customer);
		personText = (TextView) findViewById(R.id.visitleave_person);
		aimText = (TextView) findViewById(R.id.visitleave_aim);
		addrText = (TextView) findViewById(R.id.location_text);
		textView5 = (TextView) findViewById(R.id.reachtime_text);
		textView6 = (TextView) findViewById(R.id.leavetime_text);
		reachLocationText = (TextView) findViewById(R.id.reachlocation_text);

		resultEdit = (EditText) findViewById(R.id.visitleave_result);

		button1 = (Button) findViewById(R.id.preserve_button);
		button2 = (Button) findViewById(R.id.visitleave_location);
		button3 = (Button) findViewById(R.id.visitleave_confirm);
		button4 = (Button) findViewById(R.id.photo_button);

		photoImage = (ImageView) findViewById(R.id.visitleave_image);
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

			visitServer_getaddr(longitude, latitude);

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
					visitServer_getaddr(longitude, latitude);
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
					mHandler.obtainMessage(MSG_FAILURE).sendToTarget();// 获取图片失败
				} else {
					mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
				}
			}
		}
	};

	private void visitServe_GetInfo() {
		String httpUrl = User.mainurl + "sf/offvisit";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							int code = dataJson.getInt("code");
							if (code == 0) {
								JSONArray array = dataJson.getJSONArray("list");
								JSONObject obj = array.getJSONObject(0);
								customerText.setText(obj.getString("ccusname"));
								personText.setText(obj.getString("visitperson"));
								aimText.setText(obj.getString("visitgoal"));
								reachLocationText.setText(obj.getString("siadd"));
								customerCode = obj.getString("ccuscode");
//								//将光标移到最后
//								String text1= obj.getString("checkresult");
//								resultEdit.setText(text1 + "\n");
//								String text2= text1+"\n";
//								int textLength = text2.length();
//								resultEdit.setSelection(textLength, textLength);

								startid = obj.getString("startid");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void visitServer(String customer,String person, String aim, String result, String longitude,
			String latitude, String location) {
		String httpUrl = User.mainurl + "sf/offvisit_save";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
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

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							int code = dataJson.getInt("code");
							if (code==0) {
								Toast.makeText(VisitLeaveActivity.this,
										"离开现场数据上报成功", Toast.LENGTH_SHORT)
										.show();
								// 向handler发消息
								handler.sendEmptyMessage(1);
								PreferenceUtil.write(context,"result","");
								finish();

							} else if (code==1) {
								ToastUtil.toast(context,"已提交，请勿重复提交");
								// 向handler发消息
								handler.sendEmptyMessage(0);
							} else {
								ToastUtil.toast(context, "请重新提交");
								// 向handler发消息
								handler.sendEmptyMessage(0);
							}

						} catch (Exception e) {
							e.printStackTrace();
							// 向handler发消息
							handler.sendEmptyMessage(0);
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						progressDialog.dismiss();
					}
				});
	}

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
						imgPath="";
					}
					break;
				default:
					break;
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				textView6.setText(new String("离开时间:" + GetInfo.getDate()));
				progressDialog.dismiss();
				break;
			case 0:
				progressDialog.dismiss();
				break;
			}

		}
	};

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