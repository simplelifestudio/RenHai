/**
 * AppDataSyncRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

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
				String deviceSn = deviceCardObj.getString(JSONKey.FieldName.DeviceSn);
				if(deviceCardDAO.findByDeviceSn(deviceSn).size() == 0)
				{
					newDevice(deviceCardObj);
				}
				else
				{
					loadAndSaveDeviceCard(deviceCardObj);
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
	private void loadAndSaveDeviceCard(JSONObject deviceCardObj)
	{
		// TODO 加载数据库中的设备卡片及Profile；更新app提交的数据；保存到数据库中；
	}
	
	private void newDevice(JSONObject deviceCardObj)
	{
		DevicecardDAO deviceCardDAO = new DevicecardDAO();
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		// Create new impress card
		Impresscard impressCard = deviceWrapper.getDevice().getImpresscard(
				GlobalSetting.BusinessSetting.DefaultLoadedImpressLabel);
		ImpresscardDAO impressCardDAO = new ImpresscardDAO();
		impressCardDAO.save(impressCard);
		
		// Create new interest card
		Interestcard interestCard = deviceWrapper.getDevice().getInterestcard();
		InterestcardDAO interestCardDAO = new InterestcardDAO();
		interestCardDAO.save(interestCard);
		
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
		profileDAO.save(profile);
		
		// Create new deviceCard
		Devicecard deviceCard = new Devicecard();
		deviceCardDAO.save(deviceCard);
		
		Device device = new Device();
		
		device.setDevicecard(deviceCard);
		device.setProfile(profile);
		
		deviceWrapper.setDevice(device);
	}
	
	private void parseInterestCard(JSONObject jsonObj, Interestcard card)
	{
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
	}
	
	private void parseDevicecard(JSONObject jsonObj, Devicecard device)
	{
		String temp = jsonObj.getString(JSONKey.FieldName.DeviceSn);
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
		
		temp = jsonObj.getString(JSONKey.FieldName.AppVersion);
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
