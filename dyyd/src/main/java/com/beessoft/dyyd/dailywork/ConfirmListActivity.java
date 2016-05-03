package com.beessoft.dyyd.dailywork;

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

public class ConfirmListActivity extends BaseActivity {

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmlist);
		context = ConfirmListActivity.this;
		mac = GetInfo.getIMEI(context);

		listView = (ListView) findViewById(R.id.confirm_list);
	}

	@Override
	protected void onStart() {
		ProgressDialogUtil.showProgressDialog(context);
		visitServer(ConfirmListActivity.this);
		super.onStart();
	}

	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/check_confirmlist";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							datas.clear();
							if ("1".equals(dataJson.getString("code"))) {
								Toast.makeText(ConfirmListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if ("0".equals(dataJson.getString("code"))) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idTarget", obj.getString("id"));
									map.put("date", obj.getString("iday"));
									map.put("verifier",
											"审批人:" + obj.getString("verifier"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									ConfirmListActivity.this,
									datas,// 数据源
									R.layout.item_confirmlist,// 显示布局
									new String[] { "date", "verifier"},
									new int[] {R.id.date, R.id.verifier });
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
									Intent intent = new Intent(ConfirmListActivity.this,ConfirmActivity.class);
									intent.putExtra("idTarget", idTarget);
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
