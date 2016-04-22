package com.beessoft.dyyd.dailywork;

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

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class VisitReadActivity extends BaseActivity {
	private String mac, id;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read);

		listView = (ListView) findViewById(R.id.read_list);

		mac = GetInfo.getIMEI(VisitReadActivity.this);
		id = getIntent().getStringExtra("idTarget");
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(VisitReadActivity.this, "载入中...",
				"请等待...", true, false);

		visitServer(VisitReadActivity.this);
	}

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/readvisit";
		String pass = GetInfo.getPass(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						 System.out.println("response:"+response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if ("1".equals(dataJson.getString("code"))) {
								Toast.makeText(VisitReadActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							} else if ("0".equals(dataJson.getString("code"))) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("name", obj.getString("username"));
									map.put("readnum",
											"阅读次数:" + obj.getString("cs"));
									map.put("readtime",
											"最后阅读时间:"
													+ obj.getString("readtime"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										VisitReadActivity.this, datas,// 数据源
										R.layout.read_item,// 显示布局
										new String[] { "name", "readnum",
												"readtime" }, new int[] {
												R.id.person, R.id.readnum,
												R.id.readtime });
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
}
