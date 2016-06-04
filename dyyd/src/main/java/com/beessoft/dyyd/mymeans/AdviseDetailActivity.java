package com.beessoft.dyyd.mymeans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.HashMap;

public class AdviseDetailActivity extends BaseActivity {

	private String mId = "", advise,state;

	private ListView listView;
	private EditText editText;
	private Button button;

	private ArrayList<HashMap<String, String>> datas;

	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advisedetail);

		context = AdviseDetailActivity.this;

		mId = getIntent().getStringExtra("idTarget");
		state = getIntent().getStringExtra("state");

		initView();

		if(!"2".equals(state)){
			editText.setVisibility(View.GONE);
			button.setVisibility(View.GONE);
		}
		// 构建list
		datas = new ArrayList<HashMap<String, String>>();

		ProgressDialogUtil.showProgressDialog(context);
		getAnswerList();

		button.setOnClickListener(new ClickListener());
	}

	public void initView() {
		listView = (ListView) findViewById(R.id.advise_list);
		editText = (EditText) findViewById(R.id.advise_edittext);
		button = (Button) findViewById(R.id.advise_button);
	}

	class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			advise = editText.getText().toString();
			if (!TextUtils.isEmpty(advise.trim())) {
				ProgressDialogUtil.showProgressDialog(context);
				saveAnswer();
			} else {
				ToastUtil.toast(AdviseDetailActivity.this, "请填写意见");
			}
		}
	}

	private void getAnswerList() {
		String httpUrl = User.mainurl + "sf/answerlist";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("id", mId);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								ToastUtil.toast(AdviseDetailActivity.this,
										"没有相关信息");
							} else if ("0".equals(code)) {
								JSONArray arrayType = dataJson.getJSONArray("list");
								// "name":"张届","text":"张届","time":"2015-03-06"
								JSONObject objFisrt = arrayType.getJSONObject(0);
								String questionName= objFisrt.getString("name");
								for (int j = 0; j < arrayType.length(); j++) {
									JSONObject obj = arrayType.getJSONObject(j);
									HashMap<String, String> hashMap = new HashMap<String, String>();
//									提交人：**，意见内容：**；管理员：**,回复内容：**
									String name = obj.getString("name");
									if (name.equals(questionName)) {
										hashMap.put("name", "提交人："+obj.getString("name"));
										hashMap.put("text", "意见内容："+obj.getString("text"));
									}else{
										hashMap.put("name", "管理员："+obj.getString("name"));
										hashMap.put("text", "回复内容："+obj.getString("text"));
									}
									hashMap.put("time", obj.getString("time"));
									datas.add(hashMap);
								}
								adapter = new SimpleAdapter(
										AdviseDetailActivity.this,
										datas,// 数据源
										R.layout.item_advisedetail,// 显示布局
										new String[] { "name", "text", "time" },
										new int[] { R.id.name, R.id.text,
												R.id.time });
								listView.setAdapter(adapter);
								
								if(!questionName.equals(GetInfo.getName(context))){
									editText.setVisibility(View.GONE);
									button.setVisibility(View.GONE);
								}
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

	private void saveAnswer() {
		String httpUrl = User.mainurl + "sf/advise_answer_save";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("advise_id", mId);
		parameters_userInfo.put("answer", Escape.escape(advise));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								ToastUtil.toast(context, "没有相关信息");
							} else if ("0".equals(code)) {
								ToastUtil.toast(context, "提交成功");
								finish();
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