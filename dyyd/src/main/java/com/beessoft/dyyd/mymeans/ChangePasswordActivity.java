package com.beessoft.dyyd.mymeans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class ChangePasswordActivity extends BaseActivity {
	
	private EditText editText1, editText2, editText3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepassword);
		
		context = ChangePasswordActivity.this;
		
		editText1 = (EditText) findViewById(R.id.password_text);
		editText2 = (EditText) findViewById(R.id.new_text);
		editText3 = (EditText) findViewById(R.id.once_text);

		findViewById(R.id.changepassword_confirm).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String password = editText1.getText().toString();
				String newpassword = editText2.getText().toString();
				String once = editText3.getText().toString();
				if (TextUtils.isEmpty(password.trim())
						|| TextUtils.isEmpty(newpassword.trim())
						|| TextUtils.isEmpty(once.trim())) {
					ToastUtil.toast(context, "数据不能为空");
				}else if(!newpassword.equals(once)){
					ToastUtil.toast(context, "两次密码不一致");
				} else {
					visitServer(password,newpassword);
				}
			}
		});
	}

	private void visitServer(String password,String newpassword) {
		
		String httpUrl = User.mainurl + "sf/password";
		
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", GetInfo.getIMEI(context));
		parameters_userInfo.put("old", password);
		parameters_userInfo.put("newpass", newpassword);
		
		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								ToastUtil.toast(context, "修改密码成功");
								finish();
							} else {
								ToastUtil.toast(context, "原密码不正确");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}