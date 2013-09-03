package su.rss.activity;

import su.rss.util.*;
import su.rss.application.MyApplication;
import su.rss.service.OfflineDownloadService;
import su.rss.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OfflineDownloadActivity extends BaseActivity {

	private final String OFFLINE_FAILED_BROADCAST = "su.rss.service.OfflineDownloadService.ACTION_OFFLINE_FAILED";
	private String current_title_pre = "正在下载：";
	private String download_title_str = ""; 
	private String need_download_sum = "";
	private String current_downloaded = "0";
	private String download_no_str = ""; // 1/10
	private String current_network_str = "当前网络：";
	private String note_str = ""; // 提示语：系统将为你下载……

	private TextView download_title;
	private TextView download_no; // 1/10
	private MyProgress myProgress;
	private TextView current_network;
	private TextView note; 

	private Button cancelButton;
	private Handler handler;
	private Runnable runnable;
	private int percent;
	private MyApplication myapp;
	private SharedPreferences sharedPreferences;
	private boolean isOfflineDownloadError = false;
	OfflineDownloadErrorReceiver receiver;

	private ImageButton mBackBtn;
	private View mTitleBar;
	private TextView mTitleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.offlinedownload);
		initLayout(); 
		registerBroadcastReceiver();
		cancelButton.setOnClickListener(new myOnClickListener());
		myapp = (MyApplication) getApplication();
		showLayout();
	}

	private void update_layout() {
		sharedPreferences = getSharedPreferences(
				OfflineDownloadService.OFFLINE_INFO_PRF, Context.MODE_PRIVATE);
		download_title_str = current_title_pre
				+ sharedPreferences.getString(
						OfflineDownloadService.OFFLINE_CURRENT_TITLE, "");
		int value = sharedPreferences.getInt(
				OfflineDownloadService.OFFLINE_CURRENT_NUM, 0);
		current_downloaded = String.valueOf(value);
		value = sharedPreferences.getInt(
				OfflineDownloadService.OFFLINE_CURRENT_SUM, 0);
		need_download_sum = String.valueOf(value);
		download_no_str = current_downloaded + "/" + need_download_sum;
		download_title.setText(download_title_str);
		download_no.setText(download_no_str);
		percent = sharedPreferences.getInt(
				OfflineDownloadService.OFFLINE_CURRENT_PERCENT, 0);
		myProgress.setProgress(percent);
	}

	private void off_download_ok(){

		cancelButton.setText(R.string.offline_down_success_button);
		myProgress.setVisibility(View.GONE);
		download_no.setVisibility(View.GONE);
		current_network.setVisibility(View.INVISIBLE);
		download_title.setText(R.string.offline_down_success_title_tip);
		Resources res = OfflineDownloadActivity.this.getResources();

		int articalNO = sharedPreferences.getInt(
				OfflineDownloadService.OFFLINE_TOTAL_SUM, 0);
		long downloadSize = sharedPreferences.getLong(
				OfflineDownloadService.OFFLINE_DOWNLOAD_SIZE, 0);

		note.setText(res.getString(R.string.offline_down_stats_tip, articalNO,
				FileUtil.formatFileSize(downloadSize))
				+ res.getString(R.string.offline_down_finish_tip));
		handler.removeCallbacks(runnable);
	}

	private void updateRunnable() {
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				handler.postDelayed(this, 100);
				update_layout();
				if (myapp.isOfflineDownFinish()) {
					off_download_ok();
				}
			}
		};
		handler.postDelayed(runnable, 100);
	}

	private void showLayout() {
		showTitle();
		if (Tools.isWifi(OfflineDownloadActivity.this))
			current_network_str = current_network_str + "wifi";
		else
			current_network_str = current_network_str + "GPRS";

		note_str = getResources().getString(R.string.offline_download_note);
		current_network.setText(current_network_str);
		note.setText(note_str);
		
		updateRunnable();
	}

	private void showTitle() {
		mTitleBar = findViewById(R.id.title_bar);
		ImageView title_icon = (ImageView) mTitleBar
				.findViewById(R.id.title_bar_icon);
		title_icon.setVisibility(View.GONE);
		mTitleText = (TextView) mTitleBar.findViewById(R.id.title_bar_text);
		mTitleText.setText(getResources().getString(
				R.string.menu_offline_download));
		mBackBtn = (ImageButton) mTitleBar
				.findViewById(R.id.title_favorite_btn);
		mBackBtn.setBackgroundResource(R.drawable.title_home_bg);
		mBackBtn.setImageResource(R.drawable.title_home_icon);
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onApplyTheme() {
		mThemeSettingHelper.setViewBackgroundColor(this, R.id.off_linearlayout,
				R.color.activity_bg_color);
		mThemeSettingHelper.setViewBackground(this, mTitleBar,
				R.drawable.title_bar_bg);
		mThemeSettingHelper.setTextViewColor(this, mTitleText,
				R.color.title_text_color);
		mThemeSettingHelper.setTextViewColor(this,
				(TextView) download_title.findViewById(R.id.download_title),
				R.color.download_text);
		mThemeSettingHelper.setTextViewColor(this,
				(TextView) download_no.findViewById(R.id.download_no),
				R.color.download_text);
		mThemeSettingHelper.setTextViewColor(this, current_network,
				R.color.download_text);
		mThemeSettingHelper.setTextViewColor(this, note, R.color.note_color);
		mThemeSettingHelper.setViewBackground(this, cancelButton,
				R.drawable.download_button_cancel);
		mThemeSettingHelper.setTextViewColor(this, cancelButton,
				R.color.download_text);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	/**
	 * 正在下载：新浪新闻 1/10 进度条百分比
	 * */
	public void initLayout() {
		download_title = (TextView) findViewById(R.id.download_title);
		download_no = (TextView) findViewById(R.id.download_no);
		myProgress = (MyProgress) findViewById(R.id.progressBar);
		current_network = (TextView) findViewById(R.id.current_network);
		note = (TextView) findViewById(R.id.note);
		cancelButton = (Button) findViewById(R.id.OfflineDownloadCancel);

	}

	public void registerBroadcastReceiver() {
		// 注册广播
		IntentFilter filter = new IntentFilter(OFFLINE_FAILED_BROADCAST);
		receiver = new OfflineDownloadErrorReceiver();
		registerReceiver(receiver, filter);

	}

	public void unregisterBroadcastReceiver() {
		// 注销广播
		unregisterReceiver(receiver);

	}

	class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.OfflineDownloadCancel:
				if (isOfflineDownloadError) {
					finish();
					break;
				}

				if (myapp.isOfflineDownFinish()) {
					Intent intent = new Intent(OfflineDownloadActivity.this,
							MainActivity.class);
					OfflineDownloadActivity.this.startService(intent);
					startActivity(intent);
					finish();
				} else {
					myapp.setIsOfflineDownCancle(true);
					finish();
				}
				break;
			/*
			 * case R.id.home_icon_button: finish(); break;
			 */
			default:
				break;
			}
		}

	}

	private void off_download_error(String str_error) {
		download_title.setText(R.string.offline_down_failed_title_tip);// 离线下载失败
		myProgress.setVisibility(View.GONE);
		current_network.setVisibility(View.INVISIBLE);// 当前网络
		note.setText(str_error);
		cancelButton.setText(R.string.offline_down_failed_button);// 我知道了
		isOfflineDownloadError = true;
		handler.removeCallbacks(runnable);
	}

	class OfflineDownloadErrorReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(OFFLINE_FAILED_BROADCAST)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					showBroadcastError(bundle);
				}
			}
		}

		private void showBroadcastError(Bundle bundle) {

			String str = "";
			int articalNO;
			long downloadSize;
			Resources res;
			int failedTypeKey;

			articalNO = sharedPreferences.getInt(
					OfflineDownloadService.OFFLINE_TOTAL_SUM, 0);
			downloadSize = sharedPreferences.getLong(
					OfflineDownloadService.OFFLINE_DOWNLOAD_SIZE, 0);
			res = OfflineDownloadActivity.this.getResources();
			failedTypeKey = bundle
					.getInt(OfflineDownloadService.EXTRAS_TYPE_KEY);
			switch (failedTypeKey) {
			case OfflineDownloadService.OFFLINE_FAILED_TYPE_NET: // 网络不可用
				str = res.getString(R.string.offline_down_failed_net,
						articalNO, FileUtil.formatFileSize(downloadSize));
				break;
			case OfflineDownloadService.OFFLINE_FAILED_TYPE_SD: // SD卡不可用
				str = res.getString(R.string.offline_down_failed_sd, articalNO,
						FileUtil.formatFileSize(downloadSize));
				break;
			default:
				break;
			}
			if (!str.equals("")) {
				off_download_error(str);
			}

		}
	}

}

class MyProgress extends ProgressBar {

	String text;
	Paint mPaint;

	public MyProgress(Context context) {
		super(context);
		initText();
	}

	public MyProgress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initText();
	}

	public MyProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		initText();
	}

	@Override
	public synchronized void setProgress(int progress) {
		setText(progress);
		super.setProgress(progress);
	}

	private void setText(int progress) {
		int i = (progress * 100) / this.getMax();
		this.text = String.valueOf(i) + "%";
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = (getWidth() - 2 * rect.centerX()) - 7;
		int y = (getHeight() / 2 - rect.centerY());
		canvas.drawText(this.text, x, y, this.mPaint);
	}

	private void initText() {
		this.mPaint = new Paint();

		this.mPaint.setColor(Color.WHITE);
	}

}
