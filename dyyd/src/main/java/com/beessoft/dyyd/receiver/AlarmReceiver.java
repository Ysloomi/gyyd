package com.beessoft.dyyd.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.utils.Gps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
	private Context context;

	// private ConnectivityManager connManager;
	// private Intent eventIn;
	@SuppressLint("Wakelock")
	public void onReceive(Context context, Intent intent) {
		// Log.v("=========", "***** beeService *****: AlarmReceiver1");

		this.context = context;

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//		WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "");
		if ((wl != null) && // we have a WakeLock
				(wl.isHeld() == false)) { // but we don't hold it
			wl.acquire();
		}

//		PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
//		SCREEN_DIM_WAKE_LOCK：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
//		SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
//		FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
//		ACQUIRE_CAUSES_WAKEUP：正常唤醒锁实际上并不打开照明。相反，一旦打开他们会一直仍然保持(例如来世user的activity)。
//      当获得wakelock，这个标志会使屏幕或/和键盘立即打开。一个典型的使用就是可以立即看到那些对用户重要的通知。
//		ON_AFTER_RELEASE：设置了这个标志，当wakelock释放时用户activity计时器会被重置，导致照明持续一段时间。
//      如果你在wacklock条件中循环，这个可以用来减少闪烁

		// 启动定位//有没有网络都定位
		LocationClient mLocationClient = ((LocationApplication) context
				.getApplicationContext()).mLocationClient;
		Log.v("=========",
				"***** beeService *****: if:" + !mLocationClient.isStarted());
		if (Gps.exist(context, "distance.db") && !mLocationClient.isStarted()) {
			try {
				mLocationClient.stop();
				Gps.GPS_do(mLocationClient, 8000); // 1000毫秒*80=8000
				mLocationClient.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Log.v("=========", "***** beeService *****: stop:");
		}
		wl.release();
	}

	@SuppressLint("SimpleDateFormat")
	public static int compare_date(String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // hh小写，12小时制，HH大写，24小时制
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		Date nowtime = new Date();
		DATE2 = df2.format(nowtime) + " " + DATE2;
		// System.out.println("DATE2:" + DATE2 + "," + df.format(nowtime));
		try {
			Date dt1 = df.parse(df.format(nowtime));
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				// System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				// System.out.println("dt1在dt2后");
				return -1;
			} else {
				// System.out.println("dt1 = dt2");
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public void SetAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				System.currentTimeMillis(), 1000 * 60, pi); // Millisec * Second
															// * Minute
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
