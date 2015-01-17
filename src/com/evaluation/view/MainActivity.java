package com.evaluation.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.hardware.usb.UsbManager;
import com.evaluation.control.AccountManager;
import com.evaluation.control.AnnouncementManager;
import com.evaluation.control.WebServiceManager;
import com.evaluation.dao.DatabaseManager;
import com.evaluation.model.Announcement;
import com.evaluation.model.ComplaintResult;
import com.evaluation.model.DealResult;
import com.evaluation.model.LeaveMessage;
import com.evaluation.model.User;
import com.evaluation.service.HomeService;
import com.evaluation.util.CommonUtils;
import com.evaluation.util.ComplaintDialog;
import com.evaluation.util.DotView;
import com.evaluation.util.LeaveDialog;
import com.evaluation.util.MarqueeView;
import com.evaluation.util.PagerAdapter;
import com.evaluation.util.ThreeButtonDialog;
import com.evaluation.util.VerticalViewPager;
import com.evaluation.util.VerticalViewPager.OnPageChangeListener;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private String TAG = "effort";
	private boolean activityOver = false;
	private boolean annosOk = false;
	private VerticalViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	AnnouncementManager am;
	private WebServiceManager wsm;
	private ComplaintResult complaintResult;
	private DealResult dealResult;

	private String[] titles; // 图片标题
	private String[] dates; // 公告发布日期
	private String[] contents; //图片对应的正文
	private ArrayList<View> dots; // 图片标题正文的那些点

	private TextView leaveMessageView;
	private ImageButton setting;
	private ImageButton evaluation;
	private Button quit;
	private ShimmerTextView complaint;
	private Shimmer shimmer;
	private TextView weekView;
	private TextView dateView;
	private TextView timeView;
	private ImageView photoView;
	private MarqueeView userNameView, userTitleView, accountView, orgView;
	private TextView settingName, emNo, version;
	private MarqueeView macAddr;
	private String dateValue;
	private String weekValue;
	private String timeValue;
	private TextView tv_title;
	private TextView tv_date;
	private TextView tv_content;
	private TextView acceptBiz;
	private Thread annoThread = null;
	private int currentItem = 0; // 当前图片的索引号
	private Handler announHandler = new AnnounHandler();
	private DatabaseManager dba;
	private SharedPreferences sp;
	//private String loginId;
	private String account;
	private User user;
	private List<Announcement> annoList;
	private Button businessGuide;
	private Button jobStatements;
	private Button servicePromises;
	private Button infCenter;
	private Button accessWeb;
	private List<Button> buttons = new ArrayList<Button>();
	private Bitmap bitmap;
	private String versionNo;
	private boolean _isBound;
	private HomeService _boundService;
	//private UsbBroadcastReceiver mBroadcastReceiver;
	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;
	private LeaveDialog leaveDialog;// = new LeaveDialog(this, R.layout.layout_dialog, R.style.Theme_dialog);
	private ComplaintDialog complaintDialog;
	private LeaveMessage leaveMessage = new LeaveMessage();
	private boolean firstTime = true;
	ThreeButtonDialog threeButtonDialog;

	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService = null;
	private Timer uiUpdateTimer = null;

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
//			tv_title.setText(titles[currentItem]);
//			tv_content.setText(contents[currentItem]);
		};
	};
	
	// 设置标题上的时间
	private Handler dateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
	        dateView.setText(dateValue);
	        timeView.setText(timeValue);
	        weekView.setText("星期" + weekValue);
//	        if(msg.what == 2)
//	        	viewPager.setCurrentItem(currentItem);
		};
	};
	
	// 获取工作人员请假信息
