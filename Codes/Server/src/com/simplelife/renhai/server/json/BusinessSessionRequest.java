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
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
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
		if (!super.checkJSONRequest())
		{
			return false;
		}
		
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
		
		int strType = body.getIntValue(JSONKey.OperationType);
		operationType = Consts.OperationType.parseValue(strType); 
		if (operationType == Consts.OperationType.Invalid)
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription("Invalid " + JSONKey.OperationType + ": " + strType);
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
		
		JSONObject infoObj = body.getJSONObject(JSONKey.OperationInfo);
		if (!infoObj.containsKey(JSONKey.Device))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.OperationInfo + " can't be found.");
			return false;
		}
		
		JSONObject deviceObj = infoObj.getJSONObject(JSONKey.Device);
		if (!deviceObj.containsKey(JSONKey.Profile))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.Profile + " can't be found.");
			return false;
		}
		
		JSONObject profileObj = deviceObj.getJSONObject(JSONKey.Profile);
		if (!profileObj.containsKey(JSONKey.ImpressCard))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.ImpressCard + " can't be found.");
			return false;
		}
		
		JSONObject impressObj = profileObj.getJSONObject(JSONKey.ImpressCard);
		if (!impressObj.containsKey(JSONKey.AssessLabelList))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.AssessLabelList + " can't be found.");
			return false;
		}
		
		if (!impressObj.containsKey(JSONKey.ImpressLabelList))
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription(JSONKey.ImpressLabelList + " can't be found.");
			return false;
		}
		
		JSONObject assessLabelList = impressObj.getJSONObject(JSONKey.AssessLabelList);
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
		
		switch(operationType)
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
		onlinePool.getBusinessPool(type).deviceEnter(deviceWrapper);
		deviceWrapper.setBusinessType(type);
		deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, "");
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.OperationType, Consts.OperationType.EnterPool.name());
		response.addToBody(JSONKey.OperationInfo, "");
		response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Success.toString());
		response.asyncResponse();
	}

	private void leavePool()
	{
		int intType = body.getIntValue(JSONKey.BusinessType);
		Consts.BusinessType type = Consts.BusinessType.parseValue(intType);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		onlinePool.getBusinessPool(type).deviceEnter(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, "");
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.LeavePool.toString());
		response.addToBody(JSONKey.OperationInfo, "");
		response.asyncResponse();
	}

	private void agreeChat()
	{
		deviceWrapper.getOwnerBusinessSession().onAgreeChat(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, "");
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.AgreeChat.toString());
		response.addToBody(JSONKey.OperationInfo, "");
		response.asyncResponse();
	}

	private void rejectChat()
	{
		deviceWrapper.getOwnerBusinessSession().onRejectChat(deviceWrapper);
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, "");
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.RejectChat.toString());
		response.addToBody(JSONKey.OperationInfo, "");
		response.asyncResponse();
	}

	private void endChat()
	{
		deviceWrapper.getOwnerBusinessSession().onEndChat();
		
		ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.BusinessSessionResponse);
		response.addToBody(JSONKey.BusinessSessionId, "");
		response.addToBody(JSONKey.BusinessType, body.getString(JSONKey.BusinessType));
		response.addToBody(JSONKey.BusinessType, Consts.OperationType.EndChat.toString());
		response.addToBody(JSONKey.OperationInfo, "");
		response.asyncResponse();
	}

	private void assessAndContinue()
	{
		assess(false);
	}

	private void assess(boolean quitAfterAssess)
	{
		String deviceSn = body.getJSONObject(JSONKey.Device).getString(JSONKey.DeviceSn);
		IDeviceWrapper target = OnlineDevicePool.instance.getDevice(deviceSn);
		
		if (target == null)
		{
			String temp = "Failed to assess Device <" + deviceSn + "> due to it's not in online pool"; 
			logger.error(temp);
			ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.BusinessSessionResponse);
			response.addToBody(JSONKey.BusinessSessionId, deviceWrapper.getOwnerBusinessSession().getStatus());
			response.addToBody(JSONKey.OperationInfo, temp);
			response.addToBody(JSONKey.OperationValue, Consts.SuccessOrFail.Fail.toString());
			response.asyncResponse();
			return;
		}
		
		
		Impresscard card = target.getDevice().getProfile().getImpresscard();
    	Set<Impresslabelmap> impressLabelMap = card.getImpresslabelmaps();
    	
		
		JSONObject impressObj = body.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard);
		
		JSONArray assessLabels = impressObj.getJSONArray(JSONKey.AssessLabelList);
		updateOrAppendImpressLabel(impressLabelMap, target, assessLabels.getJSONObject(0));
		
		JSONArray impressLabels = impressObj.getJSONArray(JSONKey.ImpressLabelList);
		for (int i = 0; i < impressLabels.size(); i++)
		{
			updateOrAppendImpressLabel(impressLabelMap, target, impressLabels.getJSONObject(i));
		}
		
		deviceWrapper.onAssessProvided();
		if (quitAfterAssess)
		{
			deviceWrapper.getOwnerBusinessSession().onAssessAndQuit(this.deviceWrapper, target);
		}
		else
		{
			deviceWrapper.getOwnerBusinessSession().onAssessAndContinue(this.deviceWrapper, target);
		}
	}
	
	private void updateOrAppendImpressLabel(Set<Impresslabelmap> impressLabels, 
			IDeviceWrapper targetDevice, 
			JSONObject impressLabel)
    {
		String labelName = impressLabel.getString(JSONKey.ImpressLabelName); 
    	for (Impresslabelmap label : impressLabels)
    	{
    		if (label.getGlobalimpresslabel().getImpressLabel().equals(labelName))
    		{
    			label.setAssessedCount(label.getAssessedCount() + 1);
    			label.setUpdateTime(System.currentTimeMillis());
    			return;
    		}
    	}
    	
    	Globalimpresslabel globalimpresslabel = new Globalimpresslabel();
    	globalimpresslabel.setGlobalAssessCount(1);
    	globalimpresslabel.setImpressLabel(labelName);
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
