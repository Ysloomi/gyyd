package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
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

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = MyWorkActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		listView = (ListView) findViewById(R.id.list_view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ProgressDialogUtil.showProgressDialog(context);
		getData();
	}

	private void getData() {

		String httpUrl = User.mainurl + "sf/mywork_wait";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", "cs");
		parameters_userInfo.put("usercode", username);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							datas.clear();
							if (code==1) {
								ToastUtil.toast(context,"没有相关信息");
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("name", obj.getString("name"));
									map.put("message", obj.getString("txt"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									MyWorkActivity.this,
									datas,// 数据源
									R.layout.item_mywork,// 显示布局
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
									Intent intent = new Intent();
									String name = map.get("name");
									if ("渠道拜访".equals(name)) {
										intent.setClass(context, TodoListActivity.class);
										intent.putExtra("from","shop");
										startActivity(intent);
									}else if ("政企拜访".equals(name)) {
										intent.setClass(context, TodoActivity.class);
										intent.putExtra("from","unit");
										startActivity(intent);
									} else if ("待审批工作日志".equals(name)) {
										intent.setClass(context, ApproveListActivity.class);
										startActivity(intent);
									} else if ("待审批签到".equals(name)) {
										intent.setClass(context, CheckApproveListActivity.class);
										startActivity(intent);
									} else if ("上级安排工作".equals(name)) {
										intent.setClass(context, ArrangeQueryListActivity.class);
										intent.putExtra("itype", "0");
										startActivity(intent);
									} else if ("待确认工作日志".equals(name)) {
										intent.setClass(context, ConfirmListActivity.class);
										startActivity(intent);
									}
//										else if ("项目待办".equals(name)) {
//											Intent intent = new Intent(
//													MyWorkActivity.this,
//													ProjectListActivity.class);
//											startActivity(intent);
//										}
									else if ("待审批请假".equals(name)) {
										intent.setClass(context, AskLeaveApproveListActivity.class);
										startActivity(intent);
									}
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
//			ProgressDialogUtil.showProgressDialog(context);
//			getData();
//		}
//		super.onResume();
//	}
}
