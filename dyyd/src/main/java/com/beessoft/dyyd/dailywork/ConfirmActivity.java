package com.beessoft.dyyd.dailywork;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ConfirmActivity extends BaseActivity {
	private Button button;
	private TextView textView1, textView2, textView3, textView4, textView5,
			textView6,textView7;
	private String mac, id;
	private ProgressDialog progressDialog;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);
		initView();

		id = getIntent().getStringExtra("idTarget");

		mac = GetInfo.getIMEI(ConfirmActivity.this);
		visitServer(ConfirmActivity.this);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 开启ProgressDialog
				progressDialog = ProgressDialog.show(ConfirmActivity.this,
						"载入中...", "请等待...", true, false);
				visitServer_comfirm(ConfirmActivity.this);
			}
		});
	}

	public void initView() {
		textView1 = (TextView) findViewById(R.id.confirm_person);
		textView2 = (TextView) findViewById(R.id.confirm_outtime);
		textView3 = (TextView) findViewById(R.id.confirm_yester);
		textView4 = (TextView) findViewById(R.id.confirm_summary);
		textView5 = (TextView) findViewById(R.id.confirm_plan);
		textView6 = (TextView) findViewById(R.id.confirm_advise);
		textView7 = (TextView) findViewById(R.id.confirm_time);

		button = (Button) findViewById(R.id.confirm_confirm);
	}

	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/fragment_check";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if ("0".equals(dataJson.getString("code"))) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									textView1.setText(new String(obj
											.getString("username")));
									textView2.setText(new String(obj
											.getString("cmakertime")));
									textView3.setText(new String(obj
											.getString("ytomplan")));
									textView4.setText(new String(obj
											.getString("todsummary")));
									textView5.setText(new String(obj
											.getString("tomplan")));
									textView6.setText(new String(obj
											.getString("veropinion")));
									textView7.setText(new String(obj
											.getString("checktime")));
								}
							} else if ("1".equals(dataJson.getString("code"))) {
								Toast.makeText(ConfirmActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void visitServer_comfirm(Context context) {
		String httpUrl = User.mainurl + "sf/check_confirmsave";
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

						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("0")) {
								Toast.makeText(ConfirmActivity.this,
										"确认成功,有效里程"+dataJson.getString("mykm")+"公里已记录成功", Toast.LENGTH_SHORT)
										.show();
								finish();
							} else {
								Toast.makeText(ConfirmActivity.this, "请重新上传",
										Toast.LENGTH_SHORT).show();
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

}