package su.rss.manager;

import java.lang.ref.WeakReference;

import su.rss.application.MyApplication;
import su.rss.data.RSSItem;
import su.rss.db.DBOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RSSItemDataManager {

	SQLiteDatabase db;

	private static WeakReference<RSSItemDataManager> sRSSItemDataManager;

	private RSSItemDataManager(Context context) {
		MyApplication app = (MyApplication) context.getApplicationContext();
		db = app.getDB();
	}

	public static RSSItemDataManager getRSSItemDataManager(Context context) {
		if (sRSSItemDataManager == null || sRSSItemDataManager.get() == null) {
			sRSSItemDataManager = new WeakReference<RSSItemDataManager>(
					new RSSItemDataManager(context));
		}
		
		return sRSSItemDataManager.get();
	}

	public void addRSSItem(RSSItem item) {
		if (item == null)
			return;

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.ITEM_DESCRIPTION, item.getDescription());
		values.put(DBOpenHelper.ITEM_FEED_ID, item.getFeedID());
		values.put(DBOpenHelper.ITEM_IMG_FLAG, item.isHaveImage());
		values.put(DBOpenHelper.ITEM_LINK, item.getLink());
		values.put(DBOpenHelper.ITEM_PUBDATE, item.getDate());
		values.put(DBOpenHelper.ITEM_READ_FLAG, item.isRead());
		values.put(DBOpenHelper.ITEM_TITLE, item.getTitle());
		values.put(DBOpenHelper.ID, item.getUID());
		values.put(DBOpenHelper.ITEM_IMG_PATH, item.getImgPath());
		db.insert(DBOpenHelper.ITEM_TABLE, null, values);
	}

	public void updateRSSItem(RSSItem item) {
		if (item == null)
			return;

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.ITEM_DESCRIPTION, item.getDescription());
		values.put(DBOpenHelper.ITEM_FEED_ID, item.getFeedID());
		values.put(DBOpenHelper.ITEM_IMG_FLAG, item.isHaveImage());
		values.put(DBOpenHelper.ITEM_LINK, item.getLink());
		values.put(DBOpenHelper.ITEM_PUBDATE, item.getDate());
		values.put(DBOpenHelper.ITEM_READ_FLAG, item.isRead());
		values.put(DBOpenHelper.ITEM_TITLE, item.getTitle());
		values.put(DBOpenHelper.ID, item.getUID());
		values.put(DBOpenHelper.ITEM_IMG_PATH, item.getImgPath());
		db.update(DBOpenHelper.ITEM_TABLE, values, DBOpenHelper.ID + "=?",
				new String[] { item.getUID() });
	}

	public void deleteRSSItem(String itemid) {
		db.delete(DBOpenHelper.ITEM_TABLE, DBOpenHelper.ID + "=?",
				new String[] { itemid });
	}

	public RSSItem getRSSItem(String itemid) {
		String sql = "select * from " + DBOpenHelper.ITEM_TABLE + " where "
				+ DBOpenHelper.ID + "=?";
		Cursor c = db.rawQuery(sql, new String[] { itemid });
		RSSItem item = null;
		if (c.getCount() > 0) {
			item = new RSSItem();
			c.moveToFirst();
			item.setDate(c.getString(c
					.getColumnIndex(DBOpenHelper.ITEM_PUBDATE)));
			item.setDescription(c.getString(c
					.getColumnIndex(DBOpenHelper.ITEM_DESCRIPTION)));
			item.setFeedID(c.getString(c
					.getColumnIndex(DBOpenHelper.ITEM_FEED_ID)));
			int flag = c.getInt(c.getColumnIndex(DBOpenHelper.ITEM_IMG_FLAG));
			item.setHaveImage(flag == 1 ? true : false);
			item.setLink(c.getString(c.getColumnIndex(DBOpenHelper.ITEM_LINK)));
			flag = c.getInt(c.getColumnIndex(DBOpenHelper.ITEM_READ_FLAG));
			item.setReaded(flag == 1 ? true : false);
			item.setTitle(c.getString(c.getColumnIndex(DBOpenHelper.ITEM_TITLE)));
			item.setUID(itemid);
			item.setImgPath(c.getString(c.getColumnIndex(DBOpenHelper.ITEM_IMG_PATH)));
		}

		c.close();
		return item;
	}
}
