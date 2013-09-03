package su.rss.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import su.rss.R;
import su.rss.activity.SettingActivity;
import su.rss.application.MyApplication;
import su.rss.executer.DownloadPicExecuter;
import su.rss.executer.Executer.ExecuterListener;
import su.rss.manager.ReadHistoryManager;
import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.ThemeSettingHelper;
import su.rss.util.Tools;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RSSItemAdapter extends BaseAdapter implements ExecuterListener {

	public static final String MAP_KEY_TITLE = "title";
	public static final String MAP_KEY_DATE = "date";
	public static final String MAP_KEY_SOURCE = "source";
	public static final String MAP_KEY_DESCRIPTION = "description";
	public static final String MAP_KEY_IMAGE = "image";
	public static final String MAP_KEY_ITEM_ID = "item_id";

	private static final int MSG_UPDATE_IMG = 1;

	private Context mContext;
	private ArrayList<HashMap<String, Object>> mItemDataList;
	private int mDelZoneVisible;
	private onDelBtnClickListener mListener;
	private ThemeSettingHelper mThemeSettingHelper;
	private boolean mTextChangeAble;
	private Handler mHandler;
	private Bitmap mBitmap;
	private HashMap<String, Boolean> mImgDownloadMap;

	public interface onDelBtnClickListener {
		public void onClick(View v, int position);
	}

	public RSSItemAdapter(Context context, ArrayList<HashMap<String, Object>> datalist,
			boolean changeAble) {
		mContext = context;
		mDelZoneVisible = View.GONE;
		mThemeSettingHelper = ThemeSettingHelper.getThemeSettingHelper(context);
		mTextChangeAble = changeAble;
		createHandler();
		mImgDownloadMap = new HashMap<String, Boolean>();
		mItemDataList = new ArrayList<HashMap<String, Object>>();
		mItemDataList = datalist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItemDataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItemDataList.get(arg0);
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
			holder = new ViewHolder();
			convertView = setViewTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		HashMap<String, Object> map = mItemDataList.get(position);
		String itemid = (String) map.get(MAP_KEY_ITEM_ID);
		String title = (String) map.get(MAP_KEY_TITLE);
		String date = (String) map.get(MAP_KEY_DATE);
		String source = (String) map.get(MAP_KEY_SOURCE);
		String imgID = getImgeID((String) map.get(MAP_KEY_IMAGE), itemid);
		String description = (String) map.get(MAP_KEY_DESCRIPTION);

		if (imgID != null) {
			mBitmap = BitmapFactory.decodeFile(FileUtil.PIC_DIR + imgID);
			holder.mImageView.setImageBitmap(mBitmap);
			holder.mImageView.setVisibility(View.VISIBLE);
		} else {
			holder.mImageView.setVisibility(View.GONE);
		}

		holder.mTitleText.setText(title);
		holder.mDateText.setText(date);
		holder.mSourceText.setText(source);
		holder.mDescriptionText.setText(description);
		holder.mDelZone.setVisibility(mDelZoneVisible);
		setDelBtnClickable(holder.mDelBtn, position);
		setBackground(convertView, itemid);
		return convertView;
	}

	public void setDelZoneVisibility(int visibility) {
		mDelZoneVisible = visibility;
	}

	public void setOnDelBtnClickListener(onDelBtnClickListener l) {
		mListener = l;
	}

	private View setViewTag(ViewHolder holder) {
		View convertView = LayoutInflater.from(mContext).inflate(
				R.layout.rssitem_list_item, null);
		holder.mTitleText = (TextView) convertView
				.findViewById(R.id.list_item_title);
		holder.mDateText = (TextView) convertView
				.findViewById(R.id.list_item_date);
		holder.mSourceText = (TextView) convertView
				.findViewById(R.id.list_item_source);
		holder.mDescriptionText = (TextView) convertView
				.findViewById(R.id.list_item_content);
		holder.mImageView = (ImageView) convertView
				.findViewById(R.id.list_item_img);
		holder.mDelZone = convertView.findViewById(R.id.list_item_del);
		holder.mDelBtn = (ImageButton) holder.mDelZone
				.findViewById(R.id.list_item_del_btn);
		convertView.setTag(holder);

		return convertView;
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

	private void setBackground(View view, String itemid) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null)
			return;

		MyApplication app = (MyApplication) mContext.getApplicationContext();
		int titleColor = R.color.item_readed_color;
		int dateColor = R.color.item_readed_color;
		int contentColor = R.color.item_readed_color;
		if (!ReadHistoryManager.getHistoryManager(mContext).isItemRead(
				itemid, app.getUserID())
				|| !mTextChangeAble) {
			titleColor = R.color.list_item_title_color;
			dateColor = R.color.date_color;
			contentColor = R.color.content_color;
		}

		mThemeSettingHelper.setViewBackground(mContext, view,
				R.drawable.list_item_bg);
		mThemeSettingHelper.setTextViewColor(mContext, holder.mTitleText,
				titleColor);
		mThemeSettingHelper.setTextViewColor(mContext, holder.mDateText,
				dateColor);
		mThemeSettingHelper.setTextViewColor(mContext, holder.mSourceText,
				dateColor);
		mThemeSettingHelper.setTextViewColor(mContext, holder.mDescriptionText,
				contentColor);

	}

	private void setDelBtnClickable(ImageButton btn, int pos) {
		btn.setTag(pos);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mListener != null) {
					mListener.onClick(v, (Integer) v.getTag());
				}
			}

		});
	}
	
	private boolean isImgDownloading(String relateid) {
		Boolean is = mImgDownloadMap.get(relateid);
		if(is == null)
			is = false;
		return is;
	}

	private String getImgeID(String url, String relateid) {
		String id = null;
		if (url != null) {
			if (needDownloadImg(url)) {
				if (Tools.isImageDownloadEnable(mContext) && !isImgDownloading(relateid)) {
					if (url != null && url.length() > 0) {
						mImgDownloadMap.put(relateid, true);
						downloadImg(url, relateid);
					}
				}
			} else {
				id = Tools.getHashID(url);//getImgID(item.getUID());
			}
		}

		return id;
	}

	private void downloadImg(String url, String relateid) {
		DownloadPicExecuter executer = new DownloadPicExecuter(mContext, url,
				relateid);
		executer.setListener(this);
		executer.start();
	}

	private boolean needDownloadImg(String url) {
		boolean is = true;
		String id = Tools.getHashID(url);
		if(FileUtil.exists(FileUtil.PIC_DIR + id)) {
			is = false;
		}

		return is;
	}

	final class ViewHolder {
		public TextView mTitleText;
		public TextView mDateText;
		public TextView mSourceText;
		public TextView mDescriptionText;
		public ImageView mImageView;
		public View mDelZone;
		public ImageButton mDelBtn;
	}

	@Override
	public void onResult(Object result) {
		// TODO Auto-generated method stub
		if (result == null)
			return;

		mHandler.sendEmptyMessage(MSG_UPDATE_IMG);
	}
}
