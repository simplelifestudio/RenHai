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
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.HotLabel;
import com.simplelife.renhai.server.business.pool.InterestBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.IDeviceWrapper;
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
		messageId = Consts.MessageId.ServerDataSyncRequest;
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
						this.setErrorDescription(JSONKey.Current + " in " + JSONKey.InterestLabelList +" must be numric.");
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
	
	private void queryDeviceSummary(ServerJSONMessage response, 
			AbstractBusinessDevicePool randomPool, 
			InterestBusinessDevicePool interestPool)
	{
		JSONObject responseObj = new JSONObject();
		response.addToBody(JSONKey.DeviceSummary, responseObj);
		
		OnlineDevicePool.instance.reportDeviceDetails(responseObj);
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
			int labelCount = hotObj.getIntValue(JSONKey.Current);
			JSONArray hotLabelObj = new JSONArray();
			hotObj.put(JSONKey.Current, hotLabelObj);
			LinkedList<HotLabel> labels = interestPool.getHotInterestLabel(labelCount);
			
			JSONObject tempLabelObj;
			Globalinterestlabel globalLabel;
			for (HotLabel label : labels)
			{
				globalLabel = DBQueryUtil.getGlobalinterestlabel(label.getLabelName());
				tempLabelObj = new JSONObject();
				tempLabelObj.put(JSONKey.GlobalInterestLabelId, globalLabel.getGlobalInterestLabelId());
				tempLabelObj.put(JSONKey.InterestLabelName, label.getLabelName());
				tempLabelObj.put(JSONKey.GlobalMatchCount, globalLabel.getGlobalMatchCount());
				tempLabelObj.put(JSONKey.LabelOrder, null);
				tempLabelObj.put(JSONKey.MatchCount, null);
				tempLabelObj.put(JSONKey.ValidFlag, null);
				
				hotLabelObj.add(tempLabelObj);
			}
			responseObj.put(JSONKey.Current, hotLabelObj);
		}
		
		if (hotObj.containsKey(JSONKey.History))
		{
			JSONArray hotLabelObj = new JSONArray();
			responseObj.put(JSONKey.History, hotLabelObj);
			// TODO: 
		}
	}
	
	@Override
	public void doRun()
	{
		if (!checkJSONRequest())
		{
			responseError();
			return;
		}

		//DbLogger.saveProfileLog(Consts.OperationCode.ServerDataSyncRequest_1008
    	//		, deviceWrapper.getDevice().getProfile()
    	//		, header.getString(JSONKey.DeviceSn));
		
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
		
		if (body.containsKey(JSONKey.DeviceSummary))
		{
			queryDeviceSummary(response, randomPool, interestPool);
		}
		
		if (body.containsKey(JSONKey.DeviceDetailedInfo))
		{
			String deviceSn = body.getString(JSONKey.DeviceDetailedInfo);
			IDeviceWrapper device = OnlineDevicePool.instance.getDevice(deviceSn);
			if (device != null)
			{
				response.addToBody(JSONKey.DeviceDetailedInfo, device.toJSONObject());
			}
		}
				
		//DbLogger.saveProfileLog(Consts.OperationCode.ServerDataSyncResponse_1009
    	//		, deviceWrapper.getDevice().getProfile()
    	//		, header.getString(JSONKey.DeviceSn));
		OutputMessageCenter.instance.addMessage(response);		
	}


	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ServerDataSyncRequest;
	}
}
