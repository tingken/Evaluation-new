package com.romainpiel.shimmer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Shimmer
 * User: romainpiel
 * Date: 06/03/2014
 * Time: 10:19
 *
 * Shimmering TextView
 * Dumb class wrapping a ShimmerViewHelper
 */
public class ShimmerTextView extends TextView/* implements ShimmerViewBase*/ {
	
	private LinearGradient mLinearGradient;  
    private Matrix mGradientMatrix;  
    private Paint mPaint;  
    private int mViewWidth = 0;  
    private int mTranslate = 0;  
  
    private boolean mAnimating = true;  
  
    public ShimmerTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
	 @Override  
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
	        super.onSizeChanged(w, h, oldw, oldh);  
	        if (mViewWidth == 0) {  
	            mViewWidth = getMeasuredWidth();  
	            if (mViewWidth > 0) {  
	                mPaint = getPaint();  
	                mLinearGradient = new LinearGradient(-mViewWidth, 0, 0, 0,  
	                        new int[] { 0xffffffff, 0xff000000, 0xffffffff },  
	                        new float[] { 0, 0.5f, 1 }, TileMode.CLAMP);  
	                mPaint.setShader(mLinearGradient);  
	                mGradientMatrix = new Matrix();  
	            }  
	        }  
	    }  
	  
	    @Override  
	    protected void onDraw(Canvas canvas) {  
	        super.onDraw(canvas);  
	        if (mAnimating && mGradientMatrix != null) {  
	            mTranslate += mViewWidth / 10;  
	            if (mTranslate > 2 * mViewWidth) {  
	                mTranslate = -mViewWidth;  
	            }  
	            mGradientMatrix.setTranslate(mTranslate, 0);  
	            mLinearGradient.setLocalMatrix(mGradientMatrix);  
	            postInvalidateDelayed(50);  
	        }  
	    }
//    private ShimmerViewHelper shimmerViewHelper;
//
//    public ShimmerTextView(Context context) {
//        super(context);
//        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), null);
//        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
//    }
//
//    public ShimmerTextView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
//        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
//    }
//
//    public ShimmerTextView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
//        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
//    }
//
//    @Override
//    public float getGradientX() {
//        return shimmerViewHelper.getGradientX();
//    }
//
//    @Override
//    public void setGradientX(float gradientX) {
//        shimmerViewHelper.setGradientX(gradientX);
//    }
//
//    @Override
//    public boolean isShimmering() {
//        return shimmerViewHelper.isShimmering();
//    }
//
//    @Override
//    public void setShimmering(boolean isShimmering) {
//        shimmerViewHelper.setShimmering(isShimmering);
//    }
//
//    @Override
//    public boolean isSetUp() {
//        return shimmerViewHelper.isSetUp();
//    }
//
//    @Override
//    public void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback) {
//        shimmerViewHelper.setAnimationSetupCallback(callback);
//    }
//
//    @Override
//    public int getPrimaryColor() {
//        return shimmerViewHelper.getPrimaryColor();
//    }
//
//    @Override
//    public void setPrimaryColor(int primaryColor) {
//        shimmerViewHelper.setPrimaryColor(primaryColor);
//    }
//
//    @Override
//    public int getReflectionColor() {
//        return shimmerViewHelper.getReflectionColor();
//    }
//
//    @Override
//    public void setReflectionColor(int reflectionColor) {
//        shimmerViewHelper.setReflectionColor(reflectionColor);
//    }
//
//    @Override
//    public void setTextColor(int color) {
//        super.setTextColor(color);
//        if (shimmerViewHelper != null) {
//            shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
//        }
//    }
//
//    @Override
//    public void setTextColor(ColorStateList colors) {
//        super.setTextColor(colors);
//        if (shimmerViewHelper != null) {
//            shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
//        }
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (shimmerViewHelper != null) {
//            shimmerViewHelper.onSizeChanged();
//        }
//    }
//
//    @Override
//    public void onDraw(Canvas canvas) {
//        if (shimmerViewHelper != null) {
//            shimmerViewHelper.onDraw();
//        }
//        super.onDraw(canvas);
//    }
}
