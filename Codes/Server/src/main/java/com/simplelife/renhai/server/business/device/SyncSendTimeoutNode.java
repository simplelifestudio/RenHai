package com.simplelife.renhai.server.business.device;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;

public class SyncSendTimeoutNode extends AbstractTimeoutNode
{
	private String messageSn;
	private IDeviceWrapper deviceWrapper;
	private Logger logger = BusinessModule.instance.getLogger();
	
	public SyncSendTimeoutNode(int timeoutThreshold, IDeviceWrapper deviceWrapper, String messageSn)
	{
		super(timeoutThreshold);
		this.deviceWrapper = deviceWrapper;
		this.messageSn = messageSn;
		updateTime();
	}

	@Override
	public void onTimeout()
	{
		/*
		if (logger.isWarnEnabled())
		{
			String temp = "Device <"+ deviceWrapper.getDeviceIdentification() +"> has no response for message with SN: " + messageSn;
			logger.warn(temp);
		}
		AppJSONMessage request = new TimeoutRequest(messageSn);
		deviceWrapper.onJSONCommand(request);
		*/
		deviceWrapper.changeBusinessStatus(DeviceStatus.Disconnected, StatusChangeReason.TimeoutOnSyncSending);
	}
	
	public boolean isWaiting(String messageSn)
	{
		return this.messageSn.equals(messageSn);
	}
}
