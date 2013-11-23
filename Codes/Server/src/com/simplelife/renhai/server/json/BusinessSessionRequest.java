/**
 * BusinessSessionRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;


import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.Consts;
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
		
		if (operationType != Consts.OperationType.EnterPool
				&& operationType != Consts.OperationType.LeavePool
				&& operationType != Consts.OperationType.MatchStart
				&& operationType != Consts.OperationType.SessionUnbind)
		{
			if (deviceWrapper.getOwnerBusinessSession() == null)
			{
				setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				setErrorDescription("Device <" + deviceWrapper.getDeviceSn() +"> shall not deliver business request before bound with session.");
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

		/*
		String deviceSn = deviceObj.getString(JSONKey.DeviceSn);
		if (null == OnlineDevicePool.instance.getDevice(deviceSn))
		{
			this.setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			this.setErrorDescription("Device <" + deviceSn+ "> is not active device in OnlineDevicePool.");
			return false;
		}
		*/
		
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
		
		logger.debug("Received <" + operationType.name() + "> from device <{}>", deviceWrapper.getDeviceSn());
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
			case SessionUnbind:
				sessionUnbind();
				break;
			case MatchStart:
				matchStart();
				break;
			default:
				logger.error("Invalid operation type found: " + operationType.toString());
				break;
		}
	}
	
	private void enterPool()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestEnterPool_1014
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType businessType = Consts.BusinessType.parseValue(intType);
		
		Consts.DeviceStatus status = deviceWrapper.getBusinessStatus();
		if (status.getValue() >= Consts.DeviceStatus.BusinessChoosed.getValue())
		{
			Consts.BusinessType curBusinessType = deviceWrapper.getBusinessType();
			if (businessType != curBusinessType)
			{
				this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				this.setErrorDescription("Device <"+ deviceWrapper.getDeviceSn() +"> is still in " + curBusinessType.name() + " pool and request of entering "+ businessType.name() +" pool is rejected.");
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
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.EnterPool.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, operationInfo);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestEnterPool_1014
    			, deviceWrapper.getDevice().getProfile()
    			, body.getString(JSONKey.BusinessType) + ", " + deviceWrapper.getDeviceSn());
		OutputMessageCenter.instance.addMessage(response);
		
		deviceWrapper.setBusinessType(businessType);
		deviceWrapper.changeBusinessStatus(Consts.DeviceStatus.BusinessChoosed, StatusChangeReason.AppEnterBusiness);
	}
	
	private void leavePool()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestLeavePool_1015
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());

		Consts.DeviceStatus status = deviceWrapper.getBusinessStatus();
		if (status.getValue() < Consts.DeviceStatus.BusinessChoosed.getValue())
		{
			this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			this.setErrorDescription("Device <"+ deviceWrapper.getDeviceSn() +"> is not in business device pool.");
			responseError();
			return;
		}
		
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType businessType = Consts.BusinessType.parseValue(intType);
		Consts.BusinessType curBusinessType = deviceWrapper.getBusinessType();
		if (businessType != curBusinessType)
		{
			this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
			this.setErrorDescription("Device <"+ deviceWrapper.getDeviceSn() +"> is in " + curBusinessType.name() + " pool and request of leaving "+ businessType.name() +" pool is rejected.");
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
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.LeavePool.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		OutputMessageCenter.instance.addMessage(response);
		
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestLeavePool_1015
    			, deviceWrapper.getDevice().getProfile()
    			, body.getString(JSONKey.BusinessType) + ", " + deviceWrapper.getDeviceSn());
	}
	
	private void agreeChat()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAgreeChat_1016
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		
		logger.debug("Device <{}> agreed chat.", deviceWrapper.getDeviceSn());
		
		IBusinessSession session = deviceWrapper.getOwnerBusinessSession(); 

		session.onAgreeChat(deviceWrapper);
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

		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAgreeChat_1016
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		OutputMessageCenter.instance.addMessage(response);
	}
	
	private void rejectChat()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestRejectChat_1017
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		logger.debug("Device <{}> rejected chat.", deviceWrapper.getDeviceSn());
		
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
		
		deviceWrapper.getOwnerBusinessSession().onRejectChat(deviceWrapper);
		deviceWrapper.changeBusinessStatus(DeviceStatus.BusinessChoosed, StatusChangeReason.AppRejectChat);
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.RejectChat.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestRejectChat_1017
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		OutputMessageCenter.instance.addMessage(response);
	}
	
	private void endChat()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestEndChat_1018
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		logger.debug("Device <{}> ended chat.", deviceWrapper.getDeviceSn());
		deviceWrapper.getOwnerBusinessSession().onEndChat(deviceWrapper);
		
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
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestEndChat_1018
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		OutputMessageCenter.instance.addMessage(response);
	}
	
	private void assessAndContinue()
	{
		assess(false);
	}
	
	private void assess(boolean quitAfterAssess)
	{
		logger.debug("Device <{}> provides assess.", deviceWrapper.getDeviceSn());
		if (quitAfterAssess)
		{
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessQuit_1020
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		}
		else
		{
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessContinue_1019
	    			, deviceWrapper.getDevice().getProfile()
	    			, deviceWrapper.getDeviceSn());
		}
		
		String deviceSn = body.getJSONObject(JSONKey.OperationInfo)
				.getJSONObject(JSONKey.Device)
				.getString(JSONKey.DeviceSn);
		
		if (deviceSn.equals(deviceWrapper.getDeviceSn()))
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
				OutputMessageCenter.instance.addMessage(response);
				return;
			}
			
			logger.debug("Device <{}> was released before Device <" + deviceWrapper.getDeviceSn() + "> trying to assess it, load it from DB again", targetDevice.getDeviceSn());
			DAOWrapper.removeDeviceForAssess(targetDevice);
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
		
		updateOrAppendImpressLabel(targetCard, tempLabel, true);
		updateOrAppendImpressLabel(sourceCard, tempLabel, false);
		
		JSONArray impressLabels = impressObj.getJSONArray(JSONKey.ImpressLabelList);
		for (int i = 0; i < impressLabels.size(); i++)
		{
			tempLabelObj = impressLabels.getJSONObject(i); 
			tempLabel = tempLabelObj.getString(JSONKey.ImpressLabelName);
			updateOrAppendImpressLabel(targetCard, tempLabel, true);
			updateOrAppendImpressLabel(sourceCard, tempLabel, false);
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
			logger.error("Abnormal business status in device <{}>, it's assessing others but business session is null!", deviceWrapper.getDeviceSn());
			return;
		}
		response.addToBody(JSONKey.BusinessType, deviceWrapper.getBusinessType().getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		
		if (quitAfterAssess)
		{
			response.addToBody(JSONKey.OperationType, Consts.OperationType.AssessAndQuit.getValue());
			deviceWrapper.getOwnerBusinessSession().onAssessAndQuit(this.deviceWrapper);
			deviceWrapper.changeBusinessStatus(DeviceStatus.AppDataSynced, StatusChangeReason.AssessAndQuit);
			
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessQuit_1020
	    			, deviceWrapper.getDevice().getProfile()
	    			, deviceWrapper.getDeviceSn());
		}
		else
		{
			response.addToBody(JSONKey.OperationType, Consts.OperationType.AssessAndContinue.getValue());
			deviceWrapper.getOwnerBusinessSession().onAssessAndContinue(this.deviceWrapper);
			deviceWrapper.changeBusinessStatus(DeviceStatus.BusinessChoosed, StatusChangeReason.AssessAndContinue);
			
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessContinue_1019
	    			, deviceWrapper.getDevice().getProfile()
	    			, deviceWrapper.getDeviceSn());
		}
		
		if (!isDeviceInPool)
		{
			// If device has leaved OnlineDevicePool before assess, save to DB after assess
			logger.debug("Add device <{}> to DAOWrapper again after Device <{" + deviceWrapper.getDeviceSn() +"}> assessed it.", targetDevice.getDeviceSn());
			DAOWrapper.cache(targetDevice);
		}
		OutputMessageCenter.instance.addMessage(response);
	}
	
	private void updateOrAppendImpressLabel(Impresscard card, String labelName, boolean assessedFlag)
	{
		Collection<Impresslabelmap> impressLabels = card.getImpressLabelMapSet();

		for (Impresslabelmap label : impressLabels)
		{
			String tmpLabelName = label.getGlobalLabel().getImpressLabelName();
			if (tmpLabelName.equals(labelName))
			{
				synchronized(label)
				{
					if (assessedFlag)
					{
						int count = label.getAssessedCount();
						logger.debug("Impress label<"+ labelName +"> of device <{}> was increased from " 
								+ count + " to " + (count + 1), deviceWrapper.getDeviceSn());
						label.setAssessedCount(count + 1);
						label.setUpdateTime(System.currentTimeMillis());
						//label.getGlobalimpresslabel().setGlobalAssessCount(label.getGlobalimpresslabel().getGlobalAssessCount() + 1);
						DbLogger.increaseImpressAssessCount(tmpLabelName);
					}
					else
					{
						int count = label.getAssessCount();
						logger.debug("Assessing count of <" + labelName + "> by device <{}> was increased from " 
								+ count + " to " + (count + 1), deviceWrapper.getDeviceSn());
						label.setAssessCount(label.getAssessCount() + 1);
					}
				}
				return;
			}
		}
			
		// Check if it's existent global impress label
		Globalimpresslabel globalimpresslabel = DBModule.instance.impressLabelCache.getObject(labelName);
		if (globalimpresslabel == null)
		{
			logger.debug("New impress label {} from device <" + deviceWrapper.getDeviceSn() + ">", labelName);
			globalimpresslabel = new Globalimpresslabel();
			globalimpresslabel.setGlobalAssessCount(1);
			globalimpresslabel.setImpressLabelName(labelName);
			
			// If there is global label object with same label name in cache
			// replace global label by existent global label object  
			boolean isNewObject = DBModule.instance.impressLabelCache.putObject(labelName, globalimpresslabel);
			if (isNewObject)
			{
				logger.debug("============new object, save in DAOWrapper");
				DAOWrapper.cache(globalimpresslabel);
			}
			else
			{
				logger.debug("============old object, replace globalimpresslabel with old object");
				globalimpresslabel = DBModule.instance.impressLabelCache.getObject(labelName);
			}
			//globalimpresslabel.setImpresslabelmaps(impressLabels);
		}
		
		Impresslabelmap labelMap = new Impresslabelmap();
		
		if (assessedFlag)
		{
			labelMap.setAssessCount(0);
			labelMap.setAssessedCount(1);
		}
		else
		{
			labelMap.setAssessCount(1);
			labelMap.setAssessedCount(0);
		}
		
		labelMap.setGlobalImpressLabelId(globalimpresslabel.getGlobalImpressLabelId());
		labelMap.setGlobalLabel(globalimpresslabel);
		labelMap.setUpdateTime(System.currentTimeMillis());
		labelMap.setImpressCardId(card.getImpressCardId());
		
		impressLabels.add(labelMap);
	}
	
	private void assessAndQuit()
	{
		assess(true);
	}
	
	private void matchStart()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.MatchStartRequest_1016
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		logger.debug("Device <{}> request to start match.", deviceWrapper.getDeviceSn());
		
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
		OutputMessageCenter.instance.addMessage(response);
		
		synchronized (deviceWrapper)
		{
			deviceWrapper.changeBusinessStatus(DeviceStatus.MatchStarted, StatusChangeReason.AppRequestStartMatch);
		}
	}
	
	private void sessionUnbind()
	{
		logger.debug("Device <{}> request to unbind business session.", deviceWrapper.getDeviceSn());
		
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
		OutputMessageCenter.instance.addMessage(response);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionRequest;
	}
}
