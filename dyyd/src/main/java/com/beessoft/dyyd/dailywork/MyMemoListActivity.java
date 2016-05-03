package com.beessoft.dyyd.dailywork;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

public class MyMemoListActivity extends BaseActivity {

	private String state;
	private List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mymemo_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(MyMemoListActivity.this,
					MyMemoActivity.class);
			intent.putExtra("idTarget", "add");
			startActivity(intent);
			return true;
		case R.id.action_todo:
			state = "0";// 待办
			visitServer();
			return true;
		case R.id.action_done:
			state = "1";// 已完成
			visitServer();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);
		context = MyMemoListActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		listView = (ListView) findViewById(R.id.list_view);

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();

	}

	@Override
	protected void onStart() {

		state = "0";// 默认载入时显示待办
		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
		String memoAlarm = getIntent().getStringExtra("memo");

		if (null != memoAlarm) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示").setMessage("你有一条备忘录信息")
					.setPositiveButton("确认", null).create().show();
			cancelAlarm();
		}
		super.onStart();
	}

	private void cancelAlarm() {
		// 定义闹钟参数
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent in = new Intent(this, MyMemoListActivity.class);
		// in.setAction("Memo");
		in.putExtra("memo", "事件提醒");
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, in, 0);
		am.cancel(pi);
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/memo";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");
		parameters_userInfo.put("state", state);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							datas.clear();
							int code = dataJson.getInt("code");
							if (code==1) {
								Toast.makeText(MyMemoListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", obj.getString("id"));
									map.put("idate", obj.getString("date"));
									map.put("itime", obj.getString("time"));
									map.put("state", obj.getString("state"));
									map.put("memo", obj.getString("item"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										MyMemoListActivity.this, datas,// 数据源
										R.layout.item_mymemo,// 显示布局
										new String[] { "idate", "itime",
												"state", "memo" }, new int[] {
												R.id.date, R.id.time,
												R.id.state, R.id.memo });
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
										String idTarget = map.get("id");
										Intent intent = new Intent(
												MyMemoListActivity.this,
												MyMemoActivity.class);
										intent.putExtra("idTarget", idTarget);
										startActivity(intent);
									}
								});

							}
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
