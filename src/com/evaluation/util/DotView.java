package com.evaluation.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.*;
import android.graphics.RectF;
import android.view.View;

@SuppressLint("DrawAllocation")
public class DotView extends View {
	Paint vPaint = new Paint(); 
	private int i = 0; 

	public DotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	protected void onDraw(Canvas canvas) { 
		super.onDraw(canvas); 
		//System.out.println("this run " + (times++) +" times!"); 
		// 设定绘图样式  
		vPaint.setColor( 0x000000 ); //画笔颜色  
		vPaint.setAntiAlias( true );   //反锯齿
		vPaint.setStyle( Paint.Style.FILL );
		// 绘制一个弧形  
		//canvas.drawCircle(10, 10, 360, vPaint);
		RectF oval = new RectF(20, 20, 20, 20);
		canvas.drawOval(oval, vPaint);
	}
}
