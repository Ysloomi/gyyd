package com.beessoft.dyyd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DistanceDatabaseHelper extends SQLiteOpenHelper {

	final String SQL_CREATE_TABLE = "create table distance_table (" +
			"_id integer primary key autoincrement, " +
			"time_text varchar(5000),"+
			"jd_text varchar(5000),"+
			"wd_text varchar(5000),"+
			"addr_text varchar(5000),"+
			"distance_text varchar(5000),"+
			"totaldistance_text varchar(5000),"+
			"type_text varchar(5000),"+
			"iflag_text varchar(50)," +
			"speed double)";
	
	/*
	 * 构造方法 : 
	 * 参数介绍 : 
	 * 参数① : 上下文对象
	 * 参数② : 数据库名称
	 * 参数③ : 数据库版本号
	 */
	public DistanceDatabaseHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		System.out.println("call update");
		db.execSQL("DROP TABLE IF EXISTS distance_table");  
        onCreate(db);  
	}

	
	private void drop(SQLiteDatabase db){          
	     //删除表的SQL语句        
	         String sql ="DROP TABLE distance_table";           
	    //执行SQL       
	    db.execSQL(sql); 
	}  
}
