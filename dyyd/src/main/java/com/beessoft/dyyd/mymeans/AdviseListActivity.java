package com.beessoft.dyyd.mymeans;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.AdviseListAdapter;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AdviseListActivity extends BaseActivity {

	private String mac, pass, type = "", question = "", condition = "";
	private ProgressDialog progressDialog;

	private ListView listView;
	private EditText editText;
	private Button button;

	private ArrayList<HashMap<String, String>> datas;

	private AdviseListAdapter adapter;
	private Boolean isFinish = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.advise_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_advise:
			Intent intent = new Intent(this, AdviseActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adviselist);

		initView();

		mac = GetInfo.getIMEI(this);
		pass = GetInfo.getPass(this);

		type = getIntent().getStringExtra("type");
		condition = getIntent().getStringExtra("condition");
		// System.out.println("type" + type + "condition" + condition);
		// 构建list
		datas = new ArrayList<HashMap<String, String>>();

		listView.setOnItemClickListener(new ItemClickListener());
		button.setOnClickListener(new ClickListener());
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(this, "载入中...", "请等待...", true,
				false);
		cleanlist();
		getAdviseList(this);
	}

	public void initView() {
		listView = (ListView) findViewById(R.id.advise_list);
		editText = (EditText) findViewById(R.id.advise_edittext);
		button = (Button) findViewById(R.id.advise_button);
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {

			HashMap<String, String> map = (HashMap<String, String>) datas
					.get(position);
			String id = map.get("id");
			String state = map.get("state");
			Intent intent = new Intent(AdviseListActivity.this,
					AdviseDetailActivity.class);
			intent.putExtra("idTarget", id);
			intent.putExtra("state", state);
			startActivity(intent);
		}
	}

	class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (isFinish) {
				cleanlist();
				question = editText.getText().toString();
				getAdviseList(AdviseListActivity.this);
				isFinish = false;
			} else {
				ToastUtil.toast(AdviseListActivity.this, "请等待数据加载");
			}
		}
	}

	// 访问服务器http post
	private void getAdviseList(Context context) {
		String httpUrl = User.mainurl + "sf/adviselist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("type", Escape.escape(type));
		parameters_userInfo.put("question", Escape.escape(question));
		parameters_userInfo.put("condition", Escape.escape(condition));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response:" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String code = dataJson.getString("code");

							if ("1".equals(code)) {
								ToastUtil.toast(AdviseListActivity.this,
										"没有相关信息");
							} else if ("0".equals(code)) {
								JSONArray arrayType = dataJson
										.getJSONArray("list");

								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									HashMap<String, String> hashMap = new HashMap<String, String>();
									hashMap.put("id",
											obj.getString("advise_id"));
									hashMap.put("advise",
											obj.getString("advise"));
									hashMap.put("advise_type",
											obj.getString("advise_type"));
									hashMap.put("time", obj.getString("time"));
									hashMap.put("state", obj.getString("state"));
									datas.add(hashMap);
								}
								adapter = new AdviseListAdapter(
										AdviseListActivity.this, datas);
								listView.setAdapter(adapter);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							progressDialog.dismiss();
							isFinish = true;
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						progressDialog.dismiss();
						isFinish = true;
					}
				});
	}

	private void cleanlist() {
		int size = datas.size();
		if (size > 0) {
			datas.removeAll(datas);
			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
		}
	}

}