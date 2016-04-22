package com.beessoft.dyyd.utils;

import java.io.File;

import android.content.Context;

public class DatabaseUtil {

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
