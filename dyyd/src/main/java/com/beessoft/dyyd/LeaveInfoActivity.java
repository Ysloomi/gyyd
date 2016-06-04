package com.beessoft.dyyd;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class LeaveInfoActivity extends BaseActivity {


	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = LeaveInfoActivity.this;

		listView = (ListView) findViewById(R.id.list_view);

		ProgressDialogUtil.showProgressDialog(context);
		visitServer();

	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/leavemx";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(LeaveInfoActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("name", obj.getString("username"));
									map.put("department", obj.getString("cdepname"));
									map.put("offtime", "离开区域时间:" + obj.getString("cmakertime"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										LeaveInfoActivity.this,
										datas,// 数据源
										R.layout.item_leaveinfo,// 显示布局
										new String[] { "department", "name",
												"offtime" }, new int[] {
												R.id.department, R.id.person,
												R.id.offtime }); 
								listView.setAdapter(simAdapter);
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
