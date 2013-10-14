/**
 * BusinessSessionRequest.java
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
import org.hibernate.Transaction;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.AbstractBusinessScheduler;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.GlobalimpresslabelDAO;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.IBusinessSession;
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
		
		if (operationType != Consts.OperationType.EnterPool
				&& operationType != Consts.OperationType.LeavePool)
		{
			if (deviceWrapper.getOwnerBusinessSession() == null)
			{
				setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				setErrorDescription("Device <" + deviceWrapper.getDeviceSn() +"> can't deliver business request before bound with session.");
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
	public void doRun()
	{
		if (!checkJSONRequest())
		{
			responseError(Consts.MessageId.BusinessSessionRequest);
			return;
		}
		
		IBusinessSession session = deviceWrapper.getOwnerBusinessSession();
		if (session != null)
		{
			if (!session.checkProgressForRequest(deviceWrapper, operationType))
			{
				Consts.BusinessProgress progress = session.getProgressOfDevice(deviceWrapper);
				logger.error("Received " + operationType.name() + " from Device <{}> but its business progress is " + progress.name());
				this.setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				this.setErrorDescription("It's not allowed to send " + operationType.name() + " if business progress is " + progress.name());
				responseError(Consts.MessageId.BusinessSessionRequest);
				return;
			}
		}
		
		logger.debug("Received " + operationType.name() + " from device <{}>", deviceWrapper.getDeviceSn());
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
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestEnterPool_1014
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType businessType = Consts.BusinessType.parseValue(intType);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		
		String operationInfo = null;
		
		synchronized (deviceWrapper)
		{
			operationInfo = businessPool.onDeviceEnter(deviceWrapper); 
			if (operationInfo != null)
			{
				setErrorCode(Consts.GlobalErrorCode.InvalidBusinessRequest_1101);
				setErrorDescription(operationInfo);
				responseError(this.getMessageId());
				return;
			}
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
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
		response.asyncResponse();
		
		// Change status of device after sending response of EnterPool
		deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
		deviceWrapper.setBusinessType(businessType);
		
		AbstractBusinessScheduler businessScheduler = businessPool.getBusinessScheduler(); 
		businessScheduler.getLock().lock();
    	businessScheduler.signal();
    	businessScheduler.getLock().unlock();
	}
	
	private void leavePool()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestLeavePool_1015
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());

		deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Idle);
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType type = Consts.BusinessType.parseValue(intType);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		onlinePool.getBusinessPool(type).onDeviceLeave(deviceWrapper, Consts.DeviceLeaveReason.AppLeaveBusiness);
		
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
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.LeavePool.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		response.asyncResponse();
		
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
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
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
		response.asyncResponse();
		
	}
	
	private void rejectChat()
	{
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestRejectChat_1017
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
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
		
		response.addToBody(JSONKey.OperationType, Consts.OperationType.RejectChat.getValue());
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestRejectChat_1017
    			, deviceWrapper.getDevice().getProfile()
    			, deviceWrapper.getDeviceSn());
		response.asyncResponse();
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
		
		if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
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
		response.asyncResponse();
	}
	
	private void assessAndContinue()
	{
		assess(false);
	}
	
	private void assess(boolean quitAfterAssess)
	{
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
		Device targetDevice = dao.findByDeviceSn(deviceSn).get(0);
		Impresscard targetCard = targetDevice.getProfile().getImpresscard();
		Impresscard sourceCard = deviceWrapper.getDevice().getProfile().getImpresscard();
		Set<Impresslabelmap> impressLabelMap = targetCard.getImpresslabelmaps();
		JSONObject impressObj = body.getJSONObject(JSONKey.OperationInfo)
				.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard);
		
		Session session = HibernateSessionFactory.getSession();
		Transaction t = null;
		
		try
		{
			t = session.beginTransaction();
		
			String tempLabel;
			JSONArray assessLabels = impressObj.getJSONArray(JSONKey.AssessLabelList);
			tempLabel = assessLabels.getString(0);
			updateOrAppendImpressLabel(targetCard, tempLabel, true);
			updateOrAppendImpressLabel(sourceCard, tempLabel, false);
			
			JSONArray impressLabels = impressObj.getJSONArray(JSONKey.ImpressLabelList);
			for (int i = 0; i < impressLabels.size(); i++)
			{
				tempLabel = impressLabels.getString(i);
				updateOrAppendImpressLabel(targetCard, tempLabel, true);
				updateOrAppendImpressLabel(sourceCard, tempLabel, false);
			}
			t.commit();
		}
		catch (Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this,
				Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
		response.addToBody(JSONKey.BusinessType, deviceWrapper.getBusinessType().getValue());
		response.addToBody(JSONKey.OperationInfo, null);
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.getValue());
		
		if (quitAfterAssess)
		{
			response.addToBody(JSONKey.OperationType, Consts.OperationType.AssessAndQuit.getValue());
			deviceWrapper.getOwnerBusinessSession().onAssessAndQuit(this.deviceWrapper);
			
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessQuit_1020
	    			, deviceWrapper.getDevice().getProfile()
	    			, deviceWrapper.getDeviceSn());
		}
		else
		{
			response.addToBody(JSONKey.OperationType, Consts.OperationType.AssessAndContinue.getValue());
			deviceWrapper.getOwnerBusinessSession().onAssessAndContinue(this.deviceWrapper);
			
			DbLogger.saveProfileLog(Consts.OperationCode.BusinessRequestAssessContinue_1019
	    			, deviceWrapper.getDevice().getProfile()
	    			, deviceWrapper.getDeviceSn());
		}
		response.asyncResponse();
	}
	
	private void updateOrAppendImpressLabel(Impresscard card, String labelName, boolean assessedFlag)
	{
		Set<Impresslabelmap> impressLabels = card.getImpresslabelmaps();
		synchronized (impressLabels)
		{
			for (Impresslabelmap label : impressLabels)
			{
				String tmpLabelName = label.getGlobalimpresslabel().getImpressLabelName();
				if (tmpLabelName.equals(labelName))
				{
					if (assessedFlag)
					{
						label.setAssessedCount(label.getAssessedCount() + 1);
						label.setUpdateTime(System.currentTimeMillis());
						label.getGlobalimpresslabel().setGlobalAssessCount(label.getGlobalimpresslabel().getGlobalAssessCount() + 1);
					}
					else
					{
						label.setAssessCount(label.getAssessCount() + 1);
					}
					return;
				}
			}
			
			// Check if it's existent global impress label
			GlobalimpresslabelDAO dao = new GlobalimpresslabelDAO();
			List<Globalimpresslabel> globalLabelList = dao.findByImpressLabelName(labelName);
			Globalimpresslabel globalimpresslabel;
			if (globalLabelList.size() == 0)
			{
				globalimpresslabel = new Globalimpresslabel();
				globalimpresslabel.setGlobalAssessCount(1);
				globalimpresslabel.setImpressLabelName(labelName);
				//globalimpresslabel.setImpresslabelmaps(impressLabels);
			}
			else
			{
				globalimpresslabel = globalLabelList.get(0); 
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
			labelMap.setGlobalimpresslabel(globalimpresslabel);
			labelMap.setUpdateTime(System.currentTimeMillis());
			labelMap.setImpresscard(card);
			
			impressLabels.add(labelMap);
		}
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
