package su.rss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import su.rss.R;
import su.rss.util.*;

public class PreferenceList extends ListView {

	public PreferenceList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PreferenceList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PreferenceList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			View view;
			int count = getChildCount();
			for(int i = 0; i < count; i++) {
				view = getChildAt(i);
				if(view!=null)
				{Logger.v("layoutChildren "+i);
					applyThemeForChild(view);}
			}
			break;

		default:
			break;
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();
		View view;
		int count = getChildCount();
		Logger.v("PreferenceList layoutChildren count = " + count);
		for(int i = 0; i < count; i++) {
			view = getChildAt(i);
			applyThemeForChild(view);
		}
	}

	private void applyThemeForChild(View view) {
		Context context = getContext();
		ThemeSettingHelper themeHelper = ThemeSettingHelper
				.getThemeSettingHelper(context);

		TextView text_title = (TextView) view.findViewById(android.R.id.title);
		TextView text_summary = (TextView) view
				.findViewById(android.R.id.summary);
		int viewBgColorID = R.color.activity_bg_color;
		int titleTextColorID = R.color.title_font;
		if (text_summary != null)
			themeHelper.setTextViewColor(context, text_summary,
					R.color.content_color);
		else {
			viewBgColorID = R.color.catigory_bg_color;
			titleTextColorID = R.color.content_color;
			if (text_title != null)
				themeHelper.setTextViewColor(context, text_title,
						R.color.content_color);
		}

		themeHelper.setViewBackgroundColor(context, view, viewBgColorID);

		if (text_title != null)
			themeHelper.setTextViewColor(context, text_title, titleTextColorID);
	}

}
