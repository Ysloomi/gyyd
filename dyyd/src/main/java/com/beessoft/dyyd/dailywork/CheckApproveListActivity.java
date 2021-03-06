package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.MainActivity;
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

public class CheckApproveListActivity extends BaseActivity {

	private ListView listView;
	private SimpleAdapter simAdapter;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private boolean isNotice;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (isNotice) {
//					Intent[] intents = new Intent[2];
//					intents[0] = new Intent(context,MainActivity.class);
//					intents[1] = new Intent(context,MyWorkActivity.class);
					//					startActivities(intents);
					Intent intent = new Intent();
					intent.setClass(context,MainActivity.class);
					startActivity(intent);
				}else{
					finish();
				}

				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = CheckApproveListActivity.this;

		isNotice = getIntent().getBooleanExtra("notice",false);
		listView = (ListView) findViewById(R.id.list_view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}

	private void visitServer() {

		String httpUrl = User.mainurl + "sf/startwork_check";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							datas.clear();
							String code = dataJson.getString("code");
							if (code.equals("1")) {
								Toast.makeText(CheckApproveListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("workid", obj.getString("work_id"));
									map.put("date", obj.getString("iday"));
									map.put("explanation", obj.getString("iclass"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									context,
									datas,// 数据源
									R.layout.item_checkapprovelist,// 显示布局
									new String[] { "date", "name",
											"explanation" },
									new int[] {
											R.id.date, R.id.person,
											R.id.explanation });
							listView.setAdapter(simAdapter);
							// 添加点击
							listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(
										AdapterView<?> parent, View view,
										int position, long id) {
									ListView listView = (ListView) parent;
									HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
									Intent intent = new Intent();
									String date = map.get("date");
									String workid = map.get("workid");
									intent.setClass(context, CheckApproveActivity.class);
									intent.putExtra("workid", workid);
									intent.putExtra("date", date);
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

	@Override
	public void onBackPressed() {
		if (isNotice) {
			Intent intent = new Intent();
			intent.setClass(context,MainActivity.class);
			startActivity(intent);
		}else{
			super.onBackPressed();
		}
	}
}
