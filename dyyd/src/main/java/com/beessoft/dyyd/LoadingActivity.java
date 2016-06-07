package com.beessoft.dyyd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.beessoft.dyyd.utils.User;
import com.igexin.sdk.PushManager;

import java.util.TimerTask;

public class LoadingActivity extends Activity {

	private TextView versionText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			//完美解决：APP下载安装后，点击“直接打开”，启动应用后，按下HOME键，再次点击桌面上的应用，会重启一个新的应用问题
			finish();
			return;
		}

		PushManager.getInstance().initialize(this.getApplicationContext());

		versionText = (TextView) findViewById(R.id.version_text);

		versionText.setText(User.version + User.getVersionName(this));

		Handler handler = new Handler();
		TimerTask task = new TimerTask() {
			public void run() {
				startActivity(new Intent(LoadingActivity.this,
						LoginActivity.class));
			}
		};
		// 2秒后执行TimerTask任务
		handler.postDelayed(task, 2000);
	}
}
