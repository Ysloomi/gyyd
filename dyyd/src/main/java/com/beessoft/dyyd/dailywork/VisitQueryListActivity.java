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

public class VisitQueryListActivity extends BaseActivity {
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
		setContentView(R.layout.visitquerylist);

		button = (Button) findViewById(R.id.visitquery_button);
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.visitquery_text);
		listView = (ListView) findViewById(R.id.visitquery_list);

		mac = GetInfo.getIMEI(VisitQueryListActivity.this);
		GetJSON.visitServer_GetInfo_NoSpecial(VisitQueryListActivity.this, autoCompleteTextView, mac);
		autoCompleteTextView.setHint("专业、姓名、分局");
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(VisitQueryListActivity.this,
				"载入中...", "请等待...", true, false);
		// visitServer_GetInfo(VisitQueryListActivity.this);
		String level = "[全部人员]";
		visitServer(VisitQueryListActivity.this, level);

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
						VisitQueryListActivity.this, "载入中...", "请等待...", true,
						false);
				String level = autoCompleteTextView.getText().toString();

				visitServer(VisitQueryListActivity.this, level);
				
				Tools.closeInput(VisitQueryListActivity.this, autoCompleteTextView);
			}
		});
	}

	// 访问服务器http post
	private void visitServer(Context context, String level) {
		String httpUrl = User.mainurl + "sf/visitlist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("psn", Escape.escape(level));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							// System.out.println("dataJson" + dataJson);
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(VisitQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idate", obj.getString("idate"));
									map.put("name", obj.getString("username"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										VisitQueryListActivity.this,
										datas,// 数据源
										R.layout.visitquerylist_item,// 显示布局
										new String[] { "idate", "name" },
										new int[] { R.id.date, R.id.person });
								// simAdapter.setViewBinder(new MyViewBinder());
								listView.setAdapter(simAdapter);
								// 添加点击
								listView.setOnItemClickListener(new OnItemClickListener() {
									@SuppressWarnings("unchecked")
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// startActivity(new
										// Intent(VisitQueryListActivity.this,VisitQueryActivity.class));

										ListView listView = (ListView) parent;
										HashMap<String, String> map = (HashMap<String, String>) listView
												.getItemAtPosition(position);
										String idate = map.get("idate");
										String name = map.get("name");
										// // 清空列表
										// cleanlist();
										// visitServer_Detail(
										// VisitQueryListActivity.this,
										// idate, name);
										Intent intent = new Intent(
												VisitQueryListActivity.this,
												VisitQueryListDetailActivity.class);
										intent.putExtra("idate", idate);
										intent.putExtra("name", name);
										startActivity(intent);
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
			// System.out.println(size);
			datas.removeAll(datas);
			simAdapter.notifyDataSetChanged();
			listView.setAdapter(simAdapter);
		}
	}
}
