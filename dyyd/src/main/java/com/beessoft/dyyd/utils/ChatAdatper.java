package com.beessoft.dyyd.utils;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ChatAdatper extends FragmentStatePagerAdapter {
	ArrayList<Fragment> list;

	public ChatAdatper(FragmentManager fm) {
		super(fm);
	}

	public ChatAdatper(FragmentManager fm,ArrayList<Fragment> list) {
		super(fm);
		this.list=list;
	}


	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
}
