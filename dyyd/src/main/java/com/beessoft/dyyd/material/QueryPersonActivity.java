package com.beessoft.dyyd.material;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryPersonActivity extends BaseActivity {
	
    public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
    private ListView listView;
    private SimpleAdapter simAdapter ;

    private String department;
    private String departmentCode;
    private String street;
    private String max;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        context =QueryPersonActivity.this;
        listView = (ListView) findViewById(R.id.list_view);
		
        department=getIntent().getStringExtra("department");
        departmentCode=getIntent().getStringExtra("departmentcode");
        street=getIntent().getStringExtra("street");
        max=getIntent().getStringExtra("maxstreet");
    }  
	@Override
	protected void onStart() {
		super.onStart();
		Tools.cleanlist(datas, simAdapter, listView);
		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}
	
	// 访问服务器http post
	private void visitServer() {
		String httpUrl = User.mainurl + "survey/AppShopPersonalSurveyList";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("jd", Escape.escape(street));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if (code.equals("1")) {
								ToastUtil.toast(context, "没有相关信息");
								finish();
//								{"attach_shop":"我","shop_name":"没有 ","street":"吃","door_num":"1"}
							} else if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("id", obj.getString("id"));
									map.put("belong", "归属商客:"+obj.getString("attach_shop"));
									map.put("num", "门牌号:"+obj.getString("door_num"));
									map.put("name", "商铺名:"+obj.getString("shop_name"));
									datas.add(map);
								}
								simAdapter = new SimpleAdapter(
										QueryPersonActivity.this,
										datas,// 数据源
										R.layout.item_querystore,// 显示布局
										new String[] { "belong", "name", "num" },
										new int[] {
												R.id.belong, R.id.name,
												R.id.num});
								// simAdapter.setViewBinder(new MyViewBinder());
								listView.setAdapter(simAdapter);
								listView.setOnItemClickListener(new OnItemClickListener() {
									@SuppressWarnings("unchecked")
									@Override
									public void onItemClick(AdapterView<?> parent, View view,
															int position, long id) {
										ListView listView = (ListView) parent;
										HashMap<String, String> map = (HashMap<String, String>) listView
												.getItemAtPosition(position);
										String mId = map.get("id");
										Intent intent =new Intent(context,PersonResearchActivity.class);
										intent.putExtra("department", department);
										intent.putExtra("departmentcode", departmentCode);
										intent.putExtra("street", street);
										intent.putExtra("itype", "edit");
										intent.putExtra("maxstreet", max);
										intent.putExtra("id", mId);
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
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
					}
				});
	}
}
      
