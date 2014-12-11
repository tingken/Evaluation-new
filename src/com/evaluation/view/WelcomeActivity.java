package com.evaluation.view;

import java.util.List;

import com.evaluation.control.AccountManager;
import com.evaluation.dao.DatabaseAdapter;
import com.evaluation.model.User;
import com.evaluation.util.ApkInstaller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements AnimationListener {
	private LinearLayout layout = null;
	private Animation alphaAnimation = null;
	private SharedPreferences sp;
	private String account;
	private String loginId;
	private String result = "";
	private String TAG = "effort";
	private String assetsApk = "SpeechService.apk";
	private DatabaseAdapter dba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//去除标题  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.welcome);
		((MyApplication)this.getApplication()).addActivity(this);
		layout = (LinearLayout) findViewById(R.id.welcome_layout);
		alphaAnimation = AnimationUtils.loadAnimation(this,
				R.anim.welcome_alpha);
		alphaAnimation.setFillEnabled(true); // 启动Fill保持
		alphaAnimation.setFillAfter(true); // 设置动画的最后一帧是保持在View上面
		layout.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this); // 为动画设置监听
		
		//获得实例对象  
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        //初始化数据库
        dba = new DatabaseAdapter(this);
		dba.open();
        init();
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Log.e(TAG, "animation end.");
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 在欢迎界面屏蔽BACK键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}
	private void init() {
		if(!this.checkSpeechServiceInstall()){
			Thread installThread = new InstallThread();
			installThread.start();
		}
		if(sp.getBoolean("ISCHECK", false))  
		{
			if(dba.findAllUser() != null) {
				Thread loginThread = new LoginThread();
				loginThread.start();
			}else{
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
			//autoLogin = true;
		}else{
			Intent intent = new Intent(WelcomeActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
	private class LoginThread extends Thread{
    	public void run() { 
			Handler mHandler = new LoginHandler(Looper.getMainLooper());
			String msg = "";
            AccountManager accountManager = new AccountManager(WelcomeActivity.this);
            User user = dba.findLatestUser();
            if(user != null){
            	account = user.getAccount();
            	loginId = accountManager.login(user);
            	if(loginId != null)
                	msg = "success";
            	else{
                	msg = "fail";
                }
            }else{
            	msg = "fail";
            }
            mHandler.removeMessages(0);
            Message m = mHandler.obtainMessage(1, 1, 1, msg);
            mHandler.sendMessage(m);
    	}
    }

	private class LoginHandler extends Handler {
		public LoginHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
//			layout = (LinearLayout) findViewById(R.id.welcome_layout);
//			alphaAnimation = AnimationUtils.loadAnimation(WelcomeActivity.this,
//					R.anim.welcome_alpha);
//			alphaAnimation.setFillEnabled(true); // 启动Fill保持
//			alphaAnimation.setFillAfter(true); // 设置动画的最后一帧是保持在View上面
//			layout.setAnimation(alphaAnimation);
//			alphaAnimation.setAnimationListener(WelcomeActivity.this); // 为动画设置监听
			result = msg.obj.toString();
			if (result.equals("success")) {
				Toast.makeText(WelcomeActivity.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				// 跳转界面
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				intent.putExtra("account", account);
				intent.putExtra("loginId", loginId);
				WelcomeActivity.this.startActivity(intent);
				finish();
			}else{
				Toast.makeText(WelcomeActivity.this, "用户名或密码错误，请重新登录",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	private class InstallThread extends Thread {
		public void run() {
			WelcomeActivity.this.processInstall(WelcomeActivity.this, assetsApk);
		}
	}
	// 判断手机中是否安装了讯飞语音+
	private boolean checkSpeechServiceInstall() {
		String packageName = "com.iflytek.speechcloud";
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo.packageName.equals(packageName)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	/**
	 * 如果服务组件没有安装，有两种安装方式。 1.直接打开语音服务组件下载页面，进行下载后安装。
	 * 2.把服务组件apk安装包放在assets中，为了避免被编译压缩，修改后缀名为mp3，然后copy到SDcard中进行安装。
	 */
	private boolean processInstall(Context context, String assetsApk) {
		// 直接下载方式
		// ApkInstaller.openDownloadWeb(context, url);
		// 本地安装方式
		if (!ApkInstaller.installFromAssets(context, assetsApk)) {
			Log.e(TAG, "讯飞语音安装失败");
			return false;
		}
		return true;
	}
}