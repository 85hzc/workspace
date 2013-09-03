package su.rss.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;

import su.rss.R;
import su.rss.activity.OfflineDownloadActivity;
import su.rss.activity.SettingActivity;
import su.rss.application.MyApplication;
import su.rss.data.RSSFeed;
import su.rss.data.RSSItem;
import su.rss.manager.RSSFeedDataManager;
import su.rss.net.HttpBase;
import su.rss.net.MyHttpResponse;
import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.RSSUtil;
import su.rss.util.Tools;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class OfflineDownloadService extends Service {

	/**
	 * 配置文件KEY
	 */
	public static final String OFFLINE_INFO_PRF = "offline";
	public static final String OFFLINE_CURRENT_TITLE = "title";
	public static final String OFFLINE_CURRENT_SUM = "current_sum";
	public static final String OFFLINE_CURRENT_NUM = "current_num";
	public static final String OFFLINE_CURRENT_PERCENT = "percent";
	public static final String OFFLINE_TOTAL_SUM = "total_sum";
	public static final String OFFLINE_DOWNLOAD_SIZE = "size";

	/**
	 * 下载失败类型
	 */
	public static final int OFFLINE_FAILED_TYPE_NET = 1;
	public static final int OFFLINE_FAILED_TYPE_SD = 2;

	/**
	 * 下载失败广播
	 */
	public static final String ACTION_OFFLINE_FAILED = "offline_down_failed";
	public static final String EXTRAS_TYPE_KEY = "failed_type";

	public static final String INPUT_EXTRAS_KEY = "feed_id";

	private static final int MSG_DOWNLOADING = 1;
	private static final int MSG_DOWNLOAD_CANCEL = 2;
	private static final int MSG_DOWNLOAD_FINISH = 3;
	private static final int MSG_DOWNLOAD_ERROR_NET = 4;
	private static final int MSG_DOWNLOAD_ERROR_SD = 5;
	private static final int MSG_DOWNLOAD_FAILED = 6;

	private static final String DOWNLOAD_SUM_KEY = "sum";
	private static final String DOWNLOAD_COUNT_KEY = "count";
	private static final String DOWNLOAD_PERCENT_KEY = "percent";
	private static final String DOWNLOAD_FEEDNAME_KEY = "feed_name";

	private static final int NOTIFICATION_ID = 1;

	MyApplication appData;
	SQLiteDatabase db;
	NotificationManager notifiManager;
	Notification notification;
	RemoteViews remoteView;
	PendingIntent pIntent;
	int downloadSum;
	int itemFailedCount;
	int feedFailedCount;
	long downloadSize;
	int downloadLimitSize;
	ArrayList<RSSItem> itemList;
	ArrayList<String> feedIDList;
	MyHandler mHandler;
	Context mContext;

	public OfflineDownloadService() {
		downloadSum = 0;
		itemFailedCount = 0;
		feedFailedCount = 0;
		downloadSize = 0;
		mHandler = new MyHandler();
	}

	private void initNotification() {
		notifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, OfflineDownloadActivity.class);
		pIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void setNotification(int layoutid, int iconid, String title) {
		remoteView = new RemoteViews(getPackageName(), layoutid);
		notification = new Notification(iconid, title,
				System.currentTimeMillis());
		notification.contentView = remoteView;
		notification.contentIntent = pIntent;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		appData = (MyApplication) getApplicationContext();
		db = appData.getDB();
		initNotification();
		mContext = this;
		clearOfflineDownloadInfo();
		downloadLimitSize = Tools.getDownloadLimitSize(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			feedIDList = bundle.getStringArrayList(INPUT_EXTRAS_KEY);
		}
		new DownloadThread().start();

		return super.onStartCommand(intent, flags, startId);
	}

	private void exit() {
		// clearOfflineDownloadInfo();
		stopSelf();
	}

	private void setOfflineDownloadInfo(String title, int sum, int count,
			int percent) {
		SharedPreferences prf = this.getSharedPreferences(OFFLINE_INFO_PRF,
				MODE_PRIVATE);
		Editor edit = prf.edit();
		edit.putString(OFFLINE_CURRENT_TITLE, title);
		edit.putInt(OFFLINE_CURRENT_SUM, sum);
		edit.putInt(OFFLINE_CURRENT_NUM, count);
		edit.putInt(OFFLINE_CURRENT_PERCENT, percent);
		edit.putInt(OFFLINE_TOTAL_SUM, downloadSum);
		edit.putLong(OFFLINE_DOWNLOAD_SIZE, downloadSize);
		edit.commit();
	}

	private void clearOfflineDownloadInfo() {
		SharedPreferences prf = this.getSharedPreferences(OFFLINE_INFO_PRF,
				MODE_PRIVATE);
		Editor edit = prf.edit();
		edit.clear();
		edit.commit();
		appData.setIsOfflineDown(false);
		appData.setIsOfflineDownCancle(false);
		appData.setIsOfflineDownFinish(false);
	}

	private String downloadFeed(String feedid) {
		RSSFeed feed = RSSFeedDataManager.getRSSFeedDataManager(this)
				.getRSSFeed(feedid);
		String url = feed.getLink();
		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.get(url, null, response);
		if (code != HttpStatus.SC_OK)
			return null;

		HttpEntity entity = response.getBody();
		if (entity != null) {
			try {
				InputStream is = entity.getContent();
				downloadSize += entity.getContentLength();
				String filename = FileUtil.DETAILPAGE_DIR + feedid;
				FileUtil.write(filename, is, false);
				is.close();
				return filename;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	private void downloadItems(String feedid) {
		if (itemList == null || feedid == null) {
			return;
		}

		RSSFeed feed = RSSFeedDataManager.getRSSFeedDataManager(this)
				.getRSSFeed(feedid);
		if (feed == null) {
			return;
		}

		String title = feed.getTitle();
		int currentSize = itemList.size();
		int downloadCount = 0;
		int failedCount = 0;
		sendDownloadMsg(currentSize, downloadCount, 0, title);
		setOfflineDownloadInfo(title, currentSize, downloadCount, 0);
		currentSize = currentSize > downloadLimitSize ? downloadLimitSize
				: currentSize;
		for (int i = 0; i < currentSize; i++) {
			RSSItem item = itemList.get(i);
			if (!Tools.CheckNetwork(mContext)) {
				return;
			}

			if (!Tools.CheckSDCard()) {
				return;
			}

			if (appData.isOfflineDownCancle()) {
				mHandler.sendEmptyMessage(MSG_DOWNLOAD_CANCEL);
				return;
			}

			String url = item.getLink();
			String itemid = item.getUID();
			MyHttpResponse response = new MyHttpResponse();
			int code = HttpBase.get(url, null, response);
			if (code != HttpStatus.SC_OK) {
				itemFailedCount++;
				failedCount++;
				if (failedCount == currentSize) {
					break;
				}
				continue;
			}

			HttpEntity entity = response.getBody();
			if (entity != null) {
				try {
					InputStream is = entity.getContent();
					downloadSize += entity.getContentLength();
					String filename = FileUtil.DETAILPAGE_DIR + itemid;
					if (!FileUtil.exists(filename)) {
						FileUtil.write(filename, is, false);
					}
					is.close();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			downloadCount++;
			int percent = (int) (downloadCount * 1.0 / currentSize * 100);
			sendDownloadMsg(currentSize, downloadCount, percent, title);

			downloadSum++;

			setOfflineDownloadInfo(title, currentSize, downloadCount, percent);
		}

		if (downloadCount == 0) {
			Message msg = new Message();
			msg.what = MSG_DOWNLOAD_FAILED;
			Bundle bundle = new Bundle();
			bundle.putString(DOWNLOAD_FEEDNAME_KEY, title);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}

	private void sendDownloadMsg(int totalSize, int finishSize, int percent,
			String title) {
		Message msg = new Message();
		msg.what = MSG_DOWNLOADING;
		Bundle bundle = new Bundle();
		bundle.putInt(DOWNLOAD_SUM_KEY, totalSize);
		bundle.putInt(DOWNLOAD_COUNT_KEY, finishSize);
		bundle.putInt(DOWNLOAD_PERCENT_KEY, percent);
		bundle.putString(DOWNLOAD_FEEDNAME_KEY, title);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private void doMsgDownloading(Message msg) {
		Bundle bundle = msg.getData();
		if (bundle == null)
			return;
		String title = getString(R.string.offline_down_title_tip)
				+ bundle.getString(DOWNLOAD_FEEDNAME_KEY);
		int sum = bundle.getInt(DOWNLOAD_COUNT_KEY);
		int count = bundle.getInt(DOWNLOAD_SUM_KEY);
		int percent = bundle.getInt(DOWNLOAD_PERCENT_KEY);
		String num_tip = sum + "/" + count;
		setNotification(R.layout.offline_down_noti, R.drawable.noti_down_icon,
				title);
		remoteView.setProgressBar(R.id.noti_progress, 100, percent, false);
		remoteView.setTextViewText(R.id.noti_title_tip, title);
		remoteView.setTextViewText(R.id.noti_num_tip, num_tip);
		remoteView.setTextViewText(R.id.noti_persent_tip,
				String.format("%s%%", percent));
		notifiManager.notify(NOTIFICATION_ID, notification);
		Logger.v("OfflineDownloadService doMsgDownloading percent = " + percent);
	}

	private void doMsgDownloadError(Message msg) {
		String content = null;
		if (msg.what == MSG_DOWNLOAD_ERROR_NET) {
			content = getString(R.string.offline_down_failed_net);
		} else {
			content = getString(R.string.offline_down_failed_sd);
		}

		content += String.format(getString(R.string.offline_down_stats_tip),
				downloadSum, FileUtil.formatFileSize(downloadSize));
		String title = getString(R.string.offline_down_failed_title_tip);
		setNotification(R.layout.offline_down_noti_finish,
				R.drawable.noti_down_failed, title);
		remoteView.setTextViewText(R.id.noti_finish_title_tip, title);
		remoteView.setTextViewText(R.id.noti_finish_tip, content);
		remoteView.setImageViewResource(R.id.noti_finish_icon,
				R.drawable.noti_down_failed);
		notifiManager.notify(NOTIFICATION_ID, notification);
	}

	private void doMsgDownloadFinish(Message msg) {
		String title = getString(R.string.offline_down_success_title_tip);
		String content = String.format(
				getString(R.string.offline_down_stats_tip), downloadSum,
				FileUtil.formatFileSize(downloadSize))
				+ getString(R.string.offline_down_finish_tip);
		setNotification(R.layout.offline_down_noti_finish,
				R.drawable.noti_down_icon, title);
		remoteView.setTextViewText(R.id.noti_finish_title_tip, title);
		remoteView.setTextViewText(R.id.noti_finish_tip, content);
		notifiManager.notify(NOTIFICATION_ID, notification);
	}

	private void doMsgDownloadFailed(Message msg) {
		Bundle bundle = msg.getData();
		String title = bundle.getString(DOWNLOAD_FEEDNAME_KEY)
				+ getString(R.string.offline_down_failed);
		setNotification(R.layout.offline_down_noti_finish,
				R.drawable.noti_down_failed, title);
		remoteView.setTextViewText(R.id.noti_finish_title_tip, title);
		remoteView.setTextViewText(R.id.noti_finish_tip, "");
		remoteView.setImageViewResource(R.id.noti_finish_icon,
				R.drawable.noti_down_failed);
		notifiManager.notify(NOTIFICATION_ID, notification);
	}

	private void sendDownloadFailedBroadcast(int type) {
		Intent intent = new Intent(ACTION_OFFLINE_FAILED);
		intent.putExtra(EXTRAS_TYPE_KEY, type);
		sendBroadcast(intent);
	}

	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == MSG_DOWNLOADING) {
				doMsgDownloading(msg);
			} else if (msg.what == MSG_DOWNLOAD_CANCEL) {
				appData.setIsOfflineDown(false);
				appData.setIsOfflineDownFinish(false);
				appData.setIsOfflineDownCancle(false);
				notifiManager.cancel(NOTIFICATION_ID);
			} else if (msg.what == MSG_DOWNLOAD_FINISH) {
				doMsgDownloadFinish(msg);
				exit();
			} else if (msg.what == MSG_DOWNLOAD_ERROR_NET) {
				doMsgDownloadError(msg);
				sendDownloadFailedBroadcast(OFFLINE_FAILED_TYPE_NET);
				exit();
			} else if (msg.what == MSG_DOWNLOAD_ERROR_SD) {
				doMsgDownloadError(msg);
				sendDownloadFailedBroadcast(OFFLINE_FAILED_TYPE_SD);
				exit();
			} else if (msg.what == MSG_DOWNLOAD_FAILED) {
				doMsgDownloadFailed(msg);
			}
		}

	}

	class DownloadThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (feedIDList != null) {
				appData.setIsOfflineDown(true);
				for (String feedid : feedIDList) {
					if (appData.isOfflineDownCancle()) {
						mHandler.sendEmptyMessage(MSG_DOWNLOAD_CANCEL);
						return;
					}

					if (!Tools.CheckNetwork(mContext)) {
						mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR_NET);
						return;
					}

					if (!Tools.CheckSDCard()) {
						mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR_SD);
						return;
					}

					String filename = downloadFeed(feedid);
					if (filename == null) {
						feedFailedCount++;
						continue;
					}

					itemList = RSSUtil.loadItemsFromLocal(filename, feedid);
					if (itemList == null) {
						feedFailedCount++;
						continue;
					}

					downloadItems(feedid);
				}

				appData.setIsOfflineDownFinish(true);
			}

			Message msg = new Message();
			msg.what = MSG_DOWNLOAD_FINISH;
			mHandler.sendMessage(msg);
		}

	}
}
