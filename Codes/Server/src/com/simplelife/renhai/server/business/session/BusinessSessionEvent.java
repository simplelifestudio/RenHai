package com.simplelife.renhai.server.business.session;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts.BusinessSessionEventType;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IRunnableMessage;

public class BusinessSessionEvent implements IRunnableMessage
{
	private BusinessSessionEventType event;
	private IDeviceWrapper device;
	private Object operationInfo;
	private long queueTime;
	private String messageSn = CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn);
	private IBusinessSession session;
	
	public BusinessSessionEvent(BusinessSessionEventType event, IDeviceWrapper device, Object operationInfo, IBusinessSession session)
	{
		this.event = event;
		this.device = device;
		this.operationInfo = operationInfo;
		this.session = session;
	}
	
	@Override
	public void run()
	{
		try
		{
			executeEvent();
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
	}
	
	private void executeEvent()
	{
		switch(event)
		{
			case AgreeChat:
				session.onAgreeChat(device);
				break;
			case BindConfirm:
				session.onBindConfirm(device);
				break;
			case ChatMessage:
				String chatMessage = "null";
				if (operationInfo != null)
				{
					chatMessage = operationInfo.toString(); 
				}
				session.onChatMessage(device, chatMessage);
				break;
			case DeviceLeave:
				if (operationInfo instanceof StatusChangeReason)
				{
					StatusChangeReason reason = (StatusChangeReason) operationInfo; 
					session.onDeviceLeave(device, reason);
				}
				else
				{
					BusinessModule.instance.getLogger().error("Fatal error: invalid DeviceLeave event with no leave reason");
				}
				
				break;
			case EndChat:
				session.onEndChat(device);
				break;
			case RejectChat:
				session.onRejectChat(device);
				break;
			case Invalid:
				BusinessModule.instance.getLogger().error("Fatal error: invalid business session event type, session ID: {}", this.getMsgOwnerInfo());
				break;
		}
	}
	
	@Override
	public int getQueueDuration()
	{
		return (int)(System.currentTimeMillis() - queueTime);
	}
	
	@Override
	public void setQueueTime(long now)
	{
		queueTime = now;
	}
	
	@Override
	public String getMessageName()
	{
		return "SessionEvent-" + event.name();
	}
	
	@Override
	public String getMessageSn()
	{
		return messageSn;
	}
	
	@Override
	public String getMsgOwnerInfo()
	{
		return session.getSessionId();
	}
	
	@Override
	public int getDelayOfHandle()
	{
		return 0;
	}

	@Override
	public boolean isSyncMessage()
	{
		return false;
	}
	
}
