package com.evaluation.util;

import com.evaluation.view.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThreeButtonDialog extends Dialog implements View.OnClickListener {
	private static int default_width = 643; //默认宽度
    private static int default_height = 275;//默认高度
    private TextView content;
    private Button button1;
    private Button button2;
    private Button button3;
    //private ImageView cancel;
    public ThreeButtonDialog(Context context, int layout, int style) { 
        this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, layout, style); 
    }

    public ThreeButtonDialog(Context context, int width, int height, int layout, int style) {
        super(context, style);
        //set content
        setContentView(layout);
        //set window params
        content = (TextView) findViewById(R.id.content);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
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
    public void setMessage(String text) {
    	content.setText(text);
    }
    public void setPositiveButton(String text, View.OnClickListener listener) {
    	//button1.setText(text);
    	button1.setOnClickListener(listener);
    }
    public void setNeutralButton(String text, View.OnClickListener listener) {
    	//button2.setText(text);
    	button2.setOnClickListener(listener);
    }
    public void setNegativeButton(String text, View.OnClickListener listener) {
    	//button3.setText(text);
    	button3.setOnClickListener(listener);
    }
    public void show(String text, int textSize, int textColor) {
    	content.setText(text);
    	content.setTextSize(textSize);
    	content.setTextColor(textColor);
    	content.setGravity(Gravity.LEFT);
    	super.show();
    }
    public void show() {
    	content.setText("投诉后受理投诉工作人员将在5分钟内到达现场，是否等待?");
    	//content.setTextSize(36);
    	//content.setGravity(Gravity.CENTER);
    	super.show();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.complaint_dialog);
            //设置标题
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Log.e("effort", "view.id:" + view.getId());
		if(view.getId() != R.id.content)
			this.dismiss();
	}
}
