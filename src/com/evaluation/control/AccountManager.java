package com.evaluation.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evaluation.dao.DatabaseAdapter;
import com.evaluation.model.Announcement;
import com.evaluation.model.Evaluation;
import com.evaluation.model.User;
import com.evaluation.util.FileManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AccountManager {
	private String url = "http://125.71.200.138:8081/";//外网IP
	//private String url = "http://10.0.205.11:8081/";//内网IP
	private User user;
	private DatabaseAdapter dba;
	private Context context;
	private String TAG = "effort";
	private String DISCONNECT = "DISCONNECT";
	private String WRONGPW = "WRONGPW";
	private SharedPreferences sp;
	private NetworkManager nm;

	public AccountManager(Context context) {
		this.context = context;
		nm = new NetworkManager();
		// 获得实例对象
		sp = context.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
	}
	public boolean isConnect() {
		boolean statu = nm.isConnect(url);
		nm.close();
		return statu;
	}
	// 登录后返回一个ID
	public String login(User user) {
		dba = new DatabaseAdapter(context);
		dba.open();
		if(!isConnect()){
			Log.e(TAG, "The network is not connect.");
			User existUser = dba.findUserByAccount(user.getAccount());
			if(existUser == null)
				return DISCONNECT;
			else if(existUser.getPassword().equals(user.getPassword())){
				dba.updateLoginTime(user.getAccount());
				return existUser.getLoginId();
			}
			else
				return WRONGPW;
		}
		this.user = user;
		String out = "";
		JSONObject jsonObject = null;
		String loginId = "";
		try {
			out = nm.executeGet(url + "GscSupport.svc/Login?username=" + user.getAccount() + "&password=" + user.getPassword());
			jsonObject = new JSONObject(out);
			loginId = jsonObject.getString("Id");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//Log.e(TAG, e1.getMessage());
			nm.close();
			return null;
		}
		addUserInfo(loginId);
		// 判断记住密码多选框的状态
		if (!sp.getBoolean("ISCHECK", false)) {
			// 设置默认是记录密码状态
			Log.e(TAG, "login");
			user.setPassword("");
		}
		dba.insertUserInfo(user);
		dba.deleteAnnouncementByAccount(user.getAccount());
		InputStream is = getPicture(user.getPhotoUrl());
		String absolutePath = context.getFilesDir().getAbsolutePath();
		AnnouncementManager am = new AnnouncementManager(absolutePath, context);
		//am.saveStream(is, user.getPhotoName(), AnnouncementManager.picWidth, AnnouncementManager.picHeight);
		am.inputstream2file(is, user.getPhotoName());
		List<Announcement> annos = getAnnouncements(loginId);
		for(Announcement anno : annos) {
			Log.e(TAG, anno.getImageUrl());
			anno.setAccount(user.getAccount());
			dba.insertAnnouncement(anno);
			InputStream ins = getPicture(anno.getImageUrl());
			//am.saveStream(is, user.getPhotoName(), AnnouncementManager.picWidth, AnnouncementManager.picHeight);
			am.inputstream2file(ins, anno.getImageName());
		}
		try {
			if(is != null)
				is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dba.close();
		nm.close();
		return loginId;
	}

	private User addUserInfo(String loginId) {
		String out = "";
		try {
			out = nm.executeGet(url + "GscSupport.svc/Officers/" + loginId);
			nm.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e(TAG, e1.getMessage());
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(out);
			user.setOperation(jsonObject.getString("operation"));
			user.setPhotoUrl(jsonObject.getString("picture").replaceAll("\\\\", "/"));
			user.setPhotoName(FileManager.getFileName(jsonObject.getString("picture")));
			user.setName(jsonObject.getString("showName"));
			user.setOrg(jsonObject.getString("unit"));
			user.setWorkNum(jsonObject.getString("workNum"));
			user.setLoginId(loginId);
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private List<Announcement> getAnnouncements(String loginId) {
		List<Announcement> anns = new ArrayList<Announcement>();
		String out = "";
		try {
			out = nm.executeGet(url + "GscSupport.svc/Notices?loginId=" + loginId);
			JSONArray jsonArray = new JSONArray(out);
	        for(int i = 0; i<jsonArray.length(); i++) {
	        	Announcement ann = new Announcement();
	            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
	            ann.setImageUrl(jsonObject.getString("picture").replaceAll("\\\\", "/"));
	            ann.setImageName(FileManager.getFileName(jsonObject.getString("picture")));
	            ann.setTitle(jsonObject.getString("title"));
	            ann.setContent(jsonObject.getString("content"));
				ann.setRepDate(jsonObject.getString("issueDate"));
				ann.setOutOfDate(jsonObject.getString("outOfServiceDate"));
	            anns.add(ann);
	        }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		nm.close();
		return anns;
	}
	private InputStream getPicture(String picUrl) {
		try {
			InputStream is = nm.executeGetStream(url + picUrl);
			return is;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public boolean postData(String loginId, String value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("loginId", loginId);
		map.put("score", value);
		String tmp = "?loginId=" + loginId + "&score=" + value;
		boolean statu = nm.postData(map, url + "GscSupport.svc/Evaluations" + tmp);
		nm.close();
		return statu;
	}
	public String getData(String key, String url){
		String out = "";
		JSONObject jsonObject = null;
		String data = "";
		try {
			out = nm.executeGet(this.url + url);
			jsonObject = new JSONObject(out);
			data = jsonObject.getString(key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			nm.close();
			return null;
		}
		return data;
	}
	public void deleteUserByAccount(String account) {
		dba = new DatabaseAdapter(context);
		dba.open();
		dba.deleteAccount(account);
		dba.deleteAnnouncementByAccount(account);
		dba.close();
	}
	public void close() {
		if(nm != null)
			nm.close();
		if(dba != null)
			dba.close();
	}
	public void saveEvaluation(Evaluation eval) {
		dba = new DatabaseAdapter(context);
		dba.open();
		dba.insertEvaluation(eval);
		dba.close();
	}
	public void sendEvaluation() {
		dba = new DatabaseAdapter(context);
		dba.open();
		List<Evaluation> evaList = dba.findAllEvaluation();
		if(evaList.size() < 1)
			return;
		for(Evaluation eva : evaList) {
			if(!isConnect()){
				return;
			}
			String out = "";
			JSONObject jsonObject = null;
			String loginId = "";
			try {
				out = nm.executeGet(url + "GscSupport.svc/Login?username=" + eva.getAccount() + "&password=" + eva.getPassword());
				Log.e(TAG, out);
				jsonObject = new JSONObject(out);
				loginId = jsonObject.getString("Id");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				//Log.e(TAG, e1.getMessage());
				nm.close();
				return;
			}
			if(postData(loginId, eva.getValue())) {
				dba.deleteEvaluationById(eva.getId());
				Log.e(TAG, "上传保存的评价成功");
			}
		}
		dba.close();
	}
	public void autoLogin(User user) {
		dba = new DatabaseAdapter(context);
		dba.open();
		dba.deleteAnnouncementByAccount(user.getAccount());
		InputStream is = getPicture(user.getPhotoUrl());
		String absolutePath = context.getFilesDir().getAbsolutePath();
		AnnouncementManager am = new AnnouncementManager(absolutePath, context);
		//am.saveStream(is, user.getPhotoName(), AnnouncementManager.picWidth, AnnouncementManager.picHeight);
		//am.inputstream2file(is, user.getPhotoName());
		List<Announcement> annos = getAnnouncements("");
		for(Announcement anno : annos) {
			Log.e(TAG, anno.getImageUrl());
			anno.setAccount(user.getAccount());
			dba.insertAnnouncement(anno);
			InputStream ins = getPicture(anno.getImageUrl());
			//am.saveStream(is, user.getPhotoName(), AnnouncementManager.picWidth, AnnouncementManager.picHeight);
			am.inputstream2file(ins, anno.getImageName());
		}
		try {
			if(is != null)
				is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dba.close();
		nm.close();
	}
}