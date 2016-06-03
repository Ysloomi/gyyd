package com.beessoft.dyyd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SfydOpenHelper extends SQLiteOpenHelper {

	public static final String CREATE_PROVINCE = "create table Check_table ("
			+ "id integer primary key autoincrement, "
			+ "check_id text, "
			+ "check_code text, "
			+ "check_project_code text, "
			+ "check_shop_code text, "
			+ "check_name text, "
			+ "check_remarks text, "
			+ "check_begin text, "
			+ "check_end text, "
			+ "check_modelPhoto text, "
			+ "check_jd text, "
			+ "check_wd text, "
			+ "check_addr text, "
			+ "check_photo text, "
			+ "check_result text)";

	public SfydOpenHelper(Context context, String name, CursorFactory factory,
						  int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);  // 创建检查表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}