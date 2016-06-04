package com.beessoft.dyyd.mymeans;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.AdviseListAdapter;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdviseFragment extends Fragment implements OnClickListener {

	private String mac,username, ifSf, type, condition, question = "";
	private ListView listView;
	private Context context;
	private EditText editText;

	private ArrayList<HashMap<String, String>> datas;

	private AdviseListAdapter adapter;
	private Boolean isFinish = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_myadvise, container, false);
		context = getActivity();

		listView = (ListView) view.findViewById(R.id.advise_list);
		editText = (EditText) view.findViewById(R.id.advise_edittext);
		view.findViewById(R.id.txt_search).setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// adviseDao = new AdviseDao(getActivity());

		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);
		ifSf = GetInfo.getIfSf(context)?"0":"1";


		type = "全部";
		condition = "我的问题";

		// 构建list
		datas = new ArrayList<HashMap<String, String>>();

		listView.setOnItemClickListener(new ItemClickListener());
	}

	@Override
	public void onStart() {
		super.onStart();
		ProgressDialogUtil.showProgressDialog(context);
		getAdviseList();
	}

	@Override
	public void onClick(View v) {
		if (isFinish) {
			question = editText.getText().toString();
			ProgressDialogUtil.showProgressDialog(context);
			getAdviseList();
			isFinish = false;
		} else {
			ToastUtil.toast(context, "请等待数据加载");
		}
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {
			HashMap<String, String> map = datas.get(position);
			String id = map.get("id");
			String state = map.get("state");
			Intent intent = new Intent(context, AdviseDetailActivity.class);
			intent.putExtra("idTarget", id);
			intent.putExtra("state", state);
			startActivity(intent);
		}
	}


	private void getAdviseList() {

		String httpUrl = User.mainurl + "sf/adviselist";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("type", Escape.escape(type));
		parameters_userInfo.put("question", Escape.escape(question));
		parameters_userInfo.put("condition", Escape.escape(condition));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							datas.clear();
							if ("1".equals(code)) {
								ToastUtil.toast(context, "没有相关信息");
							} else if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");

								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									HashMap<String, String> hashMap = new HashMap<String, String>();
									hashMap.put("id",
											obj.getString("advise_id"));
									hashMap.put("activity_advise",
											obj.getString("activity_advise"));
									hashMap.put("advise_type",
											obj.getString("advise_type"));
									hashMap.put("time", obj.getString("time"));
									hashMap.put("state", obj.getString("state"));
									datas.add(hashMap);
								}
							}
							adapter = new AdviseListAdapter(context, datas);
							listView.setAdapter(adapter);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
							isFinish = true;
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
						isFinish = true;
					}
				});
	}
}