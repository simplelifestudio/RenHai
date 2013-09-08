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
	
	@Override
	protected boolean checkJsonCommand()
    {
		if (!super.checkJsonCommand())
		{
			return false;
		}
		
		if (!body.containsKey(JSONKey.FieldName.OperationType));
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.FieldName.OperationType + " can't be found.");
		}
		return true;
    }
	
	@Override
	public void run()
	{
		if (!checkJsonCommand())
		{
			responseError(Consts.MessageId.BusinessSessionNotificationResponse.name());
			return;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AppJSONMessage#getMessageId()
	 */
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionNotificationResponse;
	}
}
