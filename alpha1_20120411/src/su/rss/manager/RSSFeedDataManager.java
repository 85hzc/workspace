package su.rss.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import su.rss.application.MyApplication;
import su.rss.data.RSSFeed;
import su.rss.db.DBOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RSSFeedDataManager {

	SQLiteDatabase db;

	/**
	 * 单例的弱引用
	 */
	private static WeakReference<RSSFeedDataManager> sRSSFeedDataManager;

	private RSSFeedDataManager(Context context) {
		MyApplication app = (MyApplication) context.getApplicationContext();
		db = app.getDB();
	}

	public static final RSSFeedDataManager getRSSFeedDataManager(Context context) {
		if (sRSSFeedDataManager == null || sRSSFeedDataManager.get() == null) {
			sRSSFeedDataManager = new WeakReference<RSSFeedDataManager>(
					new RSSFeedDataManager(context));
		}

		return (RSSFeedDataManager) sRSSFeedDataManager.get();
	}

	public void addRSSFeed(RSSFeed feed) {
		if (feed == null)
			return;

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.FEED_IMG_PATH, feed.getImagePath());
		values.put(DBOpenHelper.FEED_LINK, feed.getLink());
		values.put(DBOpenHelper.FEED_POSITION, feed.getPosition());
		values.put(DBOpenHelper.FEED_PROPERTY, feed.getProperty());
		values.put(DBOpenHelper.FEED_PUBDATE, feed.getDate());
		values.put(DBOpenHelper.FEED_TITLE, feed.getTitle());
		values.put(DBOpenHelper.ID, feed.getUID());
		values.put(DBOpenHelper.USER_ID, feed.getUserID());
		db.insert(DBOpenHelper.FEED_TABLE, null, values);
	}

	public void updateRSSFeed(RSSFeed feed) {
		if (feed == null)
			return;

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.FEED_IMG_PATH, feed.getImagePath());
		values.put(DBOpenHelper.FEED_LINK, feed.getLink());
		values.put(DBOpenHelper.FEED_POSITION, feed.getPosition());
		values.put(DBOpenHelper.FEED_PROPERTY, feed.getProperty());
		values.put(DBOpenHelper.FEED_PUBDATE, feed.getDate());
		values.put(DBOpenHelper.FEED_TITLE, feed.getTitle());
		values.put(DBOpenHelper.ID, feed.getUID());
		values.put(DBOpenHelper.USER_ID, feed.getUserID());
		db.update(DBOpenHelper.FEED_TABLE, values, DBOpenHelper.ID + "=?",
				new String[] { feed.getUID() });
	}

	public void deleteRSSFeed(String feedid) {
		db.delete(DBOpenHelper.FEED_TABLE, DBOpenHelper.ID + "=?",
				new String[] { feedid });
	}

	public RSSFeed getRSSFeed(String feedid) {
		String sql = "select * from " + DBOpenHelper.FEED_TABLE + " where "
				+ DBOpenHelper.ID + "=?";
		String args[] = { feedid };

		Cursor c = db.rawQuery(sql, args);
		RSSFeed feed = null;
		if (c.getCount() > 0) {
			feed = new RSSFeed();
			c.moveToFirst();
			feed.setDate(c.getString(c
					.getColumnIndex(DBOpenHelper.FEED_PUBDATE)));
			feed.setImagePath(c.getString(c
					.getColumnIndex(DBOpenHelper.FEED_IMG_PATH)));
			feed.setLink(c.getString(c.getColumnIndex(DBOpenHelper.FEED_LINK)));
			feed.setPosition(c.getInt(c
					.getColumnIndex(DBOpenHelper.FEED_POSITION)));
			feed.setProperty(c.getInt(c
					.getColumnIndex(DBOpenHelper.FEED_PROPERTY)));
			feed.setTitle(c.getString(c.getColumnIndex(DBOpenHelper.FEED_TITLE)));
			feed.setUID(c.getString(c.getColumnIndex(DBOpenHelper.ID)));
			feed.setUserID(c.getString(c.getColumnIndex(DBOpenHelper.USER_ID)));
		}

		c.close();
		return feed;
	}

	public ArrayList<RSSFeed> getAllRSSFeed(String userid) {
		String sql = "select * from " + DBOpenHelper.FEED_TABLE + " where "
				+ DBOpenHelper.USER_ID + "=?  order by " + DBOpenHelper.FEED_POSITION;
		String args[] = { userid };
		Cursor c = db.rawQuery(sql, args);
		ArrayList<RSSFeed> list = null;
		if (c.getCount() > 0) {
			list = new ArrayList<RSSFeed>();
			while (c.moveToNext()) {
				RSSFeed feed = new RSSFeed();
				feed.setDate(c.getString(c
						.getColumnIndex(DBOpenHelper.FEED_PUBDATE)));
				feed.setImagePath(c.getString(c
						.getColumnIndex(DBOpenHelper.FEED_IMG_PATH)));
				feed.setLink(c.getString(c
						.getColumnIndex(DBOpenHelper.FEED_LINK)));
				feed.setPosition(c.getInt(c
						.getColumnIndex(DBOpenHelper.FEED_POSITION)));
				feed.setProperty(c.getInt(c
						.getColumnIndex(DBOpenHelper.FEED_PROPERTY)));
				feed.setTitle(c.getString(c
						.getColumnIndex(DBOpenHelper.FEED_TITLE)));
				feed.setUID(c.getString(c.getColumnIndex(DBOpenHelper.ID)));
				feed.setUserID(c.getString(c
						.getColumnIndex(DBOpenHelper.USER_ID)));
				list.add(feed);
			}
		}

		c.close();
		return list;
	}

	public void deleteAllRSSFeed(String userid) {
		db.delete(DBOpenHelper.FEED_TABLE, DBOpenHelper.USER_ID + "=?",
				new String[] { userid });
	}
	
	public boolean isRSSFeedSaved(String feedid) {
		boolean isSaved = false;
		String sql = "select * from " + DBOpenHelper.FEED_TABLE + " where "
				+ DBOpenHelper.ID + "=?";
		String args[] = { feedid };
		Cursor c = db.rawQuery(sql, args);
		if(c.getCount() > 0) {
			isSaved = true;
		}
		
		c.close();
		return isSaved;
	}
}
