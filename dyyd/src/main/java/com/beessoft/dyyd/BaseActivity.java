package com.beessoft.dyyd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.beessoft.dyyd.utils.GetInfo;

public class BaseActivity extends Activity {
	public Context context;
	public String mac;
	public String username;

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.detail_actions, menu);
//		return super.onCreateOptionsMenu(menu);
//
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context= this;
		mac = GetInfo.getIMEI(context);
		username= GetInfo.getUserName(context);

		getActionBar().setDisplayHomeAsUpEnabled(true);// 使导航栏出现返回按钮
	}
}
