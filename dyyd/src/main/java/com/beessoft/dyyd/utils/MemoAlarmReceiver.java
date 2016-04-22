package com.beessoft.dyyd.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MemoAlarmReceiver extends BroadcastReceiver {
	private Context context;

	@SuppressLint("Wakelock")
	public void onReceive(final Context context, Intent intent) {

		this.context = context;

		// 显示对话框
		new AlertDialog.Builder(context).setTitle("闹钟").// 设置标题
				setMessage("时间到了！").// 设置内容
				setPositiveButton("知道了", null).create().show();
	}
}
