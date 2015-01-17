package com.evaluation.view;

import java.util.List;

import com.evaluation.dao.DatabaseManager;
import com.evaluation.model.Announcement;
import com.evaluation.util.GestureListView;
import com.evaluation.util.InfoListAdapter;
import com.evaluation.util.LineView;
import com.evaluation.util.OnPageChangeListener;
import com.evaluation.util.PageControl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class InfoCenterActivity extends Activity implements OnPageChangeListener {
	private int currentItem;
	private DatabaseManager dba;
	private List<Announcement> annoList;
	private List<Announcement> curList;
	private String account;
	private TextView back;
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
	private String dateValue;
	private String weekValue;
	private String timeValue;
	private InfoListAdapter adapter;
    private PageControl pageControl;
    private ListView infoListView;
    private int pageNum = 1;
    private final int numPerPage = 10;
    private int curPage = 1;
	private String TAG = "effort";
	private boolean activityOver = false;
	private LineView lineView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.info_center);
		account = this.getIntent().getStringExtra("account");
		DatabaseManager.initializeInstance(this);
		dba = DatabaseManager.getInstance();
		dba.open();
		annoList = dba.findAnnouncementsByAccount(account);
        dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
		
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoCenterActivity.this.finish();
			}
		});
        infoListView = (ListView) findViewById(R.id.listView);
        adapter = new InfoListAdapter(this, pageNum, numPerPage, dba.findOnePageAnno(account, 0, numPerPage));
        infoListView.setAdapter(adapter);
        infoListView.setOnItemClickListener(new ListView.OnItemClickListener() {
        	@Override 
            public void onItemClick(AdapterView<?> parent, View view, 
                    int position, long id) { 
                // TODO Auto-generated method stub 
                Log.i(TAG, "position=="+position); 
                Intent intent = new Intent(InfoCenterActivity.this, InfoDetailActivity.class);
                curList=dba.findOnePageAnno(account, (curPage-1)*numPerPage,numPerPage);
    			intent.putExtra("currentItem", curList.get(position).getId());
    			intent.putExtra("account", curList.get(position).getAccount());
    			InfoCenterActivity.this.startActivity(intent);
            }
        });
        //初始化分页组件  
        pageControl=(PageControl) findViewById(R.id.TableLayout1);  
        pageControl.setPageChangeListener(this);
        Log.e(TAG, "total: " + dba.findAnnouncementsByAccount(account).size());
        pageControl.initPageShow(dba.findAnnouncementsByAccount(account).size(), numPerPage);
        pageControl.setVisibility(View.VISIBLE);
		Thread dateThread = new DateThread();
		dateThread.start();
		registerReceiver(mBroadcastReceiver, new IntentFilter("TIMEOUT"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("LEAVE_INFO"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("UIUPDATE"));
    }
 // 设置标题上的时间
 	private Handler dateHandler = new Handler() {
 		public void handleMessage(android.os.Message msg) {
 	        dateView.setText(dateValue);
 	        timeView.setText(timeValue);
 	        weekView.setText("星期" + weekValue);
 		};
 	};
 	private void updateUI() {
 		if((pageNum-1)*numPerPage < dba.findAnnouncementsByAccount(account).size()) {
 			curList = dba.findOnePageAnno(account, (curPage-1) * numPerPage, numPerPage);
 			Log.e(TAG, "当前公告条数:" + curList.size());
	 		adapter.clear();
	        adapter.addAll(curPage, curList);
	        //infoListView.setAdapter(adapter);
	 		adapter.notifyDataSetChanged();
 		}
 	}
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
		dba.close();
		if(mBroadcastReceiver != null)
			this.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
	@Override
	public void pageChanged(int curPage, int numPerPage) {
		// TODO Auto-generated method stub
		Log.e(TAG, "pageChanged.curPage: " + curPage);
		this.curPage = curPage;
		curList=dba.findOnePageAnno(account, (curPage-1)*numPerPage,numPerPage);  
        adapter.clear();
        adapter.addAll(curPage, curList);
        pageNum++;
        adapter.notifyDataSetChanged();
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("TIMEOUT")) {
	        	Log.e(TAG, "TIMEOUT");
	        	//Toast.makeText(EvaluationActivity.this, "HEART_BEAT", Toast.LENGTH_SHORT).show();
	        	InfoCenterActivity.this.finish();
	        } else if(intent.getAction().equals("LEAVE_INFO")) {
	        	InfoCenterActivity.this.finish();
	        } else if(intent.getAction().equals("UIUPDATE")){
	        	Log.e(TAG, "通知中心刷新界面。");
	        	updateUI();
	        }
		}
	};
}
