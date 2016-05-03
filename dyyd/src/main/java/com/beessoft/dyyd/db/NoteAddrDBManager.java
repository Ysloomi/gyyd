package com.beessoft.dyyd.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.bean.NoteAddr;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title NoteAddrDBManager
 * @Description 用户数据库操作
 * @Company beessoft
 * @author wxl
 * @date
 */
public class NoteAddrDBManager extends AbsDBManager implements
		IDBManager<NoteAddr> {
	
	public static final String TAG = NoteAddrDBManager.class.getSimpleName();

	private static NoteAddrDBManager mInstance;

	private Object mLock = new Object();

	public static NoteAddrDBManager getInstance() {
		if (mInstance == null) {
			mInstance = new NoteAddrDBManager(LocationApplication.getInstance());
		}
		return mInstance;
	}

	private NoteAddrDBManager(Context context) {
		super(context);
	}

	@Override
	public List<NoteAddr> getAll() {
//		String sql = null;
//		List<NoteAddr> users = new ArrayList<NoteAddr>();
//		Cursor cursor = null;
//		try {
//			sql = "select * from " + DyydDbHelper.TABLE_NOTEPAD_ADDR;
//			cursor = getInstance().sqliteDB().rawQuery(sql, null);
//			while (cursor.moveToNext()) {
//				NoteAddr user = new NoteAddr();
//				user.setId(cursor.getString(cursor
//						.getColumnIndex(UserInfoColumn._ID)));
//				user.setAccount(cursor.getString(cursor
//						.getColumnIndex(UserInfoColumn._ACCOUNT)));
//				user.setName(cursor.getString(cursor
//						.getColumnIndex(UserInfoColumn._NAME)));
//				user.setLoginStatus(cursor.getInt(cursor
//						.getColumnIndex(UserInfoColumn._LOGINSTATUS)));
//				user.setDefaultLogin(cursor.getInt(cursor
//						.getColumnIndex(UserInfoColumn._DEFAULTLOGIN)));
//				users.add(user);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
		return null;
	}

	@Override
	public NoteAddr getById(String id) {
		return null;
	}

	@Override
	public int deleteById(String id) {
		return 0;
	}

	@Override
	public int insert(NoteAddr user) {
		synchronized (mLock) {
			int result = 0;
			ContentValues values = null;
			try {
				getInstance().sqliteDB().beginTransaction();
				values = new ContentValues();
				values.put(AddrColumn._NAME, user.getName());
				values.put(AddrColumn._CODE, user.getCode());
				values.put(AddrColumn._ISCHECK, user.getIscheck());
				result = (int) getInstance().sqliteDB().insert(DyydDbHelper.TABLE_NOTEPAD_ADDR, null, values);
				getInstance().sqliteDB().setTransactionSuccessful();
				Log.i(TAG, "insert db successfully name = " + user.getName() + ",_ischeck = " + user.getIscheck());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				getInstance().sqliteDB().endTransaction();
				if (values != null) {
					values.clear();
					values = null;
				}
			}
			return result;
		}
	}

	@Override
	public int update(NoteAddr user) {
		if (user == null) {
			throw new IllegalArgumentException(
					"update UserInfo user == null ? ");
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(AddrColumn._NAME, user.getName());
			values.put(AddrColumn._CODE, user.getCode());
			values.put(AddrColumn._ISCHECK, user.getIscheck());
			Log.i(TAG, "update userinfo--------");
			return getInstance().sqliteDB().update(DyydDbHelper.TABLE_NOTEPAD_ADDR,
					values, "_name = ? and _code = ?", new String[] { user.getName(),user.getCode()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int delete(String ischeck) {
//		if (user == null) {
//			throw new IllegalArgumentException(
//					"update UserInfo user == null ? ");
//		}
//		ContentValues values = null;
		try {
//			values = new ContentValues();
//			values.put(AddrColumn._NAME, user.getName());
//			values.put(AddrColumn._CODE, user.getCode());
//			values.put(AddrColumn._ISCHECK, user.getIscheck());
			Log.i(TAG, "delete userinfo--------");
			return getInstance().sqliteDB().delete(DyydDbHelper.TABLE_NOTEPAD_ADDR,
					 "_ischeck = ?", new String[] { ischeck});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	// 获取选中的账号
	public List<NoteAddr> getCheck(String ischeck) {
		String sql = null;
		Cursor cursor = null;
		NoteAddr noteAddr = null;
		List<NoteAddr> noteAddrs = new ArrayList<>();
		try {
			sql = "select * from " + DyydDbHelper.TABLE_NOTEPAD_ADDR
					+ " where _ischeck = " + ischeck;
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			while (cursor != null && cursor.moveToLast()) {
				noteAddr = new NoteAddr();
				noteAddr.setName(cursor.getString(cursor.getColumnIndex(AddrColumn._NAME)));
				noteAddr.setCode(cursor.getString(cursor.getColumnIndex(AddrColumn._CODE)));
				noteAddr.setIscheck(cursor.getString(cursor.getColumnIndex(AddrColumn._ISCHECK)));
				noteAddrs.add(noteAddr);
//				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return noteAddrs;
	}

	// 获取选中的账号
	public String getCheckName(String ischeck) {
		String all = "";
		String name = "";
		String code = "";
		String sql = null;
		Cursor cursor = null;
		NoteAddr noteAddr = null;
//		List<NoteAddr> noteAddrs = new ArrayList<>();
		try {
			sql = "select * from " + DyydDbHelper.TABLE_NOTEPAD_ADDR
					+ " where _ischeck = "+ischeck;
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			while (cursor != null && cursor.moveToLast()) {
//				noteAddr = new NoteAddr();
//				noteAddr.setName(cursor.getString(cursor
//						.getColumnIndex(AddrColumn._NAME)));
//				noteAddr.setIfcheck(cursor.getString(cursor
//						.getColumnIndex(AddrColumn._IFCHECK)));
//				noteAddrs.add(noteAddr);

				name += cursor.getString(cursor.getColumnIndex(AddrColumn._NAME)) +"&";
				code += cursor.getString(cursor.getColumnIndex(AddrColumn._CODE)) +"&";
//				break;
			}
			all = name+","+code;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return all;
	}
}
