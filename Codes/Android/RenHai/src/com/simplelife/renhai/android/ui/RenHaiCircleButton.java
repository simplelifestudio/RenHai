/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiCircleButton.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-25. 
 */
package com.simplelife.renhai.android.ui;

import com.simplelife.renhai.android.R;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.ImageView;

public class RenHaiCircleButton extends ImageView {
	
	private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 30;	
	private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;	
	private static final int PRESSED_RING_ALPHA = 75;
	
	private Context mContext;	
    private Paint mRadialWidgetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	
    // Members for the flash focus circle
    private Paint mFlashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ObjectAnimator mPressedAnimator;
    private float mAnimationProgress;
	private int mPressedRingWidth;
	private int mPressedRingRadius;	
	
	private RectF mRadialScoreRect;
	
	private int mCurrentValue = 0;
	
	private int mMaxValue = 100;
	
	private float mRadius = 0.0f;
	
	private int mDiameter = 200;
	
	private int mMaxSweepAngle = 360;

	private int mCurrentScoreColorPointer = 0;
	
	private int mPressedColor = Color.parseColor("#7fffd4");
	
	private int mBaseColor = Color.parseColor("#33cc99");
	
	private int mBorderColor = Color.parseColor("#33cc99");
	
	private int mCenterTextColor = Color.WHITE;
	
	private int mSecondaryTextColor = Color.WHITE;
	
	private int mShadowColor = Color.BLACK;
	
	private float mBorderStrokeThickness = 3.0f;
	
	private float mShadowRadius = 4.0f;
	
	private String mSecondaryText = null;
	
	private boolean isShowPercentText = false;
	
	private boolean isTouchEnabled = true;
	
	private float mCenterTextSize = 0.0f;
	
	private float mSecondaryTextSize = 0.0f;
	
	private int readingValuePer = 0;
	
	private int angle = 0;
	
	private int mMinChangeValue = 0;
	
	private int mMaxChangeValue = mMaxValue;
	
	private String mFontName = null;
	
	private OnRadialViewValueChanged mCallback;
	
	public interface OnRadialViewValueChanged {
		public void onValueChanged(int value);
	}
	
	public RenHaiCircleButton(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public RenHaiCircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	public RenHaiCircleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	private void initView() {
		Rect rect = new Rect(0, 0, mDiameter, mDiameter);
		mRadialScoreRect = new RectF(rect); 
		
		this.setFocusable(true);
		this.setScaleType(ScaleType.CENTER_INSIDE);
		setClickable(true);
	
		// Init the flash circle
		mFlashPaint.setStyle(Paint.Style.STROKE);		
		mPressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				            DEFAULT_PRESSED_RING_WIDTH_DIP, getResources().getDisplayMetrics());		
		mFlashPaint.setStrokeWidth(mPressedRingWidth);
		mFlashPaint.setColor(mBorderColor);
		mFlashPaint.setAlpha(PRESSED_RING_ALPHA);
		
		// Init the animation
		final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
		mPressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
		mPressedAnimator.setDuration(pressedAnimationTime);
	}
		
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw the outer circle
		mRadialWidgetPaint.setStyle(Style.STROKE);
		mRadialWidgetPaint.setStrokeWidth(mBorderStrokeThickness * getResources().getDisplayMetrics().density);
		mRadialWidgetPaint.setColor(mBorderColor);
		canvas.drawCircle(getWidth()/ 2, getHeight() / 2, mRadius, mRadialWidgetPaint);	
		mRadialWidgetPaint.setStyle(Style.FILL);
		
		// Draw the flash circle
		canvas.drawCircle(getWidth()/ 2, getHeight() / 2, mPressedRingRadius + mAnimationProgress, mFlashPaint);
	
		// Draw the score radial		
		if(mCurrentValue <= mMaxValue) {
			double sweepAngle = ((mCurrentValue * mMaxSweepAngle) / mMaxValue); //Calculate the arc span
			//Determine the color of the score radial from the given array of colors
			
			readingValuePer = (mCurrentValue * 100) /mMaxValue;
			/*for(int counter = 1; counter <= mScoreColorRange.length; counter++) {
				int colorPer = (counter * 100)/mScoreColorRange.length;
				if(readingValuePer <= colorPer) {
					mCurrentScoreColorPointer = (counter -1);
					break;
				}
			}*/
			// Set the color to the paint and draw the arc
			mRadialWidgetPaint.setColor(mBorderColor);						
			canvas.drawArc(mRadialScoreRect, 270, (float) sweepAngle, true, mRadialWidgetPaint);
			mRadialWidgetPaint.setShadowLayer((float) (mShadowRadius/2) * getResources().getDisplayMetrics().density, 0.0f, 0.0f, mShadowColor);  
			canvas.drawArc(mRadialScoreRect, 270, (float) sweepAngle, true, mRadialWidgetPaint);
			mRadialWidgetPaint.setShadowLayer(mShadowRadius, 0.0f, 0.0f, Color.TRANSPARENT);  
		} else 
			Log.e(this.getClass().getName(), "Current value " + String.valueOf(mCurrentValue) + " greater that maximum value " + String.valueOf(mMaxValue)); 
		
