package com.evaluation.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class VerticalScrollTextView extends TextView {
	private float step = 0f;
	private Paint mPaint;
	private String text;
	private float width;
	private List<String> textList = new ArrayList<String>(); // 分行保存textview的显示信息。

	public VerticalScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VerticalScrollTextView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setFocusable(true);
		//这里主要处理如果没有传入内容显示的默认值
//		if(textList==null){
//			textList=new ArrayList<String>();
//			Notice sen=new Notice(0,"暂时没有通知公告");
//			list.add(0, sen);
//		}		
		//普通文字的字号，以及画笔颜色的设置
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(16);
		mPaint.setColor(Color.BLACK);
		mPaint.setTypeface(Typeface.SERIF);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}
		float length = 0;
		if (text == null || text.length() == 0) {
			return;
		}
		// 下面的代码是根据宽度和字体大小，来计算textview显示的行数。
		textList.clear();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			Log.e("textviewscroll", "" + i + text.charAt(i));

			if (length < width) {

				builder.append(text.charAt(i));
				length += mPaint.measureText(text.substring(i, i + 1));
				if (i == text.length() - 1) {
					Log.e("textviewscroll", "" + i + text.charAt(i));
					textList.add(builder.toString());
				}
			} else {
				textList.add(builder.toString().substring(0,
						builder.toString().length() - 1));
				builder.delete(0, builder.length() - 1);
				length = mPaint.measureText(text.substring(i, i + 1));
				i--;
			}
		}
	}
	// 下面代码是利用上面计算的显示行数，将文字画在画布上，实时更新。
	@Override
	public void onDraw(Canvas canvas) {
		for (int i = 0; i < textList.size(); i++) {
			canvas.drawText(textList.get(i), 0, this.getHeight() + (i + 1)
					* mPaint.getTextSize() - step, getPaint());
		}
		invalidate();
		step = step + 0.3f;
		if (step >= this.getHeight() + textList.size() * mPaint.getTextSize()) {
			step = 0;
		}
	}
	public void setText(String text) {
		this.text = text;
		super.setText(text);
	}
}
