package com.beessoft.dyyd.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GetJSON {
	
	/**
	 * 获取有专业的人员信息
	 * @param context
	 * @param autoCompleteTextView
	 * @param mac
	 */

	// 访问服务器http post
	public static void visitServer_GetInfo(final Context context,
			final AutoCompleteTextView autoCompleteTextView, String mac) {

		String httpUrl = User.mainurl + "app/getpsn2";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {

							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								// 构建list
								List<String> list = new ArrayList<String>();

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									list.add(obj.get("username").toString());
								}

								for (int k = 0; k < array.length(); k++) {
									JSONObject obj = array.getJSONObject(k);
									list.add(obj.get("usercode").toString());
								}
								String[] string = (String[]) list.toArray(new String[list.size()]);

								// 现实数组在system里面需要启动Arrays.deepToString(string)
								com.beessoft.dyyd.utils.ArrayAdapter<String> adapter 
								= new com.beessoft.dyyd.utils.ArrayAdapter<String>(
										context,
										android.R.layout.simple_dropdown_item_1line,
										string);
								autoCompleteTextView.setAdapter(adapter);
								autoCompleteTextView.setHint("专业、姓名、分局");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// finally {
						// progressDialog.dismiss();
						// }
					}

					// @Override
					// public void onFailure(Throwable error, String data) {
					// error.printStackTrace(System.out);
					// progressDialog.dismiss();
					// }
				});
	}
	/**
	 * 获取没有专业的人员信息
	 * @param context
	 * @param autoCompleteTextView
	 * @param mac
	 */

	// 访问服务器http post
	public static void visitServer_GetInfo_NoSpecial(final Context context,
			final AutoCompleteTextView autoCompleteTextView, String mac) {

		String httpUrl = User.mainurl + "app/getpsn";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {

							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								// 构建list
								List<String> list = new ArrayList<String>();

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									list.add(obj.get("username").toString());
								}

								for (int k = 0; k < array.length(); k++) {
									JSONObject obj = array.getJSONObject(k);
									list.add(obj.get("usercode").toString());
								}
								// System.out.println("list" + list);
								String[] string = (String[]) list
										.toArray(new String[list.size()]);

								// 现实数组在system里面需要启动Arrays.deepToString(string)
								com.beessoft.dyyd.utils.ArrayAdapter<String> adapter = new com.beessoft.dyyd.utils.ArrayAdapter<String>(
										context,
										android.R.layout.simple_dropdown_item_1line,
										string);
								autoCompleteTextView.setAdapter(adapter);
//								autoCompleteTextView.setHint("专业、姓名、分局");

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// finally {
						// progressDialog.dismiss();
						// }
					}

					// @Override
					// public void onFailure(Throwable error, String data) {
					// error.printStackTrace(System.out);
					// progressDialog.dismiss();
					// }
				});
	}

//	// 访问服务器http post
//	private void visitServer_getPerson(final Context context,
//			final Spinner spinner, String mac,String pass) {
//		String httpUrl = User.mainurl + "app/getpsn";
//		AsyncHttpClient client_request = new AsyncHttpClient();
//		RequestParams parameters_userInfo = new RequestParams();
//		parameters_userInfo.put("mac", mac);
//		parameters_userInfo.put("pass", pass);
//
//		client_request.post(httpUrl, parameters_userInfo,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(String response) {
//						// System.out.println("response:" + response);
//						try {
//
//							JSONObject dataJson = new JSONObject(Escape
//									.unescape(response));
//							if (dataJson.getString("code").equals("0")) {
//								JSONArray array = dataJson.getJSONArray("list");
//								// 构建list
//								List<String> list = new ArrayList<String>();
//
//								for (int j = 0; j < array.length(); j++) {
//									JSONObject obj = array.getJSONObject(j);
//									list.add(obj.get("username").toString());
//								}
//								// System.out.println("list" + list);
//								String[] string = (String[]) list
//										.toArray(new String[list.size()]);
//
//								// 声明一个ArrayAdapter用于存放简单数据
//								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//										context, R.layout.spinner_item,
//										string);
//								// 把定义好的Adapter设定到spinner中
//								spinner.setAdapter(adapter);
//								// 为第一个Spinner设定选中事件
//								spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//									@Override
//									public void onItemSelected(
//											AdapterView<?> parent, View view,
//											int position, long id) {
//
////										cleanlist();
//
//										String  psn = parent
//												.getItemAtPosition(position)
//												.toString();
//										if ("[全部人员]".equals(psn)) {
//											Toast.makeText(context,
//													"请选择人员", Toast.LENGTH_SHORT)
//													.show();
////											cleanlist();
//										} else {
//											visitServer(context, psn);
//										}
//									}
//
//									@Override
//									public void onNothingSelected(
//											AdapterView<?> parent) {
//										// 这个一直没有触发，我也不知道什么时候被触发。
//										// 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
//									}
//								});
//							} else if (dataJson.getString("code").equals("1")) {
//								Toast.makeText(context, "没有部门权限",
//										Toast.LENGTH_SHORT).show();
//							} else if (dataJson.getString("code").equals("-2")) {
//								Toast.makeText(context, "无权限",
//										Toast.LENGTH_SHORT).show();
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
//	}

}
