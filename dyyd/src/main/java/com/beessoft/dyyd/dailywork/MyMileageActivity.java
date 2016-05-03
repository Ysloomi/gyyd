package com.beessoft.dyyd.dailywork;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

public class MyMileageActivity extends BaseActivity {

	private TextView mileageTxt, totalTxt, finishTxt, leftTxt;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymileage);

		context = MyMileageActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		mileageTxt = (TextView) findViewById(R.id.mileage_text);
		totalTxt = (TextView) findViewById(R.id.total_text);
		finishTxt = (TextView) findViewById(R.id.finish_text);
		leftTxt = (TextView) findViewById(R.id.left_text);

		listView = (ListView) findViewById(R.id.mymileage_list);

		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}

	private void visitServer() {

		String httpUrl = User.mainurl + "sf/mykm";

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
							int code = dataJson.getInt("code");
							if (code==1) {
								Toast.makeText(MyMileageActivity.this,
										"没有里程数据", Toast.LENGTH_SHORT).show();
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
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
								listView.setAdapter(simAdapter);

								mileageTxt.setText(dataJson.getString("icost"));
								totalTxt.setText(dataJson.getString("iprice"));
								finishTxt.setText(dataJson.getString("iprice_pay"));
								leftTxt.setText(dataJson.getString("iprice_yu"));
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
