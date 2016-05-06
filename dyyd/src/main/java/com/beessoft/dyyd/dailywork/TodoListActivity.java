package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
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
import java.util.List;

public class TodoListActivity extends BaseActivity {

    private String from ;
    private List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
    private ListView listView;
    private SimpleAdapter simAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        context = TodoListActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

       from = getIntent().getStringExtra("from");

        if ("shop".equals(from)) {
            setTitle("渠道拜访");
            ProgressDialogUtil.showProgressDialog(context);
            visitServer();
        } else {
            setTitle("政企拜访");

            datas.clear();

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("step", "测试");
            map.put("done", "完成次数:1" );
            map.put("undo", "完成时长:1" );
            datas.add(map);

            simAdapter = new SimpleAdapter(
                    TodoListActivity.this,
                    datas,// 数据源
                    R.layout.item_todo,// 显示布局
                    new String[]{"step", "name", "done", "undo"},
                    new int[]{
                            R.id.step, R.id.name,
                            R.id.do_proportion,
                            R.id.time_last});
            listView.setAdapter(simAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onItemClick(
                        AdapterView<?> parent, View view,
                        int position, long id) {
                    ListView listView = (ListView) parent;
                    HashMap<String, String> map = (HashMap<String, String>) listView
                            .getItemAtPosition(position);
                    String step = map.get("step");
                    Intent intent = new Intent(context, TodoActivity.class);
                    intent.putExtra("step", step);
                    intent.putExtra("from", from);
                    startActivity(intent);
                }
            });
        }

        listView = (ListView) findViewById(R.id.list_view);


    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/mywork_class";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("ccus", Escape.escape("按层级汇总显示"));
        parameters_userInfo.put("ishow", "0");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            datas.clear();
                            if (code == 1) {
                                ToastUtil.toast(context, "没有相关信息");
                            } else if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("step", obj.getString("cccname"));
                                    map.put("done", "完成次数:" + obj.getString("done"));
                                    map.put("undo", "完成时长:" + obj.getString("undone"));
                                    datas.add(map);
                                }
                            }
                            simAdapter = new SimpleAdapter(
                                    TodoListActivity.this,
                                    datas,// 数据源
                                    R.layout.item_todo,// 显示布局
                                    new String[]{"step", "name", "done", "undo"},
                                    new int[]{
                                            R.id.step, R.id.name,
                                            R.id.do_proportion,
                                            R.id.time_last});
                            listView.setAdapter(simAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @SuppressWarnings("unchecked")
                                @Override
                                public void onItemClick(
                                        AdapterView<?> parent, View view,
                                        int position, long id) {
                                    ListView listView = (ListView) parent;
                                    HashMap<String, String> map = (HashMap<String, String>) listView
                                            .getItemAtPosition(position);
                                    String step = map.get("step");
                                    Intent intent = new Intent(context, TodoActivity.class);
                                    intent.putExtra("step", step);
                                    intent.putExtra("from", from);
                                    startActivity(intent);
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
}
      
