package com.beessoft.dyyd.dailywork;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.MapActivity;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class WorkLocationActivity extends BaseActivity {

	private String itype, itime;
	private Spinner departSpinner,groupSpinner, personSpinner;
	private EditText dateEdit, timeEdit;
	private Context context;

	private LinearLayout departLl;
	private LinearLayout groupLl;
	private LinearLayout personLl;

	private ArrayList<String> departList = new ArrayList<String>();
	private ArrayList<String> departIds = new ArrayList<String>();
	private ArrayList<String> groupList = new ArrayList<String>();
	private ArrayList<String> groupIds = new ArrayList<String>();
	private ArrayList<String> personList = new ArrayList<String>();

	private String department="";
	private String  person="";
	private String  flag="";
	private String  departId="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_worklocation);
		
		context = WorkLocationActivity.this;

		initView();

		dateEdit.setText(DateUtil.Date());
		dateEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(WorkLocationActivity.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
												  int monthOfYear, int dayOfMonth) {
								String yearStr = String.valueOf(year);
								String month = String.valueOf(monthOfYear + 1);
								String day = String.valueOf(dayOfMonth);
								if ((monthOfYear + 1) < 10) {
									month = "0" + month;
								}
								if (dayOfMonth < 10) {
									day = "0" + day;
								}

								dateEdit.setText(yearStr + "-" + month + "-" + day);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
						.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		timeEdit.setText(DateUtil.TimeNoSecond());
		timeEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new TimePickerDialog(WorkLocationActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
												  int hourOfDay, int minute) {

								String hour = String.valueOf(hourOfDay);
								String min = String.valueOf(minute);
								if (hourOfDay < 10) {
									hour = "0" + hour;
								}
								if (minute < 10) {
									min = "0" + min;
								}
								timeEdit.setText(hour + ":" + min);
							}
						}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
						true).show();
			}
		});

		getDepart();
	}

	private void initView() {
		departSpinner = (Spinner) findViewById(R.id.department_spinner);
		groupSpinner = (Spinner) findViewById(R.id.group_spinner);
		personSpinner = (Spinner) findViewById(R.id.person_spinner);


		departLl = (LinearLayout) findViewById(R.id.departLl);
		groupLl = (LinearLayout) findViewById(R.id.groupLl);
		personLl = (LinearLayout) findViewById(R.id.personLl);

		dateEdit = (EditText) findViewById(R.id.date_text);
		dateEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
		timeEdit = (EditText) findViewById(R.id.time_text);
		timeEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘

		findViewById(R.id.orbit_button).setOnClickListener(clickListener);
		findViewById(R.id.location_button).setOnClickListener(clickListener);


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
				case R.id.orbit_button:
						itype = "run";
						itime = dateEdit.getText().toString() + " " + timeEdit.getText().toString();
						if ("[全部人员]".equals(person)) {
							ToastUtil.toast(context, "请选择员工");
						} else {
							intent.setClass(context, MapActivity.class);
							intent.putExtra("department", department);
							intent.putExtra("person", person);
							intent.putExtra("itype", itype);
							intent.putExtra("itime", itime);
							if(Tools.isEmpty(person)){
								intent.putExtra("flag", "3");
							}else{
								intent.putExtra("flag", flag);
							}
							intent.putExtra("id", "");
							startActivity(intent);
						}
					break;
				case R.id.location_button:
//					if(Tools.isEmpty(department)){
//						ToastUtil.toast(context, "请至少选择部门或班组或人员再进行查询");
//					}else{
						itype = "do";
						itime = dateEdit.getText().toString() + " " + timeEdit.getText().toString();
						intent.setClass(context, MapActivity.class);
						intent.putExtra("department", department);
						intent.putExtra("person", person);
						intent.putExtra("itype", itype);
						intent.putExtra("itime", itime);
						if(Tools.isEmpty(person)){
							intent.putExtra("flag", "3");
						}else{
							intent.putExtra("flag", flag);
						}
						intent.putExtra("id", "");
						startActivity(intent);
//					}
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
				case R.id.department_spinner:
					name = departList.get(position);
					if(!"请选择".equals(name))
					{
						String locaId = "";
						if(departIds.size()!=1){
							locaId = departIds.get(position - 1);
						}else{
							locaId = departIds.get(0);
						}
						department = locaId;
						getGroup(locaId);
						getPerson(locaId);
					}else if("请选择".equals(name)){
						groupList.clear();
						reloadSpinner(groupSpinner, groupList);
						personList.clear();
						reloadSpinner(personSpinner, personList);
					}
					break;
				case R.id.group_spinner:
					name = groupList.get(position);
					if(!"请选择".equals(name))
					{
						String locaId = "";
//						if(groupIds.size()!=1){
						locaId = groupIds.get(position - 1);
//						}else{
//							locaId = groupIds.get(0);
//						}
						department = locaId;
						getPerson(locaId);
					}
					else if ("请选择".equals(name)) {
						if ("1".equals(flag)) {
							getPerson(departId);
						}
					}
					break;
				case R.id.person_spinner:
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

//	private void visitServer_GetInfo() {
//		String httpUrl = User.mainurl + "app/getdep";
//		AsyncHttpClient client_request = new AsyncHttpClient();
//		RequestParams parameters_userInfo = new RequestParams();
//		parameters_userInfo.put("mac", mac);
//		parameters_userInfo.put("pass", pass);
//
//		client_request.post(httpUrl, parameters_userInfo,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(String response) {
//						try {
//
//							JSONObject dataJson = new JSONObject(Escape.unescape(response));
//							String code = dataJson.getString("code");
//							if (code.equals("0")) {
//								JSONArray array = dataJson.getJSONArray("list");
//								List<String> list = new ArrayList<String>();
//
//								for (int j = 0; j < array.length(); j++) {
//									JSONObject obj = array.getJSONObject(j);
//									list.add(obj.get("cdepname").toString());
//								}
//
//								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//										WorkLocationActivity.this,
//										R.layout.item_spinner,
//										list);
//								// 把定义好的Adapter设定到spinner中
//								departSpinner.setAdapter(adapter);
//								// 为第一个Spinner设定选中事件
//								departSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//									@Override
//									public void onItemSelected(AdapterView<?> parent, View view,
//															   int position, long id) {
//										String department = parent.getItemAtPosition(position).toString();
//										visitServer(department);
//									}
//
//									@Override
//									public void onNothingSelected(
//											AdapterView<?> parent) {
//										// 这个一直没有触发，我也不知道什么时候被触发。
//										// 在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
//									}
//								});
//							} else if (code.equals("1")) {
//								ToastUtil.toast(context, "没有部门权限");
//							} else if (code.equals("-2")) {
//								ToastUtil.toast(context, "无权限");
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
//	}

	private void getDepart() {

		String httpUrl = User.mainurl + "app/getdep_kq";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("type", "gj");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							flag = dataJson.getString("flag");
							if ("0".equals(flag)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								departList.clear();
								departIds.clear();
//								if (arrayType.length() > 1) {
									departList.add("请选择");
//								}
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									departList.add(obj.getString("cdepname"));
									departIds.add(obj.getString("cdepcode"));
								}
								reloadSpinner(departSpinner, departList);
							} else if("1".equals(flag)){
								viewGone(departLl);
								JSONArray arrayType = dataJson.getJSONArray("list");
								JSONObject obj = arrayType.getJSONObject(0);
								departId = obj.getString("cdepcode");
								getGroup(departId);
//								getPerson(a);
							} else if("2".equals(flag)){
								viewGone(departLl);
								viewGone(groupLl);
								JSONArray arrayType = dataJson.getJSONArray("list");
								JSONObject obj = arrayType.getJSONObject(0);
								String a = obj.getString("cdepcode");
								getPerson(a);
							} else if("3".equals(flag)){
								viewGone(departLl);
								viewGone(groupLl);
								viewGone(personLl);
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
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("cdepperson", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
//							Logger.e(dataJson.toString());
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
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("dep", Escape.escape(department));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
//								List<String> list = new ArrayList<String>();
								personList.clear();
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									personList.add(obj.getString("username"));
								}

								reloadSpinner(personSpinner, personList);
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

	private void reloadSpinner(Spinner spinner,ArrayList<String> list) {
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(context,
						R.layout.item_spinner,
						list);
		spinner.setAdapter(adapter);
	}

	private void viewGone(View view) {
		view.setVisibility(View.GONE);
	}
}
