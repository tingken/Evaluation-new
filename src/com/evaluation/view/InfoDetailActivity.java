package com.evaluation.view;

import java.util.List;

import com.evaluation.control.AnnouncementManager;
import com.evaluation.dao.DatabaseAdapter;
import com.evaluation.model.Announcement;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class InfoDetailActivity extends Activity implements OnGestureListener {
	private int currentItem;
	private DatabaseAdapter dba;
	private List<Announcement> annoList;
	private String account;
	private ImageButton back;
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
	private String dateValue;
	private String weekValue;
	private String timeValue;
	private TextView titleView;
	private TextView repDateView;
	private TextView contentView;
	private String TAG = "effort";
	private boolean activityOver = false;
	// 手指在屏幕上移动距离小于此值不会被认为是手势
	private static final int SWIPE_MIN_DISTANCE = 60;
	// 手指在屏幕上移动速度小于此值不会被认为手势
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector gd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.info_detail);
		currentItem = this.getIntent().getIntExtra("currentItem", -1);
		account = this.getIntent().getStringExtra("account");
		Log.e(TAG, account + "-" + currentItem);
		dba = new DatabaseAdapter(this);
		dba.open();
		annoList = dba.findAnnouncementsByAccount(account);
        gd = new GestureDetector(this);
        dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
		
        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoDetailActivity.this.finish();
			}
		});
        if(currentItem >= 0) {
	        Announcement anno = dba.findAnnouncementsById(currentItem);
	        titleView = (TextView) findViewById(R.id.title);
	        if(anno.getTitle() != null && !anno.getTitle().equals("null"))
	        	titleView.setText(anno.getTitle());
	    	repDateView = (TextView) findViewById(R.id.rep_date);
	    	if(anno.getRepDate() != null && !anno.getRepDate().equals("null"))
	    		repDateView.setText(anno.getRepDate());
	    	contentView = (TextView) findViewById(R.id.content);
	    	if(anno.getContent() != null && !anno.getContent().equals("null"))
	    		contentView.setText(anno.getContent());
        }
    	dba.close();
		Thread dateThread = new DateThread();
		dateThread.start();
		registerReceiver(mBroadcastReceiver, new IntentFilter("TIMEOUT"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("LEAVE_INFO"));
    }
 // 设置标题上的时间
 	private Handler dateHandler = new Handler() {
 		public void handleMessage(android.os.Message msg) {
 	        dateView.setText(dateValue);
 	        timeView.setText(timeValue);
 	        weekView.setText("星期" + weekValue);
 		};
 	};
 	/**
	 * 修改标题栏上的时间
	 */
	private class DateThread extends Thread {
		public void run() {
			while(!activityOver) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Time time = new Time("GMT+8");
		        time.setToNow();
		        int week = time.weekDay;
		        dateValue = DateFormat.format("yyyy.MM.dd", System.currentTimeMillis()).toString();
		        timeValue = DateFormat.format("kk : mm", System.currentTimeMillis()).toString();
		        weekValue = String.valueOf(week);
		        if("0".equals(weekValue)){  
		        	weekValue ="天";  
		        }else if("1".equals(weekValue)){  
		        	weekValue ="一";  
		        }else if("2".equals(weekValue)){  
		        	weekValue ="二";  
		        }else if("3".equals(weekValue)){  
		        	weekValue ="三";  
		        }else if("4".equals(weekValue)){  
		        	weekValue ="四";
		        }else if("5".equals(weekValue)){  
		        	weekValue ="五";  
		        }else if("6".equals(weekValue)){  
		        	weekValue ="六";  
		        }
				dateHandler.sendEmptyMessage(1);
			}
		}
	}
	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		activityOver = true;
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		if(mBroadcastReceiver != null)
			this.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("TIMEOUT")) {
	        	Log.e(TAG, "TIMEOUT");
	        	//Toast.makeText(EvaluationActivity.this, "HEART_BEAT", Toast.LENGTH_SHORT).show();
	        	InfoDetailActivity.this.finish();
	        } else if(intent.getAction().equals("LEAVE_INFO")) {
	        	InfoDetailActivity.this.finish();
	        }
		}
	};

	/**
     * 覆写此方法，以解决ListView滑动被屏蔽问题
     */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
	        this.gd.onTouchEvent(event);
	        return super.dispatchTouchEvent(event);
	}

	/**
	 * 覆写此方法，以使用手势识别
	 */
	@Override  
	public boolean onTouchEvent(MotionEvent event) {
	    Log.v(TAG, "onTouchEvent");
	    return this.gd.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			// left
			Log.i(TAG, "ListView left");
			showNext();
			return true;
		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			// right
			Log.i(TAG, "ListView right");
			showPrevious();
			return true;
		}
		return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	public void showNext() {
		Announcement anno = new Announcement();
		for(int i = 0; i < annoList.size(); i++) {
			Log.e(TAG, "currentItem:" + currentItem);
			if(annoList.get(i).getId() == currentItem && i < annoList.size()-1) {
				anno = annoList.get(i+1);
				currentItem = anno.getId();
				if(anno.getTitle() != null && !anno.getTitle().equals("null"))
		        	titleView.setText(anno.getTitle());
		    	if(anno.getRepDate() != null && !anno.getRepDate().equals("null"))
		    		repDateView.setText(anno.getRepDate());
		    	if(anno.getContent() != null && !anno.getContent().equals("null"))
		    		contentView.setText(anno.getContent());
		    	break;
			}
		}
	}
	public void showPrevious() {
		Announcement anno = new Announcement();
		for(int i = 0; i < annoList.size(); i++) {
			Log.e(TAG, "currentItem:" + currentItem);
			if(annoList.get(i).getId() == currentItem && i > 0) {
				anno = annoList.get(i-1);
				currentItem = anno.getId();
				if(anno.getTitle() != null && !anno.getTitle().equals("null"))
		        	titleView.setText(anno.getTitle());
		    	if(anno.getRepDate() != null && !anno.getRepDate().equals("null"))
		    		repDateView.setText(anno.getRepDate());
		    	if(anno.getContent() != null && !anno.getContent().equals("null"))
		    		contentView.setText(anno.getContent());
		    	break;
			}
		}
	}
}
