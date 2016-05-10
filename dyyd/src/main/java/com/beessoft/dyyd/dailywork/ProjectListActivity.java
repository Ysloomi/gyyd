package com.beessoft.dyyd.dailywork;

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
import com.beessoft.dyyd.check.VisitReachActivity;
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

public class ProjectListActivity extends BaseActivity {

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context =ProjectListActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);
		
		listView = (ListView) findViewById(R.id.list_view);

		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/sf_projectApproval";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==1) {
								Toast.makeText(ProjectListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("name", obj.getString("name"));
									map.put("contents", "待办内容:" + obj.getString("contents"));
									map.put("date", "完成日期:" + obj.getString("date"));
									map.put("photonum", "照片要求数量:" + obj.getString("photo_num"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										ProjectListActivity.this, datas,// 数据源
										R.layout.item_project,// 显示布局
										new String[] { "name", "contents", "date",
												"photonum" }, new int[] {
												R.id.name, R.id.contents,
												R.id.date,
												R.id.photonum });
								// simAdapter.setViewBinder(new MyViewBinder());
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
										Intent intent = new Intent(context, VisitReachActivity.class);
										intent.putExtra("name", map.get("name"));
										intent.putExtra("contents", map.get("contents"));
										intent.putExtra("date", map.get("date"));
										intent.putExtra("photonum", map.get("photonum"));
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
