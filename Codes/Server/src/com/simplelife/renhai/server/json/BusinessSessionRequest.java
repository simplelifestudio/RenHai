/**
 * BusinessSessionRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;


import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.OperationType;


/**
 * 
 */
public class BusinessSessionRequest extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public BusinessSessionRequest(JSONObject jsonObject)
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
		
		if (!body.containsKey(JSONKey.BusinessType));
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.BusinessType + " can't be found.");
		}
		
		if (!body.containsKey(JSONKey.OperationType));
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationType + " can't be found.");
		}
		
		String operationType = body.getString(JSONKey.OperationType);
		if (!isValidOperationType(operationType))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription("Invalid " + JSONKey.OperationType + ": " + operationType);
		}
		
		if (!body.containsKey(JSONKey.OperationType));
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationType + " can't be found.");
		}
		return true;
    }
	
	private boolean isValidOperationType(String operationType)
	{
		OperationType[] allTypes = OperationType.values();
		
		for (OperationType type : allTypes)
		{
			if (type.name().equals(operationType))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run()
	{
		if (!checkJSONRequest())
		{
			responseError(Consts.MessageId.BusinessSessionRequest.name());
			return;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AppJSONMessage#getMessageId()
	 */
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionRequest;
	}
}
