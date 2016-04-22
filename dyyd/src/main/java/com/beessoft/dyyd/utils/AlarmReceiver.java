package com.beessoft.dyyd.utils;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
	private Context context;
	@SuppressLint("Wakelock")
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		if ((wl != null) && // we have a WakeLock
				(wl.isHeld() == false)) { // but we don't hold it
			wl.acquire();
		}
		// Location Userlist = (Location)context.getApplicationContext();
		// connManager = (ConnectivityManager)
		// this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// if (iflag){ //iflag
		// NetworkManager iNetworkManager = new NetworkManager(context);
		// if (!iNetworkManager.isNetworkConnected()){ //判断是否有网络连接，没有则先开启wifi
		// try {
		// iNetworkManager.toggleWiFi(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }

		// 4.0之后失效
		// if (!iNetworkManager.isMobileConnected()){ //判断数据是否未开启，没有则开启gps
		// try {
		// iNetworkManager.toggleGprs(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// Log.i("toggleGprs", "toggleGprs: ,isNet:" +
		// iNetworkManager.isNetworkConnected() + ",isWifi:" +
		// iNetworkManager.isWifiConnected() + ",isMObi:" +
		// iNetworkManager.isMobileConnected());
		// //Toast.makeText(context,"toggleGprs: ,isNet:" +
		// iNetworkManager.isNetworkConnected() + ",isWifi:" +
		// iNetworkManager.isWifiConnected() + ",isMObi:" +
		// iNetworkManager.isMobileConnected(), Toast.LENGTH_SHORT).show();
		// }
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// Log.v("=========", "***** beeService *****: start:");

		// Userlist.setString("0");
		// if(Userlist.getLocaSuccess().equals("1")){
		// Userlist.setLocaSuccess("1");
		// }

		// //启动定位//有没有网络都定位
		// if(exist("area.db")){
		// LocationClient mLocClient = Userlist.getLocationClient();
		// try{
		// mLocClient.stop();
		// Gps.setLocationOption(mLocClient,300); //1000毫秒*60*2=2分钟
		// mLocClient.start();
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// Log.v("=========", "***** beeService *****: stop:");
		// }
		// wl.release();
		// iNetworkManager = null;
		// 启动定位//有没有网络都定位
		LocationClient mLocationClient = ((LocationApplication) context.getApplicationContext()).mLocationClient;
		if (Gps.exist(context, "distance.db") && !mLocationClient.isStarted()) {
			try {
				mLocationClient.stop();
				Gps.GPS_do(mLocationClient, 8000); // 1000毫秒*80=8000
				mLocationClient.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.v("=========", "***** beeService *****: stop:");
		}
		wl.release();
	}

	@SuppressLint("SimpleDateFormat")
	public static int compare_date(String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // hh小写，12小时制，HH大写，24小时制
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		Date nowtime = new Date();
		DATE2 = df2.format(nowtime) + " " + DATE2;
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
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
