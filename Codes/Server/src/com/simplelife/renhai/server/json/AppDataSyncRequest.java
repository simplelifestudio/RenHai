/**
 * AppDataSyncRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.DevicecardDAO;
import com.simplelife.renhai.server.util.Consts;
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
	
	@Override
	protected boolean checkJsonCommand()
    {
		if (!super.checkJsonCommand())
		{
			return false;
		}
		
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
		if (!checkJsonCommand())
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
			JSONObject deviceCard = updateObj.getJSONObject(JSONKey.FieldName.Devicecard);
			updateDevicecard(deviceCard);
			
			DevicecardDAO dao = new DevicecardDAO();
			//String deviceSn = deviceWrapper.getDevice().getDevicecard().getDeviceSn();
			dao.attachDirty(deviceWrapper.getDevice().getDevicecard());
		}
		
		if (updateObj.containsKey(JSONKey.FieldName.Interestcard))
		{
			// TODO
		}
	}
	
	private void updateDevicecard(JSONObject deviceCardObj)
	{
		Devicecard device =  this.deviceWrapper.getDevice().getDevicecard();
		
		String temp = deviceCardObj.getString(JSONKey.FieldName.OsVersion);
		device.setOsVersion(temp);
		
		temp = deviceCardObj.getString(JSONKey.FieldName.AppVersion);
		if (temp != null)
		{
			device.setAppVersion(temp);
		}
		
		temp = deviceCardObj.getString(JSONKey.FieldName.IsJailed);
		if (temp != null)
		{
			device.setIsJailed("Yes");
		}
		
		temp = deviceCardObj.getString(JSONKey.FieldName.AppVersion);
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
