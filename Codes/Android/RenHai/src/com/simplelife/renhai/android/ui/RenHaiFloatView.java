/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiFloatView.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class RenHaiFloatView extends ImageView {
	private float startX; // the first pointer index of x coordinate
	private float startY; // the first pointer index of Y coordinate
	private float x;// the coordinate of X
	private float y;// the coordinate of Y

	private WindowManager mWindowManager = (WindowManager) getContext().getApplicationContext()
			                               .getSystemService(getContext().WINDOW_SERVICE);
	private WindowManager.LayoutParams mWinMLayoutParam = ((RenHaiApplication) getContext()
			                               .getApplicationContext()).getLayoutParams();

	public RenHaiFloatView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// To get the coordinate on the screen
		x = event.getRawX();
		y = event.getRawY() - 25; // 25 the height of the status bar
		switch (event.getAction()) {
		// begin to move the view
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			updateViewPosition();
			startX = startY = 0;
			break;
		}
		return true;
	}

	private void updateViewPosition() {
		// To change the position of the float view
		mWinMLayoutParam.x = (int) (x - startX);
		mWinMLayoutParam.y = (int) (y - startY);
		mWindowManager.updateViewLayout(this, mWinMLayoutParam);
	}

}
