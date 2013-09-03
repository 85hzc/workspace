package su.rss.manager;

import java.lang.ref.WeakReference;

import su.rss.application.MyApplication;
import su.rss.db.DBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReadHistoryManager {
	SQLiteDatabase db;
	private static WeakReference<ReadHistoryManager> sHistoryManager;

	private ReadHistoryManager(Context context) {
		MyApplication app = (MyApplication) context.getApplicationContext();
		db = app.getDB();
	}

	public static ReadHistoryManager getHistoryManager(Context context) {
		if (sHistoryManager == null || sHistoryManager.get() == null) {
			sHistoryManager = new WeakReference<ReadHistoryManager>(
					new ReadHistoryManager(context));
		}

		return sHistoryManager.get();
	}

	public void addReadHistory(String itemid, String date, String userid) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.READ_HISTORY_ITEM_ID, itemid);
		values.put(DBOpenHelper.READ_HISTORY_DATE, date);
		values.put(DBOpenHelper.USER_ID, userid);
		db.insert(DBOpenHelper.READ_HISTORY_TABLE, null, values);
	}

	public boolean isItemRead(String itemid, String userid) {
		boolean isRead = false;
		String sql = "select * from " + DBOpenHelper.READ_HISTORY_TABLE
				+ " where " + DBOpenHelper.READ_HISTORY_ITEM_ID + "=? and "
				+ DBOpenHelper.USER_ID + "=?";
		String args[] = { itemid, userid };

		Cursor c = db.rawQuery(sql, args);
		if (c.getCount() > 0) {
			isRead = true;
		}
		c.close();
		return isRead;
	}

	public void deleteReadHistory(String itemid, String userid) {
		db.delete(DBOpenHelper.READ_HISTORY_TABLE,
				DBOpenHelper.READ_HISTORY_ITEM_ID + "=? AND "
						+ DBOpenHelper.USER_ID + "=?", new String[] { itemid,
						userid });
	}
}
