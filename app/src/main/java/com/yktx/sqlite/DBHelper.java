package com.yktx.sqlite;

//package com.sqlite.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yktx.bean.GroupListBean;

/**
 * 
 @Title: DBHelper
 * @Package com.coupon.activity
 * @Description: TODO(��ݿ���)
 * @author ting
 * @date 2012-8-2 2012
 * @version V1.0
 */

public class DBHelper {

	private final String CLASSTAG = DBHelper.class.getSimpleName();

	Context mCotext;
	private final static String _DB_NAME = "YanDB";
	private final static int _DB_VERSION = 1;

	OpenHelper mDbOpenHelper;

	public DBHelper(Context context) {
		super();
		mCotext = context;
		mDbOpenHelper = new OpenHelper(context);

	}

	/**
	 * 搜索历史表
	 * 
	 * @"GROUP_ID":"1",
	 * @"GROUP_NAME":"jerry",
	 * @"GROUP_MAN_COUNT":"1",
	 * @"GROUP_TIME":"1413192168142",
	 * @"GROUP_DISTANCE":"0.06km",
	 * 
	 * */
	final static String SEARCH_HISTORY_LIST_EXCEL = "SEARCH_HISTORY_LIST_EXCEL";

	/** 搜索历史表 */

	final static String CREAT_TB_HISTORY_LIST_EXCEL = "CREATE TABLE "
			+ SEARCH_HISTORY_LIST_EXCEL + "("
			+ " GROUP_ID TEXT varchar(10) NOT NULL,"
			+ " GROUP_NAME TEXT varchar(2) NOT NULL,"
			+ " GROUP_MAN_COUNT TEXT varchar(2) NOT NULL,"
			+ " GROUP_TIME TEXT timestamp NOT NULL,"
			+ " DISTANCE TEXT varchar(4) NOT NULL" + ");";

	/**
	 * 首页历史表
	 * 
	 * @"GROUP_ID":"1",
	 * @"GROUP_NAME":"jerry",
	 * @"GROUP_ONLINE_NUMBER":"1",
	 * @"GROUP_AT_COUNT":"1",
	 * @"GROUP_TIME":"1413192168142",
	 * @"GROUP_DISTANCE":"0.06km",
	 * 
	 * */
	final static String MAIN_HISTORY_LIST_EXCEL = "MAIN_HISTORY_LIST_EXCEL";

	/** 首页历史表 */

	final static String CREAT_TB_MAIN_HISTORY_LIST_EXCEL = "CREATE TABLE "
			+ MAIN_HISTORY_LIST_EXCEL + "("
			+ " GROUP_ID TEXT varchar(10) NOT NULL,"
			+ " GROUP_NAME TEXT varchar(2) NOT NULL,"
			+ " GROUP_ONLINE_NUMBER TEXT varchar(2) NOT NULL,"
			+ " GROUP_AT_COUNT TEXT varchar(2) NOT NULL,"
			+ " GROUP_TIME TEXT timestamp NOT NULL,"
			+ " DISTANCE TEXT varchar(4) NOT NULL" + ");";

	// =========================================================
	/***
	 * 添加搜索历史
	 */
	public int insertSearchList(ArrayList<GroupListBean> list) {
		int errorCode = 0;
		synchronized (DBInstance.getInstance()) {
			SQLiteDatabase mDb = openDatabase(true);

			if (mDb != null) {
				/** ��ʼ���� */
				mDb.beginTransaction();

				mDb.delete(SEARCH_HISTORY_LIST_EXCEL, "", null);

				try {
					// JSONArray result = new JSONArray(list);

					for (int i = 0; i < list.size(); i++) {
						// JSONObject item = result.getJSONObject(i);

						ContentValues insertValues = new ContentValues();
						GroupListBean bean = list.get(i);
						/**
						 * @"GROUP_ID":"1",
						 * @"GROUP_NAME":"jerry",
						 * @"GROUP_MAN_COUNT":"1",
						 * @"DISTANCE":"0.06km",
						 * 
						 * */
						insertValues.put("GROUP_ID", bean.getGroupID());
						insertValues.put("GROUP_NAME", bean.getGroupName());
						insertValues.put("GROUP_MAN_COUNT",
								bean.getGroupPeopleCount());
						insertValues.put("DISTANCE", bean.getDistance());
						insertValues.put("GROUP_TIME", bean.getGroupTime());

						mDb.insertOrThrow(SEARCH_HISTORY_LIST_EXCEL, null,
								insertValues);
						Log.i(  "aaa", "insert------------------"
								+ bean.getGroupName());
					}

					mDb.setTransactionSuccessful();

				} catch (SQLException e) {
					errorCode--;
					Log.e("kkk",
							"insertProductsList SQLException:" + e.getMessage());

				} finally {
					mDb.endTransaction();
					mDb.close();
					// returnExcelCount(PRODUCT_LIST_EXCEL, null);
				}
			} else {
				errorCode--;
			}
		}
		return errorCode;

	}

