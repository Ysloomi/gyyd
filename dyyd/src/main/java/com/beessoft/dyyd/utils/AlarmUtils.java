package com.beessoft.dyyd.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Alarm Tools
 * 
 * @Author dejaVu
 */
public class AlarmUtils {
	/**
	 * 开启闹钟
	 * 
	 * @param context
	 * @param cls
	 * @param action
	 * @param triggerAtTime
	 */

	public static void startAlarmService(Context context, Class<?> cls,
										 String action, long triggerAtTime) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
	}

	/**
	 * 关闭闹钟
	 * 
	 * @param context
	 * @param cls
	 * @param action
	 */

	public static void stopAlarmService(Context context, Class<?> cls,
										String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
	}

	/**
	 * 开启定时闹钟
	 * 
	 * @param context
	 * @param seconds
	 * @param cls
	 * @param action
	 */
	public static void startAlarmRepeatService(Context context, int seconds,
											   Class<?> cls, String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime();
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
				seconds * 1000, pendingIntent);
	}

	/**
	 * 关闭定时闹钟
	 * 
	 * @param context
	 * @param cls
	 * @param action
	 */
	public static void stopAlarmRepeatService(Context context, Class<?> cls,
											  String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
	}



	public static void doalarm(Context context) {
		// 定义闹钟参数
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent in = new Intent(context, AlarmReceiver.class);
		in.setAction("Test");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, in, 0);

		// 设置闹钟为循环模式，每两分钟触发一次
		am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
				1000 * 60, pi); // Millisec * Second * Minute
	}


	// 取消闹钟
	public static void cancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
