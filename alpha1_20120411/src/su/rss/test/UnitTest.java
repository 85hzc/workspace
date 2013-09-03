package su.rss.test;

import java.util.ArrayList;

import su.rss.data.RSSItem;
import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.RSSUtil;
import su.rss.util.Tools;
import android.test.AndroidTestCase;

public class UnitTest extends AndroidTestCase {
	
	public void test() {
		Logger.d("test");
	}
	
	public void testXmlPullParser() {
		String id = Tools.getHashID("奶味网-国内新闻");
		ArrayList<RSSItem> items = RSSUtil.loadItems("http://www.naivix.com/china/rss.xml", id);
	}
}
