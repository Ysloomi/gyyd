package com.beessoft.dyyd.model;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class GetWebView {
	
	private ProgressBar  progressBar;
	

	/**
	 * @return the progressBar
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @param progressBar the progressBar to set
	 */
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void initWebview(WebView webView,String url) {
		// webview定义参数
		WebSettings webSettings = webView.getSettings();
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
		// 设置是否可缩放
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		// 显示web
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Message msg = new Message();
				msg.what = 200;
				msg.obj = newProgress;
				handler.sendMessage(msg);
				// 进度条到100时，自动消失
				if (newProgress >= 100) {
					progressBar.setVisibility(View.GONE);
				}
			}
		});
		webView.loadUrl(url);

		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
		// 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
		webView.setWebViewClient(new WebViewClient() {
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
				progressBar.setProgress(progress);
				break;

			default:
				break;
			}
		};
	};
}
