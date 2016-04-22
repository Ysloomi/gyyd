package com.beessoft.dyyd.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class Gps {
	private Context context;
	private String addr ;
	
	public Gps(Context context) {
		super();
		this.context = context;
	}

	// // //设置相关参数
	// public static void setLocationOption(LocationClient mLocClient,int
	// mSpan){
	// LocationClientOption option = new LocationClientOption();
	// //option.setOpenGps(mGpsCheck.isChecked()); //打开gps
	// option.setOpenGps(true);
	// //option.setCoorType(mCoorEdit.getText().toString()); //设置坐标类型
	// option.setCoorType("bd09ll");
	// option.setServiceName("com.baidu.location.service_v2.9");
	// //option.setPoiExtraInfo(mIsAddrInfoCheck.isChecked());
	// option.setPoiExtraInfo(true);
	//
	// option.setAddrType("all");
	//
	// //String mSpanEditTxt = "1000";
	// option.setScanSpan(mSpan); //间隔扫描时间，低于1秒钟，一次性定位，高于1秒钟，按频率定位
	//
	// boolean mPriorityCheckflag = false;
	// if(mPriorityCheckflag)
	// {
	// option.setPriority(LocationClientOption.NetWorkFirst); //设置网络优先
	// }
	// else
	// {
	// option.setPriority(LocationClientOption.GpsFirst); //不设置，默认是gps优先
	// }
	//
	// option.setPoiNumber(10);
	// option.disableCache(true);
	// mLocClient.setLocOption(option);
	//
	// }
	public void openGPSSettings(Context context) {
		LocationManager alm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//			Toast.makeText(context, "GPS已打开", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "请打开GPS", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(intent);
		}
	}

	public static void GPS_do(LocationClient mLocationClient, int span) {// 定位
		// if (onbtn){ //如果通过定位按钮调用，则显示提示信息
		// Toast.makeText(CheckInActivity.this, "正在定位,请稍侯... ...",
		// Toast.LENGTH_SHORT).show();
		// }
		// //启动定位
		// Location myApp = (Location) getApplication();
		// LocationClient mLocClient = myApp.getLocationClient();
		// mLocClient.stop();
		// Gps.setLocationOption(mLocClient,300); //1000毫秒*60*2=2分钟
		// mLocClient.start();
		// 启动定位
		// Location myApp = (Location) getApplication();
		// LocationClient mLocClient = myApp.getLocationClient();
		// mLocationClient =
		// ((Location)getContext().getApplication()).mLocationClient;

		mLocationClient.stop();
		InitLocation(mLocationClient, span);
		mLocationClient.start();
	}

	public static void InitLocation(LocationClient mLocationClient, int span) {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		// int span=1000;
		// try {
		// span = Integer.valueOf(frequence.getText().toString());
		// } catch (Exception e) {
		// }
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocationClient.setLocOption(option);
	}

	public static String getJd(SQLiteOpenHelper sQLiteOpenHelper) {
		// DistanceDatabaseHelper distanceHelper = new
		// DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
		Cursor cursor = sQLiteOpenHelper.getReadableDatabase().rawQuery(
				"select * from distance_table ", null);
		String jd = "";
		try {
			if (cursor.moveToLast() == true) {
//				String id = cursor.getString(0);
				jd = cursor.getString(2); // 获取第一列的值,第一列的索引从0开始
//				System.out.println("getJd:" +id+ jd);
			}
		} catch (Exception e) {
			System.out.println("getJd异常：");
			e.printStackTrace();
		} finally {
			cursor.close();
			sQLiteOpenHelper.close();
		}
		return jd;
	}

	public static String getWd(SQLiteOpenHelper sQLiteOpenHelper) {
		// DistanceDatabaseHelper distanceHelper = new
		// DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
		Cursor cursor = sQLiteOpenHelper.getReadableDatabase().rawQuery(
				"select * from distance_table ", null);
		String wd = "";
		try {
			if (cursor.moveToLast() == true) {
				String id = cursor.getString(0);
				wd = cursor.getString(3); // 获取第一列的值,第一列的索引从0开始
//				System.out.println("getWd:" + id+ wd);
			}
		} catch (Exception e) {
			System.out.println("getWd异常：");
			e.printStackTrace();
		} finally {
			cursor.close();
			sQLiteOpenHelper.close();
		}
		return wd;
	}

	public static String getAddr(SQLiteOpenHelper sQLiteOpenHelper) {
		// DistanceDatabaseHelper distanceHelper = new
		// DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
		Cursor cursor = sQLiteOpenHelper.getReadableDatabase().rawQuery(
				"select * from distance_table ", null);
		String addr = "";
		try {
			if (cursor.moveToLast() == true) {
//				String id = cursor.getString(0);
				addr = cursor.getString(4); // 获取第一列的值,第一列的索引从0开始
//				System.out.println("getAddr:" + id + addr);
			}
		} catch (Exception e) {
			System.out.println("getAddr异常：");
			e.printStackTrace();
		} finally {
			cursor.close();
			sQLiteOpenHelper.close();
		}
		return addr;
	}

	public static String getType(SQLiteOpenHelper sQLiteOpenHelper) {
		// DistanceDatabaseHelper distanceHelper = new
		// DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
		Cursor cursor = sQLiteOpenHelper.getReadableDatabase().rawQuery(
				"select * from distance_table ", null);
		String type = "";
		try {
			if (cursor.moveToLast() == true) {
//				String id = cursor.getString(0);
				type = cursor.getString(7); // 获取第一列的值,第一列的索引从0开始
//				System.out.println("getType:" + id + type);
			}
		} catch (Exception e) {
			System.out.println("getType异常：");
			e.printStackTrace();
		} finally {
			cursor.close();
			sQLiteOpenHelper.close();
		}
		return type;
	}

	// 判断
	public static boolean exist(Context context, String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		if (dbFile.exists() == true) {
			return true;
		} else {
			return false;
		}
	}
}
