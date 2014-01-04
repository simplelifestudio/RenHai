package com.simplelife.renhai.server.business.device;

import com.simplelife.renhai.server.business.pool.TimeoutLink;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;

public class PingTimeoutNode extends AbstractTimeoutNode
{
	private IDeviceWrapper deviceWrapper;
	public PingTimeoutNode(int timeoutThreshold, IDeviceWrapper deviceWrapper)
	{
		this(timeoutThreshold);
		this.deviceWrapper = deviceWrapper;
		updateTime();
	}
	
	private PingTimeoutNode(int timeoutThreshold)
	{
		super(timeoutThreshold);
	}

	@Override
	public void onTimeout()
	{
		deviceWrapper.changeBusinessStatus(DeviceStatus.Disconnected, StatusChangeReason.TimeoutOfPing);
	}
}
