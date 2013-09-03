package su.rss.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import su.rss.application.MyApplication;
import su.rss.data.PicData;
import su.rss.db.DBOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PicManager {

	SQLiteDatabase db;
	
	private static WeakReference<PicManager> sPicManager;

	private PicManager(Context context) {
		MyApplication app = (MyApplication) context.getApplicationContext();
		db = app.getDB();
	}
	
	public static final PicManager getPicManager(Context context) {
		if(sPicManager == null || sPicManager.get() == null) {
			sPicManager = new WeakReference<PicManager>(new PicManager(context));
		}
		
		return sPicManager.get();
	}

	public void addPicData(PicData data) {
		if (data == null)
			return;

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.PIC_HEIGHT, data.getHeight());
		values.put(DBOpenHelper.PIC_PATH, data.getImagePath());
		values.put(DBOpenHelper.PIC_RELATE_ID, data.getRelateID());
		values.put(DBOpenHelper.PIC_WIDTH, data.getWidth());
		values.put(DBOpenHelper.ID, data.getID());
		values.put(DBOpenHelper.PIC_LINK, data.getImageLink());
		db.insert(DBOpenHelper.PIC_TABLE, null, values);
	}

	public void deletePicData(String id, String relateid) {
		db.delete(DBOpenHelper.PIC_TABLE, DBOpenHelper.ID + "=? AND " + DBOpenHelper.PIC_RELATE_ID + "=?",
				new String[] { id, relateid });
	}

	public ArrayList<PicData> getAllPicData(String relateid) {
		String sql = "select * from " + DBOpenHelper.PIC_TABLE + " where "
				+ DBOpenHelper.PIC_RELATE_ID + "=?";
		String args[] = { relateid };
		Cursor cursor = db.rawQuery(sql, args);
		ArrayList<PicData> list = null;
		if (cursor.getCount() > 0) {
			list = new ArrayList<PicData>();
			while (cursor.moveToNext()) {
				PicData data = new PicData();
				data.setHeight(cursor.getInt(cursor
						.getColumnIndex(DBOpenHelper.PIC_HEIGHT)));
				data.setID(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ID)));
				data.setImagePath(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.PIC_PATH)));
				data.setRelateID(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.PIC_RELATE_ID)));
				data.setWidth(cursor.getInt(cursor
						.getColumnIndex(DBOpenHelper.PIC_WIDTH)));
				data.setImageLink(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.PIC_LINK)));
				list.add(data);
			}
		}

		cursor.close();
		return list;
	}
	
	public PicData getPicData(String id, String relateid) {
		String sql = "select * from " + DBOpenHelper.PIC_TABLE + " where "
				+ DBOpenHelper.ID + "=? AND " + DBOpenHelper.PIC_RELATE_ID + "=?";
		String args[] = { id, relateid };
		Cursor cursor = db.rawQuery(sql, args);
		PicData data = null;
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			data = new PicData();
			data.setHeight(cursor.getInt(cursor
					.getColumnIndex(DBOpenHelper.PIC_HEIGHT)));
			data.setID(cursor.getString(cursor
					.getColumnIndex(DBOpenHelper.ID)));
			data.setImagePath(cursor.getString(cursor
					.getColumnIndex(DBOpenHelper.PIC_PATH)));
			data.setRelateID(cursor.getString(cursor
					.getColumnIndex(DBOpenHelper.PIC_RELATE_ID)));
			data.setWidth(cursor.getInt(cursor
					.getColumnIndex(DBOpenHelper.PIC_WIDTH)));
			data.setImageLink(cursor.getString(cursor
					.getColumnIndex(DBOpenHelper.PIC_LINK)));
		}
		
		cursor.close();
		return data;
	}

	public void deleteAllPicData(String relateid) {
		db.delete(DBOpenHelper.PIC_TABLE, DBOpenHelper.PIC_RELATE_ID + "=?",
				new String[] { relateid });
	}
	
	public boolean isPicSaved(String id, String relateid) {
		String sql = "select * from " + DBOpenHelper.PIC_TABLE + " where "
				+ DBOpenHelper.ID + "=? AND " + DBOpenHelper.PIC_RELATE_ID + "=?";
		String args[] = { id, relateid };
		Cursor cursor = db.rawQuery(sql, args);
		boolean isSaved = false;
		if(cursor.getCount() > 0) {
			isSaved = true;
		}
		
		cursor.close();
		return isSaved;
	}
}
