package su.rss.executer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;

import su.rss.data.RSSFeed;
import su.rss.net.HttpBase;
import su.rss.net.MyHttpResponse;
import su.rss.util.RSSUtil;
import su.rss.util.Tools;

public class DownloadFeedsExecuter extends Executer {

	public static final String RSS_URL_PUSH = "push";
	public static final String RSS_URL_RECOMMEND = "recommend";

	private static final String HTTP_PREIX = "http://";
	String mLink;
	String pushTitle[] = { "首页-掌上天翼", "本塘", "新闻", "娱乐" };
	String pushLink[] = { "http://60.191.70.27:8888/wap/rss/rss_hp.xml",
			"http://60.191.70.27:8888/wap/rss?channelId=101",
			"http://60.191.70.27:8888/wap/rss?channelId=118",
			"http://60.191.70.27:8888/wap/rss?channelId=343" };
	String pushImgLink[] = {
			"http://60.191.70.27:8888/wap/web/images/logo.png",
			"http://60.191.70.27:8888/wap/web/images/logo.png",
			"http://60.191.70.27:8888/wap/web/images/logo.png",
			"http://60.191.70.27:8888/wap/web/images/logo.png" };

	String recommendTitle[] = { "果壳网", "一五一十部落", "互联网那些事", "有意思吧", "糗事百科",
			"极客公园", "月光博客", "奶味网-国内新闻", "爱步客 - 舒雨的博客 - IBooks", "我们爱讲冷笑话" };
	String recommendLink[] = { "http://www.guokr.com/rss/",
			"http://feed.feedsky.com/my1510newest",
			"http://www.alibuybuy.com/feed", "http://www.u148.net/rss/",
			"http://feed.feedsky.com/qiushi", "http://feeds.geekpark.net/",
			"http://feed.williamlong.info/",
			"http://www.naivix.com/china/rss.xml",
			"http://feed.feedsky.com/bobogotk", "http://lengxiaohua.net/feed/" };
	String recommendImgLink[] = {
			"",
			"http://www.my1510.cn/images/joinbanner.gif",
			"",
			"http://www.u148.net/images/logo.gif",
			"",
			"",
			"http://feed.williamlong.info/subscribers_count.png?wbgc=D2D2D2&nbgc=eaeaea&bc=AAAAAA&wc=424242&nc=424242&bgc=2f699a",
			"http://www.naivix.com/favicon.ico", "", "" };

	public DownloadFeedsExecuter(String url) {
		// TODO Auto-generated constructor stub
		mLink = url;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<RSSFeed> list = null;
		if (mLink.startsWith(HTTP_PREIX)) {
			list = RSSUtil.loadFeeds(mLink);
		}

		if (RSS_URL_PUSH.equals(mLink)) {
			list = getPushFeeds();
		} else if (RSS_URL_RECOMMEND.equals(mLink)) {
			list = getRecommendFeeds();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mListener != null)
			mListener.onResult(list);
	}

	private ArrayList<RSSFeed> getPushFeeds() {
		ArrayList<RSSFeed> list = new ArrayList<RSSFeed>();

		for (int i = 0; i < 4; i++) {
			
			RSSFeed feed = new RSSFeed();
			feed.setTitle(pushTitle[i]);
			feed.setImagePath(pushImgLink[i]);
			feed.setLink(pushLink[i]);
			feed.setUID(Tools.getHashID(pushTitle[i]));
			feed.setUserID("userid");
			feed.setPosition(i);
			feed.setProperty(RSSFeed.PROPERTY_ORG);
			list.add(feed);
		}

		return list;
	}

	private ArrayList<RSSFeed> getRecommendFeeds() {
		ArrayList<RSSFeed> list = new ArrayList<RSSFeed>();
		for (int i = 0; i < 10; i++) {
			RSSFeed feed = RSSUtil.loadFeed(recommendLink[i], "userid");
			feed.setProperty(RSSFeed.PROPERTY_PUSH);
			list.add(feed);
		}
		return list;
	}
}
