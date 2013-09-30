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

import com.alibaba.fastjson.JSON;
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
		if (!body.containsKey(JSONKey.DeviceCount) 
				&& !body.containsKey(JSONKey.InterestLabelList)
				&& !body.containsKey(JSONKey.DeviceCapacity))
		{
			String temp = JSONKey.DeviceCount + " or " 
					+ JSONKey.InterestLabelList  + " or "
					+ JSONKey.DeviceCapacity
					+ " must be provided at least one.";
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(temp);
			return false;
		}
		
		if (body.containsKey(JSONKey.InterestLabelList))
		{
			JSONObject interestObj = getJSONObject(body, JSONKey.InterestLabelList);
			
			if (interestObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.InterestLabelList + " must be provided correctly.");
				return false;
			}
			
			if (interestObj.containsKey(JSONKey.Current))
			{
				String labelCount = interestObj.getString(JSONKey.Current);
				if (labelCount != null && labelCount.length() > 0)
				{
					if (!CommonFunctions.IsNumric(labelCount))
					{
						this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
						this.setErrorDescription(JSONKey.Current + "in" + JSONKey.InterestLabelList +" must be numric.");
						return false;
					}
				}
			}
		}
		
		return true;
    }
	
	private void queryCapacity(ServerJSONMessage response, 
			AbstractBusinessDevicePool randomPool, 
			InterestBusinessDevicePool interestPool)
	{
		JSONObject responseObj = new JSONObject();
		response.addToBody(JSONKey.DeviceCapacity, responseObj);
		
		JSONObject capacityObj = body.getJSONObject(JSONKey.DeviceCapacity);
		boolean queryAll = false;
		if (capacityObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (capacityObj.isEmpty())
			{
				return;
			}
		}
		
		if (queryAll || capacityObj.containsKey(JSONKey.Online))
		{
			responseObj.put(JSONKey.Online, OnlineDevicePool.instance.getCapacity());
		}
		
		if (queryAll || capacityObj.containsKey(JSONKey.Random))
		{
			responseObj.put(JSONKey.Random, randomPool.getCapacity());
		}
		
		if (queryAll || capacityObj.containsKey(JSONKey.Interest))
		{
			responseObj.put(JSONKey.Interest, interestPool.getCapacity());
		}
	}
	
	private void queryCount(ServerJSONMessage response, 
			AbstractBusinessDevicePool randomPool, 
			InterestBusinessDevicePool interestPool)
	{
		JSONObject responseObj = new JSONObject();
		response.addToBody(JSONKey.DeviceCount, responseObj);
		
		JSONObject countObj = body.getJSONObject(JSONKey.DeviceCount);
		
		boolean queryAll = false;
		if (countObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (countObj.isEmpty())
			{
				return;
			}
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Online))
		{
			responseObj.put(JSONKey.Online, OnlineDevicePool.instance.getElementCount());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Random))
		{
			responseObj.put(JSONKey.Random, randomPool.getElementCount());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Interest))
		{
			responseObj.put(JSONKey.Interest, interestPool.getElementCount());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Chat))
		{
			responseObj.put(JSONKey.Chat, OnlineDevicePool.instance.getDeviceCountInChat());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.RandomChat))
		{
			responseObj.put(JSONKey.RandomChat, randomPool.getDeviceCountInChat());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.InterestChat))
		{
			responseObj.put(JSONKey.InterestChat, interestPool.getDeviceCountInChat());
		}
	}
	
	private void queryHotLabels(ServerJSONMessage response, 
			AbstractBusinessDevicePool randomPool, 
			InterestBusinessDevicePool interestPool)
	{
		JSONObject responseObj = new JSONObject();
		response.addToBody(JSONKey.InterestLabelList, responseObj);
		
		JSONObject hotObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		if (hotObj.containsKey(JSONKey.Current))
		{
			String labelCount = hotObj.getString(JSONKey.Current);
			
			int count = 0;
			if (labelCount == null || labelCount.length() == 0)
			{
				count = GlobalSetting.BusinessSetting.HotInterestLabelCount;
			}
			else
			{
				count = Integer.parseInt(labelCount);
			}
			
			JSONArray hotLabelObj = new JSONArray();
			hotObj.put(JSONKey.Current, hotLabelObj);
			LinkedList<AbstractLabel> labels = interestPool.getHotInterestLabel(count);
			
			for (AbstractLabel label : labels)
			{
				hotLabelObj.add(label.getName());
			}
			//responseObj.addToBody(JSONKey.CurrentHotLabels, );
			// TODO: 引入新的结构保存热门标签的计数/排序
		}
		
		if (hotObj.containsKey(JSONKey.History))
		{
			JSONArray hotLabelObj = new JSONArray();
			hotObj.put(JSONKey.History, hotLabelObj);
			// TODO: 
		}
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
	
		//JSONObject deviceCount = body.getJSONObject("deviceCount");
		//System.out.print(JSON.toJSONString(deviceCount, SerializerFeature.WriteMapNullValue));

		
		if (body.containsKey(JSONKey.DeviceCapacity))
		{
			queryCapacity(response, randomPool, interestPool);
		}
		
		if (body.containsKey(JSONKey.DeviceCount))
		{
			queryCount(response, randomPool, interestPool);
		}
		
		if (body.containsKey(JSONKey.InterestLabelList))
		{
			queryHotLabels(response, randomPool, interestPool);
		}
		
		response.asyncResponse();
	}


	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ServerDataSyncRequest;
	}
}
