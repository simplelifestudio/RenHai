/**
 * BusinessSessionRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;


import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessSessionEventType;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


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
		messageId = Consts.MessageId.BusinessSessionRequest;
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
		else
		{
			int intType = body.getIntValue(JSONKey.BusinessType);
			Consts.BusinessType businessType = Consts.BusinessType.parseValue(intType);
			if (businessType == Consts.BusinessType.Invalid)
			{
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription("Invalid business type");
				return false;
			}
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
		
		if (operationType != Consts.OperationType.ChooseBusiness
				&& operationType != Consts.OperationType.UnchooseBusiness
				&& operationType != Consts.OperationType.MatchStart
				&& operationType != Consts.OperationType.SessionUnbind)
		{
			if (deviceWrapper.getOwnerBusinessSession() == null)
			{
				setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				setErrorDescription("Device <" + deviceWrapper.getDeviceIdentification() +"> shall not deliver business request before bound with session.");
				return false;
			}
		}
		
		if (operationType == Consts.OperationType.AssessAndContinue
				|| operationType == Consts.OperationType.AssessAndQuit)
		{
			if (!checkAssess())
			{
				return false;
			}
		}
		
		if (operationType == Consts.OperationType.ChatMessage)
		{
			if (!checkChatMessage())
			{
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkChatMessage()
	{
		if (!body.containsKey(JSONKey.OperationInfo))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationInfo + " must be provided for chatMessage.");
			return false;
		}
		
		JSONObject obj = body.getJSONObject(JSONKey.OperationInfo);
		if (!obj.containsKey(JSONKey.ChatMessage))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.ChatMessage + " must be provided for chatMessage.");
			return false;
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
		
		JSONObject label = null;
		for (int i = 0; i < assessLabelList.size(); i++)
		{
			label = assessLabelList.getJSONObject(i); 
			if (!label.containsKey(JSONKey.ImpressLabelName))
			{
				setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
				setErrorDescription(JSONKey.ImpressLabelName + " is missed from " + JSONKey.AssessLabelList + ".");
				return false;
			}
		}
		
		JSONArray impressLabelList = getJSONArray(impressObj, JSONKey.ImpressLabelList);
		if (impressLabelList != null)
		{
			for (int i = 0; i < impressLabelList.size(); i++)
			{
				label = impressLabelList.getJSONObject(i);
				if (!label.containsKey(JSONKey.ImpressLabelName))
				{
					setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
					setErrorDescription(JSONKey.ImpressLabelName + " is missed from " + JSONKey.ImpressLabelList + ".");
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void doRun()
	{
		if (!checkJSONRequest())
		{
			responseError();
			return;
		}
		
		IBusinessSession session = deviceWrapper.getOwnerBusinessSession();
		if (session != null)
		{
			if (!session.checkProgressForRequest(deviceWrapper, operationType))
			{
				Consts.DeviceBusinessProgress progress = session.getProgressOfDevice(deviceWrapper);
				logger.error("Received " + operationType.name() + " from Device <{}> but its business progress is " + progress.name());
				this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				this.setErrorDescription("It's not allowed to send " + operationType.name() + " if business progress is " + progress.name());
				responseError();
				return;
			}
		}
		
		logger.debug("Received <" + operationType.name() + "> from device <{}>", deviceWrapper.getDeviceIdentification());
		switch (operationType)
		{
			case ChooseBusiness:
				chooseBusiness();
				break;
			case UnchooseBusiness:
				unchooseBusiness();
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
			case SessionUnbind:
				sessionUnbind();
				break;
			case MatchStart:
				matchStart();
				break;
			case ChatMessage:
				chatMessage();
				break;
			default:
				logger.error("Invalid operation type found: " + operationType.name());
				break;
		}
	}
	
	private void chooseBusiness()
	{
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType businessType = Consts.BusinessType.parseValue(intType);
		
		Consts.DeviceStatus status = deviceWrapper.getBusinessStatus();
		if (status.getValue() >= Consts.DeviceStatus.BusinessChoosed.getValue())
		{
			Consts.BusinessType curBusinessType = deviceWrapper.getBusinessType();
			if (businessType != curBusinessType)
			{
				this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				this.setErrorDescription("Device <"+ deviceWrapper.getDeviceIdentification() +"> is still in " + curBusinessType.name() + " pool and request of entering "+ businessType.name() +" pool is rejected.");
				responseError();
				return;
			}
		}
		
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		
		String operationInfo = null;
		
		operationInfo = businessPool.checkDeviceEnter(deviceWrapper); 
		if (operationInfo != null)
		{
			setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			setErrorDescription(operationInfo);
			responseError();
			return;
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.ChooseBusiness.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, operationInfo);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestChooseBusiness_1014
    			, deviceWrapper.getDevice().getProfile()
    			, businessType.name() + ", " + deviceWrapper.getDeviceIdentification());
		deviceWrapper.prepareResponse(response);
		deviceWrapper.setBusinessType(businessType);
		deviceWrapper.changeBusinessStatus(Consts.DeviceStatus.BusinessChoosed, StatusChangeReason.AppEnterBusiness);
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestChooseBusiness_1014
    			, deviceWrapper.getDevice().getProfile()
    			, "");
	}
	
	private void unchooseBusiness()
	{
		Consts.DeviceStatus status = deviceWrapper.getBusinessStatus();
		if (status.getValue() < Consts.DeviceStatus.BusinessChoosed.getValue())
		{
			this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			this.setErrorDescription("Device <"+ deviceWrapper.getDeviceIdentification() +"> is not in business device pool.");
			responseError();
			return;
		}
		
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType businessType = Consts.BusinessType.parseValue(intType);
		Consts.BusinessType curBusinessType = deviceWrapper.getBusinessType();
		if (businessType != curBusinessType)
		{
			this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			this.setErrorDescription("Device <"+ deviceWrapper.getDeviceIdentification() +"> is in " + curBusinessType.name() + " pool and request of leaving "+ businessType.name() +" pool is rejected.");
			responseError();
			return;
		}
		
		//OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		//onlinePool.getBusinessPool(businessType).onDeviceLeave(deviceWrapper, Consts.StatusChangeReason.AppLeaveBusiness);
		
		deviceWrapper.changeBusinessStatus(Consts.DeviceStatus.AppDataSynced, Consts.StatusChangeReason.AppLeaveBusiness);
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.UnchooseBusiness.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestUnchooseBusiness_1015
    			, deviceWrapper.getDevice().getProfile()
    			, body.getString(JSONKey.BusinessType));
	}
	
	private void agreeChat()
	{
		logger.debug("Device <{}> agreed chat.", deviceWrapper.getDeviceIdentification());
		
		IBusinessSession session = deviceWrapper.getOwnerBusinessSession(); 

		//session.onAgreeChat(deviceWrapper);
		session.newEvent(BusinessSessionEventType.AgreeChat, deviceWrapper, null);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.AgreeChat.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAgreeChat_1016
    			, deviceWrapper.getDevice().getProfile()
    			, "");
	}
	
	private void rejectChat()
	{
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		//deviceWrapper.getOwnerBusinessSession().onRejectChat(deviceWrapper);
		deviceWrapper.getOwnerBusinessSession().newEvent(BusinessSessionEventType.RejectChat, deviceWrapper, null);
		deviceWrapper.changeBusinessStatus(DeviceStatus.BusinessChoosed, StatusChangeReason.AppRejectChat);
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.RejectChat.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestRejectChat_1017
    			, deviceWrapper.getDevice().getProfile()
    			, "");
	}
	
	private void endChat()
	{
		logger.debug("Device <{}> ended chat.", deviceWrapper.getDeviceIdentification());
		//deviceWrapper.getOwnerBusinessSession().onEndChat(deviceWrapper);
		deviceWrapper.getOwnerBusinessSession().newEvent(BusinessSessionEventType.EndChat, deviceWrapper, null);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.EndChat.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestEndChat_1018
    			, deviceWrapper.getDevice().getProfile()
    			, "");
	}
	
	private void assessAndContinue()
	{
		assess(false);
	}
	
	private void assess(boolean quitAfterAssess)
	{
		logger.debug("Device <{}> provides assess.", deviceWrapper.getDeviceIdentification());
		
		String deviceSn = body.getJSONObject(JSONKey.OperationInfo)
				.getJSONObject(JSONKey.Device)
				.getString(JSONKey.DeviceSn);
		
		if (deviceSn.equals(deviceWrapper.getDeviceIdentification()))
    	{
    		logger.warn("Device <{}> is assessing itself", deviceSn);
    		setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			setErrorDescription("Self-assessing is forbidden.");
			responseError();
    		return;
    	}
		
		//DeviceDAO dao = new DeviceDAO();
		//Device targetDevice = dao.findByDeviceSn(deviceSn).get(0);
		boolean isDeviceInPool = true;
		IDeviceWrapper targetDeviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
		Device targetDevice = null;
		if (targetDeviceWrapper == null)
		{
			isDeviceInPool = false;
			// Load device from DB 
			targetDevice = DBModule.instance.deviceCache.getObject(deviceSn);
			if (targetDevice == null)
			{
				// Check if it's totally invalid deviceSn in DB
				String temp = "Failed to assess Device <" + deviceSn + "> due to it's not in DB";
				logger.error(temp);
				ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
						Consts.MessageId.BusinessSessionResponse);
				response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
				response.addToBody(JSONKey.BusinessType, deviceWrapper.getBusinessType().getValue());
				response.addToBody(JSONKey.OperationInfo, temp);
				response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Fail.getValue());
				deviceWrapper.prepareResponse(response);
				return;
			}
			
			logger.debug("Device <{}> was released before Device <" + deviceWrapper.getDeviceIdentification() + "> trying to assess it, load it from DB again", targetDevice.getDeviceSn());
			DAOWrapper.instance.removeDeviceForAssess(targetDevice);
		}
		else
		{
			targetDevice = targetDeviceWrapper.getDevice();
		}
		
		Impresscard targetCard = targetDevice.getProfile().getImpressCard();
		Impresscard sourceCard = deviceWrapper.getDevice().getProfile().getImpressCard();
		
		JSONObject impressObj = body.getJSONObject(JSONKey.OperationInfo)
				.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard);
		
		String tempLabel;
		JSONArray assessLabels = impressObj.getJSONArray(JSONKey.AssessLabelList);
		JSONObject tempLabelObj = assessLabels.getJSONObject(0); 
		tempLabel = tempLabelObj.getString(JSONKey.ImpressLabelName);
		
		//targetDeviceWrapper.increaseChatCount();
		synchronized (targetCard)
		{
			int count = targetCard.getChatTotalCount();
			logger.debug("Chat total count of device <{}> was increased from " + count + " to " + (count+1), targetDevice.getDeviceSn());
			targetCard.setChatTotalCount(count + 1);
		}
		
		targetCard.updateOrAppendImpressLabel(deviceWrapper, tempLabel, true);
		sourceCard.updateOrAppendImpressLabel(deviceWrapper, tempLabel, false);
		
		JSONArray impressLabels = impressObj.getJSONArray(JSONKey.ImpressLabelList);
		for (int i = 0; i < impressLabels.size(); i++)
		{
			tempLabelObj = impressLabels.getJSONObject(i); 
			tempLabel = tempLabelObj.getString(JSONKey.ImpressLabelName);
			targetCard.updateOrAppendImpressLabel(deviceWrapper, tempLabel, true);
			sourceCard.updateOrAppendImpressLabel(deviceWrapper, tempLabel, false);
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		IBusinessSession session = deviceWrapper.getOwnerBusinessSession();
		if (session != null)
		{
			response.addToBody(JSONKey.BusinessSessionId, session.getSessionId());
		}
		else
		{
			logger.error("Abnormal business status in device <{}>, it's assessing others but business session is null!", deviceWrapper.getDeviceIdentification());
			return;
		}
		response.addToBody(JSONKey.BusinessType, deviceWrapper.getBusinessType().getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		
		if (quitAfterAssess)
		{
			response.addToBody(JSONKey.OperationType, Consts.OperationType.AssessAndQuit.getValue());
			//deviceWrapper.getOwnerBusinessSession().onAssessAndQuit(this.deviceWrapper);
			deviceWrapper.changeBusinessStatus(DeviceStatus.AppDataSynced, StatusChangeReason.AssessAndQuit);
			
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessQuit_1020
	    			, deviceWrapper.getDevice().getProfile()
	    			, targetDevice.getProfile().getProfileId().toString());
		}
		else
		{
			response.addToBody(JSONKey.OperationType, Consts.OperationType.AssessAndContinue.getValue());
			//deviceWrapper.getOwnerBusinessSession().onAssessAndContinue(this.deviceWrapper);
			deviceWrapper.changeBusinessStatus(DeviceStatus.BusinessChoosed, StatusChangeReason.AssessAndContinue);
			
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessContinue_1019
	    			, deviceWrapper.getDevice().getProfile()
	    			, targetDevice.getDeviceSn());
		}
		
		if (!isDeviceInPool)
		{
			// If device has leaved OnlineDevicePool before assess, save to DB after assess
			logger.debug("Add device <{}> to DAOWrapper again after Device <{" + deviceWrapper.getDeviceIdentification() +"}> assessed it.", targetDevice.getDeviceSn());
			DAOWrapper.instance.cache(targetDevice);
		}
		deviceWrapper.prepareResponse(response);
	}
	
		
	private void assessAndQuit()
	{
		assess(true);
	}
	
	private void chatMessage()
	{
		logger.debug("Received chatMessage from Device <{}>.", deviceWrapper.getDeviceIdentification());
		JSONObject obj = body.getJSONObject(JSONKey.OperationInfo);
		String chatMessage = obj.getString(JSONKey.ChatMessage);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		//deviceWrapper.getOwnerBusinessSession().onChatMessage(deviceWrapper, chatMessage);
		deviceWrapper.getOwnerBusinessSession().newEvent(BusinessSessionEventType.ChatMessage, deviceWrapper, chatMessage);
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.ChatMessage.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		
		DbLogger.saveProfileLog(Consts.OperationCode.ChatMessage_1023
    			, deviceWrapper.getDevice().getProfile()
    			, chatMessage);
	}
	
	private void matchStart()
	{
		logger.debug("Device <{}> request to start match.", deviceWrapper.getDeviceIdentification());
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.MatchStart.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		
		synchronized (deviceWrapper)
		{
			deviceWrapper.changeBusinessStatus(DeviceStatus.MatchStarted, StatusChangeReason.AppRequestStartMatch);
		}
		DbLogger.saveProfileLog(Consts.OperationCode.MatchStartRequest_1016
    			, deviceWrapper.getDevice().getProfile()
    			, "");
	}
	
	private void sessionUnbind()
	{
		logger.debug("Device <{}> request to unbind business session.", deviceWrapper.getDeviceIdentification());
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		
		if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
		{
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession()
					.getSessionId());
		}
		else
		{
			response.addToBody(JSONKey.BusinessSessionId, null);
		}
		
		if (deviceWrapper.getBusinessStatus() != DeviceStatus.BusinessChoosed)
		{
			deviceWrapper.changeBusinessStatus(DeviceStatus.BusinessChoosed, StatusChangeReason.AppUnbindSession);
		}
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.SessionUnbind.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		deviceWrapper.prepareResponse(response);
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestUnbindSession_1024
    			, deviceWrapper.getDevice().getProfile()
    			, "");
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionRequest;
	}
}
