/**
 * InitiativeMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;


/** */
public class RemoteMockApp extends AbstractMockApp
{
    /** */
    public WebSocketClient connection;
    
    /** */
    @Override
    public void ping()
    {
    }
    
    /** */
    @Override
    public void assess(String impressLabelList)
    {
    }
    
    @Override
    public void assessAndQuit(String impressLabelList)
    {
    	
    }
    
    /** */
    @Override
    public void sendAlohaRequest()
    {
    }
    
    /** */
    @Override
    public void sendAppDataSyncRequest(HashMap<String, Object> queryMap, HashMap<String, Object> updateMap)
    {
    }
    
    /** */
    @Override
    public void sendServerDataSyncRequest()
    {
    }
    
    @Override
    public void sendRawJSONMessage(String jsonString)
    {
        
    }
    
    @Override
    public void sendRawJSONMessage(JSONObject jsonObject)
    {
    }
    
    /** */
    @Override
    public void sendNotificationResponse()
    {
    }
    
    /** */
    @Override
    public void sendBusinessSessionRequest(String operationType, String operationValue)
    {
    }
    
    /** */
    @Override
    public void close()
    {
    }
    
    /** */
    @Override
    public void endChat()
    {
    }
    
	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#enterPool(java.lang.String)
	 */
	@Override
	public void enterPool(String businessType)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#chatConfirm(boolean)
	 */
	@Override
	public void chatConfirm(boolean agree)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#updateInterestCard(java.lang.String)
	 */
	@Override
	public void updateInterestCard(HashMap<String, Object> interestLabels)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#onJSONCommand(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public void onJSONCommand(JSONObject obj)
	{
		// TODO Auto-generated method stub
		
	}
}
