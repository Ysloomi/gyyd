package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.MapActivity;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApproveActivity extends BaseActivity {

	private TextView nameTxt, outTimeTxt, yesterTxt, summaryTxt, planTxt, timeTxt;
	private EditText adviseEdt;
	private String  advise, id, time;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.approve_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_mileage:
			Intent intent = new Intent(ApproveActivity.this,
					MapActivity.class);
			intent.putExtra("id", id);
			intent.putExtra("department", "");
			intent.putExtra("person", "");
			intent.putExtra("itype", "run");
			intent.putExtra("itime", "");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve);

		context = ApproveActivity.this;

		nameTxt = (TextView) findViewById(R.id.approve_person);
		outTimeTxt = (TextView) findViewById(R.id.approve_outtime);
		yesterTxt = (TextView) findViewById(R.id.approve_yester);
		summaryTxt = (TextView) findViewById(R.id.approve_summary);
		planTxt = (TextView) findViewById(R.id.approve_plan);
		adviseEdt = (EditText) findViewById(R.id.approve_advise);
		timeTxt = (TextView) findViewById(R.id.approve_time);

		id = getIntent().getStringExtra("id");

		getData();

		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				advise = adviseEdt.getText().toString();
				time = timeTxt.getText().toString();
				if (TextUtils.isEmpty(advise.trim())) {
					ToastUtil.toast(context,"请先填写审批意见");
				} else {
					if (TextUtils.isEmpty(time.trim())) {
						ProgressDialogUtil.showProgressDialog(context);
						saveData();
//						if (GetInfo.getIfSf(context))
//							saveDy();
					} else {
						ToastUtil.toast(context,"已审批，请勿重复提交");
					}
				}
			}
		});
	}

	private void getData() {
		String httpUrl = User.mainurl + "sf/check";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac",mac);
		parameters_userInfo.put("usercode",username);
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
									nameTxt.setText(obj.getString("username"));
									outTimeTxt.setText(obj.getString("cmakertime"));
									yesterTxt.setText(obj.getString("ytomplan"));
									summaryTxt.setText(obj.getString("todsummary"));
									planTxt.setText(obj.getString("tomplan"));
									adviseEdt.setText(obj.getString("veropinion"));
									timeTxt.setText(obj.getString("checktime"));
								}
							} else {
								ToastUtil.toast(context,"加载失败，请重试");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void saveData() {

		String httpUrl = User.mainurl + "sf/check_save";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("id", id);
		parameters_userInfo.put("yj", Escape.escape(advise));
		parameters_userInfo.put("sf", ifSf);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								Toast.makeText(ApproveActivity.this,
										"工作审批数据上传成功", Toast.LENGTH_SHORT)
										.show();
								String time = DateUtil.getDate();
								planTxt.setText(time);
								finish();
							} else {
								Toast.makeText(ApproveActivity.this, "请重新上传",
										Toast.LENGTH_SHORT).show();
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
//		String httpUrl = User.dyMainurl + "sf/check_save";
//
//		AsyncHttpClient client_request = new AsyncHttpClient();
//		RequestParams parameters_userInfo = new RequestParams();
//
//		parameters_userInfo.put("mac", mac);
//		parameters_userInfo.put("usercode", username);
//		parameters_userInfo.put("id", id);
//		parameters_userInfo.put("yj", Escape.escape(advise));
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
//
//							} else {
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

}