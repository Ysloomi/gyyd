package com.beessoft.dyyd.dailywork;

import android.os.Bundle;
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

public class VisitReadActivity extends BaseActivity {

	private String  id;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = VisitReadActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		listView = (ListView) findViewById(R.id.list_view);

		id = getIntent().getStringExtra("idTarget");

		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/readvisit";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (1==code) {
								Toast.makeText(VisitReadActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							} else if (0==code) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("name", obj.getString("username"));
									map.put("readnum",
											"阅读次数:" + obj.getString("cs"));
									map.put("readtime",
											"最后阅读时间:" + obj.getString("readtime"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										VisitReadActivity.this, datas,// 数据源
										R.layout.item_read,// 显示布局
										new String[] { "name", "readnum",
												"readtime" }, new int[] {
												R.id.person, R.id.readnum,
												R.id.readtime });
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
