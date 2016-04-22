package com.beessoft.dyyd.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerAdviseType {
	
//    private static AdviseTypeCallback mCallback;
//
//    public interface AdviseTypeCallback{
//        abstract void rankingsResultData(JSONArray result,int code);
//        abstract void postResultData(int result);
//        abstract void shareResultData(int result);
//    }
	// 访问服务器http post
    public static void getAdviseTypeList(final Context context,String mac,String pass,final List<String> list,final ListView listView ,final ProgressDialog progressDialog) {
		String httpUrl = User.mainurl + "sf/adviseType";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						
						System.out.println("response:" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String code = dataJson.getString("code");
							if("1".equals(code)){
								ToastUtil.toast(context, "没有相关信息");
							}else if ("0".equals(code)) {
								
								JSONArray arrayType = dataJson.getJSONArray("list");
								

								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									list.add(obj.get("name").toString());
								}
								ArrayAdapter<String> adapterType = new ArrayAdapter<String>(
										context, android.R.layout.simple_list_item_1, list);
								listView.setAdapter(adapterType);
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
						
						
//		         if (response != null) {
//		        	 try {
//		        		 JSONObject status = new JSONObject(response);
//		                 int code = status.getInt("code");
//		                 JSONArray list = null;
//		                 if (code == 0) {
//		                	 list = status.getJSONArray("list");
//		                 }
//		                 if(mCallback != null){
//		                	 mCallback.rankingsResultData(list,code);
//		                	 System.out.println("mCallback:" + mCallback);
//		                 }
//		                 System.out.println("mCallback:" + mCallback);
//		              } catch (JSONException e) {
//		            	  e.printStackTrace();
//		            	  if(mCallback != null){
//		            		  mCallback.rankingsResultData(null,2);
//		                  }
//		              }
//		              finally {
//		            	  progressDialog.dismiss();
//		              }
//		          }
//		            }
//
//		            public void onFailure(Throwable e, JSONObject errorResponse) {
//		                if(mCallback != null){
//		                    mCallback.rankingsResultData(null,-1);
//		                }
//		                progressDialog.dismiss();
//		            }
//
//		            public void onFailure(Throwable error, String content) {
//		                if(mCallback != null){
//		                    mCallback.rankingsResultData(null,-1);
//		                }
//		                progressDialog.dismiss();
//		            }            
//		        });   
//		}     
						
}
