package com.beessoft.dyyd.dailywork;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfirmActivity extends BaseActivity {
	private Button button;
	private TextView textView1, textView2, textView3, textView4, textView5,
			textView6,textView7;
	private String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);

		initView();

		id = getIntent().getStringExtra("id");

		getData();

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ProgressDialogUtil.showProgressDialog(context);
				saveData();
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

	private void getData() {

		String httpUrl = User.mainurl + "sf/check";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (0==code) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									textView1.setText(obj.getString("username"));
									textView2.setText(obj.getString("cmakertime"));
									textView3.setText(obj.getString("ytomplan"));
									textView4.setText(obj.getString("todsummary"));
									textView5.setText(obj.getString("tomplan"));
									textView6.setText(obj.getString("veropinion"));
									textView7.setText(obj.getString("checktime"));
								}
							} else {
								ToastUtil.toast(context,"没有相关信息" );
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void saveData() {

		String httpUrl = User.mainurl + "sf/check_confirmsave";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								ToastUtil.toast(context, "确认成功,有效里程"+dataJson.getString("mykm")+"公里已记录成功");
								finish();
							} else {
								ToastUtil.toast(context,"请重试");
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