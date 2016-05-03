package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressLint("NewApi")
public class PhotoQueryActivity extends BaseActivity {

	private TextView textView1, textView2, textView3, textView4;
	private String mac, idTarget, photo;
	private ImageView imageView;
	// // 创建Bitmap对象
	private Bitmap bitmap;

	private Thread mThread;
	private ProgressBar progressBar;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoquery);

		textView1 = (TextView) findViewById(R.id.location_text);
		textView2 = (TextView) findViewById(R.id.phototype_text);
		textView3 = (TextView) findViewById(R.id.context_text);
		textView4 = (TextView) findViewById(R.id.date_text);

		imageView = (ImageView) findViewById(R.id.photo_image);
		progressBar = (ProgressBar) findViewById(R.id.photo_progressbar);

		mac = GetInfo.getIMEI(PhotoQueryActivity.this);
		idTarget = getIntent().getStringExtra("idTarget");

		visitServer(PhotoQueryActivity.this);

		imageView.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PhotoHelper.openPictureDialog_down(PhotoQueryActivity.this,
						bitmap);
			}
		});
	}

	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/imglist3";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");
		parameters_userInfo.put("id", idTarget);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);

							if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									textView1.setText(obj.getString("iadd"));
									textView2.setText(obj.getString("imgtype"));
									textView3.setText(obj.getString("context"));
									textView4.setText(obj.getString("itime"));

									photo = User.mainurl + obj.getString("imgfile");
									mThread = new Thread(runnable);
									mThread.start();
								}
							} else if ("1".equals(dataJson.getString("code"))) {
								Toast.makeText(PhotoQueryActivity.this, "没有数据",
										Toast.LENGTH_SHORT).show();
							} else if ("-2".equals(dataJson.getString("code"))) {
								Toast.makeText(PhotoQueryActivity.this, "无权限",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
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
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			imageView.setImageBitmap(bitmap);
			if (imageView != null) {
				progressBar.setVisibility(View.GONE);
			}
		}
	};
}