//		private Handler leaveMessageHandler = new Handler() {
//			public void handleMessage(android.os.Message msg) {
//		        if(msg.what == 1) {
//		        	leaveMessageView.setText("请假中");
//		        	leaveMessageView.setBackgroundColor(Color.GRAY);
//		        	if(scheduledExecutorService != null) {
//		    			scheduledExecutorService.shutdown();
//		    			scheduledExecutorService = null;
//		    			//activityOver = false;
//		    			annosOk = false;
//		    		}
//		        	tv_content.setText(leaveMessage.getDescription());
//		        }else {
//		        	leaveMessageView.setText("正在服务");
//		        	leaveMessageView.setBackgroundColor(Color.GREEN);
//		        	if(scheduledExecutorService == null) {
//		    			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//		    			// 当Activity显示出来后，每两秒钟切换一次图片显示
//		    			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 4, 4, TimeUnit.SECONDS);
//			        	annosOk = true;
//		    		}
//		        }
//			};
//		};
		private Handler leaveMessageHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
		        if(msg.what == 1) {
		        	leaveMessageView.setText("请假中");
		        	leaveMessageView.setBackgroundColor(Color.GRAY);
		        	if(scheduledExecutorService != null) {
		    			scheduledExecutorService.shutdown();
		    			scheduledExecutorService = null;
		    			//activityOver = false;
		    			annosOk = false;
		    		}
		        	tv_title.setText("");
		        	tv_date.setText("");
		        	tv_content.setText(leaveMessage.getDescription());
		        }else {
		        	leaveMessageView.setText("正在服务");
		        	leaveMessageView.setBackgroundColor(Color.GREEN);
		        	if(scheduledExecutorService == null) {
		    			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		    			// 当Activity显示出来后，每两秒钟切换一次图片显示
		    			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 4, 4, TimeUnit.SECONDS);
			        	annosOk = true;
		    		}
		        }
			};
		};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去除标题  
		Log.e(TAG, "MainActivity.onCreate");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        powerManager = (PowerManager)(getSystemService(Context.POWER_SERVICE));   
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, TAG);  
        wakeLock.acquire();
        setContentView(R.layout.main);
        ((MyApplication)this.getApplication()).addActivity(this);
        account = this.getIntent().getStringExtra("account");
		//loginId = this.getIntent().getStringExtra("loginId");
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
//		sp.edit().putString("account", account).commit();
//		sp.edit().putString("loginId", loginId).commit();
		((MyApplication)this.getApplication()).setAccount(account);
		//((MyApplication)this.getApplication()).setLoginId(loginId);
		((MyApplication)this.getApplication()).setEvaluatable(true);
        try {
			PackageInfo packInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			versionNo = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Typeface MSFace = Typeface.createFromAsset(getAssets(),"fonts/MSYHBD.TTF");
        leaveMessageView = (TextView) findViewById(R.id.leave_message);
        dateView = (TextView) findViewById(R.id.date);
		weekView = (TextView) findViewById(R.id.week);
		timeView = (TextView) findViewById(R.id.time);
		setting = (ImageButton) findViewById(R.id.setting);
//		complaint = (ImageButton) findViewById(R.id.complaint);
		complaint = (ShimmerTextView) findViewById(R.id.complaint);
//		shimmer = new Shimmer();
//		shimmer.start(complaint);
		
		photoView = (ImageView) findViewById(R.id.photo);
		userNameView = (MarqueeView) findViewById(R.id.user_name);
		userTitleView = (MarqueeView) findViewById(R.id.user_title);
		//userNameView.setTypeface(MSFace); //微软雅黑
		accountView = (MarqueeView) findViewById(R.id.account);
		accountView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
		accountView.getPaint().setAntiAlias(true);//抗锯齿
		orgView = (MarqueeView) findViewById(R.id.org);
		orgView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
		orgView.getPaint().setAntiAlias(true);//抗锯齿
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_title.setShadowLayer(1F, 2F,2F, Color.WHITE);
		tv_title.getPaint().setColor(Color.RED);
		tv_title.getPaint().setStrokeWidth(3);
		tv_date.setShadowLayer(1F, 2F,2F, Color.WHITE);
		tv_content.setShadowLayer(1F, 2F,2F, Color.WHITE);
		imageViews = new ArrayList<ImageView>();
		viewPager = (VerticalViewPager) findViewById(R.id.vp);

		acceptBiz = (TextView) findViewById(R.id.acceptBiz); 
		acceptBiz.setMovementMethod(ScrollingMovementMethod.getInstance());
		//注意如果想要滚动条时刻显示, 必须加上以下语句:
		acceptBiz.setScrollbarFadingEnabled(false);
		
		evaluation = (ImageButton) findViewById(R.id.evaluation);
		evaluation.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//跳转界面  
                Intent intent = new Intent(MainActivity.this,EvaluationActivity.class);
                //intent.putExtra("loginId", loginId);
                MainActivity.this.startActivity(intent);
			}
		});
		businessGuide = (Button) findViewById(R.id.businessGuide);
		businessGuide.setTag(1);
		businessGuide.setOnClickListener(this);
		buttons.add(businessGuide);
		jobStatements = (Button) findViewById(R.id.jobStatements);
		jobStatements.setTag(2);
		jobStatements.setOnClickListener(this);
		buttons.add(jobStatements);
		servicePromises = (Button) findViewById(R.id.servicePromises);
		servicePromises.setTag(3);
		servicePromises.setOnClickListener(this);
		buttons.add(servicePromises);
		infCenter = (Button) findViewById(R.id.inf_center);
		infCenter.setTag(4);
		infCenter.setOnClickListener(this);
		buttons.add(infCenter);
		accessWeb = (Button) findViewById(R.id.accessWeb);
		accessWeb.setTag(5);
		accessWeb.setOnClickListener(this);
		buttons.add(accessWeb);
		UsbManager manager=(UsbManager) getSystemService(Context.USB_SERVICE);
		registerReceiver(mBroadcastReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
		registerReceiver(mBroadcastReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
		registerReceiver(mBroadcastReceiver, new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED));
		registerReceiver(mBroadcastReceiver, new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_ATTACHED));
		registerReceiver(mBroadcastReceiver, new IntentFilter("LEAVE_INFO"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("BACK_INFO"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("TIMEOUT"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("COMPLAINT_SUCCESS"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("COMPLAINT_FAIL"));
		registerReceiver(mBroadcastReceiver, new IntentFilter("COMPLAINT_RESULT"));
		annoList = new ArrayList<Announcement>();
//		annoThread = new AnnounThread();
//		annoThread.start();
		
		leaveDialog = new LeaveDialog(this, R.layout.layout_dialog, R.style.Theme_dialog);
		leaveDialog.setCanceledOnTouchOutside(false);
		complaintDialog = new ComplaintDialog(this, R.layout.complaint_dialog, R.style.Theme_dialog);
		complaintDialog.setCanceledOnTouchOutside(false);
//		complaint.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(MainActivity.this,ComplaintActivity.class);
//				MainActivity.this.startActivity(intent);
//			}
//			
//		});
		startService();
		complaint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);           
//				//builder.setIcon(R.drawable.icon);  
//				//builder.setTitle("投票");  
//				builder.setMessage("投诉后受理投诉工作人员将在5分钟内到达现场 是否等待?");  
//				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {  
//				    public void onClick(DialogInterface dialog, int whichButton) {  
//				        //
//				    	sendComplaint("","","","群众发起了一个投诉。");
//				    }  
//				});  
//				builder.setNeutralButton("否", new DialogInterface.OnClickListener() {  
//				    public void onClick(DialogInterface dialog, int whichButton) {  
//				        //                     
//				    }  
//				});  
//				builder.setNegativeButton("我要匿名投诉", new DialogInterface.OnClickListener() {  
//				    public void onClick(DialogInterface dialog, int whichButton) {  
//				        //   
//						Intent intent = new Intent(MainActivity.this,ComplaintActivity.class);
//						MainActivity.this.startActivity(intent);
//				    	startService();
//				    }  
//				});  
//				builder.create().show();
				threeButtonDialog = new ThreeButtonDialog(MainActivity.this, R.layout.three_button_dialog, R.style.Theme_dialog);
				threeButtonDialog.setMessage("投诉后受理投诉工作人员将在5分钟内到达现场，是否等待?");
				threeButtonDialog.setPositiveButton("是", new OnClickListener() {  
					public void onClick(View view) {  
						//
						sendComplaint("","","","群众发起了一个投诉。");
						threeButtonDialogDismiss();
					}  
				});  
				threeButtonDialog.setNeutralButton("否", new OnClickListener() {  
					public void onClick(View view) {  
			        //           
						threeButtonDialogDismiss();
					}  
				});  
				threeButtonDialog.setNegativeButton("我要匿名投诉", new OnClickListener() {  
					public void onClick(View view) {  
						//   
						Intent intent = new Intent(MainActivity.this,ComplaintActivity.class);
						MainActivity.this.startActivity(intent);
						startService();
						threeButtonDialogDismiss();
					}  
				}); 
				threeButtonDialog.show();
			}
			
		});
	}
	
	private class MyDotClickListener implements View.OnClickListener{
		private int item;
		public MyDotClickListener(int item) {
			this.item = item;
		}
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			viewPager.setCurrentItem(item);
		}
		
	}
	
	@Override
	protected void onStart() {
		Log.e(TAG, "MainActivity.onStart");
		activityOver = false;
		if(scheduledExecutorService == null && !activityOver) {
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			// 当Activity显示出来后，每两秒钟切换一次图片显示
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 4, 4, TimeUnit.SECONDS);
		}
		if(uiUpdateTimer == null && !activityOver) {
			//Log.e(TAG, "定时更新数据");
			uiUpdateTimer = new Timer(true);
			// 每隔一段时间更新UI
			uiUpdateTimer.schedule(new UIUpdateTask(), 0, 1 * 60 * 1000);
		}
		Thread dateThread = new DateThread();
		dateThread.start();
		Thread leaveMessageThread = new LeaveMessageThread();
		leaveMessageThread.start();
		for(Button button : buttons) {
			button.setEnabled(true);
		}
		if(setting != null)
			setting.setEnabled(true);
