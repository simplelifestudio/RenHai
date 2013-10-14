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
		
		if (!body.containsKey(JSONKey.OperationType));
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationType + " can't be found.");
		}
		return true;
    }
	
	@Override
	public void doRun()
	{
		if (!checkJSONRequest())
		{
			responseError(Consts.MessageId.BusinessSessionNotificationResponse);
			return;
		}
		
		int intOperationType = body.getIntValue(JSONKey.OperationType);
		Consts.NotificationType notificationType = Consts.NotificationType.parseValue(intOperationType);
		switch(notificationType)
		{
			case SessionBinded:
				if (deviceWrapper.getOwnerBusinessSession() != null)
				{
					deviceWrapper.getOwnerBusinessSession().onBindConfirm(deviceWrapper);
				}
				else
				{
					logger.error("Device <{}> was in status of SessionBinded but its session is null!", deviceWrapper.getDeviceSn());
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
				logger.error("Invalid operation type received! " + intOperationType + " from device <{}>", deviceWrapper.getDeviceSn());
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