	/***
	 * 添加首页历史
	 */
	public int insertMainHistoryList(ArrayList<GroupListBean> list) {
		int errorCode = 0;
		synchronized (DBInstance.getInstance()) {
			SQLiteDatabase mDb = openDatabase(true);

			if (mDb != null) {
				/** ��ʼ���� */
				mDb.beginTransaction();

				mDb.delete(MAIN_HISTORY_LIST_EXCEL, "", null);

				try {
					// JSONArray result = new JSONArray(list);

					for (int i = 0; i < list.size(); i++) {
						// JSONObject item = result.getJSONObject(i);

						ContentValues insertValues = new ContentValues();
						GroupListBean bean = list.get(i);
						/**
						 * " GROUP_ID TEXT varchar(10) NOT NULL," +
						 * " GROUP_NAME TEXT varchar(2) NOT NULL," +
						 * " GROUP_AT_COUNT TEXT varchar(2) NOT NULL," +
						 * " GROUP_TIME TEXT timestamp NOT NULL," +
						 * " DISTANCE TEXT varchar(4) NOT NULL" + ");";
						 * 
						 * */
						insertValues.put("GROUP_ID", bean.getGroupID());
						insertValues.put("GROUP_NAME", bean.getGroupName());
						insertValues.put("GROUP_ONLINE_NUMBER",
								bean.getGroupPeopleCount());
						insertValues.put("GROUP_AT_COUNT",
								bean.getAtNum());
						insertValues.put("GROUP_TIME", bean.getGroupTime());
						insertValues.put("DISTANCE", bean.getDistance());
						
						mDb.insertOrThrow(MAIN_HISTORY_LIST_EXCEL, null,
								insertValues);
						Log.i(  "aaa", "insert------------------"
								+ bean.getGroupName());
					}

					mDb.setTransactionSuccessful();

				} catch (SQLException e) {
					errorCode--;
					Log.e("kkk",
							"insertProductsList SQLException:" + e.getMessage());

				} finally {
					mDb.endTransaction();
					mDb.close();
					// returnExcelCount(PRODUCT_LIST_EXCEL, null);
				}
			} else {
				errorCode--;
			}
		}
		return errorCode;

	}

	public ArrayList<GroupListBean> getNameList(ArrayList<GroupListBean> list) {

		GroupListBean bean = null;
		synchronized (DBInstance.getInstance()) {

			Cursor c = null;
			list.clear();

			SQLiteDatabase mDb = openDatabase(false);

			if (mDb != null) {
				try {

					c = mDb.query(SEARCH_HISTORY_LIST_EXCEL, null, null, null,
							null, null, "GROUP_TIME DESC");

					c.moveToFirst();

					for (int i = 0; i < c.getCount(); i++) {

						bean = new GroupListBean();
						bean.setGroupID(c.getString(0));
						bean.setGroupName(c.getString(1));
						bean.setGroupPeopleCount(c.getString(2));
						bean.setGroupTime(c.getLong(3));
						bean.setDistance(c.getString(4));
						// if (Contanst.STORE_DEBUG) {
						// Log.i(  "++++++++++++++++++",
						// c.getString(0) + "+"
						// + c.getString(1) + "+"
						// + c.getString(2));
						// }
						list.add(bean);
						c.moveToNext();

					}

				} catch (SQLException e) {

					Log.e(CLASSTAG + " read table exception : ", e.getMessage());

				} finally {
					if (c != null && !c.isClosed()) {
						c.close();
						c = null;
					}
					mDb.close();
					mDb = null;
				}
			}
		}
		return list;

	}