//		bindService();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		if(scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
			scheduledExecutorService = null;
		}
		
//		if(uiUpdateTimer != null) {
//			uiUpdateTimer.cancel();
//			uiUpdateTimer = null;
//		}
		leaveDialog.dismiss();
		complaintDialog.dismiss();
		activityOver = true;
//		unbindService();
		//stopService();
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestory");
		if(scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
			scheduledExecutorService = null;
		}
		if(uiUpdateTimer != null) {
			uiUpdateTimer.cancel();
			uiUpdateTimer = null;
		}
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		sp.edit().remove("account");
		sp.edit().remove("loginId");
		stopService();
		//((MyApplication)this.getApplication()).setLoginId("");
		if(mBroadcastReceiver != null)
			this.unregisterReceiver(mBroadcastReceiver);
//		if(((MyApplication)this.getApplication()).getActivityCount() <= 1)
//			((MyApplication)this.getApplication()).exit();
		super.onDestroy();
	}
	
	/**
	 * 修改标题栏上的时间
	 */
	private class DateThread extends Thread {
		public void run() {
			int count = 0;
			while(!activityOver) {
				count ++;
//				if((count % 4) == 0 && annosOk){
//					synchronized (viewPager) {
//						currentItem = (currentItem + 1) % annoList.size();
//						dateHandler.sendEmptyMessage(2); // 通过Handler切换图片
//					}
//				}
				if((count % 300) == 0) {
					Log.e(TAG, "定时上传保存的未上传评价");
					AccountManager acm = new AccountManager(MainActivity.this);
					acm.sendEvaluation();
					acm.close();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Time time = new Time("GMT+8");
		        time.setToNow();
		        int week = time.weekDay;
		        dateValue = DateFormat.format("yyyy.MM.dd", System.currentTimeMillis()).toString();
		        timeValue = DateFormat.format("kk : mm", System.currentTimeMillis()).toString();
		        weekValue = String.valueOf(week);
		        if("0".equals(weekValue)){  
		        	weekValue ="天";  
		        }else if("1".equals(weekValue)){  
		        	weekValue ="一";  
		        }else if("2".equals(weekValue)){  
		        	weekValue ="二";  
		        }else if("3".equals(weekValue)){  
		        	weekValue ="三";  
		        }else if("4".equals(weekValue)){  
		        	weekValue ="四";
		        }else if("5".equals(weekValue)){  
		        	weekValue ="五";  
		        }else if("6".equals(weekValue)){  
		        	weekValue ="六";  
		        }
				dateHandler.sendEmptyMessage(1);
			}
		}
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % annoList.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}
	}
	
