package su.rss.view;

import su.rss.R;
import su.rss.activity.MainActivity;
import su.rss.data.RSSFeed;
import su.rss.util.Logger;
import su.rss.util.RSSUtil;
import su.rss.util.ThemeSettingHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class FeedAddCustomView implements OnClickListener {

	private static final int MSG_FEED_DOWNLOAD = 1;

	Context mContext;
	View mParent;
	ImageButton mAddBtn;
	EditText mEditText;
	String mLink;
	Handler mHandler;

	public FeedAddCustomView(Context context, View parent) {
		mContext = context;
		mParent = parent;
		setupViews();
		createHandler();
	}

	private void createHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case MSG_FEED_DOWNLOAD:
					RSSFeed feed = (RSSFeed) msg.obj;
					if (feed == null) {
						Toast.makeText(mContext,
								mContext.getString(R.string.download_failed),
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						Toast.makeText(mContext,
								mContext.getString(R.string.custom_add_ok),
								Toast.LENGTH_SHORT).show();
					}
					dealResult(feed);
					break;
				}
			}

		};
	}

	private void setupViews() {
		mAddBtn = (ImageButton) mParent.findViewById(R.id.custom_add_btn);
		mAddBtn.setOnClickListener(this);
		mEditText = (EditText) mParent.findViewById(R.id.custom_edit);
	}

	private void dealResult(RSSFeed feed) {
		feed.setProperty(RSSFeed.PROPERTY_CUSTOM);
		Intent intent = new Intent(MainActivity.ACTION_FEED_ADD);
		intent.putExtra(MainActivity.EXTRAS_FEED, feed);
		mContext.sendBroadcast(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mLink = mEditText.getText().toString();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Logger.v("FeedAddCustomView mLink = " + mLink);
				RSSFeed feed = RSSUtil.loadFeed(mLink, "userid");
				Message msg = new Message();
				msg.what = MSG_FEED_DOWNLOAD;
				msg.obj = feed;
				mHandler.sendMessage(msg);
			}

		}.start();
	}

	public void onThemeChange(ThemeSettingHelper themeHelper) {
		themeHelper.setViewBackground(mContext, mEditText,
				R.drawable.custom_edit_bg);
		themeHelper.setViewBackground(mContext, mAddBtn,
				R.drawable.custom_btn_bg);
		themeHelper.setViewBackgroundColor(mContext, mParent,
				R.color.activity_bg_color);
		themeHelper
				.setImageViewSrc(mContext, mAddBtn, R.drawable.custom_btn_ok);
		themeHelper.setTextViewCompoundDrawables(mContext, mEditText,
				R.drawable.custom_add_icon,
				ThemeSettingHelper.COMPOUND_DRAWABLE_LEFT);
	}
}
