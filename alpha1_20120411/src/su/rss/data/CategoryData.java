package su.rss.data;

import java.util.ArrayList;
import java.util.List;

public class CategoryData {

	/**
	 * ��������
	 */
	String mTitle;
	
	/**
	 * Դ�б�
	 */
	ArrayList<RSSFeed> feedList;
	
	public void setTtile(String title) {
		this.mTitle = title;
	}
	
	public String getTitle() {
		return this.mTitle;
	}
	
	public void setFeedList(List<RSSFeed> list) {
		this.feedList = (ArrayList<RSSFeed>) list;
	}
	
	public ArrayList<RSSFeed> getFeedList() {
		return this.feedList;
	}
}
