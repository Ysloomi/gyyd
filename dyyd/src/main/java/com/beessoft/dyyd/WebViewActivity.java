package com.beessoft.dyyd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.NetUtil;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;

public class WebViewActivity extends BaseActivity {

    private WebView mainWebView;
    private TextView noNetTextView;
    private TextView noNet1TextView;
    private ImageView noNetImage;
    private ImageView refreshImage;

    private String url;
    private Context context;

    WebSettings webSettings;

    private String latitude;
    private String longtitude;

    private LocationClient mLocationClient;
    private Thread mThread;

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类


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
                //                if (mainWebView.canGoBack())
//                {
//                    mainWebView.goBack(); // goBack()表示返回WebView的上一页面
//                }
//                else
//                {
                finish();
                //                }
                return true;
            case R.id.action_home:
                Toast.makeText(context, "已返回顶层目录", Toast.LENGTH_SHORT).show();
                mainWebView.loadUrl(url);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        context = WebViewActivity.this;
        url = getIntent().getStringExtra("url");
        // 声明百度定位sdk的构造函数
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
        String title = getIntent().getStringExtra("title");
        setTitle(title);

        initView();
        initWeb();

        getAddrLocation();
    }

    public void initView() {
        mainWebView = (WebView) findViewById(R.id.grouppurchase_web);
        noNetTextView = (TextView) findViewById(R.id.nonet_tt);
        noNet1TextView = (TextView) findViewById(R.id.nonet_tt1);
        noNetImage = (ImageView) findViewById(R.id.no_net);
        refreshImage = (ImageView) findViewById(R.id.refresh);

        refreshImage.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.refresh:
                    initWeb();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    public void initWeb() {
        if (NetUtil.checkNet(context)) {
            setWebVisible();


            // webview定义参数
            webSettings = mainWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // 启用缓存
//            webSettings.setAppCacheEnabled(true);
//            String appCacheDir = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
//            webSettings.setAppCachePath(appCacheDir);
//            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//无缓存，每次都重新加载界面

            // 启用数据库
            webSettings.setDatabaseEnabled(true);
            String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
            // 启用地理定位
            webSettings.setGeolocationEnabled(true);
            // 设置定位的数据库路径
            webSettings.setGeolocationDatabasePath(dir);
            // 最重要的方法，一定要设置，这就是出不来的主要原因
            webSettings.setDomStorageEnabled(true);

            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

            webSettings.setAllowFileAccess(true);
            webSettings.setSaveFormData(true);

//            webSettings.setBlockNetworkImage(true);//图片下载阻塞

            if (Build.VERSION.SDK_INT < 19) {
                webSettings.setPluginState(WebSettings.PluginState.ON);
            }
            // 设置是否可缩放
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//             webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。
//             mainWebView.setInitialScale(50);
            // 显示web
            mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            // 加载进度条
            mainWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    Message msg = new Message();
                    msg.what = 200;
                    msg.obj = newProgress;
                    // 进度条到100时，自动消失
                    if (newProgress >= 100) {
                        // progressBar.setVisibility(View.GONE);
                        ProgressDialogUtil.closeProgressDialog();
                    }
                }

                /**
                 * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
                 */
                @Override
                public boolean onJsAlert(WebView view, String url,
                                         String message, final JsResult result) {
                    AlertDialog builder = new AlertDialog.Builder(context)
                            .setMessage(message)
                            .setPositiveButton("确认", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.confirm();
                                }
                            }).show();
                    builder.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    });// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                    builder.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog,
                                             int keyCode, KeyEvent event) {
                            return true;
                        }
                    });
                    // 禁止响应按back键的事件
                    // builder.setCancelable(false);
                    return true;
                }


                /**
                 * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
                 */
                @Override
                public boolean onJsConfirm(WebView view, String url, String message,
                                           final JsResult result) {
                    AlertDialog builder = new AlertDialog.Builder(context)
                            .setMessage(message)
                            .setPositiveButton("确认", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.confirm();
                                }
                            }).show();
                    builder.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    });// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                    builder.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog,
                                             int keyCode, KeyEvent event) {
                            return true;
                        }
                    });
                    // 禁止响应按back键的事件
                    // builder.setCancelable(false);
                    return true;
                }

                /**
                 * 覆盖默认的window.prompt展示界面，避免title里显示为“：来自file:////”
                 * window.prompt(‘请输入您的域名地址’, ‘618119.com’);
                 */
                @Override
                public boolean onJsPrompt(WebView view, String url, String message,
                                          String defaultValue, final JsPromptResult result) {
                    AlertDialog builder = new AlertDialog.Builder(context)
                            .setMessage(message)
                            .setPositiveButton("确认", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.confirm();
                                }
                            }).show();
                    builder.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    });// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                    builder.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog,
                                             int keyCode, KeyEvent event) {
                            return true;
                        }
                    });
                    // 禁止响应按back键的事件
                    // builder.setCancelable(false);
                    return true;
                }


                // 配置权限（同样在WebChromeClient中实现）
                public void onGeolocationPermissionsShowPrompt(String origin,
                                                               GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                }
            });
            mainWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "js2java");
            mainWebView.setWebViewClient(new MyWebViewClient());
            mainWebView.setDownloadListener(new MyWebViewDownLoadListener());
