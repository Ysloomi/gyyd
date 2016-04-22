package com.beessoft.dyyd.dailywork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WorkQueryListActivity extends BaseActivity {
	private String mac;
	private Button button;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;
	// private Spinner spinner;
	private AutoCompleteTextView autoCompleteTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workquerylist);


		initView();

		mac = GetInfo.getIMEI(WorkQueryListActivity.this);

		GetJSON.visitServer_GetInfo_NoSpecial(WorkQueryListActivity.this,
				autoCompleteTextView, mac);
		autoCompleteTextView.setHint("专业、姓名、分局、日期");
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(WorkQueryListActivity.this,
				"载入中...", "请等待...", true, false);

		String level = "[全部人员]";
		visitServer(WorkQueryListActivity.this, mac, level);
		
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
				// 清空列表
				cleanlist();

				// 显示ProgressDialog
				progressDialog = ProgressDialog.show(
						WorkQueryListActivity.this, "载入中...", "请等待...", true,
						false);
				String level = autoCompleteTextView.getText().toString();

				visitServer(WorkQueryListActivity.this, mac, level);

				Tools.closeInput(WorkQueryListActivity.this,
						autoCompleteTextView);
			}
		});
	}

	public void initView() {
		listView = (ListView) findViewById(R.id.workquery_list);
		button = (Button) findViewById(R.id.workquery_button);
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.workquery_spinner);
	}

	// // 访问服务器http post
	// private void visitServer_GetInfo(Context context) {
	// String httpUrl = User.mainurl + "app/getpsn";
	// AsyncHttpClient client_request = new AsyncHttpClient();
	// RequestParams parameters_userInfo = new RequestParams();
	// parameters_userInfo.put("mac", mac);
	//
	// client_request.post(httpUrl, parameters_userInfo,
	// new AsyncHttpResponseHandler() {
	// @Override
	// public void onSuccess(String response) {
	// // System.out.println("response:" + response);
	// try {
	//
	// JSONObject dataJson = new JSONObject(Escape
	// .unescape(response));
	// if (dataJson.getString("code").equals("0")) {
	// JSONArray array = dataJson.getJSONArray("list");
	// // 构建list
	// List<String> list = new ArrayList<String>();
	//
	// for (int j = 0; j < array.length(); j++) {
	// JSONObject obj = array.getJSONObject(j);
	// list.add(obj.get("username").toString());
	// }
	//
	// for (int k = 0; k < array.length(); k++) {
	// JSONObject obj = array.getJSONObject(k);
	// list.add(obj.get("usercode").toString());
	// }
	// // System.out.println("list" + list);
	// String[] string = (String[]) list
	// .toArray(new String[list.size()]);
	//
	// // 现实数组在system里面需要启动Arrays.deepToString(string)
	// tscolari.mobile_sample.utils.ArrayAdapter<String> adapter = new
	// tscolari.mobile_sample.utils.ArrayAdapter<String>(
	// WorkQueryListActivity.this,
	// android.R.layout.simple_dropdown_item_1line,
	// string);
	// autoCompleteTextView.setAdapter(adapter);
	// autoCompleteTextView.setHint("专业、姓名、分局");
	//
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// progressDialog.dismiss();
	// }
	// }
	//
	// @Override
	// public void onFailure(Throwable error, String data) {
	// error.printStackTrace(System.out);
	// progressDialog.dismiss();
	// }
	// });
	// }

	// 访问服务器http post
	private void visitServer(Context context, String MacAddr, String level) {
		String httpUrl = User.mainurl + "sf/checklist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", MacAddr);
		parameters_userInfo.put("itype", "1");// 查询人
		parameters_userInfo.put("btn", "1");
		parameters_userInfo.put("psn", Escape.escape(level));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response:"+response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
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
									map.put("id", j);
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
								simAdapter = new SimpleAdapter(
										WorkQueryListActivity.this,
										datas,// 数据源
										R.layout.approvelist_item,// 显示布局
										new String[] { "date", "name",
												"verifier", "state", "readtime" },
										new int[] { R.id.date, R.id.person,
												R.id.verifier, R.id.state,
												R.id.readtime });
								listView.setAdapter(simAdapter);
								// 添加点击
								listView.setOnItemClickListener(new OnItemClickListener() {
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

	// 清除处理
	private void cleanlist() {
		int size = datas.size();
		if (size > 0) {
			datas.removeAll(datas);
			simAdapter.notifyDataSetChanged();
			listView.setAdapter(simAdapter);
		}
	}

}
