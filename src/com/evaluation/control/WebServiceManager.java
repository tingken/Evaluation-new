package com.evaluation.control;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.evaluation.model.ComplaintResult;
import com.evaluation.model.DealResult;
import com.evaluation.model.LeaveMessage;

public class WebServiceManager {
	// 命名空间  
    private String nameSpace = "http://www.baidu.com";// = "http://125.71.200.138:8081";
    // EndPoint  
    private String URL = "";
    private String endPoint = "Oos/IWebServices/VocationalWorkServices.asmx";
    private String soapAction;
	private SoapObject rpc;
	private Context context;
	private String deviceKey;
	private String TAG = "effort";
	public WebServiceManager(Context context) {
		URL = AccountManager.getUrl();
		endPoint = URL + endPoint;
		this.context = context;
		deviceKey = this.getDeviceId();
	}
	public LeaveMessage getDeviceUserStatus() {
		init("GetDeviceUserStatus");
		// 设置需调用WebService接口需要传入的两个参数mobileCode、userId  
        rpc.addProperty("DeviceKey", deviceKey);
     // 获取返回的结果  
      SoapObject object = getRemoteInfo();
      if(object == null)
    	  return null;
      String result = object.getProperty("GetDeviceUserStatusResult").toString();
      Log.e(TAG, result);
      return getLeaveMessage(result);
	}
	public ComplaintResult addUserComplaintsPad(String name, String tel, String email, String content) {
		init("AddUserComplaintsPad");
		// 设置需调用WebService接口需要传入的两个参数mobileCode、userId  
		rpc.addProperty("ComplaintUser", name);
		rpc.addProperty("ComplaintPhone", tel);
		rpc.addProperty("ComplaintEmail", email);
		rpc.addProperty("ComplaintContent", content);
        rpc.addProperty("DeviceKey", deviceKey);
     // 获取返回的结果  
      SoapObject object = getRemoteInfo();
      String result = object.getProperty("AddUserComplaintsPadResult").toString();
      Log.e(TAG, result);
      return getComplaintResult(result);
	}
	public DealResult getComplaintsCheckNotice(String complaintsId) {
		init("GetComplaintsCheckNotice");
		// 设置需调用WebService接口需要传入的两个参数mobileCode、userId  
        rpc.addProperty("ComplaintsId", complaintsId);
     // 获取返回的结果  
      SoapObject object = getRemoteInfo();
      String result = object.getProperty("GetComplaintsCheckNoticeResult").toString();
      Log.e(TAG, result);
      return getDealResult(result);
	}
	public void init(String methodName) {
		  
        // 调用的方法名称  
        //String methodName = "GetDeviceUserStatus";  
          
        // SOAP Action  
        soapAction = nameSpace + "/" + methodName;  
  
        // 指定WebService的命名空间和调用的方法名  
        rpc = new SoapObject(nameSpace, methodName); 
	}
	public SoapObject getRemoteInfo() {  
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);  
  
        envelope.bodyOut = rpc;  
        // 设置是否调用的是dotNet开发的WebService  
        envelope.dotNet = true;  
        // 等价于envelope.bodyOut = rpc;  
        envelope.setOutputSoapObject(rpc);  
  
        HttpTransportSE transport = new HttpTransportSE(endPoint);  
        try {  
            // 调用WebService  
            transport.call(soapAction, envelope);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        // 获取返回的数据  
        SoapObject object = (SoapObject) envelope.bodyIn;  
        // 获取返回的结果  
//        String result = object.getProperty("GetDeviceUserStatusResult").toString();
//        return result;
        return object;
    }
	private LeaveMessage getLeaveMessage(String jsonString) {
		LeaveMessage lm = new LeaveMessage();
    	try {
			JSONObject jsonObject = new JSONObject(jsonString);
			lm.setStatus(jsonObject.getString("Status"));
			lm.setDescription(jsonObject.getString("Description"));
			lm.setStartDate(jsonObject.getString("sDate"));
			lm.setEndDate(jsonObject.getString("eDate"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	return lm;
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
	private DealResult getDealResult(String jsonString) {
		DealResult dr = new DealResult();
    	try {
			JSONObject jsonObject = new JSONObject(jsonString);
			dr.setStatus(jsonObject.getString("Status"));
			dr.setDescription(jsonObject.getString("Description"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	return dr;
    }
	public String getLocalMacAddress() {  
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        return info.getMacAddress();  
    }
	public String getAndroidId() {
		String androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
		return androidId;
	}
	public String getDeviceId() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
	}
}
