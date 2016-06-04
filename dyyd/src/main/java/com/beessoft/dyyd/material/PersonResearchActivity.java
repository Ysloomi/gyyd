package com.beessoft.dyyd.material;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

public class PersonResearchActivity extends BaseActivity {

    private EditText belongEdit;
    private EditText numEdit;
    private EditText nameEdit;
    
    private EditText cardEdit;
    private EditText phoneEdit;
    private EditText mouthEdit;
    private EditText voiceEdit;
    private EditText flowEdit;
    private EditText terminalEdit;
    
    private String company;
    private String department;
    private String departmentCode;
    private String street;
    private String belong;
    private String num;
    private String name;
    
    private String card;
    private String phone;
    private String mouth;
    private String voice;
    private String flow;
    private String terminal;
    private String image;
    private String max;

    
    private String itype;
    private String id;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personresearch);
        
        context = PersonResearchActivity.this;
        
        company="什邡分公司";
        department=getIntent().getStringExtra("department");
        departmentCode=getIntent().getStringExtra("departmentcode");
        street=getIntent().getStringExtra("street");
        max=getIntent().getStringExtra("maxstreet");
        itype=getIntent().getStringExtra("itype");
        id=getIntent().getStringExtra("id");
        
        initView();
    }

	private void initView() {
		TextView companyText = (TextView) findViewById(R.id.company_tv);
		TextView departmentText = (TextView) findViewById(R.id.department_tv);
		TextView streetTextView = (TextView) findViewById(R.id.street_tv);
		
		companyText.setText(company);
		departmentText.setText(department);
		streetTextView.setText(street);
		
		belongEdit = (EditText) findViewById(R.id.belong_tv);
		numEdit = (EditText) findViewById(R.id.num_tv);
		nameEdit = (EditText) findViewById(R.id.name_tv);
		
		cardEdit = (EditText) findViewById(R.id.card_tv);
		phoneEdit = (EditText) findViewById(R.id.phone_tv);
		mouthEdit = (EditText) findViewById(R.id.mouth_tv);
		voiceEdit = (EditText) findViewById(R.id.voice_tv);
		flowEdit = (EditText) findViewById(R.id.flow_tv);
		terminalEdit = (EditText) findViewById(R.id.terminal_tv);
	
		
		findViewById(R.id.confirm_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		
			     belong= belongEdit.getText().toString();
			     num= numEdit.getText().toString();
			     name= nameEdit.getText().toString();
			     
			     card= cardEdit.getText().toString();
			     phone= phoneEdit.getText().toString();
			     mouth= mouthEdit.getText().toString();
			     voice= voiceEdit.getText().toString();
			     flow= flowEdit.getText().toString();
			     terminal= terminalEdit.getText().toString();
			     
			     Boolean a = true;
			     String b = num.replaceAll("，", ",");
			     String[] c = b.split(",");
			     for (int i = 0; i < c.length; i++) {
					if(Integer.valueOf(c[i]) > Integer.valueOf(max)){
						a = false;
						break;
					}
				}
			     if(a){
					 ProgressDialogUtil.showProgressDialog(context);
					visitServer();
				}else{
					ToastUtil.toast(context, "门牌号不能大于街道门牌");
				}
			}
		});
		
		if(!Tools.isEmpty(id)){
			ProgressDialogUtil.showProgressDialog(context);
			visitGet();
		}
	} 
	private void visitGet() {
		
		String httpUrl = User.mainurl + "survey/AppShopPersonalSurveyQuery";
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
							Log.e("sfyd", response);
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							Log.e("sfyd", dataJson.toString());
							if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								JSONObject obj = array.getJSONObject(0);
								
								belongEdit.setText(obj.getString("attach_shop"));
								numEdit.setText(obj.getString("door_num"));
								nameEdit.setText(obj.getString("shop_name"));
								
								cardEdit.setText(obj.getString("clerk_card"));
								phoneEdit.setText(obj.getString("clerk_phone"));
								mouthEdit.setText(obj.getString("month_consume"));
								voiceEdit.setText(obj.getString("voice_situation"));
								flowEdit.setText(obj.getString("flow_situation"));
								terminalEdit.setText(obj.getString("terminal_situation"));
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
			
			String httpUrl = User.mainurl + "survey/AppShopPersonalSurveySave";
			AsyncHttpClient client_request = new AsyncHttpClient();
			RequestParams parameters_userInfo = new RequestParams();

			parameters_userInfo.put("mac", mac);
			parameters_userInfo.put("usercode", username);
			parameters_userInfo.put("sf", ifSf);
			parameters_userInfo.put("cdepcode", departmentCode);
			parameters_userInfo.put("company", Escape.escape(company));
			parameters_userInfo.put("attach_shop", Escape.escape(belong));
			parameters_userInfo.put("street", Escape.escape(street));
			parameters_userInfo.put("door_num", Escape.escape(num));
			parameters_userInfo.put("shop_name", Escape.escape(name));
			
			parameters_userInfo.put("clerk_card", Escape.escape(card));
			parameters_userInfo.put("clerk_phone", Escape.escape(phone));
			parameters_userInfo.put("month_consume", Escape.escape(mouth));
			parameters_userInfo.put("voice_situation", Escape.escape(voice));
			parameters_userInfo.put("flow_situation", Escape.escape(flow));
			parameters_userInfo.put("terminal_situation", Escape.escape(terminal));
			parameters_userInfo.put("image", "");
			
			parameters_userInfo.put("itype", itype);
			if("edit".equals(itype)){
				parameters_userInfo.put("id", id);
			}

			client_request.post(httpUrl, parameters_userInfo,new AsyncHttpResponseHandler() {
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
      
