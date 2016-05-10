package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
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

public class ArrangeQueryListActivity extends BaseActivity {

    private String mac, itype, state, iflag;

    public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

    private ListView listView;
    private SimpleAdapter simAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.arrangelist_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_todo:
                state = "0";// 待办
                ProgressDialogUtil.showProgressDialog(context);
                visitServer();
                return true;
            case R.id.action_done:
                state = "1";// 已完成
                ProgressDialogUtil.showProgressDialog(context);
                visitServer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        context = ArrangeQueryListActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        itype = getIntent().getStringExtra("itype");

        if (!"1".equals(itype)) {
            CharSequence myTitle = "上级安排工作";
            setTitle(myTitle);
        }

        listView = (ListView) findViewById(R.id.list_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        state = "0";// 默认载入时显示待办
        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/upwork";
        String pass = GetInfo.getPass(context);
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("pass", pass);
        parameters_userInfo.put("itype", itype);// 0为执行人，1为安排人
        parameters_userInfo.put("state", state);// 0为待办，1为完成

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            datas.clear();
                            if (code==1) {
                                Toast.makeText(ArrangeQueryListActivity.this,
                                        "没有相关信息", Toast.LENGTH_SHORT).show();
                            } else if (code==0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("idTarget", obj.getString("id"));
                                    map.put("idate", obj.getString("uptime"));
                                    map.put("username", obj.getString("upuser"));
                                    map.put("work", obj.getString("uptxt"));
                                    map.put("state", obj.getString("state"));
                                    map.put("iflag", obj.getString("oper"));
                                    datas.add(map);
                                }
                            }
                            simAdapter = new SimpleAdapter(
                                    ArrangeQueryListActivity.this, datas,// 数据源
                                    R.layout.item_arrangequery,// 显示布局
                                    new String[]{"idate", "username",
                                            "work", "state"}, new int[]{
                                    R.id.date, R.id.name,
                                    R.id.work, R.id.state});
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

                                    String idTarget = map.get("idTarget");
                                    iflag = map.get("iflag");
                                    Intent intent = new Intent(
                                            ArrangeQueryListActivity.this,
                                            ArrangeQueryActivity.class);
                                    intent.putExtra("idTarget", idTarget);
                                    intent.putExtra("itype", itype);// 0为上级安排，1为安排查询
                                    intent.putExtra("iflag", iflag);// 0为不能执行，1为可操作
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
