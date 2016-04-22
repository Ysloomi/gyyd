package com.beessoft.dyyd.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beessoft.dyyd.bean.Advise;

public class AdviseDao {
//	private final String TAG = "Db";
	private AdviseDBHelper adviseDBHelper;

	public AdviseDao(Context context) {
		adviseDBHelper = new AdviseDBHelper(context);
	}

	public void add(Advise advise) {
//		Log.e(TAG, "add news newstype " + customer.getCustomerName());
		String sql = "insert into tb_advise (advisetype) values(?) ;";
		SQLiteDatabase db = adviseDBHelper.getWritableDatabase();
		db.execSQL(
				sql,
				new Object[] {advise.getAdviseType()});
		db.close();
	}

	public void deleteAll() {
//		System.out.println("test");
		String sql = "delete from tb_advise";
		SQLiteDatabase db = adviseDBHelper.getWritableDatabase();
		db.execSQL(sql, new Object[]{});
		db.close();
	}

	public void add(List<Advise> advises) {
		for (Advise advise : advises) {
			add(advise);
		}
	}

	/**
	 * 根据newsType和currentPage从数据库中取数据
	 * 
	 * @param newsType
	 * @param currentPage
	 * @return
	 */
	public List<Advise> list(String currentPage) {

//		Log.e(TAG, currentPage + "  currentPage");
		// 0 -9 , 10 - 19 ,
		List<Advise> advises = new ArrayList<Advise>();
		try {
			int page = Integer.valueOf(currentPage)/10;
			int offset = 10 * (page - 1);
			String sql = "select *from tb_advise  limit ?,? ";
			SQLiteDatabase db = adviseDBHelper.getReadableDatabase();
			Cursor c = db.rawQuery(sql, new String[] { offset + "", "" + (offset + 10) });

			Advise advise = null;

			while (c.moveToNext()) {
				advise = new Advise();

				String adviseType = c.getString(c.getColumnIndex("advisetype"));

				advise.setAdviseType(adviseType);

				advises.add(advise);

			}
			c.close();
			db.close();
//			Log.e(TAG, customers.size() + "  customers.size()");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advises;
	}
	
	public List<Advise> list() {

//		Log.e(TAG, currentPage + "  currentPage");
		// 0 -9 , 10 - 19 ,
		List<Advise> advises = new ArrayList<Advise>();
		try {
			String sql = "select * from tb_advise";
			SQLiteDatabase db = adviseDBHelper.getReadableDatabase();
			Cursor c = db.rawQuery(sql, new String[] {});

			Advise advise = null;

			while (c.moveToNext()) {
				advise = new Advise();

				String adviseType = c.getString(c.getColumnIndex("advisetype"));

				advise.setAdviseType(adviseType);

				advises.add(advise);

			}
			c.close();
			db.close();
//			Log.e(TAG, customers.size() + "  customers.size()");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advises;
	}
}
