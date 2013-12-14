/**
 * IDeviceWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.nio.ByteBuffer;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.AbstractTimeoutNode;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;


/** */
public interface IDeviceWrapper
{
    /** */
    public Consts.DeviceStatus getBusinessStatus();
    
    /** */
    public boolean checkExistenceInDb();
    
    /** */
    public long getLastPingTime();
    
    /** */
    public void bindBusinessSession(IBusinessSession session);
    
    /** */
    public void syncSendMessage(ServerJSONMessage message);
    
    /** */
    public void asyncSendMessage(ServerJSONMessage message);
    
    /** */
    public long getLastActivityTime();
    
    public Device getDevice();
    
    public void setDevice(Device device);
    
    public void setBusinessType(Consts.BusinessType businessType);
    
    public IBusinessSession getOwnerBusinessSession();
    
    public String getDeviceIdentification();
    
    public void updateActivityTime();
    
    public void updatePingTime();
    
    public void changeBusinessStatus(Consts.DeviceStatus targetStatus, Consts.StatusChangeReason reason);
    
	public Consts.BusinessType getBusinessType();
    
    public void bindOnlineDevicePool(OnlineDevicePool pool);
    
    public void unbindOnlineDevicePool();
    
    public OnlineDevicePool getOwnerOnlineDevicePool();
    
    /** */
    public void onConnectionClose();
    
    /** */
    public void onPing(IBaseConnection conection, ByteBuffer payload);
    
    /** */
    public void onJSONCommand(AppJSONMessage command);
    
    /** */
    public void onTimeOut();
    
    public IBaseConnection getConnection();
    
    public JSONObject toJSONObject();
    public JSONObject toJSONObject_DeviceCard();
    public JSONObject toJSONObject_Device();
    public JSONObject toJSONObject_Profile();
    public JSONObject toJSONObject_InterestCard(Profile profile);
    public JSONObject toJSONObject_ImpressCard(Profile profile);
    public void toJSONObject_ImpressLabels(Impresscard impressCard, JSONObject impressCardObj, int labelCount);
    
    
    public void increaseChatCount();
    
    public void increaseChatDuration(int duration);
    
    public void increaseChatLoss();
    
    public void increaseMatchCount(String interestLabel);
    
    public void prepareResponse(ServerJSONMessage response);
    
    public AbstractTimeoutNode getPingNode();
}
