/**
 * AppJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.MessageId;


/** */
public class AppJSONMessage extends AbstractJSONMessage
{
    private String errorDescription;
    private Consts.GlobalErrorCode errorCode;
    protected PrintWriter out;
    
    /** */
    protected Consts.MessageId messageId;
    protected String messageSn;
    
    public AppJSONMessage(JSONObject jsonObject, PrintWriter out)
    {
    	this.out = out;
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
    
    public Consts.MessageId getMessageId()
    {
    	return MessageId.Invalid;
    }
    
    
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
    	
    	/*
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
    	*/
    
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
    	ServerErrorResponse reponse = new ServerErrorResponse(this, out);
    	return reponse;
    }

    public void responseError()
    {
    	ServerErrorResponse response = createErrorResponse();
    	response.addToBody(JSONKey.ReceivedMessage, this.jsonObject);
    	response.addToBody(JSONKey.ErrorCode, this.errorCode.getValue());
    	response.addToBody(JSONKey.ErrorDescription, this.errorDescription);
    	response.addToHeader(JSONKey.MessageSn, this.getMessageSn());
    	
    	//OutputMessageCenter.instance.addMessage(response);
    }

    @Override
	public void run()
	{
    	try
		{
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
	}
	
	public void doAfterRun()
	{
	}
	
	public void doRun()
	{
		
	}
	
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
