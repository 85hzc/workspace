package su.rss.executer;

import java.util.ArrayList;

import su.rss.data.CategoryData;
import su.rss.data.RSSFeed;
import su.rss.util.RSSUtil;
import su.rss.util.Tools;

public class DownloadCateGoryExecuter extends Executer {

	String mLink;
	
	String categoryTitle[] = {"新闻", "体育", "娱乐"};

	String title[] = { "百度国内焦点新闻", 
						"网易国内新闻", 
						"国内要闻-新浪新闻", 
						"百度体育最新新闻", 
						"综合体育-网易体育频道",
						"焦点新闻-新浪体育",
						"百度娱乐最新新闻",
						"网易娱乐频道",
						"娱乐要闻汇总-新浪娱乐"};
	String link[] = { "http://news.baidu.com/n?cmd=1&class=civilnews&tn=rss&sub=0",
			"http://news.163.com/special/00011K6L/rss_gn.xml",
			"http://rss.sina.com.cn/news/china/focus15.xml",
			"http://news.baidu.com/n?cmd=4&class=sportnews&tn=rss",
			"http://sports.163.com/special/00051K7F/rss_sportszh.xml",
			"http://rss.sina.com.cn/news/allnews/sports.xml",
			"http://news.baidu.com/n?cmd=4&class=enternews&tn=rss",
			"http://ent.163.com/special/00031K7Q/rss_entspecial.xml",
			"http://rss.sina.com.cn/ent/hot_roll.xml"};
	String imgLink[] = { "http://img.baidu.com/img/logo-news.gif",
			"",
			"http://www.sinaimg.cn/home/deco/2009/0330/logo_home_news.gif",
			"http://img.baidu.com/img/logo-news.gif",
			"",
			"http://www.sinaimg.cn/home/deco/2009/0330/logo_home.gif",
			"http://img.baidu.com/img/logo-news.gif",
			"",
			"http://i1.sinaimg.cn/home/deco/2009/0330/logo_home.gif"};
	

	public DownloadCateGoryExecuter(String url) {
		mLink = url;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		ArrayList<CategoryData> list = RSSUtil.loadCateGories(mLink);
		ArrayList<CategoryData> list = new ArrayList<CategoryData>();
		for(int i = 0; i < 3; i++) {
			CategoryData data = new CategoryData();
			data.setTtile(categoryTitle[i]);
			ArrayList<RSSFeed> feedlist = new ArrayList<RSSFeed>();
			for(int j = 0; j < 3; j++) {
				RSSFeed feed = new RSSFeed();
				feed.setTitle(title[i * 3 + j]);
				feed.setImagePath(imgLink[i * 3 + j]);
				feed.setLink(link[i * 3 + j]);
				feed.setUID(Tools.getHashID(title[i * 3 + j]));
				feed.setUserID("userid");
				feed.setPosition(j);
				feedlist.add(feed);
			}
			data.setFeedList(feedlist);
			list.add(data);
		}
		if(mListener != null)
			mListener.onResult(list);
	}
}
