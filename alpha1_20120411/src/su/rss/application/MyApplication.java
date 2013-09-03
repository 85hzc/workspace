package su.rss.application;

import su.rss.db.DBOpenHelper;
import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class MyApplication extends Application {
	
	SQLiteDatabase db;
	DBOpenHelper dbOpenHelper;
	boolean isOfflineDownload;
	boolean isOfflineDownloadFinish;
	boolean isOfflineDownloadCancel;
	SharedPreferences set_pref;
	String userID;
	
	public MyApplication() {
		isOfflineDownload = false;
		isOfflineDownloadFinish = false;
		isOfflineDownloadCancel = false;
		userID = "userid";
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initDB();
		set_pref = PreferenceManager.getDefaultSharedPreferences(this);

	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		if(db != null) {
			db.close();
		}
		
		if(dbOpenHelper != null) {
			dbOpenHelper.close();
		}
	}

	public void initDB() {
		dbOpenHelper = new DBOpenHelper(this.getApplicationContext());
		db = dbOpenHelper.getWritableDatabase();
	}
	
	public void setDB(SQLiteDatabase sqldb) {
		db = sqldb;
	}
	
	public SQLiteDatabase getDB() {
		return db;
	}
	
	public void setDBOpenHelper(DBOpenHelper helper) {
		dbOpenHelper = helper;
	}
	
	public DBOpenHelper getDBOpenHelper() {
		return dbOpenHelper;
	}
	
	public void setIsOfflineDown(boolean down) {
		this.isOfflineDownload = down;
	}
	
	public boolean isOfflineDown() {
		return this.isOfflineDownload;
	}
	
	public void setIsOfflineDownFinish(boolean finish) {
		this.isOfflineDownloadFinish = finish;
	}
	
	public boolean isOfflineDownFinish() {
		return this.isOfflineDownloadFinish;
	}
	
	public void setIsOfflineDownCancle(boolean cancel) {
		this.isOfflineDownloadCancel = cancel;
	}
	
	public boolean isOfflineDownCancle() {
		return this.isOfflineDownloadCancel;
	}
	
	public void setSharedPreferences(SharedPreferences pref) {
		this.set_pref = pref;
	}
	
	public SharedPreferences SharedPreferences() {
		return this.set_pref;
	}
	
	public void setUserID(String userid) {
		userID = userid;
	}
	
	public String getUserID() {
		return userID;
	}
}
