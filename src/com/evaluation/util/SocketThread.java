package com.evaluation.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.evaluation.view.EvaluationActivity;
import com.evaluation.view.MyApplication;
import com.evaluationo.protocol.DataHelper;
import com.evaluationo.protocol.DataType;
import com.evaluationo.protocol.PayLoad;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SocketThread extends Thread {
	private Socket socket = null;
	TcpConnect tcpConnect;
	private MyApplication context;
	private boolean keepAlive = true;
	private boolean statu = true;
	private OutputStream out = null;
	//private Handler mChildHandler;
	//private Looper myLooper;
	private String TAG = "effort";
	
	private boolean needEvaluate = false;

	public SocketThread(TcpConnect tcpConnect, Socket mClient, MyApplication context) {
		this.tcpConnect = tcpConnect;
		socket = mClient;
		this.context = context;
	}

	public void run() {

		InputStream in = null;
//		Looper.prepare();
//		myLooper = Looper.myLooper();
		while (keepAlive) {
			try {
				socket.setSoTimeout(6000);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			try {
				in = socket.getInputStream();

				out = socket.getOutputStream();

				final byte[] b = new byte[4];
				PayLoad payload = DataHelper.read(in);
				switch (payload.getType()) {
				case OPEN_CONNECTION:
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.RESPONSE.Flag();
					b[2] = DataType.OPEN_CONNECTION.Flag();
					b[3] = DataHelper.END_BYTE;
					break;
				case HEART_BEAT:
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.RESPONSE.Flag();
					b[2] = DataType.HEART_BEAT.Flag();
					b[3] = DataHelper.END_BYTE;
					Intent intent = new Intent("HEART_BEAT");
					context.sendBroadcast(intent);
					break;
				case CLOSE_CONNECTION:
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.RESPONSE.Flag();
					b[2] = DataType.CLOSE_CONNECTION.Flag();
					b[3] = DataHelper.END_BYTE;
					keepAlive = false;
					break;
				case APPLY_EVALUATE:
					needEvaluate = true;
					Log.e("effort", "APPLY_EVALUATE");
					Intent emptyIntent = new Intent(context,
							EvaluationActivity.class);
					emptyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(emptyIntent);
					break;
				case LEAVE_INFO:
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.RESPONSE.Flag();
					b[2] = DataType.LEAVE_INFO.Flag();
					b[3] = DataHelper.END_BYTE;
					Intent intent2 = new Intent("LEAVE_INFO");
					context.sendBroadcast(intent2);
					break;
				case BACK_INFO:
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.RESPONSE.Flag();
					b[2] = DataType.BACK_INFO.Flag();
					b[3] = DataHelper.END_BYTE;
					Intent intent3 = new Intent("BACK_INFO");
					context.sendBroadcast(intent3);
					break;
				default:
					break;
				}
				if(needEvaluate){
//					if(firstTime){
//					if(myLooper == null) {
//						Looper.prepare();
//						myLooper = Looper.myLooper();
//					}
//						firstTime = false;
//					}
//					mChildHandler = new Handler() {
//						public void handleMessage(Message msg) {
//							Log.e(TAG, "receive message.");
//							b[0] = DataHelper.BEGIN_BYTE;
//							b[1] = DataType.EVALUATE_RESULT.Flag();
//							b[2] = (byte)msg.what;
//							b[3] = DataHelper.END_BYTE;
//							myLooper.quit();
//						}
//					};
//					Looper.loop();
//					needEvaluate = false;
					while(!context.isStatu()){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.EVALUATE_RESULT.Flag();
					b[2] = (byte)context.getValue();
					b[3] = DataHelper.END_BYTE;
					needEvaluate = false;
					context.setStatu(false);
				}
				out.write(b);
				out.flush();
			} catch (java.net.SocketTimeoutException e) {
				e.printStackTrace();
				keepAlive = false;
				Intent intent = new Intent("TIMEOUT");
				context.sendBroadcast(intent);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "SocketThread " + e.toString());
			}
		}
		try {
			if(out != null)
				out.close();
			if(in != null)
				in.close();
			if(socket != null){
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(socket != null)
				try {
					socket.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		Log.e(TAG, "连接关闭");
		tcpConnect.removeSocketThread(this);
	}
	public void setNeedEvaluation(boolean statu) {
		needEvaluate = statu;
	}
//	public Handler getHandler(){
//		return mChildHandler;
//	}
	public void close() {
		keepAlive = false;
	}
}
