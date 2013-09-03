package su.rss.drag;

import java.util.HashMap;

import su.rss.util.AnimaitonUtil;
import su.rss.util.Logger;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class WorkSpace extends LinearLayout implements DragScroller,
		DropTarget, DragSource {

	private static final String TAG = "WorkSpace";
	private static final int LAYOUT_MARGIN = 3;

	DragController mDragController;
	ScrollView mScrollView;
	OnLongClickListener mLongClickListener;
	Context mContext;
	HashMap<Integer, View> mViewPosMap;
	int ScreenX;
	int ScreenY;

	public WorkSpace(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mViewPosMap = new HashMap<Integer, View>();
	}

	public WorkSpace(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mViewPosMap = new HashMap<Integer, View>();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int count = getChildCount();
		int childWidth = dip2px(mContext, 150);
		int childHeight = dip2px(mContext, 130);
		int paddingTop = getPaddingTop();
		if(height == 0) {
			height = (count / 2 + (count % 2)) * (childHeight + dip2px(mContext, LAYOUT_MARGIN) * 2) + paddingTop;
		}
		ScreenX = width;
		ScreenY = height;
		setMeasuredDimension(width, height);
		
		int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth,
				MeasureSpec.EXACTLY);
		int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight,
				MeasureSpec.EXACTLY);
		
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			child.measure(childWidthSpec, childHeightSpec);
			CellInfo cell = (CellInfo) child.getTag();
			if (cell == null) {
				cell = new CellInfo();
				mViewPosMap.put(i, child);
				cell.setPos(i);
			} else if(cell.getRect() == null) {
				mViewPosMap.put(i, child);
				cell.setPos(i);
			}
			
			cell.setViewPos(i);
			child.setTag(cell);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Logger.v("WorkSpace onLayout");
		final int count = getChildCount();
		int row = 0;
		int parentWidth = r - l;
		int paddingTop = getPaddingTop();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			CellInfo cell = (CellInfo) child.getTag();
			Rect rect = cell.getRect();
			if (rect != null) {
				child.layout(rect.left, rect.top, rect.right, rect.bottom);
				if(i % 2 == 1) {
					row++;
				}
				continue;
			}

			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			int left = parentWidth / 2 - dip2px(mContext, LAYOUT_MARGIN)
					- width;
			int top = row * (height + dip2px(mContext, LAYOUT_MARGIN) * 2) + t + paddingTop;
			if (i % 2 == 1) {
				left = parentWidth / 2 + dip2px(mContext, LAYOUT_MARGIN);
				row++;
			}
			child.layout(left, top, left + width, top + height);
			rect = new Rect(left, top, left + width, top + height);
			cell.setRect(rect);
			child.setTag(cell);
		}
	}
	
	@Override
	public void addView(View child, int index) {
		// TODO Auto-generated method stub
		
		View view = mViewPosMap.get(index);
		if(view != null) {
			moveBackward(mViewPosMap.size() - 1, index);
			mViewPosMap.put(index, child);
		}
		
		super.addView(child, index);
	}

	@Override
	public void removeView(View view) {
		// TODO Auto-generated method stub
		super.removeView(view);
		CellInfo info = (CellInfo) view.getTag();
		if(info != null) {
			int pos = info.getPos();
			moveForward(pos + 1, mViewPosMap.size() - 1);
			mViewPosMap.remove(mViewPosMap.size() - 1);
		}
	}

	@Override
	public void removeViewAt(int index) {
		// TODO Auto-generated method stub
		View child = getChildAt(index);
		CellInfo info = (CellInfo) child.getTag();
		if(info != null) {
			int pos = info.getPos();
			moveForward(pos + 1, mViewPosMap.size() - 1);
			mViewPosMap.remove(mViewPosMap.size() - 1);
		}
		super.removeViewAt(index);
	}

	@Override
	public void removeViewInLayout(View view) {
		// TODO Auto-generated method stub
		super.removeViewInLayout(view);
	}

	@Override
	public void removeViews(int start, int count) {
		// TODO Auto-generated method stub
		super.removeViews(start, count);
	}

	@Override
	public void removeViewsInLayout(int start, int count) {
		// TODO Auto-generated method stub
		super.removeViewsInLayout(start, count);
	}

	@Override
	public void scrollUp() {
		// TODO Auto-generated method stub
		Rect outRect = new Rect();
		mScrollView.getWindowVisibleDisplayFrame(outRect);
		int y = mScrollView.getScrollY() - 50;
		y = y < 50 ? 0 : y;
		mScrollView.scrollTo(0, y);
	}

	@Override
	public void scrollDown() {
		// TODO Auto-generated method stub
		int y = mScrollView.getScrollY() + 10;
		mScrollView.scrollTo(0, y);
	}
	
	public void setScrollView(ScrollView view) {
		mScrollView = view;
	}

	public void setDragController(DragController controller) {
		mDragController = controller;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDragEnter");
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		// Log.v(TAG, "onDragOver");
		CellInfo cell = (CellInfo) dragInfo;
		if (!cell.getRect().contains(x, y)) {
			moveChilds(x, y, cell);
		}
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDragExit");
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		// TODO Auto-generated method stub
		return null;
	}

	public void startDrag(View v) {
		CellInfo info = (CellInfo) v.getTag();
		mDragController.startDrag(v, this, v.getTag(),
				DragController.DRAG_ACTION_MOVE);

	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDropCompleted");

	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		// TODO Auto-generated method stub
		mLongClickListener = l;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).setOnLongClickListener(l);
		}
	}

	public int dip2px(Context context, int dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	private void moveChilds(int x, int y, CellInfo info) {
		int curpos = info.getPos();
		int pos = getChildPos(x, y, curpos);
		if (pos != -1) {
			setDragingViewInfo(pos, curpos);
			if (pos > curpos) {
				moveForward(curpos + 1, pos);
			} else {
				moveBackward(curpos - 1, pos);
			}

			mViewPosMap.put(pos, info.getView());
		}
	}

	private void setDragingViewInfo(int pos, int curpos) {
		View v = mViewPosMap.get(pos);
		CellInfo info = (CellInfo) v.getTag();
		Rect rect = info.getRect();
		View view = mViewPosMap.get(curpos);
		CellInfo cell = (CellInfo) view.getTag();
		cell.setRect(rect);
		cell.setPos(pos);
		view.setTag(cell);
	}

	private void moveForward(int start, int end) {
		for (int i = start; i <= end; i++) {
			View v = mViewPosMap.get(i);
			CellInfo info = (CellInfo) v.getTag();
			Rect oldrect = info.getRect();
			Rect r = getPreviousRect(oldrect);
			info.setPos(i - 1);
			info.setRect(r);
			v.setTag(info);
			mViewPosMap.put(i - 1, v);
			v.layout(r.left, r.top, r.right, r.bottom);
			if(v.isShown()) {
				if (oldrect.top != r.top) {
					v.startAnimation(AnimaitonUtil.TranslateAnimaiton(-1, 0, 1, 0));
				} else {
					v.startAnimation(AnimaitonUtil.TranslateAnimaiton(1, 0, 0, 0));
				}
			} else {
				int visibility = v.getVisibility();
				v.setVisibility(visibility);
			}
			
		}
	}

	private void moveBackward(int start, int end) {
		for (int i = start; i >= end; i--) {
			View v = mViewPosMap.get(i);
			CellInfo info = (CellInfo) v.getTag();
			Rect oldrect = info.getRect();
			Rect r = getBackwardRect(oldrect);
			v.layout(r.left, r.top, r.right, r.bottom);
			if(v.isShown()) {
				if (oldrect.top != r.top) {
					v.startAnimation(AnimaitonUtil.TranslateAnimaiton(1, 0, -1, 0));
				} else {
					v.startAnimation(AnimaitonUtil.TranslateAnimaiton(-1, 0, 0, 0));
				}
			} else {
				int visibility = v.getVisibility();
				v.setVisibility(visibility);
			}
			info.setPos(i + 1);
			info.setRect(r);
			v.setTag(info);
			mViewPosMap.put(i + 1, v);
		}
	}

	private Rect getPreviousRect(Rect r) {
		int x = r.left - r.width() - 2 * dip2px(mContext, LAYOUT_MARGIN);
		int y = r.top;
		if (x < 0) {
			x = r.left + r.width() + 2 * dip2px(mContext, LAYOUT_MARGIN);
			y = r.top - r.height() - 2 * dip2px(mContext, LAYOUT_MARGIN);
		}

		return new Rect(x, y, x + r.width(), y + r.height());
	}

	private Rect getBackwardRect(Rect r) {
		int x = r.left + r.width() + 2 * dip2px(mContext, LAYOUT_MARGIN);
		int y = r.top;
		if (x > ScreenX - r.width()) {
			x = r.left - r.width() - 2 * dip2px(mContext, LAYOUT_MARGIN);
			y = r.top + r.height() + 2 * dip2px(mContext, LAYOUT_MARGIN);
		}

		return new Rect(x, y, x + r.width(), y + r.height());
	}

	private int getChildPos(int x, int y, int curpos) {
		int start = curpos - 3;
		int end = curpos + 3;
		if(end > getChildCount()) {
			end = getChildCount() - 2;
		}
		for (int i = start; i <= end; i++) {
			if (mViewPosMap.containsKey(i)) {
				View v = mViewPosMap.get(i);
				CellInfo info = (CellInfo) v.getTag();
				if (info.getRect().contains(x, y) && info.isMovingEnable()) {
					return i != curpos ? i : -1;
				}
			}
		}

		return -1;
	}
}
