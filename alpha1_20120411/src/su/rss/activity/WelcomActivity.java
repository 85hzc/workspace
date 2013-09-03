package su.rss.activity;

import su.rss.R;
import su.rss.executer.DownloadFeedsExecuter;
import su.rss.service.OfflineDownloadService;
import su.rss.util.Logger;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WelcomActivity extends BaseActivity {
	public final int START_MAINACTIVITY = 0;
	private Handler handler = new Handler()

	{ @Override
		public void handleMessage(Message msg)
	{
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch(msg.what)
		{
		case START_MAINACTIVITY:
			Intent intent = new Intent(WelcomActivity.this,
					MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	}; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcom);
		
		handler.sendEmptyMessageDelayed(START_MAINACTIVITY, 1000);
	}

}
