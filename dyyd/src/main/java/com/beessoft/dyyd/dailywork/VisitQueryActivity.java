package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class VisitQueryActivity extends BaseActivity {

	private TextView textView1, textView2, textView3, textView4, textView5,
			textView6, textView7, textView8;
	private ImageView imageView;
	private String id, photo,mac;
	private Bitmap bitmap;
	private Thread mThread;
	private ProgressBar progressBar;
	private Button button;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.read_actions, menu);
		return super.onCreateOptionsMenu(menu);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_read:
			Intent intent =new Intent(this,VisitReadActivity.class);
			intent.putExtra("idTarget", id);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visitquery);

		context = VisitQueryActivity.this;
		mac= GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		initView();
		
		//使textview可以滚动
		textView4.setMovementMethod(ScrollingMovementMethod.getInstance());

		id = getIntent().getStringExtra("id");


		ProgressDialogUtil.showProgressDialog(context);
		visitServer();
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				mThread = new Thread(runnable);
				mThread.start();
			}
		});
		imageView.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoHelper.openPictureDialog_down(VisitQueryActivity.this, bitmap);
			}
		});
	}

	private void initView() {
		textView1 = (TextView) findViewById(R.id.visitquery_customer);
		textView2 = (TextView) findViewById(R.id.visitquery_person);
		textView3 = (TextView) findViewById(R.id.visitquery_aim);
		textView4 = (TextView) findViewById(R.id.visitquery_result);
		imageView = (ImageView) findViewById(R.id.visitquery_image);
		textView5 = (TextView) findViewById(R.id.location_text);
		textView6 = (TextView) findViewById(R.id.reachtime_text);
		textView7 = (TextView) findViewById(R.id.leavetime_text);
		textView8 = (TextView) findViewById(R.id.reachlocation_text);

		button = (Button) findViewById(R.id.download_image);
		progressBar = (ProgressBar) findViewById(R.id.photo_progressbar);
	}

	private void visitServer() {
		String httpUrl = User.mainurl + "sf/visitlist3";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("id", id);
		parameters_userInfo.put("mac", mac);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
//						System.out.println("response:" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									textView1.setText(obj.getString("ccuscode"));
									textView2.setText(obj.getString("visitperson"));
									textView3.setText(obj.getString("visitgoal"));
									textView4.setText(obj.getString("visitresult"));
									textView5.setText(obj.getString("iadd"));
									textView6.setText("到达时间:" + obj.getString("starttime"));
									textView7.setText("离开时间:" + obj.getString("offtime"));
									textView8.setText(obj.getString("siadd"));
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
			imageView.setImageBitmap(bitmap);
			if(imageView!=null){
				progressBar.setVisibility(View.GONE);
			}
		}
	};
}