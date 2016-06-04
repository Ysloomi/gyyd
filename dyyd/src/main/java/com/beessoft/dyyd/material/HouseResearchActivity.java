package com.beessoft.dyyd.material;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.Calendar;

public class HouseResearchActivity extends BaseActivity {

    private EditText addrEdit;
    private EditText buildingEdit;
    private EditText unitEdit;
    private EditText floorEdit;
    private EditText numEdit;
    private EditText personEdit;
    private EditText ageEdit;
    private EditText phoneEdit;
    private EditText familyEdit;
    private EditText ifnetEdit;
    private EditText operaterEdit;
    private EditText netcostEdit;
    private EditText netstarttimeEdit;
    private EditText tvoperaterEdit;
    private EditText tvcostEdit;
    private EditText tvnumEdit;
    private EditText ifydnetEdit;
    private EditText needEdit;
    private EditText reasonEdit;

    
    private String company;
    private String department;
    private String departmentCode;
    private String house;
    
    private String addr;
    private String building;
    private String unit;
    private String floor;
    private String num;
    private String person;
    private String age;
    private String phone;
    private String family;
    private String ifnet;
    private String operater;
    private String netcost;
    private String netstarttime;
    private String tvoperater;
    private String tvcost;
    private String tvnum;
    private String ifydnet;
    private String need;
    private String reason;
    
