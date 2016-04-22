package com.beessoft.dyyd.dailywork;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckApproveListActivity extends BaseActivity {

	private String mac;

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkapprovelist);

		listView = (ListView) findViewById(R.id.approve_list);

		mac = GetInfo.getIMEI(CheckApproveListActivity.this);

	}

	@Override
	protected void onStart() {
		cleanlist();
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(CheckApproveListActivity.this,
				"载入中...", "请等待...", true, false);
		visitServer(CheckApproveListActivity.this);
		super.onStart();
	}

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/startwork_check";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String code = dataJson.getString("code");
							if (code.equals("1")) {
								Toast.makeText(CheckApproveListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idTarget", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("date", obj.getString("iday"));
									map.put("explanation",
											obj.getString("iclass"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										CheckApproveListActivity.this, 
										datas,// 数据源
										R.layout.checkapprovelist_item,// 显示布局
										new String[] { "date", "name",
												"explanation" }, 
										new int[] {
												R.id.date, R.id.person,
												R.id.explanation });
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
										
										Intent intent = new Intent(
												CheckApproveListActivity.this,
												CheckApproveActivity.class);
										intent.putExtra("idTarget", idTarget);
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
