/**
 * Main.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.asynchttpclient.websocket.WebSocket;
import org.asynchttpclient.websocket.WebSocketTextListener;
import org.asynchttpclient.websocket.WebSocketUpgradeHandler;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.business.pool.InputMessageCenter;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
import com.simplelife.renhai.server.business.pool.RandomBusinessDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.db.Operationcode;
import com.simplelife.renhai.server.db.OperationcodeDAO;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Statisticsitem;
import com.simplelife.renhai.server.db.StatisticsitemDAO;
import com.simplelife.renhai.server.db.Systemmodule;
import com.simplelife.renhai.server.db.SystemmoduleDAO;
import com.simplelife.renhai.server.db.Systemoperationlog;
import com.simplelife.renhai.server.db.SystemoperationlogDAO;
import com.simplelife.renhai.server.json.AlohaRequest;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.test.AbstractTestCase;
import com.simplelife.renhai.server.test.MockApp;
import com.simplelife.renhai.server.test.MockAppConsts;
import com.simplelife.renhai.server.test.RenHaiWebSocketClient;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.websocket.WebSocketConnection;

//import com.ning.http.client.*;
import java.util.concurrent.Future;


/**
 * 
 */
public class MainFunction extends AbstractTestCase
{
	private class SaveDeviceThread extends Thread
	{
		private Device device;
		
		public SaveDeviceThread(Device device)
		{
			this.device = device;
		}
		