    private String itype;
    private String id;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseresearch);
        
        context = HouseResearchActivity.this;
        
        company="什邡分公司";
        department=getIntent().getStringExtra("department");
        departmentCode=getIntent().getStringExtra("departmentcode");
        house=getIntent().getStringExtra("house");
        itype=getIntent().getStringExtra("itype");
        id=getIntent().getStringExtra("id");
        
        initView();
    }

	private void initView() {
		
		TextView companyText = (TextView) findViewById(R.id.company_tv);
		TextView departmentText = (TextView) findViewById(R.id.department_tv);
		TextView houseTextView = (TextView) findViewById(R.id.house_tv);
		
		companyText.setText(company);
		departmentText.setText(department);
		houseTextView.setText(house);
		
		addrEdit = (EditText) findViewById(R.id.addr_tv);
		buildingEdit = (EditText) findViewById(R.id.building_tv);
		unitEdit = (EditText) findViewById(R.id.unit_tv);
		floorEdit = (EditText) findViewById(R.id.floor_tv);
		numEdit = (EditText) findViewById(R.id.num_tv);
		personEdit = (EditText) findViewById(R.id.person_tv);
		ageEdit = (EditText) findViewById(R.id.age_tv);
		phoneEdit = (EditText) findViewById(R.id.phone_tv);
		familyEdit = (EditText) findViewById(R.id.family_tv);
		ifnetEdit = (EditText) findViewById(R.id.ifnet_tv);
		operaterEdit = (EditText) findViewById(R.id.operater_tv);
		netcostEdit = (EditText) findViewById(R.id.netcost_tv);
		netstarttimeEdit = (EditText) findViewById(R.id.netstarttime_tv);
		tvoperaterEdit = (EditText) findViewById(R.id.tvoperater_tv);
		
		tvcostEdit = (EditText) findViewById(R.id.tvcost_tv);
		tvnumEdit = (EditText) findViewById(R.id.tvnum_tv);
		ifydnetEdit = (EditText) findViewById(R.id.ifydnet_tv);
		needEdit = (EditText) findViewById(R.id.need_tv);
		reasonEdit = (EditText) findViewById(R.id.reason_tv);
		
		netstarttimeEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
		netstarttimeEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(context,
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
								netstarttimeEdit.setText(yearStr + "-" + month + "-"
										+ day);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		findViewById(R.id.confirm_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addr = addrEdit.getText().toString();
				building = buildingEdit.getText().toString();
				unit = unitEdit.getText().toString();
				floor = floorEdit.getText().toString();
				num = numEdit.getText().toString();
				person = personEdit.getText().toString();
				age = ageEdit.getText().toString();
				phone = phoneEdit.getText().toString();
				family = familyEdit.getText().toString();
				ifnet = ifnetEdit.getText().toString();
				operater = operaterEdit.getText().toString();
				netcost = netcostEdit.getText().toString();
				netstarttime = netstarttimeEdit.getText().toString();
				tvoperater = tvoperaterEdit.getText().toString();
				tvcost = tvcostEdit.getText().toString();
				tvnum = tvnumEdit.getText().toString();
				ifydnet = ifydnetEdit.getText().toString();
			    need= needEdit.getText().toString();
			    reason= reasonEdit.getText().toString();

				ProgressDialogUtil.showProgressDialog(context);
				visitServer();
			}
		});
		
		if(!Tools.isEmpty(id)){
			ProgressDialogUtil.showProgressDialog(context);
			visitGet();
		}
	} 
	private void visitGet() {
		
		String httpUrl = User.mainurl + "survey/AppSurveyVillageQuery";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							String code = dataJson.getString("code");
							if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								JSONObject obj = array.getJSONObject(0);

								addrEdit.setText(obj.getString("xqdz"));
								buildingEdit.setText(obj.getString("dong"));
								unitEdit.setText(obj.getString("dy"));
								floorEdit.setText(obj.getString("lou"));
								numEdit.setText(obj.getString("hao"));
								personEdit.setText(obj.getString("yzxm"));
								ageEdit.setText(obj.getString("age"));
								phoneEdit.setText(obj.getString("phone"));
								familyEdit.setText(obj.getString("jtrs"));
								ifnetEdit.setText(obj.getString("sfazkd"));
								operaterEdit.setText(obj.getString("kdyys"));
								netcostEdit.setText(obj.getString("kdzf"));
								netstarttimeEdit.setText(obj.getString("idate"));
								tvoperaterEdit.setText(obj.getString("dsyys"));
								tvcostEdit.setText(obj.getString("dszf"));
								tvnumEdit.setText(obj.getString("dssl"));
								ifydnetEdit.setText(obj.getString("fgkd"));
								needEdit.setText(obj.getString("ywxq"));
								reasonEdit.setText(obj.getString("cmemo"));
							} 
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
		private void visitServer() {
			
			String httpUrl = User.mainurl + "survey/AppSurveyVillageSave";
			AsyncHttpClient client_request = new AsyncHttpClient();
			RequestParams parameters_userInfo = new RequestParams();

			parameters_userInfo.put("mac", mac);
			parameters_userInfo.put("usercode", username);
			parameters_userInfo.put("sf", ifSf);
			parameters_userInfo.put("cdepcode", departmentCode);
//			parameters_userInfo.put("fgs", Escape.escape(company));
			parameters_userInfo.put("xqmc", Escape.escape(house));
			parameters_userInfo.put("xqdz", Escape.escape(addr));
			parameters_userInfo.put("dong", Escape.escape(building));
			parameters_userInfo.put("dy", Escape.escape(unit));
			parameters_userInfo.put("lou", Escape.escape(floor));
			parameters_userInfo.put("hao", Escape.escape(num));
			parameters_userInfo.put("yzxm", Escape.escape(person));
			parameters_userInfo.put("age", Escape.escape(age));
			parameters_userInfo.put("phone", Escape.escape(phone));
			parameters_userInfo.put("jtrs", Escape.escape(family));
			parameters_userInfo.put("sfazkd", Escape.escape(ifnet));
			parameters_userInfo.put("kdyys", Escape.escape(operater));
			parameters_userInfo.put("kdzf", Escape.escape(netcost));
			parameters_userInfo.put("idate", netstarttime);
			parameters_userInfo.put("dsyys", Escape.escape(tvoperater));
			parameters_userInfo.put("dszf", Escape.escape(tvcost));
			parameters_userInfo.put("dssl", Escape.escape(tvnum));
			parameters_userInfo.put("fgkd", Escape.escape(ifydnet));
			parameters_userInfo.put("ywxq", Escape.escape(need));
			parameters_userInfo.put("cmemo", Escape.escape(reason));
			
			
			parameters_userInfo.put("itype", itype);
			if("edit".equals(itype)){
				parameters_userInfo.put("id", id);
			}

			client_request.post(httpUrl, parameters_userInfo,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							try {
								JSONObject dataJson = new JSONObject(response);
								Log.e("sfyd", dataJson.toString());
								String code = dataJson.getString("code");
								if (code.equals("0")) {
									ToastUtil.toast(context, "上传成功");
									finish();
								} else if (code.equals("1")) {
									ToastUtil.toast(context, "上传失败");
								} 
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
      
