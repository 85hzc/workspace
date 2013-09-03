package su.rss.util;

import android.util.Log;

public class Logger {
	private static final String TAG = "RSSReader"; // Ĭ�ϵ�LOG tag
	private static boolean DEBUG = true; // trace����

	/**
	 * Send a DEBUG log message.
	 * 
	 * @param msg
	 */
	public static void d(String msg) // Debug����log
	{
		if (DEBUG)
			Log.d(TAG, msg);
	}

	public static void d(String tag, String msg) // ��tag��debug����log
	{
		if (DEBUG)
			Log.d(tag, msg);
	}

	public static void e(String msg) // Error����log
	{
		if (DEBUG)
			Log.e(TAG, msg);
	}

	public static void e(String tag, String msg) // Error���ʹ�tag��log
	{
		if (DEBUG)
			Log.e(tag, msg);
	}

	public static void i(String msg) // info����log
	{
		if (DEBUG)
			Log.i(TAG, msg);
	}

	public static void i(String tag, String msg) // info���ʹ�tag��log
	{
		if (DEBUG)
			Log.i(tag, msg);
	}

	public static void v(String msg) {
		if (DEBUG)
			Log.v(TAG, msg);
	}

	public static void v(String tag, String msg) {
		if (DEBUG)
			Log.v(tag, msg);
	}

	public static void w(String msg) {
		if (DEBUG)
			Log.w(TAG, msg);
	}

	public static void w(String tag, String msg) {
		if (DEBUG)
			Log.w(tag, msg);
	}

	public static void w(String tag, Throwable tr) {
		if (DEBUG)
			Log.w(tag, "", tr);

	}

	public static void w(String tag, String msg, Throwable tr) {
		if (DEBUG)
			Log.e(TAG, msg, tr);
	}

//	protected static String buildMessage(String msg) {
//		StackTraceElement caller = new Throwable().fillInStackTrace()
//				.getStackTrace()[2];
//
//		return new StringBuilder().append(caller.getClassName()).append(".")
//				.append(caller.getMethodName()).append("(): ").append(msg)
//				.toString();
//	}

}
