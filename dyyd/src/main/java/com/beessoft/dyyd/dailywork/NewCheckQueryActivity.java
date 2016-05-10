package com.beessoft.dyyd.dailywork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.CheckQueryDetailActivity;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewCheckQueryActivity extends BaseActivity {

	private EditText dateEdit;
	private Spinner departSpinner;
	private Spinner groupSpinner;
	private Spinner personSpinner;
	private LinearLayout departLl;
	private LinearLayout groupLl;
	private LinearLayout personLl;

	private Button myCheckButton;
	private Button subCheckButton;

	private Context context;
	private String mac;

	private ArrayList<String> departList = new ArrayList<String>();
	private ArrayList<String> departIds = new ArrayList<String>();
	private ArrayList<String> groupList = new ArrayList<String>();
	private ArrayList<String> groupIds = new ArrayList<String>();
	private ArrayList<String> personList = new ArrayList<String>();
//	private ArrayList<String> personIds = new ArrayList<String>();

	private String yearStr;
	private String month;
	private String person="";
	private String flag="";
	private String  departId="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newcheckquery);
		context = NewCheckQueryActivity.this;
		mac = GetInfo.getIMEI(context);
		initView();

		String now = GetInfo.YearMonth();
		String[] a = now.split("-");
		yearStr = a[0];
		month = a[1];

//		dateEdit.setText(now);
//		dateEdit.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Calendar c = Calendar.getInstance();
//				new DatePickerDialog(context,
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//												  int monthOfYear, int dayOfMonth) {
//								yearStr = String.valueOf(year);
//								month = String.valueOf(monthOfYear + 1);
//								String day = String.valueOf(dayOfMonth);
//								if ((monthOfYear + 1) < 10) {
//									month = "0" + month;
//								}
//								if (dayOfMonth < 10) {
//									day = "0" + day;
//								}
//
//								dateEdit.setText(yearStr + "-" + month);
//							}
//						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//						.get(Calendar.DAY_OF_MONTH)).show();
//			}
//		});


		getDepart();

	}

	private void initView() {
		dateEdit = (EditText) findViewById(R.id.date_et);
		departSpinner = (Spinner) findViewById(R.id.department_sp);
		groupSpinner = (Spinner) findViewById(R.id.group_sp);
		personSpinner = (Spinner) findViewById(R.id.person_sp);
		departLl = (LinearLayout) findViewById(R.id.department_ll);
		groupLl = (LinearLayout) findViewById(R.id.group_ll);
		personLl = (LinearLayout) findViewById(R.id.person_ll);


		myCheckButton = (Button) findViewById(R.id.mycheck_bt);
		subCheckButton = (Button) findViewById(R.id.subordinatecheck_bt);

		myCheckButton.setOnClickListener(clickListener);
		subCheckButton.setOnClickListener(clickListener);

		departSpinner.setOnItemSelectedListener(itemSelectedListener);
		groupSpinner.setOnItemSelectedListener(itemSelectedListener);
		personSpinner.setOnItemSelectedListener(itemSelectedListener);
	}


	View.OnClickListener clickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			switch (v.getId())
			{
				case R.id.mycheck_bt:
//					if(!Tools.isEmpty(personId)){
						intent.setClass(context, CheckQueryDetailActivity.class);
//						intent.putExtra("year", yearStr);
//						intent.putExtra("month",month);
						intent.putExtra("person",person);
						intent.putExtra("btn", "0");//0为个人考勤，1为下属考勤
						startActivity(intent);
//					}
					break;
				case R.id.subordinatecheck_bt:
					if (Tools.isEmpty(person)||"[全部人员]".equals(person))
					{
						ToastUtil.toast(context, "请选择要查看的人员");
					} else {
						intent.setClass(context, CheckQueryDetailActivity.class);
//						intent.putExtra("year", yearStr);
//						intent.putExtra("month", month);
						intent.putExtra("person", person);
						intent.putExtra("btn", "1");
						startActivity(intent);
					}
					break;
				default:
					break;
			}
		}
	};


	AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
								   int position, long id) {
			String name ="";
			switch (parent.getId()) {
				case R.id.department_sp:
					name = departList.get(position);
					if(!"请选择".equals(name))
					{
						String locaId = "";
						if(departIds.size()!=1){
							locaId = departIds.get(position - 1);
						}else{
							locaId = departIds.get(0);
						}
						getGroup(locaId);
						getPerson(locaId);
					}
					else if("请选择".equals(name)){
						reloadSpinner(groupSpinner, groupList);
					}
					break;
				case R.id.group_sp:
					name = groupList.get(position);
					if(!"请选择".equals(name))
					{
						String locaId = "";
//						if(groupIds.size()!=1){
							locaId = groupIds.get(position - 1);
//						}else{
//							locaId = groupIds.get(0);
//						}
						getPerson(locaId);
					}
//					else if("请选择".equals(name)||"".equals(name)){
//						personList.clear();
//						reloadSpinner(personSpinner,personList);
//					}

					else if ("请选择".equals(name)) {
						if ("1".equals(flag)) {
							getPerson(departId);
						}
					}
					break;
				case R.id.person_sp:
					person = personList.get(position);
//					if(!"请选择".equals(name))
//					{
//						personId = personIds.get(position - 1);
//					}else if("请选择".equals(name)){
//						personId = "";
//					}
					break;
				default:
					break;
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};


	private void getDepart() {

		String httpUrl = User.mainurl + "app/getdep_kq";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("type", "kq");

//		Logger.e(httpUrl+"?"+parameters_userInfo);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
//							Logger.e(dataJson.toString());
//							String code = dataJson.getString("code");
							flag = dataJson.getString("flag");//考勤权限标示
							if ("0".equals(flag)) {//可查看部门考勤
								JSONArray arrayType = dataJson.getJSONArray("list");
								departList.clear();
								departIds.clear();
								if (arrayType.length() > 1) {
									departList.add("请选择");
								}
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									departList.add(obj.getString("cdepname"));
									departIds.add(obj.getString("cdepcode"));
								}
								reloadSpinner(departSpinner, departList);
							} else if("1".equals(flag)){//可查看班组考勤
								viewGone(departLl);
								JSONArray arrayType = dataJson.getJSONArray("list");
								JSONObject obj = arrayType.getJSONObject(0);
								departId = obj.getString("cdepcode");
								getGroup(departId);
							} else if("2".equals(flag)){//可查看下属考勤
								viewGone(departLl);
								viewGone(groupLl);
								JSONArray arrayType = dataJson.getJSONArray("list");
								JSONObject obj = arrayType.getJSONObject(0);
								String a = obj.getString("cdepcode");
								getPerson(a);
							} else if("3".equals(flag)){//可查看自己的考勤
								viewGone(departLl);
								viewGone(groupLl);
								viewGone(personLl);
								viewGone(subCheckButton);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}


	private void getGroup(String id) {

		String httpUrl = User.mainurl + "app/getdep_kq";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("cdepperson", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
//							Log.e("main", dataJson.toString());
							String code = dataJson.getString("code");
//							String flag = dataJson.getString("flag");
							if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								groupList.clear();
								groupIds.clear();
//								if (arrayType.length() > 1) {
								groupList.add("请选择");
//								}
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									groupList.add(obj.getString("cdepname"));
									groupIds.add(obj.getString("cdepcode"));
								}
								reloadSpinner(groupSpinner, groupList);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}


	private void getPerson(String department) {

		String httpUrl = User.mainurl + "app/getpsn";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("dep", Escape.escape(department));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
//							Log.e("main", dataJson.toString());
							String code = dataJson.getString("code");
//							String flag = dataJson.getString("flag");
							if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								personList.clear();
//								groupIds.clear();
//								if (arrayType.length() > 1) {
//									personList.add("请选择");
//								}
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									personList.add(obj.getString("username"));
//									groupIds.add(obj.getString("cdepcode"));
								}
								reloadSpinner(personSpinner, personList);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}


	private void reloadSpinner(Spinner spinner,ArrayList<String> list) {
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(
						NewCheckQueryActivity.this,
						R.layout.item_spinner,
						list);
		spinner.setAdapter(adapter);
	}

	private void viewGone(View view) {
		view.setVisibility(View.GONE);
	}
}
