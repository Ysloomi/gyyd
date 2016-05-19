package com.beessoft.dyyd.model;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetJSON {

    /**
     * 获取有专业的人员信息
     *
     * @param context
     * @param autoCompleteTextView
     * @param mac
     */
    public static void visitServer_GetInfo(final Context context,
                                           final AutoCompleteTextView autoCompleteTextView, String mac,String username) {

        String httpUrl = User.mainurl + "app/getpsn2";

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
                            if (dataJson.getString("code").equals("0")) {
                                JSONArray array = dataJson.getJSONArray("list");
                                List<String> list = new ArrayList<String>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    list.add(obj.getString("username"));
                                }
//								for (int k = 0; k < array.length(); k++) {
//									JSONObject obj = array.getJSONObject(k);
//									list.add(obj.getString("usercode"));
//								}
                                // 现实数组在system里面需要启动Arrays.deepToString(string)
                                com.beessoft.dyyd.utils.ArrayAdapter<String> adapter
                                        = new com.beessoft.dyyd.utils.ArrayAdapter<String>(
                                        context,
                                        android.R.layout.simple_dropdown_item_1line,
                                        list);
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
     *
     * @param context
     * @param autoCompleteTextView
     * @param mac
     */
    public static void visitServer_GetInfo_NoSpecial(final Context context,
                                                     final AutoCompleteTextView autoCompleteTextView,
                                                     String mac,String username) {

        String httpUrl = User.mainurl + "app/getpsn";

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
                            if (dataJson.getString("code").equals("0")) {
                                JSONArray array = dataJson.getJSONArray("list");
                                List<String> list = new ArrayList<String>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    list.add(obj.getString("username"));
                                }
//								for (int k = 0; k < array.length(); k++) {
//									JSONObject obj = array.getJSONObject(k);
//									list.add(obj.getString("usercode"));
//								}
                                com.beessoft.dyyd.utils.ArrayAdapter<String> adapter = new com.beessoft.dyyd.utils.ArrayAdapter<String>(
                                        context,
                                        android.R.layout.simple_dropdown_item_1line,
                                        list);
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
}
