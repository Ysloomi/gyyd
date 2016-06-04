package com.beessoft.dyyd.check;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
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

public class SpecialActivity extends BaseActivity {

	public ArrayList<HashMap<String, String>> datas = new ArrayList<>();
	private ListView listView;
	private SimpleAdapter simAdapter;
	private int CURRENT_TYPE = 0;
	private String projectId = "";
	private String shopId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list);

		context = SpecialActivity.this;

		listView = (ListView) findViewById(R.id.list_view);
	}

	private void visitMain(String httpUrl, RequestParams parameters_userInfo) {

		AsyncHttpClient client_request = new AsyncHttpClient();

//		Logger.e(httpUrl+"?"+parameters_userInfo);
		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							datas.clear();
							if (code ==1) {
								ToastUtil.toast(context,"没有相关信息");
							} else if (code ==0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, String> map = new HashMap<>();
									if (CURRENT_TYPE==0){
										map.put("id", obj.getString("clid"));
										map.put("name", obj.getString("pjname"));
									}else if (CURRENT_TYPE==1){
										map.put("id", obj.getString("ccuscode"));
										map.put("name", obj.getString("ccusname"));
									}
//									else{
//										map.put("id", obj.getString("blid"));
//										map.put("name", obj.getString("detailsName"));
//										map.put("remarks", obj.getString("remarks"));
//										map.put("begin", obj.getString("begintime"));
//										map.put("end", obj.getString("endtime"));
//										map.put("photo", obj.getString("model"));
//									}
									datas.add(map);
								}
							}
//							if (CURRENT_TYPE==2){
//									simAdapter = new SimpleAdapter(
//											context,
//											datas,// 数据源
//											R.layout.item_check,// 显示布局
//											new String[] { "name","begin","end"},
//											new int[] { R.id.name,R.id.begin,R.id.end});
//								}else{
								simAdapter = new SimpleAdapter(
										context,
										datas,// 数据源
										R.layout.item_base_list,// 显示布局
										new String[] { "name"},
										new int[] { R.id.name});
//								}
								listView.setAdapter(simAdapter);
								listView.setOnItemClickListener(new OnItemClickListener() {
									@SuppressWarnings("unchecked")
									@Override
									public void onItemClick(AdapterView<?> parent, View view,
															int position, long id) {
										HashMap<String,String> map = datas.get(position);
										String url = User.mainurl;
										RequestParams parameters_userInfo = new RequestParams();
										parameters_userInfo.put("mac", mac);
										parameters_userInfo.put("usercode", username);
										if (CURRENT_TYPE==0){
											projectId = map.get("id");
											parameters_userInfo.put("id", projectId);
											parameters_userInfo.put("type", "1");
											url += "call/chkMain";
											CURRENT_TYPE = 1;
											ProgressDialogUtil.showProgressDialog(context);
											visitMain(url, parameters_userInfo);
										} else if (CURRENT_TYPE==1){
											shopId = map.get("id");
											Intent intent = new Intent();
											intent.setClass(context,SpecialUpActivity.class );
											intent.putExtra("shopId", shopId);
											intent.putExtra("projectId", projectId);
											startActivity(intent);
//											parameters_userInfo.put("ccuscode", projectId);
//											parameters_userInfo.put("id", shopId);
//											parameters_userInfo.put("type", "2");
//											url += "call/chkMain";
//											CURRENT_TYPE = 2;
//											ProgressDialogUtil.showProgressDialog(context);
//											visitMain(url, parameters_userInfo);
										}
//										else{
//											Intent intent = new Intent();
//											intent.setClass(context,SpecialUpActivity.class );
//											intent.putExtra("list",datas);
//											startActivity(intent);
//										}
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
	protected void onStart() {
		super.onStart();
		String url = User.mainurl;
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		if (CURRENT_TYPE==0){
			parameters_userInfo.put("type", "0");
			url += "call/chkMain";
			ProgressDialogUtil.showProgressDialog(context);
			visitMain(url, parameters_userInfo);
		}else if (CURRENT_TYPE==1){
			parameters_userInfo.put("id", projectId);
			parameters_userInfo.put("type", "1");
			url += "call/chkMain";
			ProgressDialogUtil.showProgressDialog(context);
			visitMain(url, parameters_userInfo);
		}
//		else if (CURRENT_TYPE==2){
//			parameters_userInfo.put("ccuscode", projectId);
//			parameters_userInfo.put("id", shopId);
//
//
// .put("type", "2");
//			url += "call/chkMain";
//			ProgressDialogUtil.showProgressDialog(context);
//			visitMain(url, parameters_userInfo);
//		}
	}
}
