package com.beessoft.dyyd.dailywork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TodoListActivity extends BaseActivity {
	
	
	private String mac ;
	private String level = "按层级汇总显示";
    public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
    public Cursor cursor;
    
    private ListView listView;
    
    private ProgressDialog progressDialog;
    
    private SimpleAdapter simAdapter ;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist);
        
        listView = (ListView) findViewById(R.id.todo_list);
        
        mac = GetInfo.getIMEI(TodoListActivity.this); 
       
//		显示ProgressDialog
		progressDialog = ProgressDialog.show(TodoListActivity.this, "载入中...", "请等待...", true, false);	
		visitServer(TodoListActivity.this);
    }  
	
	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/mywork_class";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("ccus", Escape.escape(level));
		parameters_userInfo.put("ishow", "0");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));

							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(TodoListActivity.this, "没有相关信息",
										Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", j);
									map.put("step", obj.getString("cccname"));
									map.put("done", "完成次数:"+obj.getString("done"));
									map.put("undo", "完成时长:"+obj.getString("undone"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										TodoListActivity.this,
										datas,// 数据源
										R.layout.todo_item,// 显示布局
										new String[] { "step", "name", "done","undo" }, 
										new int[] {
												R.id.step, R.id.name,
												R.id.do_proportion,
												R.id.time_last });
								// simAdapter.setViewBinder(new MyViewBinder());
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
										String level = map.get("step");
										Intent intent =new Intent(TodoListActivity.this,TodoActivity.class);
										intent.putExtra("level", level);
										startActivity(intent);
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
}
      
