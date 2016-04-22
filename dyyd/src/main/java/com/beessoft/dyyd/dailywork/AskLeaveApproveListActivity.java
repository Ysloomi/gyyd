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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AskLeaveApproveListActivity extends BaseActivity {

	private Context context;
	private String mac;

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkapprovelist);
		
		context = AskLeaveApproveListActivity.this;
		mac = GetInfo.getIMEI(context);
		listView = (ListView) findViewById(R.id.approve_list);

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		Tools.cleanlist(datas, simAdapter, listView);
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(context,"载入中...", "请等待...", true, false);
		visitServer();
	}

	// 访问服务器http post
	private void visitServer() {
		
		String httpUrl = User.mainurl + "sf/LeaveCheck";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", mac);

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
								
//								{"code":"0","list":[{"mindate":"2015-09-09","maxdate":"2015-09-10","username":"陈晓娟",
//								"usercode":"chenxiaojuan","days":"2.0天","state":"年休☆",
//								"cmemo":"五ull","intodate":"2015-09-09","am":"1","pm":"1"},
//								{"mindate":"2015-09-09","maxdate":"2015-09-09","username":"程茜",
//								"usercode":"chengqian","days":"0.5天","state":"病假O","cmemo":"hfh",
//								"intodate":"2015-09-09","am":"1","pm":"0"}]}
								
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("start", obj.getString("mindate"));
									map.put("over", obj.getString("maxdate"));
									map.put("username", obj.getString("username"));
									map.put("usercode", obj.getString("usercode"));
									map.put("days", obj.getString("days"));
									map.put("type", obj.getString("state"));
									map.put("cmemo", obj.getString("cmemo"));
									map.put("intodate", obj.getString("intodate"));
									map.put("am",obj.getString("am"));
									map.put("pm",obj.getString("pm"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										AskLeaveApproveListActivity.this, 
										datas,// 数据源
										R.layout.askleaveapprovelist_item,// 显示布局
										new String[] { "start", "over",
												"username" ,"type" }, 
										new int[] {
												R.id.start, R.id.over,
												R.id.person,R.id.type });
								listView.setAdapter(simAdapter);
								// 添加点击
								listView.setOnItemClickListener(new OnItemClickListener() {
									@SuppressWarnings("unchecked")
									@Override
									public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
										ListView listView = (ListView) parent;
										HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
										Intent intent = new Intent();
										intent.setClass(context, AskLeaveApproveActivity.class);
										intent.putExtra("hashmap", map);
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
}
