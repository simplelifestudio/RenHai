/**
 * ServerDataSyncRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import java.util.LinkedList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.InterestBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class ServerDataSyncRequest extends AppJSONMessage
{
	
	/**
	 * @param messageObject
	 */
	public ServerDataSyncRequest(JSONObject messageObject)
	{
		super(messageObject);
	}
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
    {
		if (body.containsKey(JSONKey.CurrentHotLabels))
		{
			String labelCount = body.getString(JSONKey.CurrentHotLabels);
			if (labelCount.length() > 0)
			{
				if (!CommonFunctions.IsNumric(labelCount))
				{
					this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
					this.setErrorDescription(JSONKey.CurrentHotLabels + " must be numric.");
					return false;
				}
			}
		}
		return true;
    }
	
	@Override
	public void run()
	{
		if (!checkJSONRequest())
		{
			responseError(Consts.MessageId.ServerDataSyncRequest.name());
			return;
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.ServerDataSyncResponse);
		AbstractBusinessDevicePool randomPool = OnlineDevicePool.instance.getBusinessPool(Consts.BusinessType.Random);
		InterestBusinessDevicePool interestPool = (InterestBusinessDevicePool) OnlineDevicePool.instance.getBusinessPool(Consts.BusinessType.Interest);
		
		if (body.containsKey(JSONKey.DeviceInOnlinePool))
		{
			response.addToBody(JSONKey.DeviceInOnlinePool, String.valueOf(OnlineDevicePool.instance.getElementCount()));
		}
		
		if (body.containsKey(JSONKey.CapacityOfOnlinePool))
		{
			response.addToBody(JSONKey.CapacityOfOnlinePool, String.valueOf(OnlineDevicePool.instance.getCapacity()));
		}
		
		if (body.containsKey(JSONKey.DeviceInRandomPool))
		{
			response.addToBody(JSONKey.DeviceInRandomPool, String.valueOf(randomPool.getElementCount()));
		}
		
		if (body.containsKey(JSONKey.CapacityOfRandomPool))
		{
			response.addToBody(JSONKey.CapacityOfRandomPool, String.valueOf(randomPool.getCapacity()));
		}
		
		if (body.containsKey(JSONKey.DeviceInInterestPool))
		{
			
			response.addToBody(JSONKey.DeviceInInterestPool, String.valueOf(interestPool.getCapacity()));
		}
		
		if (body.containsKey(JSONKey.CapacityOfInterestPool))
		{
			response.addToBody(JSONKey.CapacityOfInterestPool, String.valueOf(interestPool.getCapacity()));
		}
		
		if (body.containsKey(JSONKey.DeviceInChat))
		{
			response.addToBody(JSONKey.DeviceInChat, String.valueOf(OnlineDevicePool.instance.getDeviceCountInChat()));
		}
		
		if (body.containsKey(JSONKey.DeviceInRandomChat))
		{
			response.addToBody(JSONKey.DeviceInRandomChat, String.valueOf(randomPool.getDeviceCountInChat()));
		}
		
		if (body.containsKey(JSONKey.DeviceInInterestChat))
		{
			response.addToBody(JSONKey.DeviceInInterestChat, String.valueOf(interestPool.getDeviceCountInChat()));
		}
		
		if (body.containsKey(JSONKey.CurrentHotLabels))
		{
			String labelCount = body.getString(JSONKey.CurrentHotLabels);
			
			int count = 0;
			if (labelCount.length() == 0)
			{
				count = GlobalSetting.BusinessSetting.HotInterestLabelCount;
			}
			else
			{
				count = Integer.parseInt(labelCount);
			}
			
			JSONArray hotLabelObj = new JSONArray();
			LinkedList<AbstractLabel> labels = interestPool.getHotInterestLabel(count);
			
			for (AbstractLabel label : labels)
			{
				hotLabelObj.add(label.getName());
			}
			//response.addToBody(JSONKey.CurrentHotLabels, );
			// TODO:
		}
		
		if (body.containsKey(JSONKey.HistoryHotLabels))
		{
			// TODO:
		}
	}


	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ServerDataSyncRequest;
	}
}
