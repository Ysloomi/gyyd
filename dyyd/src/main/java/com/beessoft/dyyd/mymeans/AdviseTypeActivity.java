package com.beessoft.dyyd.mymeans;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;

public class AdviseTypeActivity extends BaseActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.advise_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_advise:
			Intent intent = new Intent(this, AdviseActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkquery);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// 设置为Tab模式

		initView();
	}

	private void initView() {
		// 得到Activity的ActionBar
		ActionBar bar = getActionBar();
		// 设置为Tab模式
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// 新建2个Tab
		ActionBar.Tab tabA = bar.newTab().setText("所有问题");
		ActionBar.Tab tabB = bar.newTab().setText("我的问题");
		// 绑定到Fragment
		Fragment alladvise = new AllAdviseFragment();
		Fragment myadvise = new MyAdviseFragment();
		tabA.setTabListener(new MyTabsListener(alladvise));
		tabB.setTabListener(new MyTabsListener(myadvise));
		bar.addTab(tabA);
		bar.addTab(tabB);

	}

	protected class MyTabsListener implements ActionBar.TabListener {
		private Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		// 当Tab被选中的时候添加对应的Fragment
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// fragment = Fragment.instantiate(mActivity, mClass.getName());
			ft.add(R.id.fragment_place, fragment, null);
			// ft.attach(fragment);
		}

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction ft) {
		}

		// 当Tab没被选中的时候删除对应的此Tab对应的Fragment
		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction ft) {
			ft.remove(fragment);
		}
	}
}
