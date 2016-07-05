package com.beessoft.dyyd.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;


public class User {

	public static final String mainurl = "http://wq-app.dyg3.com:8000/wqapp/";
//	public static final String mainurl = "http://jingongoa.ticp.net:8001/dy10086/";
//	public static final String mainurl = "http://hwq-app.dyg3.com:8001/";//邮政银行
//	public static final String mainurl = "http://223.86.31.75:8000/dy10086/";
//	public static final String mainurl = "http://192.168.199.157:8080/dy10086/";
//	public static final String dyMainurl = "http://wq-app.dyg3.com:8000/wqapp/";
	public static final String version = "中国移动德阳公司  v";
	public static final String xml = "upapk/dyydversion.xml";

//	public static final String mainurl = "http://121.40.80.201/sunhome/";
//	public static final String version = "成都大黄蜂信息技术有限公司  v";
//	public static final String xml = "upapk/sjversion.xml";

	/**
	 * 获取软件名称
	 * @param context
	 * @return
	 */
	public static final String getVersionName(Context context) {
		String VerName = "";
		try {
			VerName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return VerName;
	}

	/**
	 * 获取软件版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
}
