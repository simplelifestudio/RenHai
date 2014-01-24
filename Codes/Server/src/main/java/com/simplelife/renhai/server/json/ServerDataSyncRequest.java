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

import org.apache.ibatis.session.SqlSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.InterestBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.RandomBusinessDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.DeviceMapper;
import com.simplelife.renhai.server.db.GlobalimpresslabelMapper;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.GlobalinterestlabelMapper;
import com.simplelife.renhai.server.db.StatisticsMapper;
import com.simplelife.renhai.server.util.ComparableResult;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.GlobalSetting;
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
		if (countObj == null || countObj.isEmpty())
		{
			queryAll = true;
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Online))
		{
			responseObj.put(JSONKey.Online, OnlineDevicePool.instance.getDeviceCount());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Random))
		{
			responseObj.put(JSONKey.Random, randomPool.getDeviceCount());
		}
		
		if (queryAll || countObj.containsKey(JSONKey.Interest))
		{
			responseObj.put(JSONKey.Interest, interestPool.getDeviceCount());
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
		
		if (countObj.containsKey(JSONKey.ManagementData))
		{
			queryManagementStat(responseObj);
		}
	}
	
	private void queryManagementStat(JSONObject response)
	{
		JSONObject responseObj = new JSONObject();
		response.put(JSONKey.ManagementData, responseObj);
		
		SqlSession session = DAOWrapper.instance.getSession();
		StatisticsMapper mapper = session.getMapper(StatisticsMapper.class);
		
		queryRegisterDeviceCount(mapper, responseObj);
		queryTopInterestLabels(mapper, responseObj);
		queryTopImpressLabels(mapper, responseObj);
		queryDeviceCountByModel(mapper, responseObj);
		queryDeviceCountByOS(mapper, responseObj);
		queryTotalChatCount(mapper, responseObj);
		queryTotalChatDuration(mapper, responseObj);
		
		session.close();
	}
	
	private void queryTotalChatDuration(StatisticsMapper mapper, JSONObject responseObj)
	{
		long count = mapper.selectTotalChatDuration();
		responseObj.put(JSONKey.TotalChatDuration, count);
	}
	
	private void queryTotalChatCount(StatisticsMapper mapper, JSONObject responseObj)
	{
		long count = mapper.selectTotalChatCount();
		responseObj.put(JSONKey.TotalChatCount, count);
	}
	
	private void queryDeviceCountByOS(StatisticsMapper mapper, JSONObject responseObj)
	{
		LinkedList<ComparableResult> objects = mapper.selectDeviceCountByOS();
		addComparableObjets(JSONKey.DeviceCountByOS, objects, responseObj);
	}
	
	private void queryDeviceCountByModel(StatisticsMapper mapper, JSONObject responseObj)
	{
		LinkedList<ComparableResult> objects = mapper.selectDeviceCountByModel();
		addComparableObjets(JSONKey.DeviceCountByModel, objects, responseObj);
	}
	
	private void queryTopImpressLabels(StatisticsMapper mapper, JSONObject responseObj)
	{
		LinkedList<ComparableResult> objects = mapper.selectTopImpressLabels(GlobalSetting.BusinessSetting.MaxImpressLabelCount);
		addComparableObjets(JSONKey.TopImpressLabels, objects, responseObj);
	}
	
	private void queryTopInterestLabels(StatisticsMapper mapper, JSONObject responseObj)
	{
		LinkedList<ComparableResult> objects = mapper.selectTopInterestLabels(GlobalSetting.BusinessSetting.MaxImpressLabelCount);
		addComparableObjets(JSONKey.TopInterestLabels, objects, responseObj);
	}
	
	private void addComparableObjets(String jsonKey, LinkedList<ComparableResult> objects, JSONObject responseObj)
	{
		//JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		responseObj.put(jsonKey, obj);
		
		for (ComparableResult object : objects)
		{
			obj.put(object.toString(), object.getCount());
		}
	}
	
	private void queryRegisterDeviceCount(StatisticsMapper mapper, JSONObject responseObj)
	{
		int registerDeviceCount = mapper.selectRegisterDeviceCount();
		responseObj.put(JSONKey.RegisterDeviceCount, registerDeviceCount);
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
			LinkedList<ComparableResult> labels = interestPool.getHotInterestLabel(labelCount);
			
			JSONObject tempLabelObj;
			Globalinterestlabel globalLabel;
			ComparableResult label;
			String labelName;
			while(!labels.isEmpty())
			{
				label = labels.removeLast();
				labelName = label.toString();
				globalLabel = DBModule.instance.interestLabelCache.getObject(labelName);
				tempLabelObj = new JSONObject();
				tempLabelObj.put(JSONKey.GlobalInterestLabelId, globalLabel.getGlobalInterestLabelId());
				tempLabelObj.put(JSONKey.InterestLabelName, labelName);
				tempLabelObj.put(JSONKey.GlobalMatchCount, globalLabel.getGlobalMatchCount());
				tempLabelObj.put(JSONKey.CurrentProfileCount, label.getCount());
				//tempLabelObj.put(JSONKey.LabelOrder, null);
				//tempLabelObj.put(JSONKey.MatchCount, null);
				//tempLabelObj.put(JSONKey.ValidFlag, null);
				
				hotLabelObj.add(tempLabelObj);
			}
			responseObj.put(JSONKey.Current, hotLabelObj);
		}
		
		if (hotObj.containsKey(JSONKey.History))
		{
			JSONArray hotLabelObj = new JSONArray();
			responseObj.put(JSONKey.History, hotLabelObj);
			// TODO: ������ʷ��ǩ�Ĳ�ѯ����
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
		RandomBusinessDevicePool randomPool = (RandomBusinessDevicePool)OnlineDevicePool.instance.getBusinessPool(Consts.BusinessType.Random);
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
		deviceWrapper.prepareResponse(response);		
	}


	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ServerDataSyncRequest;
	}
}
