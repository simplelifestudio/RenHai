/**
 * SlaveMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.Device;

/** */
public class LocalMockApp extends AbstractMockApp
{
    /** */
    public MockWebSocketConnection connection;
    
    /** */
    public void ping()
    {
        connection.onPing();
    }
    
    /** */
    public void assess(String impressLabelList)
    {
    }
    
    /** */
    public void sendAlohaRequest()
    {
    }
    
    /** */
    public void sendAppDataSyncRequest()
    {
    }
    
    /** */
    public void sendServerDataSyncRequest()
    {
    }
    
    public void sendRawJSONMessage(String jsonString)
    {
        
    }
    
    public void sendRawJSONMessage(JSONObject jsonObject)
    {
        
    }
    
    /** */
    public void sendNotificationResponse()
    {
    }
    
    /** */
    public void sendBusinessSessionRequest(String operationType, String operationValue)
    {
    }
    
    /** */
    public void close()
    {
    }
    
    /** */
    public void selectBusiness()
    {
    }
    
    /** */
    public void endChat()
    {
    }
    
    /** */
    public void chatConfirm()
    {
    }
    
    /** */
    public void onJSONMessage()
    {
    }
}
