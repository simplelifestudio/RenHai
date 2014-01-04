package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.Executors;

import com.simplelife.renhai.server.util.GlobalSetting;

public class OutputMsgExecutorPool extends AbstractMsgExecutorPool
{
	public final static OutputMsgExecutorPool instance = new OutputMsgExecutorPool();
	
	private OutputMsgExecutorPool()
	{
	}
	
	@Override
	public void startService()
	{
		//executeThreadPool = Executors.newFixedThreadPool(GlobalSetting.BusinessSetting.OutputMessageSendThreads);
	}
}
