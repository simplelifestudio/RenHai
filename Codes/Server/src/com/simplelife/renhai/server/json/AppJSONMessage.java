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
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IRunnableMessage;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public abstract class AppJSONMessage extends AbstractJSONMessage
{
    private String errorDescription;
    private Consts.GlobalErrorCode errorCode;
    
    /** */
    protected Consts.MessageId messageId;
    protected String messageSn;
    
    public AppJSONMessage(JSONObject jsonObject)
    {
    	this.jsonObject = jsonObject;
    	
    	if (jsonObject == null)
    	{
    		// It may be null for invalid JSON string
    		return;
    	}
    	
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
    
    public JSONObject getJSONObject()
    {
    	return jsonObject;
    }
    
    public JSONObject getHeader()
    {
    	if (jsonObject == null)
    	{
    		return null;
    	}
    	
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
    		setErrorDescription("Invalid request: " + JSONKey.Header + " can't be found in request");
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
    	
    	if (body.isEmpty())
    	{
    		setErrorDescription("Invalid request: " + JSONKey.Body + " can't be empty.");
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
    	
    	String messageSn = header.getString(JSONKey.MessageSn).trim();
    	if (messageSn.length() == 0)
    	{
    		setErrorDescription("Invalid request: " + JSONKey.MessageSn + " can't be empty.");
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

    protected void responseError()
    {
    	ServerErrorResponse response = createErrorResponse();
    	response.addToBody(JSONKey.ReceivedMessage, this.jsonObject);
    	response.addToBody(JSONKey.ErrorCode, this.errorCode.getValue());
    	response.addToBody(JSONKey.ErrorDescription, this.errorDescription);
    	response.addToHeader(JSONKey.MessageSn, this.getMessageSn());
    	
    	deviceWrapper.prepareResponse(response);
    }

    @Override
	public void run()
	{
    	try
		{
			//Thread.currentThread().setName(messageId.name() + DateUtil.getCurrentMiliseconds());
    		int duration = getQueueDuration();
    		if (duration >= GlobalSetting.BusinessSetting.MessageQueueTime)
    		{
    			logger.warn("Queue duration of message {} is " + duration +"ms, consider increasing size of input message execution thread pool.", getMessageSn());
    		}
			
			if (deviceWrapper == null || !deviceWrapper.getConnection().isOpen())
	    	{
	    		logger.warn("deviceWrapper of message with SN: {} is released, execution of message " + this.messageId.name() + " is given up", this.getMessageSn());
	    		return;
	    	}
			doBeforeRun();
			doRun();
			doAfterRun();
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
	}
	
	public void doBeforeRun()
	{
		/*
		Device device = this.deviceWrapper.getDevice();
		if ((device != null) && (!hibernateSesion.contains(device)))
		{
			Object obj = hibernateSesion.merge(device);
			if (obj == null)
			{
				hibernateSesion.update(device);
			}
			else
			{
				hibernateSesion.update(obj);
			}
			//hibernateSesion.load(Device.class, device.getDeviceId());
		}
		*/
	}
	
	public void doAfterRun()
	{
	}
	
	public abstract void doRun();
	
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
    	
    	return Consts.MessageType.parseValue(header.getIntValue(JSONKey.MessageType)); 
    }
    

    public String getMessageSn()
    {
    	if (messageSn != null)
    	{
    		return messageSn;
    	}
    	
    	if (getHeader() == null)
    	{
    		return null;
    	}
    	
    	if (!header.containsKey(JSONKey.MessageSn))
    	{
    		return null;
    	}
    	
    	messageSn = header.getString(JSONKey.MessageSn);
    	return messageSn; 
    }
    
    
    public IDeviceWrapper getDeviceWrapper()
    {
    	return deviceWrapper;
    }
    
    protected boolean checkNoEmptyAllowed(JSONObject obj, String fieldName)
	{
		if (header.containsKey(fieldName))
		{
			if (obj.get(fieldName) == null)
			{
				String temp = fieldName + " can't be empty.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
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
