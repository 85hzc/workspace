package su.rss.activity;

import java.util.ArrayList;

import su.rss.R;
import su.rss.adapter.FeedAddAdapter;
import su.rss.data.RSSFeed;
import su.rss.util.Logger;
import su.rss.view.FeedAddListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedAddActivity extends BaseActivity implements OnClickListener {

	public static final String INPUT_DATA_KEY = "feed";
	
	private ImageButton mBackBtn;
	private View mTitleBar;
	private TextView mTitleText;
	private FeedAddListView mListLayout;
	private ArrayList<RSSFeed> mFeedList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_add);
		Intent intent = getIntent();
		mFeedList = (ArrayList<RSSFeed>) intent.getSerializableExtra(INPUT_DATA_KEY);
		setupViews();
	}
	
	@Override
	public void onApplyTheme() {
		// TODO Auto-generated method stub
		mThemeSettingHelper.setViewBackground(this, mTitleBar, R.drawable.title_bar_bg);
		mThemeSettingHelper.setTextViewColor(this, mTitleText, R.color.title_text_color);
		mListLayout.onThemeChange(mThemeSettingHelper);
	}

	private void setupViews() {
		mTitleBar = findViewById(R.id.title_bar);
		ImageView title_icon = (ImageView) mTitleBar.findViewById(R.id.title_bar_icon);
		title_icon.setVisibility(View.GONE);
		mTitleText = (TextView) mTitleBar.findViewById(R.id.title_bar_text);
		mTitleText.setText(R.string.feed_add_title);
		mBackBtn = (ImageButton) mTitleBar.findViewById(R.id.title_favorite_btn);
		mBackBtn.setBackgroundResource(R.drawable.title_home_bg);
		mBackBtn.setImageResource(R.drawable.title_home_icon);
		mBackBtn.setOnClickListener(this);
		View view = findViewById(R.id.feed_add_list);
		mListLayout = new FeedAddListView(this, view, FeedAddAdapter.TYPE_FEED, mFeedList);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	}

}
