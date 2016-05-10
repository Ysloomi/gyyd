package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitQueryListDetailActivity extends BaseActivity {

    public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
    private ListView listView;
    private SimpleAdapter simAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        context = VisitQueryListDetailActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        listView = (ListView) findViewById(R.id.list_view);

        String idate = getIntent().getStringExtra("idate");
        String name = getIntent().getStringExtra("name");
        ProgressDialogUtil.showProgressDialog(context);
        visitServer_Detail(idate, name);
    }

    private void visitServer_Detail(String date, String name) {
        String httpUrl = User.mainurl + "sf/visitlist2";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("idate", Escape.escape(date));
        parameters_userInfo.put("username", Escape.escape(name));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 1) {
                                Toast.makeText(VisitQueryListDetailActivity.this,
                                        "没有相关信息", Toast.LENGTH_SHORT).show();
                            } else if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("id", obj.getString("id"));
                                    map.put("idate", obj.getString("idate"));
//                                    map.put("code", obj.getString("ccuscode"));
                                    map.put("name", obj.getString("ccusname"));
                                    map.put("reachtime", obj.getString("s1"));
                                    map.put("leavetime", "－" + obj.getString("s2"));
                                    map.put("totaltime", "时长" + obj.getString("s3") + "分钟");
                                    map.put("cs", "阅读次数:" + obj.getString("cs"));
                                    map.put("type", obj.getString("cccname"));
                                    datas.add(map);
                                }
                                simAdapter = new SimpleAdapter(
                                        context,
                                        datas,// 数据源
                                        R.layout.item_visitquerylist_detail,// 显示布局
                                        new String[]{"idate", "name","type",
                                                "reachtime", "leavetime",
                                                "totaltime", "cs"}, new int[]{
                                        R.id.date,
                                        R.id.person,
                                        R.id.type,
                                        R.id.reach_time,
                                        R.id.leave_time,
                                        R.id.total_time,
                                        R.id.read_time});
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
                                        String idVisit = map.get("id");
                                        Intent intent = new Intent(context, VisitQueryActivity.class);
                                        intent.putExtra("id", idVisit);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });

    }
}
