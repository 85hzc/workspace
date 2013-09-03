package su.rss.activity;

import su.rss.R;
import su.rss.adapter.FeedAddAdapter;
import su.rss.util.AnimaitonUtil;
import su.rss.view.FeedAddCustomView;
import su.rss.view.FeedAddListView;
import su.rss.view.MoveListener;
import su.rss.view.ScrollLayout;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RSSAddActivity extends BaseActivity implements OnClickListener, MoveListener {

	private ImageButton mBackBtn;
	private ScrollLayout mScrollView;
	private FeedAddCustomView mCustomLayout;
	private FeedAddListView mPushLayout;
	private FeedAddListView mCategoryLayout;
	private ImageView mTabCustomView;
	private ImageView mTabPushView;
	private ImageView mTabCategoryView;
	private View mTitleBar;
	private TextView mTitleText;
	private View mTabBar;
	private TextView mCustomTab;
	private TextView mPushTab;
	private TextView mCategorTab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rssadd);
		setupViews();
	}
	
	@Override
	public void onApplyTheme() {
		// TODO Auto-generated method stub
		mThemeSettingHelper.setViewBackground(this, mTitleBar, R.drawable.title_bar_bg);
		mThemeSettingHelper.setTextViewColor(this, mTitleText, R.color.title_text_color);
		mThemeSettingHelper.setViewBackground(this, mTabBar, R.drawable.tab_bar_bg);
		mThemeSettingHelper.setViewBackgroundColor(this, mTabCategoryView, R.color.tab_img_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mTabCustomView, R.color.tab_img_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mTabPushView, R.color.tab_img_color);
		mThemeSettingHelper.setTextViewColor(this, mCategorTab, R.color.tab_text_color);
		mThemeSettingHelper.setTextViewColor(this, mCustomTab, R.color.tab_text_color);
		mThemeSettingHelper.setTextViewColor(this, mPushTab, R.color.tab_text_color);
		mThemeSettingHelper.setViewBackgroundColor(this, mScrollView, R.color.activity_bg_color);
		
		mCustomLayout.onThemeChange(mThemeSettingHelper);
		mPushLayout.onThemeChange(mThemeSettingHelper);
		mCategoryLayout.onThemeChange(mThemeSettingHelper);
	}

	private void setupViews() {
		mTitleBar = findViewById(R.id.title_bar);
		mBackBtn = (ImageButton) mTitleBar.findViewById(R.id.title_favorite_btn);
		mTitleText = (TextView) mTitleBar.findViewById(R.id.title_bar_text);
		mTitleText.setText(getString(R.string.rssadd_title_name));
		ImageView title_icon = (ImageView) mTitleBar.findViewById(R.id.title_bar_icon);
		title_icon.setVisibility(View.GONE);
		mTabBar = findViewById(R.id.tab_bar);
		mTabCustomView = (ImageView) mTabBar.findViewById(R.id.tab_custom_img);
		mTabPushView = (ImageView) mTabBar.findViewById(R.id.tab_push_img);
		mTabCategoryView = (ImageView) mTabBar.findViewById(R.id.tab_category_img);
		mCustomTab = (TextView) mTabBar.findViewById(R.id.tab_custom);
		mPushTab = (TextView) mTabBar.findViewById(R.id.tab_push);
		mCategorTab = (TextView) mTabBar.findViewById(R.id.tab_category);
		
		mScrollView = (ScrollLayout) findViewById(R.id.scroll_layout);
		
		mBackBtn.setBackgroundResource(R.drawable.title_home_bg);
		mBackBtn.setImageResource(R.drawable.title_home_icon);
		mBackBtn.setOnClickListener(this);
		
		mScrollView.setMoveListener(this);
		mScrollView.setToScreen(1);
		
		mCustomTab.setOnClickListener(this);
		mPushTab.setOnClickListener(this);
		mCategorTab.setOnClickListener(this);
		
		mPushLayout = new FeedAddListView(this, findViewById(R.id.push_layout), FeedAddAdapter.TYPE_FEED);
		mCategoryLayout = new FeedAddListView(this, findViewById(R.id.category_layout), FeedAddAdapter.TYPE_CATEGORY);
		mCustomLayout = new FeedAddCustomView(this, findViewById(R.id.customer_layout));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mBackBtn) {
			finish();
		} else if(v == mCustomTab) {
			mScrollView.snapToScreen(0);
			mTabCustomView.setVisibility(View.VISIBLE);
			mTabPushView.setVisibility(View.INVISIBLE);
			mTabCategoryView.setVisibility(View.INVISIBLE);
		} else if(v == mPushTab) {
			mScrollView.snapToScreen(1);
			mTabCustomView.setVisibility(View.INVISIBLE);
			mTabPushView.setVisibility(View.VISIBLE);
			mTabCategoryView.setVisibility(View.INVISIBLE);
		} else if(v == mCategorTab) {
			mScrollView.snapToScreen(2);
			mTabCustomView.setVisibility(View.INVISIBLE);
			mTabPushView.setVisibility(View.INVISIBLE);
			mTabCategoryView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void moveTo(int index, int direction) {
		// TODO Auto-generated method stub
		switch(index) {
		case 0:
			showCustomImage(direction);
			break;
		case 1:
			showPushImage(direction);
			break;
		case 2:
			showCategoryImage(direction);
			break;
		}
	}

	@Override
	public void cancelMove(int index) {
		// TODO Auto-generated method stub
		
	}
	
	private void showCustomImage(int direction) {
		mTabCustomView.startAnimation(AnimaitonUtil.TranslateAnimaiton(1, 0, 0, 0));
		mTabCustomView.setVisibility(View.VISIBLE);
		mTabPushView.setVisibility(View.INVISIBLE);
	}
	
	private void showPushImage(int direction) {
		if(direction == ScrollLayout.SCROLL_DIRECTION_LEFT) {
			mTabCategoryView.setVisibility(View.INVISIBLE);
			mTabPushView.startAnimation(AnimaitonUtil.TranslateAnimaiton(1, 0, 0, 0));
			mTabPushView.setVisibility(View.VISIBLE);
		} else {
			mTabCustomView.setVisibility(View.INVISIBLE);
			mTabPushView.startAnimation(AnimaitonUtil.TranslateAnimaiton(-1, 0, 0, 0));
			mTabPushView.setVisibility(View.VISIBLE);
		}
	}
	
	private void showCategoryImage(int direction) {
		mTabPushView.setVisibility(View.INVISIBLE);
		mTabCategoryView.startAnimation(AnimaitonUtil.TranslateAnimaiton(-1, 0, 0, 0));
		mTabCategoryView.setVisibility(View.VISIBLE);
	}

}
