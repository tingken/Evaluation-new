package com.evaluation.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.evaluation.control.WebServiceManager;
import com.evaluation.model.ComplaintResult;
import com.evaluation.model.DealResult;
import com.evaluation.service.HomeService.LocalBinder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ComplaintService extends Service{
	private final IBinder mBinder = new LocalBinder();
	private WebServiceManager wsm;
	private ComplaintResult complaintResult;
	private String deviceId;
	private static final String TAG = "effort";
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	public class LocalBinder extends Binder{
		public ComplaintService getService(){
			return ComplaintService.this;
		}
	}
	public void onCreate(){
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();
        Log.e(TAG, deviceId);
	}
	public void sendComplaint(String name, String tel, String email, String content){
    	Thread sendComplaintThread = new SendComplaintThread(name, tel, email, content);
    	sendComplaintThread.start();
    }
	private void dealComplaintResult() {
		if(complaintResult.getStatus().equals("1")) {
        	Intent intent = new Intent("COMPLAINT_SUCCESS");
			sendBroadcast(intent);
			getResponse();
		}else{
			Intent intent = new Intent("COMPLAINT_FAIL");
			sendBroadcast(intent);
		}
	}
	private void getResponse() {
		Thread getResponseThread = new GetResponseThread();
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
			wsm = new WebServiceManager(ComplaintService.this);
			complaintResult = wsm.addUserComplaintsPad(name, tel, email, content);
        	dealComplaintResult();
		}
	}
	private class GetResponseThread extends Thread{
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
				DealResult dealResult = wsm.getComplaintsCheckNotice(deviceId);
				if(dealResult.getStatus().equals("1")){
					Intent intent = new Intent("COMPLAINT_RESULT");
					intent.putExtra("description", dealResult.getDescription());
					sendBroadcast(intent);
				}
	        	
			}
		}
	}
}
