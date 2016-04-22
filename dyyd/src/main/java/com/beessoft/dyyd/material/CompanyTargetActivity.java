package com.beessoft.dyyd.material;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.User;

public class CompanyTargetActivity extends BaseActivity {

	private ProgressBar ProgressBar;
	private WebView mainWebView;
	private String url;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mywork_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_home:
			Toast.makeText(CompanyTargetActivity.this, "已返回顶层目录",
					Toast.LENGTH_SHORT).show();
			mainWebView.loadUrl(url);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled", "SdCardPath" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.companytarget);


		if ("notice".equals(getIntent().getStringExtra("from"))) {
			url = User.mainurl + getIntent().getStringExtra("url");
		} else {
			url = User.mainurl + "books.jsp?booktype=4&mac="
					+ GetInfo.getIMEI(this)+"&usercode="+GetInfo.getUserName(this);
		}

		mainWebView = (WebView) findViewById(R.id.material_web);
		// 加载进度条
		ProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		// webview定义参数
		WebSettings webSettings = mainWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDomStorageEnabled(true); // enable use of local storage
		webSettings.setGeolocationDatabasePath("/data/data/<my-app>");
		webSettings.setGeolocationEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//		webSettings.setAppCacheMaxSize(8 * 1024 * 1024);
		webSettings.setAllowFileAccess(true);
		webSettings.setSaveFormData(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		
		// 设置是否可缩放
//		webSettings.setLoadWithOverviewMode(true);
//		webSettings.setBuiltInZoomControls(true);
//		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。
//		mainWebView.setInitialScale(50);
		
		// 显示web
		mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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

		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
		// 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
		mainWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
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

	// 在handler里更新进度条
	@SuppressLint("HandlerLeak")
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
