package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.Executors;

import com.simplelife.renhai.server.util.GlobalSetting;

public class InputMsgExecutorPool extends AbstractMsgExecutorPool
{
	public final static InputMsgExecutorPool instance = new InputMsgExecutorPool();
	
	private InputMsgExecutorPool()
	{
	}
	
	@Override
	public void startService()
	{
		executeThreadPool = Executors.newFixedThreadPool(GlobalSetting.BusinessSetting.InputMessageHandleThreads);
	}
}
