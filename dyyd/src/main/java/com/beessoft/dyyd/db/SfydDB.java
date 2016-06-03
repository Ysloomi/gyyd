package com.beessoft.dyyd.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beessoft.dyyd.bean.Special;

import java.util.ArrayList;
import java.util.List;

public class SfydDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "sfyd";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;

	private static SfydDB sfydDB;

	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private SfydDB(Context context) {
		SfydOpenHelper dbHelper = new SfydOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例。
	 */
	public synchronized static SfydDB getInstance(Context context) {
		if (sfydDB == null) {
			sfydDB = new SfydDB(context);
		}
		return sfydDB;
	}

	/**
	 * 将Check实例存储到数据库。
	 */
	public int saveCheck(Special special) {
		int result = 0;
		try {
			if (special != null) {
				ContentValues values = new ContentValues();
				values.put("check_id", special.getId());
				values.put("check_code", special.getSubjectId());
				values.put("check_project_code", special.getProjectId());
				values.put("check_shop_code", special.getShopId());
				values.put("check_name", special.getName());
				values.put("check_remarks", special.getRemarks());
				values.put("check_begin", special.getBegin());
				values.put("check_end", special.getEnd());
				values.put("check_modelPhoto", special.getModelPhoto());
				values.put("check_jd", special.getJd());
				values.put("check_wd", special.getWd());
				values.put("check_addr", special.getAddr());
				values.put("check_photo", special.getPhoto());
				values.put("check_result", special.getResult());
				result = (int) db.insert("Check_table", null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将Check实例修改数据库。
	 */
	public int updateCheck(Special special) {
		int result = 0;
		try {
			if (special != null) {
				ContentValues values = new ContentValues();
//				values.put("check_code", special.getSubjectId());
//				values.put("check_project_code", special.getProjectId());
//				values.put("check_shop_code", special.getShopId());
//				values.put("check_name", special.getName());
//				values.put("check_remarks", special.getRemarks());
//				values.put("check_begin", special.getBegin());
//				values.put("check_end", special.getEnd());
//				values.put("check_modelPhoto", special.getModelPhoto());
				values.put("check_jd", special.getJd());
				values.put("check_wd", special.getWd());
				values.put("check_addr", special.getAddr());
				values.put("check_photo", special.getPhoto());
				values.put("check_result", special.getResult());
				result =  db.update("Check_table", values, "check_project_code = ? and check_shop_code = ? and check_code = ?",
						new String[]{special.getProjectId(), special.getShopId(), special.getSubjectId()});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  result;
	}


	public void deleteByProjectAndShop(String projectId, String shopId) {
		String sql = "delete from Check_table where check_project_code = " + projectId
				+" and check_shop_code ="+shopId ;
		db.execSQL(sql, new Object[]{});
	}

	/**
	 * 从数据库读取项目和营业厅下面的数量
	 */
	public int checkNum(String projectId, String shopId) {
		int num = 0;
		String sql = null;
		Cursor cursor = null;
		try {
			sql = "select count(*) from Check_table where check_project_code = " + projectId
					+" and check_shop_code ="+shopId;
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				num = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return num;
	}

	/**
	 * 从数据库读取shopId和projectId下的数据
	 */
	public Special loadCheck(String projectId, String shopId, String id) {
		String sql = null;
		Cursor cursor = null;
		Special special = null;
		try {
			sql = "select * from Check_table where check_project_code = " + projectId
					+" and check_shop_code ="+shopId+" and check_id = "+id;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToFirst()) {
				special = new Special();
				special.setId(cursor.getInt(cursor.getColumnIndex("check_id")));
				special.setProjectId(cursor.getString(cursor.getColumnIndex("check_project_code")));
				special.setShopId(cursor.getString(cursor.getColumnIndex("check_shop_code")));
				special.setSubjectId(cursor.getString(cursor.getColumnIndex("check_code")));
				special.setName(cursor.getString(cursor.getColumnIndex("check_name")));
				special.setRemarks(cursor.getString(cursor.getColumnIndex("check_remarks")));
				special.setBegin(cursor.getString(cursor.getColumnIndex("check_begin")));
				special.setEnd(cursor.getString(cursor.getColumnIndex("check_end")));
				special.setModelPhoto(cursor.getString(cursor.getColumnIndex("check_modelPhoto")));
				special.setJd(cursor.getString(cursor.getColumnIndex("check_jd")));
				special.setWd(cursor.getString(cursor.getColumnIndex("check_wd")));
				special.setAddr(cursor.getString(cursor.getColumnIndex("check_addr")));
				special.setPhoto(cursor.getString(cursor.getColumnIndex("check_photo")));
				special.setResult(cursor.getString(cursor.getColumnIndex("check_result")));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return special;
	}



	/**
	 * 从数据库读取全部检查。
	 */
	public List<Special> loadChecks(String projectId, String shopId) {
		List<Special> list = new ArrayList<>();
		String sql = null;
		Cursor cursor = null;
		try {
			sql = "select * from Check_table where check_project_code = " + projectId
					+" and check_shop_code ="+shopId;
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {
					Special special = new Special();
					special.setProjectId(cursor.getString(cursor.getColumnIndex("check_project_code")));
					special.setShopId(cursor.getString(cursor.getColumnIndex("check_shop_code")));
					special.setSubjectId(cursor.getString(cursor.getColumnIndex("check_code")));
					special.setName(cursor.getString(cursor.getColumnIndex("check_name")));
					special.setRemarks(cursor.getString(cursor.getColumnIndex("check_remarks")));
					special.setBegin(cursor.getString(cursor.getColumnIndex("check_begin")));
					special.setEnd(cursor.getString(cursor.getColumnIndex("check_end")));
					special.setModelPhoto(cursor.getString(cursor.getColumnIndex("check_modelPhoto")));
					special.setJd(cursor.getString(cursor.getColumnIndex("check_jd")));
					special.setWd(cursor.getString(cursor.getColumnIndex("check_wd")));
					special.setAddr(cursor.getString(cursor.getColumnIndex("check_addr")));
					special.setPhoto(cursor.getString(cursor.getColumnIndex("check_photo")));
					special.setResult(cursor.getString(cursor.getColumnIndex("check_result")));
					list.add(special);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
}