//			mainWebView.requestFocusFromTouch();
//			mainWebView.requestFocus(View.FOCUS_DOWN);
            mainWebView.loadUrl(url);

        } else {
            setWebGone();
        }
    }

    public void setWebGone() {
        ProgressDialogUtil.closeProgressDialog();
        mainWebView.setVisibility(View.GONE);
        noNetTextView.setVisibility(View.VISIBLE);
        noNet1TextView.setVisibility(View.VISIBLE);
        noNetImage.setVisibility(View.VISIBLE);
        refreshImage.setVisibility(View.VISIBLE);
    }

    public void setWebVisible() {

        ProgressDialogUtil.showProgressDialog(context);
        mainWebView.setVisibility(View.VISIBLE);
        noNetTextView.setVisibility(View.GONE);
        noNet1TextView.setVisibility(View.GONE);
        noNetImage.setVisibility(View.GONE);
        refreshImage.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        if (mainWebView.canGoBack())
            mainWebView.goBack(); // goBack()表示返回WebView的上一页面
        else
            super.onBackPressed();
    }

    final class MyWebViewClient extends WebViewClient {
        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            setWebGone();
            try {
                mainWebView.stopLoading();
            } catch (Exception e) {
            }
            try {
                mainWebView.clearView();
            } catch (Exception e) {
            }
            if (mainWebView.canGoBack()) {
                mainWebView.goBack();
            }
            mainWebView.loadUrl(url);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        public void onPageFinished(WebView view, String url) {
//            webSettings.setBlockNetworkImage(false);
            ProgressDialogUtil.closeProgressDialog();
            super.onPageFinished(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          String url) {
            if (url.startsWith("http") || url.startsWith("https")) {
                return super.shouldInterceptRequest(view, url);
            } else if (url.startsWith("mqqwpa")) {
                try {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.toast(context, "请安装qq，再点击使用");
                }
                return super.shouldInterceptRequest(view, url);
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void map(String lat, String lng) {
//            Intent intent = new Intent();
//            intent.setClass(context, RoutePlanActivity.class);
//            intent.putExtra("jd", lng);
//            intent.putExtra("wd", lat);
//            intent.putExtra("jdnow", longtitude + "");
//            intent.putExtra("wdnow", latitude + "");
//            startActivity(intent);
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

//    @Override
//    protected void onPause() {
//        try {
//            mainWebView.getClass().getMethod("onPause").invoke(mainWebView, (Object[]) null);
//        } catch (IllegalAccessException | IllegalArgumentException
//                | InvocationTargetException | NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        XGPushManager.onActivityStoped(this);
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
////		Log.d("TPush", "onResumeXGPushClickedResult:" + click);
//        if (click != null) { // 判断是否来自信鸽的打开方式
////			Toast.makeText(this, "通知被点击:" + click.toString(),
////					Toast.LENGTH_SHORT).show();
//
//            try {
//                String customContent = click.getCustomContent();
//                JSONObject obj = new JSONObject(customContent);
//                url = obj.getString("url");
////                Logger.e("url>>>" + url);
//                mainWebView.loadUrl(url);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        super.onResume();
//    }

    public void getAddrLocation() {
        mThread = new Thread(runnable);
        if (Gps.exist(context, "distance.db")) {
            distanceHelper = new DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
            longtitude = Gps.getJd(distanceHelper);
            latitude = Gps.getWd(distanceHelper);
            distanceHelper.close();
            mThread.start();// 线程启动
        } else {
            Gps.GPS_do(mLocationClient, 1100);
            mThread.start();// 线程启动
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {// run()在新的线程中运行
            int sleepcount = 1500;
            if (!Gps.exist(context, "distance.db")) {// 未签到时，延长定位时间，以便获取到更准确的定位方式
                sleepcount = 3300;
            }
            try {
                Thread.sleep(sleepcount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Gps.exist(context, "distance.db")) {
                LocationApplication myApp = (LocationApplication) getApplication();
                longtitude = myApp.getJd();
                latitude = myApp.getWd();
                // 未签到时，关闭location服务
                mLocationClient.stop();
            }
        }
    };
}
