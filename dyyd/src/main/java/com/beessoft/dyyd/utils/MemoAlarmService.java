package com.beessoft.dyyd.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.beessoft.dyyd.R;

/**
 * MemoAlarmService
 * 
 * @Author dejaVu
 * 
 */
public class MemoAlarmService extends Service {

	public static final String ACTION = "tscolari.mobile_sample.service.MemoAlarmService";

	private Notification mNotification;
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
	}

	@Override
	public void onStart(Intent intent, int startId) {

//		showNotification();
		// new PollingThread().start();
	}

	private void initNotifiManager() {

		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.login_logo1;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "备忘录提醒";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

//	@SuppressWarnings("deprecation")
//	private void showNotification() {
//		mNotification.when = System.currentTimeMillis();
//		// Navigator to the new activity when click the notification title
//		Intent i = new Intent(this, MyMemoListActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//				Intent.FLAG_ACTIVITY_NEW_TASK);
//		mNotification.setLatestEventInfo(this,
//				getResources().getString(R.string.app_name), "有一条备忘录",
//				pendingIntent);
//		mManager.notify(0, mNotification);
//	}

	// /**
	// * Polling thread
	// *
	// * @Author Ryan
	// * @Create 2013-7-13 上午10:18:34
	// */
	// int count = 0;
	//
	// class PollingThread extends Thread {
	// @Override
	// public void run() {
	// // System.out.println("Polling...");
	// count++;
	// if (count % 5 == 0) {
	// showNotification();
	// System.out.println("New message!");
	// }
	// }
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
