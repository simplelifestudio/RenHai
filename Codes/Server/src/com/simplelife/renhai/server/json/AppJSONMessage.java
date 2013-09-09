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
    	this.jsonObject = jsonObject;
    }
    
    public JSONObject getHeader()
    {
    	if (header == null)
    	{
    		header = jsonObject.getJSONObject(JSONKey.FieldName.Header);
    	}
    	return header;
    }
    
    public JSONObject getBody()
    {
    	if (body == null)
    	{
    		body = jsonObject.getJSONObject(JSONKey.FieldName.Body);
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
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.Header+ " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (getBody() == null)
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.Body + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    
    	if (!header.containsKey(JSONKey.FieldName.MessageType))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.MessageType + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageSn))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.MessageSn + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageId))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.MessageId + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	String messageId = header.getString(JSONKey.FieldName.MessageId); 
    	if (!CommonFunctions.IsNumric(messageId))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.MessageId + " must be numric");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	/*
    	if (!header.containsKey(JSONKey.FieldName.DeviceId))
    	{
    		errorMessage = "Invalid request: " + JSONKey.FieldName.DeviceId + " can't be found in request";
    		return false;
    	}
    	*/
    	
    	if (!header.containsKey(JSONKey.FieldName.DeviceSn))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.DeviceSn + " can't be found in request");
    		setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
    		logger.error(errorDescription);
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.TimeStamp))
    	{
    		setErrorDescription("Invalid request: " + JSONKey.FieldName.TimeStamp + " can't be found in request");
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
    	response.addToBody(JSONKey.FieldName.ReceivedMessage, messageId);
    	response.addToBody(JSONKey.FieldName.ErrorCode, this.errorCode);
    	response.addToBody(JSONKey.FieldName.ErrorDescription, this.errorDescription);
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
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageType))
    	{
    		return Consts.MessageType.Invalid;
    	}
    	
    	return Consts.MessageType.getEnumItemByValue(header.getIntValue(JSONKey.FieldName.MessageType)); 
    }
    

    public String getMessageSn()
    {
    	if (getHeader() == null)
    	{
    		return "";
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageType))
    	{
    		return "";
    	}
    	
    	return header.getString(JSONKey.FieldName.MessageSn); 
    }
    
    
    public IDeviceWrapper getDeviceWrapper()
    {
    	return deviceWrapper;
    }
}
