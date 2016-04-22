package com.beessoft.dyyd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdviseDBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "sfyd_advise";

	public AdviseDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * customerclass，customer
		 */
		String sql = "create table tb_advise( _id integer primary key autoincrement , "
				+ "advisetype text);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
