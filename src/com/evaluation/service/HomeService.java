package com.evaluation.service;

import com.evaluation.control.AccountManager;
import com.evaluation.dao.DatabaseAdapter;
import com.evaluation.util.TcpConnect;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class HomeService extends Service{
	/**
	 * 
	 */
	private static final String TAG = "effort";
	//private TcpConnect iTCPConnect = null;
	//public DatabaseAdapter mDataAdapter;
	public boolean serviceOver = false;
	private int value;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e(TAG, "============>HomeService.onBind");
		return null;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public class LocalBinder extends Binder{
		public HomeService getService(){
			return HomeService.this;
		}
	}
	public boolean onUnbind(Intent i){
		Log.e(TAG, "===========>HomeService.onUnbind");
		return false;
	}
	public void onRebind(Intent i){
		Log.e(TAG, "===========>HomeService.onRebind");
	}
	public void onCreate(){
        
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    	registerReceiver(homePressReceiver, homeFilter);
    	Thread thread = new SendEvaluationThread();
    	thread.start();
	}

	public int onStartCommand(Intent intent,int flags, int startId){
		Log.e(TAG, "============>HomeService.onStartCommand");
		//iTCPConnect = new TcpConnect(this);
        //iTCPConnect.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onDestroy(){
		if (homePressReceiver != null) {
			try {
				unregisterReceiver(homePressReceiver);
			} catch (Exception e) {
				Log.e(TAG,
						"unregisterReceiver homePressReceiver failure :"
								+ e.getCause());
			}
		}
		Log.e(TAG, "onStop");
		serviceOver = true;
		//Intent i = new Intent("RESTART_ACTIVITY");
		//i.addFlags(Intent.flag);
		//sendBroadcast(i);//传递过去
		//iTCPConnect.Close();
		super.onDestroy();
	}
	
	private final BroadcastReceiver homePressReceiver = new BroadcastReceiver() {
		final String SYSTEM_DIALOG_REASON_KEY = "reason";
		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//				if (reason != null
//				&& reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//					// 自己随意控制程序，写自己想要的结果
//					Log.i(TAG, "home_press");
//					Intent i = new Intent("RESTART_ACTIVITY");
//					sendBroadcast(i);//传递过去
//				}
			}
		}
	};
	private class SendEvaluationThread extends Thread {
		public void run() {
			
			while(!serviceOver){
				try {
					Thread.sleep(600 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e(TAG, "定时上传保存的未上传评价");
				AccountManager am = new AccountManager(HomeService.this);
				am.sendEvaluation();
				am.close();
			}
		}
	}
}