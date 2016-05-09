package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.model.GetJSON;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitQueryListActivity extends BaseActivity implements View.OnClickListener{

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter simAdapter;
	// private Spinner spinner;
	private AutoCompleteTextView autoCompleteTextView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visitquerylist);

		context = VisitQueryListActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.act_search);
		listView = (ListView) findViewById(R.id.visitquery_list);


		GetJSON.visitServer_GetInfo_NoSpecial(VisitQueryListActivity.this, autoCompleteTextView, mac);
		autoCompleteTextView.setHint("专业、姓名、分局");

		ProgressDialogUtil.showProgressDialog(context);
		String level = "[全部人员]";
		visitServer(level);

		autoCompleteTextView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompleteTextView.showDropDown();// 显示下拉列表
				return false;
			}
		});
	}

	private void visitServer(String level) {

		String httpUrl = User.mainurl + "sf/visitlist";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("psn", Escape.escape(level));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							datas.clear();
							int code = dataJson.getInt("code");
							if (code==1) {
								Toast.makeText(VisitQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("idate", obj.getString("idate"));
									map.put("name", obj.getString("username"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									VisitQueryListActivity.this,
									datas,// 数据源
									R.layout.item_visitquerylist,// 显示布局
									new String[] { "idate", "name" },
									new int[] { R.id.date, R.id.person });
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
									String idate = map.get("idate");
									String name = map.get("name");

									Intent intent = new Intent(context,
											VisitQueryListDetailActivity.class);
									intent.putExtra("idate", idate);
									intent.putExtra("name", name);
									startActivity(intent);
								}
							});
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.txt_search:
				ProgressDialogUtil.showProgressDialog(context);
				String level = autoCompleteTextView.getText().toString();
				visitServer(level);
				Tools.closeInput(VisitQueryListActivity.this, autoCompleteTextView);
				break;
		}
	}
}
