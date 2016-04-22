package com.beessoft.dyyd.dailywork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.material.BossWorkBookActivity;
import com.beessoft.dyyd.material.BranchTargetActivity;
import com.beessoft.dyyd.material.CompanyTargetActivity;
import com.beessoft.dyyd.material.SalesWorkBookActivity;
import com.beessoft.dyyd.material.WorkBookActivity;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NoticeListActivity extends BaseActivity {

	private String mac, state,username;

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;

	List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.notice_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_unread:
			Tools.cleanlist(dataList, simAdapter, listView);
			state = "0";// 未读
			visitServer(NoticeListActivity.this);
			return true;
		case R.id.action_read:
			Tools.cleanlist(dataList, simAdapter, listView);
			state = "1";// 已读
			visitServer(NoticeListActivity.this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticelist);

		listView = (ListView) findViewById(R.id.notice_list);
		mac = GetInfo.getIMEI(NoticeListActivity.this);
		username = GetInfo.getUserName(this);
		
		state = "0";// 未读
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(NoticeListActivity.this, "载入中...",
				"请等待...", true, false);
		visitServer(NoticeListActivity.this);
	}

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/noticelist";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("state", state);
		parameters_userInfo.put("usercode", username);
//		System.out.println(username);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("notice response:" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {

								JSONArray array = dataJson.getJSONArray("list");

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("idTarget", obj.getString("id"));
									map.put("itype", obj.getString("itype"));
									map.put("context", obj.getString("context"));
									map.put("url", obj.getString("url"));
									map.put("bookclass",
											obj.getString("bookclass"));
									map.put("state", obj.getString("state"));
									map.put("date", obj.getString("ddate"));
									map.put("fbperson",
											obj.getString("fbperson"));
									dataList.add(map);
								}
								simAdapter = new SimpleAdapter(
										NoticeListActivity.this,
										dataList,// 数据源
										R.layout.noticelist_item,// 显示布局
										new String[] { "date", "fbperson",
												"state", "context" },
										new int[] { R.id.date, R.id.person,
												R.id.state, R.id.context });
								listView.setAdapter(simAdapter);
								// 添加点击
								listView.setOnItemClickListener(new OnItemClickListener() {
									@SuppressWarnings({ "unchecked" })
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {

										ListView listView = (ListView) parent;
										HashMap<String, String> map = (HashMap<String, String>) listView
												.getItemAtPosition(position);

										String itype = map.get("itype");
										String idTarget = map.get("idTarget");
										String person = map.get("fbperson");
										String date = map.get("date");
										String myContext = map.get("context");

										if ("0".equals(itype)) {

											Tools.cleanlist(dataList,
													simAdapter, listView);

											visitServer_save(
													NoticeListActivity.this,
													idTarget);
											visitServer(NoticeListActivity.this);

											inputTitleDialog(date, person,
													myContext);

										} else if ("1".equals(itype)) {
											visitServer_save(
													NoticeListActivity.this,
													idTarget);
											String bookclass = map
													.get("bookclass");
											Intent intent = new Intent();
											if ("1".equals(bookclass)) {
												intent.setClass(
														NoticeListActivity.this,
														WorkBookActivity.class);
											} else if ("2".equals(bookclass)) {
												intent.setClass(
														NoticeListActivity.this,
														SalesWorkBookActivity.class);
											} else if ("3".equals(bookclass)) {
												intent.setClass(
														NoticeListActivity.this,
														BossWorkBookActivity.class);
											} else if ("4".equals(bookclass)) {
												intent.setClass(
														NoticeListActivity.this,
														CompanyTargetActivity.class);
											} else if ("5".equals(bookclass)) {
												intent.setClass(
														NoticeListActivity.this,
														BranchTargetActivity.class);
											}
											intent.putExtra("from", "notice");
											intent.putExtra("url",
													map.get("url") + "&mac=" + mac+"&usercode="+username);
											startActivity(intent);
										}
									}
								});
							} else if ("1".equals(dataJson.getString("code"))) {
								Toast.makeText(NoticeListActivity.this, "暂无通知",
										Toast.LENGTH_SHORT).show();
							} else if ("-2".equals(dataJson.getString("code"))) {
								Toast.makeText(NoticeListActivity.this, "无权限",
										Toast.LENGTH_SHORT).show();
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

	/**
	 * 打开dialog显示
	 */
	@SuppressLint("InflateParams")
	private void inputTitleDialog(String date, String person, String myContext) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.noticeinfo, null);
		// EditText editText = (EditText)findViewById(R.id.content);// error
		TextView textView1 = (TextView) view.findViewById(R.id.info_date);
		TextView textView2 = (TextView) view.findViewById(R.id.info_person);
		TextView textView3 = (TextView) view.findViewById(R.id.info_context);
		// System.out.println("")
		textView1.setText(date);
		textView2.setText(person);
		textView3.setText(myContext);
		textView3.setMovementMethod(ScrollingMovementMethod.getInstance());// 可滚动

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("通知详情").setView(view).setPositiveButton("确认", null)
				.show();
	}

	private void visitServer_save(Context context, String idTarget) {
		String httpUrl = User.mainurl + "sf/notice_save";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");
		parameters_userInfo.put("id", idTarget);
		parameters_userInfo.put("usercode", username);


		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("0")) {

								Toast.makeText(NoticeListActivity.this,
										"标记已阅读成功", Toast.LENGTH_SHORT).show();

							} else if (dataJson.getString("code").equals("1")) {
								Toast.makeText(NoticeListActivity.this,
										"当日已签到", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("-2")) {
								Toast.makeText(NoticeListActivity.this, "无权限",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}
