package su.rss.activity;

import java.util.ArrayList;
import java.util.HashMap;

import su.rss.R;
import su.rss.adapter.RSSItemAdapter;
import su.rss.adapter.RSSItemAdapter.onDelBtnClickListener;
import su.rss.data.RSSItem;
import su.rss.manager.CollectManager;
import su.rss.manager.RSSItemDataManager;
import su.rss.util.ItemConvert;
import su.rss.util.ItemConvert.ConvertFinishListener;
import su.rss.util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CollectActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, onDelBtnClickListener, ConvertFinishListener {

	public static final String ACTION_REMOVE_FAVORITE = "action_remove";
	public static final String INPUT_ITEMID_KEY = "item_id";
	
	private static final int STATE_EDITING = 1;
	private static final int STATE_NORMAL = 2;
	
	private static final int MSG_ITEM_CONVERTED = 1;
	private static final int MSG_ITEM_CONVERT_FAILED = 2;

	private ImageButton mEditBtn;
	private ImageButton mEditCommitBtn;
	private ListView mListView;
	private View mTitleBar;
	private View mLoadingBar;
	private TextView mTitleText;
	private RSSItemAdapter mAdapter;
	private ArrayList<RSSItem> mItemDataList;
	private ArrayList<HashMap<String, Object>> mAdapterData;
	private Handler mHandler;
	private int mEditState;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(ACTION_REMOVE_FAVORITE.equals(action)) {
				String itemid = intent.getStringExtra(INPUT_ITEMID_KEY);
				int size = mItemDataList.size();
				for(int i = 0; i < size; i++) {
					RSSItem item = mItemDataList.get(i);
					if(itemid.equals(item.getUID())) {
						removeData(i);
						break;
					}
				}
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlist);
		createHandler();
		setupViews();
		mEditState = STATE_NORMAL;
		registerReceiver();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
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
		if(mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REMOVE_FAVORITE);
		registerReceiver(mReceiver, filter);
	}
	
	private void createHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case MSG_ITEM_CONVERTED:
					mAdapterData.clear();
					mAdapterData.addAll((ArrayList<HashMap<String, Object>>) msg.obj);
					mAdapter.notifyDataSetChanged();
					break;
				case MSG_ITEM_CONVERT_FAILED:
					break;
				}
			}
			
		};
	}

	private void setupViews() {
		mTitleBar = findViewById(R.id.title_bar);
		mEditBtn = (ImageButton) mTitleBar
				.findViewById(R.id.title_favorite_btn);
		ImageView title_icon = (ImageView) mTitleBar
				.findViewById(R.id.title_bar_icon);
		title_icon.setVisibility(View.GONE);
		mTitleText = (TextView) mTitleBar.findViewById(R.id.title_bar_text);
		mTitleText.setText(getString(R.string.fav_title_name));
		mListView = (ListView) findViewById(R.id.rss_item_list);
		mLoadingBar = findViewById(R.id.loading);

		mEditBtn.setOnClickListener(this);
		showEditBtn();

		setListView();
		mListView.setOnItemClickListener(this);
		mLoadingBar.setVisibility(View.GONE);
	}

	private void setListView() {
		initData();
		mAdapter = new RSSItemAdapter(this, mAdapterData, false);
		mListView.setAdapter(mAdapter);
		mAdapter.setOnDelBtnClickListener(this);
	}

	private void initData() {
		mAdapterData = new ArrayList<HashMap<String, Object>>();
		mItemDataList = new ArrayList<RSSItem>();
		ArrayList<RSSItem> list = CollectManager.getCollectManager(this).getAllCollect();
		if(list != null) {
			mItemDataList.addAll(list);
			ItemConvert coverter = new ItemConvert(list);
			coverter.setConvertFinishListener(this);
			coverter.start();
		}
	}

	private void showEditBtn() {
		mEditBtn.setBackgroundResource(R.drawable.title_fav_edit_bg);
		mEditBtn.setImageResource(R.drawable.title_fav_edit_icon);

	}

	private void showCommitBtn() {
		mEditCommitBtn = mEditBtn;
		mEditCommitBtn.setBackgroundResource(R.drawable.exit_drag_btn_bg);
		mEditCommitBtn.setImageResource(R.drawable.exit_drag_icon);
	}

	private void hideDeleteBtn() {
		mAdapter.setDelZoneVisibility(View.GONE);
		mAdapter.notifyDataSetChanged();
	}

	private void showDeleteBtn() {
		mAdapter.setDelZoneVisibility(View.VISIBLE);
		mAdapter.notifyDataSetChanged();
	}
	
	private void removeData(int position) {
		RSSItem item = mItemDataList.get(position);
		CollectManager.getCollectManager(this).deleteCollect(item.getUID());
		RSSItemDataManager.getRSSItemDataManager(this).deleteRSSItem(item.getUID());
		mItemDataList.remove(position);
		mAdapterData.remove(position);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mEditState == STATE_NORMAL) {
			showCommitBtn();
			showDeleteBtn();
			mEditState = STATE_EDITING;
		} else if (mEditState == STATE_EDITING) {
			showEditBtn();
			hideDeleteBtn();
			mEditState = STATE_NORMAL;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ReaderActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ReaderActivity.ITEM_KEY, mItemDataList.get(arg2));
		bundle.putBoolean(ReaderActivity.FAV_KEY, true);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onClick(View v, int position) {
		// TODO Auto-generated method stub
		removeData(position);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mEditState == STATE_EDITING) {
				showEditBtn();
				hideDeleteBtn();
				mEditState = STATE_NORMAL;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFinish(ArrayList<HashMap<String, Object>> list) {
		// TODO Auto-generated method stub
		if(list != null) {
			Message msg = new Message();
			msg.what = MSG_ITEM_CONVERTED;
			msg.obj = list;
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendEmptyMessage(MSG_ITEM_CONVERT_FAILED);
		}
	}

}
