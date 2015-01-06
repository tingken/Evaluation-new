package com.evaluation.dao;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evaluation.model.Announcement;
import com.evaluation.model.Evaluation;
import com.evaluation.model.User;
import com.evaluation.util.FileManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class DatabaseAdapter {
	private static final String TAG = "effort";
	public static final String KEY_ID = "ID";
	public static final String KEY_TITLE = "item";
	public static final String KEY_DATA = "data";
	private static final String DB_NAME = "Evaluation.db";
	private static final String USER_TABLE = "USER";
	private static final int USER_TABLE_MAX = 4;
	private static final String ANNOUNCE_TABLE = "ANNOUNCEMENT";
	private static final String ACCEPTBUIZ_TABLE = "ACCEPTBUIZ";
	private static final String EVALUATION_TABLE = "EVALUATION";
	private static final int DB_VERSION = 3;
	private Context mContext = null;
	private String[] userColumn = new String[] {"ACCOUNT", "PASSWORD", "LOGIN_ID", "NAME", "ORG", "WORKNO", "PHOTO_NAME", "PHOTO_URL", "OPERATION", "TIME"};
	private String[] annoColumn = {"ID", "USER_ACCOUNT", "IMAGE_NAME", "IMAGE_URL", "TITLE", "CONTENT", "ISSUE_DATE", "OUTOF_DATE"};
	private String[] evaluationColumn = {"ID", "EVALUATION", "USER_ACCOUNT", "PASSWORD"};
	private static final String CREATE_USER_TB = "CREATE TABLE "
												+ USER_TABLE
												+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
												+ "ACCOUNT TEXT,"
												+ "PASSWORD TEXT,"
												+ "LOGIN_ID TEXT,"
												+ "NAME TEXT,"
												+ "ORG TEXT,"
												+ "WORKNO TEXT,"
												+ "PHOTO_NAME TEXT,"
												+ "PHOTO_URL TEXT,"
												+ "OPERATION TEXT,"
												+ "TIME DATETIME)";
	private static final String CREATE_ANNOUNCE_TB = "CREATE TABLE "
												+ ANNOUNCE_TABLE + " (ID"
												+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
												+ "IMAGE_NAME TEXT,"
												+ "IMAGE_URL TEXT,"
												+ "TITLE TEXT,"
												+ "CONTENT TEXT,"
												+ "USER_ACCOUNT TEXT,"
												+ "ISSUE_DATE TEXT," 
												+ "OUTOF_DATE TEXT)";
	private static final String CREATE_ACCEPTBUIZ_TB = "CREATE TABLE "
												+ ACCEPTBUIZ_TABLE + " (ID"
												+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
												+ "BUIZ_NAME TEXT,"
												+ "USER_ACCOUNT TEXT)";
	private static final String CREATE_EVALUATION_TB = "CREATE TABLE "
												+ EVALUATION_TABLE + " (ID"
												+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
												+ "EVALUATION TEXT,"
												+ "USER_ACCOUNT TEXT,"
												+ "PASSWORD TEXT)";
	//
	private SQLiteDatabase mSQLiteDatabase = null;
	private DatabaseHelper mDatabaseHelper = null;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context){
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_ANNOUNCE_TB);
			db.execSQL(CREATE_USER_TB);
			db.execSQL(CREATE_ACCEPTBUIZ_TB);
			db.execSQL(CREATE_EVALUATION_TB);
