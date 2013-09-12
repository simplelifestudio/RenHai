/**
 * AppJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IAppJSONMessage;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public abstract class AppJSONMessage extends AbstractJSONMessage implements Runnable, IAppJSONMessage
{
    private String errorDescription;
    private Consts.GlobalErrorCode errorCode;
    
    /** */
    protected JSONObject jsonObject;
    protected JSONObject header;
    protected JSONObject body;
    protected Consts.MessageId messageId;
    
    public AppJSONMessage(JSONObject jsonObject)
    {
    	Logger logger = JSONModule.instance.getLogger();
    	this.jsonObject = jsonObject;
    	
    	if (jsonObject.containsKey(JSONKey.Header))
    	{
    		header = jsonObject.getJSONObject(JSONKey.Header);
    	}
    	else
    	{
    		logger.error("Trying to create APP JSON request bases on invalid string: " + jsonObject.toJSONString());
    	}
    	
    	if (jsonObject.containsKey(JSONKey.Body))
    	{
    		body = jsonObject.getJSONObject(JSONKey.Body);
    	}
    	else
    	{
    		logger.error("Trying to create APP JSON request bases on invalid string: " + jsonObject.toJSONString());
    	}
    }
    
    public JSONObject getHeader()
    {
    	if (header == null)
    	{
    		header = jsonObject.getJSONObject(JSONKey.Header);
    	}
    	return header;
    }
    
    public JSONObject getBody()
    {
    	if (body == null)
    	{
    		body = jsonObject.getJSONObject(JSONKey.Body);
    	}
    	return body;
    }
    
    public void bindDeviceWrapper(IDeviceWrapper deviceWrapper)
    {
    	this.deviceWrapper = deviceWrapper;
    }
    
    public abstract Consts.MessageId getMessageId();
    
    
	/**
	 * Check if JSON request is valid
	 */
	protected boolean checkJSONRequest()
    {
    	Logger logger = JSONModule.instance.getLogger();
    	
    	if (jsonObject == null)
		{
    		setErrorDescription("Empty JSON Object is attached in request");
    		setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
    		logger.error(errorDescription);
			return false;
		}
    	
    	JSONObject header = getHeader();
    	if (header == null)
    	{
    		setErrorDescription("Invalid request: " + JSONKey.Header+ " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (getBody() == null)
    	{
    		setErrorDescription("Invalid request: " + JSONKey.Body + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    
    	if (!header.containsKey(JSONKey.MessageType))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.MessageType + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.MessageSn))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.MessageSn + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.MessageId))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.MessageId + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	String messageId = header.getString(JSONKey.MessageId); 
    	if (!CommonFunctions.IsNumric(messageId))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.MessageId + " must be numric");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	/*
    	if (!header.containsKey(JSONKey.DeviceId))
    	{
    		errorMessage = "Invalid request: " + JSONKey.DeviceId + " can't be found in request";
    		return false;
    	}
    	*/
    	
    	if (!header.containsKey(JSONKey.DeviceSn))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.DeviceSn + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.TimeStamp))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.TimeStamp + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	return true;
    }
    
    
    protected ServerErrorResponse createErrorResponse()
    {
    	ServerErrorResponse reponse = new ServerErrorResponse(this);
    	return reponse;
    }

    protected void responseError(String messageId)
    {
    	ServerErrorResponse response = createErrorResponse();
    	response.addToBody(JSONKey.ReceivedMessage, messageId);
    	response.addToBody(JSONKey.ErrorCode, this.errorCode);
    	response.addToBody(JSONKey.ErrorDescription, this.errorDescription);
    	response.asyncResponse();
    }

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AbstractJSONMessage#run()
	 */
	public abstract void run();
	
	
	public void setErrorCode(Consts.GlobalErrorCode errorCode)
	{
		this.errorCode = errorCode;
	}
	
	public void setErrorDescription(String errorDescription)
	{
		this.errorDescription = errorDescription;
	}
	
	public Consts.GlobalErrorCode getErrorCode()
	{
		return errorCode;
	}
	
	public String getErrorDescription()
	{
		return errorDescription;
	}
	
    public Consts.MessageType getMessageType()
    {
    	if (getHeader() == null)
    	{
    		return Consts.MessageType.Invalid;
    	}
    	
    	if (!header.containsKey(JSONKey.MessageType))
    	{
    		return Consts.MessageType.Invalid;
    	}
    	
    	return Consts.MessageType.getEnumItemByValue(header.getIntValue(JSONKey.MessageType)); 
    }
    

    public String getMessageSn()
    {
    	if (getHeader() == null)
    	{
    		return "";
    	}
    	
    	if (!header.containsKey(JSONKey.MessageType))
    	{
    		return "";
    	}
    	
    	return header.getString(JSONKey.MessageSn); 
    }
    
    
    public IDeviceWrapper getDeviceWrapper()
    {
    	return deviceWrapper;
    }
    
    protected boolean checkNoEmptyAllowed(JSONObject obj, String fieldName)
	{
		if (header.containsKey(JSONKey.OsVersion))
		{
			String fieldValue = obj.getString(fieldName);
			
			if (fieldValue.trim().length() == 0)
			{
				String temp = fieldName + " can't be empty.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
		}
		return true;
	}
}
