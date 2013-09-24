/**
 * AppDataSyncRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.GlobalinterestlabelDAO;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.TableColumnName;
import com.simplelife.renhai.server.db.TableName;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class AppDataSyncRequest extends AppJSONMessage
{
	private SyncType syncType = SyncType.Invalid;
	
	private enum SyncType
	{
		Invalid, NewDevice, ExistentNotLoaded, ExistentLoaded
	}
	
	/**
	 * @param jsonObject
	 */
	public AppDataSyncRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}

	
	
	protected boolean checkUpdate(JSONObject dataUpdateObj)
	{
		if (dataUpdateObj.isEmpty())
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.DataUpdate + " can't be empty.");
			return false;
		}
		
		if (!dataUpdateObj.containsKey(JSONKey.Device))
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.Device + " must be provided.");
			return false;
		}
		
		JSONObject deviceObj = dataUpdateObj.getJSONObject(JSONKey.Device);
		if (!checkUpdateDevice(deviceObj))
		{
			return false;
		}
		return true;
	}
	
	protected boolean checkUpdateDevice(JSONObject deviceObj)
	{
		if (!deviceObj.containsKey(JSONKey.DeviceSn))
		{
			String temp = JSONKey.LabelOrder + " must be provided for device.";
			logger.error(temp);
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(temp);
			return false;
		}
		
		if (deviceObj.containsKey(JSONKey.DeviceCard))
		{
			JSONObject deviceCardObj = deviceObj.getJSONObject(JSONKey.DeviceCard);
			if (!checkUpdateDeviceCard(deviceCardObj))
			{
				return false;
			}
		}
		else if (deviceObj.containsKey(JSONKey.Profile))
		{
			JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
			if (!checkUpdateProfile(profileObj))
			{
				return false;
			}
		}
		else
		{
			String temp = "Neither " + JSONKey.DeviceCard + " nor " + JSONKey.Profile + " is provided.";
			logger.error(temp);
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(temp);
			return false;
		}
		
		return true;
	}
	
	protected boolean checkUpdateInterestCard(JSONObject interestCardObj)
	{
		if (!interestCardObj.containsKey(JSONKey.InterestLabelList))
		{
			String temp = JSONKey.LabelOrder + " must be provided for interestCard.";
			logger.error(temp);
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(temp);
			return false;
		}
		
		JSONArray interestLabelList;
		try
		{
			interestLabelList = interestCardObj.getJSONArray(JSONKey.InterestLabelList);
		}
		catch (Exception e)
		{
			String temp = JSONKey.InterestLabelList + " is not described in JSON Array.";
			logger.error(temp);
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(temp);
			return false;
		}
		
		if (interestLabelList.size() == 0)
		{
			String temp = JSONKey.InterestLabelList + " can't be empty for updating interestCard.";
			logger.error(temp);
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(temp);
			return false;
		}
		
		JSONObject tmpObj;
		String tmpStr;
		for (int i = 0; i < interestLabelList.size(); i++)
		{
			tmpObj = interestLabelList.getJSONObject(i);
			if (!tmpObj.containsKey(JSONKey.InterestLabel))
			{
				String temp = JSONKey.InterestLabel + " must be provided in " + i + " label in " + JSONKey.InterestLabelList;
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!checkNoEmptyAllowed(tmpObj, JSONKey.InterestLabel))
			{
				return false;
			}
			
			if (!tmpObj.containsKey(JSONKey.LabelOrder))
			{
				String temp = JSONKey.LabelOrder + " must be provided in " + i + " label in " + JSONKey.InterestLabelList;
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			tmpStr = tmpObj.getString(JSONKey.LabelOrder);
			if (!CommonFunctions.IsNumric(tmpStr))
			{
				String temp = JSONKey.LabelOrder + " must be numric in " +  i + " label in " + JSONKey.InterestLabelList;
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
		}
		return true;
	}
	
	protected boolean checkUpdateProfile(JSONObject profileObj)
	{
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			JSONObject interestCardObj = profileObj.getJSONObject(JSONKey.InterestCard);
			if (!checkUpdateInterestCard(interestCardObj))
			{
				return false;
			}
		}
		else if (profileObj.containsKey(JSONKey.ImpressCard))
		{
			// Currently there is no update of impress card from APP
		}
		else
		{
			String temp = "Neither " + JSONKey.InterestCard + " nor " + JSONKey.ImpressCard + " is included in " + JSONKey.Profile;
			logger.error(temp);
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(temp);
			return false;
		}
		
		return true;
	}
	
	protected boolean checkUpdateDeviceCard(JSONObject deviceCardObj)
	{
		if (deviceCardObj.isEmpty())
		{
			String temp = JSONKey.DeviceCard + " can't be empty.";
			logger.error(temp);
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(temp);
			return false;
		}
		
		DeviceDAO deviceDAO = new DeviceDAO();
		String deviceSn = header.getString(JSONKey.DeviceSn);
		if(deviceDAO.findByDeviceSn(deviceSn).size() == 0)
		{
			if (!deviceCardObj.containsKey(JSONKey.DeviceModel))
			{
				String temp = JSONKey.DeviceModel + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!deviceCardObj.containsKey(JSONKey.OsVersion))
			{
				String temp = JSONKey.OsVersion + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!deviceCardObj.containsKey(JSONKey.AppVersion))
			{
				String temp = JSONKey.AppVersion + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!deviceCardObj.containsKey(JSONKey.IsJailed))
			{
				String temp = JSONKey.IsJailed + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
		}
		
		// Check values of DeviceCard
		if (deviceCardObj.containsKey(JSONKey.IsJailed))
		{
			String fieldValue = deviceCardObj.getString(JSONKey.IsJailed);
			
			if (!fieldValue.matches("[0-1]"))
			{
				String temp = JSONKey.IsJailed + " must be 0 or 1.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
		}
		
		if (!checkNoEmptyAllowed(deviceCardObj, JSONKey.OsVersion))
		{
			return false;
		}
		
		if (!checkNoEmptyAllowed(deviceCardObj, JSONKey.AppVersion))
		{
			return false;
		}
		
		if (!checkNoEmptyAllowed(deviceCardObj, JSONKey.DeviceModel))
		{
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
    {
		if (body.containsKey(JSONKey.DataUpdate))
		{
			if (!checkUpdate(body.getJSONObject(JSONKey.DataUpdate)))
			{
				return false;
			}
		}
		else if (body.containsKey(JSONKey.DataQuery))
		{
			// it doesn't need check for dataquery currently
		}
		else
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription("Neither " + JSONKey.DataQuery + " nor " + JSONKey.DataUpdate + " is included in request");
			return false;
		}
		
		return true;
    }
	
	@Override
	public void run()
	{
		if (!checkJSONRequest())
		{
			responseError(Consts.MessageId.AppDataSyncRequest.name());
			return;
		}
		
		String deviceSn = header.getString(JSONKey.DeviceSn);
		if (OnlineDevicePool.instance.isDeviceInPool(deviceSn))
		{
			syncType = SyncType.ExistentLoaded;
		}
		else if (DBQueryUtil.isNewDevice(deviceSn))
		{
			syncType = SyncType.NewDevice;
		}
		else
		{
			syncType = SyncType.ExistentNotLoaded;
		}
		
		if (syncType == SyncType.ExistentNotLoaded)
		{
			loadDevice(deviceSn);
		}
		else if (syncType == SyncType.NewDevice)
		{
			if (body.containsKey(JSONKey.DataUpdate))
			{
				newDevice(deviceSn);
			}
			else
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription("It's not allowed to query a new device without update.");
				responseError(Consts.MessageId.AppDataSyncRequest.name());
				return;
			}
		}
		
		// Check if profile is banned
		Profile profile = deviceWrapper.getDevice().getProfile();
		String strServiceStatus = profile.getServiceStatus();
		Consts.ServiceStatus status = Consts.ServiceStatus.parseFromStringValue(strServiceStatus);  
		if (status == Consts.ServiceStatus.Banned)
		{
			long unbanDate = profile.getUnbanDate();
			if (unbanDate <= System.currentTimeMillis())
			{	
				// Recover to normal
				profile.setServiceStatus(Consts.ServiceStatus.Normal.name());
				profile.setUnbanDate(null);
				
				DAOWrapper.cache(profile);
				//ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.AppDataSyncResponse);
				deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Idle);
			}
			else
			{
				OnlineDevicePool.instance.IdentifyBannedDevice(deviceWrapper);
			}
		}
		else
		{
			deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Idle);
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.AppDataSyncResponse);
		if (profile.getServiceStatus().equals(Consts.ServiceStatus.Normal.name()))
		{
			// Only allow normal device to update profile
			if (body.containsKey(JSONKey.DataUpdate))
			{
				dataUpdate(body.getJSONObject(JSONKey.DataUpdate), response.getBody());
				DAOWrapper.cache(this.deviceWrapper.getDevice());
			}
		}
		
		if (profile.getServiceStatus().equals(Consts.ServiceStatus.Banned.name()))
		{
			// Append query of profile status  
			appendQueryServiceStatus();
		}
		
		if (body.containsKey(JSONKey.DataQuery))
		{
			dataQuery(body.getJSONObject(JSONKey.DataQuery), response.getBody());
		}
		response.asyncResponse();
	}

	private void appendQueryServiceStatus()
	{
		if (!body.containsKey(JSONKey.DataQuery))
		{
			body.put(JSONKey.DataQuery, new JSONObject());
		}
		
		JSONObject queryObj = body.getJSONObject(JSONKey.DataQuery);
		if (!queryObj.containsKey(JSONKey.Device))
		{
			queryObj.put(JSONKey.Device, new JSONObject());
		}
		
		JSONObject deviceObj = queryObj.getJSONObject(JSONKey.Device);
		if (!deviceObj.containsKey(JSONKey.Profile))
		{
			deviceObj.put(JSONKey.Profile, new JSONObject());
		}
		
		JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
		if (!profileObj.containsKey(JSONKey.ServiceStatus))
		{
			profileObj.put(JSONKey.ServiceStatus, "");
		}
		
		if (!profileObj.containsKey(JSONKey.UnbanDate))
		{
			profileObj.put(JSONKey.UnbanDate, "");
		}
	}
	private void loadDevice(String deviceSn)
	{
		DeviceDAO deviceDAO = new DeviceDAO();
		List<Device> cardList = deviceDAO.findByDeviceSn(deviceSn);
		if (cardList.size() == 0 )
		{
			logger.error("DeviceCard can't be found in DB with SN: " + deviceSn);
			return;
		}

		Device device = cardList.get(0);
		deviceWrapper.setDevice(device);
	}
	
	private void dataQuery(JSONObject queryObj, JSONObject response)
	{
		if (queryObj.isEmpty())
		{
			return;
		}
		
		if (!queryObj.containsKey(JSONKey.Device))
		{
			return;
		}
		
		JSONObject deviceObj = queryObj.getJSONObject(JSONKey.Device);
		JSONObject queryResponse = new JSONObject();
		response.put(JSONKey.DataQuery, queryResponse);
		
		dataQueryDevice(deviceObj, queryResponse);
	}
	
	private void dataQueryDevice(JSONObject deviceObj, JSONObject queryResponse)
	{
		if (deviceObj.isEmpty())
		{
			return;
		}
		
		JSONObject deviceResponse = new JSONObject();
		queryResponse.put(JSONKey.Device, deviceResponse);
		
		if (deviceObj.containsKey(JSONKey.DeviceId))
		{
			deviceResponse.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
		}
		
		if (deviceObj.containsKey(JSONKey.DeviceSn))
		{
			deviceResponse.put(JSONKey.DeviceSn, deviceWrapper.getDevice().getDeviceSn());
		}
		
		if (deviceObj.containsKey(JSONKey.DeviceCard))
		{
			JSONObject deviceCardObj = deviceObj.getJSONObject(JSONKey.DeviceCard);
			JSONObject deviceCardResponse = new JSONObject();
			deviceResponse.put(JSONKey.DeviceCard, deviceCardResponse);
			
			dataQueryDeviceCard(deviceCardObj, deviceCardResponse);
		}
		
		if (deviceObj.containsKey(JSONKey.Profile))
		{
			JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
			JSONObject profileResponse = new JSONObject();
			deviceResponse.put(JSONKey.Profile, profileResponse);
			
			dataQueryProfile(profileObj, profileResponse);
		}
	}
	
	private void dataQueryProfile(JSONObject profileObj, JSONObject profileResponse)
	{
		if (profileObj.isEmpty())
		{
			return;
		}
		
		Profile profile = deviceWrapper.getDevice().getProfile();
		if (profileObj.containsKey(JSONKey.ProfileId))
		{
			profileResponse.put(JSONKey.ProfileId, profile.getProfileId());
		}
		
		if (profileObj.containsKey(JSONKey.ServiceStatus))
		{
			profileResponse.put(JSONKey.ServiceStatus, profile.getServiceStatus());
		}
		
		if (profileObj.containsKey(JSONKey.UnbanDate))
		{
			profileResponse.put(JSONKey.UnbanDate, profile.getUnbanDate());
		}
		
		if (profileObj.containsKey(JSONKey.LastActivityTime))
		{
			profileResponse.put(JSONKey.LastActivityTime, DateUtil.getDateStringByLongValue(profile.getLastActivityTime()));
		}
		
		if (profileObj.containsKey(JSONKey.CreateTime))
		{
			profileResponse.put(JSONKey.CreateTime, DateUtil.getDateStringByLongValue(profile.getCreateTime()));
		}
		
		if (profileObj.containsKey(JSONKey.Active))
		{
			profileResponse.put(JSONKey.Active, profile.getActive());
		}
		
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			JSONObject interestCardObj = profileObj.getJSONObject(JSONKey.InterestCard);
			JSONObject interestCardResponse = new JSONObject();
			profileResponse.put(JSONKey.Profile, interestCardResponse);
			
			dataQueryInterestCard(interestCardObj, interestCardResponse);
		}
		
		if (profileObj.containsKey(JSONKey.ImpressCard))
		{
			JSONObject impressCardObj = profileObj.getJSONObject(JSONKey.ImpressCard);
			JSONObject impressCardResponse = new JSONObject();
			profileResponse.put(JSONKey.Profile, impressCardResponse);
			
			dataQueryImpressCard(impressCardObj, impressCardResponse);
		}
	}
	
	private void dataQueryImpressCard(JSONObject impressCardObj, JSONObject impressCardResponse)
	{
		if (impressCardObj.isEmpty())
		{
			return;
		}
		
		Impresscard card = deviceWrapper.getDevice().getProfile().getImpresscard();
		if (impressCardObj.containsKey(JSONKey.ImpressCardId))
		{
			impressCardResponse.put(JSONKey.ImpressCardId, card.getImpressCardId());
		}
		
		if (impressCardObj.containsKey(JSONKey.ChatTotalCount))
		{
			impressCardResponse.put(JSONKey.ChatTotalCount, card.getChatTotalCount());
		}
		
		if (impressCardObj.containsKey(JSONKey.ChatTotalDuration))
		{
			impressCardResponse.put(JSONKey.ChatTotalDuration, card.getChatTotalDuration());
		}
		
		if (impressCardObj.containsKey(JSONKey.ChatLossCount))
		{
			impressCardResponse.put(JSONKey.ChatLossCount, card.getChatLossCount());
		}
		
		if (impressCardObj.containsKey(JSONKey.ImpressCard))
		{
			JSONArray labels = new JSONArray();
			impressCardResponse.put(JSONKey.ImpressCard, labels);
			
			int impressLabelCount;
			if (impressCardObj.containsKey(JSONKey.ImpressLabelList))
			{
				impressLabelCount = impressCardObj.getIntValue(JSONKey.ImpressLabelList);
			}
			else
			{
				impressLabelCount = card.getImpresslabelmaps().size();
			}
			
			// TODO: 需要考虑印象标签的排序
			Object[] labelArray = card.getImpresslabelmaps().toArray();
			
			for (int i = 0; i < impressLabelCount; i++)
			{
				Impresslabelmap label = (Impresslabelmap) labelArray[i];
				JSONObject labelObj = new JSONObject();
				labelObj.put(JSONKey.ImpressLabel, label.getGlobalimpresslabel().getImpressLabel());
				labelObj.put(JSONKey.GlobalImpressLabelId, label.getGlobalimpresslabel().getGlobalImpressLabelId());
				labelObj.put(JSONKey.AssessedCount, label.getAssessedCount());
				labelObj.put(JSONKey.UpdateTime, DateUtil.getDateStringByLongValue(label.getUpdateTime()));
				labelObj.put(JSONKey.AssessCount, label.getAssessCount());

				labels.add(labelObj);
			}
		}
	}
	
	private void dataQueryInterestCard(JSONObject interestCardObj, JSONObject interestCardResponse)
	{
		if (interestCardObj.isEmpty())
		{
			return;
		}
		
		Interestcard card = deviceWrapper.getDevice().getProfile().getInterestcard();
		if (interestCardObj.containsKey(JSONKey.InterestCardId))
		{
			interestCardResponse.put(JSONKey.InterestCardId, card.getInterestCardId());
		}
		
		if (interestCardObj.containsKey(JSONKey.InterestLabelList))
		{
			JSONArray labels = new JSONArray();
			interestCardResponse.put(JSONKey.InterestLabelList, labels);
			
			for (Interestlabelmap label : card.getInterestlabelmaps())
			{
				JSONObject labelObj = new JSONObject();
				labelObj.put(JSONKey.GlobalImpressLabelId, label.getGlobalinterestlabel().getGlobalInterestLabelId());
				labelObj.put(JSONKey.InterestLabel, label.getGlobalinterestlabel().getInterestLabel());
				labelObj.put(JSONKey.GlobalMatchCount, label.getGlobalinterestlabel().getGlobalMatchCount());
				labelObj.put(JSONKey.LabelOrder, label.getlabelOrder());
				labelObj.put(JSONKey.MatchCount, label.getMatchCount());
				
				labels.add(labelObj);
			}
		}
	}
	
	private void dataQueryDeviceCard(JSONObject deviceCardObj, JSONObject deviceCardResponse)
	{
		if (deviceCardObj.isEmpty())
		{
			return;
		}
		
		Devicecard card = deviceWrapper.getDevice().getDevicecard();
		if (deviceCardObj.containsKey(JSONKey.DeviceCardId))
		{
			deviceCardResponse.put(JSONKey.DeviceCardId, card.getDeviceCardId());
		}
		
		if (deviceCardObj.containsKey(JSONKey.DeviceId))
		{
			deviceCardResponse.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
		}
		
		if (deviceCardObj.containsKey(JSONKey.RegisterTime))
		{
			deviceCardResponse.put(JSONKey.RegisterTime, DateUtil.getDateStringByLongValue(card.getRegisterTime()));
		}
		
		if (deviceCardObj.containsKey(JSONKey.DeviceModel))
		{
			deviceCardResponse.put(JSONKey.DeviceModel, card.getDeviceModel());
		}
		
		if (deviceCardObj.containsKey(JSONKey.OsVersion))
		{
			deviceCardResponse.put(JSONKey.OsVersion, card.getOsVersion());
		}
		
		if (deviceCardObj.containsKey(JSONKey.AppVersion))
		{
			deviceCardResponse.put(JSONKey.AppVersion, card.getAppVersion());
		}
		
		if (deviceCardObj.containsKey(JSONKey.Location))
		{
			deviceCardResponse.put(JSONKey.Location, card.getLocation());
		}
		
		if (deviceCardObj.containsKey(JSONKey.IsJailed))
		{
			deviceCardResponse.put(JSONKey.IsJailed, card.getIsJailed());
		}
	}
	
	private void dataUpdate(JSONObject updateObj, JSONObject response)
	{
		if (updateObj == null)
		{
			return;
		}
		JSONObject dataUpdateResponse = new JSONObject();
		response.put(JSONKey.DataUpdate, dataUpdateResponse);
		
		JSONObject deviceObj = updateObj.getJSONObject(JSONKey.Device);
		dataUpdateDevice(deviceObj, dataUpdateResponse);
	}
	
	private void dataUpdateDevice(JSONObject deviceObj, JSONObject dataUpdateResponse)
	{
		JSONObject deviceResponse = new JSONObject();
		dataUpdateResponse.put(JSONKey.Device, deviceResponse);
		Device device = deviceWrapper.getDevice();
		device.setDeviceSn(header.getString(JSONKey.DeviceSn));
		
		// Update device card if needed
		if (deviceObj.containsKey(JSONKey.DeviceCard))
		{
			dataUpdateDeviceCard(deviceObj, deviceResponse);
		}
		
		
		if (deviceObj.containsKey(JSONKey.Profile))
		{
			dataUpdateProfile(deviceObj, deviceResponse);
		}
	}
	
	private void dataUpdateProfile(JSONObject deviceObj, JSONObject deviceResponse)
	{
		JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
		JSONObject profileResponse = new JSONObject();
		
		parseProfile(profileObj, profileResponse);
		deviceResponse.put(JSONKey.Profile, profileResponse);
		
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			dataUpdateInterestCard(profileObj, profileResponse);
		}

		/*
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			// currently there is no update of impresscard from APP
		}
		*/
	}
	
	private void dataUpdateInterestCard(JSONObject profileObj, JSONObject profileResponse)
	{
		JSONObject interestCardObj = profileObj.getJSONObject(JSONKey.InterestCard);
		JSONObject interestCardResponse = new JSONObject();
		parseInterestCard(interestCardObj, interestCardResponse);
		profileResponse.put(JSONKey.InterestCard, interestCardResponse);
	}
	
	private void dataUpdateDeviceCard(JSONObject deviceObj, JSONObject deviceResponse)
	{
		JSONObject deviceCardObj = deviceObj.getJSONObject(JSONKey.DeviceCard);
		JSONObject deviceCardResponse = new JSONObject();
		parseDevicecard(deviceCardObj, deviceCardResponse);
		deviceResponse.put(JSONKey.DeviceCard, deviceCardResponse);
	}
	private void newDevice(String deviceSn)
	{
		// Create new impress card
		Impresscard impressCard = new Impresscard();
		impressCard.setChatLossCount(0);
		impressCard.setChatTotalCount(0);
		impressCard.setChatTotalDuration(0);
		
		// Create new interest card
		Interestcard interestCard = new Interestcard();
		
		// Create new profile
		Profile profile = new Profile();
		profile.setImpresscard(impressCard);
		profile.setInterestcard(interestCard);
		long now = System.currentTimeMillis();
		profile.setLastActivityTime(now);
		profile.setCreateTime(now);
		profile.setServiceStatus(Consts.ServiceStatus.Normal.name());
		
		// Bind profile with cards
		profile.setInterestcard(interestCard);
		profile.setImpresscard(impressCard);
		interestCard.setProfile(profile);
		impressCard.setProfile(profile);
		
		Devicecard deviceCard = new Devicecard();
		deviceCard.setRegisterTime(now);
		
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDeviceSn(deviceSn);
		device.setDevicecard(deviceCard);
		deviceCard.setDevice(device);
		device.setProfile(profile);
		profile.setDevice(device);
		
		// Bind DeviceWrapper with Device
		deviceWrapper.setDevice(device);
		
		Date date = DateUtil.getNowDate();
		deviceWrapper.updateActivityTime();
		deviceWrapper.updatePingTime();
		deviceWrapper.setServiceStatus(Consts.ServiceStatus.Normal);
	}
	
	private void parseInterestCard(JSONObject interestCardObj, JSONObject response)
	{
		if (interestCardObj.isEmpty())
		{
			return;
		}
		
		Interestcard card = deviceWrapper.getDevice().getProfile().getInterestcard();
		JSONArray interestLabelList = interestCardObj.getJSONArray(JSONKey.InterestLabelList);
		
		JSONArray responseArray = new JSONArray();
		response.put(JSONKey.InterestLabelList, responseArray);
		
		JSONObject tmpJSONObj;
		String tmpStr;
		Set<Interestlabelmap> labelMapList = new LinkedHashSet<Interestlabelmap>();
		List<Globalinterestlabel> tmpInterestLabelList;
		
		GlobalinterestlabelDAO globalInterestDAO = new GlobalinterestlabelDAO();
		Globalinterestlabel globalInterestObj;
		Interestlabelmap interestLabelMapObj;
		
		JSONObject responseObj;
		for (int i = 0; i < interestLabelList.size(); i++)
		{
			tmpJSONObj = interestLabelList.getJSONObject(i);
			responseObj = new JSONObject();
			
			interestLabelMapObj = new Interestlabelmap();
			
			// Check if it's new interest label
			tmpStr = tmpJSONObj.getString(JSONKey.InterestLabel);
			tmpInterestLabelList = globalInterestDAO.findByInterestLabel(tmpStr);
			if (tmpInterestLabelList.size() == 0)
			{
				globalInterestObj = new Globalinterestlabel();
				globalInterestObj.setInterestLabel(tmpStr);
				globalInterestObj.setGlobalMatchCount(0);
				
				interestLabelMapObj.setMatchCount(0);
				interestLabelMapObj.setValidFlag("Valid");
			}
			else
			{
				globalInterestObj = tmpInterestLabelList.get(0);
				
				if (tmpJSONObj.containsKey(JSONKey.MatchCount))
				{
					interestLabelMapObj.setMatchCount(tmpJSONObj.getInteger(JSONKey.MatchCount));
				}
				else
				{
					interestLabelMapObj.setMatchCount(0);
				}
				interestLabelMapObj.setValidFlag("Valid");
			}
			responseObj.put(JSONKey.InterestLabel, Consts.SuccessOrFail.Success);
			interestLabelMapObj.setGlobalinterestlabel(globalInterestObj);
			
			interestLabelMapObj.setLabelOrder(Integer.parseInt(tmpJSONObj.getString(JSONKey.LabelOrder)));
			responseObj.put(JSONKey.LabelOrder, Consts.SuccessOrFail.Success);
			labelMapList.add(interestLabelMapObj);
			
			interestLabelMapObj.setInterestcard(card);
		}
		
		card.setInterestlabelmaps(labelMapList);
		
		if (labelMapList.size() > 0)
		{
			String interestCardId = DBQueryUtil.getIDByDeviceSn(TableColumnName.InterestCardId, deviceWrapper.getDeviceSn());
			
			if (interestCardId != null)
			{
				String sql = "delete from " + TableName.InterestLabelMap 
						+ " where " + TableColumnName.InterestCardId + " = " + interestCardId;
				DAOWrapper.executeSql(sql);
			}
		}
	}
	
	private void parseProfile(JSONObject profileObj, JSONObject profileResponse)
	{
		// currently there is no update of profile from APP 
		Profile profile = deviceWrapper.getDevice().getProfile();
		profile.setActive(Consts.YesNo.Yes.name());
	}
	
	private void parseDevicecard(JSONObject jsonObj, JSONObject response)
	{
		Device device = deviceWrapper.getDevice();
		Devicecard deviceCard = device.getDevicecard();
		
		String temp = jsonObj.getString(JSONKey.DeviceModel);
		if (temp != null)
		{
			response.put(JSONKey.DeviceModel, Consts.SuccessOrFail.Success.toString());
			deviceCard.setDeviceModel(temp);
		}
		
		temp = jsonObj.getString(JSONKey.OsVersion);
		if (temp != null)
		{
			response.put(JSONKey.OsVersion, Consts.SuccessOrFail.Success.toString());
			deviceCard.setOsVersion(temp);
		}
		
		temp = jsonObj.getString(JSONKey.AppVersion);
		if (temp != null)
		{
			response.put(JSONKey.AppVersion, Consts.SuccessOrFail.Success.toString());
			deviceCard.setAppVersion(temp);
		}
		
		temp = jsonObj.getString(JSONKey.IsJailed);
		if (temp != null)
		{
			response.put(JSONKey.IsJailed, Consts.SuccessOrFail.Success.toString());
			Consts.YesNo isJailed = Consts.YesNo.parseValue(jsonObj.getIntValue(JSONKey.IsJailed)); 
			deviceCard.setIsJailed(isJailed.name());
		}
		
		temp = jsonObj.getString(JSONKey.Location);
		if (temp != null)
		{
			response.put(JSONKey.Location, Consts.SuccessOrFail.Success.toString());
			deviceCard.setLocation(temp);
		}
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AppDataSyncRequest;
	}
}
