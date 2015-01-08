package com.evaluation.view;

import java.util.List;

import com.evaluation.control.AccountManager;
import com.evaluation.dao.DatabaseManager;
import com.evaluation.model.User;
import com.evaluation.util.DeletableAdapter;

import android.app.Activity; 
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;  
import android.content.Intent;  
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;  
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;  
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;  
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;  
import android.widget.CompoundButton;  
import android.widget.CompoundButton.OnCheckedChangeListener;  
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;  
  
public class LoginActivity extends Activity {

	private EditText account;
	private EditText password;
	private CheckBox rem_pw;
	private Button select;
	private Button btn_login;
	private Dialog logining;
	private String accountValue, passwordValue;
	private SharedPreferences sp;
	private AccountManager accountManager;
	private String loginId;
	private int autoLogin = 0;
	//private Thread autoLoginThread = new AutoLoginThread();
	private Thread m_Thread;
	private String TAG = "effort";
	private boolean isLogining = false;
	private DatabaseManager dba;

	private String DISCONNECT = "DISCONNECT";
	private String WRONGPW = "WRONGPW";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);
		((MyApplication)this.getApplication()).addActivity(this);
		((MyApplication)this.getApplication()).setEvaluatable(true);

		LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		View view = inflater.inflate(R.layout.select, null);
		// 获得实例对象
		sp = this.getSharedPreferences("autoLogin", Context.MODE_WORLD_READABLE);
		account = (EditText) findViewById(R.id.et_zh);
		password = (EditText) findViewById(R.id.et_mima);
		rem_pw = (CheckBox) findViewById(R.id.cb_mima);
		// auto_login = (CheckBox) findViewById(R.id.cb_auto);
		btn_login = (Button) findViewById(R.id.btn_login);
		// btnQuit = (ImageButton)findViewById(R.id.img_btn);
		DatabaseManager.initializeInstance(this);
		dba = DatabaseManager.getInstance();
		dba.open();
		accountManager = new AccountManager(this);
		ListView accountListView = (ListView) view
				.findViewById(R.id.accountList);

		btn_login.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((MyApplication)LoginActivity.this.getApplication()).setEvaluatable(false);
				isLogining = true;
