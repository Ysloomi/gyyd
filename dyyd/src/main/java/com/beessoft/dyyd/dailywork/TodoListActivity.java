package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
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

public class TodoListActivity extends BaseActivity {

    private String from;
    private List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
    private ListView listView;
    private SimpleAdapter simAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        context = TodoListActivity.this;

        from = getIntent().getStringExtra("from");

        listView = (ListView) findViewById(R.id.list_view);

//        if ("shop".equals(from)) {
            setTitle("渠道拜访");
            ProgressDialogUtil.showProgressDialog(context);
            visitServer();
//        } else {
//            setTitle("政企拜访");
//        }
    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/mywork_class";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            datas.clear();
                                JSONArray array = new JSONArray(response);
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("name", obj.getString("cccname"));
                                    map.put("code", obj.getString("ccccode"));
                                    int t = obj.getInt("type");
                                    String type = "";
                                    if (t==0){
                                        type="全量";
                                    }else if (t==1){
                                        type="类型单量";
                                    }else if (t==2){
                                        type="类型全量";
                                    }
                                    map.put("type", type);
                                    map.put("done", "完成次数:" + obj.getString("fin")+"/"+obj.getString("count"));
                                    map.put("undo", "完成时长:" + obj.getString("finTime")+"/"+obj.getString("sumTime"));
                                    datas.add(map);
                                }
                            simAdapter = new SimpleAdapter(
                                    TodoListActivity.this,
                                    datas,// 数据源
                                    R.layout.item_todo_list,// 显示布局
                                    new String[]{"name","type","done", "undo"},
                                    new int[]{ R.id.name, R.id.type,
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
                                    HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                                    Intent intent = new Intent(context, TodoActivity.class);
                                    intent.putExtra("step", map.get("name"));
                                    intent.putExtra("code", map.get("code"));
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
      
