package com.beessoft.dyyd;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DetailActivity extends BaseActivity {
	private String mac, pass;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;
	private Context context;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_actions, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_leaveinfo:
			Intent intent = new Intent(this,LeaveInfoActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		context = DetailActivity.this;
		listView = (ListView) findViewById(R.id.detail_list);

		mac = GetInfo.getIMEI(DetailActivity.this);
		pass = GetInfo.getPass(this);
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(DetailActivity.this, "载入中...",
				"请等待...", true, false);
		visitServer();
	}

	// 访问服务器http post
	private void visitServer() {
		String httpUrl = User.mainurl + "sf/lxmx";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);// 查询人

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								ToastUtil.toast(context, "没有相关信息");
							} else if ("0".equals(code)) {
								JSONArray array = dataJson.getJSONArray("list");

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("name", obj.getString("username"));
									map.put("department",
											obj.getString("cdepname"));
									map.put("offtime","离线时间:"+ obj.getString("cmakertime"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										DetailActivity.this,
										datas,// 数据源
										R.layout.item_detail,// 显示布局
										new String[] { "department", "name",
												"offtime" }, new int[] {
												R.id.department, R.id.person,
												R.id.offtime }); 
								listView.setAdapter(simAdapter);
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
