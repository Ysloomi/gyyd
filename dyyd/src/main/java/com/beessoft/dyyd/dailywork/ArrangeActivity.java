package com.beessoft.dyyd.dailywork;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.model.GetJSON;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ArrangeActivity extends BaseActivity {

	private Button button1;
	private EditText editText;
	private String mac, person, message;

	private ProgressDialog progressDialog;

//	private Spinner spinner;

	private AutoCompleteTextView autoCompleteTextView;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.arrange_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_arrange:
			Intent intent = new Intent(ArrangeActivity.this,
					ArrangeQueryListActivity.class);
			intent.putExtra("itype", "1");// 标示为安排人查看自己的安排
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arrange);

		initView();

		mac = GetInfo.getIMEI(ArrangeActivity.this);
		
		GetJSON.visitServer_GetInfo(ArrangeActivity.this, autoCompleteTextView, mac);
		
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// person = spinner.getSelectedItem().toString();
				person = autoCompleteTextView.getText().toString();
				message = editText.getText().toString();

				if ("[全部人员]".equals(person)) {
					Toast.makeText(ArrangeActivity.this, "请选择人员",
							Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(message.trim())) {
					Toast.makeText(ArrangeActivity.this, "数据不能为空",
							Toast.LENGTH_SHORT).show();
				} else {

					// 显示ProgressDialog
					progressDialog = ProgressDialog.show(ArrangeActivity.this,
							"载入中...", "请等待...", true, false);

					visitServer(ArrangeActivity.this);
				}
			}
		});
		autoCompleteTextView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompleteTextView.showDropDown();// 显示下拉列表
				return false;
			}
		});
	}

	public void initView() {
		button1 = (Button) findViewById(R.id.arrange_confirm);
		editText = (EditText) findViewById(R.id.arrange_text);

//		button2 = (Button) findViewById(R.id.arrange_button);
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.arrange_auto_text);

	}

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/upwork_up";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("workuser", Escape.escape(person));
		parameters_userInfo.put("uptxt", Escape.escape(message));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								Toast.makeText(ArrangeActivity.this,
										"安排工作数据上传成功", Toast.LENGTH_SHORT)
										.show();

								finish();
							} else if (dataJson.getString("code").equals("1")) {
								Toast.makeText(ArrangeActivity.this, "上传失败",
										Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("-2")) {
								Toast.makeText(ArrangeActivity.this, "无权限",
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