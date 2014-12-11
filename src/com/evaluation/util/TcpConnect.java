package com.evaluation.util;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.util.ArrayList;
import java.util.List;

import com.evaluation.view.MyApplication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class TcpConnect extends Thread {
	    private final int SERVER_PORT = 22222;  
	    private ServerSocket mServerSocket;  
	    private Socket mClient;  
	    private boolean iRunFlag = true;
	    private MyApplication context;
	    private List<SocketThread> threadList = new ArrayList<SocketThread>();
	    private String TAG = "effort";
	public TcpConnect(MyApplication context) {
		this.context = context;
	}
	public void Close()
	{
		for(SocketThread thread : threadList){
			thread.close();
		}
		iRunFlag = false;
		//this.interrupt();
	}
	public void setNeedEvaluation(boolean statu) {
		for(SocketThread thread : threadList){
			thread.setNeedEvaluation(statu);
		}
	}
	public boolean isRunning()
	{
		return iRunFlag;
	}
//	public List<Handler> getHandlers() {
//		List<Handler> handlerList = new ArrayList<Handler>();
//		for(SocketThread thread : threadList){
//			handlerList.add(thread.getHandler());
//		}
//		return handlerList;
//	}
	public void run() {
		
		 try {              
	            String ip = InetAddress.getLocalHost().getHostAddress();    
	            Log.e(TAG, "ip地址是: " + ip);
	            //System.out.println(aDeviceId + "   型号: " + aDeviceType);  
	            mServerSocket = new ServerSocket(SERVER_PORT);  
	            Log.e(TAG, "TcpConnect" + "建立Socket");
	        //  listen();  
	              
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            //e.printStackTrace();  
	            Log.e(TAG, "TcpConnect:" + e.getMessage());
	            return;
	        }         
	            
	        Log.e(TAG, "TcpConnect" + "开始监听");
	        while(iRunFlag){  
	        try{                       
				mClient = mServerSocket.accept();
	            Log.e("effort","TcpConnect" + "检测到有连接");
	            SocketThread thread = new SocketThread(this, mClient, context);
	            thread.start();
	            threadList.add(thread);
	        }   
	        catch(Exception e)
	        {   
	        	iRunFlag = false;
	        	Log.e("effort", "TCP error"); 
	        }   
	    }
            if(mServerSocket != null)
            {  
                try 
                {  
                    mServerSocket.close();  
                } catch (IOException e) 
                {  
                    // TODO Auto-generated catch block  
                    //System.out.println("TcpConnect" + e.getMessage());  
                }  
            }
		Log.e("effort", "thread run end");
	}
	
	public void removeSocketThread(SocketThread thread){
		threadList.remove(thread);
	}
}