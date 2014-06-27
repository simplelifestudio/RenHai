/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiTimerHelper.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-18. 
 */
package com.simplelife.renhai.android.timer;

import java.util.Timer;
import java.util.TimerTask;

public class RenHaiTimerHelper {
    
    private RenHaiTimerProcessor mProcessor;
    
    private int mDelayMs;
    
    private int mPeriod;
    
    private Timer mTimer;
    
    private TimerTask mTimerTask;
    
    public RenHaiTimerHelper(int _delayMs, RenHaiTimerProcessor _processor) {
        mProcessor = _processor;
        mDelayMs   = _delayMs;
    }
    
    public RenHaiTimerHelper(int _delayMs, int _period, RenHaiTimerProcessor _processor) {
        mProcessor = _processor;
        mDelayMs   = _delayMs;
        mPeriod    = _period;
    }
    
    public void startTimer() {
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (mProcessor != null) {
                    mProcessor.onTimeOut();
                }
            }            
        };
        
        mTimer.schedule(mTimerTask, mDelayMs);
    }
    
    public void startRepeatTimer() {
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (mProcessor != null) {
                    mProcessor.onTimeOut();
                }
            }            
        };
        
        mTimer.schedule(mTimerTask, mDelayMs, mPeriod);
    }
    
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
    
    public void resetTimer(){
    	if (mTimer != null)
    		stopTimer();
    	startTimer();
    }

}