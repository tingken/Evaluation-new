package com.evaluation.service;

import com.evaluation.control.AccountManager;
import com.evaluation.dao.DatabaseAdapter;
import com.evaluation.util.TcpConnect;
import com.evaluation.view.*;

import android.os.BatteryManager;
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
	private TcpConnect iTCPConnect = null;
	//public DatabaseAdapter mDataAdapter;
	private final IBinder mBinder = new LocalBinder();
	public boolean serviceOver = false;
	private int value;
//	private TcpConnect iTCPConnect = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e(TAG, "============>HomeService.onBind");
		return mBinder;
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
    	registerReceiver(homePressReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    	iTCPConnect = new TcpConnect((MyApplication)this.getApplication());
        iTCPConnect.start();
//        Thread thread = new UnConnectThread();
//    	thread.start();
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
		iTCPConnect.Close();
		//Intent i = new Intent("RESTART_ACTIVITY");
		//i.addFlags(Intent.flag);
		//sendBroadcast(i);//传递过去
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
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				Intent i = new Intent("TIMEOUT");
				int status = intent.getIntExtra("status", 0);
				 switch (status) {
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					break;
				case BatteryManager.BATTERY_STATUS_CHARGING:
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					HomeService.this.sendBroadcast(i);
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					HomeService.this.sendBroadcast(i);
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					break;
				}
			}
		}
	};
	private class UnConnectThread extends Thread {
		public void run() {
			while(!serviceOver){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int count = iTCPConnect.getCurrentLinkedSocketThreadNum();
				Log.e(TAG, "定时检测socket连接数: " + count);
				if(count < 1) {
					Intent intent = new Intent("TIMEOUT");
					HomeService.this.sendBroadcast(intent);
				}
			}
		}
	}
}