//			db.execSQL("INSERT INTO " + USER_TABLE + " (ACCOUNT, PASSWORD, NAME, ORG, WINNO, PHOTO_NAME) VALUES ('admin', 'admin', '韩乾杰', '商务部', '15号', 'a.jpg')");
//			db.execSQL("INSERT INTO " + ANNOUNCE_TABLE + " (USER_ACCOUNT, FILE_NAME, CONTENT) VALUES ('admin', 'a.jpg', '公告栏测试1')");
//			db.execSQL("INSERT INTO " + ANNOUNCE_TABLE + " (USER_ACCOUNT, FILE_NAME, CONTENT) VALUES ('admin', 'b.jpg', '公告栏测试2')");
//			db.execSQL("INSERT INTO " + ANNOUNCE_TABLE + " (USER_ACCOUNT, FILE_NAME, CONTENT) VALUES ('admin', 'c.jpg', '公告栏测试3')");
//			db.execSQL("INSERT INTO " + ANNOUNCE_TABLE + " (USER_ACCOUNT, FILE_NAME) VALUES ('admin', 'd.jpg')");
//			db.execSQL("INSERT INTO " + ACCEPTBUIZ_TABLE + " (USER_ACCOUNT, BUIZ_NAME) VALUES ('admin', '异地退休人员资格核查')");
//			db.execSQL("INSERT INTO " + ACCEPTBUIZ_TABLE + " (USER_ACCOUNT, BUIZ_NAME) VALUES ('admin', '部分退休人员档案查询')");
//			db.execSQL("INSERT INTO " + ACCEPTBUIZ_TABLE + " (USER_ACCOUNT, BUIZ_NAME) VALUES ('admin', '城乡居民养老保险查询')");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + ANNOUNCE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + ACCEPTBUIZ_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + EVALUATION_TABLE);
			onCreate(db);
		}
	}
	public DatabaseAdapter(Context context) {
		mContext = context;
	}
	public void open() throws SQLException{
		mDatabaseHelper = new DatabaseHelper(mContext);
		Log.e(TAG, "DatabaseAdapter.open");
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}
	public void close(){
		mDatabaseHelper.close();
	}
	public long insertUserInfo(User user) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("PASSWORD", user.getPassword());
		initialValues.put("LOGIN_ID", user.getLoginId());
		initialValues.put("NAME", user.getName());
		initialValues.put("ORG", user.getOrg());
		initialValues.put("WORKNO", user.getWorkNum());
		initialValues.put("PHOTO_NAME", user.getPhotoName());
		initialValues.put("PHOTO_URL", user.getPhotoUrl());
		initialValues.put("OPERATION", user.getOperation());
		initialValues.put("TIME", new Date().toString());
		if(findUserByAccount(user.getAccount()) != null) {
			FileManager.delete(user.getPhotoName());
			return mSQLiteDatabase.update(USER_TABLE, initialValues, "ACCOUNT=\'" + user.getAccount() + "\'", null);
		}else {
			//ACCOUNT, PASSWORD, NAME, ORG, WINNO, PHOTO_NAME
			if(findAllUser() != null && findAllUser().size() >= USER_TABLE_MAX){
				//mSQLiteDatabase.delete(USER_TABLE, "ID = min(ID)", null);
				this.deleteAnnouncementByAccount(this.findOldestUser().getAccount());
				this.deleteAccount(this.findOldestUser().getAccount());
			}
			initialValues.put("ACCOUNT", user.getAccount());
			return mSQLiteDatabase.insert(USER_TABLE, KEY_ID, initialValues);
		}
	}
	public int updateLoginTime(String account) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("TIME", new Date().toString());
		return mSQLiteDatabase.update(USER_TABLE, initialValues, "ACCOUNT=\'" + account + "\'", null);
	}
	public long insertAnnouncement(Announcement anno) {
		//USER_ACCOUNT, FILE_NAME, TITLE
		ContentValues initialValues = new ContentValues();
		initialValues.put("USER_ACCOUNT", anno.getAccount());
		initialValues.put("IMAGE_NAME", anno.getImageName());
		initialValues.put("IMAGE_URL", anno.getImageUrl());
		initialValues.put("TITLE", anno.getTitle());
		initialValues.put("CONTENT", anno.getContent());
		initialValues.put("ISSUE_DATE", anno.getRepDate());
		initialValues.put("OUTOF_DATE", anno.getOutOfDate());
		return mSQLiteDatabase.insert(ANNOUNCE_TABLE, KEY_ID, initialValues);
	}
	public long insertEvaluation(Evaluation eval) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("USER_ACCOUNT", eval.getAccount());
		initialValues.put("EVALUATION", eval.getValue());
		initialValues.put("PASSWORD", eval.getPassword());
		return mSQLiteDatabase.insert(EVALUATION_TABLE, KEY_ID, initialValues);
	}
	public List<Evaluation> findAllEvaluation() {
		List<Evaluation> evaList = new ArrayList<Evaluation>();
		Cursor mCursor = mSQLiteDatabase.query(false, EVALUATION_TABLE, evaluationColumn, null, null, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		while(!mCursor.isAfterLast()) {
			Map<String, String> map = new HashMap<String, String>();
			for(String column : evaluationColumn) {
				int columnIndex = mCursor.getColumnIndexOrThrow(column);
				String value;
				try{
					value = mCursor.getString(columnIndex);
				}catch(Exception e)
				{
					value = null;
				}
				map.put(column, value);
			}
			Evaluation eva = new Evaluation();
			eva.setId(Integer.parseInt(map.get("ID")));
			eva.setAccount(map.get("USER_ACCOUNT"));
			eva.setValue(map.get("EVALUATION"));
			eva.setPassword(map.get("PASSWORD"));
			evaList.add(eva);
			mCursor.moveToNext();
		}
		return evaList;
	}
	public boolean deleteEvaluationById(int id) {
		return mSQLiteDatabase.delete(EVALUATION_TABLE, "ID=" + id, null) > 0;
	}
	public boolean deleteAccount(String account){
		return mSQLiteDatabase.delete(USER_TABLE, "ACCOUNT=\'" + account + "\'", null) > 0;
	}
	public boolean deleteAnnouncementByAccount(String account){
		List<Announcement> anns = findAnnouncementsByAccount(account);
		for(Announcement ann : anns) {
			FileManager.delete(ann.getImageName());
		}
		return mSQLiteDatabase.delete(ANNOUNCE_TABLE, "USER_ACCOUNT=\'" + account +"\'", null) > 0;
	}
	public User findUserByAccount(String account) {
		//ACCOUNT, PASSWORD, NAME, ORG, WINNO, PHOTO_NAME
		Cursor mCursor = null;
		User user = new User();
		mCursor = mSQLiteDatabase.query(false, USER_TABLE, userColumn, "ACCOUNT" + "=" + "\'" + account + "\'", null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		Log.e(TAG, "count: " + String.valueOf(mCursor.getCount()));
		if(mCursor.getCount() > 1){
			Log.e(TAG, "There are more than 1 user use this account.");
			return null;
		}
		if(mCursor.getCount() <= 0)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for(String column : userColumn) {
			int columnIndex = mCursor.getColumnIndexOrThrow(column);
			String value;
			try{
				value = mCursor.getString(columnIndex);
			}catch(Exception e)
			{
				value = null;
			}
			map.put(column, value);
		}
		user.setAccount(map.get("ACCOUNT"));
		user.setName(map.get("NAME"));
		user.setLoginId(map.get("LOGIN_ID"));
		user.setOrg(map.get("ORG"));
		user.setPassword(map.get("PASSWORD"));
		user.setPhotoName(map.get("PHOTO_NAME"));
		user.setPhotoUrl(map.get("PHOTO_URL"));
		user.setWorkNum(map.get("WORKNO"));
		user.setOperation(map.get("OPERATION"));
		Log.e(TAG, "数据库时间" + map.get("TIME"));
		return user;
	}
	public List<Announcement> findAnnouncementsByAccount(String account) {
		Cursor mCursor = null;
		List<Announcement> annoList = new ArrayList<Announcement>();
		mCursor = mSQLiteDatabase.query(false, ANNOUNCE_TABLE, annoColumn, "USER_ACCOUNT" + "=" + "\'" + account + "\'", null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		while(!mCursor.isAfterLast()) {
			Map<String, String> map = new HashMap<String, String>();
			for(String column : annoColumn) {
				int columnIndex = mCursor.getColumnIndexOrThrow(column);
				String value;
				try{
					value = mCursor.getString(columnIndex);
				}catch(Exception e)
				{
					value = null;
				}
				map.put(column, value);
			}
			mapList.add(map);
			mCursor.moveToNext();
		}
		for(Map<String, String> map : mapList) {
			//USER_ACCOUNT, FILE_NAME, TITLE
			Announcement anno = new Announcement();
			anno.setId(Integer.parseInt(map.get("ID")));
			anno.setAccount(map.get("USER_ACCOUNT"));
			anno.setImageName(map.get("IMAGE_NAME"));
			anno.setImageUrl(map.get("IMAGE_URL"));
			anno.setTitle(map.get("TITLE"));
			anno.setContent(map.get("CONTENT"));
			anno.setRepDate(map.get("ISSUE_DATE"));
			anno.setOutOfDate(map.get("OUTOF_DATE"));
			annoList.add(anno);
		}
		return annoList;
	}
	
	public Announcement findAnnouncementsById(int id) {
		//ACCOUNT, PASSWORD, NAME, ORG, WINNO, PHOTO_NAME
		Cursor mCursor = null;
		Announcement anno = new Announcement();
		mCursor = mSQLiteDatabase.query(false, ANNOUNCE_TABLE, annoColumn, "ID" + "=" + id, null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		Log.e(TAG, "count: " + String.valueOf(mCursor.getCount()));
		if(mCursor.getCount() > 1){
			Log.e(TAG, "There are more than 1 user use this account.");
			return null;
		}
		if(mCursor.getCount() <= 0)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for(String column : annoColumn) {
			int columnIndex = mCursor.getColumnIndexOrThrow(column);
			String value;
			try{
				value = mCursor.getString(columnIndex);
			}catch(Exception e)
			{
				value = null;
			}
			map.put(column, value);
		}
		anno.setId(Integer.parseInt(map.get("ID")));
		anno.setAccount(map.get("USER_ACCOUNT"));
		anno.setImageName(map.get("IMAGE_NAME"));
		anno.setImageUrl(map.get("IMAGE_URL"));
		anno.setTitle(map.get("TITLE"));
		anno.setContent(map.get("CONTENT"));
		anno.setRepDate(map.get("ISSUE_DATE"));
		anno.setOutOfDate(map.get("OUTOF_DATE"));
		Log.e(TAG, "数据库时间" + map.get("TIME"));
		return anno;
	}
	
	public List<Announcement> findOnePageAnno(String account, int start, int size) {
		Cursor mCursor = null;
		List<Announcement> annoList = new ArrayList<Announcement>();
		//mCursor = mSQLiteDatabase.query(false, ANNOUNCE_TABLE, annoColumn, "USER_ACCOUNT" + "=" + "\'" + account + "\'", null, null, null, null, " " + size + " offset " + start);
		String sql = "select * from " + ANNOUNCE_TABLE + " where USER_ACCOUNT=\'" + account + "\' limit " + size + " offset " + start;
		Log.e(TAG, sql);
		mCursor = mSQLiteDatabase.rawQuery(sql, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		while(!mCursor.isAfterLast()) {
			Map<String, String> map = new HashMap<String, String>();
			for(String column : annoColumn) {
				int columnIndex = mCursor.getColumnIndexOrThrow(column);
				String value;
				try{
					value = mCursor.getString(columnIndex);
				}catch(Exception e)
				{
					value = null;
				}
				map.put(column, value);
			}
			mapList.add(map);
			mCursor.moveToNext();
		}
		for(Map<String, String> map : mapList) {
			//USER_ACCOUNT, FILE_NAME, TITLE
			Announcement anno = new Announcement();
			anno.setId(Integer.parseInt(map.get("ID")));
			anno.setAccount(map.get("USER_ACCOUNT"));
			anno.setImageName(map.get("IMAGE_NAME"));
			anno.setImageUrl(map.get("IMAGE_URL"));
			anno.setTitle(map.get("TITLE"));
			anno.setContent(map.get("CONTENT"));
			anno.setRepDate(map.get("ISSUE_DATE"));
			anno.setOutOfDate(map.get("OUTOF_DATE"));
			annoList.add(anno);
		}
		return annoList;
	}
	public List<User> findAllUser() throws SQLException{
		Cursor mCursor = null;
		List<User> userList = new ArrayList<User>();
		mCursor = mSQLiteDatabase.query(false, USER_TABLE, userColumn, null, null, null, null, "TIME desc", null);
		if(mCursor.getCount() <= 0)
			return null;
		mCursor.moveToFirst();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		while(!mCursor.isAfterLast()) {
			Map<String, String> map = new HashMap<String, String>();
			for(String column : userColumn) {
				int columnIndex = mCursor.getColumnIndexOrThrow(column);
				String value;
				try{
					value = mCursor.getString(columnIndex);
				}catch(Exception e)
				{
					value = null;
				}
				map.put(column, value);
			}
			mapList.add(map);
			mCursor.moveToNext();
		}
		for(Map<String, String> map : mapList) {
			User user = new User();
			user.setAccount(map.get("ACCOUNT"));
			user.setName(map.get("NAME"));
			user.setLoginId(map.get("LOGIN_ID"));
			user.setOrg(map.get("ORG"));
			user.setPassword(map.get("PASSWORD"));
			user.setPhotoName(map.get("PHOTO_NAME"));
			user.setPhotoUrl(map.get("PHOTO_URL"));
			user.setWorkNum(map.get("WORKNO"));
			user.setOperation(map.get("OPERATION"));
			userList.add(user);
		}
		return userList;
	}
	
	public User findLatestUser() {
		Cursor mCursor = null;
		User user = new User();
		mCursor = mSQLiteDatabase.query(false, USER_TABLE, userColumn, null, null, null, null, "TIME DESC", null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		Log.e(TAG, "count: " + String.valueOf(mCursor.getCount()));
		if(mCursor.getCount() <= 0)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for(String column : userColumn) {
			int columnIndex = mCursor.getColumnIndexOrThrow(column);
			String value;
			try{
				value = mCursor.getString(columnIndex);
			}catch(Exception e)
			{
				value = null;
			}
			map.put(column, value);
		}
		user.setAccount(map.get("ACCOUNT"));
		user.setName(map.get("NAME"));
		user.setLoginId(map.get("LOGIN_ID"));
		user.setOrg(map.get("ORG"));
		user.setPassword(map.get("PASSWORD"));
		user.setPhotoName(map.get("PHOTO_NAME"));
		user.setPhotoUrl(map.get("PHOTO_URL"));
		user.setWorkNum(map.get("WORKNO"));
		user.setOperation(map.get("OPERATION"));
		Log.e(TAG, "数据库时间" + map.get("TIME"));
		return user;
	}
	public User findOldestUser() {
		Cursor mCursor = null;
		User user = new User();
		mCursor = mSQLiteDatabase.query(false, USER_TABLE, userColumn, null, null, null, null, "TIME ASC", null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		Log.e(TAG, "count: " + String.valueOf(mCursor.getCount()));
		if(mCursor.getCount() <= 0)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for(String column : userColumn) {
			int columnIndex = mCursor.getColumnIndexOrThrow(column);
			String value;
			try{
				value = mCursor.getString(columnIndex);
			}catch(Exception e)
			{
				value = null;
			}
			map.put(column, value);
		}
		user.setAccount(map.get("ACCOUNT"));
		user.setName(map.get("NAME"));
		user.setLoginId(map.get("LOGIN_ID"));
		user.setOrg(map.get("ORG"));
		user.setPassword(map.get("PASSWORD"));
		user.setPhotoName(map.get("PHOTO_NAME"));
		user.setPhotoUrl(map.get("PHOTO_URL"));
		user.setWorkNum(map.get("WORKNO"));
		user.setOperation(map.get("OPERATION"));
		Log.e(TAG, "数据库时间" + map.get("TIME"));
		return user;
	}
	
	public boolean deleteByAccount(String account) {
		return false;
	}
//	public long insertData(String title, String data){
//		ContentValues initialValues = new ContentValues();
//		initialValues.put(KEY_TITLE, title);
//		initialValues.put(KEY_DATA, data);
//		return mSQLiteDatabase.insert(ANNOUNCE_TABLE, KEY_ID, initialValues);
//	}
//	public boolean deleteData(long rowId){
//		return mSQLiteDatabase.delete(ANNOUNCE_TABLE, KEY_ID + "=" + rowId, null) > 0;
//	}
	
	public Cursor fetchAllData(){
		try{
			return mSQLiteDatabase.query(ANNOUNCE_TABLE, new String[]{KEY_ID, KEY_TITLE, KEY_DATA}, null, null, null, null, null);
		}catch(Exception e){
			return null;
		}
	}
	public Cursor fetchData(long rowId) throws SQLException{
		Cursor mCursor = mSQLiteDatabase.query(true, ANNOUNCE_TABLE, new String[] {KEY_ID, KEY_TITLE, KEY_DATA}, KEY_ID + "=" + rowId, null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	public Cursor fetchData(String title) throws SQLException{
		Log.e(TAG, "DatabaseAdapter.fetchData");
		Cursor mCursor = null;
		mCursor = mSQLiteDatabase.query(false, ANNOUNCE_TABLE, new String[] {KEY_ID, KEY_TITLE, KEY_DATA}, KEY_TITLE + "=" + "\'" + title + "\'", null, null, null, null, null);
		Log.e(TAG, "ok!" + mCursor.getCount());
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public int getCount(String title) throws SQLException{
		Cursor mCursor = null;
		mCursor = mSQLiteDatabase.query(false, ANNOUNCE_TABLE, new String[] {KEY_ID, KEY_TITLE, KEY_DATA}, KEY_TITLE + "=" + "\'" + title + "\'", null, null, null, null, null);
		return mCursor.getCount();
	}
	public String getValue(String title){
		Cursor mCursor = null;
		mCursor = mSQLiteDatabase.query(false, ANNOUNCE_TABLE, new String[] {KEY_ID, KEY_TITLE, KEY_DATA}, KEY_TITLE + "=" + "\'" + title + "\'", null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		if(mCursor.getCount() > 1){
			Log.e(TAG, "DatabaseAdapter.getValue");
			return null;
		}
		int columnIndex = mCursor.getColumnIndexOrThrow(KEY_DATA);
		String value;
		try{
			value = mCursor.getString(columnIndex);
		}catch(Exception e)
		{
			value = null;
		}
		return value;
	}
	
	public boolean updateData(long rowId, String title, String data){
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DATA, data);
		return mSQLiteDatabase.update(ANNOUNCE_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}
	
	public boolean updateData(String title, String data){
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DATA, data);
		return mSQLiteDatabase.update(ANNOUNCE_TABLE, args, KEY_TITLE + "=" + "\'" + title + "\'", null) > 0;
	}
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
        'a', 'b', 'c', 'd', 'e', 'f' };  
	public static String toHexString(byte[] b) {  
		// String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}