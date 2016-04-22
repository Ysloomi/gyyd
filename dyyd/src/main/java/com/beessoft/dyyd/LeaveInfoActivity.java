package com.beessoft.dyyd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LeaveInfoActivity extends BaseActivity {
	private String mac, pass;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaveinfo);

		listView = (ListView) findViewById(R.id.leaveinfo_list);

		mac = GetInfo.getIMEI(LeaveInfoActivity.this);
		pass = GetInfo.getPass(this);
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(LeaveInfoActivity.this, "载入中...",
				"请等待...", true, false);

		visitServer(LeaveInfoActivity.this);

	}

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/leavemx";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
//						 System.out.println("response:"+response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(LeaveInfoActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code")
									.equals("0")) {

								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("name", obj.getString("username"));
									map.put("department",
											obj.getString("cdepname"));
									map.put("offtime",
											"离开区域时间:"
													+ obj.getString("cmakertime"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										LeaveInfoActivity.this,
										datas,// 数据源
										R.layout.leaveinfo_item,// 显示布局
										new String[] { "department", "name",
												"offtime" }, new int[] {
												R.id.department, R.id.person,
												R.id.offtime }); 
								listView.setAdapter(simAdapter);
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

//	// 清除处理
//	private void cleanlist() {
//		int size = datas.size();
//		if (size > 0) {
//			datas.removeAll(datas);
//			simAdapter.notifyDataSetChanged();
//			listView.setAdapter(simAdapter);
//		}
//	}
}
