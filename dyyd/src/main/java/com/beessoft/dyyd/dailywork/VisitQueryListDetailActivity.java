package com.beessoft.dyyd.dailywork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

public class VisitQueryListDetailActivity extends BaseActivity {
	private String MacAddr;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visitquerylistdetail);

//		textView = (Spinner) findViewById(R.id.visitquery_spinner);
		listView = (ListView) findViewById(R.id.visitquery_list);

		MacAddr = GetInfo.getIMEI(VisitQueryListDetailActivity.this);
		String idate =getIntent().getStringExtra("idate");
		String name=getIntent().getStringExtra("name");
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(VisitQueryListDetailActivity.this,
				"载入中...", "请等待...", true, false);
		visitServer_Detail(VisitQueryListDetailActivity.this,idate, name);
	}

	// 访问服务器http post
	private void visitServer_Detail(Context context, String date, String name) {
		String httpUrl = User.mainurl + "sf/visitlist2";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", MacAddr);
		parameters_userInfo.put("idate", Escape.escape(date));
		parameters_userInfo.put("username", Escape.escape(name));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							// System.out.println("dataJson" + dataJson);
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(VisitQueryListDetailActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idVisit", obj.getString("id"));
									map.put("idate", obj.getString("idate"));
									map.put("name", obj.getString("ccuscode"));
									map.put("reachtime", obj.getString("s1"));
									map.put("leavetime", "－" + obj.getString("s2"));
									map.put("totaltime", "时长" + obj.getString("s3") + "分钟");
									map.put("cs", "阅读次数:" + obj.getString("cs"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										VisitQueryListDetailActivity.this, datas,// 数据源
										R.layout.visitquerylist_detail_item,// 显示布局
										new String[] { "idate", "name",
												"reachtime", "leavetime",
												"totaltime","cs" }, new int[] {
												R.id.date, R.id.person,
												R.id.reach_time,
												R.id.leave_time,
												R.id.total_time,
												R.id.read_time});
								// simAdapter.setViewBinder(new MyViewBinder());
								listView.setAdapter(simAdapter);
								// 添加点击
								listView.setOnItemClickListener(new OnItemClickListener() {
									@SuppressWarnings("unchecked")
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										startActivity(new Intent(
												VisitQueryListDetailActivity.this,
												VisitQueryActivity.class));

										ListView listView = (ListView) parent;
										HashMap<String, String> map = (HashMap<String, String>) listView
												.getItemAtPosition(position);
										String idVisit = map.get("idVisit");
										// System.out.println("idVisit" +
										// idVisit);
										// 获取SharedPreferences对象
										SharedPreferences sharedPre = getSharedPreferences(
												"idVisit", MODE_PRIVATE);
										// 获取Editor对象
										Editor editor = sharedPre.edit();
										// 设置参数
										editor.putString("id", idVisit);
										// 提交
										editor.commit();
									}
								});
							}
						} catch (Exception e) {
							System.out.println("异常：123");
							e.printStackTrace();
						} finally {
							if (listView != null) {
								progressDialog.dismiss();
							}
						}
					}
				});

	}
}
