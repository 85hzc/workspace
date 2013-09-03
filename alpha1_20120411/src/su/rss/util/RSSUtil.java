package su.rss.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;

import su.rss.data.CategoryData;
import su.rss.data.RSSFeed;
import su.rss.data.RSSItem;
import su.rss.net.HttpBase;
import su.rss.net.MyHttpResponse;
import su.rss.xml.XMLData;
import su.rss.xml.XMLData.XMLAttributes;
import su.rss.xml.XMLOperator;

public class RSSUtil {

	private static final String FEED_DOWNLOAD_URL = "";
	private static final String FEED_UPLOAD_URL = "";
	private static final String FEED_TITLE_TAG = "title";
	private static final String FEED_TAG = "feed";
	private static final String FEED_LINK_TAG = "link";
	private static final String FEED_DATE_TAG = "date";
	private static final String FEED_IMAGE_TAG = "image";
	private static final String ITEM_TAG = "item";
	private static final String ITEM_TITLE_TAG = "title";
	private static final String ITEM_LINK_TAG = "link";
	private static final String ITEM_DESCRIPTION_TAG = "description";
	private static final String ITEM_DATE_TAG = "pubdate";
	private static final String ITEM_IMAGE_TAG = "image";
	private static final String CATEGORY_TAG = "category";
	private static final String CATEGORY_ATTR_NAME = "name";
	private static final String IMG_LINK_URL = "url";

