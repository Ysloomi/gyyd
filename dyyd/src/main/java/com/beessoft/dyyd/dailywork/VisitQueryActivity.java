package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Logger;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class VisitQueryActivity extends BaseActivity implements View.OnClickListener{

	private TextView usernameTxt, visitpersonTxt, visitAimTxt, visitResultTxt, leaveAddrTxt,
			startTxt, endTxt, reachAddrTxt;
	private LinearLayout questionTypeLl;
	private LinearLayout questionLl;
	private TextView questionTypeTxt;
	private TextView questionTxt;
	private ImageView photoImage;
	private String id, photo;
	private Bitmap bitmap;
	private Thread mThread;
	private ProgressBar progressBar;
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.read_actions, menu);
//		return super.onCreateOptionsMenu(menu);
//
//	}
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.action_read:
//			Intent intent =new Intent(this,VisitReadActivity.class);
//			intent.putExtra("id", id);
//			startActivity(intent);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visitquery);

		context = VisitQueryActivity.this;

		id = getIntent().getStringExtra("id");
		
		initView();
		
		//使textview可以滚动
//		visitResultTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
	}

	private void initView() {

		usernameTxt = (TextView) findViewById(R.id.visitquery_customer);
		visitpersonTxt = (TextView) findViewById(R.id.visitquery_person);
		visitAimTxt = (TextView) findViewById(R.id.visitquery_aim);
		visitResultTxt = (TextView) findViewById(R.id.visitquery_result);
		photoImage = (ImageView) findViewById(R.id.visitquery_image);
		reachAddrTxt = (TextView) findViewById(R.id.txt_reach_addr);
		leaveAddrTxt = (TextView) findViewById(R.id.txt_leave_addr);
		startTxt = (TextView) findViewById(R.id.reachtime_text);
		endTxt = (TextView) findViewById(R.id.leavetime_text);

		questionTypeLl = (LinearLayout) findViewById(R.id.ll_question_type);
		questionLl = (LinearLayout) findViewById(R.id.ll_question);

		questionTypeTxt = (TextView) findViewById(R.id.txt_question_type);
		questionTxt = (TextView) findViewById(R.id.txt_question);

		progressBar = (ProgressBar) findViewById(R.id.photo_progressbar);
		
		photoImage.setOnClickListener(this);
		findViewById(R.id.txt_download).setOnClickListener(this);
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/visitlist3";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("id", id);
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code == 0) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
//									usernameTxt.setText(obj.getString("ccuscode"));
									usernameTxt.setText(obj.getString("ccusname"));
									visitpersonTxt.setText(obj.getString("visitperson"));
									visitAimTxt.setText(obj.getString("visitgoal"));
									visitResultTxt.setText(obj.getString("visitresult"));
									reachAddrTxt.setText(obj.getString("siadd"));
									leaveAddrTxt.setText(obj.getString("iadd"));
									startTxt.setText(obj.getString("starttime"));
									endTxt.setText(obj.getString("offtime"));
									String qtname = obj.getString("qtname");
									if (TextUtils.isEmpty(qtname)){
										questionTypeLl.setVisibility(View.GONE);
									}else{
										questionTypeLl.setVisibility(View.VISIBLE);
										questionTypeTxt.setText(qtname);
									}
									String qtdetails = obj.getString("qtdetails");
									if (TextUtils.isEmpty(qtdetails)){
										questionLl.setVisibility(View.GONE);
									}else{
										questionLl.setVisibility(View.VISIBLE);
										questionTxt.setText(qtdetails);
									}
									photo = User.mainurl + obj.getString("photo");
								}
							} else {
								Toast.makeText(VisitQueryActivity.this,
										"无拜访数据", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}

					@Override
					public void onFailure(Throwable throwable, String s) {
						super.onFailure(throwable, s);
						ProgressDialogUtil.closeProgressDialog();
					}
				});

	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {// run()在新的线程中运行
			try {
				Tools tools = new Tools();
				bitmap = tools.getBitMap(photo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(0);
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			photoImage.setImageBitmap(bitmap);
			if(photoImage !=null){
				progressBar.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.txt_download:
				progressBar.setVisibility(View.VISIBLE);
				mThread = new Thread(runnable);
				mThread.start();
				break;
			case R.id.visitquery_image:
				PhotoHelper.openPictureDialog_down(VisitQueryActivity.this, bitmap);
				break;
		}
	}
}