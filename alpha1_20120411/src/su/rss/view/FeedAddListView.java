package su.rss.view;

import java.util.ArrayList;
import java.util.HashMap;

import su.rss.R;
import su.rss.activity.FeedAddActivity;
import su.rss.activity.MainActivity;
import su.rss.adapter.FeedAddAdapter;
import su.rss.adapter.FeedAddAdapter.OnBtnClickListener;
import su.rss.data.CategoryData;
import su.rss.data.RSSFeed;
import su.rss.executer.DownloadCateGoryExecuter;
import su.rss.executer.DownloadFeedsExecuter;
import su.rss.executer.Executer.ExecuterListener;
import su.rss.manager.RSSFeedDataManager;
import su.rss.util.Logger;
import su.rss.util.ThemeSettingHelper;
import su.rss.util.Tools;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FeedAddListView implements OnBtnClickListener, OnItemClickListener, ExecuterListener {

	private static final int MSG_LOAD_FINISH = 1;
	private static final int MSG_FEEDDATA_CHANGED = 2;
	private static final int MSG_CATEDATA_CHANGED = 3;
	
	Context mContext;
	View mParent;
	View mLoadingBar;
	ListView mListView;
	FeedAddAdapter mAdapter;
	ArrayList<HashMap<String, Object>> mDataList;
	ArrayList<CategoryData> mCategoryList;
	ArrayList<RSSFeed> mFeedList;
	Handler mHandler;
	int mType;

	public FeedAddListView(Context context, View parent, int type) {
		mContext = context;
		mParent = parent;
		mType = type;
		mLoadingBar = mParent.findViewById(R.id.loading);
		mDataList = new ArrayList<HashMap<String, Object>>();
		createHandler();
		initDataList();
		initListView();
	}
	
	public FeedAddListView(Context context, View parent, int type, ArrayList<RSSFeed> list) {
		mContext = context;
		mParent = parent;
		mType = type;
		mFeedList = list;
		mLoadingBar = mParent.findViewById(R.id.loading);
		mLoadingBar.setVisibility(View.GONE);
		mDataList = new ArrayList<HashMap<String, Object>>();
		createHandler();
		initListView();
		buildFeedDataList(list);
	}

	public FeedAddListView(Context context, int resID, int type) {
		mContext = context;
		mParent = LayoutInflater.from(mContext).inflate(resID, null);
		mType = type;
		mLoadingBar = mParent.findViewById(R.id.loading);
		mDataList = new ArrayList<HashMap<String, Object>>();
		createHandler();
		initDataList();
		initListView();
	}
	
	private void createHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case MSG_LOAD_FINISH:
					mLoadingBar.setVisibility(View.GONE);
					break;
				case MSG_FEEDDATA_CHANGED:
					buildFeedDataList(mFeedList);
					break;
				case MSG_CATEDATA_CHANGED:
					buildCategoryDataList(mCategoryList);
					break;
				}
			}
			
		};
	}

	private void initListView() {
		mListView = (ListView) mParent.findViewById(R.id.rssadd_list_view);
		mAdapter = new FeedAddAdapter(mContext, mDataList, mType);
		mAdapter.setOnBtnClickListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	private void initDataList() {
		if (mType == FeedAddAdapter.TYPE_FEED) {
			initFeedDataList();
		} else {
			initCategoryDataList();
		}
	}

	private void initFeedDataList() {
		DownloadFeedsExecuter executer = new DownloadFeedsExecuter(DownloadFeedsExecuter.RSS_URL_RECOMMEND);
		executer.setListener(this);
		executer.start();
	}

	private void initCategoryDataList() {
		DownloadCateGoryExecuter executer = new DownloadCateGoryExecuter("url");
		executer.setListener(this);
		executer.start();
	}
	
	private void buildFeedDataList(ArrayList<RSSFeed> list) {
		int count = list.size();
		RSSFeedDataManager manager = RSSFeedDataManager.getRSSFeedDataManager(mContext);
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(FeedAddAdapter.MAP_ID_KEY, list.get(i).getUID());
			map.put(FeedAddAdapter.MAP_IMAGE_KEY, list.get(i).getImagePath());
			map.put(FeedAddAdapter.MAP_TITLE_KEY, list.get(i).getTitle());
			map.put(FeedAddAdapter.MAP_SAVE_STATE, false);
			if(manager.isRSSFeedSaved(list.get(i).getUID())) {
				map.put(FeedAddAdapter.MAP_SAVE_STATE, true);
			}
			mDataList.add(map);
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	private void buildCategoryDataList(ArrayList<CategoryData> list) {
		int count = list.size();
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(FeedAddAdapter.MAP_IMAGE_KEY, "url");
			map.put(FeedAddAdapter.MAP_TITLE_KEY, list.get(i).getTitle());
			mDataList.add(map);
		}
		mAdapter.notifyDataSetChanged();
	}

	public void onThemeChange(ThemeSettingHelper themeHelper) {
		themeHelper.setListViewDividerColor(mContext, mListView,
				R.color.list_divider_color);
		themeHelper.setViewBackgroundColor(mContext, mListView, R.color.activity_bg_color);
		themeHelper.setViewBackgroundColor(mContext, mLoadingBar, R.color.activity_bg_color);
	}

	@Override
	public void onBtnClick(Button v, int position) {
		// TODO Auto-generated method stub
		String title = (String) mDataList.get(position).get(FeedAddAdapter.MAP_TITLE_KEY);
		String id = Tools.getHashID(title);
		Drawable drawable;
		Resources res = mContext.getResources();
		Intent intent = new Intent();
		RSSFeedDataManager manager = RSSFeedDataManager.getRSSFeedDataManager(mContext);
		if(manager.isRSSFeedSaved(id)) {
			v.setText(mContext.getString(R.string.rssadd_subscribe));
			drawable = res.getDrawable(R.drawable.add_icon);
			intent.setAction(MainActivity.ACTION_FEED_REMOVE);
			mDataList.get(position).put(FeedAddAdapter.MAP_SAVE_STATE, false);
			
		} else {
			v.setText(mContext.getString(R.string.rssadd_cancel));
			drawable = res.getDrawable(R.drawable.list_item_del_icon);
			intent.setAction(MainActivity.ACTION_FEED_ADD);
			mDataList.get(position).put(FeedAddAdapter.MAP_SAVE_STATE, true);
		}
		
		intent.putExtra(MainActivity.EXTRAS_FEED, mFeedList.get(position));
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		v.setCompoundDrawables(null, null, drawable, null);
		mAdapter.notifyDataSetChanged();
		mContext.sendBroadcast(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (mType == FeedAddAdapter.TYPE_CATEGORY) {
			Intent intent = new Intent(mContext, FeedAddActivity.class);
			ArrayList<RSSFeed> list = mCategoryList.get(arg2).getFeedList();
			Logger.v("FeedAddListView RSSFeed list = " + list);
			intent.putExtra(FeedAddActivity.INPUT_DATA_KEY, list);
			mContext.startActivity(intent);
		}
	}

	@Override
	public void onResult(Object result) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(MSG_LOAD_FINISH);
		if(result == null) {
			return;
		}
		
		ArrayList list = (ArrayList) result;
		if(list.size() == 0)
			return;
		
		Object obj = list.get(0);
		if(obj instanceof RSSFeed) {
			mFeedList = list;
			mHandler.sendEmptyMessage(MSG_FEEDDATA_CHANGED);
//			buildFeedDataList(list);
		} else {
			mCategoryList = list;
			mHandler.sendEmptyMessage(MSG_CATEDATA_CHANGED);
//			buildCategoryDataList(list);
		}
		
		
	}
}
