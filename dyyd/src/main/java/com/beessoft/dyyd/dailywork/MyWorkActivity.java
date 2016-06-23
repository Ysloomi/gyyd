package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.MyWorkAdapter;
import com.beessoft.dyyd.check.SpecialActivity;
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

	public List<HashMap<String, String>> datas = new ArrayList<>();
	private ListView listView;
	private MyWorkAdapter myWorkAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = MyWorkActivity.this;

		listView = (ListView) findViewById(R.id.list_view);

		myWorkAdapter = new MyWorkAdapter(context, datas);
		listView.setAdapter(myWorkAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(
					AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				HashMap<String, String> map = (HashMap<String, String>) listView
						.getItemAtPosition(position);
				String name = map.get("name");
				Intent intent = new Intent();
				if ("渠道拜访".equals(name)) {
					intent.setClass(context, TodoListActivity.class);
					intent.putExtra("from","shop");
					startActivity(intent);
				} else if ("政企拜访".equals(name)) {
					intent.setClass(context, TodoActivity.class);
					intent.putExtra("from","unit");
					startActivity(intent);
				} else if ("待审批工作日志".equals(name)) {
					intent.setClass(context, ApproveListActivity.class);
					startActivity(intent);
				} else if ("待审批签到".equals(name)) {
					intent.setClass(context, CheckApproveListActivity.class);
					intent.putExtra("from","check");
					startActivity(intent);
				} else if ("上级安排工作".equals(name)) {
					intent.setClass(context, ArrangeQueryListActivity.class);
					intent.putExtra("itype", "0");
					startActivity(intent);
				} else if ("待确认工作日志".equals(name)) {
					intent.setClass(context, ConfirmListActivity.class);
					startActivity(intent);
				} else if ("待审批请假".equals(name)) {
					intent.setClass(context, AskLeaveApproveListActivity.class);
					startActivity(intent);
				} else if ("专项检查".equals(name)) {
					intent.setClass(context, SpecialActivity.class);
					startActivity(intent);
				} else if ("专项审批".equals(name)) {
					intent.setClass(MyWorkActivity.this,SpecialApproveActivity.class);
					intent.putExtra("from","special");
					startActivity(intent);
				}
// else if ("项目待办".equals(name)) {
//                    intent.setClass(
//                            MyWorkActivity.this,
//                            ProjectListActivity.class);
//                    startActivity(intent);
//                }

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		ProgressDialogUtil.showProgressDialog(context);
		visitServer_Main();
	}

	private void visitServer_Main() {

		String httpUrl = User.mainurl + "sf/mywork_wait";

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
							int code = dataJson.getInt("code");
							datas.clear();
							if (code == 1) {
								ToastUtil.toast(context, "没有相关信息");
							} else if (code == 0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, String> map = new HashMap<>();
									map.put("name", obj.getString("name"));
									map.put("message", obj.getString("txt"));
									datas.add(map);
								}
							}
							myWorkAdapter.notifyDataSetChanged();
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
