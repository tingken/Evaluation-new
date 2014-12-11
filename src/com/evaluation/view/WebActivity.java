package com.evaluation.view;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Intent;
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
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;


public class WebActivity extends Activity {
	//private View main;
	private WebView webView;
	private ImageButton back;
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
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
		webView = (WebView) findViewById(R.id.webView);    
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		String url = this.getIntent().getStringExtra("url");
		String content = this.getIntent().getStringExtra("content");
		if(url != null) {
			if(url.indexOf("http://") < 0)
				url = "http://" + url;
			webView.loadUrl(url);
		} else if(content != null) {
			//webView.getSettings().setDefaultTextEncodingName(“UTF -8”) ;
			webView.loadDataWithBaseURL(null, content, "text/plain",  "utf-8", null);
		}
		//设置Web视图 
        webView.setWebViewClient(new HelloWebViewClient ());
        dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
		
        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WebActivity.this.finish();
			}
		});
		Thread dateThread = new DateThread();
		dateThread.start();
    } 
     
    @Override
    //设置回退 
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) { 
        	webView.goBack(); //goBack()表示返回WebView的上一页面 
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
