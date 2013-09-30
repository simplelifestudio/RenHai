/**
 * BusinessSessionRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;


import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.Consts;


/**
 * 
 */
public class BusinessSessionRequest extends AppJSONMessage
{
	private Logger logger = JSONModule.instance.getLogger();
	private Consts.OperationType operationType;
	
	/**
	 * @param jsonObject
	 */
	public BusinessSessionRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
	{
		if (!body.containsKey(JSONKey.BusinessType))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.BusinessType + " can't be found.");
			return false;
		}
		
		if (!body.containsKey(JSONKey.OperationType))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationType + " can't be found.");
			return false;
		}
		
		if (!checkNoEmptyAllowed(body, JSONKey.OperationType))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationType + " can't be empty.");
			return false;
		}
		
		int intType = body.getIntValue(JSONKey.OperationType);
		operationType = Consts.OperationType.parseValue(intType);
		if (operationType == Consts.OperationType.Invalid)
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription("Invalid " + JSONKey.OperationType + ": " + intType);
			return false;
		}
		
		if (operationType == Consts.OperationType.AssessAndContinue
				|| operationType == Consts.OperationType.AssessAndQuit)
		{
			if (!checkAssess())
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean checkAssess()
	{
		// Information of target device must be provided
		if (!body.containsKey(JSONKey.OperationInfo))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationInfo + " can't be found.");
			return false;
		}
		
		JSONObject infoObj = getJSONObject(body, JSONKey.OperationInfo);
		if (infoObj == null)
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.OperationInfo + " must be provided correctly.");
			return false;
		}
		
		JSONObject deviceObj = getJSONObject(infoObj, JSONKey.Device);
		if (deviceObj == null)
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.Device + " must be provided correctly.");
			return false;
		}
		
		JSONObject profileObj = getJSONObject(deviceObj, JSONKey.Profile);
		if (profileObj == null)
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.Profile + " must be provided correctly.");
			return false;
		}
		
		
		JSONObject impressObj = getJSONObject(profileObj, JSONKey.ImpressCard);
		if (impressObj == null)
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.ImpressCard + " must be provided correctly.");
			return false;
		}
		
		JSONArray assessLabelList = getJSONArray(impressObj, JSONKey.AssessLabelList);
		if (assessLabelList == null)
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription(JSONKey.AssessLabelList + " must be provided correctly.");
			return false;
		}
		
		if (assessLabelList.size() != 1)
		{
			// 好评，中评，差评只能有一个
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.AssessLabelList + " shall have only one impress label.");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void run()
	{
		if (!checkJSONRequest())
		{
			responseError(Consts.MessageId.BusinessSessionRequest.name());
			return;
		}
		
		switch (operationType)
		{
			case EnterPool:
				enterPool();
				break;
			case LeavePool:
				leavePool();
				break;
			case AgreeChat:
				agreeChat();
				break;
			case RejectChat:
				rejectChat();
				break;
			case EndChat:
				endChat();
				break;
			case AssessAndContinue:
				assessAndContinue();
				break;
			case AssessAndQuit:
				assessAndQuit();
				break;
			default:
				logger.error("Invalid operation type found: " + operationType.toString());
				break;
		}
	}
	
	private void enterPool()
	{
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType type = Consts.BusinessType.parseValue(intType);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		
		synchronized (deviceWrapper)
		{
			deviceWrapper.enterPool(type);
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationType, Consts.OperationType.EnterPool.getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		response.asyncResponse();
	}
	
	private void leavePool()
	{
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType type = Consts.BusinessType.parseValue(intType);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		onlinePool.getBusinessPool(type).onDeviceEnter(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.LeavePool.getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.asyncResponse();
	}
	
	private void agreeChat()
	{
		logger.debug("Device <{}> agreed chat.", deviceWrapper.getDeviceSn());
		deviceWrapper.getOwnerBusinessSession().onAgreeChat(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.AgreeChat.getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.asyncResponse();
	}
	
	private void rejectChat()
	{
		logger.debug("Device <{}> rejected chat.", deviceWrapper.getDeviceSn());
		deviceWrapper.getOwnerBusinessSession().onRejectChat(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.RejectChat.getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.asyncResponse();
	}
	
	private void endChat()
	{
		logger.debug("Device <{}> ended chat.", deviceWrapper.getDeviceSn());
		deviceWrapper.getOwnerBusinessSession().onEndChat(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.EndChat.getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.asyncResponse();
	}
	
	private void assessAndContinue()
	{
		assess(false);
	}
	
	private void assess(boolean quitAfterAssess)
	{
		logger.debug("Device <{}> provided assess.", deviceWrapper.getDeviceSn());
		String deviceSn = body.getJSONObject(JSONKey.OperationInfo)
				.getJSONObject(JSONKey.Device)
				.getString(JSONKey.DeviceSn);
		
		if (deviceSn.equals(deviceWrapper.getDeviceSn()))
    	{
    		logger.warn("Device <{}> is assessing itself", deviceSn);
    		return;
    	}
		
		if (DBQueryUtil.isNewDevice(deviceSn))
		{
			String temp = "Failed to assess Device <" + deviceSn + "> due to it's not in online pool";
			logger.error(temp);
			ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
					Consts.MessageId.BusinessSessionResponse);
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
			response.addToBody(JSONKey.BusinessType, deviceWrapper.getBusinessType().getValue());
			response.addToBody(JSONKey.OperationInfo, temp);
			response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Fail.getValue());
			response.asyncResponse();
			return;
		}
		
		DeviceDAO dao = new DeviceDAO();
		Device target = dao.findByDeviceSn(deviceSn).get(0);;
		Impresscard card = target.getProfile().getImpresscard();
		Set<Impresslabelmap> impressLabelMap = card.getImpresslabelmaps();
		
		JSONObject impressObj = body.getJSONObject(JSONKey.OperationInfo)
				.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard);
		
		JSONArray assessLabels = impressObj.getJSONArray(JSONKey.AssessLabelList);
		updateOrAppendImpressLabel(impressLabelMap, assessLabels.getString(0));
		
		JSONArray impressLabels = impressObj.getJSONArray(JSONKey.ImpressLabelList);
		for (int i = 0; i < impressLabels.size(); i++)
		{
			updateOrAppendImpressLabel(impressLabelMap, impressLabels.getString(i));
		}
		
		// Save to DB
		DBModule.instance.cache(target);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
		response.addToBody(JSONKey.BusinessType, deviceWrapper.getBusinessType().getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		
		if (quitAfterAssess)
		{
			deviceWrapper.getOwnerBusinessSession().onAssessAndQuit(this.deviceWrapper);
		}
		else
		{
			deviceWrapper.getOwnerBusinessSession().onAssessAndContinue(this.deviceWrapper);
		}
		response.asyncResponse();
	}
	
	private void updateOrAppendImpressLabel(Set<Impresslabelmap> impressLabels, String labelName)
	{
		//String labelName = impressLabel.getString(JSONKey.ImpressLabelName);
		for (Impresslabelmap label : impressLabels)
		{
			if (label.getGlobalimpresslabel().getImpressLabelName().equals(labelName))
			{
				label.setAssessedCount(label.getAssessedCount() + 1);
				label.setUpdateTime(System.currentTimeMillis());
				return;
			}
		}
		
		Globalimpresslabel globalimpresslabel = new Globalimpresslabel();
		globalimpresslabel.setGlobalAssessCount(1);
		globalimpresslabel.setImpressLabelName(labelName);
		globalimpresslabel.setImpresslabelmaps(impressLabels);
		
		Impresslabelmap labelMap = new Impresslabelmap();
		labelMap.setAssessCount(0);
		labelMap.setAssessedCount(1);
		labelMap.setGlobalimpresslabel(globalimpresslabel);
		labelMap.setUpdateTime(System.currentTimeMillis());
		
		impressLabels.add(labelMap);
	}
	
	private void assessAndQuit()
	{
		assess(true);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionRequest;
	}
}
