package com.beessoft.dyyd.mymeans;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.AdviseListAdapter;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyAdviseFragment extends Fragment {

	private String mac, pass, type, condition, question = "";
	private ListView listView;
	private Context context;
	private EditText editText;
	private Button button;

	private ProgressDialog progressDialog;

	private ArrayList<HashMap<String, String>> datas;

	private AdviseListAdapter adapter;
	private Boolean isFinish = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View check = inflater.inflate(R.layout.myadvise, container, false);
		listView = (ListView) check.findViewById(R.id.advise_list);
		editText = (EditText) check.findViewById(R.id.advise_edittext);
		button = (Button) check.findViewById(R.id.advise_button);

		context = getActivity();
		return check;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// adviseDao = new AdviseDao(getActivity());

		mac = GetInfo.getIMEI(context);
		pass = GetInfo.getPass(context);

		type = "全部";
		condition = "我的问题";

		// 构建list
		datas = new ArrayList<HashMap<String, String>>();



		listView.setOnItemClickListener(new ItemClickListener());
		button.setOnClickListener(new ClickListener());
	}

	@Override
	public void onStart() {
		super.onStart();
		// 显示ProgressDialog
		progressDialog = ProgressDialog.show(context, "载入中...", "请等待...", true,
				false);
		cleanlist();
		getAdviseList(context);
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {

			HashMap<String, String> map = (HashMap<String, String>) datas
					.get(position);
			String id = map.get("id");
			String state = map.get("state");
			Intent intent = new Intent(context, AdviseDetailActivity.class);
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
				getAdviseList(context);
				isFinish = false;
			} else {
				ToastUtil.toast(context, "请等待数据加载");
			}
		}
	}

	// 访问服务器http post
	private void getAdviseList(final Context context) {
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
								ToastUtil.toast(context, "没有相关信息");
							} else if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");

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
								adapter = new AdviseListAdapter(context, datas);
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