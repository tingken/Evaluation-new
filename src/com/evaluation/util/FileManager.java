package com.evaluation.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;
import android.util.Log;

public class FileManager {
	private static String TAG = "effort";
	public static boolean delete(String fileName) {
		File file = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
		if(file.isDirectory()) {
			Log.e(TAG, "It's a direcotory path,please input a file path");
			return false;
		}
		if(file.exists())
			file.delete();
		return true;
	}
	public static boolean saveFile(String absolutePath, InputStream is) {
		File file = new File(absolutePath);
		if(file.isDirectory()) {
			return false;
		}
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			
			byte[] by = new byte[1024];
			int c;
			while((c = is.read(by)) != -1){
				out.write(by, 0, c);
			}
			out.close();
			is.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static String getFileName(String str) {
		if(str == null || str.trim().equals("")){
			return null;
		}
		int index = str.lastIndexOf("/") > str.lastIndexOf("\\") ? str.lastIndexOf("/") : str.lastIndexOf("\\");
		return str.substring(index+1);
	}
}
