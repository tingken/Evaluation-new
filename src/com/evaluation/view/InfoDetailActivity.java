package com.evaluation.view;

import java.util.List;

import com.evaluation.control.AnnouncementManager;
import com.evaluation.dao.DatabaseAdapter;
import com.evaluation.model.Announcement;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class InfoDetailActivity extends Activity {
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
		activityOver = false;
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}
}
