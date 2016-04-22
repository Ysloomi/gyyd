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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ArrangeQueryListActivity extends BaseActivity {

	private String mac, itype, state, iflag;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.arrangelist_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_todo:
			cleanlist();// 清除表格
			state = "0";// 待办
			// 开启ProgressDialog
			progressDialog = ProgressDialog.show(ArrangeQueryListActivity.this,
					"载入中...", "请等待...", true, false);
			visitServer(ArrangeQueryListActivity.this);
			return true;
		case R.id.action_done:
			cleanlist();// 清除表格
			state = "1";// 已完成
			// 开启ProgressDialog
			progressDialog = ProgressDialog.show(ArrangeQueryListActivity.this,
					"载入中...", "请等待...", true, false);
			visitServer(ArrangeQueryListActivity.this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arrangequerylist);

		listView = (ListView) findViewById(R.id.photoquery_list);

		itype = getIntent().getStringExtra("itype");

		if (!"1".equals(itype)) {
			 CharSequence myTitle = "上级安排工作";
			setTitle(myTitle);
		}

		mac = GetInfo.getIMEI(ArrangeQueryListActivity.this);

	}

	@Override
	protected void onStart() {
		cleanlist();
		state = "0";// 默认载入时显示待办
		// 开启ProgressDialog
		progressDialog = ProgressDialog.show(ArrangeQueryListActivity.this,
				"载入中...", "请等待...", true, false);
		visitServer(ArrangeQueryListActivity.this);
		super.onStart();
	}

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/upwork";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("itype", itype);// 0为执行人，1为安排人
		parameters_userInfo.put("state", state);// 0为待办，1为完成

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(ArrangeQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idTarget", obj.getString("id"));
									map.put("idate", obj.getString("uptime"));
									map.put("username", obj.getString("upuser"));
									map.put("work", obj.getString("uptxt"));
									map.put("state", obj.getString("state"));
									map.put("iflag", obj.getString("oper"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										ArrangeQueryListActivity.this, datas,// 数据源
										R.layout.arrangequery_item,// 显示布局
										new String[] { "idate", "username",
												"work", "state" }, new int[] {
												R.id.date, R.id.name,
												R.id.work, R.id.state });
								listView.setAdapter(simAdapter);
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
										iflag = map.get("iflag");
										Intent intent = new Intent(
												ArrangeQueryListActivity.this,
												ArrangeQueryActivity.class);
										intent.putExtra("idTarget", idTarget);
										intent.putExtra("itype", itype);// 0为上级安排，1为安排查询
										intent.putExtra("iflag", iflag);// 0为不能执行，1为可操作
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
