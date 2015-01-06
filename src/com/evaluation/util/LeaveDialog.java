package com.evaluation.util;

import com.evaluation.view.R;
import android.app.Dialog;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaveDialog extends Dialog {
	private static int default_width = 643; //默认宽度
    private static int default_height = 275;//默认高度
    private TextView content;
    private ImageView cancel;
    public LeaveDialog(Context context, int layout, int style) { 
        this(context, default_width, default_height, layout, style); 
    }

    public LeaveDialog(Context context, int width, int height, int layout, int style) {
        super(context, style);
        //set content
        setContentView(layout);
        //set window params
        content = (TextView) findViewById(R.id.content);
        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				LeaveDialog.this.dismiss();
			}
        	
        });
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        float density = getDensity(context);
        params.width = (int) (width*density);
        params.height = (int) (height*density);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
       return dm.density;
    }
    public void show(String text, int textSize, int textColor, int dismissTime) {
    	cancel.setVisibility(View.VISIBLE);
    	content.setText(text);
    	content.setTextSize(textSize);
    	content.setTextColor(textColor);
    	content.setGravity(Gravity.LEFT);
    	Thread dismissThread = new DismissThread(dismissTime);
    	dismissThread.start();
    	super.show();
    }
    public void show() {
    	cancel.setVisibility(View.INVISIBLE);
    	content.setText("工作人员暂时离开\r\n请稍等...");
    	content.setTextSize(36);
    	content.setGravity(Gravity.CENTER);
    	super.show();
    }
    private class DismissThread extends Thread{
    	private int time;
    	public DismissThread(int time) {
    		this.time = time;
    	}
    	public void run() {
			try {
				Thread.sleep(time * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LeaveDialog.this.dismiss();
		}
    }
}
