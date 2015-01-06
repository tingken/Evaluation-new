package com.evaluation.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.evaluation.control.WebServiceManager;
import com.evaluation.model.ComplaintResult;
import com.evaluation.model.DealResult;
import com.evaluation.service.ComplaintService;
import com.evaluation.view.*;

public class ComplaintDialog extends Dialog {
	private static int default_width = 700; //默认宽度
    private static int default_height = 500;//默认高度
    private EditText content;
    private Button submit;
    private Button cancel;
	private Context context;
	private WebServiceManager wsm;
	private ComplaintResult complaintResult;
	private DealResult dealResult;
//	private boolean _isBound;
//	private ComplaintService _boundService;
	private String TAG = "effort";
	
    public ComplaintDialog(Context context, int layout, int style) {
        this(context, default_width, default_height, layout, style); 
    }

    public ComplaintDialog(Context context, int width, int height, int layout, int style) {
    	super(context, style);
        //set content
    	this.context = context;
    	setContentView(layout);
        //set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        float density = getDensity(context);
        params.width = (int) (width*density);
        params.height = (int) (height*density);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setTitle("投诉");
        content = (EditText)findViewById(R.id.content);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(submitClickListener);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelClickListener);
        
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.complaint_dialog);
            //设置标题
            
    }
    public void show() {
    	content.setText("");
    	super.show();
    }
    private float getDensity(Context context) {
    	Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }
    private View.OnClickListener submitClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	String contentText = content.getText().toString();
        	if(!contentText.equals("")) {
        		sendComplaint("", "", "", contentText);
        	}else
        		return;
        	ComplaintDialog.this.dismiss();
        }
    };
    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	ComplaintDialog.this.dismiss();
            //Toast.makeText(context, deviceId, Toast.LENGTH_LONG).show();
        }
    };
    public void sendComplaint(String name, String tel, String email, String content){
    	Thread sendComplaintThread = new SendComplaintThread(name, tel, email, content);
    	sendComplaintThread.start();
    }
	private void dealComplaintResult() {
		if(complaintResult.getStatus().equals("1")) {
        	Intent intent = new Intent("COMPLAINT_SUCCESS");
			context.sendBroadcast(intent);
			getResponse(complaintResult.getKey());
		}else{
			Intent intent = new Intent("COMPLAINT_FAIL");
			context.sendBroadcast(intent);
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
			wsm = new WebServiceManager(context);
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
					context.sendBroadcast(intent);
				}
	        	
			}
		}
	}
//    private ServiceConnection _connection = new ServiceConnection(){
//		public void onServiceConnected(ComponentName className, IBinder Service){
//			_boundService = ((ComplaintService.LocalBinder)Service).getService();
//			//Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
//		}
//		public void onServiceDisconnected(ComponentName className){
//			//
//			_boundService = null;
//			//Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
//		}
//	};
//	
//	private void startService(){
//		Intent i = new Intent(context, ComplaintService.class);
//		context.startService(i);
//	}
//	private void stopService(){
//		Intent i = new Intent(context, ComplaintService.class);
//		context.stopService(i);
//	}
//	private void bindService(){
//		Log.e(TAG, "================>Main.bindService");
//		Intent i = new Intent(context, ComplaintService.class);
//		context.bindService(i, _connection, Context.BIND_AUTO_CREATE);
//		_isBound = true;
//	}
//	private void unbindService(){
//		Log.e(TAG, "================>Main.unbindService");
//		if(_isBound){
//			context.unbindService(_connection);
//			_isBound = false;
//		}
//	}
}
