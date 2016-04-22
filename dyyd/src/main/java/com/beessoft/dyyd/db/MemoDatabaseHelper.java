package com.beessoft.dyyd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoDatabaseHelper extends SQLiteOpenHelper {

	final String SQL_CREATE_TABLE = "create table memo_table (" +
			"_id integer primary key autoincrement, " +
			"time_text varchar(5000),"+
			"memo_text varchar(5000))";
	
	/*
	 * 构造方法 : 
	 * 参数介绍 : 
	 * 参数① : 上下文对象
	 * 参数② : 数据库名称
	 * 参数③ : 数据库版本号
	 */
	public MemoDatabaseHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS memo_table");  
        onCreate(db);  
	}

	
	private void drop(SQLiteDatabase db){          
	     //删除表的SQL语句        
	         String sql ="DROP TABLE distance_table";           
	    //执行SQL       
	    db.execSQL(sql); 
	}  
}
