package com.beessoft.dyyd;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ShareActivity extends BaseActivity {
	private Button button;
	private ProgressBar ProgressBar;
	private WebView mainWebView;
	private EditText editText1;
	private String url = User.mainurl + "qrcode.jsp";
	private String message;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled", "SdCardPath" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharepage);

		mainWebView = (WebView) findViewById(R.id.material_web);
		editText1 = (EditText) findViewById(R.id.phonenum_text);
		button = (Button) findViewById(R.id.share_button);

		// webview定义参数
		WebSettings webSettings = mainWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDomStorageEnabled(true); // enable use of local storage
		webSettings.setGeolocationDatabasePath("/data/data/<my-app>");
		webSettings.setGeolocationEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAppCacheMaxSize(8 * 1024 * 1024);
		webSettings.setAllowFileAccess(true);
		webSettings.setSaveFormData(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		// 显示web
		mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// 加载进度条
		ProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		mainWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Message msg = new Message();
				msg.what = 200;
				msg.obj = newProgress;
				handler.sendMessage(msg);
				// 进度条到100时，自动消失
				if (newProgress >= 100) {
					ProgressBar.setVisibility(View.GONE);
				}
			}
		});

		mainWebView.loadUrl(url);
		visitServer(this);

		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
		// 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
		mainWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String phonenum = editText1.getText().toString();
				Uri uri = Uri.parse("smsto:" + phonenum);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra("sms_body", message);
				startActivity(intent);
			}
		});
	}

	// @Override
	// //设置回退
	// //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && mainWebView.canGoBack()) {
	// mainWebView.goBack(); //goBack()表示返回WebView的上一页面
	// return true;
	// }
	// return false;
	// }

	// 访问服务器http post
	private void visitServer(Context context) {
		String httpUrl = User.mainurl + "sf/share";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		// parameters_userInfo.put("mac", MacAddr);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							message = dataJson.getString("url")
									+ dataJson.getString("ps");
							;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	// 在handler里更新进度条
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 200:
				int progress = (Integer) msg.obj;
				ProgressBar.setProgress(progress);
				break;

			default:
				break;
			}
		};
	};
}
