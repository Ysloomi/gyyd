package com.beessoft.dyyd.dailywork;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.QueryMapActivity;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CheckApproveActivity extends BaseActivity {
	private Button button1, button2, button3, button4;
	private TextView textView1, textView2, textView3, textView4, textView5,
			textView6, textView7, textView8, textView9, textView10;
	private String mac, id, btn, photo, result, jd, wd,unagree_reason;
	private ProgressDialog progressDialog;
	private ImageView imageView;
	private ProgressBar progressBar;
	private Bitmap bitmap;
	private Thread mThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkapprove);

		initView();

		id =  getIntent().getStringExtra("idTarget");

		if ("query".equals(getIntent().getStringExtra("query"))) {
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
			button3.setVisibility(View.VISIBLE);
			
			// 设置标题
//			CharSequence titleLable = "考勤记录";
			setTitle("考勤记录");
			
		} else {
			textView8.setBackgroundResource(R.drawable.unedit_text_bg);
			textView9.setBackgroundResource(R.drawable.unedit_text_bg);
			textView10.setBackgroundResource(R.drawable.unedit_text_bg);
		}

		// 根据mac地址获取今日总结和明日计划
		mac = GetInfo.getIMEI(CheckApproveActivity.this);
		visitServer(CheckApproveActivity.this);

		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				result = textView10.getText().toString();
				if ("".equals(result)) {
					btn = "0";//同意
					// 显示ProgressDialog
					progressDialog = ProgressDialog.show(
							CheckApproveActivity.this, "载入中...", "请等待...",
							true, false);
					visitServer_comfirm(CheckApproveActivity.this, btn);
				} else {
					Toast.makeText(CheckApproveActivity.this, "已审批，请勿重复提交",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				result = textView10.getText().toString();
				if ("".equals(result)) {
					btn = "1";//不同意
					inputExamineDialog();
				} else {
					Toast.makeText(CheckApproveActivity.this, "已审批，请勿重复提交",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String username = textView1.getText().toString();
				if (!TextUtils.isEmpty(username.trim())) {
					Intent intent = new Intent(CheckApproveActivity.this,
							QueryMapActivity.class);
					intent.putExtra("jd", jd);
					intent.putExtra("wd", wd);
					intent.putExtra("username", username);
					startActivity(intent);
				}
			}
		});
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				mThread = new Thread(runnable);
				mThread.start();
			}
		});
		imageView.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoHelper.openPictureDialog_down(CheckApproveActivity.this,
						bitmap);
			}
		});
	}

	public void initView() {
		textView1 = (TextView) findViewById(R.id.person_text);
		textView2 = (TextView) findViewById(R.id.time_text);
		textView3 = (TextView) findViewById(R.id.location_text);
		textView4 = (TextView) findViewById(R.id.context_text);
		textView5 = (TextView) findViewById(R.id.class_text);
		textView6 = (TextView) findViewById(R.id.explain_text);
		textView7 = (TextView) findViewById(R.id.journey_type);
		textView8 = (TextView) findViewById(R.id.approveman_text);
		textView9 = (TextView) findViewById(R.id.approvetime_text);
		textView10 = (TextView) findViewById(R.id.approveresult_text);

		button1 = (Button) findViewById(R.id.agree_button);
		button2 = (Button) findViewById(R.id.refuse_button);
		button3 = (Button) findViewById(R.id.query_map);
		button4 = (Button) findViewById(R.id.download_image);
		imageView = (ImageView) findViewById(R.id.checkin_image);

		progressBar = (ProgressBar) findViewById(R.id.photo_progressbar);
	}
	
	@SuppressLint("InflateParams")
	private void inputExamineDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.unagree, null);

		final EditText editText1 = (EditText) view
				.findViewById(R.id.reason_text);

		final AlertDialog myDialog = new AlertDialog.Builder(
				CheckApproveActivity.this).setView(view)
				.setPositiveButton("确认", null).setNegativeButton("取消", null)
				.setCancelable(false).create();

		myDialog.setTitle("请输入检查结果");
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
							// 显示ProgressDialog
							progressDialog = ProgressDialog.show(
									CheckApproveActivity.this, "载入中...", "请等待...",
									true, false);
							visitServer_comfirm(CheckApproveActivity.this, btn);
							myDialog.dismiss();
						}
					}
				});
				Button button1 = myDialog
						.getButton(AlertDialog.BUTTON_NEGATIVE);
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

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/startwork_check2";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {

								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									textView1.setText(new String(obj
											.getString("username")));
									textView2.setText(new String(obj
											.getString("iday")));
									textView3.setText(new String(obj
											.getString("iadd")));
									textView4.setText(new String(obj
											.getString("bin")));
									textView5.setText(new String(obj
											.getString("iclass")));
									textView6.setText(new String(obj
											.getString("cmemo")));
									textView7.setText(new String(obj
											.getString("type")));
									textView8.setText(new String(obj
											.getString("checker")));
									textView9.setText(new String(obj
											.getString("checktime")));
									textView10.setText(new String(obj
											.getString("checkval")));
									jd = obj.getString("lng");
									wd = obj.getString("lat");
									photo = User.mainurl
											+ obj.getString("photo");
								}
							} else {
								Toast.makeText(CheckApproveActivity.this,
										"请重新上传", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	// 访问服务器http post
	private void visitServer_comfirm(Context context, String btn) {
		String httpUrl = User.mainurl + "sf/startwork_checksave";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("id", id);
		parameters_userInfo.put("btn", btn);
		parameters_userInfo.put("unagree_reason", unagree_reason);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
//						 System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("0")) {
								Toast.makeText(CheckApproveActivity.this,
										"签到审批数据上传成功", Toast.LENGTH_SHORT)
										.show();
								finish();
							} else {
								Toast.makeText(CheckApproveActivity.this,
										"请重新上传", Toast.LENGTH_SHORT).show();
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
	/**
	 * 打开线程打开图片
	 */
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
			imageView.setImageBitmap(bitmap);
			if (imageView != null) {
				progressBar.setVisibility(View.GONE);
			}
		}
	};
}