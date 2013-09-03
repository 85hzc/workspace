package su.rss.activity;

import java.util.ArrayList;

import su.rss.R;
import su.rss.data.PicData;
import su.rss.data.RSSFeed;
import su.rss.drag.CellInfo;
import su.rss.drag.DragController;
import su.rss.drag.DragController.DragListener;
import su.rss.drag.DragLayer;
import su.rss.drag.DragSource;
import su.rss.drag.WorkSpace;
import su.rss.executer.DownloadFeedsExecuter;
import su.rss.executer.DownloadPicExecuter;
import su.rss.executer.Executer.ExecuterListener;
import su.rss.manager.PicManager;
import su.rss.manager.RSSFeedDataManager;
import su.rss.service.OfflineDownloadService;
import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.ThemeSettingHelper;
import su.rss.util.Tools;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnLongClickListener,
		DragListener, OnClickListener, ExecuterListener {

	public static final String ACTION_FEED_ADD = "feed_add";
	public static final String ACTION_FEED_REMOVE = "feed_remove";
	public static final String EXTRAS_FEED = "feed_extras";

	private static final int MSG_UPDATE_VIEW = 1;
	private static final int MSG_ADD_VIEW = 2;

	private DragController mDragController;
	private DragLayer mDragLayer;
	private WorkSpace mWorkspace;
	private ScrollView mScrollView;
	private View mDragView;
	private ImageButton mFavBtn;
	private ImageButton mAddBtn;
	private ImageButton mExitDragBtn;
	private View mCellAddView;
	private View mTitleBar;
	private View mLoadingBar;
	private boolean isDrag;
	private ArrayList<RSSFeed> feedList;
	private Context mContext;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (ACTION_FEED_ADD.equals(action)) {
				RSSFeed feed = (RSSFeed) intent
						.getSerializableExtra(EXTRAS_FEED);
				feed.setPosition(feedList.size());
				RSSFeedDataManager.getRSSFeedDataManager(mContext).addRSSFeed(feed);
				feedList.add(feed);
				addView(feed);
				downloadPic(feed);
			} else if (ACTION_FEED_REMOVE.equals(action)) {
				RSSFeed feed = (RSSFeed) intent
						.getSerializableExtra(EXTRAS_FEED);
				removeView(feed);
				RSSFeedDataManager.getRSSFeedDataManager(mContext).deleteRSSFeed(feed.getUID());
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		registerReceiver();
		mDragController = new DragController(this);
		setupViews();
		mContext = this;
		isDrag = false;
		feedList = RSSFeedDataManager.getRSSFeedDataManager(this)
				.getAllRSSFeed("userid");
		addViews(feedList);
		if (Tools.CheckNetwork(this)) {
			DownloadFeedsExecuter executer = new DownloadFeedsExecuter(
					DownloadFeedsExecuter.RSS_URL_PUSH);
			executer.setListener(this);
			executer.start();
		} else {
			Toast.makeText(this, getString(R.string.net_unuseful),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterReceiver();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (isDrag) {
				exitDrag();
			}
		}
		return super.onTouchEvent(event);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FEED_ADD);
		filter.addAction(ACTION_FEED_REMOVE);
		registerReceiver(mReceiver, filter);
	}

	private void unRegisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	private void setupViews() {
		DragController dragController = mDragController;
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mWorkspace = (WorkSpace) findViewById(R.id.workspace);
		mScrollView = (ScrollView) findViewById(R.id.scroll_view);
		mTitleBar = findViewById(R.id.title_bar);
		mFavBtn = (ImageButton) mTitleBar.findViewById(R.id.title_favorite_btn);
		mAddBtn = (ImageButton) mTitleBar.findViewById(R.id.title_add_btn);
		mLoadingBar = findViewById(R.id.loading);
		mCellAddView = findViewById(R.id.add_cell);

		mDragLayer.setDragController(dragController);
		// mWorkspace.setOnLongClickListener(this);
		mWorkspace.setDragController(dragController);
		mWorkspace.setHapticFeedbackEnabled(false);
		mWorkspace.setScrollView(mScrollView);

		mDragController.setDragScoller(mWorkspace);
		mDragController.setScrollView(mScrollView);
		mDragController.addDropTarget(mWorkspace);
		mDragController.setDragListener(this);

		mFavBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
		mAddBtn.setVisibility(View.VISIBLE);

		mWorkspace.setOnClickListener(this);
		mCellAddView.setOnClickListener(this);
	}

	@Override
	public void onApplyTheme() {
		// TODO Auto-generated method stub
		mThemeSettingHelper.setViewBackground(this, mTitleBar,
				R.drawable.title_bar_bg);
		mThemeSettingHelper.setTextViewColor(this,
				(TextView) mTitleBar.findViewById(R.id.title_bar_text),
				R.color.title_text_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mDragLayer,
				R.color.activity_bg_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mWorkspace,
				R.color.activity_bg_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mScrollView,
				R.color.activity_bg_color);

		int count = mWorkspace.getChildCount() - 1;
		for (int i = 0; i < count; i++) {
			View v = mWorkspace.getChildAt(i);
			TextView text = (TextView) v.findViewById(R.id.cell_text);
			mThemeSettingHelper.setTextViewColor(this, text, R.color.white);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		if (v == mCellAddView) {
			return false;
		}

		mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
				HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
		showDeleteButton();
		ImageButton btn = (ImageButton) v.findViewById(R.id.cell_del);
		btn.setVisibility(View.GONE);
		mWorkspace.startDrag(v);
		showExitDragBtn();
		mDragView = v;
		isDrag = true;

		mCellAddView.setVisibility(View.GONE);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, getString(R.string.menu_offline_download)).setIcon(
				R.drawable.menu_down_icon);
		if (ThemeSettingHelper.THEME_DEFAULT.equals(mThemeSettingHelper
				.getCurrentThemePackage())) {
			menu.add(0, 1, 1, getString(R.string.menu_theme_night)).setIcon(
					R.drawable.night_icon);
		} else {
			menu.add(0, 1, 1, getString(R.string.menu_theme_light)).setIcon(
					R.drawable.night_icon);
		}
		menu.add(0, 2, 2, getString(R.string.menu_setting)).setIcon(
				R.drawable.setting_icon);
		menu.add(0, 3, 3, getString(R.string.menu_about)).setIcon(
				R.drawable.about_icon);
		menu.add(0, 4, 4, getString(R.string.menu_help)).setIcon(
				R.drawable.help_icon);
		menu.add(0, 5, 5, getString(R.string.menu_exit)).setIcon(
				R.drawable.exit_icon);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			if (Tools.isWifi(this)) {
				startOfflineDownload();
			} else {
				showNetInfoDlg();
			}

			break;
		case 1:
			if (ThemeSettingHelper.THEME_DEFAULT.equals(mThemeSettingHelper
					.getCurrentThemePackage())) {
				mThemeSettingHelper.changeTheme(this,
						ThemeSettingHelper.THEME_NIGHT);
				item.setTitle(R.string.menu_theme_light);
			} else {
				mThemeSettingHelper.changeTheme(this,
						ThemeSettingHelper.THEME_DEFAULT);
				item.setTitle(R.string.menu_theme_night);
			}
			break;
		case 2:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragEnd() {
		// TODO Auto-generated method stub
		ImageButton btn = (ImageButton) mDragView.findViewById(R.id.cell_del);
		btn.setVisibility(View.VISIBLE);
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = mWorkspace.getChildCount() - 1;
				for (int i = 0; i < count; i++) {
					View child = mWorkspace.getChildAt(i);
					CellInfo info = (CellInfo) child.getTag();
					RSSFeed feed = info.getFeed();
					if (feed.getProperty() != RSSFeed.PROPERTY_ORG) {
						feed.setPosition(info.getPos());
						RSSFeedDataManager.getRSSFeedDataManager(mContext)
								.updateRSSFeed(feed);
					}
				}
			}

		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isDrag) {
				exitDrag();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mFavBtn) {
			if (isDrag) {
				exitDrag();
			} else {
				startActivity(new Intent(this, CollectActivity.class));
			}
		} else if (v == mAddBtn) {
			startActivity(new Intent(this, RSSAddActivity.class));
		} else if (v == mCellAddView) {
			startActivity(new Intent(this, RSSAddActivity.class));
		} else if (!isDrag) {
			Intent intent = new Intent(this, ItemListActivity.class);
			CellInfo info = (CellInfo) v.getTag();
			intent.putExtra(ItemListActivity.INPUT_DATA_KEY,
					feedList.get(info.getViewPos()));
			startActivity(intent);
		} else {
			exitDrag();
		}
	}

	private void addViews(ArrayList<RSSFeed> list) {
		if (list == null)
			return;

		int count = list.size();
		LayoutInflater inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < count; i++) {
			RSSFeed feed = list.get(i);
			View v = inflate.inflate(R.layout.cell_layer, null);
			setupCellViews(v, feed);
			setDeleteListener(v);
			v.setOnClickListener(this);
			CellInfo info = new CellInfo();
			if (feed.getProperty() == RSSFeed.PROPERTY_ORG) {
				info.setMovingEnable(false);

			} else {
				info.setMovingEnable(true);
				v.setOnLongClickListener(this);
			}
			info.setFeed(feed);
			info.setView(v);
			v.setTag(info);
			mWorkspace.addView(v, i);

		}

		mLoadingBar.setVisibility(View.GONE);
	}

	private void addView(RSSFeed feed) {
		LayoutInflater inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.cell_layer, null);
		setupCellViews(v, feed);
		setDeleteListener(v);
		v.setOnClickListener(this);
		CellInfo info = new CellInfo();
		if (feed.getProperty() == RSSFeed.PROPERTY_ORG) {
			info.setMovingEnable(false);
		} else {
			info.setMovingEnable(true);
			v.setOnLongClickListener(this);
		}
		info.setFeed(feed);
		info.setView(v);
		v.setTag(info);
		mWorkspace.addView(v, mWorkspace.getChildCount() - 1);
	}

	private void removeView(RSSFeed feed) {
		String id = feed.getUID();
		int count = feedList.size();
		for (int i = 0; i < count; i++) {
			if (id.equals(feedList.get(i).getUID())) {
				mWorkspace.removeViewAt(i);
				feedList.remove(i);
				break;
			}
		}
	}

	private void updateViews(ArrayList<RSSFeed> list) {
		if (list == null)
			return;

		int viewCount = mWorkspace.getChildCount() - 1;
		int listCount = list.size();
		int count = viewCount > listCount ? listCount : viewCount;
		Logger.v("MainActivity updateViews viewCount = " + viewCount
				+ ", listCount = " + listCount + ", count = " + count);
		for (int i = 0; i < count; i++) {
			RSSFeed feed = list.get(i);
			View child = mWorkspace.getChildAt(i);
			setupCellViews(child, feed);
		}
	}

	private void setupCellViews(View view, RSSFeed feed) {
		TextView text = (TextView) view.findViewById(R.id.cell_text);
		text.setText(feed.getTitle());
		mThemeSettingHelper.setTextViewColor(this, text, R.color.white);
		ArrayList<PicData> picList = PicManager.getPicManager(this)
				.getAllPicData(feed.getUID());
		if (picList == null) {
			Logger.v("setupCellViews picList is null");
			return;
		}
		ImageView img = (ImageView) view.findViewById(R.id.cell_img);
		Bitmap bitmap = BitmapFactory.decodeFile(picList.get(0).getImagePath());
		if (bitmap == null)
			return;
		img.setImageBitmap(bitmap);
		// img.setBackgroundDrawable(new BitmapDrawable(bitmap));
	}

	private void showExitDragBtn() {
		mExitDragBtn = mFavBtn;
		mExitDragBtn.setBackgroundResource(R.drawable.exit_drag_btn_bg);
		mExitDragBtn.setImageResource(R.drawable.exit_drag_icon);
		mAddBtn.setVisibility(View.GONE);
	}

	private void hideExitDragBtn() {
		mFavBtn.setBackgroundResource(R.drawable.title_fav_bg);
		mFavBtn.setImageResource(R.drawable.title_fav_icon);
		mAddBtn.setVisibility(View.VISIBLE);
	}

	private void showDeleteButton() {
		int count = mWorkspace.getChildCount() - 1;
		for (int i = 0; i < count; i++) {
			RSSFeed feed = feedList.get(i);
			if (feed.getProperty() != RSSFeed.PROPERTY_ORG) {
				View v = mWorkspace.getChildAt(i);
				ImageButton btn = (ImageButton) v.findViewById(R.id.cell_del);
				btn.setVisibility(View.VISIBLE);
			}
		}
	}

	private void removeFeed(int pos) {
		RSSFeed feed = feedList.get(pos);
		RSSFeedDataManager.getRSSFeedDataManager(this).deleteRSSFeed(
				feed.getUID());
	}

	private void setDeleteListener(final View view) {
		ImageButton btn = (ImageButton) view.findViewById(R.id.cell_del);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CellInfo info = (CellInfo) view.getTag();
				int pos = info.getViewPos();
				removeFeed(pos);
				mWorkspace.removeView(view);
				int count = mWorkspace.getChildCount();
				if (count == 1) {
					exitDrag();
				}
			}

		});
	}

	private void exitDrag() {
		hideExitDragBtn();
		mCellAddView.setVisibility(View.VISIBLE);
		int count = mWorkspace.getChildCount() - 1;
		for (int i = 0; i < count; i++) {
			View v = mWorkspace.getChildAt(i);
			ImageButton btn = (ImageButton) v.findViewById(R.id.cell_del);
			btn.setVisibility(View.GONE);
		}

		isDrag = false;
	}

	@Override
	public void onResult(Object result) {
		// TODO Auto-generated method stub
		if (result == null)
			return;

		ArrayList<RSSFeed> list = (ArrayList<RSSFeed>) result;
		RSSFeedDataManager manager = RSSFeedDataManager
				.getRSSFeedDataManager(this);

		if (feedList != null) {
			for (RSSFeed feed : feedList) {
				manager.updateRSSFeed(feed);
				downloadPic(feed);
			}
		} else {
			Message msg = new Message();
			msg.obj = list;
			msg.what = MSG_ADD_VIEW;
			mHandler.sendMessage(msg);
			for (RSSFeed feed : list) {
				manager.addRSSFeed(feed);
				downloadPic(feed);
			}

			feedList = list;
		}
	}

	private void downloadPic(RSSFeed feed) {
		if (feed == null || feed.getImagePath() == null)
			return;

		PicManager mgr = PicManager.getPicManager(this);
		String link = feed.getImagePath();
		String relateid = feed.getUID();
		String id = Tools.getHashID(link);
		if (!mgr.isPicSaved(id, relateid)) {
			downloadPic(link, relateid);
		} else {
			PicData data = mgr.getPicData(id, relateid);
			if (!FileUtil.exists(data.getImagePath())) {
				mgr.deletePicData(id, relateid);
				downloadPic(link, relateid);
			}
		}
	}

	private void downloadPic(String link, String relateid) {
		DownloadPicExecuter picExecuter = new DownloadPicExecuter(this, link,
				relateid);
		picExecuter.setListener(new PicDownloadListener());
		picExecuter.start();
	}

	private void startOfflineDownload() {
		Intent intent = new Intent(this, OfflineDownloadService.class);
		Bundle bundle = new Bundle();
		ArrayList<String> list = new ArrayList<String>();
		for (RSSFeed feed : feedList) {
			list.add(feed.getUID());
		}
		bundle.putStringArrayList(OfflineDownloadService.INPUT_EXTRAS_KEY, list);
		intent.putExtras(bundle);
		startService(intent);

		Toast.makeText(
				this,
				String.format(getString(R.string.offline_download_toast_all),
						Tools.getDownloadLimitSize(this)), Toast.LENGTH_SHORT)
				.show();
	}

	private void showNetInfoDlg() {
		AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.setTitle(getString(R.string.net_dlg_title));
		dlg.setMessage(getString(R.string.net_tip));
		dlg.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});

		dlg.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(R.string.download),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startOfflineDownload();
					}

				});
		dlg.show();
	}

	class PicDownloadListener implements ExecuterListener {

		@Override
		public void onResult(Object result) {
			// TODO Auto-generated method stub
			if (result == null) {
				Logger.e("download imgage failed");
				return;
			}

			Message msg = new Message();
			msg.obj = feedList;
			msg.what = MSG_UPDATE_VIEW;
			mHandler.sendMessage(msg);
		}

	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		Logger.v("MainActivity processMessage what = " + msg.what);

		ArrayList<RSSFeed> list = (ArrayList<RSSFeed>) msg.obj;
		if (list == null)
			list = feedList;

		switch (msg.what) {
		case MSG_UPDATE_VIEW:
			updateViews(list);
			break;
		case MSG_ADD_VIEW:
			addViews(list);
			break;
		}
	}

}