		@Override
		public void run()
		{
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			session.save(device);
			t.commit();
			HibernateSessionFactory.closeSession();
		}
	}
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
				Session session = HibernateSessionFactory.getSession();
				Transaction t = null;
				try
				{
					t =  session.beginTransaction();
					session.saveOrUpdate(session.merge(item));
		    		session.flush();
		    		
		    		String temp = session.toString();
		    		System.out.print(name + " update value to " + now + " in session: " + temp + "\n");
		    		t.commit();
		    		Thread.sleep(1);
				}
				catch(Exception e)
				{
					FileLogger.printStackTrace(e);
				}
				finally
				{
					HibernateSessionFactory.closeSession();
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
			FileLogger.printStackTrace(e);
		}
	}
	
	@Test
	public void testLeaveRandomPool()
	{
		MockApp app = createNewMockApp(this.demoDeviceSn);
		
		String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:29.845\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"58G334J6A89MBA7L\",\"deviceId\":0},\"body\":{\"dataUpdate\":{\"device\":{\"deviceCard\":{\"isJailed\":1,\"appVersion\":\"0.1\",\"deviceModel\":\"iPhone 5\",\"osVersion\":\"6.1.2\"},\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\"}},\"dataQuery\":{\"device\":null}}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:38.346\",\"messageType\":1,\"messageId\":103,\"messageSn\":\"QR543EPJ1OJ82FEJ\",\"deviceId\":0},\"body\":{\"operationValue\":null,\"businessSessionId\":null,\"operationInfo\":null,\"operationType\":1,\"businessType\":1}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:43.060\",\"messageType\":1,\"messageId\":102,\"messageSn\":\"7J0I40HDF1AQ0VHM\",\"deviceId\":0},\"body\":{\"deviceCount\":{\"chat\":null,\"interest\":null,\"randomChat\":null,\"online\":null,\"interestChat\":null,\"random\":null}}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:50.593\",\"messageType\":1,\"messageId\":103,\"messageSn\":\"4Q59EA4B0J7TQR82\",\"deviceId\":0},\"body\":{\"operationValue\":null,\"businessSessionId\":null,\"operationInfo\":null,\"operationType\":2,\"businessType\":1}}}";
		sendRawCommand(app, jsonString);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
		
		jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-02 16:45:56.894\",\"messageType\":1,\"messageId\":102,\"messageSn\":\"X7H2Z3ZPR0Q6WH8L\",\"deviceId\":0},\"body\":{\"deviceCount\":{\"chat\":null,\"interest\":null,\"randomChat\":null,\"online\":null,\"interestChat\":null,\"random\":null}}}}";
		sendRawCommand(app, jsonString);
	}
	
	private void sendRawCommand(MockApp app, String jsonString)
	{
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope); 
		
		app.sendRawJSONMessage(obj, true);
	}
	
	@Test
	public void testRawCommand_AppDataSync()
	{
		String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":null},\"dataUpdate\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceModel\":\"iPhone6\",\"deviceSn\":\"线程组 1-2\",\"isJailed\":0,\"location\":\"22.511962,113.380301\",\"osVersion\":\"iOS 6.1.2\"},\"deviceSn\":\"线程组 1-2\",\"profile\":{\"interestCard\":{\"interestLabelList\":[{\"interestLabelName\":\"音乐\",\"labelOrder\":0},{\"interestLabelName\":\"看电影\",\"labelOrder\":0},{\"interestLabelName\":\"privateInterestOf 线程组 1-2\",\"labelOrder\":0}]}}}}},\"header\":{\"deviceId\":0,\"deviceSn\":\"线程组 1-2\",\"messageId\":101,\"messageSn\":\"R3RLJ67G4WBNM34V\",\"messageType\":1,\"timeStamp\":\"2013-10-19 13:50:24.250\"}}}";
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope);

		MockApp app = createNewMockApp("线程组 1-2");
		app.sendRawJSONMessage(obj, true);
	}
	
	@Test
	public void testReplaceWebsocket()
	{
		MockApp app = new MockApp(demoDeviceSn, "Slave");
		
		app.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		
		app.connect(false);
		app.syncDevice();
		app.sendServerDataSyncRequest();
		
		app.connect(false);
		app.syncDevice();
		app.sendServerDataSyncRequest();
		
		app.connect(false);
		app.syncDevice();
		app.sendServerDataSyncRequest();
	}
	
	@Test
	public void testRawCommand()
	{
		//String jsonString ="{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"demoDeviceSn\",\"timeStamp\":\"2013-10-12 16:45:45.003\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"3ZBJO4HCZR43NZX3\",\"deviceId\":3},\"body\":{\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"labelOrder\":0,\"globalMatchCount\":2,\"validFlag\":1,\"interestLabelName\":\"Topic6\",\"matchCount\":0,\"globalInterestLabelId\":2},{\"labelOrder\":0,\"globalMatchCount\":1,\"validFlag\":1,\"interestLabelName\":\"Topic8\",\"matchCount\":0,\"globalInterestLabelId\":1},{\"labelOrder\":0,\"globalMatchCount\":3,\"validFlag\":1,\"interestLabelName\":\"Topic7\",\"matchCount\":0,\"globalInterestLabelId\":3},{\"labelOrder\":0,\"globalMatchCount\":2,\"validFlag\":1,\"interestLabelName\":\"Topic6\",\"matchCount\":0,\"globalInterestLabelId\":2},{\"labelOrder\":0,\"globalMatchCount\":3,\"validFlag\":1,\"interestLabelName\":\"Topic7\",\"matchCount\":0,\"globalInterestLabelId\":3},{\"labelOrder\":0,\"globalMatchCount\":1,\"validFlag\":1,\"interestLabelName\":\"Topic8\",\"matchCount\":0,\"globalInterestLabelId\":1}]}}}},\"dataQuery\":{\"device\":{\"profile\":{\"interestCard\":null}}}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalInterestLabelId\":10,\"globalMatchCount\":10,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":12,\"globalMatchCount\":12,\"interestLabelName\":\"Allen\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":13,\"globalMatchCount\":13,\"interestLabelName\":\"Chris\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":11,\"globalMatchCount\":11,\"interestLabelName\":\"Tomas\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"48UA5C56982G3MB8\",\"messageType\":1,\"timeStamp\":\"2013-10-12 22:14:00.749\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalInterestLabelId\":10,\"globalMatchCount\":10,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":12,\"globalMatchCount\":12,\"interestLabelName\":\"Allen\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"48UA5C56982G3MB8\",\"messageType\":1,\"timeStamp\":\"2013-10-12 22:14:00.749\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-12 20:17:41.630\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"O54JKG4HXZ5EU846\",\"deviceId\":3},\"body\":{\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"labelOrder\":1,\"globalMatchCount\":1,\"validFlag\":1,\"interestLabelName\":\"Topic8\",\"matchCount\":0,\"globalInterestLabelId\":1},{\"labelOrder\":4,\"globalMatchCount\":2,\"validFlag\":1,\"interestLabelName\":\"Topic6\",\"matchCount\":0,\"globalInterestLabelId\":2}]}}}},\"dataQuery\":{\"device\":{\"profile\":{\"interestCard\":null}}}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{},\"dataUpdate\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceModel\":\"Simulator\",\"isJailed\":0,\"osVersion\":\"6.1\"},\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\"}}},\"header\":{\"deviceId\":0,\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\",\"messageId\":101,\"messageSn\":\"T572P03A06725I13\",\"messageType\":1,\"timeStamp\":\"2013-10-13 20:51:59.747\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{},\"dataUpdate\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceModel\":\"iPhone 5\",\"isJailed\":1,\"osVersion\":\"6.1.2\"},\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\"}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"97QCOOICO8C4T5XN\",\"messageType\":1,\"timeStamp\":\"2013-10-14 06:17:14.663\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestLabelList\":[{\"globalMatchCount\":0,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":0}]}}}}},\"header\":{\"deviceId\":0,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"UPM59M9KUR485GQ3\",\"messageType\":1,\"timeStamp\":\"2013-10-14 11:18:01.285\"}}}";
		
		// topic 7
		String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalMatchCount\":0,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":0},{\"globalInterestLabelId\":3,\"globalMatchCount\":3,\"interestLabelName\":\"Topic7\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":1,\"globalMatchCount\":1,\"interestLabelName\":\"Topic8\",\"labelOrder\":2,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"52D3Q6SWC9072GQ2\",\"messageType\":1,\"timeStamp\":\"2013-10-14 13:37:50.057\"}}}";
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope); 
		
		MockApp app = createNewMockApp("45CF7936-3FA1-49B2-937D-D462AB5F378A");
		//app.syncDevice();
		app.sendRawJSONMessage(obj, true);
		
		// topic 6
		jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalMatchCount\":0,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":0},{\"globalInterestLabelId\":3,\"globalMatchCount\":3,\"interestLabelName\":\"Topic6\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":1,\"globalMatchCount\":1,\"interestLabelName\":\"Topic8\",\"labelOrder\":2,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"52D3Q6SWC9072GQ2\",\"messageType\":1,\"timeStamp\":\"2013-10-14 13:37:50.057\"}}}";
		wholeObj = JSONObject.parseObject(jsonString);
		obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope);
		app.sendRawJSONMessage(obj, true);
	}
	
	@Test
	public void testJSONFactory()
	{
		String strMessage = "{\"header\":{\"messageType\":\"1\",\"messageSn\":\"AFLNWERJL3203598FDLGSLDF\",\"messageId\":\"102\",\"deviceId\":\"1234\",\"deviceSn\":\"ABCD77631GGWQ\",\"timeStamp\":\"2013-08-14 21:18:49\"},\"body\":{\"content\":\"Hello Server!\"}}";
		JSONObject obj = JSONObject.parseObject(strMessage);
		
		WebSocketConnection connection = new WebSocketConnection("1");
		new DeviceWrapper(connection);
		
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
	public void testAssess()
	{
		String deviceSn = "demoDeviceSn";
		DeviceDAO dao = new DeviceDAO();
		Device target = dao.findByDeviceSn(deviceSn).get(0);
		
		Impresscard card = target.getProfile().getImpresscard();
		Set<Impresslabelmap> impressLabelMap = card.getImpresslabelmaps();
		
		JSONArray assessLabels = new JSONArray();
		assessLabels.add("^#Happy#^");
		updateOrAppendImpressLabel(card, impressLabelMap, assessLabels.getString(0));
		
		JSONArray impressLabels = new JSONArray();
		impressLabels.add("评价_new");
		impressLabels.add("帅哥");
		for (int i = 0; i < impressLabels.size(); i++)
		{
			updateOrAppendImpressLabel(card, impressLabelMap, impressLabels.getString(i));
		}
		
		// Save to DB
		//DBModule.instance.cache(target);
		DAOWrapper.asyncSave(target);
		DAOWrapper.flushToDB();
		
		System.out.println("ended");
		//session.flush();
	}
	
	private void updateOrAppendImpressLabel(Impresscard card, Set<Impresslabelmap> impressLabels, String labelName)
	{
		synchronized (impressLabels)
		{
			//String labelName = impressLabel.getString(JSONKey.ImpressLabelName);
			for (Impresslabelmap label : impressLabels)
			{
				String tmpLabelName = label.getGlobalimpresslabel().getImpressLabelName();
				if (tmpLabelName.equals(labelName))
				{
					label.setAssessedCount(label.getAssessedCount() + 1);
					label.setUpdateTime(System.currentTimeMillis());
					return;
				}
			}
			
			Globalimpresslabel globalimpresslabel = new Globalimpresslabel();
			globalimpresslabel.setGlobalAssessCount(1);
			globalimpresslabel.setImpressLabelName(labelName);
			//globalimpresslabel.setImpresslabelmaps(impressLabels);
			
			Impresslabelmap labelMap = new Impresslabelmap();
			labelMap.setAssessCount(0);
			labelMap.setAssessedCount(1);
			labelMap.setGlobalimpresslabel(globalimpresslabel);
			labelMap.setUpdateTime(System.currentTimeMillis());
			labelMap.setImpresscard(card);
			
			impressLabels.add(labelMap);
		}
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
		session.close();
	}
	
	@Test
	public void testSyncDevice()
	{
		MockApp app = new MockApp(demoDeviceSn);
		app.syncDevice();
	}
	
	@Test
	public void testSaveStats()
	{
		OnlineDevicePool.instance.saveStatistics();
	}
	
	@Test
	public void testEnum()
	{
		Consts.BusinessProgress progress1 = Consts.BusinessProgress.Init; 
		Consts.BusinessProgress progress2 = Consts.BusinessProgress.SessionBoundConfirmed;
		
		System.out.print("\nSessionBoundConfirmed compareTo Init: " + progress2.compareTo(progress1));
		System.out.print("\nChatConfirmed compareTo Init: " + Consts.BusinessProgress.ChatConfirmed.compareTo(progress1));
		System.out.print("\nSessionBoundConfirmed compareTo progress2: " + Consts.BusinessProgress.SessionBoundConfirmed.compareTo(progress2));
		System.out.print("\nprogress1 compareTo SessionBoundConfirmed: " + progress1.compareTo(Consts.BusinessProgress.SessionBoundConfirmed));
	}
	
	public void testLossConnection()
	{
		/*
		MockApp mockApp1 = new MockApp("deviceSn", "Slave", true);
		mockApp1.syncDevice();
		mockApp1.disconnect();
		*/
		
		MockApp mockApp1 = new MockApp("deviceSn", "Slave", true);
		mockApp1.syncDevice();

		// 2把1踢掉
		MockApp mockApp2 = new MockApp("deviceSn", "Slave", true);
		mockApp2.syncDevice();
		
		// 1被踢掉，重新建立连接之前无法发消息
		mockApp1.sendServerDataSyncRequest();
		
		// 重新建立连接，同步，理论上应该把2踢掉
		mockApp1 = new MockApp("deviceSn", "Slave", true);
		mockApp1.syncDevice();
		
		// 应该失败
		mockApp2.sendServerDataSyncRequest();
	}
	
	@Test
	public void testWebsocketClient()
	{
		String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\",\"timeStamp\":\"2013-10-08 13:01:52.113\",\"messageType\":1,\"messageId\":102,\"messageSn\":\"271Z30DCS7Z6H2B7\",\"deviceId\":0},\"body\":{\"deviceCount\":{\"chat\":null,\"interest\":null,\"randomChat\":null,\"online\":null,\"interestChat\":null,\"random\":null}}}}";
		String serverlocation = "ws://192.81.135.31/renhai/websocket"; 
		URI uri = URI.create(serverlocation);
		
		new Draft_17();
		//AutobahnClientTest e = new AutobahnClientTest(d, uri);
		RenHaiWebSocketClient e = new RenHaiWebSocketClient(uri);
		
		Thread t = new Thread( e );
		t.start();
		e.send(jsonString);
		try {
			t.join();

		} catch ( InterruptedException e1 ) {
			e1.printStackTrace();
		} finally {
			e.closeConnection();
		}
	}
	
	@Test
	public void testBehaviorMode() throws InterruptedException
	{
		MockApp app1 = new MockApp(demoDeviceSn, "NormalAndQuit");
		app1.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		app1.connect(true);
		
		MockApp app2 = new MockApp(demoDeviceSn2, "NormalAndQuit");
		app2.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		app2.connect(true);
		
		while (app1.getBusinessStatus() != MockAppConsts.MockAppBusinessStatus.Ended
				|| app2.getBusinessStatus() != MockAppConsts.MockAppBusinessStatus.Ended)
		{
			Thread.sleep(1000);
			
			if (app1.getBusinessStatus() != MockAppConsts.MockAppBusinessStatus.Ended)
			{
				//app1.sendServerDataSyncRequest();
			}
		}
	}
	
	@Test
	public void testOutputMessageCenter() throws InterruptedException
	{
		OutputMessageCenter.instance.startThreads();
		InputMessageCenter.instance.startThreads();
		OnlineDevicePool.instance.startTimers();
		
		MockApp app1 = new MockApp(demoDeviceSn);
		app1.stopAutoReply();
		app1.syncDevice();
		app1.enterPool(BusinessType.Random);
		
		MockApp app2 = new MockApp(demoDeviceSn2);
		app2.stopAutoReply();
		app2.syncDevice();
		app2.enterPool(BusinessType.Random);
		
		Thread.sleep(1000);
		//app1.waitMessage();
		//app2.waitMessage();
		
		app1.chatConfirm(true);
		app2.chatConfirm(true);
		app1.sendServerDataSyncRequest();
	}
	
	@Test
	public void testNullKeyEntry()
	{
		ConcurrentHashMap<String, String> deviceMap = new ConcurrentHashMap<String, String>();
		deviceMap.put("Key1", "Value1");
		deviceMap.put("Key2", "Value2");
		deviceMap.put("Key3", "Value3");
		deviceMap.put("Key4", "Value4");
		
		Iterator<Entry<String, String>> entryKeyIterator = deviceMap.entrySet().iterator();
        
		int count = 0;
		
		/*
		Set<String> keys = deviceMap.keySet();
		for (String key : keys)
		{
			System.out.println(deviceMap.get(key));
			count ++;
			if (count == 2)
			{
				deviceMap.remove("Key3");
			}
		}
		*/
		
		while (entryKeyIterator.hasNext())
		{
			Entry<String, String> e = entryKeyIterator.next();
			String value = e.getValue();
			System.out.println(value);
			count ++;
			if (count == 2)
			{
				deviceMap.remove("Key4");
			}
		}
	}
	
	@Test
	public void testDBCache()
	{
		System.out.print(OnlineDevicePool.instance.getCapacity());
		
		DeviceDAO dao = new DeviceDAO();
		Device device = dao.findByDeviceSn(demoDeviceSn).get(0);
		Devicecard card = device.getDevicecard();
		card.setAppVersion("30.0");
		
		DAOWrapper.asyncSave(device);
		//DAOWrapper.flushToDB();
		
		Profile profile = device.getProfile();
		profile.setActive("No");
		
		DAOWrapper.asyncSave(profile);
		DAOWrapper.flushToDB();
		
		System.out.print(OnlineDevicePool.instance.getCapacity());
	}
	
	@Test
	public void testEnterPool()
	{
		MockApp app1 = new MockApp(demoDeviceSn);
		//app1.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		//app1.connect(false);
		app1.syncDevice();
		//app1.sendBusinessSessionRequest(Consts.OperationType.LeavePool, null, Consts.BusinessType.Random.toString());
		
		app1.enterPool(BusinessType.Interest);
		
		app1.enterPool(BusinessType.Interest);
		
		app1.enterPool(BusinessType.Random);
		
		app1.setBusinessType(Consts.BusinessType.Random);
		app1.sendBusinessSessionRequest(Consts.OperationType.LeavePool, null, Consts.BusinessType.Random.toString());
		
		app1.setBusinessType(Consts.BusinessType.Interest);
		app1.sendBusinessSessionRequest(Consts.OperationType.LeavePool, null, Consts.BusinessType.Interest.toString());
		
		app1.setBusinessType(Consts.BusinessType.Random);
		app1.sendBusinessSessionRequest(Consts.OperationType.LeavePool, null, Consts.BusinessType.Random.toString());
	}
	
	@Test
	public void testWebsocketClient2() throws InterruptedException, ExecutionException, IOException
	{
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		/*
		Future<Response> f;
		Response r;
		try
		{
			f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
			r = f.get();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		WebSocket websocket = asyncHttpClient.prepareGet("ws://127.0.0.1/renhai/websocket")
			      .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
			          new WebSocketTextListener() {

			          @Override
			          public void onMessage(String message) {
			        	  logger.debug("onMessage: {}", message);
			          }

			          @Override
			          public void onOpen(WebSocket websocket) {
			        	  logger.debug("onOpen");
			              //websocket.sendTextMessage("...");
			          }

			          @Override
			          public void onError(Throwable t) {
			          }

					@Override
					public void onClose(WebSocket websocket)
					{
						logger.debug("onClose");
					}

					@Override
					public void onFragment(String fragment, boolean last)
					{
						logger.debug("onFragment: {}", fragment);
					
					}
			      }).build()).get();

		websocket.sendTextMessage("fdsafdsaklj;");
	}
	
	@Test
	public void testServerDataSync()
	{
		//MockApp app1 = new MockApp(demoDeviceSn,"Slave");
		//app1.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		//app1.connect(true);
		
		MockApp app1 = new MockApp(demoDeviceSn);
		app1.syncDevice();
		app1.sendServerDataSyncRequest();
		
		app1.sendServerDataSyncRequest();
	}
	
	@Test
	public void testRandomScheduler() throws InterruptedException
	{
		MockApp app1 = new MockApp(demoDeviceSn);
		app1.syncDevice();
		app1.enterPool(BusinessType.Random);
		
		MockApp app2 = new MockApp(demoDeviceSn2);
		app2.syncDevice();
		app2.enterPool(BusinessType.Random);
		
		app1.waitMessage();
		app2.waitMessage();
		
		Thread.sleep(2000);
		app1.chatConfirm(true);
		app2.chatConfirm(true);
		
		//String jsonString = "{\"body\":{\"businessType\":1,\"operationInfo\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceCardId\":3,\"deviceModel\":\"iPhone5\",\"isJailed\":1,\"osVersion\":\"6.1.2\",\"registerTime\":\"2013-10-0806:15:30.854\"},\"deviceId\":3,\"deviceSn\":\"demoDeviceSn\",\"profile\":{\"active\":true,\"createTime\":\"2013-10-0806:15:30.854\",\"impressCard\":{\"assessLabelList\":[{\"assessCount\":0,\"assessedCount\":0,\"impressLabelName\":\"^#Happy#^\"}],\"chatLossCount\":0,\"chatTotalCount\":0,\"chatTotalDuration\":0,\"impressLabelList\":[]},\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalInterestLabelId\":2,\"globalMatchCount\":2,\"interestLabelName\":\"Topic6\",\"labelOrder\":2,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":1,\"globalMatchCount\":1,\"interestLabelName\":\"Topic8\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":3,\"globalMatchCount\":3,\"interestLabelName\":\"Topic7\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1}]},\"profileId\":3,\"serviceStatus\":1}}},\"operationType\":7},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":103,\"messageSn\":\"7JWX0VP8R9T2C931\",\"messageType\":1,\"timeStamp\":\"2013-10-2414:30:16.733\"}}";
		//app1.sendRawJSONMessage(jsonString, true);
		Thread.sleep(3000);
		app1.endChat();
		app2.endChat();
		
		Thread.sleep(2000);
		app1.assessAndQuit("^#Happy#^,好感");
		app2.assessAndQuit("^#Happy#^,还行");
		
		app1.sendServerDataSyncRequest();
	}
	
	@Test
	public void testSaveObject() throws InterruptedException
	{
		DeviceDAO dao = new DeviceDAO();
		Device device = dao.findByDeviceSn(demoDeviceSn).get(0);
		
		device.getDevicecard().setAppVersion("15.0123");
		
		Thread.sleep(1000);
		SaveDeviceThread thread = new SaveDeviceThread(device);
		thread.start();
		
		Thread.sleep(1000);
	}
	
}
