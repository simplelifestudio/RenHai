/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiImageView.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android.ui;

import android.content.Context;
import android.widget.ImageView;

public class RenHaiImageView extends ImageView {
	
	public String mText;

	public RenHaiImageView(Context context) {
		super(context);
	}
	
	public void setText(String _text) {
		mText = _text;
	}
	
	public String getText() {
		return mText;
	}

}
