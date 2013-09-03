package su.rss.executer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;

import android.content.Context;

import su.rss.data.PicData;
import su.rss.manager.PicManager;
import su.rss.net.HttpBase;
import su.rss.net.MyHttpResponse;
import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.Tools;

public class DownloadPicExecuter extends Executer {

	Context mContext;
	String mLink;
	String mRelateID;
	
	public DownloadPicExecuter(Context context, String url, String relateid) {
		mContext = context;
		mLink = url;
		mRelateID = relateid;
		Logger.i("DownloadPicExecuter mLink = " + mLink);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		MyHttpResponse response = new MyHttpResponse();
		int code = HttpBase.get(mLink, null, response);
		if(code != HttpStatus.SC_OK) {
			if(mListener != null)
				mListener.onResult(null);
		}
		
		HttpEntity entity = response.getBody();
		if(entity != null) {
			String id = Tools.getHashID(mLink);
			try {
				InputStream is = entity.getContent();
				FileUtil.write(FileUtil.PIC_DIR + id, is, false);
				is.close();
				savePicData(id);
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				if(mListener != null)
					mListener.onResult(mRelateID);
				
				return;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if(mListener != null)
			mListener.onResult(null);
	}
	
	private void savePicData(String id) {
		PicManager mgr = PicManager.getPicManager(mContext);
		PicData data = new PicData();
		data.setID(id);
		data.setRelateID(mRelateID);
		data.setImageLink(mLink);
		data.setImagePath(FileUtil.PIC_DIR + id);
		
		if(mgr.isPicSaved(id, mRelateID)) {
			mgr.deleteAllPicData(mRelateID);
		}
		mgr.addPicData(data);
	}

}
