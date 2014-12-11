package com.evaluation.control;

import com.evaluation.view.MainActivity;
import com.evaluation.view.WelcomeActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {  
    public static final String TAG = "effort";  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
        	Intent i = new Intent(context, WelcomeActivity.class);
       	 	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);  
//        } else if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)) {
//        	Log.e(TAG, "ACTION_USB_ACCESSORY_DETACHED");
//        	//context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        } else if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)) {
//        	Log.e(TAG, "ACTION_USB_ACCESSORY_ATTACHED");
        }else {
        	Toast.makeText(context, "Received unexpected intent " + intent.toString(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Received unexpected intent " + intent.toString());  
        }  
    }  
}  
