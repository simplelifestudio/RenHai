/**
 * IMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.util;

import com.alibaba.fastjson.JSONObject;

/** */
public interface IMockApp
{
    /** */
    public void ping();
    
    /** */
    public void assess(String impressLabelList)
    ;
    public void assessAndQuit(String impressLabelList);
    
    /** */
    public void sendAlohaRequest();
    
    /** */
    public void sendAppDataSyncRequest();
    
    /** */
    public void sendServerDataSyncRequest();
    
    /** */
    public void sendRawJSONMessage(String jsonString);
    
    public void sendRawJSONMessage(JSONObject jsonObject);
    
    /** */
    public void sendNotificationResponse();
    
    /** */
    public void sendBusinessSessionRequest(String operationType, String operationValue);
    
    /** */
    public void close();
    
    /** */
    public void selectBusiness();
    
    /** */
    public void endChat();
    
    /** */
    public void chatConfirm();
    
    /** */
    public void onJSONMessage();
}
