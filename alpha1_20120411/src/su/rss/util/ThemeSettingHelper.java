package su.rss.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ThemeSettingHelper {

	public static final String THEME_DEFAULT = "default_theme";
	public static final String THEME_NIGHT = "night_theme";
	
	public static final int COMPOUND_DRAWABLE_LEFT = 0;
	public static final int COMPOUND_DRAWABLE_TOP = 1;
	public static final int COMPOUND_DRAWABLE_RIGHT = 2;
	public static final int COMPOUND_DRAWABLE_BOTTOM = 3;

	private static final String THEME_KEY = "theme";
	private static final String RES_DRAWABLE = "drawable";
	private static final String RES_COLOR = "color";
	private static final String RES_NIGHT_PREFIX = "night_";

	public abstract interface ThemeCallback {
		public abstract void applyTheme();
	}

	/**
	 * 单例的弱引用
	 */
	private static WeakReference<ThemeSettingHelper> sThemeSettingHelper;
	/**
	 * 主题更新监听列表
	 */
	private final ArrayList<WeakReference<ThemeCallback>> mCallbacks;
	/**
	 * 上下文环境
	 */
	private Context mThemeContext;
	/**
	 * 当前主题名
	 */
	private String mThemePackageName;

	private ThemeSettingHelper(Context context) {
		mCallbacks = new ArrayList<WeakReference<ThemeCallback>>();
		String str = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(THEME_KEY, THEME_DEFAULT);
		initTheme(context, str);
	}

	public static final ThemeSettingHelper getThemeSettingHelper(Context context) {
		if (sThemeSettingHelper == null || sThemeSettingHelper.get() == null) {
			sThemeSettingHelper = new WeakReference<ThemeSettingHelper>(
					new ThemeSettingHelper(context));
		}

		return (ThemeSettingHelper) sThemeSettingHelper.get();
	}

	private void initTheme(Context context, String name) {
		mThemeContext = getThemeContext(context, name);
	}

	private Context getThemeContext(Context context, String theme) {
		mThemePackageName = theme;
		Context tmpContext = null;
		if (THEME_DEFAULT.equals(mThemePackageName)
				|| THEME_NIGHT.equals(mThemePackageName)) {
			tmpContext = context.getApplicationContext();
		} else {
			try {
				tmpContext = context.createPackageContext(theme,
						Context.CONTEXT_IGNORE_SECURITY
								| Context.CONTEXT_INCLUDE_CODE);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tmpContext;
	}
	
	public boolean isDefaultTheme() {
		if(THEME_NIGHT.equals(mThemePackageName)) {
			return false;
		}
		
		return true;
	}

	public String getCurrentThemePackage() {
		return mThemePackageName;
	}

	public void changeTheme(Context context, String theme) {
		synchronized (mCallbacks) {
			String themeName = mThemePackageName;
			initTheme(context, theme);
			if (!mThemePackageName.equals(themeName)) {
				SharedPreferences.Editor editor = PreferenceManager
						.getDefaultSharedPreferences(context).edit();
				editor.putString(THEME_KEY, mThemePackageName);
				editor.commit();
				for (int i = 0; i < mCallbacks.size(); i++) {
					WeakReference<ThemeCallback> weakRef = mCallbacks.get(i);
					ThemeCallback callback = (ThemeCallback) weakRef.get();
					if (callback != null) {
						callback.applyTheme();
					}
				}
			}
		}
	}

	private Object getResourcesValue(Resources resources, int id, String resType) {
		Object o = null;
		if (RES_DRAWABLE.equals(resType)) {
			o = resources.getDrawable(id);
		} else if (RES_COLOR.equals(resType)) {
			o = resources.getColorStateList(id);
//			o = resources.getColor(id);
		}

		return o;
	}

	private Object getResourcesValueByName(Context context, String resName,
			String resType) {
		String name = resName.toLowerCase().trim();
		Resources resources = context.getResources();
		int id = resources.getIdentifier(name, resType,
				context.getPackageName());
		Object o = null;
		if (id != 0) {
			o = getResourcesValue(resources, id, resType);
		}

		return o;
	}

	private Object getDefaultResourceValue(Context context, int resId) {
		Resources resources = context.getResources();
		String resType = resources.getResourceTypeName(resId);
		return getResourcesValue(resources, resId, resType);
	}

	private Object getThemeResources(Context context, int resId) {
		Object o = null;
		Resources resources = context.getResources();
		String resType = resources.getResourceTypeName(resId);
		String resName = resources.getResourceEntryName(resId);
		if (THEME_NIGHT.equals(mThemePackageName)) {
			resName = RES_NIGHT_PREFIX + resName;
		}

		o = getResourcesValueByName(mThemeContext, resName, resType);
		if (o == null) {
			o = getDefaultResourceValue(context, resId);
		}

		return o;
	}

	public ColorStateList getThemeColor(Context context, int id) {
		return (ColorStateList) getThemeResources(context, id);
	}

	public Drawable getThemeDrawable(Context context, int id) {
		return (Drawable) getThemeResources(context, id);
	}

	public void setImageViewSrc(Activity activity, int viewId, int resId) {
		ImageView imgView = (ImageView) activity.findViewById(viewId);
		if (imgView != null) {
			setImageViewSrc(activity, imgView, resId);
		}
	}

	public void setImageViewSrc(Context context, ImageView view, int resId) {
		view.setImageDrawable(getThemeDrawable(context, resId));
	}

	public void setTextViewColor(Activity activity, int viewId, int resId) {
		TextView view = (TextView) activity.findViewById(viewId);
		if (view != null) {
			setTextViewColor(activity, view, resId);
		}
	}

	public void setTextViewColor(Context context, TextView view, int resId) {
		view.setTextColor(getThemeColor(context, resId));
	}
	
	public void setTextViewCompoundDrawables(Activity activity, int viewId, int resId, int position) {
		TextView view = (TextView) activity.findViewById(viewId);
		if (view != null) {
			setTextViewCompoundDrawables(activity, view, resId, position);
		}
	}
	
	public void setTextViewCompoundDrawables(Context context, TextView view, int resId, int position) {
		Drawable drawable = getThemeDrawable(context, resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		switch(position) {
		case COMPOUND_DRAWABLE_LEFT:
			view.setCompoundDrawables(drawable, null, null, null);
			break;
		case COMPOUND_DRAWABLE_TOP:
			view.setCompoundDrawables(null, drawable, null, null);
			break;
		case COMPOUND_DRAWABLE_RIGHT:
			view.setCompoundDrawables(null, null, drawable, null);
			break;
		case COMPOUND_DRAWABLE_BOTTOM:
			view.setCompoundDrawables(null, null, null, drawable);
			break;
		}
	}

	public void setListViewDivider(Activity activity, int viewId, int resId) {
		ListView view = (ListView) activity.findViewById(viewId);
		if (view != null) {
			setListViewDivider(activity, view, resId);
		}
	}

	public void setListViewDivider(Context context, ListView view, int resId) {
		view.setDivider(getThemeDrawable(context, resId));
	}
	
	public void setListViewDividerColor(Context context, ListView view, int resId) {
		ColorStateList list = getThemeColor(context, resId);
		view.setDivider(new ColorDrawable(list.getDefaultColor()));
		view.setDividerHeight(Tools.dip2px(context, 1));
	}

	public void setListViewSelector(Activity activity, int viewId, int resId) {
		ListView view = (ListView) activity.findViewById(viewId);
		if (view != null) {
			setListViewSelector(activity, view, resId);
		}
	}

	public void setListViewSelector(Context context, ListView view, int resId) {
		view.setSelector(getThemeDrawable(context, resId));
	}

	public void setViewBackground(Activity activity, int viewId, int resId) {
		View view = activity.findViewById(viewId);
		if (view != null) {
			setViewBackground(activity, view, resId);
		}
	}

	public void setViewBackground(Context context, View view, int resId) {
		view.setBackgroundDrawable(getThemeDrawable(context, resId));
	}

	public void setViewBackgroundColor(Activity activity, int viewId, int resId) {
		View view = activity.findViewById(viewId);
		if (view != null) {
			setViewBackgroundColor(activity, view, resId);
		}
	}

	public void setViewBackgroundColor(Context context, View view, int resId) {
		ColorStateList list = getThemeColor(context, resId);
		view.setBackgroundColor(list.getDefaultColor());
	}

	public void setWindowBackground(Activity activity, int resId) {
		Window wnd = activity.getWindow();
		wnd.setBackgroundDrawable(getThemeDrawable(activity, resId));
	}

	public void registerThemeCallback(ThemeCallback callback) {
		if (callback == null)
			return;

		synchronized (mCallbacks) {
			WeakReference<ThemeCallback> weakRef = new WeakReference<ThemeCallback>(
					callback);
			mCallbacks.add(weakRef);
		}
	}

	public void unregisterThemeCallback(ThemeCallback callback) {
		if (callback == null)
			return;
		synchronized (mCallbacks) {
			ArrayList<WeakReference<ThemeCallback>> subList = new ArrayList<WeakReference<ThemeCallback>>();
			for (int i = 0; i < mCallbacks.size(); i++) {
				WeakReference<ThemeCallback> weakRef = mCallbacks.get(i);
				if (weakRef != null) {
					if (callback.equals(weakRef.get())) {
						subList.add(weakRef);
					}
				}
			}

			mCallbacks.removeAll(subList);
		}
	}
}
