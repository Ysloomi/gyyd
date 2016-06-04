package com.beessoft.dyyd.mymeans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Advise;
import com.beessoft.dyyd.db.AdviseDao;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdviseActivity extends BaseActivity {

	private String type, advise;
	private Button button;
	private EditText editText;
	private Spinner spinner;

	private AdviseDao adviseDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advise);

		context = AdviseActivity.this;

		initView();

		adviseDao = new AdviseDao(this);

		// 构建list
		List<String> list = new ArrayList<String>();
		List<Advise> advises = adviseDao.list();
		for (int j = 0; j < advises.size(); j++) {
			if(j==0)
				list.add("选择");
			else
				list.add(advises.get(j).getAdviseType());
		}
		ArrayAdapter<String> adapterType = new ArrayAdapter<>(context,
				R.layout.item_spinner, list);
		spinner.setAdapter(adapterType);

		button.setOnClickListener(new ClickListener());
	}

	private void initView() {
		button = (Button) findViewById(R.id.advise_button);
		editText = (EditText) findViewById(R.id.advise_edittext);
		spinner = (Spinner) findViewById(R.id.advise_spinner);
	}

	class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			advise = editText.getText().toString();
			type = spinner.getSelectedItem().toString();
			if (!TextUtils.isEmpty(advise.trim())) {
				if ("选择".equals(type)) {
					ToastUtil.toast(context, "请选择反馈类型");
				} else {
					ProgressDialogUtil.showProgressDialog(context);
					saveData();
				}
			} else {
				ToastUtil.toast(context, "意见不能为空");
			}
		}
	}

	private void saveData() {
		String httpUrl = User.mainurl + "sf/advise_save";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("type", Escape.escape(type));
		parameters_userInfo.put("activity_advise", Escape.escape(advise));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								ToastUtil.toast(AdviseActivity.this.context, "没有相关信息");
							} else if ("0".equals(code)) {
								ToastUtil.toast(AdviseActivity.this.context, "提交成功");
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
