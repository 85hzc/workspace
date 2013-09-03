package su.rss.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
	
	public static String getStringByTag(String url, String tag) {
		if(url == null)
			return null;
		
		Document document = Jsoup.parse(url);
		Elements elements = document.getElementsByTag(tag);
		String str = null;
		if(elements != null && elements.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for(Element element : elements) {
				builder.append(element.text());
			}
			str = builder.toString();
		}
		
		return str;
	}
	
	public static String getStringWithTag(String url, String tag) {
		if(url == null)
			return null;
		
		Document document = Jsoup.parse(url);
		Elements elements = document.getElementsByTag(tag);
		String str = null;
		if(elements != null && elements.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for(Element element : elements) {
				builder.append("<" + tag + ">¡¡¡¡" + element.text() + "</" + tag + ">");
			}
			str = builder.toString();
		}
		
		return str;
	}
	
	public static String getFirstImgLink(String url) {
		if(url == null)
			return null;
		
		Document document = Jsoup.parse(url);
		Elements elements = document.getElementsByTag("img");
		String str = null;
		if(elements != null && elements.size() > 0) {
			Element element = elements.get(0);
			str = element.attr("src");
		}
		
		return str;
	}
}
