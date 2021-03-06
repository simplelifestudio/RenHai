/**
 * BusinessSessionNotificationResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessSessionEventType;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class BusinessSessionNotificationResponse extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public BusinessSessionNotificationResponse(JSONObject jsonObject)
	{
		super(jsonObject);
		messageId = Consts.MessageId.BusinessSessionNotificationResponse;
	}
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
    {
		if (!super.checkJSONRequest())
		{
			return false;
		}
		
		if (!body.containsKey(JSONKey.OperationType))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationType + " can't be found.");
			return false;
		}
		
		if (deviceWrapper.getBusinessStatus() != DeviceStatus.SessionBound)
		{
			setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			setErrorDescription("The business session has been released");
			return false;
		}
		
		return true;
    }
	
	@Override
	public void doRun()
	{
		if (!checkJSONRequest())
		{
			responseError();
			return;
		}
		
		int intOperationType = body.getIntValue(JSONKey.OperationType);
		Consts.NotificationType notificationType = Consts.NotificationType.parseValue(intOperationType);
		logger.debug("Received confirm of " + notificationType.name() 
				+ " from Device <" + deviceWrapper.getDeviceIdentification() 
				+ ">, session status: {}"
				, deviceWrapper.getOwnerBusinessSession().getStatus().name());
		switch(notificationType)
		{
			case SessionBound:
				if (deviceWrapper.getOwnerBusinessSession() != null)
				{
					//deviceWrapper.getOwnerBusinessSession().onBindConfirm(deviceWrapper);
					deviceWrapper.getOwnerBusinessSession().newEvent(BusinessSessionEventType.BindConfirm, deviceWrapper, null);
				}
				else
				{
					logger.error("Fatal error: Device <{}> was in status of SessionBound but its session is null!", deviceWrapper.getDeviceIdentification());
				}
				break;
			case OthersideAgreed:
				break;
			case OthersideEndChat:
				break;
			case OthersideLost:
				break;
			case OthersideRejected:
				break;
			case Invalid:
				logger.error("Invalid operation type received! " + intOperationType + " from device <{}>", deviceWrapper.getDeviceIdentification());
			default:
				break;
		}
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionNotificationResponse;
	}
}