	/**
	 * 获取首页历史list
	 * @param list
	 * @return
	 */
	public ArrayList<GroupListBean> getMainHistoryList(ArrayList<GroupListBean> list) {

		GroupListBean bean = null;
		synchronized (DBInstance.getInstance()) {

			Cursor c = null;
			list.clear();

			SQLiteDatabase mDb = openDatabase(false);

			if (mDb != null) {
				try {

					c = mDb.query(MAIN_HISTORY_LIST_EXCEL, null, null, null,
							null, null, "GROUP_TIME DESC");
					
					c.moveToFirst();
					
					for (int i = 0; i < c.getCount(); i++) {
//						 * @"GROUP_ID":"1",
//						 * @"GROUP_NAME":"jerry",
//						 * @"GROUP_ONLINE_NUMBER":"1",
//						 * @"GROUP_AT_COUNT":"1",
//						 * @"GROUP_TIME":"1413192168142",
//						 * @"GROUP_DISTANCE":"0.06km",;";
						bean = new GroupListBean();
						bean.setGroupID(c.getString(0));
						bean.setGroupName(c.getString(1));
						bean.setAtNum(c.getString(2));
						bean.setGroupPeopleCount(c.getString(3));
						bean.setGroupTime(c.getLong(4));
						bean.setDistance(c.getString(5));
						list.add(bean);
						c.moveToNext();
						
					}

				} catch (SQLException e) {
					
					Log.e(CLASSTAG + " read table exception : ", e.getMessage());
					
				} finally {
					if (c != null && !c.isClosed()) {
						c.close();
						c = null;
					}
					mDb.close();
					mDb = null;
				}
			}
		}
		return list;

	}
	
	
	/**
	 * 获取历史记录的GroupId集合
	 * 
	 * @return
	 */
	public ArrayList<String> getGroupIdList() {

		ArrayList<String> groupIdList = new ArrayList<String>();
		synchronized (DBInstance.getInstance()) {

			Cursor c = null;
			SQLiteDatabase mDb = openDatabase(false);

			if (mDb != null) {
				try {

					c = mDb.query(SEARCH_HISTORY_LIST_EXCEL, null, null, null,
							null, null, "GROUP_TIME DESC");

					c.moveToFirst();

					for (int i = 0; i < c.getCount(); i++) {

						// bean = new GroupListBean();
						// bean.setGroupID(c.getString(0));
						// bean.setGroupName(c.getString(1));
						// bean.setGroupPeopleCount(c.getString(2));
						// bean.setGroupTime(c.getLong(3));
						// bean.setDistance(c.getString(4));
						groupIdList.add(c.getString(0));
						c.moveToNext();
					}

				} catch (SQLException e) {

					Log.e(CLASSTAG + " read table exception : ", e.getMessage());

				} finally {
					if (c != null && !c.isClosed()) {
						c.close();
						c = null;
					}
					mDb.close();
					mDb = null;
				}
			}
		}
		return groupIdList;

	}

	/***
	 * 删除搜索历史
	 */
	public int deleteSearchList() {
		int errorCode = 0;
		synchronized (DBInstance.getInstance()) {
			SQLiteDatabase mDb = openDatabase(true);

			if (mDb != null) {
				mDb.beginTransaction();

				// mDb.delete(SEARCH_HISTORY_LIST_EXCEL, "", null);

				try {
					mDb.delete(SEARCH_HISTORY_LIST_EXCEL, "", null);
					mDb.setTransactionSuccessful();

				} catch (SQLException e) {
					errorCode--;
					Log.e("kkk",
							"deleteSearchList SQLException:" + e.getMessage());

				} finally {
					mDb.endTransaction();
					mDb.close();
				}
			} else {
				errorCode--;
			}
		}
		return errorCode;
	}

	// ///////////////////////////////////////////////////////////////////
	// DB MANAGER
	// ////////////////////////////////////////////////////////////////////
	private SQLiteDatabase openDatabase(boolean writable) {

		SQLiteDatabase db = null;

		if (mDbOpenHelper != null) {
			mDbOpenHelper.close();
		}

		try {
			if (writable) {
				db = mDbOpenHelper.getWritableDatabase();
			} else {
				db = mDbOpenHelper.getReadableDatabase();
			}
		} catch (SQLiteFullException e) {

		} catch (SQLiteDiskIOException e) {

		} catch (SQLiteException e) {

		}
		return db;
	}

	public void createTable() {

	}

	private class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, _DB_NAME, null, _DB_VERSION);
			Log.i(  "kkk", "OpenHelper");
		}

		public void onCreate(SQLiteDatabase db) {
			/** ��ʼ���� */
			db.beginTransaction();

			try {
				// ����
				db.execSQL(CREAT_TB_HISTORY_LIST_EXCEL);
				db.execSQL(CREAT_TB_MAIN_HISTORY_LIST_EXCEL);

				db.setTransactionSuccessful();

			} catch (Exception ex) {

				ex.printStackTrace();

			} finally {

				if (db != null) {

					db.endTransaction();
				}
			}
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.beginTransaction();

			try {

				db.execSQL("DROP TABLE IF EXISTS "
						+ CREAT_TB_HISTORY_LIST_EXCEL);
				db.execSQL("DROP TABLE IF EXISTS "
						+ CREAT_TB_MAIN_HISTORY_LIST_EXCEL);
				db.setTransactionSuccessful();

			} catch (Exception ex) {

				ex.printStackTrace();

			} finally {

				if (db != null) {
					db.endTransaction();
					onCreate(db);
				}
			}
		}
	}
}
