package su.rss.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import su.rss.R;
import su.rss.executer.DownloadPicExecuter;
import su.rss.executer.Executer.ExecuterListener;
import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.ThemeSettingHelper;
import su.rss.util.Tools;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedAddAdapter extends BaseAdapter implements OnClickListener, ExecuterListener {

	public static final int TYPE_FEED = 1;
	public static final int TYPE_CATEGORY = 2;

	public static final String MAP_TITLE_KEY = "title";
	public static final String MAP_IMAGE_KEY = "image";
	public static final String MAP_ID_KEY = "id";
	public static final String MAP_SAVE_STATE = "save_state";
	
	private static final int MSG_UPDATE_IMG = 1;

	Context mContext;
	ArrayList<HashMap<String, Object>> mItemList;
	int mType;
	OnBtnClickListener mListener;
	ThemeSettingHelper mThemeSettingHelper;
	Bitmap mBitmap;
	Handler mHandler;
	HashMap<String, Boolean> mImgDownloadMap;

	public interface OnBtnClickListener {
		public void onBtnClick(Button v, int position);
	}

	public FeedAddAdapter(Context context,
			ArrayList<HashMap<String, Object>> itemList, int type) {
		mContext = context;
		mItemList = itemList;
		mType = type;
		mThemeSettingHelper = ThemeSettingHelper.getThemeSettingHelper(context);
		mImgDownloadMap = new HashMap<String, Boolean>();
		createHandler();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (mItemList != null) {
			count = mItemList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		Object item = null;
		if (mItemList != null) {
			item = mItemList.get(arg0);
		}
		return item;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.rssadd_item, null);
			holder = new ViewHolder();
			holder.mItemImage = (ImageView) convertView
					.findViewById(R.id.rssadd_item_img);
			holder.mItemText = (TextView) convertView
					.findViewById(R.id.rssadd_item_text);
			holder.mSubscribeBtn = (Button) convertView
					.findViewById(R.id.rssadd_item_btn);
			holder.mArrowImage = (ImageView) convertView
					.findViewById(R.id.rssadd_item_category_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
//		Bitmap bitmap = (Bitmap) mItemList.get(position).get(MAP_IMAGE_KEY);
		String imgUrl = (String)mItemList.get(position).get(MAP_IMAGE_KEY);
		String title = (String) mItemList.get(position).get(MAP_TITLE_KEY);
		mBitmap = getImage(imgUrl, title);
		
		if(mBitmap != null) {
			holder.mItemImage.setImageBitmap(mBitmap);
		} else {
			holder.mItemImage.setImageResource(R.drawable.cell_bg);
		}
		holder.mItemText.setText(title);
		if (mType == TYPE_FEED) {
			holder.mSubscribeBtn.setVisibility(View.VISIBLE);
			holder.mArrowImage.setVisibility(View.GONE);
			setBtnOnClickListener(holder.mSubscribeBtn, position);
			setButtonDrawable(holder.mSubscribeBtn, position);
		} else {
			holder.mArrowImage.setVisibility(View.VISIBLE);
			holder.mSubscribeBtn.setVisibility(View.GONE);
		}
		
		setViewBacground(convertView);

		return convertView;
	}

	public void setOnBtnClickListener(OnBtnClickListener l) {
		mListener = l;
	}

	private void setBtnOnClickListener(Button v, int position) {
		v.setTag(position);
		v.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mListener != null) {
			mListener.onBtnClick((Button)v, (Integer) v.getTag());
		}

		if (mType == TYPE_FEED) {
			setButtonDrawable((Button) v, (Integer) v.getTag());
		}
	}
	
	private void createHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_UPDATE_IMG:
					notifyDataSetChanged();
					Logger.v("notify data changed");
					break;
				}
			}
		};
	}

	private void setViewBacground(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null)
			return;

		mThemeSettingHelper.setViewBackground(mContext, view,
				R.drawable.list_item_bg);
		mThemeSettingHelper.setTextViewColor(mContext, holder.mItemText,
				R.color.rssadd_item_text_color);
		mThemeSettingHelper.setTextViewColor(mContext, holder.mSubscribeBtn,
				R.color.rssadd_item_text_color);
		mThemeSettingHelper.setViewBackground(mContext, holder.mSubscribeBtn,
				R.drawable.rssadd_item_btn_bg);
	}

	private void setButtonDrawable(Button btn, int position) {
		Drawable drawable;
		Resources res = mContext.getResources();
		boolean isSaved = (Boolean)mItemList.get(position).get(MAP_SAVE_STATE);
		if (isSaved) {
			btn.setText(mContext.getString(R.string.rssadd_cancel));
			drawable = res.getDrawable(R.drawable.list_item_del_icon);
		} else {
			btn.setText(mContext.getString(R.string.rssadd_subscribe));
			drawable = res.getDrawable(R.drawable.add_icon);
		}

		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		btn.setCompoundDrawables(null, null, drawable, null);
	}
	
	private Bitmap getImage(String url, String title) {
		if(url == null || url.length() == 0)
			return null;
		
		Bitmap bitmap = null;
		String feedid = Tools.getHashID(title);
		String id = Tools.getHashID(url);
		if(FileUtil.exists(FileUtil.PIC_DIR + id)) {
			bitmap = BitmapFactory.decodeFile(FileUtil.PIC_DIR + id);
		} else if(!isImgDownloading(feedid)){
			mImgDownloadMap.put(feedid, true);
			DownloadPicExecuter executer = new DownloadPicExecuter(mContext, url, feedid);
			executer.setListener(this);
			executer.start();
		}
		
		return bitmap;
	}
	
	private boolean isImgDownloading(String relateid) {
		Boolean is = mImgDownloadMap.get(relateid);
		if(is == null)
			is = false;
		return is;
	}

	final class ViewHolder {
		public ImageView mItemImage;
		public TextView mItemText;
		public Button mSubscribeBtn;
		public ImageView mArrowImage;
	}

	@Override
	public void onResult(Object result) {
		// TODO Auto-generated method stub
		if(result == null)
			return;
		mHandler.sendEmptyMessage(MSG_UPDATE_IMG);
	}

}
