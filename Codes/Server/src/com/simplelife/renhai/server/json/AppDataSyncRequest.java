/**
 * AppDataSyncRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.Device;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.DevicecardDAO;
import com.simplelife.renhai.server.db.GlobalinterestlabelDAO;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.ImpresscardDAO;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.InterestcardDAO;
import com.simplelife.renhai.server.db.InterestlabelcollectionDAO;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.ProfileDAO;
import com.simplelife.renhai.server.db.TableColumnName;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class AppDataSyncRequest extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public AppDataSyncRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
    {
		boolean query = body.containsKey(JSONKey.FieldName.DataQuery);
		boolean update = body.containsKey(JSONKey.FieldName.DataUpdate);
		if (!(query || update))
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription("Neither " + JSONKey.FieldName.DataQuery + " nor " + JSONKey.FieldName.DataUpdate + " is included in request");
		}
		
		DevicecardDAO deviceCardDAO = new DevicecardDAO();
		String deviceSn = header.getString(JSONKey.FieldName.DeviceSn);
		if(deviceCardDAO.findByDeviceSn(deviceSn).size() == 0)
		{
			// For new devices, all requested fields must be provided
			if (!header.containsKey(JSONKey.FieldName.DeviceSn))
			{
				String temp = JSONKey.FieldName.DeviceSn + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			JSONObject updateObj = body.getJSONObject(JSONKey.FieldName.DataUpdate);
			JSONObject deviceCardObj = updateObj.getJSONObject(JSONKey.FieldName.Devicecard);
			if (!deviceCardObj.containsKey(JSONKey.FieldName.DeviceModel))
			{
				String temp = JSONKey.FieldName.DeviceModel + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!deviceCardObj.containsKey(JSONKey.FieldName.OsVersion))
			{
				String temp = JSONKey.FieldName.OsVersion + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
			
			if (!deviceCardObj.containsKey(JSONKey.FieldName.AppVersion))
			{
				String temp = JSONKey.FieldName.AppVersion + " must be provided.";
				logger.error(temp);
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(temp);
				return false;
			}
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
		
		if (body.containsKey(JSONKey.FieldName.DataUpdate))
		{
			dataUpdate(body.getJSONObject(JSONKey.FieldName.DataUpdate));
		}
	}

	private void dataUpdate(JSONObject updateObj)
	{
		if (updateObj == null)
		{
			return;
		}
		
		if (updateObj.containsKey(JSONKey.FieldName.Devicecard))
		{
			JSONObject deviceCardObj = updateObj.getJSONObject(JSONKey.FieldName.Devicecard);
			if (deviceWrapper.getDevice() != null)
			{
				// Device has been loaded before
				Devicecard card = deviceWrapper.getDevice().getDevicecard();
				parseDevicecard(deviceCardObj, card);
				
				DevicecardDAO dao = new DevicecardDAO();
				dao.save(card);
			}
			else
			{
				DevicecardDAO deviceCardDAO = new DevicecardDAO();
				String deviceSn = header.getString(JSONKey.FieldName.DeviceSn);
				if(deviceCardDAO.findByDeviceSn(deviceSn).size() == 0)
				{
					newDevice(deviceCardObj);
				}
				else
				{
					loadAndSaveDeviceCard(updateObj);
				}
			}
		}
		
		if (updateObj.containsKey(JSONKey.FieldName.Interestcard))
		{
			JSONObject interestCardObj = updateObj.getJSONObject(JSONKey.FieldName.Interestcard);
			updateAndSaveInterestCard(interestCardObj);
		}
	}
	
	private void updateAndSaveInterestCard(JSONObject interestCardObj)
	{
		// TODO 更新兴趣卡片；保存到数据库中
	}
	private void loadAndSaveDeviceCard(JSONObject dataUpdateObj)
	{
		// TODO 加载数据库中的设备卡片及Profile；更新app提交的数据；保存到数据库中；
		DevicecardDAO deviceCardDAO = new DevicecardDAO();
		//ImpresscardDAO impressCardDAO = new ImpresscardDAO();
		InterestcardDAO interestCardDAO = new InterestcardDAO();
		//ProfileDAO profileDAO = new ProfileDAO();
		String deviceSn = header.getString(JSONKey.FieldName.DeviceSn);
		
		List<Devicecard> cardList = deviceCardDAO.findByDeviceSn(deviceSn);
		if (cardList.size() == 0 )
		{
			logger.error("DeviceCard can't be found in DB with SN: " + deviceSn);
			return;
		}

		Devicecard deviceCard = cardList.get(0);
		Profile profile = deviceCard.getProfile();
		Impresscard impressCard = profile.getImpresscard();
		Interestcard interestCard = profile.getInterestcard();
		
		JSONObject deviceCardObj = dataUpdateObj.getJSONObject(JSONKey.FieldName.Devicecard);
		JSONObject interestCardObj = dataUpdateObj.getJSONObject(JSONKey.FieldName.Interestcard);
		
		parseDevicecard(deviceCardObj, deviceCard);
		parseInterestCard(interestCardObj, interestCard);
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		// TODO: save interestCard
		//interestCardDAO.save(interestCard);
		deviceCardDAO.save(deviceCard);
		session.getTransaction().commit();
	}
	
	private void newDevice(JSONObject deviceCardObj)
	{
		// Create new impress card
		Impresscard impressCard = new Impresscard();
		ImpresscardDAO impressCardDAO = new ImpresscardDAO();
		impressCard.setChatLossCount(0);
		impressCard.setChatTotalCount(0);
		impressCard.setChatTotalDuration(0);
		
		// Create new interest card
		Interestcard interestCard = new Interestcard();
		InterestcardDAO interestCardDAO = new InterestcardDAO();
		interestCard.setCreateTime(DateUtil.getNowDate().getTime());
		
		// Create new profile
		Profile profile = new Profile();
		profile.setImpresscard(impressCard);
		profile.setInterestcard(interestCard);
		long now = DateUtil.getNowDate().getTime();
		profile.setLastActivityTime(now);
		profile.setCreateTime(now);
		
		// Bind profile with cards
		profile.setInterestcard(interestCard);
		profile.setImpresscard(impressCard);
		
		// Save profile
		ProfileDAO profileDAO = new ProfileDAO();
		
		// Create new deviceCard and update to data from APP
		Devicecard deviceCard = new Devicecard();
		parseDevicecard(deviceCardObj, deviceCard);
		
		// Save device card
		DevicecardDAO deviceCardDAO = new DevicecardDAO();
		deviceCard.setProfile(profile);
		deviceCard.setRegisterTime(now);
		deviceCard.setServiceStatus(Consts.DeviceServiceStatus.Normal.name());
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		impressCardDAO.save(impressCard);
		interestCardDAO.save(interestCard);
		profileDAO.save(profile);
		deviceCardDAO.save(deviceCard);
		session.getTransaction().commit();
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDevicecard(deviceCard);
		device.setProfile(profile);
		
		// Bind DeviceWrapper with Device
		deviceWrapper.setDevice(device);
		
		// TODO: save interest label collection
	}
	
	private void parseImpressCard(JSONObject jsonObj, Impresscard card)
	{
		card.setChatLossCount(jsonObj.getInteger(JSONKey.FieldName.ChatLossCount));
		card.setChatTotalCount(jsonObj.getInteger(JSONKey.FieldName.ChatTotalCount));
		card.setChatTotalDuration(jsonObj.getInteger(JSONKey.FieldName.ChatTotalDuration));
	}
	
	private void parseInterestCard(JSONObject jsonObj, Interestcard card)
	{
		/*
		Set<String> keySet =  jsonObj.keySet();
		
		GlobalinterestlabelDAO globalLabelDAO = new GlobalinterestlabelDAO();
		InterestlabelcollectionDAO interestLabelDAO = new InterestlabelcollectionDAO();
		
		JSONObject labelObj;

		// TODO: 对比数据库中该兴趣卡片的所有标签和app提供的所有标签，将更新保存到数据库中
		for (String key : keySet)
		{
			labelObj = jsonObj.getJSONObject(key);
			if (interestLabelDAO.findByProperty(TableColumnName.InterestcardId, card.getInterestcardId()).size() > 0)
			{
				
			}
		}
		*/
	}
	
	private void parseDevicecard(JSONObject jsonObj, Devicecard device)
	{
		String temp = header.getString(JSONKey.FieldName.DeviceSn);
		if (temp != null)
		{
			device.setDeviceSn(temp);
		}
		
		temp = jsonObj.getString(JSONKey.FieldName.DeviceModel);
		if (temp != null)
		{
			device.setDeviceModel(temp);
		}
		
		temp = jsonObj.getString(JSONKey.FieldName.OsVersion);
		if (temp != null)
		{
			device.setOsVersion(temp);
		}
		
		temp = jsonObj.getString(JSONKey.FieldName.AppVersion);
		if (temp != null)
		{
			device.setAppVersion(temp);
		}
		
		temp = jsonObj.getString(JSONKey.FieldName.IsJailed);
		if (temp != null)
		{
			device.setIsJailed("Yes");
		}
		
		temp = jsonObj.getString(JSONKey.FieldName.Location);
		if (temp != null)
		{
			device.setLocation(temp);
		}
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AppDataSyncRequest;
	}
}
