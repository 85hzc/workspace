package su.rss.activity;

import su.rss.application.MyApplication;
import su.rss.util.ThemeSettingHelper;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class BaseActivity extends Activity implements ThemeSettingHelper.ThemeCallback {
	
	public MyApplication app;
	public SQLiteDatabase db;
	public ThemeSettingHelper mThemeSettingHelper;
	SharedPreferences mPref;
	Handler mHandler;
	boolean isPaused;
	boolean isThemeChanged;
	
	@Override
	public void applyTheme() {
		// TODO Auto-generated method stub
		if(isPaused) {
			isThemeChanged = true;
		} else {
			onApplyTheme();
		}
	}
	
	public void onApplyTheme() {
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (MyApplication) getApplicationContext();
		db = app.getDB();
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		createHandler();
		isThemeChanged = true;
		mThemeSettingHelper = ThemeSettingHelper.getThemeSettingHelper(this);
		mThemeSettingHelper.registerThemeCallback(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mThemeSettingHelper.unregisterThemeCallback(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isPaused = false;
		if(isThemeChanged) {
			isThemeChanged = false;
			onApplyTheme();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isPaused = true;
	}
	
	public void processMessage(Message msg) {
		
	}
	
	private void createHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				processMessage(msg);
			}
			
		};
	}
}
