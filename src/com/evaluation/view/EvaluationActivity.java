package com.evaluation.view;

import java.util.ArrayList;
import java.util.List;

import com.evaluation.model.Evaluation;
import com.evaluation.model.User;
import com.evaluation.service.HomeService;
import com.evaluation.control.*;
import com.evaluation.dao.DatabaseManager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EvaluationActivity extends Activity implements OnClickListener, OnInitListener{
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
	private String dateValue;
	private String weekValue;
	private String timeValue;
	private boolean activityOver = false;
	//private ImageButton setting;
	private ImageButton button0;
	private ImageButton button1;
	private ImageButton button2;
	private ImageButton button3;
	private ImageButton button4;
	private ImageButton button5;
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	private TextView thanks;
	private String account = "";
	private String loginId = "";
	private TextToSpeech tts;
	private String TAG = "effort";
	private int MY_DATA_CHECK_CODE = 0;
	private boolean _isBound;
	private HomeService _boundService;
	private SharedPreferences sp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.evaluation);
		dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
		//loginId = this.getIntent().getStringExtra("loginId");
		((MyApplication)this.getApplication()).addActivity(this);
		account = ((MyApplication)this.getApplication()).getAccount();
		//loginId = ((MyApplication)this.getApplication()).getLoginId();
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		loginId = sp.getString("loginId", "");
//		setting = (ImageButton) findViewById(R.id.setting);
//		setting.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				toTtsSettings();
//			}
//			
//		});
		button0 = (ImageButton) findViewById(R.id.button0);
		button0.setTag(0);
		button0.setOnClickListener(this);
		buttons.add(button0);
		button1 = (ImageButton) findViewById(R.id.button1);
		button1.setTag(1);
		button1.setOnClickListener(this);
		buttons.add(button1);
		button2 = (ImageButton) findViewById(R.id.button2);
		button2.setTag(2);
		button2.setOnClickListener(this);
		buttons.add(button2);
		button3 = (ImageButton) findViewById(R.id.button3);
		button3.setTag(3);
		button3.setOnClickListener(this);
		buttons.add(button3);
		button4 = (ImageButton) findViewById(R.id.button4);
		button4.setTag(4);
		button4.setOnClickListener(this);
		buttons.add(button4);
		button5 = (ImageButton) findViewById(R.id.button5);
		button5.setTag(5);
		button5.setOnClickListener(this);
		buttons.add(button5);
		
		thanks = (TextView) findViewById(R.id.thanks);
		registerReceiver(mBroadcastReceiver, new IntentFilter("TIMEOUT"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("LEAVE_INFO"));
		startService();
		bindService();
	}
	@Override
	public void onStart() {
//		Intent checkIntent = new Intent();
//		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		
		activityOver = false;
		//tts = _boundService.getTts();
		Thread dateThread = new DateThread();
		dateThread.start();
		
		super.onStart();
	}
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == MY_DATA_CHECK_CODE) {
//			Log.e(TAG, "resultCode: " + resultCode);
//			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
//				// success, create the TTS instance
//				tts = new TextToSpeech(this, this);
//			} else {
//				// missing data, install it
//				Intent installIntent = new Intent();
//				installIntent
//						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//				startActivity(installIntent);
//				toTtsSettings();
//			}
//		}
//
//	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onClick");
		int tag = (Integer)view.getTag();
		for(ImageButton button : buttons) {
			button.setEnabled(false);
		}
		thanks.setText("谢谢您的评价，我们会努力做到更好。");
		//tts.speak("谢谢您的评价，我们会努力做到更好。", TextToSpeech.QUEUE_ADD, null);
		_boundService.setWords("谢谢您的评价，我们会努力做到更好。");
		switch(tag){
		case 0:
			button0.setBackgroundResource(R.drawable.face_sel);
			saveData(1);
			break;
		case 1:
			button1.setBackgroundResource(R.drawable.face_sel);
			saveData(2);
			break;
		case 2:
			button2.setBackgroundResource(R.drawable.face_sel);
			saveData(3);
			break;
		case 3:
			button3.setBackgroundResource(R.drawable.face_sel);
			saveData(4);
			break;
		case 4:
			button4.setBackgroundResource(R.drawable.face_sel);
			saveData(5);
			break;
		case 5:
			button5.setBackgroundResource(R.drawable.face_sel);
			saveData(6);
			break;
		default :
			break;
		}
	}
