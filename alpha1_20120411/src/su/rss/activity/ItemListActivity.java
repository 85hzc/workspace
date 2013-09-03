package su.rss.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import su.rss.R;
import su.rss.adapter.RSSItemAdapter;
import su.rss.data.RSSFeed;
import su.rss.data.RSSItem;
import su.rss.executer.DownloadItemsExecuter;
import su.rss.executer.Executer.ExecuterListener;
import su.rss.manager.ReadHistoryManager;
import su.rss.service.OfflineDownloadService;
import su.rss.util.FileUtil;
import su.rss.util.ItemConvert;
import su.rss.util.ItemConvert.ConvertFinishListener;
import su.rss.util.Logger;
import su.rss.util.Tools;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ItemListActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, ExecuterListener, ConvertFinishListener {

	public static final String INPUT_DATA_KEY = "feed";

	private static final int MSG_ITEM_GETED = 1;
	private static final int MSG_ITEM_GET_FAILED = 2;

	private ImageButton mRefreshBtn;
	private ProgressBar mProgressBar;
	private ListView mListView;
	private RSSItemAdapter mAdapter;
	private ArrayList<RSSItem> mItemDataList;
	private ArrayList<HashMap<String, Object>> mAdapterData;
	private View mTitleBar;
	private View mLoadingBar;
	private TextView mTitleText;
	private RSSFeed mFeed;
	private boolean isInitialized;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlist);
		isInitialized = false;
		Intent intent = getIntent();
		mFeed = (RSSFeed) intent.getSerializableExtra(INPUT_DATA_KEY);
		getItems(mFeed);
		setupViews();
	}

	@Override
	public void onApplyTheme() {
		// TODO Auto-generated method stub
		mThemeSettingHelper.setViewBackground(this, mTitleBar,
				R.drawable.title_bar_bg);
		mThemeSettingHelper.setTextViewColor(this, mTitleText,
				R.color.title_text_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mListView,
				R.color.activity_bg_color);
		mThemeSettingHelper.setListViewDividerColor(this, mListView,
				R.color.list_divider_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mLoadingBar,
				R.color.activity_bg_color);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, getString(R.string.menu_offline_download)).setIcon(
				R.drawable.menu_down_icon);
		menu.add(0, 1, 1, getString(R.string.menu_refresh_name)).setIcon(
				R.drawable.menu_refersh_icon);
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
			getItems(mFeed);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupViews() {
		mLoadingBar = findViewById(R.id.loading);
		mTitleBar = findViewById(R.id.title_bar);
		mRefreshBtn = (ImageButton) mTitleBar
				.findViewById(R.id.title_favorite_btn);
		mProgressBar = (ProgressBar) mTitleBar.findViewById(R.id.refresh_wait);
		mRefreshBtn.setBackgroundResource(R.drawable.title_refresh_bg);
		mRefreshBtn.setImageResource(R.drawable.title_refresh_icon);
		mRefreshBtn.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.rss_item_list);
		ImageView title_icon = (ImageView) mTitleBar
				.findViewById(R.id.title_bar_icon);
		title_icon.setVisibility(View.GONE);
		mTitleText = (TextView) mTitleBar.findViewById(R.id.title_bar_text);
		mTitleText.setText(mFeed.getTitle());
		setListData();

		mListView.setOnItemClickListener(this);
	}

	private void setListData() {
		// initData();
		mItemDataList = new ArrayList<RSSItem>();
		mAdapterData = new ArrayList<HashMap<String, Object>>();
		mAdapter = new RSSItemAdapter(this, mAdapterData, true);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mRefreshBtn && isInitialized) {
//			mRefreshBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
			mProgressBar.setVisibility(View.VISIBLE);
			mRefreshBtn.setVisibility(View.GONE);
			FileUtil.deleteFile(FileUtil.DETAILPAGE_DIR + mFeed.getUID());
			getItems(mFeed);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		RSSItem item = mItemDataList.get(arg2);
		Intent intent = new Intent(this, ReaderActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ReaderActivity.ITEM_KEY, item);
		intent.putExtras(bundle);
		startActivity(intent);
		ReadHistoryManager mgr = ReadHistoryManager.getHistoryManager(this);
		if (!mgr.isItemRead(item.getUID(), app.getUserID())) {
			Date date = new Date(System.currentTimeMillis());
			mgr.addReadHistory(item.getUID(), date.toGMTString(),
					app.getUserID());
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResult(Object result) {
		// TODO Auto-generated method stub
		if (result == null) {
			mHandler.sendEmptyMessage(MSG_ITEM_GET_FAILED);
			return;
		}

		ArrayList<RSSItem> list = (ArrayList<RSSItem>) result;
		mItemDataList.clear();
		mItemDataList.addAll(list);
		ItemConvert coverter = new ItemConvert(list);
		coverter.setConvertFinishListener(this);
		coverter.start();
	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MSG_ITEM_GETED:
			isInitialized = true;
			mAdapterData.clear();
			mAdapterData.addAll((ArrayList<HashMap<String, Object>>) msg.obj);
			mAdapter.notifyDataSetChanged();
			mLoadingBar.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.GONE);
			mRefreshBtn.setVisibility(View.VISIBLE);
			Logger.v("item data have get");
			break;
		case MSG_ITEM_GET_FAILED:
			isInitialized = true;
			mLoadingBar.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.GONE);
			mRefreshBtn.setVisibility(View.VISIBLE);
			Toast.makeText(this, getString(R.string.download_failed),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void getItems(RSSFeed feed) {
		DownloadItemsExecuter executer = new DownloadItemsExecuter(this,
				feed.getLink(), feed.getUID());
		executer.setListener(this);
		executer.start();
	}

	private void startOfflineDownload() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(mFeed.getUID());
		Intent intent = new Intent(this, OfflineDownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(OfflineDownloadService.INPUT_EXTRAS_KEY, list);
		intent.putExtras(bundle);
		startService(intent);
		
		Toast.makeText(
				this,
				String.format(getString(R.string.offline_download_toast_item),
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

	@Override
	public void onFinish(ArrayList<HashMap<String, Object>> list) {
		// TODO Auto-generated method stub
		if(list != null) {
			Message msg = new Message();
			msg.what = MSG_ITEM_GETED;
			msg.obj = list;
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendEmptyMessage(MSG_ITEM_GET_FAILED);
		}
	}
}
