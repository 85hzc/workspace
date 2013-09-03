package su.rss.activity;

import su.rss.R;

import su.rss.util.FileUtil;
import su.rss.util.Logger;
import su.rss.util.ThemeSettingHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity implements
		OnPreferenceChangeListener, ThemeSettingHelper.ThemeCallback,
		OnClickListener {
	boolean isPaused;
	public ThemeSettingHelper mThemeSettingHelper;
	boolean isThemeChanged;
	private ImageButton mBackBtn;
	private View mTitleBar;
	private TextView mTitleText;
	public static final String FONT_PRE_KEY = "fontkey";
	public static final String NET_PRE_KEY = "network";
	public static final String DOWNLOAD_NO_PRE_KEY = "offline_no";
	public static final String IS_PIC_ALLOW_KEY = "is_pic_allow";
	public static final String IS_AUTO_UPDATE_KEY = "is_auto_update";
	
	public static final String NET_WIFI = "WIFI";
	public static final String NET_G = "3G/2G";
	public static final String NET_NOT_LIMIT = "not_limit";

	private Preference cleanHistory;
	private Preference login;
	private ListPreference font_listPreference;
	private ListPreference net_listPreference;
	private ListPreference download_no_listPreference;

	View checkbox_read;
	ListView mListView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isThemeChanged = true;
		mThemeSettingHelper = ThemeSettingHelper.getThemeSettingHelper(this);
		mThemeSettingHelper.registerThemeCallback(this);
		setContentView(R.layout.setting);
		addPreferencesFromResource(R.xml.settings);
		setupViews();
		init();
		
	}

	private void setupViews() {
		mTitleBar = findViewById(R.id.title_bar);
		ImageView title_icon = (ImageView) mTitleBar
				.findViewById(R.id.title_bar_icon);
		title_icon.setVisibility(View.GONE);
		mTitleText = (TextView) mTitleBar.findViewById(R.id.title_bar_text);
		mTitleText.setText(getResources().getString(R.string.menu_setting));
		mBackBtn = (ImageButton) mTitleBar
				.findViewById(R.id.title_favorite_btn);
		mBackBtn.setBackgroundResource(R.drawable.title_home_bg);
		mBackBtn.setImageResource(R.drawable.title_home_icon);
		mBackBtn.setOnClickListener(this);
	}

	private void init() {
		String net_work;
		mListView = (ListView) findViewById(android.R.id.list);
		cleanHistory = findPreference("cleanhistory");
		login = findPreference("login");
		font_listPreference = (ListPreference) findPreference(FONT_PRE_KEY);
		net_listPreference = (ListPreference) findPreference(NET_PRE_KEY);
		download_no_listPreference = (ListPreference) findPreference(DOWNLOAD_NO_PRE_KEY);

		font_listPreference.setSummary(getResources().getString(
				R.string.font_size_summary)
				+ font_listPreference.getEntry());
		net_work = net_listPreference.getEntry().toString();
		if (net_work.equals(NET_NOT_LIMIT)) {
			net_listPreference.setSummary(getResources().getString(
					R.string.network_summary)
					+ getResources().getString(R.string.net_work_not_limit));
		} else
			net_listPreference.setSummary(getResources().getString(
					R.string.network_summary)
					+ net_work);

		download_no_listPreference.setSummary(getResources().getString(
				R.string.offline_down_no_summary)
				+ download_no_listPreference.getEntry());

		font_listPreference.setOnPreferenceChangeListener(this);
		net_listPreference.setOnPreferenceChangeListener(this);
		download_no_listPreference.setOnPreferenceChangeListener(this);
	}

	private void Login_Dialog() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		final Editor ed = settings.edit();
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.log_dialog, null);
		final EditText usrname = (EditText) view
				.findViewById(R.id.txt_username);
		final EditText password = (EditText) view
				.findViewById(R.id.txt_password);
		usrname.setText(settings.getString("usrname", ""));
		password.setText(settings.getString("password", ""));

		AlertDialog.Builder builder2 = new AlertDialog.Builder(
				SettingActivity.this);
		builder2.setView(view);
		builder2.setTitle(getResources().getString(R.string.login_title))
				.setPositiveButton(getResources().getString(R.string.login),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ed.putString("usrname", usrname.getText()
										.toString());
								ed.putString("password", password.getText()
										.toString());
								ed.commit();
								dialog.dismiss();
							}
						})
				.setNegativeButton(getResources().getString(R.string.logout),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).create().show();

	}

	private void CleanHistory_Dialog() {
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.clean_history))
				.setMessage(getResources().getString(R.string.is_clean_history))
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								FileUtil.clearCache();
								Toast.makeText(
										SettingActivity.this,
										getResources().getString(
												R.string.clean_ok),
										Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton(getResources().getString(R.string.no),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == cleanHistory) {
			CleanHistory_Dialog();
		} else if (preference == login) {
			Login_Dialog();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(FONT_PRE_KEY)) {
			String str = font_listPreference.getSharedPreferences().getString(
					FONT_PRE_KEY, "");
			if (str.equals(newValue) != true) {
				font_listPreference.setValue((String) newValue);
				preference.setSummary(getResources().getString(
						R.string.font_size_summary)
						+ font_listPreference.getEntry());
			}

		} else if (preference.getKey().equals(NET_PRE_KEY)) {
			if (newValue.toString().equals(NET_NOT_LIMIT)) {
				preference
						.setSummary(getResources().getString(
								R.string.network_summary)
								+ getResources().getString(
										R.string.net_work_not_limit));
			} else
				preference.setSummary(getResources().getString(
						R.string.network_summary)
						+ (String) newValue);
		} else if (preference.getKey().equals(DOWNLOAD_NO_PRE_KEY)) {
			preference.setSummary(getResources().getString(
					R.string.offline_down_no_summary)
					+ (String) newValue);
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPaused = false;
		if (isThemeChanged) {
			isThemeChanged = false;
			onApplyTheme();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		isPaused = true;
	}

	@Override
	public void applyTheme() {
		if (isPaused) {
			isThemeChanged = true;
		} else {
			onApplyTheme();
		}
	}

	public void onApplyTheme() {
		mThemeSettingHelper.setViewBackground(this, mTitleBar,
				R.drawable.title_bar_bg);
		mThemeSettingHelper.setTextViewColor(this, mTitleText,
				R.color.title_text_color);
		ListView view = getListView();
		mThemeSettingHelper.setListViewDividerColor(this, view,
				R.color.list_divider_color);
		mThemeSettingHelper.setViewBackgroundColor(this, view, R.color.activity_bg_color);
		view.requestLayout();
	}

	@Override
	public void onClick(View v) {
		finish();
	}
}
