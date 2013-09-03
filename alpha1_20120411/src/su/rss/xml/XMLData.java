package su.rss.xml;

import java.util.ArrayList;
import java.util.List;

public class XMLData {
	
	public String tagName = null;

	public String characters = null;
	
	public String preTag = null;
	
	public String nextTag = null;
	
	public List<XMLData> childs = null;
	
	public ArrayList<XMLAttributes> attributes = null;
	
	public static XMLAttributes newPullData() {
		return new XMLAttributes();
	}
	
	public static class XMLAttributes {
		
		public String name;
		
		public String value;
	}
}