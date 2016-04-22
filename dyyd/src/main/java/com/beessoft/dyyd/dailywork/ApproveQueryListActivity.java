package com.beessoft.dyyd.dailywork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApproveQueryListActivity extends BaseActivity {

	private String MacAddr;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.approvequerylist);
		
		listView = (ListView) findViewById(R.id.query_list);
		MacAddr = GetInfo.getIMEI(ApproveQueryListActivity.this);
	}

	@Override
	protected void onStart() {
		
		cleanlist();
		// / 显示ProgressDialog
		progressDialog = ProgressDialog.show(ApproveQueryListActivity.this,
				"载入中...", "请等待...", true, false);
		visitServer(ApproveQueryListActivity.this, MacAddr);
		super.onStart();
	}

	// 访问服务器http post
	private void visitServer(Context context, String MacAddr) {
		String httpUrl = User.mainurl + "sf/checklist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", MacAddr);
		parameters_userInfo.put("itype", "1");// 查询人
		parameters_userInfo.put("btn", "0");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(ApproveQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idTarget", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("date", obj.getString("iday"));
									map.put("verifier","审批人:" + obj.getString("verifier"));
									map.put("state",obj.getString("shstate"));
									map.put("readtime","阅读次数:" +obj.getString("cs"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									ApproveQueryListActivity.this,
									datas,// 数据源
									R.layout.approvelist_item,// 显示布局
									new String[] { "date", "name",
											"verifier","state","readtime" },
									new int[] {
											R.id.date, R.id.person,
											R.id.verifier ,R.id.state,R.id.readtime});
							listView.setAdapter(simAdapter);
							// 添加点击
							listView.setOnItemClickListener(new OnItemClickListener() {
								@SuppressWarnings("unchecked")
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {

									ListView listView = (ListView) parent;
									HashMap<String, String> map = (HashMap<String, String>) listView
											.getItemAtPosition(position);
									String idTarget = map.get("idTarget");
									Intent intent = new Intent(
											ApproveQueryListActivity.this,
											ApproveQueryActivity.class);
									intent.putExtra("idTarget", idTarget);
									startActivity(intent);
								}
							});
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
