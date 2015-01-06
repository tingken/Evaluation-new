package com.evaluation.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class GestureListView extends ListView {
	private PageControl pageControl;
	public GestureListView(Context context) {
		super(context);
	}
	public GestureListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//init();
	}
	public void init(PageControl pageControl) {
		this.pageControl = pageControl;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLvDetector.onTouchEvent(ev))
			return true;
		return super.onTouchEvent(ev);
	}

	private GestureDetector mLvDetector = new GestureDetector(
			new OnGestureListener() {
				// 手指在屏幕上移动距离小于此值不会被认为是手势
				private static final int SWIPE_MIN_DISTANCE = 120;
				// 手指在屏幕上移动速度小于此值不会被认为手势
				private static final int SWIPE_THRESHOLD_VELOCITY = 150;

				@Override
				public boolean onDown(MotionEvent e) {
					int position = pointToPosition((int) e.getX(),
							(int) e.getY());
					if (position != ListView.INVALID_POSITION) {
						View child = getChildAt(position
								- getFirstVisiblePosition());
						if (child != null)
							child.setPressed(true);
					}
					return true;
				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						// left
						Log.i("GestureDemo", "ListView left");
						pageControl.showNext();
						return true;
					} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						// right
						Log.i("GestureDemo", "ListView right");
						pageControl.showPrevious();
						return true;
					}
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					System.out.println("Listview long press");
					int position = pointToPosition((int) e.getX(),
							(int) e.getY());
					if (position != ListView.INVALID_POSITION) {
						View child = getChildAt(position
								- getFirstVisiblePosition());
						if (child != null)
							GestureListView.this.showContextMenuForChild(child);
					}
				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					return false;
				}

				@Override
				public void onShowPress(MotionEvent e) {

				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return false;
				}

			});
}
