package cn.koolcloud.pos.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteMisuseException;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseSqlAdapter {

	protected SQLiteOpenHelper mDbHelper;
	public SQLiteDatabase mDb;

	public SQLiteDatabase getmDb() {
		return mDb;
	}

	public void setmDb(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}

	
	/**
	* @Title: excuteSql
	* @Description: execute sql
	* @param @param sql
	* @param @return
	* @return boolean 
	* @throws
	*/
	public boolean excuteSql(String sql) {
		SQLiteDatabase db = getReadable();
		try {
			db.execSQL(sql);
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
				db = null;
			}
		}
		return true;
	}
	
	public boolean excuteWriteAbleSql(String sql) {
		SQLiteDatabase db = getWrittable();
		try {
			db.execSQL(sql);
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
				db = null;
			}
		}
		return true;
	}

	/** exec sql */
	public boolean excuteSql(String sql, String[] pr) {
		SQLiteDatabase db = getReadable();
		try {
			db.execSQL(sql, pr);
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
				db = null;
			}
		}
		return true;
	}

	public boolean insertSql(String tableName, ContentValues cv) {
		boolean result = false;
		SQLiteDatabase db = getWrittable();
		try {
			db.beginTransaction();
			db.insert(tableName, null, cv);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			if (db != null) {
				db.close();
				db = null;
			}
		}
		return result;
	}

	/** exec sql */
	public boolean excuteSql(ArrayList<SQLEntity> sqlList) {
		SQLiteDatabase db = getWrittable();
		try {
			db.beginTransaction();
			for (SQLEntity entity : sqlList) {
				if (null == entity.getParams())
					db.execSQL(entity.getSql());
				else
					db.execSQL(entity.getSql(), entity.getParams());
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db.isOpen()) {
				db.endTransaction();
			}
			if (db != null) {
				db.close();
				db = null;
			}
		}
		return true;
	}

	/**
	 * get cursor
	 * */
	public Cursor getCursor(String rawQuery, String[] args) {
		getReadable();
		Cursor cursor = null;
		try {
			cursor = mDb.rawQuery(rawQuery, null);
		} catch (SQLiteMisuseException e) {
			e.printStackTrace();
		}
		return cursor;
	}

	SQLiteDatabase getReadable() {
		if (mDb == null || !mDb.isOpen())

			mDb = mDbHelper.getReadableDatabase();
		return mDb;
	}

	SQLiteDatabase getWrittable() {
		if (mDb == null || !mDb.isOpen())
			mDb = mDbHelper.getWritableDatabase();
		return mDb;
	}

	public SQLiteDatabase getDb() throws Exception {
		SQLiteDatabase db;
		try {
			db = mDbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			db = mDbHelper.getReadableDatabase();
		}

		return db;
	}

	/**
	 * close database
	 * */
	public void closeDB() {
		if (mDb != null && mDb.isOpen())
			mDb.close();
		mDb = null;
	}

	public void closeHelp() {
		if (mDbHelper != null)
			mDbHelper.close();
	}

	/**
	 * excuteSql entity bean
	 */
	public class SQLEntity {
		private String sql;
		private String[] params;

		public SQLEntity(String sql, String[] params) {
			super();
			this.sql = sql;
			this.params = params;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public String[] getParams() {
			return params;
		}

		public void setParams(String[] params) {
			this.params = params;
		}

	}
}
