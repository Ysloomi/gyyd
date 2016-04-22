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
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyMileageActivity extends BaseActivity {

	private TextView textView1, textView2, textView3, textView4;
	private String MacAddr;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private ProgressDialog progressDialog;

	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymileage);

		textView1 = (TextView) findViewById(R.id.mileage_text);
		textView2 = (TextView) findViewById(R.id.total_text);
		textView3 = (TextView) findViewById(R.id.finish_text);
		textView4 = (TextView) findViewById(R.id.left_text);

		listView = (ListView) findViewById(R.id.mymileage_list);

		MacAddr = GetInfo.getIMEI(MyMileageActivity.this);
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(MyMileageActivity.this, "载入中...",
				"请等待...", true, false);
		visitServer(MyMileageActivity.this, MacAddr);
	}

	// 访问服务器http post
	private void visitServer(Context context, String MacAddr) {
		String httpUrl = User.mainurl + "sf/mykm";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", MacAddr);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(MyMileageActivity.this,
										"没有里程数据", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("date", obj.getString("iday"));
									map.put("journey", obj.getString("type"));
									map.put("mileage",
											"里程:" + obj.getString("km"));
									map.put("unitprice",
											"单价:" + obj.getString("dj"));
									map.put("sum", "金额:" + obj.getString("je"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										MyMileageActivity.this,
										datas,// 数据源
										R.layout.mymileage_item,// 显示布局
										new String[] { "date", "journey",
												"mileage", "unitprice", "sum" },
										new int[] { R.id.date, R.id.journey,
												R.id.mileage, R.id.unitprice,
												R.id.sum });
								// simAdapter.setViewBinder(new MyViewBinder());
								listView.setAdapter(simAdapter);

								textView1.setText(new String(dataJson
										.getString("icost")));
								textView2.setText(new String(dataJson
										.getString("iprice")));
								textView3.setText(new String(dataJson
										.getString("iprice_pay")));
								textView4.setText(new String(dataJson
										.getString("iprice_yu")));
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
