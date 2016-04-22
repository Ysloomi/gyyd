package com.beessoft.dyyd.dailywork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PhotoQueryListActivity extends BaseActivity {

	private String mac, department, type;
	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;
	private ProgressDialog progressDialog;
	private SimpleAdapter simAdapter;
	private Spinner spinner;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoquerylist);

		listView = (ListView) findViewById(R.id.photoquery_list);
		spinner = (Spinner) findViewById(R.id.departmenr_spinner);

		mac = GetInfo.getIMEI(PhotoQueryListActivity.this);

		visitServer_GetInfo(PhotoQueryListActivity.this);
	}

	// 访问服务器http post
	private void visitServer_GetInfo(Context context) {
		String httpUrl = User.mainurl + "app/getdep";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response:" + response);
						try {

							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								// 构建list
								List<String> list = new ArrayList<String>();

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									list.add(obj.get("cdepname").toString());
								}
								// System.out.println("list" + list);
								String[] string = (String[]) list
										.toArray(new String[list.size()]);

								// 声明一个ArrayAdapter用于存放简单数据
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										PhotoQueryListActivity.this,
										R.layout.spinner_item, string);
								// 把定义好的Adapter设定到spinner中
								spinner.setAdapter(adapter);

								// 为第一个Spinner设定选中事件
								spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long id) {
										// 清空列表
										cleanlist();
										
										department = parent.getItemAtPosition(
												position).toString();
										// 显示ProgressDialog
										progressDialog = ProgressDialog
												.show(PhotoQueryListActivity.this,
														"载入中...", "请等待...",
														true, false);

										visitServer_Main(PhotoQueryListActivity.this);
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> parent) {
										// 这个一直没有触发，我也不知道什么时候被触发。
										// 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
									}
								});
							} else if (dataJson.getString("code").equals("1")) {
								Toast.makeText(PhotoQueryListActivity.this,
										"没有部门权限", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("-2")) {
								Toast.makeText(PhotoQueryListActivity.this,
										"无权限", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	// 访问服务器http post
	private void visitServer_Main(Context context) {
		String httpUrl = User.mainurl + "sf/imglist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");
		parameters_userInfo.put("dep", Escape.escape(department));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
//						 System.out.println("response1:" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(PhotoQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("type", obj.getString("imgtype"));
									map.put("message", obj.getString("sl"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										PhotoQueryListActivity.this,
										datas,// 数据源
										R.layout.photoquery_main_item,// 显示布局
										new String[] { "type", "message" },
										new int[] { R.id.name, R.id.message });
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
										type = map.get("type");
//										// 清空列表
//										cleanlist();
										
										Intent intent =new Intent(PhotoQueryListActivity.this,PhotoQueryListDetailActivity.class);
										intent.putExtra("type", type);
										intent.putExtra("department", department);
										startActivity(intent);
//										visitServer_WithoutName(PhotoQueryListActivity.this);
									}
								});
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

//	// 访问服务器http post
//	private void visitServer_WithoutName(Context context) {
//		String httpUrl = User.mainurl + "sf/imglist2";
//		AsyncHttpClient client_request = new AsyncHttpClient();
//		RequestParams parameters_userInfo = new RequestParams();
//		parameters_userInfo.put("mac", mac);
//		parameters_userInfo.put("pass", "");
//		parameters_userInfo.put("dep", Escape.escape(department));
//		parameters_userInfo.put("imgtype", Escape.escape(type));
//		
//		client_request.post(httpUrl, parameters_userInfo,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(String response) {
////						 System.out.println("response2:" + response);
//						try {
//							JSONObject dataJson = new JSONObject(Escape
//									.unescape(response));
//
//							if (dataJson.getString("code").equals("1")) {
//								Toast.makeText(PhotoQueryListActivity.this,
//										"没有相关信息", Toast.LENGTH_SHORT).show();
//							} else if (dataJson.getString("code").equals("0")) {
//								JSONArray array = dataJson.getJSONArray("list");
//								for (int j = 0; j < array.length(); j++) {
//									JSONObject obj = array.getJSONObject(j);
//									HashMap<String, Object> map = new HashMap<String, Object>();
//									map.put("id", j);
//									map.put("idTarget", obj.getString("id"));
//									map.put("idate", obj.getString("itime"));
//									map.put("username",
//											obj.getString("username"));
//									map.put("explanation",
//											obj.getString("context"));
//									datas.add(map);
//								}
//								simAdapter = new SimpleAdapter(
//										PhotoQueryListActivity.this, datas,// 数据源
//										R.layout.photoquery_item,// 显示布局
//										new String[] { "idate", "username",
//												"explanation" }, new int[] {
//												R.id.date, R.id.name,
//												R.id.explanation });
//								listView.setAdapter(simAdapter);
//								listView.setOnItemClickListener(new OnItemClickListener() {
//									@SuppressWarnings("unchecked")
//									@Override
//									public void onItemClick(
//											AdapterView<?> parent, View view,
//											int position, long id) {
//										ListView listView = (ListView) parent;
//										HashMap<String, String> map = (HashMap<String, String>) listView
//												.getItemAtPosition(position);
//										String idTarget = map.get("idTarget");
//										Intent intent = new Intent(
//												PhotoQueryListActivity.this,
//												PhotoQueryActivity.class);
//										intent.putExtra("idTarget", idTarget);
//										startActivity(intent);
//									}
//								});
//
//							}
//						} catch (Exception e) {
//							System.out.println("异常:");
//							e.printStackTrace();
//						} finally {
//							progressDialog.dismiss();
//						}
//					}
//					@Override
//					public void onFailure(Throwable error, String data) {
//						System.out.println("failure:login");
//						error.printStackTrace(System.out);
//						progressDialog.dismiss();
//					}
//				});
//
//	}

	// 清除处理
	private void cleanlist() {
		int size = datas.size();
		if (size > 0) {
			datas.removeAll(datas);
			simAdapter.notifyDataSetChanged();
			listView.setAdapter(simAdapter);
		}
	}

}
