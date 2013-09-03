package su.rss.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import su.rss.util.Logger;

import android.util.Xml;

public class XMLOperator {
	public static ArrayList<XMLData> parseXmlBySax(InputStream inStream,
			String tag) {

		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser;
			saxParser = spf.newSAXParser();
			// saxParser.setProperty("http://xml.org/sax/features/namespaces",
			// true);
			XMLContentHandler handler = new XMLContentHandler(tag);
			saxParser.parse(inStream, handler);
			return handler.getXmlData();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static List<XMLData> parseXmlByDom(InputStream inStream, String tag) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inStream);
			Element root = dom.getDocumentElement();
			NodeList nodes;
			if (tag != null) {
				nodes = root.getElementsByTagName(tag);
			} else {
				nodes = dom.getChildNodes();
			}

			List<XMLData> list = new ArrayList<XMLData>();
			return domParseElement(nodes, list);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static List<XMLData> domParseElement(NodeList items,
			List<XMLData> list) {
		for (int i = 0; i < items.getLength(); i++) {
			Element element = null;
			Node node = (Node) items.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node;
			} else {
				continue;
			}
			XMLData data = new XMLData();
			data.tagName = element.getNodeName();
			NamedNodeMap attrs = element.getAttributes();
			int count = attrs.getLength();
			if (count > 0) {
				data.attributes = new ArrayList<XMLData.XMLAttributes>();
				for (int j = 0; j < count; j++) {
					XMLData.XMLAttributes attr = XMLData.newPullData();
					attr.name = attrs.item(j).getNodeName();
					attr.value = attrs.item(j).getNodeValue();
					data.attributes.add(attr);
				}
			}
			Node n = element.getFirstChild();
			if (n != null) {
				String str = n.getNodeValue();
				if (str != null) {
					Logger.i(data.tagName);
					Logger.v(str);
					str = str.trim();
					if (str.length() > 0) {
						data.characters = str;
					}
				}
			}

			NodeList childNodes = element.getChildNodes();
			if (childNodes.getLength() > 0) {
				data.childs = new ArrayList<XMLData>();
				domParseElement(childNodes, data.childs);
			}

			list.add(data);
		}

		return list;
	}

	public static List<XMLData> parseXmlByPull(Reader reader, String tag) {
		XmlPullParser parser = Xml.newPullParser();
		ArrayList<XMLData> dataList = null;
		ArrayList<XMLData> stack = null;
		boolean isInStack = false;

		try {
			parser.setInput(reader);
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					dataList = new ArrayList<XMLData>();
					stack = new ArrayList<XMLData>();
					break;
				case XmlPullParser.START_TAG: {
					String name = parser.getName();
					if (tag != null) {
						if (tag.equalsIgnoreCase(name)) {
							isInStack = true;
						}
					}
					stack.add(setData(parser, name));
				}
					break;
				case XmlPullParser.TEXT: {
					XMLData data = stack.get(stack.size() - 1);
					data.characters = parser.getText().trim();
				}
					break;
				case XmlPullParser.END_TAG: {
					String name = parser.getName();
					XMLData data = stack.get(stack.size() - 1);
					stack.remove(stack.size() - 1);
					if (tag != null) {
						if (tag.equalsIgnoreCase(name)) {
							isInStack = false;
							dataList.add(data);
						} else if (isInStack) {
							XMLData pre = stack.get(stack.size() - 1);
							ArrayList<XMLData> childs = (ArrayList<XMLData>) pre.childs;
							if (childs == null) {
								childs = new ArrayList<XMLData>();
							}
							childs.add(data);
							pre.childs = childs;
						}
					}

				}
					break;
				}

				eventType = parser.next();
			}

			return dataList;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

//	public static ArrayList<XMLData> parseXmlByPull(Reader reader, String tag) {
//		XmlPullParser parser = Xml.newPullParser();
//		ArrayList<XMLData> dataList = null;
//		String preTag = null;
//		boolean isStart = false;
//		XMLData parentData = null;
//		XMLData childData = null;
//		ArrayList<XMLData> childList = null;
//		try {
//			parser.setInput(reader);
//			int eventType = parser.getEventType();
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				switch (eventType) {
//				case XmlPullParser.START_DOCUMENT:
//					dataList = new ArrayList<XMLData>();
//					break;
//				case XmlPullParser.START_TAG:
//					String name = parser.getName();
//					if (tag != null) {
//						if (tag.equalsIgnoreCase(name)) {
//							parentData = setData(parser, name);
//							isStart = true;
//						} else if (isStart) {
//							if (childList == null) {
//								childList = new ArrayList<XMLData>();
//							}
//							childData = setData(parser, name);
//						}
//					} else {
//						XMLData data = setData(parser, name);
//						if (preTag != null) {
//							data.preTag = preTag;
//						}
//						preTag = name;
//						if (!dataList.isEmpty()) {
//							dataList.get(dataList.size() - 1).nextTag = name;
//						}
//						dataList.add(data);
//					}
//					break;
//				case XmlPullParser.TEXT:
//					if (tag.equals(parser.getName())) {
//						parentData.characters = parser.getText();
//					} else if (isStart) {
//						if (childData != null) {
//							childData.characters = parser.getText();
//							childData.characters = childData.characters.trim();
//						}
//					}
//
//					break;
//				case XmlPullParser.END_TAG:
//					if (tag.equals(parser.getName())) {
//						dataList.add(parentData);
//						childList = null;
//						isStart = false;
//					} else if (isStart) {
//						childList.add(childData);
//						parentData.childs = childList;
//						childData = null;
//					}
//					break;
//				}
//
//				eventType = parser.next();
//			}
//
//			return dataList;
//		} catch (XmlPullParserException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return null;
//	}

	private static XMLData setData(XmlPullParser parser, String tag) {
		XMLData data = new XMLData();
		data.tagName = tag;
		int count = parser.getAttributeCount();
		if (count > 0) {
			data.attributes = new ArrayList<XMLData.XMLAttributes>();
			for (int i = 0; i < count; i++) {
				XMLData.XMLAttributes attr = XMLData.newPullData();
				attr.name = parser.getAttributeName(i);
				attr.value = parser.getAttributeValue(i);
				data.attributes.add(attr);
			}
		}

		return data;
	}

	public static String xmlBuilde(List<XMLData> list, Writer writer) {
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			writeDocument(list, serializer);
			serializer.endDocument();
			return writer.toString();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static void writeDocument(List<XMLData> list,
			XmlSerializer serializer) throws IllegalArgumentException,
			IllegalStateException, IOException {
		for (XMLData data : list) {
			serializer.startTag("", data.tagName);
			if (data.attributes != null) {
				for (XMLData.XMLAttributes attr : data.attributes) {
					serializer.attribute("", attr.name, attr.value);
				}
			}

			if (data.characters != null) {
				serializer.text(data.characters);
			}

			if (data.childs != null) {
				writeDocument(data.childs, serializer);
			}

			serializer.endTag("", data.tagName);
		}
	}
}
