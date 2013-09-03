package su.rss.drag;

import su.rss.data.RSSFeed;
import android.graphics.Rect;
import android.view.View;

public class CellInfo {
	int position;
	int viewPos;
	Rect rect;
	View view;
	RSSFeed feed;
	boolean moveEnable;
	
	public void setViewPos(int pos) {
		viewPos = pos;
	}
	
	public int getViewPos() {
		return viewPos;
	}
	
	public void setMovingEnable(boolean enable) {
		moveEnable = enable;
	}
	
	public boolean isMovingEnable() {
		return moveEnable;
	}
	
	public void setPos(int pos) {
		position = pos;
	}

	public int getPos() {
		return position;
	}

	public void setRect(Rect r) {
		rect = r;
	}

	public Rect getRect() {
		return rect;
	}

	public void setView(View v) {
		view = v;
	}

	public View getView() {
		return view;
	}
	
	public void setFeed(RSSFeed f) {
		feed = f;
	}
	
	public RSSFeed getFeed() {
		return feed;
	}
}
