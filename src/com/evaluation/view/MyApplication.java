package com.evaluation.view;

import java.util.LinkedList;
import java.util.List;

import com.evaluation.util.CrashHandler;
import com.evaluation.util.TcpConnect;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyApplication extends Application {
	private List<Activity> mList = new LinkedList<Activity>(); 
//	private TcpConnect iTCPConnect = null;
	private int value = 0;
	private boolean statu = false;
	private boolean evaluatable = false;
	private String account = "";
	private String loginId = "";
	private String TAG = "effort";
	
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "myApplication.onCreate");
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(this);
//		iTCPConnect = new TcpConnect(this);
//        iTCPConnect.start();
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
//		SendThread sendThread = new SendThread();
//		sendThread.start();
	}
	public boolean isStatu() {
		return statu;
	}
	public void setStatu(boolean statu) {
		this.statu = statu;
	}
	public boolean isEvaluatable() {
		return evaluatable;
	}
	public void setEvaluatable(boolean evaluatable) {
		this.evaluatable = evaluatable;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	// add Activity  
    public void addActivity(Activity activity) { 
        mList.add(activity);
    }
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null) 
                    activity.finish();
            }
//            if(iTCPConnect != null)
//            	iTCPConnect.Close();
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0);
        }
    }
    public int getActivityCount(){
    	int count = 0;
    	for (Activity activity : mList) { 
            if (activity != null)
            	count++;
        }
    	return count;
    }
    public void onLowMemory() {
    	Log.e(TAG, "MyApplication.onLowMemory");
        super.onLowMemory(); 
        System.gc(); 
    }
//	public class SendThread extends Thread {
//		public void run() {
//			List<Handler> handlerList = iTCPConnect.getHandlers();
//			if(handlerList.size() > 0){
//				for(Handler handler : handlerList) {
//					if(handler != null) {
//						Log.e(TAG, "向PC端发送评价。");
//						handler.sendEmptyMessage(value);
//					}
//				}
//			}
//		}
//	}
}
