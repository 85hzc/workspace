package su.rss.util;

import java.util.ArrayList;
import java.util.HashMap;

import su.rss.adapter.RSSItemAdapter;
import su.rss.data.RSSItem;

public class ItemConvert extends Thread {
	
	private static final int MAX_TEXT_LENGTH = 50;
	
	ArrayList<HashMap<String, Object>> mItemDataList;
	ArrayList<RSSItem> mList;
	ConvertFinishListener mListener;
	
	public interface ConvertFinishListener {
		public void onFinish(ArrayList<HashMap<String, Object>> list);
	}
	
	public ItemConvert(ArrayList<RSSItem> list) {
		mList = list;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(mList == null || mList.size() == 0) {
			if(mListener != null)
				mListener.onFinish(null);
			return;
		}
		
		mItemDataList = new ArrayList<HashMap<String, Object>>();
		for(RSSItem item : mList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(RSSItemAdapter.MAP_KEY_TITLE, item.getTitle());
			map.put(RSSItemAdapter.MAP_KEY_DATE, item.getDate());
			map.put(RSSItemAdapter.MAP_KEY_SOURCE, item.getSource());
			map.put(RSSItemAdapter.MAP_KEY_DESCRIPTION, convertDescription(item.getDescription()));
			map.put(RSSItemAdapter.MAP_KEY_IMAGE, item.getImgPath());
			map.put(RSSItemAdapter.MAP_KEY_ITEM_ID, item.getUID());
			mItemDataList.add(map);
		}
		
		if(mListener != null)
			mListener.onFinish(mItemDataList);
	}
	
	public void setConvertFinishListener(ConvertFinishListener l) {
		mListener = l;
	}
	
	private String convertDescription(String description) {
		String des = "";
		if (description != null && description.length() > 0) {
			des = description.toLowerCase();
			des = HtmlParser.getStringByTag(des, "p");
			if (des != null && des.length() > 0) {
				des = des.substring(0,
						des.length() > MAX_TEXT_LENGTH ? MAX_TEXT_LENGTH : des
								.length());
			} else {
				des = description.substring(0,
						description.length() > MAX_TEXT_LENGTH ? MAX_TEXT_LENGTH
								: description.length());
			}
		}

		return des;
	}
}
