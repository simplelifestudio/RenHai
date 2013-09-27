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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.GlobalimpresslabelDAO;
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
import com.simplelife.renhai.server.util.Consts.DBExistResult;
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
		
		JSONObject deviceObj = getJSONObject(dataUpdateObj, JSONKey.Device); 
		if (deviceObj == null)
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.Device + " must be provided correctly.");
			return false;
		}
		
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
			JSONObject deviceCardObj = getJSONObject(deviceObj, JSONKey.DeviceCard);
			if (deviceCardObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.DeviceCard + " must be provided correctly.");
				return false;
			}
				
			if (!checkUpdateDeviceCard(deviceCardObj))
			{
				return false;
			}
		}
		else if (deviceObj.containsKey(JSONKey.Profile))
		{
			JSONObject profileObj = getJSONObject(deviceObj, JSONKey.Profile);
			
			if (profileObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.Profile + " must be provided correctly.");
				return false;
			}
			
			
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
		if (profileObj.isEmpty())
		{
			String temp = JSONKey.Profile + " can't be empty.";
			logger.error(temp);
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(temp);
			return false;
		}
		
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			JSONObject interestCardObj = getJSONObject(profileObj, JSONKey.InterestCard);
			
			if (interestCardObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.InterestCard + " must be provided correctly.");
				return false;
			}
			
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
		
		String sql = "select * from " + TableName.Device 
				+ " where " + TableColumnName.DeviceSn + " = '" + deviceSn + "' ";
		if(DAOWrapper.exists(sql) == DBExistResult.NonExistent)
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
			JSONObject updateObj = getJSONObject(body, JSONKey.DataUpdate);
			if (updateObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.DataUpdate + " must be provided correctly.");
				return false;
			}
			
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
		if (deviceWrapper.getOwnerOnlineDevicePool() == null)
		{
			logger.debug("Device <{}> synchronizing after connnection was released", deviceWrapper.getDeviceSn());
			deviceWrapper.bindOnlineDevicePool(OnlineDevicePool.instance);
		}
		
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
		else 
		{
			boolean newDevice = false; 
			
			try
			{
				newDevice = DBQueryUtil.isNewDevice(deviceSn);
			}
			catch(Exception e)
			{
				this.setErrorCode(Consts.GlobalErrorCode.DBException_1001);
				this.setErrorDescription("Server error must be provided correctly.");
				responseError(Consts.MessageId.AppDataSyncRequest.name());
				return;
			}
			
			if (newDevice)
			{
				syncType = SyncType.NewDevice;
			}
			else
			{
				syncType = SyncType.ExistentNotLoaded;
			}
			
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
				
				DBModule.instance.cache(profile);
				//ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.AppDataSyncResponse);
				deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Idle);
			}
			else
			{
				OnlineDevicePool.instance.IdentifyBannedDevice(deviceWrapper);
				this.setErrorCode(Consts.GlobalErrorCode.NoPermission_1102);
				this.setErrorDescription("Device was banned till " + DateUtil.getDateStringByLongValue(unbanDate));
				responseError(Consts.MessageId.AppDataSyncRequest.name());
				return;
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
				update(body.getJSONObject(JSONKey.DataUpdate), response.getBody());
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
			query(body.getJSONObject(JSONKey.DataQuery), response.getBody());
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
	
	private void query(JSONObject queryObj, JSONObject response)
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
		
		queryDevice(deviceObj, queryResponse);
	}
	
	private void queryDevice(JSONObject deviceObj, JSONObject queryResponse)
	{
		boolean queryAll = false;
		if (deviceObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (deviceObj.isEmpty())
			{
				return;
			}
		}
		
		JSONObject deviceResponse = new JSONObject();
		queryResponse.put(JSONKey.Device, deviceResponse);
		
		if (queryAll || deviceObj.containsKey(JSONKey.DeviceId))
		{
			deviceResponse.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
		}
		
		if (queryAll || deviceObj.containsKey(JSONKey.DeviceSn))
		{
			deviceResponse.put(JSONKey.DeviceSn, deviceWrapper.getDevice().getDeviceSn());
		}
		
		if (queryAll || deviceObj.containsKey(JSONKey.DeviceCard))
		{
			JSONObject deviceCardObj = deviceObj.getJSONObject(JSONKey.DeviceCard);
			JSONObject deviceCardResponse = new JSONObject();
			deviceResponse.put(JSONKey.DeviceCard, deviceCardResponse);
			
			queryDeviceCard(deviceCardObj, deviceCardResponse);
		}
		
		if (queryAll || deviceObj.containsKey(JSONKey.Profile))
		{
			JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
			JSONObject profileResponse = new JSONObject();
			deviceResponse.put(JSONKey.Profile, profileResponse);
			
			queryProfile(profileObj, profileResponse);
		}
	}
	
	private void queryProfile(JSONObject profileObj, JSONObject profileResponse)
	{
		boolean queryAll = false;
		if (profileObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (profileObj.isEmpty())
			{
				return;
			}
		}
		
		Profile profile = deviceWrapper.getDevice().getProfile();
		if (queryAll || profileObj.containsKey(JSONKey.ProfileId))
		{
			profileResponse.put(JSONKey.ProfileId, profile.getProfileId());
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.ServiceStatus))
		{
			profileResponse.put(JSONKey.ServiceStatus, profile.getServiceStatus());
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.UnbanDate))
		{
			profileResponse.put(JSONKey.UnbanDate, profile.getUnbanDate());
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.LastActivityTime))
		{
			profileResponse.put(JSONKey.LastActivityTime, DateUtil.getDateStringByLongValue(profile.getLastActivityTime()));
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.CreateTime))
		{
			profileResponse.put(JSONKey.CreateTime, DateUtil.getDateStringByLongValue(profile.getCreateTime()));
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.Active))
		{
			profileResponse.put(JSONKey.Active, Consts.YesNo.parseValue(profile.getActive()).getValue());
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.InterestCard))
		{
			JSONObject interestCardObj = profileObj.getJSONObject(JSONKey.InterestCard);
			JSONObject interestCardResponse = new JSONObject();
			profileResponse.put(JSONKey.Profile, interestCardResponse);
			
			queryInterestCard(interestCardObj, interestCardResponse);
		}
		
		if (queryAll || profileObj.containsKey(JSONKey.ImpressCard))
		{
			JSONObject impressCardObj = profileObj.getJSONObject(JSONKey.ImpressCard);
			JSONObject impressCardResponse = new JSONObject();
			profileResponse.put(JSONKey.Profile, impressCardResponse);
			
			queryImpressCard(impressCardObj, impressCardResponse);
		}
	}
	
	private void queryImpressCard(JSONObject impressCardObj, JSONObject impressCardResponse)
	{
		boolean queryAll = false;
		if (impressCardObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (impressCardObj.isEmpty())
			{
				return;
			}
		}
		
		Impresscard card = deviceWrapper.getDevice().getProfile().getImpresscard();
		if (queryAll || impressCardObj.containsKey(JSONKey.ImpressCardId))
		{
			impressCardResponse.put(JSONKey.ImpressCardId, card.getImpressCardId());
		}
		
		if (queryAll || impressCardObj.containsKey(JSONKey.ChatTotalCount))
		{
			impressCardResponse.put(JSONKey.ChatTotalCount, card.getChatTotalCount());
		}
		
		if (queryAll || impressCardObj.containsKey(JSONKey.ChatTotalDuration))
		{
			impressCardResponse.put(JSONKey.ChatTotalDuration, card.getChatTotalDuration());
		}
		
		if (queryAll || impressCardObj.containsKey(JSONKey.ChatLossCount))
		{
			impressCardResponse.put(JSONKey.ChatLossCount, card.getChatLossCount());
		}
		
		if (queryAll || impressCardObj.containsKey(JSONKey.ImpressCard))
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
	
	private void queryInterestCard(JSONObject interestCardObj, JSONObject interestCardResponse)
	{
		boolean queryAll = false;
		if (interestCardObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (interestCardObj.isEmpty())
			{
				return;
			}
		}
		
		Interestcard card = deviceWrapper.getDevice().getProfile().getInterestcard();
		if (queryAll || interestCardObj.containsKey(JSONKey.InterestCardId))
		{
			interestCardResponse.put(JSONKey.InterestCardId, card.getInterestCardId());
		}
		
		if (queryAll || interestCardObj.containsKey(JSONKey.InterestLabelList))
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
	
	private void queryDeviceCard(JSONObject deviceCardObj, JSONObject deviceCardResponse)
	{
		boolean queryAll = false;
		if (deviceCardObj == null)
		{
			queryAll = true;
		}
		else
		{
			if (deviceCardObj.isEmpty())
			{
				return;
			}
		}
		
		Devicecard card = deviceWrapper.getDevice().getDevicecard();
		if (queryAll || deviceCardObj.containsKey(JSONKey.DeviceCardId))
		{
			deviceCardResponse.put(JSONKey.DeviceCardId, card.getDeviceCardId());
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.DeviceId))
		{
			deviceCardResponse.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.RegisterTime))
		{
			deviceCardResponse.put(JSONKey.RegisterTime, DateUtil.getDateStringByLongValue(card.getRegisterTime()));
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.DeviceModel))
		{
			deviceCardResponse.put(JSONKey.DeviceModel, card.getDeviceModel());
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.OsVersion))
		{
			deviceCardResponse.put(JSONKey.OsVersion, card.getOsVersion());
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.AppVersion))
		{
			deviceCardResponse.put(JSONKey.AppVersion, card.getAppVersion());
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.Location))
		{
			deviceCardResponse.put(JSONKey.Location, card.getLocation());
		}
		
		if (queryAll || deviceCardObj.containsKey(JSONKey.IsJailed))
		{
			deviceCardResponse.put(JSONKey.IsJailed, card.getIsJailed());
		}
	}
	
	private void update(JSONObject updateObj, JSONObject response)
	{
		if (updateObj == null)
		{
			return;
		}
		JSONObject dataUpdateResponse = new JSONObject();
		response.put(JSONKey.DataUpdate, dataUpdateResponse);
		
		JSONObject deviceObj = updateObj.getJSONObject(JSONKey.Device);
		updateDevice(deviceObj, dataUpdateResponse);
	}
	
	private void updateDevice(JSONObject deviceObj, JSONObject dataUpdateResponse)
	{
		JSONObject deviceResponse = new JSONObject();
		dataUpdateResponse.put(JSONKey.Device, deviceResponse);
		Device device = deviceWrapper.getDevice();
		device.setDeviceSn(header.getString(JSONKey.DeviceSn));
		
		// Update device card if needed
		if (deviceObj.containsKey(JSONKey.DeviceCard))
		{
			updateDeviceCard(deviceObj, deviceResponse);
		}
		
		
		if (deviceObj.containsKey(JSONKey.Profile))
		{
			updateProfile(deviceObj, deviceResponse);
		}
	}
	
	private void updateProfile(JSONObject deviceObj, JSONObject deviceResponse)
	{
		JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
		JSONObject profileResponse = new JSONObject();
		
		parseProfile(profileObj, profileResponse);
		deviceResponse.put(JSONKey.Profile, profileResponse);
		
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			updateInterestCard(profileObj, profileResponse);
		}

		/*
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			// currently there is no update of impresscard from APP
		}
		*/
	}
	
	private void updateInterestCard(JSONObject profileObj, JSONObject profileResponse)
	{
		JSONObject interestCardObj = profileObj.getJSONObject(JSONKey.InterestCard);
		JSONObject interestCardResponse = new JSONObject();
		parseInterestCard(interestCardObj, interestCardResponse);
		profileResponse.put(JSONKey.InterestCard, interestCardResponse);
	}
	
	private void updateDeviceCard(JSONObject deviceObj, JSONObject deviceResponse)
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
		
		// Save default solid impress label for new device
		Set<Impresslabelmap> impressLabelMaps = new HashSet<Impresslabelmap>();
		GlobalimpresslabelDAO dao = new GlobalimpresslabelDAO();
		List<Globalimpresslabel> globalLabelList;
		for (Consts.SolidAssessLabel label : Consts.SolidAssessLabel.values())
		{
			if (label == Consts.SolidAssessLabel.Invalid)
			{
				continue;
			}
			
			String strValue = label.getValue();
			Globalimpresslabel impressLabel;
			globalLabelList = dao.findByImpressLabel(strValue);
			if (globalLabelList.size() == 0)
			{
				impressLabel = new Globalimpresslabel();
				impressLabel.setGlobalAssessCount(0);
				impressLabel.setImpressLabel(label.getValue());
			}
			else
			{
				impressLabel = globalLabelList.get(0);
			}
			
			Impresslabelmap labelMap = new Impresslabelmap();
			labelMap.setAssessCount(0);
			labelMap.setAssessedCount(0);
			labelMap.setGlobalimpresslabel(impressLabel);
			labelMap.setImpresscard(impressCard);
			labelMap.setUpdateTime(System.currentTimeMillis());
			
			impressLabelMaps.add(labelMap);
		}
		
		impressCard.setImpresslabelmaps(impressLabelMaps);
		
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
			responseObj.put(JSONKey.InterestLabel, Consts.SuccessOrFail.Success.getValue());
			interestLabelMapObj.setGlobalinterestlabel(globalInterestObj);
			
			interestLabelMapObj.setLabelOrder(Integer.parseInt(tmpJSONObj.getString(JSONKey.LabelOrder)));
			responseObj.put(JSONKey.LabelOrder, Consts.SuccessOrFail.Success.getValue());
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
			response.put(JSONKey.DeviceModel, Consts.SuccessOrFail.Success.getValue());
			deviceCard.setDeviceModel(temp);
		}
		
		temp = jsonObj.getString(JSONKey.OsVersion);
		if (temp != null)
		{
			response.put(JSONKey.OsVersion, Consts.SuccessOrFail.Success.getValue());
			deviceCard.setOsVersion(temp);
		}
		
		temp = jsonObj.getString(JSONKey.AppVersion);
		if (temp != null)
		{
			response.put(JSONKey.AppVersion, Consts.SuccessOrFail.Success.getValue());
			deviceCard.setAppVersion(temp);
		}
		
		temp = jsonObj.getString(JSONKey.IsJailed);
		if (temp != null)
		{
			response.put(JSONKey.IsJailed, Consts.SuccessOrFail.Success.getValue());
			Consts.YesNo isJailed = Consts.YesNo.parseValue(jsonObj.getIntValue(JSONKey.IsJailed)); 
			deviceCard.setIsJailed(isJailed.name());
		}
		
		temp = jsonObj.getString(JSONKey.Location);
		if (temp != null)
		{
			response.put(JSONKey.Location, Consts.SuccessOrFail.Success.getValue());
			deviceCard.setLocation(temp);
		}
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AppDataSyncRequest;
	}
}
