package com.beessoft.dyyd.dailywork;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
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

    private String level;
    public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
    private ListView listView;
    private SimpleAdapter simAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        listView = (ListView) findViewById(R.id.list_view);

        level = getIntent().getStringExtra("level");

        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/mywork";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("ccus", Escape.escape(level));
        parameters_userInfo.put("ishow", "0");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 1) {
                                ToastUtil.toast(context, "没有相关信息");
                            } else if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("step", obj.getString("cccname"));
                                    map.put("name", obj.getString("ccusname"));
                                    map.put("done", "完成次数:" + obj.getString("done"));
                                    map.put("undo", "完成时长:" + obj.getString("undone"));
                                    datas.add(map);
                                }
                                simAdapter = new SimpleAdapter(
                                        TodoActivity.this, datas,// 数据源
                                        R.layout.item_todo,// 显示布局
                                        new String[]{
                                                "step", "name", "done",
                                                "undo"},
                                        new int[]{
                                                R.id.step,
                                                R.id.name,
                                                R.id.do_proportion,
                                                R.id.time_last});
                                listView.setAdapter(simAdapter);
                                listView.setOnItemClickListener(null);
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
      
