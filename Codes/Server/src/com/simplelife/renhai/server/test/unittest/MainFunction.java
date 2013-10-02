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
import org.hibernate.Transaction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Operationcode;
import com.simplelife.renhai.server.db.OperationcodeDAO;
import com.simplelife.renhai.server.db.Statisticsitem;
import com.simplelife.renhai.server.db.StatisticsitemDAO;
import com.simplelife.renhai.server.db.Systemmodule;
import com.simplelife.renhai.server.db.SystemmoduleDAO;
import com.simplelife.renhai.server.db.Systemoperationlog;
import com.simplelife.renhai.server.db.SystemoperationlogDAO;
import com.simplelife.renhai.server.db.Systemstatistics;
import com.simplelife.renhai.server.json.AlohaRequest;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.test.AbstractTestCase;
import com.simplelife.renhai.server.test.LocalMockApp;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/**
 * 
 */
public class MainFunction extends AbstractTestCase
{
	private class SaveTask extends Thread
	{
		private Statisticsitem item;
		private int count = 0;
		private String name;
		
		public SaveTask(Statisticsitem item, String name)
		{
			this.item = item;
			this.name = name;
		}
		
		@Override
		public void run()
		{
			while (count < 1000)
			{
				String now = DateUtil.getNow();
				item.setDescription(name + ", " + now);
				try
				{
					Session session = HibernateSessionFactory.getSession();
					
					//Transaction ts = session.beginTransaction();
					//ts.begin();
		    		session.saveOrUpdate(session.merge(item));
		    		//session.saveOrUpdate(item);
		    		session.flush();
		    		//session.clear();
		    		
		    		String temp = session.toString();
		    		System.out.print(name + " update value to " + now + " in session: " + temp + "\n");
		    		session.beginTransaction().commit();
		    		Thread.sleep(1);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				count++;
			}
		}
	}
	
	@Test
	public void testTwoSession()
	{
		StatisticsitemDAO dao = new StatisticsitemDAO();
		Statisticsitem item = dao.findById(41);
		item.setDescription("demo");
		
		SaveTask task1 = new SaveTask(item, "1");
		SaveTask task2 = new SaveTask(item, "2");
		task1.start();
		task2.start();
		
		try
		{
			Thread.sleep(8000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLeaveRandomPool()
	{
		LocalMockApp app = createNewMockApp(this.demoDeviceSn);
		
		String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:29.845\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"58G334J6A89MBA7L\",\"deviceId\":0},\"body\":{\"dataUpdate\":{\"device\":{\"deviceCard\":{\"isJailed\":1,\"appVersion\":\"0.1\",\"deviceModel\":\"iPhone 5\",\"osVersion\":\"6.1.2\"},\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\"}},\"dataQuery\":{\"device\":null}}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:38.346\",\"messageType\":1,\"messageId\":103,\"messageSn\":\"QR543EPJ1OJ82FEJ\",\"deviceId\":0},\"body\":{\"operationValue\":null,\"businessSessionId\":null,\"operationInfo\":null,\"operationType\":1,\"businessType\":1}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:43.060\",\"messageType\":1,\"messageId\":102,\"messageSn\":\"7J0I40HDF1AQ0VHM\",\"deviceId\":0},\"body\":{\"deviceCount\":{\"chat\":null,\"interest\":null,\"randomChat\":null,\"online\":null,\"interestChat\":null,\"random\":null}}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:50.593\",\"messageType\":1,\"messageId\":103,\"messageSn\":\"4Q59EA4B0J7TQR82\",\"deviceId\":0},\"body\":{\"operationValue\":null,\"businessSessionId\":null,\"operationInfo\":null,\"operationType\":2,\"businessType\":1}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:56.894\",\"messageType\":1,\"messageId\":102,\"messageSn\":\"X7H2Z3ZPR0Q6WH8L\",\"deviceId\":0},\"body\":{\"deviceCount\":{\"chat\":null,\"interest\":null,\"randomChat\":null,\"online\":null,\"interestChat\":null,\"random\":null}}}}";
		sendRawCommand(app, jsonString);
	}
	
	private void sendRawCommand(LocalMockApp app, String jsonString)
	{
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope); 
		
		app.sendRawJSONMessage(obj, true);
	}
	
	@Test
	public void testRawCommand()
	{
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"194AF3A8-FFB0-4997-B9DE-CD4CFB68252B\",\"timeStamp\":\"2013-09-30 12:54:30.809\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"B569O1MTRE5A0UA6\",\"deviceId\":0},\"body\":{\"dataUpdate\":{\"device\":{\"deviceCard\":{\"isJailed\":0,\"appVersion\":\"0.1\",\"deviceModel\":\"Simulator\",\"osVersion\":\"6.1\"},\"deviceSn\":\"794AF3A8-FFB0-4997-B9DE-CD4CFB68252B\"}},\"dataQuery\":{\"device\":null}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"794AF3A8-FFB0-4997-B9DE-CD4CFB68252A\",\"timeStamp\":\"2013-09-30 14:26:26.344\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"0871CLZJY289O310\",\"deviceId\":0},\"body\":{\"dataUpdate\":{\"device\":{\"deviceCard\":{\"isJailed\":0,\"appVersion\":\"0.1\",\"deviceModel\":\"Simulator\",\"osVersion\":\"6.1\"}}},\"dataQuery\":{\"device\":{\"deviceId\":null,\"deviceSn\":null,\"deviceCard\":null,\"profile\":null}}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"194AF3A8-FFB0-4997-B9DE-CD4CFB68252B\",\"timeStamp\":\"2013-09-30 15:28:31.408\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"5I4Z5A61I1250505\",\"deviceId\":0},\"body\":{\"dataQuery\":{\"device\":{\"profile\":{\"impressCard\":null}}}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"794AF3A8-FFB0-4997-B9DE-CD4CFB68252A\",\"timeStamp\":\"2013-10-01 15:14:30.322\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"E936R0GBTJAAEB0Q\",\"deviceId\":0},\"body\":{\"dataUpdate\":{\"device\":{\"deviceCard\":{\"isJailed\":0,\"appVersion\":\"0.1\",\"deviceModel\":\"Simulator\",\"osVersion\":\"6.1\"},\"deviceSn\":\"794AF3A8-FFB0-4997-B9DE-CD4CFB68252A\"}},\"dataQuery\":{\"device\":null}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\",\"timeStamp\":\"2013-10-01 16:02:44.578\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"816SY86DJ6C88RC7\",\"deviceId\":0},\"body\":{\"dataUpdate\":{\"device\":{\"deviceCard\":{\"isJailed\":0,\"appVersion\":\"0.1\",\"deviceModel\":\"Simulator\",\"osVersion\":\"6.1\"},\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\"}},\"dataQuery\":{\"device\":null}}}}";
		String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 12:54:46.097\",\"messageType\":1,\"messageId\":103,\"messageSn\":\"SAUB9WO215092BO9\",\"deviceId\":0},\"body\":{\"operationValue\":null,\"businessSessionId\":null,\"operationInfo\":null,\"operationType\":4,\"businessType\":1}}}";
		
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope); 
		
		LocalMockApp app = createNewMockApp(this.demoDeviceSn);
		app.syncDevice();
		app.sendRawJSONMessage(obj, true);
		//app.sendServerDataSyncRequest();
	}
	
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
	
		long time = System.currentTimeMillis();
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
