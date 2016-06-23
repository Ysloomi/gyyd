package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.QueryMapActivity;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class CheckApproveActivity extends BaseActivity implements View.OnClickListener {
	private Button agreeBtn, refuseBtn, queryBtn;
	private TextView personTxt, timeTxt, locationTxt, contextTxt, classTxt,
			explainTxt, journeyTxt, approvemanTxt, approvetimeTxt, approveresultTxt;
	private String id, btn, photo, result, jd, wd,unagree_reason;
	private ImageView photoImg;
	private ProgressBar progressBar;
	private Bitmap bitmap;
	private Thread mThread;

	private LinearLayout approveManLl;
	private LinearLayout approveTimeLl;
	private LinearLayout approveResultLl;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkapprove);

		context = CheckApproveActivity.this;
		initView();

		id =  getIntent().getStringExtra("idTarget");

		if ("query".equals(getIntent().getStringExtra("query"))) {
			agreeBtn.setVisibility(View.GONE);
			refuseBtn.setVisibility(View.GONE);
			queryBtn.setVisibility(View.VISIBLE);
			setTitle("考勤记录");
		} else {
			approveManLl.setVisibility(View.GONE);
			approveTimeLl.setVisibility(View.GONE);
			approveResultLl.setVisibility(View.GONE);
		}

		getData();
	}

	public void initView() {


		approveManLl = (LinearLayout) findViewById(R.id.ll_approveman) ;
		approveTimeLl = (LinearLayout) findViewById(R.id.ll_approve_time) ;
		approveResultLl = (LinearLayout) findViewById(R.id.ll_approve_result) ;

		personTxt = (TextView) findViewById(R.id.person_text);
		timeTxt = (TextView) findViewById(R.id.time_text);
		locationTxt = (TextView) findViewById(R.id.location_text);
		contextTxt = (TextView) findViewById(R.id.context_text);
		classTxt = (TextView) findViewById(R.id.class_text);
		explainTxt = (TextView) findViewById(R.id.explain_text);
		journeyTxt = (TextView) findViewById(R.id.journey_type);
		approvemanTxt = (TextView) findViewById(R.id.approveman_text);
		approvetimeTxt = (TextView) findViewById(R.id.approvetime_text);
		approveresultTxt = (TextView) findViewById(R.id.approveresult_text);

		agreeBtn = (Button) findViewById(R.id.agree_button);
		refuseBtn = (Button) findViewById(R.id.refuse_button);
		queryBtn = (Button) findViewById(R.id.query_map);

		photoImg = (ImageView) findViewById(R.id.checkin_image);
		progressBar = (ProgressBar) findViewById(R.id.photo_progressbar);

		agreeBtn.setOnClickListener(this);
		refuseBtn.setOnClickListener(this);
		queryBtn.setOnClickListener(this);
		photoImg.setOnClickListener(this);
		findViewById(R.id.txt_download).setOnClickListener(this);
	}
	
	@SuppressLint("InflateParams")
	private void inputExamineDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_unagree, null);

		final EditText editText1 = (EditText) view
				.findViewById(R.id.reason_text);

		final AlertDialog myDialog = new AlertDialog.Builder(
				CheckApproveActivity.this).setView(view)
				.setPositiveButton("确认", null).setNegativeButton("取消", null)
				.setCancelable(false).create();

		myDialog.setTitle("不同意的原因");
		myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button button = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						unagree_reason = editText1.getText().toString();
						if (TextUtils.isEmpty(unagree_reason.trim())) {
							ToastUtil.toast(CheckApproveActivity.this, "请填写不同意原因");
						} else {
							ProgressDialogUtil.showProgressDialog(context);
							saveData();
//							if (GetInfo.getIfSf(context))
//								saveDy();
							myDialog.dismiss();
						}
					}
				});
				Button button1 = myDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
				button1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						myDialog.dismiss();
					}
				});
			}
		});
		myDialog.show();
	}

	private void getData() {
		String httpUrl = User.mainurl + "sf/startwork_check2";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("id", id);
		parameters_userInfo.put("sf", ifSf);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									personTxt.setText(obj.getString("username"));
									timeTxt.setText(obj.getString("iday"));
									locationTxt.setText(obj.getString("iadd"));
									contextTxt.setText(obj.getString("bin"));
									classTxt.setText(obj.getString("iclass"));
									explainTxt.setText(obj.getString("cmemo"));
									journeyTxt.setText(obj.getString("type"));
									approvemanTxt.setText(obj.getString("checker"));
									approvetimeTxt.setText(obj.getString("checktime"));
									approveresultTxt.setText(obj.getString("checkval"));
									jd = obj.getString("lng");
									wd = obj.getString("lat");
									photo = User.mainurl + obj.getString("photo");
								}
							} else {
								Toast.makeText(CheckApproveActivity.this,
										"请重试", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void saveData() {

		String httpUrl = User.mainurl + "sf/startwork_checksave";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("id", id);
		parameters_userInfo.put("btn", btn);
		parameters_userInfo.put("unagree_reason", unagree_reason);
		parameters_userInfo.put("sf", ifSf);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								ToastUtil.toast(context,"签到审批数据上传成功");
								finish();
							} else {
								ToastUtil.toast(context,"请重新上传");
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


//	private void saveDy() {
//
//		String httpUrl = User.dyMainurl + "sf/startwork_checksave";
//
//		AsyncHttpClient client_request = new AsyncHttpClient();
//		RequestParams parameters_userInfo = new RequestParams();
//
//		parameters_userInfo.put("mac", mac);
//		parameters_userInfo.put("usercode", username);
//		parameters_userInfo.put("id", id);
//		parameters_userInfo.put("btn", btn);
//		parameters_userInfo.put("unagree_reason", unagree_reason);
//		parameters_userInfo.put("sf", ifSf);
//
//		client_request.post(httpUrl, parameters_userInfo,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(String response) {
//						try {
//							JSONObject dataJson = new JSONObject(response);
//							int code = dataJson.getInt("code");
//							if (code==0) {
//								ToastUtil.toast(context,"签到审批数据上传成功");
//								finish();
//							} else {
//								ToastUtil.toast(context,"请重新上传");
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


	Runnable runnable = new Runnable() {
		@Override
		public void run() {// run()在新的线程中运行
			try {
				Tools tools = new Tools();
				bitmap = tools.getBitMap(photo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(0);
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			photoImg.setImageBitmap(bitmap);
			if (photoImg != null) {
				progressBar.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.txt_download:
				progressBar.setVisibility(View.VISIBLE);
				mThread = new Thread(runnable);
				mThread.start();
				break;
			case R.id.agree_button:
				result = approveresultTxt.getText().toString();
				if (TextUtils.isEmpty(result)) {
					btn = "0";//同意
					ProgressDialogUtil.showProgressDialog(context);
					saveData();
//					if (GetInfo.getIfSf(context))
//						saveDy();
				} else {
					ToastUtil.toast(context,"已审批，请勿重复提交");
				}
				break;
			case R.id.refuse_button:
				result = approveresultTxt.getText().toString();
				if ("".equals(result)) {
					btn = "1";//不同意
					inputExamineDialog();
				} else {
					ToastUtil.toast(context,"已审批，请勿重复提交");
				}

				queryBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

					}
				});
				break;
			case R.id.query_map:
				String username = personTxt.getText().toString();
				if (!TextUtils.isEmpty(username.trim())) {
					Intent intent = new Intent(CheckApproveActivity.this,
							QueryMapActivity.class);
					intent.putExtra("jd", jd);
					intent.putExtra("wd", wd);
					intent.putExtra("username", username);
					startActivity(intent);
				}
				break;
			case R.id.checkin_image:
				PhotoHelper.openPictureDialog_down(CheckApproveActivity.this, bitmap);
				break;
		}
	}
}