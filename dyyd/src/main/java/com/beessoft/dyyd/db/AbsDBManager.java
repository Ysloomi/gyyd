package com.beessoft.dyyd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.beessoft.dyyd.utils.Constant;

/**
* @Title AbsDBManager 
* @Description 抽象的DBManager
* @Company beessoft
* @author wxl
* @date
*/
public abstract class AbsDBManager {
	
	public static final String TAG = AbsDBManager.class.getSimpleName();
	
	private final DBObserver dbObserver = new DBObserver();
	private static SQLiteDatabase sqLiteDatabase;
	private static DyydDbHelper dyydDbHelper;
	
	public AbsDBManager(Context context){
		openDb(context, Constant.version);
	}

	private void openDb(Context context, int versionCode) {
		if(dyydDbHelper == null){
			dyydDbHelper = new DyydDbHelper(context, this, versionCode);
		}
		if(sqLiteDatabase == null){
			sqLiteDatabase = dyydDbHelper.getWritableDatabase();
		}
	}
	
	private void open(boolean isReadOnly){
		if(sqLiteDatabase == null){
			if(isReadOnly){
				sqLiteDatabase = dyydDbHelper.getReadableDatabase();
			}else{
				sqLiteDatabase = dyydDbHelper.getWritableDatabase();
			}
		}
	}

	public void destroy(){
		try {
			if(dyydDbHelper != null){
				dyydDbHelper.close();
			}
			closeDb();
		} catch (Exception e) {
//			CustomLog.d(e.toString());
		}
	}
	
	public final void reopen(){
		closeDb();
		open(false);
//		CustomLog.d("----reopen this db----");
	}

	private void closeDb(){
		if(sqLiteDatabase != null){
			sqLiteDatabase.close();
			sqLiteDatabase = null;
		}
	}
	
	protected final SQLiteDatabase sqliteDB(){
		open(false);
		return sqLiteDatabase;
	}

	public static class DyydDbHelper extends SQLiteOpenHelper{
		
		public static final String DATABASE_NAME = "dyyd.db";
		
		public static final String TABLE_NOTEPAD_ADDR = "addr";
		
		private AbsDBManager dbManager;
		
		public DyydDbHelper(Context context, AbsDBManager dbManager, int version) {
			this(context, DATABASE_NAME, null, version, dbManager);
		}
		public DyydDbHelper(Context context, String name, CursorFactory factory,
							int version, AbsDBManager dbManager) {
			super(context, name, factory, version);
			this.dbManager = dbManager;
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
		public void createTables(SQLiteDatabase db) {
			//创建记事本
			createAddrTable(db);
//			//创建用户设置表
//			createUserSettingsTable(db);
//			//创建会话背景表
//			createConversationBgTable(db);
//			//创建草稿表
//			createDraftMsgTable(db);
		}

		private void createAddrTable(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS " +
					TABLE_NOTEPAD_ADDR
					+ " ("
					+ AddrColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ AddrColumn._NAME +" TEXT, "
					+ AddrColumn._CODE +" TEXT, "
					+ AddrColumn._ISCHECK +" TEXT"
					+")";
//			CustomLog.d("execute createDraftMsgTable sql = "+sql);
			db.execSQL(sql);
		}
//		private void createConversationBgTable(SQLiteDatabase db) {
//			String sql = "CREATE TABLE IF NOT EXISTS " +
//					TABLE_CONVERSATION_BG
//					+ " ("
//					+ ConversationBgColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ ConversationBgColume._ACCOUNT +" TEXT, "
//					+ ConversationBgColume._TARGETID +" TEXT, "
//					+ ConversationBgColume._BGPATH +" TEXT"
//					+")";
//			CustomLog.d("execute createConversationBgTable sql = "+sql);
//			db.execSQL(sql);
//		}
//		private void createUserSettingsTable(SQLiteDatabase db) {
//			String sql = "CREATE TABLE IF NOT EXISTS " +
//					TABLE_USER_SETTINGS
//					+ " ("
//					+ UserSettingColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ UserSettingColume._ACCOUNT +" TEXT, "
//					+ UserSettingColume._ASADDRESSANDPORT +" TEXT, "
//					+ UserSettingColume._TCPADDRESSANDPORT +" TEXT, "
//					+ UserSettingColume._TOKEN +" TEXT, "
//					+ UserSettingColume._MSGNOTIFY +" INTEGER DEFAULT 1, "
//					+ UserSettingColume._MSGVITOR +" INTEGER DEFAULT 1, "
//					+ UserSettingColume._MSGVOICE +" INTEGER DEFAULT 1"
//					+")";
//			CustomLog.d("execute createUserSettingsTable sql = "+sql);
//			db.execSQL(sql);
//		}
//		private void createUserTable(SQLiteDatabase db) {
//			String sql = "CREATE TABLE IF NOT EXISTS " +
//					TABLE_USER
//					+ " ("
//					+ UserInfoColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ UserInfoColumn._ACCOUNT +" TEXT, "
//					+ UserInfoColumn._NAME+" TEXT, "
//					+ UserInfoColumn._LOGINSTATUS +" INTEGER DEFAULT 1, "
//					+ UserInfoColumn._DEFAULTLOGIN +" INTEGER DEFAULT 1"
//					+")";
//			CustomLog.d("execute createUserTable sql = "+sql);
//			db.execSQL(sql);
//		}
	}
	
	public void addObserver(OnDbChangeListener observer){
		dbObserver.addObserver(observer);
	}

	public void removeObserver(OnDbChangeListener observer){
		dbObserver.removeObserver(observer);
	}
	
	protected void notify(String notifyId){
		dbObserver.notify(notifyId);
	}
	
	protected void clear(){
		dbObserver.clear();
	}
	
	/**
	* @Title BaseColumn 
	* @Description 基础的表列字段
	* @Company yunzhixun
	* @author zhuqian
	* @date 2015-12-2 下午2:32:00
	 */
	public static class BaseColumn{
		public static final String _ID = "_id";
	}
	/**
	* @Title AddrColumn
	* @Description 记事本地点表列字段
	* @Company beessoft
	* @author wxl
	* @date
	 */
	public static class AddrColumn extends BaseColumn{
		public static final String _NAME = "_name";
		public static final String _CODE = "_code";
		public static final String _ISCHECK = "_ischeck";
	}
}
