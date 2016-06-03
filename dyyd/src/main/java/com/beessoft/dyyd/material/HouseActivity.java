package com.beessoft.dyyd.material;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import java.util.List;

public class HouseActivity extends BaseActivity {

	private String mac, department, street, flag = "wait";
	private String departCode;
	private Spinner departSpinner, spinner2;
	private Context context;
	
	private List<String> listDepCodes = new ArrayList<String>();
	private String research ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_house);
		
		context = HouseActivity.this;
		research = getIntent().getStringExtra("research");
		mac = GetInfo.getIMEI(context);
				
		departSpinner = (Spinner) findViewById(R.id.departmenr_spinner);
		spinner2 = (Spinner) findViewById(R.id.person_spinner);

		findViewById(R.id.add_bt).setOnClickListener(onClickListener);
		findViewById(R.id.requery_bt).setOnClickListener(onClickListener);


		ProgressDialogUtil.showProgressDialog(context);
		visitServer_GetInfo();
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_bt:
				if ("finish".equals(flag)) {
					department = departSpinner.getSelectedItem().toString();
					street = spinner2.getSelectedItem().toString();
					if ("[全部]".equals(street)) {
						ToastUtil.toast(context, "请选择街道");
					} else {
						Intent intent = new Intent();
						intent.setClass(context, HouseResearchActivity.class);
						intent.putExtra("department", department);
						intent.putExtra("departmentcode", departCode);
						intent.putExtra("house", street);
						intent.putExtra("itype", "add");
						intent.putExtra("id", "");
						startActivity(intent);
					}
				}
				break;
			case R.id.requery_bt:
				if ("finish".equals(flag)) {
					department = departSpinner.getSelectedItem().toString();
					street = spinner2.getSelectedItem().toString();
					Intent intent = new Intent();
					intent.setClass(context, QueryHouseActivity.class);
					intent.putExtra("department", department);
					intent.putExtra("departmentcode", departCode);
					intent.putExtra("house", street);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}
	};

	private void visitServer_GetInfo() {
		
		String httpUrl = User.mainurl + "app/getdep";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							String code = dataJson.getString("code");
							if ("0".equals(code)) {
								JSONArray array = dataJson.getJSONArray("list");
								List<String> list = new ArrayList<String>();

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									list.add(obj.getString("cdepname"));
									listDepCodes.add(obj.getString("cdepcode"));
								}
					

								// 声明一个ArrayAdapter用于存放简单数据
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										HouseActivity.this,
										R.layout.item_spinner,
										list);
								// 把定义好的Adapter设定到spinner中
								departSpinner.setAdapter(adapter);

								// 为第一个Spinner设定选中事件
								departSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long id) {
										departCode = listDepCodes.get(position);
										visitServer();
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> parent) {
										// 这个一直没有触发，我也不知道什么时候被触发。
										// 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
									}
								});
							} else if (code.equals("1")) {
								ToastUtil.toast(context, "没有部门权限");
							} else if (code.equals("-2")) {
								ToastUtil.toast(context, "无权限");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	// 访问服务器http post
	private void visitServer() {
		
		String httpUrl = User.mainurl + "survey/AppGetVillage";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("cdepcode", departCode);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							String code = dataJson.getString("code");
							if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								// 构建list
								List<String> list = new ArrayList<String>();

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									list.add(obj.get("villagename").toString());
								}
								// 声明一个ArrayAdapter用于存放简单数据
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										HouseActivity.this,
										R.layout.item_spinner,
										list);
								// 把定义好的Adapter设定到spinner中
								spinner2.setAdapter(adapter);
							} else if (code.equals("1")) {
								ToastUtil.toast(context, "没有街道权限");
							} 
							flag = "finish";
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
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
