package com.beessoft.dyyd.dailywork;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ArrangeQueryActivity extends BaseActivity {
	private Button button1, button2, button3, button4;
	private TextView textView1, textView2, textView3, textView4, textView5,
			textView6;
	private EditText editText1, editText2;
	private String mac, advise, idTarget, state, itype, pass, btn, judge = "",
			iflag, appeal = "";
	private ProgressDialog progressDialog;
	private Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arrangequery);

		initView();

		itype = getIntent().getStringExtra("itype");// 0为上级安排，1为安排查询
		idTarget = getIntent().getStringExtra("idTarget");
		iflag = getIntent().getStringExtra("iflag");// 0为不能执行，1为可操作
		mac = GetInfo.getIMEI(ArrangeQueryActivity.this);
		pass = GetInfo.getPass(this);

		if ("1".equals(itype)) {
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
			button3.setVisibility(View.GONE);
			button4.setVisibility(View.GONE);

			editText1.setKeyListener(null);
			editText2.setKeyListener(null);
			spinner.setEnabled(false);
			// relativeLayout.setVisibility(View.GONE);
		} else {
			// CharSequence myTitle = "上级安排工作";
			setTitle("上级安排工作");
		}

		/**
		 * 填充spinner
		 */
		String[] judgeList = new String[] { "评价选择", "满意", "一般", "不满意" };
		ArrayAdapter<String> adapter = new ArrayAdapter<>(
				ArrangeQueryActivity.this, R.layout.spinner_item, judgeList);
		spinner.setAdapter(adapter);

		visitServer(ArrangeQueryActivity.this);

		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				advise = editText1.getText().toString();
				btn = "0";// 执行工作

				if (TextUtils.isEmpty(advise.trim())) {
					Toast.makeText(ArrangeQueryActivity.this, "请填写完成结果",
							Toast.LENGTH_SHORT).show();
				} else {
					// 开启ProgressDialog
					progressDialog = ProgressDialog.show(
							ArrangeQueryActivity.this, "载入中...", "请等待...",
							true, false);
					visitServer_comfirm(ArrangeQueryActivity.this);
				}
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				judge = spinner.getSelectedItem().toString();
				advise = editText1.getText().toString();
				// System.out.println("judge:" + judge);
				btn = "1";// 评价
				if ("评价选择".equals(judge)) {
					Toast.makeText(ArrangeQueryActivity.this, "请选择评价",
							Toast.LENGTH_SHORT).show();
				} else {
					// 开启ProgressDialog
					progressDialog = ProgressDialog.show(
							ArrangeQueryActivity.this, "载入中...", "请等待...",
							true, false);
					visitServer_comfirm(ArrangeQueryActivity.this);
				}
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				advise = editText1.getText().toString();
				judge = spinner.getSelectedItem().toString();
				appeal = editText2.getText().toString();
				btn = "2";// 申诉
				if (TextUtils.isEmpty(appeal.trim())) {
					Toast.makeText(ArrangeQueryActivity.this, "请填写申诉理由",
							Toast.LENGTH_SHORT).show();
				} else {
					// 开启ProgressDialog
					progressDialog = ProgressDialog.show(
							ArrangeQueryActivity.this, "载入中...", "请等待...",
							true, false);
					visitServer_comfirm(ArrangeQueryActivity.this);
				}
			}
		});
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				advise = editText1.getText().toString();
				judge = spinner.getSelectedItem().toString();
				btn = "3";// 确认

				// 开启ProgressDialog
				progressDialog = ProgressDialog.show(ArrangeQueryActivity.this,
						"载入中...", "请等待...", true, false);
				visitServer_comfirm(ArrangeQueryActivity.this);
			}
		});

	}

	public void initView() {
		textView1 = (TextView) findViewById(R.id.date_text);
		textView2 = (TextView) findViewById(R.id.boss_text);
		textView3 = (TextView) findViewById(R.id.arrange_text);
		textView4 = (TextView) findViewById(R.id.person_text);
		textView5 = (TextView) findViewById(R.id.time_text);
		textView6 = (TextView) findViewById(R.id.state_text);

		editText1 = (EditText) findViewById(R.id.result_text);
		editText2 = (EditText) findViewById(R.id.appeal_text);

		button1 = (Button) findViewById(R.id.arrange_confirm);
		button2 = (Button) findViewById(R.id.judge_button);
		button3 = (Button) findViewById(R.id.appeal_button);
		button4 = (Button) findViewById(R.id.confirm_button);

		spinner = (Spinner) findViewById(R.id.judge_spinner);
	}

	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/upwork_show";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", idTarget);

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
											.getString("uptime")));
									textView2.setText(new String(obj
											.getString("upuser")));
									textView3.setText(new String(obj
											.getString("uptxt")));
									textView4.setText(new String(obj
											.getString("workuser")));
									textView5.setText(new String(obj
											.getString("worktime")));
									editText1.setText(new String(obj
											.getString("worktxt")));
									textView6.setText(new String(obj
											.getString("state")));
									String getJudge = obj.getString("pj");

									if (!"".equals(getJudge)) {

										Tools.setSpinnerItemSelectedByValue(
												spinner, getJudge);// 显示评价

									}

									editText2.setText(obj.getString("ss"));

								}

								state = textView6.getText().toString();

								// System.out.println("state:" + state);

								if ("已确认".equals(state)) {
									button1.setVisibility(View.GONE);
									button2.setVisibility(View.GONE);
									button3.setVisibility(View.GONE);
									button4.setVisibility(View.GONE);
									editText1.setKeyListener(null);
									editText2.setKeyListener(null);
									spinner.setEnabled(false);
									// relativeLayout.setVisibility(View.GONE);
								} else if ("上级安排".equals(state)) {
									textView5
											.setBackgroundResource(R.drawable.unedit_text_bg);

									if ("1".equals(itype)) {
										editText1
												.setBackgroundResource(R.drawable.unedit_text_bg);
									}
									spinerNoSelected();
									editText2.setKeyListener(null);
									editText2
											.setBackgroundResource(R.drawable.unedit_text_bg);

									button2.setVisibility(View.GONE);
									button3.setVisibility(View.GONE);
									button4.setVisibility(View.GONE);
								} else if ("待评价".equals(state)) {
									editText1.setKeyListener(null);
									editText2.setKeyListener(null);
									// editText2
									// .setBackgroundResource(R.drawable.unedit_text_bg);

									button1.setVisibility(View.GONE);
									button3.setVisibility(View.GONE);
									button4.setVisibility(View.GONE);
									if ("0".equals(iflag)) {
										button2.setVisibility(View.GONE);
										spinerNoSelected();
									}
								} else if ("待确认".equals(state)) {
									editText1.setKeyListener(null);
									button1.setVisibility(View.GONE);
									button2.setVisibility(View.GONE);
									spinner.setEnabled(false);
									if (!"".equals(editText2.getText()
											.toString())) {
										button3.setVisibility(View.GONE);
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					public void spinerNoSelected() {
						spinner.setEnabled(false);
						spinner.setBackgroundResource(R.drawable.unedit_text_bg);
						ArrayAdapter<String> adapter = new ArrayAdapter<>(
								ArrangeQueryActivity.this,
								R.layout.spinner_item);
						spinner.setAdapter(adapter);
					}
				});
	}

	private void visitServer_comfirm(Context context) {
		String httpUrl = User.mainurl + "sf/upwork_do";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", idTarget);
		// System.out.println(btn);
		parameters_userInfo.put("btn", btn);
		parameters_userInfo.put("pj", Escape.escape(judge));
		parameters_userInfo.put("ss", Escape.escape(appeal));
		parameters_userInfo.put("worktxt", Escape.escape(advise));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("0")) {
								Toast.makeText(ArrangeQueryActivity.this,
										"工作完成数据上传成功", Toast.LENGTH_SHORT)
										.show();
								finish();
							} else if (dataJson.getString("code").equals("1")) {
								Toast.makeText(ArrangeQueryActivity.this, "失败",
										Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("-2")) {
								Toast.makeText(ArrangeQueryActivity.this,
										"无权限", Toast.LENGTH_SHORT).show();
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