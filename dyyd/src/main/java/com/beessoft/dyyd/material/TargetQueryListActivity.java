package com.beessoft.dyyd.material;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
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

public class TargetQueryListActivity extends BaseActivity {
	private String target;
	private Button button;

	public List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

	private ListView listView;

	private SimpleAdapter simAdapter;
	private EditText editText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_targetquerylist);

		context = TargetQueryListActivity.this;

		listView = (ListView) findViewById(R.id.targetquery_list);
		button = (Button) findViewById(R.id.targetquery_button);
		editText = (EditText) findViewById(R.id.targetquery_text);


		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 清空列表
				Tools.cleanlist(datas, simAdapter, listView);
				target = editText.getText().toString();
				if (TextUtils.isEmpty(target.trim())) {
					Toast.makeText(TargetQueryListActivity.this, "请填写查询条件",
							Toast.LENGTH_SHORT).show();
				} else {
					ProgressDialogUtil.showProgressDialog(context);
					visitServer();
				}
			}
		});
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/mblist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("val", Escape.escape(target));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							datas.clear();
							if (dataJson.getString("code").equals("1")) {
								Toast.makeText(TargetQueryListActivity.this,
										"没有相关信息", Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("code",
											"业务编号:" + obj.getString("code"));
									map.put("name",
											"业务名称:" + obj.getString("name"));
									map.put("phonenum",
											"手机号码:" + obj.getString("phonenum"));
									map.put("pername",
											"姓名:" + obj.getString("pername"));
									map.put("addr",
											"地址:" + obj.getString("addr"));
									map.put("context",
											"业务介绍:" + obj.getString("context"));
									datas.add(map);
								}
							}
							simAdapter = new SimpleAdapter(
									TargetQueryListActivity.this, datas,// 数据源
									R.layout.item_targetquerylist,// 显示布局
									new String[] { "code", "name",
											"phonenum", "pername", "addr",
											"context" }, new int[] {
									R.id.code, R.id.name,
									R.id.phonenum, R.id.personname,
									R.id.addr, R.id.context });
							listView.setAdapter(simAdapter);
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
