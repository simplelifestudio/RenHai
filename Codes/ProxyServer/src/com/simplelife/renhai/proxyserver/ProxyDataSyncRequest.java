/**
 * ProxyRequest.java
 * 
 * History:
 *     2013-11-17: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.MessageId;
import com.simplelife.renhai.proxyserver.Consts.MessageType;
import com.simplelife.renhai.proxyserver.Consts.ServiceStatus;

/**
 * 
 */
public class ProxyDataSyncRequest extends AppJSONMessage
{
	public ProxyDataSyncRequest(JSONObject jsonObject, PrintWriter out)
	{
		super(jsonObject, out);
		messageId = Consts.MessageId.ProxyDataSyncRequest;
	}
	
	@Override
	public MessageType getMessageType()
	{
		return MessageType.AppRequest;
	}
	
	@Override
	public MessageId getMessageId()
	{
		return MessageId.ProxyDataSyncRequest;
	}
	
	@Override
	public boolean checkJSONRequest()
	{
		return true;
	}

	private JSONObject getAddressJSON()
	{
		JSONObject addrObj = new JSONObject();
		addrObj.put(JSONKey.Ip, GlobalSetting.instance.getIpAddress());
		addrObj.put(JSONKey.Port, GlobalSetting.instance.getPort());
		addrObj.put(JSONKey.Path, GlobalSetting.instance.getPath());
		addrObj.put(JSONKey.Protocol, GlobalSetting.instance.getProtocol());
		return addrObj;
	}
	
	private JSONObject getServiceStatus()
	{
		JSONObject statusObj = new JSONObject();
		statusObj.put(JSONKey.TimeZone, GlobalSetting.instance.getTimeZone());
		statusObj.put(JSONKey.BeginTime, GlobalSetting.instance.getBeginTime());
		statusObj.put(JSONKey.EndTime, GlobalSetting.instance.getEndTime());
		return statusObj;
	}
	
	@Override
	public void doRun()
	{
		logger.debug("Start to execute ProxyDataSyncRequest");
		
		try
		{
			ServiceStatus status = GlobalSetting.instance.getServiceStatus();
			
			ProxyDataSyncResponse response = new ProxyDataSyncResponse(this, this.out);
			String zone = GlobalSetting.instance.getTimeZone();
			Date begin = DateUtil.getDateByTimeZoneDateString(GlobalSetting.instance.getBeginTime(), zone);
			Date end = DateUtil.getDateByTimeZoneDateString(GlobalSetting.instance.getEndTime(), zone);
			
			if (status == ServiceStatus.Normal || System.currentTimeMillis() > end.getTime())
			{
				// Normal status, only provide serviceAddress
				response.addToBody(JSONKey.ServiceStatus, ServiceStatus.Normal.getValue());
				response.addToBody(JSONKey.ServiceAddress, getAddressJSON());
				response.addToBody(JSONKey.StatusPeriod, null);
			}
			else
			{
				if (System.currentTimeMillis() < begin.getTime())
				{
					// to be maintained
					response.addToBody(JSONKey.ServiceStatus, ServiceStatus.Maintenance.getValue());
					response.addToBody(JSONKey.ServiceAddress, getAddressJSON());
					response.addToBody(JSONKey.StatusPeriod, getServiceStatus());
				}
				else
				{
					// under maintenance
					response.addToBody(JSONKey.ServiceStatus, ServiceStatus.Maintenance.getValue());
					response.addToBody(JSONKey.ServiceAddress, null);
					response.addToBody(JSONKey.StatusPeriod, getServiceStatus());
				}
			}
			
			response.run();
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return;
		}
	}
	
}