//	private class UIUpdateTask implements Runnable {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			Log.d(TAG, "更新UI线程启动");
//			AccountManager acm = new AccountManager(MainActivity.this);
//			DatabaseManager.initializeInstance(MainActivity.this);
//			dba = DatabaseManager.getInstance();
//			dba.open();
//			user = dba.findUserByAccount(account);
//			acm.addUserInfo(loginId);
//			acm.getRemoteFile(user);
//			//if(annoThread == null)
//			annoThread = new AnnounThread();
//			annoThread.start();
//			if(dba != null)
//				dba.close();
//		}
//		
//	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			tv_date.setText(dates[position]);
			tv_content.setText(contents[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
//			if((position + annoList.size() - 1) % annoList.size() < imageViews.size()) {
//				ImageView uselessImageView = imageViews.get((position + annoList.size() - 1) % annoList.size());
//				BitmapDrawable bitmapDrawable = (BitmapDrawable) uselessImageView
//						.getDrawable();
//				Bitmap bmp = bitmapDrawable.getBitmap();
//				if (null != bmp && !bmp.isRecycled()) {
//					//bmp.recycle();
//					bmp = null;
//				}
//				uselessImageView = null;
//			}
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	
	private class MyViewPagerOnClickListener implements OnClickListener {
		private int item;
		public MyViewPagerOnClickListener(int item) {
			this.item = item;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this, InfoDetailActivity.class);
			intent.putExtra("currentItem", item);
			intent.putExtra("account", account);
			MainActivity.this.startActivity(intent);
		}
		
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter  {

		@Override
		public int getCount() {
			return annoList.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			if(annoList.size() < 1)
				return null;
			if(arg1 >= annoList.size())
				arg1 = arg1 % annoList.size();
			ImageView imageView = new ImageView(MainActivity.this);
			Bitmap bmp = am.getBitmapFromMemCache(arg1, annoList.get(arg1).getImageName());
			if(bmp != null && !bmp.isRecycled())
				imageView.setImageBitmap(bmp);
			imageView.setOnClickListener(new MyViewPagerOnClickListener(
					annoList.get(arg1).getId()));
			((VerticalViewPager) arg0).addView(imageView);
//			return imageViews.get(arg1);
//			if (imageViews.size() <= arg1)
//				imageViews.add(imageView);
//			else
//				imageViews.set(arg1, imageView);
//			if(imageViews != null && imageViews.size() > 0) {
//				((VerticalViewPager) arg0).addView(imageViews.get(arg1));
//				return imageViews.get(arg1);
//			}
//			return null;
			return imageView;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((VerticalViewPager) arg0).removeView((View) arg2);
			//am.removeImageCache(arg1, annoList.get(arg1).getImageName());
//			Bitmap bmp = (Bitmap) ((ImageView) arg2).getDrawingCache();
//			if (null != bmp && !bmp.isRecycled()) {
//				bmp.recycle();
//				bmp = null;
//			}
//			arg2 = null;
//			System.gc();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
	
	private class UIUpdateTask extends TimerTask {
		public void run() {
			Log.e(TAG, "定时更新界面");
			if(scheduledExecutorService != null) {
				scheduledExecutorService.shutdown();
				scheduledExecutorService = null;
			}
			if(!firstTime) {
				AccountManager acm = new AccountManager(MainActivity.this);
				DatabaseManager.initializeInstance(MainActivity.this);
				dba = DatabaseManager.getInstance();
				dba.open();
				user = dba.findUserByAccount(account);
				String loginId = acm.login(user);
				if(dba != null)
					dba.close();
				acm.close();
				if(loginId == null || loginId.equals("DISCONNECT") || loginId.equals("WRONGPW"))
					return;
			}
			firstTime = false;
			
			//annoList.clear();
			DatabaseManager.initializeInstance(MainActivity.this);
			dba = DatabaseManager.getInstance();
			dba.open();
			user = dba.findUserByAccount(account);
			String absolutePath = getFilesDir().getAbsolutePath();
			am = new AnnouncementManager(absolutePath, MainActivity.this);
			if(user != null)
				bitmap = am.getBitmapByName(user.getPhotoName());
			List<Announcement> allAnnos = dba.findAnnouncementsByAccount(account);
			annoList.clear();
			for(Announcement ann : allAnnos) {
				if(afterNow(ann.getOutOfDate(), "yyyy年MM月dd日") || ann.getOutOfDate().equals("null"))
					annoList.add(ann);
				//Log.e(TAG, "标题:" + ann.getTitle());
			}
			titles = new String[annoList.size()];
			dates = new String[annoList.size()];
			contents = new String[annoList.size()];
			
			//imageViews = am.getImageViews();
			for(int i = 0; i < annoList.size(); i++) {
				if (!annoList.get(i).getTitle().equals("null"))
					titles[i] = annoList.get(i).getTitle();
				if (!annoList.get(i).getRepDate().equals("null"))
					dates[i] = annoList.get(i).getRepDate();
				if (!annoList.get(i).getContent().equals("null")) {
					contents[i] = annoList.get(i).getContent();
				}
//				ImageView imageView = new ImageView(MainActivity.this);
//				if(annoList.get(i).getImageName() != null && !annoList.get(i).getImageName().equals("null")) {
//					imageView.setImageBitmap(am.getRoundCornerBitmapByName(annoList.get(i).getImageName()));
//				}
//				imageView.setOnClickListener(new MyViewPagerOnClickListener(annoList.get(i).getId()));
//				imageViews.add(imageView);
			}
			
			if(dba != null)
				dba.close();
			announHandler.sendEmptyMessage(1);
			Intent intent = new Intent("UIUPDATE");
			MainActivity.this.sendBroadcast(intent);
		}
	}
	private class AnnounHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			MyAdapter mAdapter = new MyAdapter();
			viewPager.setAdapter(mAdapter);// 设置填充ViewPager页面的适配器
			// 设置一个监听器，当ViewPager中的页面改变时调用
			viewPager.setOnPageChangeListener(new MyPageChangeListener());
			if(user != null) {
//				userNameView.setText(user.getName());
//				userTitleView.setText(user.getTitle());
				if(user.getTitle().equals("")){
					userNameView.setText(user.getName());
					userTitleView.setText("");
				}else{
					userNameView.setText(user.getTitle());
					userTitleView.setText(user.getName());
				}
				accountView.setText(user.getWorkNum());
				orgView.setText(user.getOrg());
				acceptBiz.setText(user.getOperation());
				photoView.setImageBitmap(bitmap);
			}
			if(titles.length > 0)
				tv_title.setText(titles[0]);
			if(dates.length > 0)
				tv_date.setText(dates[0]);
			if(contents.length > 0)
				tv_content.setText(contents[0]);
			//tv_title.setText(titles[0]);
			
			
			LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.dots);
			dotsLayout.removeAllViews();
			dots = new ArrayList<View>();
			for(int i = 0; i < annoList.size(); i++) {
				View dot = new DotView(MainActivity.this);
				dot.setId(50 + i);
				if(i == 0){
					dot.setBackgroundResource(R.drawable.dot_focused);
				}else{
					dot.setBackgroundResource(R.drawable.dot_normal);
				}
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
				params.setMargins(3, 0, 3, 0);
				dot.setLayoutParams(params);
				dots.add(dot);
				dotsLayout.addView(dot, i);
			}
			//dotsLayout.addChildrenForAccessibility(dots);
			
//			for(int i = 0; i < dots.size(); i++) {
//				dots.get(i).setOnClickListener(new MyDotClickListener(i));
//			}
			if(scheduledExecutorService == null) {
				scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
				// 当Activity显示出来后，每两秒钟切换一次图片显示
				scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 4, 4, TimeUnit.SECONDS);
			}
			
			annosOk = true;
			//设置按钮功能实现
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);  
	        // 引入窗口配置文件  
	        View view = inflater.inflate(R.layout.setting, null);  
	        // 创建PopupWindow对象  
	        final PopupWindow pop = new PopupWindow(view, 320, 410, false); 
	        // 需要设置一下此参数，点击外边可消失  
	        pop.setBackgroundDrawable(new BitmapDrawable());
	        //设置点击窗口外边窗口消失  
	        pop.setOutsideTouchable(true);
	        // 设置此参数获得焦点，否则无法点击  
	        pop.setFocusable(true);

	        setting.setOnClickListener(new View.OnClickListener() {  
	              
	            @Override  
	            public void onClick(View v) {  
	                if(pop.isShowing()) {  
	                    // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏  
	                    pop.dismiss();  
	                } else {  
	                    // 显示窗口  
	                    pop.showAsDropDown(v);
	                }  
	                  
	            }  
	        });
			settingName = (TextView) view.findViewById(R.id.setting_name);
			emNo = (TextView) view.findViewById(R.id.em_no);
			if(user != null) {
				settingName.setText(user.getName());
				emNo.setText(user.getWorkNum());
			}
			version = (TextView) view.findViewById(R.id.version);
			version.setText("版本号:" + versionNo);

	        macAddr = (MarqueeView) view.findViewById(R.id.deviceId);
	        macAddr.setText("机器码:" + getLocalMacAddress());
	        quit = (Button) view.findViewById(R.id.quit);
	        quit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pop.dismiss();
					((MyApplication)MainActivity.this.getApplication()).setAccount("");
					//((MyApplication)MainActivity.this.getApplication()).setLoginId("");
					MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
					MainActivity.this.finish();
				}
			});
		}
	}
	private ServiceConnection _connection = new ServiceConnection(){
		public void onServiceConnected(ComponentName className, IBinder Service){
			_boundService = ((HomeService.LocalBinder)Service).getService();
			//Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
		}
		public void onServiceDisconnected(ComponentName className){
			//
			_boundService = null;
			//Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void startService(){
		Intent i = new Intent(this, HomeService.class);
		this.startService(i);
	}
	private void stopService(){
		Intent i = new Intent(this, HomeService.class);
		this.stopService(i);
	}
	private void bindService(){
		Log.e(TAG, "================>Main.bindService");
		Intent i = new Intent(this, HomeService.class);
		this.bindService(i, _connection, Context.BIND_AUTO_CREATE);
		_isBound = true;
	}
	private void unbindService(){
		Log.e(TAG, "================>Main.unbindService");
		if(_isBound){
			unbindService(_connection);
			_isBound = false;
		}
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)) {
	        	Log.e(TAG, "ACTION_USB_ACCESSORY_DETACHED");
	        	Toast.makeText(MainActivity.this, "ACTION_USB_ACCESSORY_DETACHED", Toast.LENGTH_SHORT).show();
	        	wakeLock.release();
	        	wakeLock = null;
	        } else if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)) {
	        	Log.e(TAG, "ACTION_USB_ACCESSORY_ATTACHED");
	        	Toast.makeText(MainActivity.this, "ACTION_USB_ACCESSORY_ATTACHED", Toast.LENGTH_SHORT).show();
	        	wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");   
	        	wakeLock.acquire();
	        } else if(intent.getAction().equals("LEAVE_INFO")) {
	        	if(scheduledExecutorService != null) {
	    			scheduledExecutorService.shutdown();
	    			scheduledExecutorService = null;
	    			//activityOver = false;
	    		}
	        	annosOk = false;
	        	leaveDialog.show();
	        	Log.e(TAG, "收到PC端工作人员离开消息");
	        }else if(intent.getAction().equals("BACK_INFO")) {
	        	Log.e(TAG, "工作人员回来");
	        	leaveDialog.dismiss();
	        	if(scheduledExecutorService == null) {
	    			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	    			// 当Activity显示出来后，每两秒钟切换一次图片显示
	    			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 4, 4, TimeUnit.SECONDS);
	    		}
	        	annosOk = true;
	        }else if(intent.getAction().equals("COMPLAINT_SUCCESS")) {
	        	leaveDialog.show(intent.getStringExtra("description"), 22, Color.BLACK, 60);
	        }else if(intent.getAction().equals("COMPLAINT_FAIL")) {
	        	String showContent = intent.getStringExtra("description");
	        	if(showContent != null && !showContent.equals(""))
	        		leaveDialog.show(showContent, 36, Color.BLACK, 60);
	        	else
	        		leaveDialog.show("您的投诉未能成功提交，请使用其它投诉方式进行投诉!", 36, Color.BLACK, 60);
	        }else if(intent.getAction().equals("COMPLAINT_RESULT")) {
	        	String description = intent.getStringExtra("description");
	        	leaveDialog.show(description, 22, Color.BLACK, 60);
	        }else if(intent.getAction().equals("TIMEOUT")) {
	        	if(scheduledExecutorService != null) {
	    			scheduledExecutorService.shutdown();
	    			scheduledExecutorService = null;
	    			//activityOver = false;
	    		}
	        	if(leaveDialog != null)
	        		leaveDialog.dismiss();
	        	annosOk = false;
	        	//Toast.makeText(MainActivity.this, "Socket timeout", Toast.LENGTH_SHORT).show();
//	        	if (wakeLock != null && wakeLock.isHeld()) {
//	        		wakeLock.release();
//	        		wakeLock = null;
//	            }
	        	//Log.e(TAG, "time out,release wake lock");
//	        	if(wakeLock == null) {
//		        	wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
//		        	wakeLock.acquire();
//	        	}
//	        	wakeLock.release();
//	        	wakeLock = null;
//	        	if(powerManager != null) {
//	        		powerManager.goToSleep(100);
//	        	}
	        	Intent emptyIntent = new Intent(MainActivity.this, EmptyActivity.class);
	        	context.startActivity(emptyIntent);
	        }else{
	        	//Toast.makeText(MainActivity.this, "Received unexpected intent " + intent.toString(), Toast.LENGTH_SHORT).show();
	        	Log.e(TAG, "Received unexpected intent " + intent.toString());
	        }
		}	
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//屏蔽BACK键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (CommonUtils.isFastDoubleClick()) {  
	        return;
		}
		int tag = (Integer)v.getTag();
