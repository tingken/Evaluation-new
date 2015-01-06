package com.evaluation.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class EmptyActivity extends Activity {
	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;
	private String TAG = "effort";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);
		powerManager = (PowerManager)(getSystemService(Context.POWER_SERVICE));   
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, TAG);  
        wakeLock.acquire();
		WindowManager.LayoutParams params = getWindow().getAttributes();
    	params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
    	params.screenBrightness = 0;
    	getWindow().setAttributes(params);
    	
    	registerReceiver(mBroadcastReceiver, new IntentFilter("HEART_BEAT"));
    	registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("HEART_BEAT")) {
	        	Log.e(TAG, "HEART_BEAT");
	        	//Toast.makeText(EmptyActivity.this, "HEART_BEAT", Toast.LENGTH_SHORT).show();
	        	EmptyActivity.this.finish();
	        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	        	Log.e(TAG, "ACTION_SCREEN_OFF");
	        	EmptyActivity.this.finish();
	        }
		}
	};
	@Override
	protected void onDestroy() {
		if(mBroadcastReceiver != null)
			this.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
}
