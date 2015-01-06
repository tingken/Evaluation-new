package com.evaluation.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.evaluation.protocol.DataHelper;
import com.evaluation.protocol.DataType;
import com.evaluation.protocol.PayLoad;
import com.evaluation.view.EvaluationActivity;
import com.evaluation.view.MyApplication;

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
	private volatile boolean keepAlive = true;
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
		int i = 0;
		InputStream in = null;
//		Looper.prepare();
//		myLooper = Looper.myLooper();
		while (keepAlive) {
//			try {
//				socket.setSoTimeout(4000);
//			} catch (SocketException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				return;
//			}
			try {
				//socket.setKeepAlive(true);
				socket.setSoTimeout(4000);
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
					Log.e(TAG, "关闭连接");
					keepAlive = false;
					break;
				case APPLY_EVALUATE:
					needEvaluate = true;
					Intent emptyIntent = new Intent(context,
							EvaluationActivity.class);
					emptyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(emptyIntent);
					context.setStatu(false);
					Log.e(TAG, "APPLY_EVALUATE");
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
				if(needEvaluate && context.isStatu()){
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
					
//					int heldtime = 0;
//					while(!context.isStatu() && heldtime < 10 * 10){
//						heldtime++;
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					b[0] = DataHelper.BEGIN_BYTE;
					b[1] = DataType.EVALUATE_RESULT.Flag();
					b[2] = (byte)context.getValue();
					b[3] = DataHelper.END_BYTE;
					Log.e(TAG, "通过socket发送的评价结果: " + b[2]);
					needEvaluate = false;
					context.setStatu(false);
				}
				out.write(b);
				out.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				keepAlive = false;
				Log.e(TAG, "Exception: SocketThread " + e.toString());
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
			if(socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Intent intent = new Intent("TIMEOUT");
		context.sendBroadcast(intent);
		Log.e(TAG, "退出socket");
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
