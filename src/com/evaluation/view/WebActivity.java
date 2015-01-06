package com.evaluation.view;

import java.io.UnsupportedEncodingException;

import com.evaluation.control.AccountManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class WebActivity extends Activity {
	//private View main;
	private WebView webView;
	private RelativeLayout webLayout;
	private ImageButton back;
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
	private TextView titleView;
	private int tag;
	private String data;
	private String loginId;
	private String dateValue;
	private String weekValue;
	private String timeValue;
	private String TAG = "effort";
	private boolean activityOver = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去除标题 、全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.web);
		((MyApplication)this.getApplication()).addActivity(this);
		webLayout = (RelativeLayout) findViewById(R.id.webLayout);
//		webView = new WebView(this);
//		webView.setLayoutParams(new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		webView.getSettings().setSupportZoom(true);
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setBuiltInZoomControls(true);
//		webLayout.addView(webView);
		tag = this.getIntent().getIntExtra("tag", 0);
		loginId = this.getIntent().getStringExtra("loginId");
//		String url = this.getIntent().getStringExtra("url");
//		String content = this.getIntent().getStringExtra("content");
//		String title = this.getIntent().getStringExtra("title");
//		if(url != null) {
//			if(url.indexOf("http://") < 0)
//				url = "http://" + url;
//			webView.loadUrl(url);
//		} else if(content != null) {
//			//webView.getSettings().setDefaultTextEncodingName(“UTF -8”) ;
//			webView.loadDataWithBaseURL(null, content, "text/plain",  "utf-8", null);
//		}
		//设置Web视图 
//        webView.setWebViewClient(new HelloWebViewClient ());
        dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
//		titleView = (TextView) findViewById(R.id.title);
//		if(title != null)
//			titleView.setText(title);
		
        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WebActivity.this.finish();
			}
		});
        registerReceiver(mBroadcastReceiver, new IntentFilter("TIMEOUT"));
        registerReceiver(mBroadcastReceiver, new IntentFilter("LEAVE_INFO"));
		Thread dateThread = new DateThread();
		dateThread.start();
		Thread uiThread = new UiThread();
		uiThread.start();
    }
	
    @Override
    //设置回退 
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView != null && webView.canGoBack()) { 
        	webView.goBack(); //goBack()表示返回WebView的上一页面 
            return true; 
        } else if(keyCode == KeyEvent.KEYCODE_BACK) {
        	this.finish();
        	return true;
        }
        return false; 
    } 
     
    //Web视图 
    private class HelloWebViewClient extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); 
            return true; 
        } 
    }
    private class UiThread extends Thread {
    	public void run() {
    		String url;
    		AccountManager am = new AccountManager(WebActivity.this);
    		switch(tag) {
    		case 1:
				url = "GscSupport.svc/BusinessGuideWebSites?loginId=" + loginId;
				data = am.getData("url", url);
	    		uiHandler.sendEmptyMessage(1);
				break;
			case 2:
				url = "GscSupport.svc/JobStatements?loginId=" + loginId;
				data = am.getData("content", url);
	    		uiHandler.sendEmptyMessage(2);
//				if(data != null){
//		            intent.putExtra("content", data);
//		            intent.putExtra("title", "岗位职责");
//				}
				break;
			case 3:
				url = "GscSupport.svc/ServicePromises?loginId=" + loginId;
				data = am.getData("content", url);
	    		uiHandler.sendEmptyMessage(3);
//				if(data != null){
//		            intent.putExtra("content", data);
//		            intent.putExtra("title", "服务承诺");
//				}
				break;
			case 5:
				url = "GscSupport.svc/OfficeCenterWebSites?loginId=" + loginId;
				data = am.getData("url", url);
	    		uiHandler.sendEmptyMessage(1);
				break;
			default:
				break;
    		}
    		am.close();
    	}
    }
    private Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				webView = new WebView(WebActivity.this);
				webView.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.getSettings().setBuiltInZoomControls(true);
				webLayout.addView(webView);
				if(data != null) {
					if(data.indexOf("http://") < 0)
						data = "http://" + data;
					webView.loadUrl(data);
				}
				break;
			case 2:
				if(isHtml(data)) {
					webView = new WebView(WebActivity.this);
					webView.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					webView.getSettings().setSupportZoom(true);
					webView.getSettings().setJavaScriptEnabled(true);
					webView.getSettings().setBuiltInZoomControls(true);
					webLayout.addView(webView);
					webView.loadDataWithBaseURL(null, data, "text/plain",  "utf-8", null);
				}else {
					LayoutInflater inflater = LayoutInflater.from(WebActivity.this);
					// 引入窗口配置文件
					View view = inflater.inflate(R.layout.web_content, null);
					view.setLayoutParams(new LinearLayout.LayoutParams(
							664, LayoutParams.MATCH_PARENT));
					TextView title = (TextView) view.findViewById(R.id.title);
					TextView contentView = (TextView) view.findViewById(R.id.contentView);
					webLayout.addView(view);
					title.setText("岗位职责");
					contentView.setText(data);
				}
				break;
			case 3:
				if(isHtml(data)) {
					webView = new WebView(WebActivity.this);
					webView.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					webView.getSettings().setSupportZoom(true);
					webView.getSettings().setJavaScriptEnabled(true);
					webView.getSettings().setBuiltInZoomControls(true);
					webLayout.addView(webView);
					webView.loadDataWithBaseURL(null, data, "text/plain",  "utf-8", null);
				}else {
					LayoutInflater inflater = LayoutInflater.from(WebActivity.this);
					// 引入窗口配置文件
					LinearLayout view = (LinearLayout)inflater.inflate(R.layout.web_content, null);
					view.setLayoutParams(new LinearLayout.LayoutParams(
							664, LayoutParams.MATCH_PARENT));
					TextView title = (TextView) view.findViewById(R.id.title);
					TextView contentView = (TextView) view.findViewById(R.id.contentView);
					webLayout.addView(view);
					title.setText("服务承诺");
					contentView.setText(data);
				}
				break;
			}
		};
	};
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
	protected void onStart() {
		// 当Activity不可见的时候停止切换
		activityOver = false;
		super.onStart();
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
	private boolean isHtml(String data) {
		if(data != null)
			return data.indexOf("<html>") >= 0;
		else
			return false;	
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("TIMEOUT")) {
	        	Log.e(TAG, "TIMEOUT");
	        	//Toast.makeText(EvaluationActivity.this, "HEART_BEAT", Toast.LENGTH_SHORT).show();
	        	WebActivity.this.finish();
	        } else if(intent.getAction().equals("LEAVE_INFO")) {
	        	WebActivity.this.finish();
	        }
		}
	};
}