//	private void setOtherButtonUnEnabled(int id) {
//		for(int i = 0; i < 6; i++){
//			if(id != i)
//				(Button)Class.forName("button" + i).
//		}
//	}
	private void saveData(int data){
		if(loginId != null && !loginId.trim().equals("")) {
			Thread dataThread = new SaveDataThread(loginId, data);
			dataThread.start();
		}
	}
	private class SaveDataThread extends Thread{
		private int m_data;
		private String m_loginId;
		public SaveDataThread(String m_loginId, int data){
			this.m_data = data;
			this.m_loginId = m_loginId;
		}
		public void run() {
			Log.e(TAG, "评价上传");
			AccountManager am = new AccountManager(EvaluationActivity.this);
			handler.sendEmptyMessage(m_data);
			if(am.postData(m_loginId, String.valueOf(m_data)))
				Log.e(TAG, "评价上传成功!");
			else {
				Evaluation eval = new Evaluation();
				//eval.setAccount(account);
				eval.setValue(String.valueOf(m_data));
				DatabaseManager.initializeInstance(EvaluationActivity.this);
				DatabaseManager dba = DatabaseManager.getInstance();
				dba.open();
				//User user = dba.findUserByAccount(account);
				//eval.setPassword(user.getPassword());
				eval.setLoginId(m_loginId);
				dba.insertEvaluation(eval);
				dba.close();
				Log.e(TAG, "评价信息上传失败，已保存到数据库。");
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				EvaluationActivity.this.finish();
			}
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			((MyApplication)getApplication()).setValue(msg.what);
			((MyApplication)getApplication()).setStatu(true);
			activityOver = true;
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
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tts == null && _boundService != null)
				tts = _boundService.getTts();
			while(tts != null && tts.isSpeaking()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			EvaluationActivity.this.finish();
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
	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		Log.e(TAG, "onStop");
		//activityOver = true;
		super.onStop();
	}
	@Override
	protected void onDestroy() {
//		tts.shutdown();
		Log.e(TAG, "onDestroy");
		unbindService();
		if(mBroadcastReceiver != null)
			this.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) {
			Log.e(TAG, "Text-To-Speech engine is initialized");
//			tts.speak("请对本次服务进行评价！", TextToSpeech.QUEUE_ADD, null);
		} else if (status == TextToSpeech.ERROR) {
			Log.e(TAG, "Error occurred while initializing Text-To-Speech engine");
		}
	}
	/** 跳转到“语音输入与输出”设置界面 */
	private boolean toTtsSettings() {
		try {
			startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("TIMEOUT")) {
	        	Log.e(TAG, "TIMEOUT");
	        	//Toast.makeText(EvaluationActivity.this, "HEART_BEAT", Toast.LENGTH_SHORT).show();
	        	EvaluationActivity.this.finish();
	        } else if(intent.getAction().equals("LEAVE_INFO")) {
	        	EvaluationActivity.this.finish();
	        }
		}
	};
	private ServiceConnection _connection = new ServiceConnection(){
		public void onServiceConnected(ComponentName className, IBinder Service){
			_boundService = ((HomeService.LocalBinder)Service).getService();
			_boundService.setWords("请对本次服务进行评价！");
			//Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
		}
		public void onServiceDisconnected(ComponentName className){
			//
			_boundService = null;
			//Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void startService(){
		Intent i = new Intent(this, HomeService.class);
		this.startService(i);
	}
	private void stopService(){
		Intent i = new Intent(this, HomeService.class);
		this.stopService(i);
	}
	private void bindService(){
		Log.e(TAG, "================>Main.bindService");
		Intent i = new Intent(this, HomeService.class);
		this.bindService(i, _connection, Context.BIND_AUTO_CREATE);
		_isBound = true;
	}
	private void unbindService(){
		Log.e(TAG, "================>Main.unbindService");
		if(_isBound){
			unbindService(_connection);
			_isBound = false;
		}
	}
}