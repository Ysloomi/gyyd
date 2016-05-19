package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.model.GetJSON;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkQueryListActivity extends BaseActivity {

	private Button button;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;
	// private Spinner spinner;
	private AutoCompleteTextView autoCompleteTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workquerylist);

		context = WorkQueryListActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);


		initView();

		GetJSON.visitServer_GetInfo_NoSpecial(context, autoCompleteTextView, mac,username);
		autoCompleteTextView.setHint("专业、姓名、分局、日期");

		ProgressDialogUtil.showProgressDialog(context);
		String level = "[全部人员]";
		visitServer(level);
		
		autoCompleteTextView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompleteTextView.showDropDown();// 显示下拉列表
				return false;
			}
		});

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ProgressDialogUtil.showProgressDialog(context);
				String level = autoCompleteTextView.getText().toString();
				visitServer(level);
				Tools.closeInput(WorkQueryListActivity.this, autoCompleteTextView);
			}
		});
	}

	public void initView() {
		listView = (ListView) findViewById(R.id.workquery_list);
		button = (Button) findViewById(R.id.workquery_button);
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.workquery_spinner);
	}

	private void visitServer(String level) {
		String httpUrl = User.mainurl + "sf/checklist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("itype", "1");// 查询人
		parameters_userInfo.put("btn", "1");
		parameters_userInfo.put("psn", Escape.escape(level));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							datas.clear();
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								Toast.makeText(WorkQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if ("2".equals(code)) {
								Toast.makeText(WorkQueryListActivity.this,
										"日期格式不对，请按照2015-01-01输入",
										Toast.LENGTH_SHORT).show();
							} else if ("0".equals(code)) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("idTarget", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("date", obj.getString("iday"));
									map.put("verifier",
											"审批人:" + obj.getString("verifier"));
									map.put("state", obj.getString("shstate"));
									map.put("readtime",
											"阅读次数:" + obj.getString("cs"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									WorkQueryListActivity.this,
									datas,// 数据源
									R.layout.item_approvelist,// 显示布局
									new String[] { "date", "name",
											"verifier", "state", "readtime" },
									new int[] { R.id.date, R.id.person,
											R.id.verifier, R.id.state,
											R.id.readtime });
							listView.setAdapter(simAdapter);
							// 添加点击
							listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@SuppressWarnings("unchecked")
								@Override
								public void onItemClick(
										AdapterView<?> parent, View view,
										int position, long id) {

									ListView listView = (ListView) parent;
									HashMap<String, String> map = (HashMap<String, String>) listView
											.getItemAtPosition(position);

									String idTarget = map.get("idTarget");
									if ("未提交".equals(map.get("state"))) {
										Toast.makeText(
												WorkQueryListActivity.this,
												"日志未提交，不能查询",
												Toast.LENGTH_SHORT).show();
									} else {
										Intent intent = new Intent(
												WorkQueryListActivity.this,
												WorkQueryActivity.class);
										intent.putExtra("idTarget",
												idTarget);
										startActivity(intent);
									}
								}
							});
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
