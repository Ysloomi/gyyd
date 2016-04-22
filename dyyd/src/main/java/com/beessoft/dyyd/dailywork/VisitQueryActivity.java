package com.beessoft.dyyd.dailywork;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
		case android.R.id.home:
			finish();
			return true;
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
		setContentView(R.layout.visitquery);
		

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
		
		//使textview可以滚动
		textView4.setMovementMethod(ScrollingMovementMethod.getInstance());

		SharedPreferences sharedPre = getSharedPreferences("idVisit",
				MODE_PRIVATE);
		id = sharedPre.getString("id", "");
		mac= GetInfo.getIMEI(this);
		
		visitServer(VisitQueryActivity.this);
		
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

	// 访问服务器http post
	private void visitServer(Context context) {
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
									textView1.setText(new String(obj
											.getString("ccuscode")));
									textView2.setText(new String(obj
											.getString("visitperson")));
									textView3.setText(new String(obj
											.getString("visitgoal")));
									textView4.setText(new String(obj
											.getString("visitresult")));
									textView5.setText(new String(obj
											.getString("iadd")));
									textView6.setText(new String("到达时间:"
											+ obj.getString("starttime")));
									textView7.setText(new String("离开时间:"
											+ obj.getString("offtime")));
									textView8.setText(new String(obj
											.getString("siadd")));
									photo = User.mainurl
											+ obj.getString("photo");
								}
							} else {
								Toast.makeText(VisitQueryActivity.this,
										"无拜访数据", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
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