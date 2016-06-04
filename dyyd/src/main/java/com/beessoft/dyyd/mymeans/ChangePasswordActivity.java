package com.beessoft.dyyd.mymeans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class ChangePasswordActivity extends BaseActivity {
	
	private EditText passEdt, newpassEdt, onceEdt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);
		
		context = ChangePasswordActivity.this;
		
		passEdt = (EditText) findViewById(R.id.edt_pass);
		newpassEdt = (EditText) findViewById(R.id.edt_newpass);
		onceEdt = (EditText) findViewById(R.id.edt_once);

		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String password = passEdt.getText().toString();
				String newpassword = newpassEdt.getText().toString();
				String once = onceEdt.getText().toString();
				if (TextUtils.isEmpty(password.trim())
						|| TextUtils.isEmpty(newpassword.trim())
						|| TextUtils.isEmpty(once.trim())) {
					ToastUtil.toast(context, "数据不能为空");
				}else if(!newpassword.equals(once)){
					ToastUtil.toast(context, "两次密码不一致");
				} else {
					ProgressDialogUtil.showProgressDialog(context);
					visitServer(password,newpassword);
				}
			}
		});
	}

	private void visitServer(String password,String newpassword) {
		
		String httpUrl = User.mainurl + "sf/password";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("old", password);
		parameters_userInfo.put("newpass", newpassword);
		parameters_userInfo.put("sf", ifSf);
		
		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								ToastUtil.toast(context, "修改密码成功");
								finish();
							} else {
								ToastUtil.toast(context, "原密码不正确");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}
				});
	}
}