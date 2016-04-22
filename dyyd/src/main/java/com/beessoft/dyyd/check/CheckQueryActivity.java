package com.beessoft.dyyd.check;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;

public class CheckQueryActivity extends BaseActivity {

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
		ActionBar.Tab tabA = bar.newTab().setText("我的考勤");
		ActionBar.Tab tabB = bar.newTab().setText("下属考勤");
		// 绑定到Fragment
		Fragment mycheck = new MyCheckFragment();
		Fragment subcheck = new SubordinateCheckFragment();
		tabA.setTabListener(new MyTabsListener(mycheck));
		tabB.setTabListener(new MyTabsListener(subcheck));
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
//			ft.attach(fragment);
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
