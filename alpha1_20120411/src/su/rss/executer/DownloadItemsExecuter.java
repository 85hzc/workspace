package su.rss.executer;

import java.util.ArrayList;

import su.rss.data.RSSItem;
import su.rss.util.FileUtil;
import su.rss.util.RSSUtil;
import su.rss.util.Tools;
import android.content.Context;

public class DownloadItemsExecuter extends Executer {

	Context mContext;
	String mLink;
	String mFeedid;
	
	public DownloadItemsExecuter(Context context, String url, String feedid) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mLink = url;
		mFeedid = feedid;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String path = FileUtil.DETAILPAGE_DIR + mFeedid;
		ArrayList<RSSItem> list = RSSUtil.loadItemsFromLocal(path, mFeedid);
		if(list != null) {
			if(mListener != null) {
				mListener.onResult(list);
			}
		}
		
		if(Tools.CheckNetwork(mContext)) {
			if(list == null || Tools.isAutoUpdate(mContext)) {
				list = RSSUtil.loadItems(mLink, mFeedid);
			}
		}
		
		if(mListener != null) {
			mListener.onResult(list);
		}
	}

}
