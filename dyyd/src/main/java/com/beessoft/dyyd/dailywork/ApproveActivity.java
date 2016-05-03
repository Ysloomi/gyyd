package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.MapActivity;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApproveActivity extends BaseActivity {
	private Button button;
	private TextView textView1, textView2, textView3, textView4, textView5, textView6;
	private EditText editText1;
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
		setContentView(R.layout.approve);

		textView1 = (TextView) findViewById(R.id.approve_person);
		textView2 = (TextView) findViewById(R.id.approve_outtime);
		textView3 = (TextView) findViewById(R.id.approve_yester);
		textView4 = (TextView) findViewById(R.id.approve_summary);
		textView5 = (TextView) findViewById(R.id.approve_plan);
		editText1 = (EditText) findViewById(R.id.approve_advise);
		textView6 = (TextView) findViewById(R.id.approve_time);
		button = (Button) findViewById(R.id.approve_confirm);

		id = getIntent().getStringExtra("idTarget");

		mac = GetInfo.getIMEI(ApproveActivity.this);
		visitServer();

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				advise = editText1.getText().toString();
				time = textView6.getText().toString();
				if (TextUtils.isEmpty(advise.trim())) {
					Toast.makeText(ApproveActivity.this, "请先填写审批意见",
							Toast.LENGTH_SHORT).show();
				} else {
					if (TextUtils.isEmpty(time.trim())) {
						ProgressDialogUtil.showProgressDialog(context);
						visitServer_comfirm();
					} else {
						Toast.makeText(ApproveActivity.this, "已审批，请勿重复提交",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/fragment_check";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac",mac);
		parameters_userInfo.put("id", id);

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
									textView1.setText(obj.getString("username"));
									textView2.setText(obj.getString("cmakertime"));
									textView3.setText(obj.getString("ytomplan"));
									textView4.setText(obj.getString("todsummary"));
									textView5.setText(obj.getString("tomplan"));
									editText1.setText(obj.getString("veropinion"));
									textView6.setText(obj.getString("checktime"));
								}
							} else {
								Toast.makeText(ApproveActivity.this, "请重新上传",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void visitServer_comfirm() {
		String httpUrl = User.mainurl + "sf/check_save";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("id", id);
		parameters_userInfo.put("yj", Escape.escape(advise));

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
								String time = GetInfo.getDate();
								textView5.setText(time);
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

}