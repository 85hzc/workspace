package su.rss.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import su.rss.application.MyApplication;
import su.rss.data.RSSItem;
import su.rss.db.DBOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CollectManager {

	SQLiteDatabase db;
	private static WeakReference<CollectManager> sCollectManager;

	private CollectManager(Context context) {
		MyApplication app = (MyApplication) context.getApplicationContext();
		db = app.getDB();
	}

	public static CollectManager getCollectManager(Context context) {
		if (sCollectManager == null || sCollectManager.get() == null) {
			sCollectManager = new WeakReference<CollectManager>(
					new CollectManager(context));
		}

		return sCollectManager.get();
	}

	public void addCollect(String userid, String itemid) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.COLLECT_ITEM_ID, itemid);
		values.put(DBOpenHelper.USER_ID, userid);
		db.insert(DBOpenHelper.COLLECT_TABLE, null, values);
	}

	public void deleteCollect(String itemid) {
		db.delete(DBOpenHelper.COLLECT_TABLE, DBOpenHelper.COLLECT_ITEM_ID
				+ "=?", new String[] { itemid });
	}

	public ArrayList<RSSItem> getAllCollect() {
		String sql = "select * from " + DBOpenHelper.ITEM_TABLE
				+ " left outer join " + DBOpenHelper.COLLECT_TABLE + " on "
				+ DBOpenHelper.ITEM_TABLE + "." + DBOpenHelper.ID + "="
				+ DBOpenHelper.COLLECT_TABLE + "."
				+ DBOpenHelper.COLLECT_ITEM_ID;
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<RSSItem> list = null;
		if (cursor.getCount() > 0) {
			list = new ArrayList<RSSItem>();
			while (cursor.moveToNext()) {
				RSSItem item = new RSSItem();
				item.setDate(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ITEM_PUBDATE)));
				item.setDescription(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ITEM_DESCRIPTION)));
				item.setFeedID(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ITEM_FEED_ID)));
				int flag = cursor.getInt(cursor
						.getColumnIndex(DBOpenHelper.ITEM_IMG_FLAG));
				item.setHaveImage(flag == 1 ? true : false);
				item.setLink(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ITEM_LINK)));
				flag = cursor.getInt(cursor
						.getColumnIndex(DBOpenHelper.ITEM_READ_FLAG));
				item.setReaded(flag == 1 ? true : false);
				item.setTitle(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ITEM_TITLE)));
				item.setUID(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ID)));
				item.setImgPath(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ITEM_IMG_PATH)));
				list.add(item);
			}
		}

		cursor.close();
		return list;
	}

	public void deleteAllCollect(String userid) {
		db.delete(DBOpenHelper.COLLECT_TABLE, DBOpenHelper.USER_ID + "=?",
				new String[] { userid });
	}

	public boolean isItemCollected(String itemid, String userid) {
		boolean is = false;
		String sql = "select * from " + DBOpenHelper.COLLECT_TABLE + " where "
				+ DBOpenHelper.COLLECT_ITEM_ID + "=? and "
				+ DBOpenHelper.USER_ID + "=?";
		String args[] = { itemid, userid };
		Cursor c = db.rawQuery(sql, args);
		if (c.getCount() > 0) {
			is = true;
		}
		c.close();
		return is;
	}
}
