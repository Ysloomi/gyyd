package com.beessoft.dyyd.mymeans;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Advise;
import com.beessoft.dyyd.db.AdviseDao;
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
import java.util.List;

public class AllAdviseFragment extends Fragment {

	private String mac, pass, condition;
	private ListView listView;
	private List<String> list;
	private AdviseDao adviseDao;
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View check = inflater.inflate(R.layout.alladvise, container, false);
		listView = (ListView) check.findViewById(R.id.advise_listview);
		context = getActivity();
		return check;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mac = GetInfo.getIMEI(context);
		pass = GetInfo.getPass(context);


		adviseDao = new AdviseDao(getActivity());

		condition = "所有问题";
		// 构建list
		list = new ArrayList<String>();
		ProgressDialogUtil.showProgressDialog(context);
		getAdviseTypeList();
		listView.setOnItemClickListener(new ItemClickListener());
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {

			String type = list.get(position);
			Intent intent = new Intent(getActivity(), AdviseListActivity.class);
			intent.putExtra("type", type);
			intent.putExtra("condition", condition);
			startActivity(intent);
		}
	}

	private void getAdviseTypeList() {
		String httpUrl = User.mainurl + "sf/adviseType";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							Advise advise = null;
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								ToastUtil.toast(getActivity(), "没有相关信息");
							} else if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								adviseDao.deleteAll();
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									advise = new Advise();
									advise.setAdviseType(obj.getString("name"));
									list.add(obj.getString("name"));
									adviseDao.add(advise);
								}
								ArrayAdapter<String> adapterType = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_list_item_1,
										list);
								listView.setAdapter(adapterType);
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