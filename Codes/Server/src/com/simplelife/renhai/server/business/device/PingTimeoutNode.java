package com.simplelife.renhai.server.business.device;

import com.simplelife.renhai.server.business.pool.TimeoutLink;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;

public class PingTimeoutNode extends AbstractTimeoutNode
{
	private IDeviceWrapper deviceWrapper;
	public PingTimeoutNode(int timeoutThreshold, IDeviceWrapper deviceWrapper, TimeoutLink ownerLink)
	{
		this(timeoutThreshold, ownerLink);
		this.deviceWrapper = deviceWrapper;
		updateTime();
	}
	
	private PingTimeoutNode(int timeoutThreshold, TimeoutLink ownerLink)
	{
		super(timeoutThreshold, ownerLink);
	}

	@Override
	public void onTimeout()
	{
		deviceWrapper.changeBusinessStatus(DeviceStatus.Disconnected, StatusChangeReason.TimeoutOfPing);
	}
}
