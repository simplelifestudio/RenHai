/**
 * AppDataSyncRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.log.LogFactory;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.db.InterestlabelmapDAO;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.Consts.MessageType;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
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
		messageId = Consts.MessageId.AppDataSyncRequest;
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
			this.setErrorDescription(JSONKey.Device + " must be provided under " + JSONKey.DataUpdate);
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
		/*
		 * if (!deviceObj.containsKey(JSONKey.DeviceSn)) { String temp =
		 * JSONKey.DeviceSn + " must be provided for device.";
		 * logger.error(temp);
		 * this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
		 * this.setErrorDescription(temp); return false; }
		 */
		
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
		
		if (interestLabelList.isEmpty())
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
			if (!tmpObj.containsKey(JSONKey.InterestLabelName))
			{
				String temp = JSONKey.InterestLabelName + " must be provided in " + i + " label in "
						+ JSONKey.InterestLabelList;
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!checkNoEmptyAllowed(tmpObj, JSONKey.InterestLabelName))
			{
				return false;
			}
			
			if (!tmpObj.containsKey(JSONKey.LabelOrder))
			{
				String temp = JSONKey.LabelOrder + " must be provided in " + i + " label in "
						+ JSONKey.InterestLabelList;
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			tmpStr = tmpObj.getString(JSONKey.LabelOrder);
			if (!CommonFunctions.IsNumric(tmpStr))
			{
				String temp = JSONKey.LabelOrder + " must be numric in " + i + " label in "
						+ JSONKey.InterestLabelList;
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
			String temp = "Neither " + JSONKey.InterestCard + " nor " + JSONKey.ImpressCard
					+ " is included in " + JSONKey.Profile;
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
		
		String deviceSn = header.getString(JSONKey.DeviceSn);
		if (DBQueryUtil.isNewDevice(deviceSn))
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
		String deviceSn = header.getString(JSONKey.DeviceSn);
		if (DBQueryUtil.isNewDevice(deviceSn))
		{
			if (!body.containsKey(JSONKey.DataUpdate))
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.DataUpdate + " must be provided for new device.");
				return false;
			}
			
			JSONObject updateObj = body.getJSONObject(JSONKey.DataUpdate);
			if (!updateObj.containsKey(JSONKey.Device))
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.Device + " under " + JSONKey.DataUpdate
						+ " must be provided for new device.");
				return false;
			}
			
			JSONObject deviceObj = updateObj.getJSONObject(JSONKey.Device);
			if (!deviceObj.containsKey(JSONKey.DeviceCard))
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.DeviceCard + " under " + JSONKey.Device
						+ " must be provided for new device.");
				return false;
			}
			
			/*
			 * if (!deviceObj.containsKey(JSONKey.DeviceSn)) {
			 * this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			 * this.setErrorDescription(JSONKey.DeviceSn + " under " +
			 * JSONKey.Device + " must be provided for new device."); return
			 * false; }
			 */
			// 检查到这里就可以了，只要提供了DeviceCard，接下来具体字段的检查放到checkUpdateDeviceCard中
		}
		
		if (body.containsKey(JSONKey.DataUpdate))
		{
			JSONObject updateObj = getJSONObject(body, JSONKey.DataUpdate);
			if (updateObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.DataUpdate + " can't be null.");
				return false;
			}
			
			if (!checkUpdate(body.getJSONObject(JSONKey.DataUpdate)))
			{
				return false;
			}
		}
		else if (body.containsKey(JSONKey.DataQuery))
		{
			JSONObject queryObj = getJSONObject(body, JSONKey.DataQuery);
			if (queryObj == null)
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription(JSONKey.DataQuery + " can't be null.");
				return false;
			}
			
			if (!queryObj.isEmpty())
			{
				if (!queryObj.containsKey(JSONKey.Device))
				{
					this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
					this.setErrorDescription(JSONKey.Device + " must be provided under " + JSONKey.DataQuery);
					return false;
				}
			}
		}
		else
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription("Neither " + JSONKey.DataQuery + " nor " + JSONKey.DataUpdate
					+ " is included in request");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void doRun()
	{
		/*
		if (deviceWrapper.getOwnerOnlineDevicePool() == null)
		{
			logger.debug("Device <{}> synchronizing after connnection was released",
					deviceWrapper.getDeviceSn());
			deviceWrapper.bindOnlineDevicePool(OnlineDevicePool.instance);
		}
		*/
		
		if (!checkJSONRequest())
		{
			responseError();
			return;
		}
		
		DbLogger.saveSystemLog(Consts.OperationCode.AppDataSyncRequest_1004
    			, Consts.SystemModule.business
    			, header.getString(JSONKey.DeviceSn));
		
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
			catch (Exception e)
			{
				this.setErrorCode(Consts.GlobalErrorCode.DBException_1001);
				this.setErrorDescription("Server internal error.");
				responseError();
				FileLogger.printStackTrace(e);
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
		
		if (syncType == SyncType.ExistentNotLoaded || syncType == SyncType.ExistentLoaded)
		{
			logger.debug("Device is Existent in DB, try to load it from DB.");
			loadDevice(deviceSn);
		}
		else if (syncType == SyncType.NewDevice)
		{
			logger.debug("AppDataSyncRequest from new device.");
			if (body.containsKey(JSONKey.DataUpdate))
			{
				newDevice(deviceSn);
			}
			else
			{
				this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				this.setErrorDescription("It's not allowed to query a new device without update.");
				responseError();
				return;
			}
		}
		
		Device device = deviceWrapper.getDevice();
		Profile profile = device.getProfile();
		
		// Check if profile is banned
		if (!checkBanDate(profile))
		{
			return;
		}
		
		deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Idle, StatusChangeReason.AppDataSynchronize);
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.AppDataSyncResponse);
		if (profile.getServiceStatus().equals(Consts.ServiceStatus.Normal.name()))
		{
			// Only allow normal device to update profile
			if (body.containsKey(JSONKey.DataUpdate))
			{
				update(body.getJSONObject(JSONKey.DataUpdate), response.getBody());
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
		
		// Set deviceId again to fill actual id in DB
		response.addToHeader(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
		
		DbLogger.saveSystemLog(Consts.OperationCode.AppDataSyncResponse_1007
    			, Consts.SystemModule.business
    			, header.getString(JSONKey.DeviceSn));
		OutputMessageCenter.instance.addMessage(response);
	}
	
	private boolean checkBanDate(Profile profile)
	{
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
			}
			else
			{
				OnlineDevicePool.instance.IdentifyBannedDevice(deviceWrapper);
				this.setErrorCode(Consts.GlobalErrorCode.NoPermission_1102);
				this.setErrorDescription("Device was banned till "
						+ DateUtil.getDateStringByLongValue(unbanDate));
				responseError();
				return false;
			}
		}
		return true;
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
		Device device = DAOWrapper.getDeviceInCache(deviceSn);
		if (device != null)
		{
			logger.debug("Find Device object in DB cache, reuse it directly");
			deviceWrapper.setDevice(device);
			return;
		}
		
		if (DBQueryUtil.isNewDevice(deviceSn))
		{
			logger.error("DeviceCard can't be found in DB with SN: " + deviceSn);
			return;
		}
		
		try
		{
			DeviceDAO dao = new DeviceDAO();
			device = dao.findByDeviceSn(deviceSn).get(0);
			if (device == null)
			{
				logger.error("Fatal error, device load from DB by SN <{}> is null!", deviceSn);
			}
			deviceWrapper.setDevice(device);
		}
		catch(Exception e)
		{
			logger.error("Fatal error when trying to load device <{}> from DB", deviceSn);
			FileLogger.printStackTrace(e);
		}
	}
	
	private void query(JSONObject queryObj, JSONObject response)
	{
		if (queryObj == null || queryObj.isEmpty())
		{
			JSONObject queryResponse = new JSONObject();
			response.put(JSONKey.DataQuery, queryResponse);
			queryResponse.put(JSONKey.Device, deviceWrapper.toJSONObject_Device());
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
		if (deviceObj == null)
		{
			queryResponse.put(JSONKey.Device, deviceWrapper.toJSONObject_Device());
			return;
		}
		else
		{
			if (deviceObj.isEmpty())
			{
				queryResponse.put(JSONKey.Device, new JSONObject());
				return;
			}
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
			queryDeviceCard(deviceCardObj, deviceResponse);
		}
		
		if (deviceObj.containsKey(JSONKey.Profile))
		{
			JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
			queryProfile(profileObj, deviceResponse);
		}
	}
	
	private void queryProfile(JSONObject profileObj, JSONObject deviceResponse)
	{
		// boolean queryAll = false;
		if (profileObj == null)
		{
			deviceResponse.put(JSONKey.Profile, deviceWrapper.toJSONObject_Profile());
			return;
		}
		else
		{
			if (profileObj.isEmpty())
			{
				deviceResponse.put(JSONKey.Profile, new JSONObject());
				return;
			}
		}
		
		JSONObject profileResponse = new JSONObject();
		deviceResponse.put(JSONKey.Profile, profileResponse);
		
		Profile profile = deviceWrapper.getDevice().getProfile();
		if (profileObj.containsKey(JSONKey.ProfileId))
		{
			profileResponse.put(JSONKey.ProfileId, profile.getProfileId());
		}
		
		if (profileObj.containsKey(JSONKey.ServiceStatus))
		{
			profileResponse.put(JSONKey.ServiceStatus,
					Consts.ServiceStatus.parseFromStringValue(profile.getServiceStatus()).getValue());
		}
		
		if (profileObj.containsKey(JSONKey.UnbanDate))
		{
			if (profile.getUnbanDate() == null)
			{
				profileResponse.put(JSONKey.UnbanDate, null);
			}
			else
			{
				profileResponse.put(JSONKey.UnbanDate,
						DateUtil.getDateStringByLongValue(profile.getUnbanDate()));
			}
		}
		
		if (profileObj.containsKey(JSONKey.LastActivityTime))
		{
			profileResponse.put(JSONKey.LastActivityTime,
					DateUtil.getDateStringByLongValue(profile.getLastActivityTime()));
		}
		
		if (profileObj.containsKey(JSONKey.CreateTime))
		{
			profileResponse.put(JSONKey.CreateTime,
					DateUtil.getDateStringByLongValue(profile.getCreateTime()));
		}
		
		if (profileObj.containsKey(JSONKey.Active))
		{
			profileResponse.put(JSONKey.Active, Consts.YesNo.parseValue(profile.getActive()).getValue());
		}
		
		if (profileObj.containsKey(JSONKey.InterestCard))
		{
			JSONObject interestCardObj = profileObj.getJSONObject(JSONKey.InterestCard);
			queryInterestCard(interestCardObj, profileResponse, profile);
		}
		
		if (profileObj.containsKey(JSONKey.ImpressCard))
		{
			JSONObject impressCardObj = profileObj.getJSONObject(JSONKey.ImpressCard);
			queryImpressCard(impressCardObj, profileResponse, profile);
		}
	}
	
	private void queryImpressCard(JSONObject impressCardObj, JSONObject profileResponse, Profile profile)
	{
		if (impressCardObj == null)
		{
			profileResponse.put(JSONKey.ImpressCard, deviceWrapper.toJSONObject_ImpressCard(profile));
			return;
		}
		else
		{
			if (impressCardObj.isEmpty())
			{
				profileResponse.put(JSONKey.ImpressCard, new JSONObject());
				return;
			}
		}
		
		JSONObject impressCardResponse = new JSONObject();
		profileResponse.put(JSONKey.ImpressCard, impressCardResponse);
		
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
			int impressLabelCount;
			if (impressCardObj != null && impressCardObj.containsKey(JSONKey.ImpressLabelList))
			{
				impressLabelCount = impressCardObj.getIntValue(JSONKey.ImpressLabelList);
			}
			else
			{
				impressLabelCount = card.getImpresslabelmaps().size();
			}
			deviceWrapper.toJSONObject_ImpressLabels(card, impressCardResponse, impressLabelCount);
		}
	}
	
	private void queryInterestCard(JSONObject interestCardObj, JSONObject profileResponse, Profile profile)
	{
		if (interestCardObj == null)
		{
			profileResponse.put(JSONKey.InterestCard, deviceWrapper.toJSONObject_InterestCard(profile));
			return;
		}
		else
		{
			if (interestCardObj.isEmpty())
			{
				profileResponse.put(JSONKey.InterestCard, new JSONObject());
				return;
			}
		}
		
		JSONObject interestCardResponse = new JSONObject();
		profileResponse.put(JSONKey.InterestCard, interestCardResponse);
		
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
				labelObj.put(JSONKey.GlobalImpressLabelId, label.getGlobalinterestlabel()
						.getGlobalInterestLabelId());
				labelObj.put(JSONKey.InterestLabelName, label.getGlobalinterestlabel().getInterestLabelName());
				labelObj.put(JSONKey.GlobalMatchCount, label.getGlobalinterestlabel().getGlobalMatchCount());
				labelObj.put(JSONKey.LabelOrder, label.getLabelOrder());
				labelObj.put(JSONKey.MatchCount, label.getMatchCount());
				labelObj.put(JSONKey.ValidFlag, Consts.ValidInvalid.Valid.getValue());
				labels.add(labelObj);
			}
		}
	}
	
	private void queryDeviceCard(JSONObject deviceCardObj, JSONObject deviceResponse)
	{
		if (deviceCardObj == null)
		{
			deviceResponse.put(JSONKey.DeviceCard, deviceWrapper.toJSONObject_DeviceCard());
			return;
		}
		else
		{
			if (deviceCardObj.isEmpty())
			{
				deviceResponse.put(JSONKey.DeviceCard, new JSONObject());
				return;
			}
		}
		
		JSONObject deviceCardResponse = new JSONObject();
		deviceResponse.put(JSONKey.DeviceCard, deviceCardResponse);
		
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
			deviceCardResponse.put(JSONKey.RegisterTime,
					DateUtil.getDateStringByLongValue(card.getRegisterTime()));
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
			deviceCardResponse.put(JSONKey.IsJailed, Consts.YesNo.parseValue(card.getIsJailed()).getValue());
		}
	}
	
	private void update(JSONObject updateObj, JSONObject response)
	{
		if (updateObj != null)
		{
			JSONObject dataUpdateResponse = new JSONObject();
			response.put(JSONKey.DataUpdate, dataUpdateResponse);
			
			JSONObject deviceObj = updateObj.getJSONObject(JSONKey.Device);
			updateDevice(deviceObj, dataUpdateResponse);
			
			Profile profile = deviceWrapper.getDevice().getProfile();
			profile.setActive(Consts.YesNo.Yes.name());
		}
		
		/*
		Transaction t = null;
		try
		{
			LoggerFactory.getLogger("DB").debug("Begins transaction of saving device: ", deviceWrapper.getDeviceSn());
			t= hibernateSesion.beginTransaction();
			hibernateSesion.save(deviceWrapper.getDevice());
			t.commit();
			LoggerFactory.getLogger("DB").debug("Committed transaction of saving device: ", deviceWrapper.getDeviceSn());
		}
		catch (Exception e)
		{
			if (t != null)
			{
				t.rollback();
			}
			FileLogger.printStackTrace(e);
		}
		*/
	}
	
	private void updateDevice(JSONObject deviceObj, JSONObject dataUpdateResponse)
	{
		JSONObject deviceResponse = new JSONObject();
		dataUpdateResponse.put(JSONKey.Device, deviceResponse);
		Device device = deviceWrapper.getDevice();
		device.setDeviceSn(header.getString(JSONKey.DeviceSn));
		
		if (deviceObj.containsKey(JSONKey.DeviceSn))
		{
			// All command use deviceSn in header
			// Report error to App if there is update of DeviceSn here
			dataUpdateResponse.put(JSONKey.DeviceSn, 0);
		}
		
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
		 * if (profileObj.containsKey(JSONKey.InterestCard)) { // currently
		 * there is no update of impresscard from APP }
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
		Interestcard interestCard = new Interestcard();
		
		Set<Impresslabelmap> impressLabelMaps = new HashSet<Impresslabelmap>();
		for (Consts.SolidAssessLabel label : Consts.SolidAssessLabel.values())
		{
			if (label == Consts.SolidAssessLabel.Invalid)
			{
				continue;
			}
			
			String strValue = label.getValue();
			Globalimpresslabel impressLabel = DBQueryUtil.getGlobalimpresslabel(strValue);
			if (impressLabel == null)
			{
				impressLabel = new Globalimpresslabel();
				impressLabel.setGlobalAssessCount(0);
				impressLabel.setImpressLabelName(strValue);
				DAOWrapper.cache(impressLabel);
			}
			
			Impresslabelmap labelMap = new Impresslabelmap();
			labelMap.setAssessCount(0);
			labelMap.setAssessedCount(0);
			labelMap.setGlobalimpresslabel(impressLabel);
			labelMap.setImpresscard(impressCard);
			labelMap.setUpdateTime(System.currentTimeMillis());
			
			impressLabelMaps.add(labelMap);
		}
		
		Profile profile = new Profile();
		Devicecard deviceCard = new Devicecard();
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDeviceSn(deviceSn);
		device.setDevicecard(deviceCard);
		deviceCard.setDevice(device);
		device.setProfile(profile);
		profile.setDevice(device);
		
		impressCard.setChatLossCount(0);
		impressCard.setChatTotalCount(0);
		impressCard.setChatTotalDuration(0);
		
		// Save default solid impress label for new device
		impressCard.setImpresslabelmaps(impressLabelMaps);
		
		// Create new interest card
		// Create new profile
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
		
		deviceCard.setRegisterTime(now);
		
		// Bind DeviceWrapper with Device
		deviceWrapper.setDevice(device);
		deviceWrapper.updateActivityTime();
		deviceWrapper.updatePingTime();
		deviceWrapper.setServiceStatus(Consts.ServiceStatus.Normal);
	}
	
	private void parseInterestLabels(
			JSONArray interestLabelList,
			Interestcard card,
			JSONArray responseArray
			)
	{
		JSONObject tmpJSONObj;
		JSONObject responseObj;
		String tempStr;
		
		Globalinterestlabel globalInterest;
		
		Interestlabelmap interestLabelMap;
		Set<Interestlabelmap> labelMapSet = card.getInterestlabelmaps();
		
		// Save old label in temp Map
		HashMap<String, Interestlabelmap> tempMap = new HashMap<String, Interestlabelmap>();
		for (Interestlabelmap label : labelMapSet)
		{
			tempMap.put(label.getGlobalinterestlabel().getInterestLabelName(), label);
		}
		
		labelMapSet.clear();
		//label.setInterestcard(null);
		
		// Check all labels from APP
		for (int i = 0; i < interestLabelList.size(); i++)
		{
			tmpJSONObj = interestLabelList.getJSONObject(i);
			responseObj = new JSONObject();
			
			tempStr = tmpJSONObj.getString(JSONKey.InterestLabelName);
			
			if (isLabelInLabelMapSet(labelMapSet, tempStr))
			{
				tempMap.remove(tempStr);
				continue;
			}
			
			// Check if it's old label
			if (tempMap.containsKey(tempStr))
			{
				interestLabelMap = tempMap.get(tempStr);
				interestLabelMap.setLabelOrder(tmpJSONObj.getInteger(JSONKey.LabelOrder));
				labelMapSet.add(interestLabelMap);
				tempMap.remove(tempStr);
				
				responseObj.put(JSONKey.InterestLabelName, tempStr);
				responseObj.put(JSONKey.LabelOrder, Consts.SuccessOrFail.Success.getValue());
				responseArray.add(responseObj);
				continue;
			}
			
			interestLabelMap = new Interestlabelmap();
			
			globalInterest = DBQueryUtil.getGlobalinterestlabel(tempStr);
			
			// Check if it's new interest label for this device but existent global interest label
			if (globalInterest == null)
			{
				logger.debug("New interest label " +  tempStr + " submitted by device <{}>", deviceWrapper.getDeviceSn());
				globalInterest = new Globalinterestlabel();
				globalInterest.setInterestLabelName(tempStr);
				globalInterest.setGlobalMatchCount(0);
				DAOWrapper.cache(globalInterest);
			}
			
			interestLabelMap.setMatchCount(0);
			interestLabelMap.setValidFlag(Consts.ValidInvalid.Valid.name());
			interestLabelMap.setGlobalinterestlabel(globalInterest);
			interestLabelMap.setLabelOrder(tmpJSONObj.getInteger(JSONKey.LabelOrder));
			interestLabelMap.setInterestcard(card);
			labelMapSet.add(interestLabelMap);
			
			responseObj.put(JSONKey.InterestLabelName, tempStr);
			responseObj.put(JSONKey.LabelOrder, Consts.SuccessOrFail.Success.getValue());
			responseArray.add(responseObj);
		}
		
		// delete label that are still in tempMap
		Set <String> keySet = tempMap.keySet();
		InterestlabelmapDAO interestLabelDAO = new InterestlabelmapDAO();
		
		for (String key : keySet)
		{
			interestLabelMap = tempMap.get(key);
			//interestLabelMap.setInterestcard(null);
			interestLabelDAO.delete(interestLabelMap);
		}
	}
	
	private boolean isLabelInLabelMapSet(Set<Interestlabelmap> labelMapSet, String label)
	{
		for (Interestlabelmap labelMap : labelMapSet)
		{
			if (labelMap.getGlobalinterestlabel().getInterestLabelName().equals(label))
			{
				return true;
			}
		}
		return false;
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
		parseInterestLabels(interestLabelList, card, responseArray);
		
		//card.setInterestlabelmaps(labelMapList);
		
		/*
		if (labelMapList.size() > 0)
		{
			String interestCardId = DBQueryUtil.getIDByDeviceSn(TableColumnName.InterestCardId,
					deviceWrapper.getDeviceSn());
			
			if (interestCardId != null)
			{
				String sql = "delete from " + TableName.InterestLabelMap + " where "
						+ TableColumnName.InterestCardId + " = " + interestCardId;
				DAOWrapper.executeSql(sql);
			}
		}
		*/
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
