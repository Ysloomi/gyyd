package com.beessoft.dyyd.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * 判断当前手机联网的渠道
 * 
 * @author dejaVu
 * 
 */
public class NetUtil {
	/**
	 * 检查当前手机网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {
		// 判断连接方式
		boolean wifiConnected = isWIFIConnected(context);
		boolean mobileConnected = isMOBILEConnected(context);
		if (wifiConnected == false && mobileConnected == false) {
			// 如果都没有连接返回false，提示用户当前没有网络
			return false;
		}
		return true;
	}

	// 判断手机使用是wifi还是mobile
	/**
	 * 判断手机是否采用wifi连接
	 */
	public static boolean isWIFIConnected(Context context) {
		// Context.CONNECTIVITY_SERVICE).

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static boolean isMOBILEConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/***
	 * 获取网关IP地址
	 * 
	 * @return
	 */
	public static String getHostIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return null;
	}

	public static String getLocalIpAddress() {
		try {
			String ipv4;
			ArrayList<NetworkInterface> nilist = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist) {
				ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
				for (InetAddress address : ialist) {
					final String ipAddress = address.getHostAddress();
					if (!address.isLoopbackAddress()
							&& address instanceof Inet4Address
//							&& InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())
					) {
						return ipAddress;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("netutil", ex.toString());
		}
		return null;
	}
}
