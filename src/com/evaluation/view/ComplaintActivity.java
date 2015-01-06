package com.evaluation.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.evaluation.control.AccountManager;
import com.evaluation.control.WebServiceManager;
import com.evaluation.model.ComplaintResult;
import com.evaluation.model.DealResult;
import com.evaluation.util.ComplaintDialog;
import com.evaluation.view.WebActivity.webViewClient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ComplaintActivity extends Activity {
	private ImageButton back;
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
	private TextView titleView;
	private String dateValue;
	private String weekValue;
	private String timeValue;
	private EditText nameEditor;
	private EditText telEditor;
	private EditText emailEditor;
	private EditText content;
    private Button submit;
    private Button cancel;
	private WebServiceManager wsm;
	private ComplaintResult complaintResult;
	private DealResult dealResult;
	private String TAG = "effort";
	private boolean activityOver = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去除标题 、全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.complaint);
		((MyApplication)this.getApplication()).addActivity(this);
		complaintResult = new ComplaintResult();
		dealResult = new DealResult();
        dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
		nameEditor = (EditText) findViewById(R.id.name);
		telEditor = (EditText) findViewById(R.id.tel);
		emailEditor = (EditText) findViewById(R.id.email);
		content = (EditText)findViewById(R.id.content);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(submitClickListener);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelClickListener);
		
        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ComplaintActivity.this.finish();
			}
		});
        registerReceiver(mBroadcastReceiver, new IntentFilter("TIMEOUT"));
        registerReceiver(mBroadcastReceiver, new IntentFilter("LEAVE_INFO"));
		Thread dateThread = new DateThread();
		dateThread.start();
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
	protected void onStart() {
		// 当Activity不可见的时候停止切换
		activityOver = false;
		super.onStart();
	}
	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		activityOver = true;
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			//imm.hideSoftInputFromInputMethod(content.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(ComplaintActivity.this.getCurrentFocus().getWindowToken(), 0);
		}
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
	        	ComplaintActivity.this.finish();
	        } else if(intent.getAction().equals("LEAVE_INFO")) {
	        	ComplaintActivity.this.finish();
	        }
		}
	};
private View.OnClickListener submitClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	String name = nameEditor.getText().toString();
        	String tel = telEditor.getText().toString();
        	String email = emailEditor.getText().toString();
        	String contentText = content.getText().toString();
        	if(!contentText.equals("")) {
        		sendComplaint(name, tel, email, contentText);
        	}else
        		return;
        	ComplaintActivity.this.finish();
        }
    };
    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	ComplaintActivity.this.finish();
            //Toast.makeText(context, deviceId, Toast.LENGTH_LONG).show();
        }
    };
    public void sendComplaint(String name, String tel, String email, String content){
    	Thread sendComplaintThread = new SendComplaintThread(name, tel, email, content);
    	sendComplaintThread.start();
    }
	private void dealComplaintResult() {
		if(complaintResult == null || complaintResult.getStatus() == null){
			Intent intent = new Intent("COMPLAINT_FAIL");
			ComplaintActivity.this.sendBroadcast(intent);
			return;
		}
		if(complaintResult.getStatus().equals("1")) {
        	Intent intent = new Intent("COMPLAINT_SUCCESS");
        	ComplaintActivity.this.sendBroadcast(intent);
			getResponse(complaintResult.getKey());
		}else{
			Intent intent = new Intent("COMPLAINT_FAIL");
			ComplaintActivity.this.sendBroadcast(intent);
		}
	}
	private void getResponse(String key) {
		Thread getResponseThread = new GetResponseThread(key);
		getResponseThread.start();
	}
	private ComplaintResult getComplaintResult(String jsonString) {
		ComplaintResult cr = new ComplaintResult();
    	try {
			JSONObject jsonObject = new JSONObject(jsonString);
			cr.setStatus(jsonObject.getString("Status"));
			cr.setDescription(jsonObject.getString("Description"));
			cr.setKey(jsonObject.getString("Key"));
			cr.setMaxTime(jsonObject.getString("MaxTime"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	return cr;
    }
	private String getJsonValue(String jsonString, String key) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			return jsonObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	private class SendComplaintThread extends Thread{
		private String name;
		private String tel;
		private String email;
		private String content;
		public SendComplaintThread(String name, String tel, String email, String content) {
			this.name = name;
			this.tel = tel;
			this.email = email;
			this.content = content;
		}
		public void run() {
			wsm = new WebServiceManager(ComplaintActivity.this);
			complaintResult = wsm.addUserComplaintsPad(name, tel, email, content);
        	dealComplaintResult();
		}
	}
	private class GetResponseThread extends Thread{
		private String key;
		public GetResponseThread(String key) {
			this.key = key;
		}
		public void run() {
			int maxTime = Integer.parseInt(complaintResult.getMaxTime());
			String key = complaintResult.getKey();
			for(int i = 0; i < maxTime/60 ; i++) {
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dealResult = wsm.getComplaintsCheckNotice(key);
				if(dealResult.getStatus().equals("1")){
					Intent intent = new Intent("COMPLAINT_RESULT");
					intent.putExtra("description", dealResult.getDescription());
					ComplaintActivity.this.sendBroadcast(intent);
				}
	        	
			}
		}
	}
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