	public static ArrayList<RSSFeed> loadFeeds(String userid) {
		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.get(FEED_DOWNLOAD_URL, null, response);
		if (code != HttpStatus.SC_OK) {
			return null;
		}

		HttpEntity entity = response.getBody();
		if (entity != null) {
			try {
				InputStream is = entity.getContent();
				List<XMLData> xmlList = XMLOperator.parseXmlBySax(is, "feed");
				is.close();
				return buildRSSFeed(xmlList, userid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public static RSSFeed loadFeed(String link, String userid) {
		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.get(link, null, response);
		if (code != HttpStatus.SC_OK)
			return null;

		HttpEntity entity = response.getBody();
		if (entity != null) {
			try {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						is));
				List<XMLData> xmlList = XMLOperator
						.parseXmlByPull(reader, "channel");
				is.close();
				return buildRSSFeed(xmlList, userid, link);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public static boolean uploadFeeds(ArrayList<RSSFeed> list, String userid) {
		String xml = XMLOperator.xmlBuilde(buildXMLData(list, userid),
				new StringWriter());
		if (xml == null) {
			return false;
		}

		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.post(FEED_UPLOAD_URL, xml, null, response);
		if (code != HttpStatus.SC_OK) {
			return false;
		}

		return true;

	}

	public static ArrayList<RSSItem> loadItems(String url, String feedid) {
		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.get(url, null, response);
		if (code != HttpStatus.SC_OK) {
			return null;
		}
		HttpEntity entity = response.getBody();
		if (entity != null) {

			try {
				InputStream is = entity.getContent();
				FileUtil.write(FileUtil.DETAILPAGE_DIR + feedid, is, false);
				is.close();
				return loadItemsFromLocal(FileUtil.DETAILPAGE_DIR + feedid,
						feedid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public static ArrayList<CategoryData> loadCateGories(String url) {
		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.get(url, null, response);
		if (code != HttpStatus.SC_OK) {
			return null;
		}

		HttpEntity entity = response.getBody();
		if (entity != null) {
			try {
				InputStream is = entity.getContent();
				List<XMLData> xmlList = XMLOperator.parseXmlByDom(is,
						"category");
				is.close();

				return buildCateGoryData(xmlList);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public static ArrayList<RSSItem> loadItemsFromLocal(String path,
			String feedid) {
		try {
			File file = new File(path);
			if (!file.exists())
				return null;
			String charset = "UTF-8";
			charset = Tools.getEncoding(file.toURL());
			InputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, charset));
			List<XMLData> xmlList = XMLOperator.parseXmlByPull(reader, "item");
			is.close();
			return buildRSSItem(xmlList, feedid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void deleteLocalFeedFile(String path) {
		FileUtil.deleteFile(path);
	}

	private static ArrayList<RSSFeed> buildRSSFeed(List<XMLData> list,
			String userid) {
		if (list == null)
			return null;

		ArrayList<RSSFeed> feeds = new ArrayList<RSSFeed>();

		for (XMLData data : list) {
			if (FEED_TAG.equals(data.tagName)) {
				ArrayList<XMLData> childs = (ArrayList<XMLData>) data.childs;
				if (childs != null) {
					RSSFeed feed = new RSSFeed();
					for (XMLData child : childs) {
						if (FEED_TITLE_TAG.equals(child.tagName)) {
							feed.setTitle(child.characters);
						} else if (FEED_DATE_TAG.equals(child.tagName)) {
							feed.setDate(child.characters);
						} else if (FEED_IMAGE_TAG.equals(child.tagName)) {
							feed.setImagePath(child.characters);
						} else if (FEED_LINK_TAG.equals(child.tagName)) {
							feed.setLink(child.characters);
						}
					}

					feed.setUserID(userid);
					feed.setUID(Tools.getHashID(feed.getTitle()));
					feeds.add(feed);
				}
			}
		}

		return feeds;
	}

	private static ArrayList<XMLData> buildXMLData(ArrayList<RSSFeed> list,
			String userid) {
		if (list == null)
			return null;

		ArrayList<XMLData> dataList = new ArrayList<XMLData>();
		for (RSSFeed feed : list) {
			XMLData data = new XMLData();
			data.tagName = FEED_TAG;
			ArrayList<XMLData> childs = new ArrayList<XMLData>();
			XMLData title = new XMLData();
			title.tagName = FEED_TITLE_TAG;
			title.characters = feed.getTitle();
			childs.add(title);
			XMLData link = new XMLData();
			link.tagName = FEED_LINK_TAG;
			link.characters = feed.getLink();
			childs.add(link);
			XMLData date = new XMLData();
			date.tagName = FEED_DATE_TAG;
			date.characters = feed.getDate();
			childs.add(date);
			XMLData image = new XMLData();
			image.tagName = FEED_IMAGE_TAG;
			image.characters = feed.getImagePath();
			childs.add(image);
			date.childs = childs;
			dataList.add(data);
		}

		return dataList;
	}

	private static RSSFeed buildRSSFeed(List<XMLData> list, String userid,
			String link) {
		if (list == null)
			return null;

		RSSFeed feed = null;
		for (XMLData data : list) {
			ArrayList<XMLData> childs = (ArrayList<XMLData>) data.childs;
			if (childs != null) {
				feed = new RSSFeed();
				for (XMLData child : childs) {
					if (FEED_TITLE_TAG.equals(child.tagName)) {
						feed.setTitle(child.characters);
					} else if (FEED_DATE_TAG.equals(child.tagName)) {
						feed.setDate(child.characters);
					} else if (FEED_IMAGE_TAG.equals(child.tagName)) {
						if (child.characters != null
								&& child.characters.length() > 0) {
							feed.setImagePath(child.characters);
						} else {
							ArrayList<XMLData> imgChilds = (ArrayList<XMLData>) child.childs;
							if (imgChilds != null) {
								for (XMLData imgChild : imgChilds) {
									if (IMG_LINK_URL
											.equalsIgnoreCase(imgChild.tagName)) {
										feed.setImagePath(imgChild.characters);
									}
								}
							}
						}

					} else if(ITEM_TAG.equalsIgnoreCase(child.tagName)) {
						if(feed.getImagePath() == null) {
							ArrayList<XMLData> itemChilds = (ArrayList<XMLData>) child.childs;
							if(itemChilds != null) {
								for(XMLData itemChild : itemChilds) {
									if(ITEM_DESCRIPTION_TAG.equalsIgnoreCase(itemChild.tagName)) {
										String url = HtmlParser.getFirstImgLink(itemChild.characters);
										if(url != null) {
											feed.setImagePath(url);
										}
									}
								}
							}
						}
					}
				}
				
				feed.setLink(link);
				feed.setUserID(userid);
				feed.setUID(Tools.getHashID(feed.getTitle()));
			}
		}

		return feed;
	}

	private static ArrayList<RSSItem> buildRSSItem(List<XMLData> list,
			String feedid) {
		if (list == null) {
			return null;
		}

		ArrayList<RSSItem> items = new ArrayList<RSSItem>();
		for (XMLData data : list) {
			if (ITEM_TAG.equals(data.tagName)) {
				ArrayList<XMLData> childs = (ArrayList<XMLData>) data.childs;
				if (childs != null) {
					RSSItem item = new RSSItem();
					for (XMLData child : childs) {
						if (ITEM_TITLE_TAG.equals(child.tagName)) {
							item.setTitle(child.characters);
						} else if (ITEM_DATE_TAG
								.equalsIgnoreCase(child.tagName)) {
							item.setDate(child.characters.substring(0,
									child.characters.lastIndexOf(":") + 3));
						} else if (ITEM_LINK_TAG
								.equalsIgnoreCase(child.tagName)) {
							item.setLink(child.characters);
						} else if (ITEM_DESCRIPTION_TAG
								.equalsIgnoreCase(child.tagName)) {
							item.setDescription(child.characters);
							String link = HtmlParser
									.getFirstImgLink(child.characters);
							if (link != null && link.length() > 0) {
								item.setHaveImage(true);
								item.setImgPath(link);
							}
						} else if (ITEM_IMAGE_TAG
								.equalsIgnoreCase(child.tagName)) {
							if (child.characters != null
									&& child.characters.length() > 0) {
								item.setHaveImage(true);
								item.setImgPath(child.characters);
							}
						}
					}

					item.setUID(Tools.getHashID(item.getTitle()));
					item.setFeedID(feedid);
					items.add(item);
				}
			}
		}

		return items;
	}

	private static ArrayList<CategoryData> buildCateGoryData(List<XMLData> list) {
		if (list == null)
			return null;

		ArrayList<CategoryData> cates = new ArrayList<CategoryData>();
		for (XMLData data : list) {
			if (CATEGORY_TAG.equals(data.tagName)) {
				CategoryData cate = new CategoryData();
				ArrayList<XMLAttributes> attrs = data.attributes;
				for (XMLAttributes attr : attrs) {
					if (CATEGORY_ATTR_NAME.equals(attr.name)) {
						cate.setTtile(attr.value);
					}
				}

				cate.setFeedList(buildRSSFeed(data.childs, null));

				cates.add(cate);
			}
		}

		return cates;
	}
}
