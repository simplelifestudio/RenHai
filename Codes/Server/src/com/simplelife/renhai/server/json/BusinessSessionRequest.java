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
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.JSONKey.OperationType;


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
	
	@Override
	protected boolean checkJsonCommand()
    {
		if (!super.checkJsonCommand())
		{
			return false;
		}
		
		if (!body.containsKey(JSONKey.FieldName.BusinessType));
		{
			errorCode = 1103;
			errorDescription = JSONKey.FieldName.BusinessType + " can't be found.";
		}
		
		if (!body.containsKey(JSONKey.FieldName.OperationType));
		{
			errorCode = 1103;
			errorDescription = JSONKey.FieldName.OperationType + " can't be found.";
		}
		
		String operationType = body.getString(JSONKey.FieldName.OperationType);
		if (!isValidOperationType(operationType))
		{
			errorCode = 1103;
			errorDescription = "Invalid " + JSONKey.FieldName.OperationType + ": " + operationType;
		}
		
		if (!body.containsKey(JSONKey.FieldName.OperationType));
		{
			errorCode = 1103;
			errorDescription = JSONKey.FieldName.OperationType + " can't be found.";
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
		if (!checkJsonCommand())
		{
			responseError();
			return;
		}
		
	}
}