//				logining = new ProgressDialog(LoginActivity.this);
//				logining.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				//logining.setTitle("提示");
//				logining.setMessage("正在登录");
//				//logining.setIcon(R.drawable.logo);
//				logining.setIndeterminate(true);
//				logining.setCanceledOnTouchOutside(false);
//				logining.show();
				logining = createLoadingDialog(LoginActivity.this, "正在登录");
				logining.setCanceledOnTouchOutside(false);
				logining.show();
				login();
			}
		});

		// 监听记住密码多选框按钮事件
		rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (rem_pw.isChecked()) {
					sp.edit().putBoolean("ISCHECK", true).commit();
				} else {
					sp.edit().putBoolean("ISCHECK", false).commit();
				}
			}
		});
		// 创建PopupWindow对象
		final PopupWindow pop = new PopupWindow(view, 225, ViewGroup.LayoutParams.WRAP_CONTENT, false);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);
		select = (Button) findViewById(R.id.select);
		pop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				select.setBackgroundResource(R.drawable.select_down);
			}

		});

		select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					// select.setBackgroundResource(R.drawable.select_down);
					pop.dismiss();
				} else {
					// 显示窗口
					select.setBackgroundResource(R.drawable.select_up);
					pop.showAsDropDown(v, -167, -10);
				}
			}
		});
		List<User> userList = dba.findAllUser();
		if(userList != null) {
			DeletableAdapter accountAdapter = new DeletableAdapter(this,
					userList, pop, account, password);
			accountListView.setAdapter(accountAdapter);
		}
		// 判断记住密码多选框的状态
		if (sp.getBoolean("ISCHECK", true)) {
			// 设置默认是记录密码状态
			rem_pw.setChecked(true);
		}
	}
	
	private void login(){
		m_Thread = new LoginThread();
		m_Thread.start();
	}
	
	private class LoginThread extends Thread {
		public void run() {
			Handler mHandler = new LoginHandler(Looper.getMainLooper());
			String msg = "";
			accountValue = account.getText().toString();
			passwordValue = password.getText().toString();
			User user = new User();
			user.setAccount(accountValue);
			user.setPassword(passwordValue);
			//accountManager = new AccountManager(LoginActivity.this);
			loginId = accountManager.login(user);
			if (loginId == null) {
				msg = "fail";
				mHandler.removeMessages(0);
				Message m = mHandler.obtainMessage(1, 1, 1, msg);
				mHandler.sendMessage(m);
			} else if(loginId.equals(DISCONNECT)) {
				msg = DISCONNECT;
				mHandler.removeMessages(0);
				Message m = mHandler.obtainMessage(1, 1, 1, msg);
				mHandler.sendMessage(m);
			} else if(loginId.equals(WRONGPW)) {
				msg = WRONGPW;
				mHandler.removeMessages(0);
				Message m = mHandler.obtainMessage(1, 1, 1, msg);
				mHandler.sendMessage(m);
			} else {
				msg = "success";
				mHandler.removeMessages(0);
				Message m = mHandler.obtainMessage(1, 1, 1, msg);
				mHandler.sendMessage(m);
			}
		}
	}

	private class LoginHandler extends Handler {
		public LoginHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			String value = msg.obj.toString();
			if (value.equals("success")) {
				((MyApplication)LoginActivity.this.getApplication()).setEvaluatable(true);
				//dba.close();
//				Toast.makeText(LoginActivity.this,"登录成功",
//				Toast.LENGTH_SHORT).show();
				
				// 跳转界面
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.putExtra("account", accountValue);
				intent.putExtra("loginId", loginId);
				LoginActivity.this.startActivity(intent);
				logining.setTitle("登录成功");
				logining.dismiss();
				finish();
			} else if(value.equals("emptyAutoLogin")) {
				// 跳转界面
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.putExtra("account", "<null>");
				intent.putExtra("loginId", "");
				LoginActivity.this.startActivity(intent);
				finish();
			}else if(value.equals(DISCONNECT)) {
				Toast.makeText(LoginActivity.this, "没有连接网络",
						Toast.LENGTH_LONG).show();
				logining.setTitle("没有连接网络");
				logining.dismiss();
				autoLogin = 0;
			}else if(value.equals(WRONGPW)) {
				Toast.makeText(LoginActivity.this, "密码错误，请重新输入正确密码",
						Toast.LENGTH_LONG).show();
				logining.setTitle("密码错误");
				logining.dismiss();
				autoLogin = 0;
			}else {
				//((MyApplication)LoginActivity.this.getApplication()).setEvaluatable(true);
				Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新登录",
						Toast.LENGTH_LONG).show();
				logining.setTitle("用户名或密码错误");
				logining.dismiss();
				autoLogin = 0;
				isLogining = false;
				Thread autoLoginThread = new AutoLoginThread();
				autoLoginThread.start();
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 在欢迎界面屏蔽BACK键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}
	
	@Override
	protected void onStart() {
		Thread autoLoginThread = new AutoLoginThread();
		autoLoginThread.start();
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		dba.close();
		accountManager.close();
		if(logining != null)
			logining.dismiss();
		super.onDestroy();
	}
	
	private class AutoLoginThread extends Thread {
		public void run() {
			while(!isLogining) {
				autoLogin++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(autoLogin % 200 == 0) {
					((MyApplication)LoginActivity.this.getApplication()).setEvaluatable(false);
					Handler mHandler = new LoginHandler(Looper.getMainLooper());
					User user = new User();
					user.setAccount("<null>");
					if(!accountManager.autoLogin(user))
						continue;
					String msg = "emptyAutoLogin";
					mHandler.removeMessages(0);
					Message m = mHandler.obtainMessage(1, 1, 1, msg);
					mHandler.sendMessage(m);
					isLogining = true;
					autoLogin = 0;
				}
			}
		}
	}
	/** 
     * 得到自定义的progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
    public static Dialog createLoadingDialog(Context context, String msg) {  
  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);// 设置加载信息  
  
        Dialog loadingDialog = new Dialog(context, R.style.Theme_dialog);// 创建自定义样式dialog  
  
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局  
        return loadingDialog;  
  
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
