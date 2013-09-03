package su.rss.util;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import su.rss.activity.SettingActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;

public class Tools {
	
	private static final String DEFAULT_DOWNLOAD_SIZE = "20";

	public static String getHashID(String str) {
		long hash;
		int i;
		for (hash = 0, i = 0; i < str.length(); ++i) {
			hash += str.charAt(i);
			hash += (hash << 10);
			hash += (hash >> 6);
		}

		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);

		String s = new String(Long.toHexString(hash));
		return s.toUpperCase();
	}

	public static boolean CheckNetwork(Context context) {
		boolean is = false;
		ConnectivityManager mnger = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mnger.getActiveNetworkInfo() != null) {
			is = mnger.getActiveNetworkInfo().isAvailable();
		}

		return is;
	}

	public static boolean CheckSDCard() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean isWifi(Context context) {
		NetworkInfo info = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if ((info != null) && (info.getType() == ConnectivityManager.TYPE_WIFI)) {
			return true;
		}

		return false;
	}
	
	public static int dip2px(Context context, int dipValue) {
		float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
	}
	
	public static String getEncoding(URL url) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		detector.add(JChardetFacade.getInstance());
		String charsetName = "UTF-8";
		try {
			Charset charset =  detector.detectCodepage(url);
			charsetName = charset.name();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return charsetName;
	}
	
	public static boolean isAutoUpdate(Context context) {
		SharedPreferences prf = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prf.getBoolean(SettingActivity.IS_AUTO_UPDATE_KEY, true);
	}
	
	public static boolean isImageDownloadEnable(Context context) {
		SharedPreferences prf = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prf.getBoolean(SettingActivity.IS_PIC_ALLOW_KEY, true);
	}
	
	public static int getDownloadLimitSize(Context context) {
		SharedPreferences prf = PreferenceManager
				.getDefaultSharedPreferences(context);
		String size = prf.getString(SettingActivity.DOWNLOAD_NO_PRE_KEY,
				DEFAULT_DOWNLOAD_SIZE);
		return Integer.parseInt(size);
	}
}
