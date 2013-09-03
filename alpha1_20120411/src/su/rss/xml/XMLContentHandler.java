package su.rss.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLContentHandler extends DefaultHandler {

	private static final int STATE_IDLE = 0;
	private static final int STATE_START = 1;
	private static final int STATE_END = 2;
	private ArrayList<XMLData> mDataList;
	private String mParseTag;
	private String mCurrentTag;
	private String mPreviousTag;
	private int mState;

	public XMLContentHandler() {
		this(null);
	}

	public XMLContentHandler(String tag) {
		mDataList = new ArrayList<XMLData>();
		mParseTag = tag;
		mCurrentTag = null;
		mPreviousTag = null;
		mState = STATE_IDLE;
	}

	public ArrayList<XMLData> getXmlData() {
		return mDataList;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
		if (mParseTag != null) {
			if (this.mCurrentTag.equals(mParseTag) || (mState == STATE_START)) {
				String data = new String(ch, start, length);
				data = data.trim();
				if(data.length() > 0) {
					mDataList.get(mDataList.size() - 1).characters = data;
				}
			}
		} else {
			String data = new String(ch, start, length);
			data = data.trim();
			if(data.length() > 0) {
				mDataList.get(mDataList.size() - 1).characters = data;
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if(localName.equals(mParseTag)) {
			mState = STATE_END;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		mCurrentTag = localName;
		if (mParseTag != null) {
			if (localName.equals(mParseTag) || (mState == STATE_START)) {
				mState = STATE_START;
				XMLData data = new XMLData();
				data.tagName = localName;
				int count = attributes.getLength();
				if(count > 0) {
					data.attributes = new ArrayList<XMLData.XMLAttributes>();
					for(int i = 0; i < count; i++) {
						XMLData.XMLAttributes attr = XMLData.newPullData();
						attr.name = attributes.getQName(i);
						attr.value = attributes.getValue(attr.name);
						data.attributes.add(attr);
					}
				}
				mDataList.add(data);
			}
		} else {
			XMLData data = new XMLData();
			data.tagName = localName;
			int count = attributes.getLength();
			if(count > 0) {
				data.attributes = new ArrayList<XMLData.XMLAttributes>();
				for(int i = 0; i < count; i++) {
					XMLData.XMLAttributes attr = XMLData.newPullData();
					attr.name = attributes.getQName(i);
					attr.value = attributes.getValue(attr.name);
					data.attributes.add(attr);
				}
			}
			if(this.mPreviousTag != null) {
				data.preTag = this.mPreviousTag;
			}
			mPreviousTag = localName;
			if(!mDataList.isEmpty()) {
				mDataList.get(mDataList.size() - 1).nextTag = localName;
			}
			mDataList.add(data);
		}
	}

}