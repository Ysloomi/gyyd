package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.TodoAdapter;
import com.beessoft.dyyd.check.VisitReachActivity;
import com.beessoft.dyyd.utils.Logger;
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
import java.util.List;

public class TodoActivity extends BaseActivity {

    private String from;
    private String step;
    public List<HashMap<String, String>> datas = new ArrayList<>();
    private ListView listView;
    private TodoAdapter todoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        context = TodoActivity.this;

        listView = (ListView) findViewById(R.id.list_view);
        todoAdapter = new TodoAdapter(context,datas);
        listView.setAdapter(todoAdapter);

        from = getIntent().getStringExtra("from");

        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        if ("shop".equals(from)) {
            String httpUrl = User.mainurl + "sf/mywork_cus";
            setTitle("渠道拜访");
            String code = getIntent().getStringExtra("code");
            step = getIntent().getStringExtra("step");
            ProgressDialogUtil.showProgressDialog(context);
            parameters_userInfo.put("ccccode", code);
            visitServer(httpUrl, parameters_userInfo);
        } else {
            String httpUrl = User.mainurl + "sf/mywork";
            setTitle("政企拜访");
            ProgressDialogUtil.showProgressDialog(context);
            parameters_userInfo.put("ccusType", "1");
            visitServer(httpUrl, parameters_userInfo);
        }
    }

    private void visitServer(String httpUrl, RequestParams parameters_userInfo) {


        AsyncHttpClient client_request = new AsyncHttpClient();

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            datas.clear();
                            if (!"shop".equals(from)) {
                                JSONObject dataJson = new JSONObject(response);
                                int code = dataJson.getInt("code");
                                if (code == 1) {
                                    ToastUtil.toast(context, "没有相关信息");
                                } else if (code == 0) {
                                    JSONArray array = dataJson.getJSONArray("list");
                                    for (int j = 0; j < array.length(); j++) {
                                        JSONObject obj = array.getJSONObject(j);
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("name", obj.getString("ccusname"));
                                        map.put("customercode", obj.getString("ccuscode"));
                                        map.put("step", obj.getString("cccname"));
                                        map.put("done", "完成次数:" + obj.getString("done"));
                                        map.put("undo", "完成时长:" + obj.getString("undo"));
//                                        map.put("lat", obj.getString("lat"));
//                                        map.put("lng", obj.getString("lng"));
//                                        map.put("scope", obj.getString("fw"));
                                        datas.add(map);
                                    }
                                }
                            } else {
                                JSONArray array = new JSONArray(response);
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("step", step);
                                    map.put("name", obj.getString("ccusname"));
                                    map.put("customercode", obj.getString("ccuscode"));
                                    map.put("done", obj.getString("txt"));
                                    map.put("undo", obj.getString("txt1"));
                                    datas.add(map);
                                }
                            }
                            todoAdapter.notifyDataSetChanged();
//                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                                    if ("unit".equals(from)) {
//                                        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
//                                        String name = map.get("name");
//                                        String customercode = map.get("customercode");
////                                        String lat = map.get("lat");
////                                        String lng = map.get("lng");
////                                        int scope = Integer.valueOf(map.get("scope"));
//                                        visitServer(name, customercode, lat, lng, scope);
////                                    }
//                                }
//                            });
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


    private void visitServer(final String name, final String customerCode
            , final String lat, final String lng
            , final int scope) {
        String httpUrl = User.mainurl + "sf/startwork_do";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//                            String code= dataJson.getString("icount");
                            if ("0".equals(dataJson.getString("visit"))) {
                                Intent intent = new Intent();
                                intent.setClass(context, VisitReachActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("customercode", customerCode);
                                intent.putExtra("lat", lat);
                                intent.putExtra("lng", lng);
                                intent.putExtra("scope", scope);
                                intent.putExtra("from", from);
                                startActivity(intent);
                            } else {
                                ToastUtil.toast(context, "尚有到达现场，请先离开");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
      
