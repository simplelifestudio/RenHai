/**
 * Main.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Operationcode;
import com.simplelife.renhai.server.db.OperationcodeDAO;
import com.simplelife.renhai.server.db.Systemmodule;
import com.simplelife.renhai.server.db.SystemmoduleDAO;
import com.simplelife.renhai.server.db.Systemoperationlog;
import com.simplelife.renhai.server.db.SystemoperationlogDAO;
import com.simplelife.renhai.server.json.AlohaRequest;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/**
 * 
 */
public class MainFunction extends TestCase
{
	@Test
	public void testJSONFactory()
	{
		String strMessage = "{\"header\":{\"messageType\":\"1\",\"messageSn\":\"AFLNWERJL3203598FDLGSLDF\",\"messageId\":\"102\",\"deviceId\":\"1234\",\"deviceSn\":\"ABCD77631GGWQ\",\"timeStamp\":\"2013-08-14 21:18:49\"},\"body\":{\"content\":\"Hello Server!\"}}";
		JSONObject obj = JSONObject.parseObject(strMessage);
		
		WebSocketConnection connection = new WebSocketConnection("1");
		DeviceWrapper device = new DeviceWrapper(connection);
		
		AppJSONMessage request = JSONFactory.createAppJSONMessage(obj);
		assertTrue(request instanceof AlohaRequest);
	}
	
	@Test
	public void testFileLogger()
	{
		Logger log = LoggerFactory.getLogger("RenHai");
		log.debug("This is debug log: {}", DateUtil.getNow());
		log.info("This is info log: {}", DateUtil.getNow());
		log.warn("This is warn log: {}", DateUtil.getNow());
		log.error("This is error log: {}", DateUtil.getNow());
	}

	@Test
	public void testDbOperations()
	{
		Systemoperationlog log = new Systemoperationlog();
		Session session = HibernateSessionFactory.getSession();
		
		SystemmoduleDAO moduleDAO = new SystemmoduleDAO();
		Systemmodule module = moduleDAO.findByModuleNo(3).get(0);
		log.setSystemmodule(module);
		
		OperationcodeDAO dao = new OperationcodeDAO();
		Operationcode operCode = dao.findByOperationCode(1003).get(0);
		log.setOperationcode(operCode);
	
		long time = DateUtil.getNowDate().getTime();
		log.setLogTime(time);
		log.setLogInfo("This is log info");
		
		SystemoperationlogDAO logDAO = new SystemoperationlogDAO();
		session.beginTransaction();
		logDAO.save(log);
		
		System.out.print("============Contains: " + session.contains(log) + "\n");
		System.out.print("============Cache Mode: " + session.getCacheMode() + "\n");
		System.out.print("============IsDirty: " + session.isDirty() + "\n");
		System.out.print("============IsConnected: " + session.isConnected() + "\n");
		System.out.print("============IsOpen: " + session.isOpen() + "\n");
		
		List<Systemoperationlog> logList = logDAO.findByLogTime(time);
		if (logList.size() == 0)
		{
			System.out.print("=========Error: the saved record can't be found by query\n");
		}
		else
		{
			System.out.print("=========Correct record found by query\n");
			System.out.print("log.getLogInfo: " + log.getLogInfo() + "\n");
			System.out.print("log.getLogTime: " + log.getLogTime() + "\n");
			
			System.out.print("log.getOperationCodeId: " + log.getOperationcode().getOperationCodeId() + "\n");
			System.out.print("log.getOperationCode: " + log.getOperationcode().getOperationCode() + "\n");
			
			System.out.print("log.getModuleId: " + log.getSystemmodule().getModuleId() + "\n");
			System.out.print("log.getModuleNo: " + log.getSystemmodule().getModuleNo() + "\n");
		}
		
		System.out.print("=========Entity count of session: " + HibernateSessionFactory.getSession().getStatistics().getEntityCount() + "\n");
		
		session.getTransaction().commit();
		
		//HibernateSessionFactory.getSession().flush();
		//HibernateSessionFactory.getSession().close();
	}
	
	@Test
	public void testSyncDevice()
	{
		String jsonMessage = "{\"header\":{\"messageType\":\"1\",\"messageSn\":\"AFLNWERJ2228FDLGSLDF\",\"messageId\":\"104\",\"deviceId\":\"1234\",\"deviceSn\":\"ABCD77fdsdGGWQ\",\"timeStamp\":\"2013-08-14 21:18:49\"},\"body\":{\"dataQuery\":{\"deviceCard\":{},\"impressCard\":{\"labelListCount\":\"10\"},\"interestCard\":{\"labelListCount\":\"5\"}},\"dataUpdate\":{\"deviceCard\":{\"osVersion\":\"iOS 6.1.3\",\"deviceModel\":\"iPhone5s\",\"appVersion\":\"1.5\",\"isJailed\":\"No\",\"location\":\"22.511962,113.380301\"},\"interestCard\":{\"soccer\":{\"order\":\"0\",\"matchCount\":\"7\"},\"music\":{\"order\":\"1\",\"matchCount\":\"3\"}}}}}";
		JSONObject obj = JSONObject.parseObject(jsonMessage);
		
		AppJSONMessage appRequest = JSONFactory.createAppJSONMessage(obj);
		DeviceWrapper deviceWrapper = new DeviceWrapper(null);
		appRequest.bindDeviceWrapper(deviceWrapper);
		appRequest.run();
	}
}