//		AccessWeb accessWeb = new AccessWeb(tag);
//		accessWeb.start();
		for(Button button : buttons) {
			button.setEnabled(false);
		}
		if(setting != null)
			setting.setEnabled(false);
		if(tag == 4) {
			Intent i = new Intent(MainActivity.this, InfoCenterActivity.class);
			i.putExtra("account", account);
//			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			MainActivity.this.startActivity(i);
		}else {
			Intent intent = new Intent(MainActivity.this,WebActivity.class);
			intent.putExtra("tag", tag);
			//intent.putExtra("loginId", loginId);
			MainActivity.this.startActivity(intent);
		}
	}
//	private class AccessWeb extends Thread{
//		private int tag;
//		private String url;
//		public AccessWeb(int tag) {
//			this.tag = tag;
//		}
//		
//		@Override
//		public void run() {
//			Intent intent = new Intent(MainActivity.this,WebActivity.class);
//			AccountManager am = new AccountManager(MainActivity.this);
//			String data = "";
//			switch(tag) {
//			case 1:
//				url = "GscSupport.svc/BusinessGuideWebSites?loginId=" + loginId;
//				data = am.getData("url", url);
//				if(data != null)
//		            intent.putExtra("url", data);
//				break;
//			case 2:
//				url = "GscSupport.svc/JobStatements?loginId=" + loginId;
//				data = am.getData("content", url);
//				if(data != null){
//		            intent.putExtra("content", data);
//		            intent.putExtra("title", "岗位职责");
//				}
//				break;
//			case 3:
//				url = "GscSupport.svc/ServicePromises?loginId=" + loginId;
//				data = am.getData("content", url);
//				if(data != null){
//		            intent.putExtra("content", data);
//		            intent.putExtra("title", "服务承诺");
//				}
//				break;
//			case 4:
//				Intent i = new Intent(MainActivity.this, InfoCenterActivity.class);
//				i.putExtra("account", account);
////				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				MainActivity.this.startActivity(i);
//				return;
//			case 5:
//				url = "GscSupport.svc/OfficeCenterWebSites?loginId=" + loginId;
//				data = am.getData("url", url);
//				if(data != null)
//		            intent.putExtra("url", data);
//				break;
//			default:
//				break;
//			}
//			am.close();
////			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            MainActivity.this.startActivity(intent);
//		}
//	}
	//
	private boolean afterNow(String date, String dateFormat) {
		Date time=new Date();
		SimpleDateFormat sd=new SimpleDateFormat(dateFormat);
		try {
			time = sd.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "输入的日期格式有误");
			return false;
		}
		return time.after(new Date());
	}
	private boolean beforeNow(String date, String dateFormat) {
		Date time=new Date();
		SimpleDateFormat sd=new SimpleDateFormat(dateFormat);
		try {
			time = sd.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "输入的日期格式有误");
			return false;
		}
		return time.before(new Date());
	}