		// Draw the center circle that contains the text information
		mRadialWidgetPaint.setColor(mBaseColor);
		canvas.drawCircle(getWidth()/ 2, getHeight() / 2, (float) (mRadius * .8), mRadialWidgetPaint);
		mRadialWidgetPaint.setShadowLayer(mShadowRadius * getResources().getDisplayMetrics().density, 0.0f, 0.0f, mShadowColor);  
		canvas.drawCircle(getWidth()/ 2, getHeight() / 2, (float) (mRadius * .8), mRadialWidgetPaint);
		mRadialWidgetPaint.setShadowLayer(mShadowRadius, 0.0f, 0.0f, Color.TRANSPARENT);
		
		// Draw the center value text
		mRadialWidgetPaint.setColor(mCenterTextColor);
		mRadialWidgetPaint.setTextSize(mCenterTextSize);		
		if(mFontName != null) 
			mRadialWidgetPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), mFontName));
		float textWidth = 0.0f;
		// Check if the user wants percentage value		
		if(isShowPercentText) {
			textWidth = mRadialWidgetPaint.measureText(String.valueOf(readingValuePer) + "%");
			canvas.drawText(String.valueOf(readingValuePer) + "%", (getWidth()/ 2) - (textWidth/2), (getHeight()/2) + mRadius/8, mRadialWidgetPaint);
		}else{
			textWidth = mRadialWidgetPaint.measureText(mContext.getString(R.string.startvedio_buttontext));
			canvas.drawText(mContext.getString(R.string.startvedio_buttontext), (getWidth()/ 2) - (textWidth/2), (getHeight()/2) + mRadius/8, mRadialWidgetPaint);
		}
		/*else {
			textWidth = mRadialWidgetPaint.measureText(String.valueOf(mCurrentValue));
			canvas.drawText(String.valueOf(mCurrentValue), (getWidth()/ 2) - (textWidth/2), (getHeight()/2) + mRadius/8, mRadialWidgetPaint);
		}*/
		
		// Draw the center secondary text
		if(mSecondaryText != null) {
			mRadialWidgetPaint.setColor(mSecondaryTextColor);
			textWidth = mRadialWidgetPaint.measureText(mSecondaryText);
			mRadialWidgetPaint.setTextSize(mSecondaryTextSize);		
			canvas.drawText(mSecondaryText, (getWidth()/ 2) - (textWidth/5), (getHeight()/2) + mRadius/2, mRadialWidgetPaint);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Determine the diameter and the radius based on device orientation
		if(w > h) {
			mDiameter = h;
			mRadius = mDiameter/2 - (getPaddingTop() + getPaddingBottom());
		} else {
			mDiameter = w;
			mRadius = mDiameter/2 - (getPaddingLeft() + getPaddingRight());
		}
		
		// Determine the focus circle radius
		mPressedRingRadius = mDiameter/2 - mPressedRingWidth - mPressedRingWidth/2;
		
		// Init the draw arc Rect object
		int left   = (getWidth()/2) - (int) mRadius + getPaddingLeft();
		int right  = (getWidth()/2) + (int) mRadius - getPaddingRight();
		int top    = (getHeight()/2) - (int) mRadius + getPaddingTop();
		int bottom = (getHeight()/2) + (int) mRadius - getPaddingBottom();
		Rect rect = new Rect(left, top, right, bottom);
		mRadialScoreRect = new RectF(rect); 
		
		// Init the font size
		mCenterTextSize = mRadius/2;
		mSecondaryTextSize = mRadius/5;
	}
	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isTouchEnabled) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:				
				//setPressed(true);
				break;
				
			case MotionEvent.ACTION_UP:
				//setPressed(false);
				break;
				
			case MotionEvent.ACTION_MOVE:
				angle = getAngleABC(new Point(getWidth()/2, 0),  new Point(getWidth()/2, getHeight()/2), new Point((int) event.getX(), (int) event.getY()));
				//Log.d("Test", "Angle " + angle);
				if((int) ((angle * mMaxValue) / mMaxSweepAngle) > mMinChangeValue && (int) ((angle * mMaxValue) / mMaxSweepAngle) < mMaxChangeValue) {
					setCurrentValue((int) ((angle * mMaxValue) / mMaxSweepAngle));
					if(mCallback != null)
						mCallback.onValueChanged(getCurrentValue());
					invalidate();
				}
				break;
				
			case MotionEvent.ACTION_CANCEL:
				//setPressed(false);
				break;
			}
			return true;
		} else
			return false;
	}*/
	
	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		
		

		if (mRadialWidgetPaint != null) {
			
			if(pressed)
			{
				Log.i("circlebutton","setPressed activated 1!");
				mRadialWidgetPaint.setColor(mPressedColor);
			}				
			else
			{
				Log.i("circlebutton","setPressed activated 2!");
				mRadialWidgetPaint.setColor(mBorderColor);	
			}
						
		}

		/*if (pressed) {
			showPressedRing();
		} else {
			hidePressedRing();
		}*/
	}
	
	public void updatePercent(){
		if(mCallback != null)
			mCallback.onValueChanged(getCurrentValue());
		invalidate();
	}
	
	public void autoShiningRing(){				
		mPressedAnimator.setDuration(1200);		
		mPressedAnimator.setRepeatCount(Animation.INFINITE);
		mPressedAnimator.setRepeatMode(Animation.REVERSE);
		mPressedAnimator.setFloatValues(mAnimationProgress, mPressedRingWidth);
		mPressedAnimator.start();
	}
	
	private void hidePressedRing() {
		mPressedAnimator.setFloatValues(mAnimationProgress, 0f);
		mPressedAnimator.start();
	}
	
	private void showPressedRing() {
		mPressedAnimator.setFloatValues(mAnimationProgress, mPressedRingWidth);
		mPressedAnimator.start();
	}
	
	public float getAnimationProgress() {
		return mAnimationProgress;
	}

	public void setAnimationProgress(float animationProgress) {
		this.mAnimationProgress = animationProgress;
		this.invalidate();
	}
	
	private int getAngleABC( Point a, Point b, Point c ) {
		Point ab = new Point(b.x - a.x, b.y - a.y);
		Point cb = new Point(b.x - c.x, b.y - c.y);
	    float dot = (ab.x * cb.x + ab.y * cb.y); // dot product
	    float cross = (ab.x * cb.y - ab.y * cb.x); // cross product
	    float alpha = (float) Math.atan2(cross, dot);
	    if((int) Math.toDegrees(alpha) < 0) 
	    	return ((int) Math.toDegrees(alpha)) + 360;
	    else
	    	return (int) Math.toDegrees(alpha);
	}
	
	/********************************************************************************************************************
	 * Getter and setter functions
	 ********************************************************************************************************************/
	public void setProgress(int _progress)
	{
		if(_progress > mMaxValue)
		{
			_progress = mMaxValue;
		}
		
		if(_progress <= mMaxValue)
		{
			mCurrentValue = _progress;
			postInvalidate();
		}
	}
	
	public void resetProgress(){
		mCurrentValue = 0;
		postInvalidate();
	}
	/**
	 * @return the mCurrentValue
	 */
	public int getCurrentValue() {
		return mCurrentValue;
	}
	
	public void setOnRadialViewValueChanged(OnRadialViewValueChanged callback) {
		mCallback = callback;
	}

	/**
	 * @param mCurrentValue the mCurrentValue to set
	 */
	public void setCurrentValue(int mCurrentValue) {
		this.mCurrentValue = mCurrentValue;
	}

	/**
	 * @return the mMaxValue
	 */
	public int getMaxValue() {
		return mMaxValue;
	}

	/**
	 * @param mMaxValue the mMaxValue to set
	 */
	public void setMaxValue(int mMaxValue) {
		this.mMaxValue = mMaxValue;
	}

	/**
	 * @return the mBaseColor
	 */
	public int getBaseColor() {
		return mBaseColor;
	}

	/**
	 * @param mBaseColor the mBaseColor to set
	 */
	public void setBaseColor(int mBaseColor) {
		this.mBaseColor = mBaseColor;
	}

	/**
	 * @return the mBorderColor
	 */
	public int getBorderColor() {
		return mBorderColor;
	}

	/**
	 * @param mBorderColor the mBorderColor to set
	 */
	public void setBorderColor(int mBorderColor) {
		this.mBorderColor = mBorderColor;
	}

	/**
	 * @return the mCenterTextColor
	 */
	public int getCenterTextColor() {
		return mCenterTextColor;
	}

	/**
	 * @param mCenterTextColor the mCenterTextColor to set
	 */
	public void setCenterTextColor(int mCenterTextColor) {
		this.mCenterTextColor = mCenterTextColor;
	}

	/**
	 * @return the mSecondaryTextColor
	 */
	public int getSecondaryTextColor() {
		return mSecondaryTextColor;
	}

	/**
	 * @param mSecondaryTextColor the mSecondaryTextColor to set
	 */
	public void setSecondaryTextColor(int mSecondaryTextColor) {
		this.mSecondaryTextColor = mSecondaryTextColor;
	}

	/**
	 * @return the mShadowColor
	 */
	public int getShadowColor() {
		return mShadowColor;
	}

	/**
	 * @param mShadowColor the mShadowColor to set
	 */
	public void setShadowColor(int mShadowColor) {
		this.mShadowColor = mShadowColor;
	}

	/**
	 * @return the mBorderStrokeThickness
	 */
	public float getBorderStrokeThickness() {
		return mBorderStrokeThickness;
	}

	/**
	 * @param mBorderStrokeThickness the mBorderStrokeThickness to set
	 */
	public void setBorderStrokeThickness(float mBorderStrokeThickness) {
		this.mBorderStrokeThickness = mBorderStrokeThickness;
	}

	/**
	 * @return the mShadowRadius
	 */
	public float getShadowRadius() {
		return mShadowRadius;
	}

	/**
	 * @param mShadowRadius the mShadowRadius to set
	 */
	public void setShadowRadius(float mShadowRadius) {
		this.mShadowRadius = mShadowRadius;
	}

	/**
	 * @return the mSecondaryText
	 */
	public String getSecondaryText() {
		return mSecondaryText;
	}

	/**
	 * @param mSecondaryText the mSecondaryText to set
	 */
	public void setSecondaryText(String mSecondaryText) {
		this.mSecondaryText = mSecondaryText;
	}

	/**
	 * @return the isShowPercentText
	 */
	public boolean isShowPercentText() {
		return isShowPercentText;
	}

	/**
	 * @param isShowPercentText the isShowPercentText to set
	 */
	public void setShowPercentText(boolean isShowPercentText) {
		this.isShowPercentText = isShowPercentText;
	}

	/**
	 * @return the mCenterTextSize
	 */
	public float getCenterTextSize() {
		return mCenterTextSize;
	}

	/**
	 * @param mCenterTextSize the mCenterTextSize to set
	 */
	public void setCenterTextSize(float mCenterTextSize) {
		this.mCenterTextSize = mCenterTextSize;
	}

	/**
	 * @return the mSecondaryTextSize
	 */
	public float getSecondaryTextSize() {
		return mSecondaryTextSize;
	}

	/**
	 * @param mSecondaryTextSize the mSecondaryTextSize to set
	 */
	public void setSecondaryTextSize(float mSecondaryTextSize) {
		this.mSecondaryTextSize = mSecondaryTextSize;
	}

	/**
	 * @return the isTouchEnabled
	 */
	public boolean isTouchEnabled() {
		return isTouchEnabled;
	}

	/**
	 * @param isTouchEnabled the isTouchEnabled to set
	 */
	public void setTouchEnabled(boolean isTouchEnabled) {
		this.isTouchEnabled = isTouchEnabled;
	}

	/**
	 * @return the mMinChangeValue
	 */
	public int getMinChangeValue() {
		return mMinChangeValue;
	}

	/**
	 * @param mMinChangeValue the mMinChangeValue to set
	 */
	public void setMinChangeValue(int mMinChangeValue) {
		this.mMinChangeValue = mMinChangeValue;
	}

	/**
	 * @return the mMaxChangeValue
	 */
	public int getMaxChangeValue() {
		return mMaxChangeValue;
	}

	/**
	 * @param mMaxChangeValue the mMaxChangeValue to set
	 */
	public void setMaxChangeValue(int mMaxChangeValue) {
		this.mMaxChangeValue = mMaxChangeValue;
	}
	
	/**
	 * @param mFont
	 */
	public void setFontName(String mFont) {
		mFontName = mFont;
	}
}
