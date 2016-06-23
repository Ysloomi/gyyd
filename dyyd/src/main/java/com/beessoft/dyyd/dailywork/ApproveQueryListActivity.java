package com.beessoft.dyyd.dailywork;

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
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApproveQueryListActivity extends BaseActivity {

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = ApproveQueryListActivity.this;

		listView = (ListView) findViewById(R.id.list_view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}

	private void visitServer() {

		String httpUrl = User.mainurl + "sf/checklist";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("itype", "1");// 查询人
		parameters_userInfo.put("btn", "0");
		parameters_userInfo.put("sf", ifSf);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code ==1) {
								Toast.makeText(ApproveQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("date", obj.getString("iday"));
									map.put("verifier","审批人:" + obj.getString("verifier"));
									map.put("state",obj.getString("shstate"));
									map.put("readtime","阅读次数:" +obj.getString("cs"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									context,
									datas,// 数据源
									R.layout.item_approvelist,// 显示布局
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
									HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
									String idTarget = map.get("id");
									Intent intent = new Intent(context, ApproveQueryActivity.class);
									intent.putExtra("id", idTarget);
									startActivity(intent);
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
