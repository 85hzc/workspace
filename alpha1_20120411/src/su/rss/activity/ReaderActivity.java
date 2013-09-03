package su.rss.activity;

import su.rss.R;
import su.rss.data.RSSFeed;
import su.rss.data.RSSItem;
import su.rss.manager.CollectManager;
import su.rss.manager.RSSFeedDataManager;
import su.rss.manager.RSSItemDataManager;
import su.rss.util.FileUtil;
import su.rss.util.HtmlParser;
import su.rss.util.Logger;
import su.rss.util.Tools;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

public class ReaderActivity extends BaseActivity {

	public static final String ITEM_KEY = "item";
	public static final String FAV_KEY = "favorite";

	private static final String SCRIPTINTERFACE_NAME = "news";
	private static final String URL_PREFIX = "file://";
	private static final String NEWS_URL = "file:///android_asset/shownews.html";
	private static final String NIGHT_NEWS_URL = "file:///android_asset/night_shownews.html";

	private static final String SHOW_SUPERBIG_URL = "javascript:showSuperBigSize()";
	private static final String SHOW_BIG_URL = "javascript:showBigSize()";
	private static final String SHOW_MID_URL = "javascript:showMidSize()";
	private static final String SHOW_SMALL_URL = "javascript:showSmallSize()";

	private static final String LOAD_IMG_URL = "<center><img src=\"/sdcard/su/rss/pic/img.png\" height=\"%s\"/></center>";

	// private static final String SHOW_IMG_URL =
	// "javascript:(function(){$('#imgcontent0').css('background-image',\"url(/sdcard/su/rss/pic/img.png)\");})()";

	private static final int SHOW_SUPERBIG_SIZE = 0;
	private static final int SHOW_BIG_SIZE = 1;
	private static final int SHOW_MID_SIZE = 2;
	private static final int SHOW_SMALL_SIZE = 3;

	WebView mWebView;
	RSSItem mItem;
	View mLoadingBar;
	Context mContext;
	String newsHtml;
	boolean isFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			mItem = (RSSItem) bundle.getSerializable(ITEM_KEY);
			isFavorite = bundle.getBoolean(FAV_KEY, false);
		}

		if (mThemeSettingHelper.isDefaultTheme()) {
			newsHtml = NEWS_URL;
		} else {
			newsHtml = NIGHT_NEWS_URL;
		}

		mContext = this;
		mWebView = (WebView) findViewById(R.id.web_view);
		setWebView(mWebView);
		mWebView.loadUrl(newsHtml);
		mLoadingBar = findViewById(R.id.loading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(isFavorite) {
			menu.add(0, 0, 0, getString(R.string.menu_remove_name)).setIcon(
					R.drawable.menu_remove_icon);
		} else {
			menu.add(0, 0, 0, getString(R.string.menu_fav_name)).setIcon(
					R.drawable.menu_fav_icon);
		}
		menu.add(0, 1, 1, getString(R.string.menu_show_all)).setIcon(
				R.drawable.menu_showall_icon);
		menu.add(0, 2, 2, getString(R.string.menu_share_name)).setIcon(
				R.drawable.menu_share_icon);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case 0:
			if(isFavorite) {
				removeFavorite();
			} else {
				addFavorite();
			}
			break;
		case 1:
			mLoadingBar.setVisibility(View.VISIBLE);
			String url = FileUtil.DETAILPAGE_DIR + mItem.getUID();
			if(!FileUtil.exists(url)) {
				url = mItem.getLink();
			} else {
				url = URL_PREFIX + url;
			}
			mWebView.loadUrl(url);
			break;
		case 2:
			share();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onApplyTheme() {
		// TODO Auto-generated method stub
		mThemeSettingHelper.setViewBackgroundColor(mContext, mWebView,
				R.color.activity_bg_color);
		mThemeSettingHelper.setViewBackgroundColor(mContext, mLoadingBar,
				R.color.activity_bg_color);
	}

	private void setWebView(WebView webView) {
//		webView.setInitialScale(30);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
//		webSettings.setUseWideViewPort(true);
//		webSettings.setLoadWithOverviewMode(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.setWebViewClient(new MyWebViewClient());
		webView.addJavascriptInterface(new JavaScriptInterface(),
				SCRIPTINTERFACE_NAME);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}
	
	private void addFavorite() {
		RSSItemDataManager itemMgr = RSSItemDataManager.getRSSItemDataManager(this);
		itemMgr.addRSSItem(mItem);
		CollectManager.getCollectManager(this).addCollect("userid", mItem.getUID());
	}
	
	private void removeFavorite() {
		Intent intent = new Intent(CollectActivity.ACTION_REMOVE_FAVORITE);
		intent.putExtra(CollectActivity.INPUT_ITEMID_KEY, mItem.getUID());
		sendBroadcast(intent);
		finish();
	}
	
	private void share() {
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "·ÖÏí"); 
		intent.putExtra(Intent.EXTRA_TEXT, mItem.getTitle() + mItem.getLink());
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	private void setTextSize(WebView webView, int size) {
		switch (size) {
		case SHOW_SUPERBIG_SIZE:
			webView.loadUrl(SHOW_SUPERBIG_URL);
			break;
		case SHOW_BIG_SIZE:
			webView.loadUrl(SHOW_BIG_URL);
			break;
		case SHOW_MID_SIZE:
			webView.loadUrl(SHOW_MID_URL);
			break;
		case SHOW_SMALL_SIZE:
			webView.loadUrl(SHOW_SMALL_URL);
			break;
		}
	}
	
	private String replaceTag(String str) {
		str = str.replace("<h1>", "<p>");
		str = str.replace("</h1>", "</p>");
		return str;
	}

	class JavaScriptInterface {

		public String getTitle() {
			return mItem.getTitle();
		}

		public String getSource() {
			String source = mItem.getSource();
			if (source == null)
				source = "";
			return source;
		}

		public String getTime() {
			String time = mItem.getDate();
			if(time == null)
				time = "";
			return time;
		}

		public String getBody() {
			String content = mItem.getDescription();
			content = replaceTag(content);
			String url = HtmlParser.getStringWithTag(content, "p");
			if(Tools.isImageDownloadEnable(mContext) || url == null) {
				url = content;
			}
			if (url == null)
				url = "";
			return url;
		}

		public String getMoreBody() {
			return "more";
		}

		public int hasNext() {
			RSSFeed feed = RSSFeedDataManager.getRSSFeedDataManager(mContext)
					.getRSSFeed(mItem.getFeedID());
			if (feed.getProperty() == RSSFeed.PROPERTY_CUSTOM) {
				return 1;
			}
			return 0;
		}

		public void getMore() {
			new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String url = FileUtil.DETAILPAGE_DIR + mItem.getUID();
					if(!FileUtil.exists(url)) {
						url = mItem.getLink();
						Logger.v(url);
					} else {
						url = URL_PREFIX + url;
					}
					mWebView.loadUrl(url);
				}
				
			}.start();
		}

		public void triggerFullScreen() {

		}

		public void showBigPic(int i) {

		}
	}

	class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			Logger.v("onProgressChanged, newProgress = " + newProgress);
			if (newProgress > 20 && mLoadingBar.isShown()) {
				SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(mContext);
				String size = prf.getString(SettingActivity.FONT_PRE_KEY, "2");
				setTextSize(view, Integer.parseInt(size));
				mLoadingBar.setVisibility(View.GONE);
			}
		}

	}
	
	class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}
		
	}

}
