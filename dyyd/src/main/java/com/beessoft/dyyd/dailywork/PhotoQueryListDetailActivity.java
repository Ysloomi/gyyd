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
import com.beessoft.dyyd.utils.Escape;
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

public class PhotoQueryListDetailActivity extends BaseActivity {

	private String department, type;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = PhotoQueryListDetailActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		listView = (ListView) findViewById(R.id.list_view);

		department = getIntent().getStringExtra("department");
		type = getIntent().getStringExtra("type");

		ProgressDialogUtil.showProgressDialog(context);
		visitServer_WithoutName();
	}

	// 访问服务器http post
	private void visitServer_WithoutName() {
		
		String httpUrl = User.mainurl + "sf/imglist2";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");
		parameters_userInfo.put("dep", Escape.escape(department));
		parameters_userInfo.put("imgtype", Escape.escape(type));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==1) {
								ToastUtil.toast(context,"没有相关信息");
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", obj.getString("id"));
									map.put("idate", obj.getString("itime"));
									map.put("username",
											obj.getString("username"));
									map.put("explanation",
											obj.getString("context"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										PhotoQueryListDetailActivity.this,
										datas,// 数据源
										R.layout.photoquery_item,// 显示布局
										new String[] { "idate", "username",
												"explanation" }, new int[] {
												R.id.date, R.id.name,
												R.id.explanation });
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
										Intent intent = new Intent(PhotoQueryListDetailActivity.this,PhotoQueryActivity.class);
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