//	private class LeaveMessageThread extends Thread {
//		public void run() {
//			while(!activityOver) {
//				WebServiceManager wsm = new WebServiceManager(MainActivity.this);
//				leaveMessage = wsm.getDeviceUserStatus();
//				if(leaveMessage == null)
//					return;
//				String dateFormat = "";
//				if(beforeNow(leaveMessage.getStartDate(), dateFormat) && afterNow(leaveMessage.getEndDate(), dateFormat)) {
//					//请假中
//					leaveMessageHandler.sendEmptyMessage(1);
//				}else{
//					//正常工作中
//					leaveMessageHandler.sendEmptyMessage(2);
//				}
//				try {
//					Thread.sleep(20 * 60 * 1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					continue;
//				}
//			}
//		}
//	}
	private class LeaveMessageThread extends Thread {
		public void run() {
			while(!activityOver) {
				WebServiceManager wsm = new WebServiceManager(MainActivity.this);
				leaveMessage = wsm.getDeviceUserStatus();
				if(leaveMessage == null){
					leaveMessageHandler.sendEmptyMessage(2);
					continue;
				}
				String dateFormat = "yyyy-MM-dd HH:mm";
				if(beforeNow(leaveMessage.getStartDate(), dateFormat) && afterNow(leaveMessage.getEndDate(), dateFormat)) {
					//请假中
					leaveMessageHandler.sendEmptyMessage(1);
				}else{
					//正常工作中
					leaveMessageHandler.sendEmptyMessage(2);
				}
				try {
					Thread.sleep(20 * 60 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	public String getLocalMacAddress() {  
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        String mac = info.getMacAddress();
        if(mac == null)
        	return "";
        return mac.replace(":", "");   
    }
	public void sendComplaint(String name, String tel, String email, String content){
    	Thread sendComplaintThread = new SendComplaintThread(name, tel, email, content);
    	sendComplaintThread.start();
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
			wsm = new WebServiceManager(MainActivity.this);
			complaintResult = wsm.addUserComplaintsPad(name, tel, email, content);
        	dealComplaintResult();
		}
	}
	private void dealComplaintResult() {
		if(complaintResult == null || complaintResult.getStatus() == null){
			Intent intent = new Intent("COMPLAINT_FAIL");
			if(complaintResult != null && !complaintResult.getDescription().equals(""))
				intent.putExtra("description", complaintResult.getDescription());
			MainActivity.this.sendBroadcast(intent);
			return;
		}
		else if(complaintResult.getStatus().equals("1")) {
        	Intent intent = new Intent("COMPLAINT_SUCCESS");
        	intent.putExtra("description", complaintResult.getDescription());
        	MainActivity.this.sendBroadcast(intent);
			getResponse(complaintResult.getKey());
		}else{
			Intent intent = new Intent("COMPLAINT_FAIL");
			intent.putExtra("description", complaintResult.getDescription());
			MainActivity.this.sendBroadcast(intent);
		}
	}
	private void getResponse(String key) {
		Thread getResponseThread = new GetResponseThread(key);
		getResponseThread.start();
	}
	private class GetResponseThread extends Thread{
		private String key;
		public GetResponseThread(String key) {
			this.key = key;
		}
		public void run() {
			int maxTime = Integer.parseInt(complaintResult.getMaxTime());
			String key = complaintResult.getKey();
			//String dateFormat = "yyyy-MM-dd HH:mm";
			boolean threadOver = false;
			for(int i = 0; i < maxTime && !threadOver; i++) {
				
				dealResult = wsm.getComplaintsCheckNotice(key);
				if(dealResult.getStatus().equals("1")){
					Intent intent = new Intent("COMPLAINT_RESULT");
					intent.putExtra("description", dealResult.getDescription());
					MainActivity.this.sendBroadcast(intent);
					threadOver = true;
				}
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void threeButtonDialogDismiss() {
		if(threeButtonDialog != null)
			threeButtonDialog.dismiss();
	}
}