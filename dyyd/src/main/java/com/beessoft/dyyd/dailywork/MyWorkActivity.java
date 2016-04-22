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

public class MyWorkActivity extends BaseActivity {

	private String mac, name;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mywork);

		listView = (ListView) findViewById(R.id.mywork_list);
	}

	@Override
	protected void onStart() {
		cleanlist();
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(MyWorkActivity.this, "载入中...",
				"请等待...", true, false);
		visitServer_Main(MyWorkActivity.this);
		super.onStart();
	}

	// 访问服务器http post
	private void visitServer_Main(Context context) {
		String httpUrl = User.mainurl + "sf/mywork_wait";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", GetInfo.getIMEI(MyWorkActivity.this));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String code = dataJson.getString("code");

							if (code.equals("1")) {
								Toast.makeText(MyWorkActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							} else if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("name", obj.getString("name"));
									map.put("message", obj.getString("txt"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										MyWorkActivity.this,
										datas,// 数据源
										R.layout.mywork_item,// 显示布局
										new String[] { "name", "message" },
										new int[] { R.id.name, R.id.message });
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
										name = map.get("name");
										if ("渠道拜访".equals(name)) {
											// // 清空列表
											// cleanlist();
											// visitServer_WithoutName(MyWorkActivity.this);
											Intent intent = new Intent(
													MyWorkActivity.this,
													TodoListActivity.class);
											startActivity(intent);
										} else if ("待审批工作日志".equals(name)) {
											Intent intent = new Intent(
													MyWorkActivity.this,
													ApproveListActivity.class);
											startActivity(intent);
										} else if ("待审批签到".equals(name)) {
											Intent intent = new Intent(
													MyWorkActivity.this,
													CheckApproveListActivity.class);
											startActivity(intent);
										} else if ("上级安排工作".equals(name)) {
											Intent intent = new Intent(
													MyWorkActivity.this,
													ArrangeQueryListActivity.class);
											intent.putExtra("itype", "0");
											startActivity(intent);
										} else if ("待确认工作日志".equals(name)) {
											Intent intent = new Intent(
													MyWorkActivity.this,ConfirmListActivity.class);
											startActivity(intent);
										} 
//										else if ("项目待办".equals(name)) {
//											Intent intent = new Intent(
//													MyWorkActivity.this,
//													ProjectListActivity.class);
//											startActivity(intent);
//										}
										else if ("待审批请假".equals(name)) {
											Intent intent = new Intent(
													MyWorkActivity.this,
													AskLeaveApproveListActivity.class);
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


//	@Override
//	protected void onPause() {
//		super.onPause();
//		XGPushManager.onActivityStoped(this);
//	}
//	@Override
//	protected void onResume() {
//		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
////		Log.d("TPush", "onResumeXGPushClickedResult:" + click);
//		if (click != null) { // 判断是否来自信鸽的打开方式
////			Toast.makeText(this, "通知被点击:" + click.toString(),
////					Toast.LENGTH_SHORT).show();
//
////			String customContent = click.getCustomContent();
////			try {
////				JSONObject obj = new JSONObject(customContent);
//////				Log.d("TPush", "自定义key-value:"+obj);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
//			cleanlist();
//			// 显示ProgressDialog
//			progressDialog = ProgressDialog.show(MyWorkActivity.this, "载入中...",
//					"请等待...", true, false);
//			visitServer_Main(MyWorkActivity.this);
//		}
//		super.onResume();
//	}

}
