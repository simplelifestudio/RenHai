/**
 * Main.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.java_websocket.drafts.Draft_17;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.business.pool.InputMsgExecutorPool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMsgExecutorPool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceMapper;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.GlobalinterestlabelMapper;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Webrtcsession;
import com.simplelife.renhai.server.db.WebrtcsessionMapper;
import com.simplelife.renhai.server.json.AlohaRequest;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.JSONModule;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.test.AbstractTestCase;
import com.simplelife.renhai.server.test.MockApp;
import com.simplelife.renhai.server.test.MockAppConsts;
import com.simplelife.renhai.server.test.MockWebSocketConnection;
import com.simplelife.renhai.server.test.RenHaiWebSocketClient;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.DbObjectCache;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.websocket.SecurityUtils;
import com.simplelife.renhai.server.websocket.WebSocketConnection;
import com.simplelife.renhai.server.websocket.WebSocketModule;


/**
 * 
 */
public class MainFunction extends AbstractTestCase
{
	private final String manualMode = MockAppConsts.MockAppBehaviorMode.Manual.name();
	
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
		String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{},\"dataUpdate\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceModel\":\"iPhone 5\",\"isJailed\":1,\"osVersion\":\"6.1.2\"},\"deviceSn\":\"24237ACE-7F7F-4656-A47C-11CF0473D7BB\"}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"24237ACE-7F7F-4656-A47C-11CF0473D7BB\",\"messageId\":101,\"messageSn\":\"IX5IT4CTLZC03A68\",\"messageType\":1,\"timeStamp\":\"2013-11-05 07:37:50.960\"}}}";
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope);

		MockApp app = new MockApp(demoDeviceSn, manualMode);
		app.setWebsocketLink("ws://192.81.135.31/renhai/websocket");
		app.connect(false);
		app.sendRawJSONMessage(obj, true);
		
		app.sendServerDataSyncRequest();
		
		app.sendRawJSONMessage(obj, true);
		
		app.sendServerDataSyncRequest();
	}
	
	@Test
	public void testReplaceWebsocket()
	{
		MockApp app = new MockApp(demoDeviceSn, manualMode);
		
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
	public void testJSONObject() throws Exception
	{
		JSONObject messageObj = null;
		String tmpStr = "Kz8g5SNtphF85ucenPGJ9hfjBhklb8s0jczpkR+HE2lu32AYu8Gu9AotjnXI 0qUxsDeXo0eWebo1ST2I4rY+/vRmKZuhpPCCtrrTLolUL2h90eII/bz8cwa7 KgGLI5PRRvInyqHp7PzCBVf9Z21t83LRV60Kx+p0o4AKEsFXxHFdm4bkC63A c969P1lrDvcucIvT5NvuOrSokSUXCthw/T/4awdMpU+DBYR9fzbxvo+t6Lam czPfPauVuYF05xibiBTlTbszeqlo1Q6cobZalBXZ7oy24LiwYDHGcdzL972k BHwdnXov6q4YvHaECb0c4wgM7/V92zqH8qVMuRdGkIpA/fTv5vWFsHkVdL61 kHOHXhekRpHiRa3Ys4Mpr4JOOxZjbyIqhRyn6ROTjt3fWnRz6Nmgkjg+KS54 v4Pu1143BWWVkMfY9ged2cs9ZS4TXnkjDh20WEysvNyPEHZSRKGclUuF4tnK H8FI7SZHIjuswV1RRo3NZDZVDNbf+HIWjsTAgrWkSbYmhS1JpR4JqMj/Qoft 6qioe2Z6Ln+Z9mguHggatA+V5+v98rdeSxm/oB/yHtuBOoJqHIOd2r506URY 2HqeNXoR4cswHVS8bIU46hVtPNp8Pxl+k0t2rLG0OYg9gw6UDFvODWGWbAtV Tn3frYg+cTZ3jBUeEAT7qnIM9QdxHBlOfIVgU2HWT9FTs6l+ZhZSHeIKgEPX edSJFWD6zdrqSQEPiDi0F3Y6/gRl5fgZDa1y83NYH98BodskRBe3azvYw+uV uQIExBGJCicyk7NnOqK4AjchkxdgbEzZXQJC2jrVg6A2xNde0Wg3SpDYxn0h 0Lg6WQEQss68R+99/0zzFSgN";
		tmpStr = SecurityUtils.decryptByDESAndDecodeByBase64(tmpStr, GlobalSetting.BusinessSetting.EncryptKey);
		//logger.debug("Message after decrypt:\n{}", tmpStr);
		messageObj = JSONObject.parseObject(tmpStr);
		System.out.println(JSON.toJSONString(messageObj, true));
	}
	
	@Test
	public void testRawCommand()
	{
		BusinessModule.instance.startService();
		DBModule.instance.startService();
		JSONModule.instance.startService();
		WebSocketModule.instance.startService();
		
		//String jsonString ="{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"demoDeviceSn\",\"timeStamp\":\"2013-10-12 16:45:45.003\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"3ZBJO4HCZR43NZX3\",\"deviceId\":3},\"body\":{\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"labelOrder\":0,\"globalMatchCount\":2,\"validFlag\":1,\"interestLabelName\":\"Topic6\",\"matchCount\":0,\"globalInterestLabelId\":2},{\"labelOrder\":0,\"globalMatchCount\":1,\"validFlag\":1,\"interestLabelName\":\"Topic8\",\"matchCount\":0,\"globalInterestLabelId\":1},{\"labelOrder\":0,\"globalMatchCount\":3,\"validFlag\":1,\"interestLabelName\":\"Topic7\",\"matchCount\":0,\"globalInterestLabelId\":3},{\"labelOrder\":0,\"globalMatchCount\":2,\"validFlag\":1,\"interestLabelName\":\"Topic6\",\"matchCount\":0,\"globalInterestLabelId\":2},{\"labelOrder\":0,\"globalMatchCount\":3,\"validFlag\":1,\"interestLabelName\":\"Topic7\",\"matchCount\":0,\"globalInterestLabelId\":3},{\"labelOrder\":0,\"globalMatchCount\":1,\"validFlag\":1,\"interestLabelName\":\"Topic8\",\"matchCount\":0,\"globalInterestLabelId\":1}]}}}},\"dataQuery\":{\"device\":{\"profile\":{\"interestCard\":null}}}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalInterestLabelId\":10,\"globalMatchCount\":10,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":12,\"globalMatchCount\":12,\"interestLabelName\":\"Allen\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":13,\"globalMatchCount\":13,\"interestLabelName\":\"Chris\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":11,\"globalMatchCount\":11,\"interestLabelName\":\"Tomas\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"48UA5C56982G3MB8\",\"messageType\":1,\"timeStamp\":\"2013-10-12 22:14:00.749\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalInterestLabelId\":10,\"globalMatchCount\":10,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":12,\"globalMatchCount\":12,\"interestLabelName\":\"Allen\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"48UA5C56982G3MB8\",\"messageType\":1,\"timeStamp\":\"2013-10-12 22:14:00.749\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"header\":{\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"timeStamp\":\"2013-10-12 20:17:41.630\",\"messageType\":1,\"messageId\":101,\"messageSn\":\"O54JKG4HXZ5EU846\",\"deviceId\":3},\"body\":{\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"labelOrder\":1,\"globalMatchCount\":1,\"validFlag\":1,\"interestLabelName\":\"Topic8\",\"matchCount\":0,\"globalInterestLabelId\":1},{\"labelOrder\":4,\"globalMatchCount\":2,\"validFlag\":1,\"interestLabelName\":\"Topic6\",\"matchCount\":0,\"globalInterestLabelId\":2}]}}}},\"dataQuery\":{\"device\":{\"profile\":{\"interestCard\":null}}}}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{},\"dataUpdate\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceModel\":\"Simulator\",\"isJailed\":0,\"osVersion\":\"6.1\"},\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\"}}},\"header\":{\"deviceId\":0,\"deviceSn\":\"EFAD498F-9A95-4B90-A59E-FC599AC21FA3\",\"messageId\":101,\"messageSn\":\"T572P03A06725I13\",\"messageType\":1,\"timeStamp\":\"2013-10-13 20:51:59.747\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{},\"dataUpdate\":{\"device\":{\"deviceCard\":{\"appVersion\":\"0.1\",\"deviceModel\":\"iPhone 5\",\"isJailed\":1,\"osVersion\":\"6.1.2\"},\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\"}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"97QCOOICO8C4T5XN\",\"messageType\":1,\"timeStamp\":\"2013-10-14 06:17:14.663\"}}}";
		//String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestLabelList\":[{\"globalMatchCount\":0,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":0}]}}}}},\"header\":{\"deviceId\":0,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"UPM59M9KUR485GQ3\",\"messageType\":1,\"timeStamp\":\"2013-10-14 11:18:01.285\"}}}";
		
		// Patrick, topic 7, topic 8
		String jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalMatchCount\":0,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":0},{\"globalInterestLabelId\":3,\"globalMatchCount\":3,\"interestLabelName\":\"Topic7\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":1,\"globalMatchCount\":1,\"interestLabelName\":\"Topic8\",\"labelOrder\":2,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"52D3Q6SWC9072GQ2\",\"messageType\":1,\"timeStamp\":\"2013-10-14 13:37:50.057\"}}}";
		JSONObject wholeObj = JSONObject.parseObject(jsonString);
		JSONObject obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope); 
		
		MockApp app = createNewMockApp("45CF7936-3FA1-49B2-937D-D462AB5F378A");
		app.syncDevice();
		app.sendRawJSONMessage(obj, true);
		
		// Patrick, topic 6, topic 8
		jsonString = "{\"jsonEnvelope\":{\"body\":{\"dataQuery\":{\"device\":{\"profile\":{}}},\"dataUpdate\":{\"device\":{\"profile\":{\"interestCard\":{\"interestCardId\":3,\"interestLabelList\":[{\"globalMatchCount\":0,\"interestLabelName\":\"Patrick\",\"labelOrder\":0,\"matchCount\":0,\"validFlag\":0},{\"globalInterestLabelId\":3,\"globalMatchCount\":3,\"interestLabelName\":\"Topic6\",\"labelOrder\":1,\"matchCount\":0,\"validFlag\":1},{\"globalInterestLabelId\":1,\"globalMatchCount\":1,\"interestLabelName\":\"Topic8\",\"labelOrder\":2,\"matchCount\":0,\"validFlag\":1}]}}}}},\"header\":{\"deviceId\":3,\"deviceSn\":\"45CF7936-3FA1-49B2-937D-D462AB5F378A\",\"messageId\":101,\"messageSn\":\"52D3Q6SWC9072GQ2\",\"messageType\":1,\"timeStamp\":\"2013-10-14 13:37:50.057\"}}}";
		wholeObj = JSONObject.parseObject(jsonString);
		obj = wholeObj.getJSONObject(JSONKey.JsonEnvelope);
		app.sendRawJSONMessage(obj, true);
		app.disconnect();
		
		DAOWrapper.flushToDB();
		app.sendServerDataSyncRequest();
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
	public void testSyncDevice()
	{
		MockApp app = new MockApp(demoDeviceSn);
		app.syncDevice();
		app.disconnect();
		
		System.out.print("1");
	}
	
	@Test
	public void testSaveStats()
	{
		OnlineDevicePool.instance.saveStatistics();
	}
	
	@Test
	public void testEnum()
	{
		Consts.DeviceBusinessProgress progress1 = Consts.DeviceBusinessProgress.Init; 
		Consts.DeviceBusinessProgress progress2 = Consts.DeviceBusinessProgress.SessionBoundAcked;
		
		System.out.print("\nSessionBoundConfirmed compareTo Init: " + progress2.compareTo(progress1));
		System.out.print("\nChatConfirmed compareTo Init: " + Consts.DeviceBusinessProgress.ChatAgreed.compareTo(progress1));
		System.out.print("\nSessionBoundConfirmed compareTo progress2: " + Consts.DeviceBusinessProgress.SessionBoundAcked.compareTo(progress2));
		System.out.print("\nprogress1 compareTo SessionBoundConfirmed: " + progress1.compareTo(Consts.DeviceBusinessProgress.SessionBoundAcked));
	}
	
	public void testLossConnection()
	{
		/*
		MockApp mockApp1 = new MockApp("deviceSn", manualMode, true);
		mockApp1.syncDevice();
		mockApp1.disconnect();
		*/
		
		MockApp mockApp1 = new MockApp("deviceSn", manualMode, true);
		mockApp1.syncDevice();

		// 2把1踢掉
		MockApp mockApp2 = new MockApp("deviceSn", manualMode, true);
		mockApp2.syncDevice();
		
		// 1被踢掉，重新建立连接之前无法发消息
		mockApp1.sendServerDataSyncRequest();
		
		// 重新建立连接，同步，理论上应该把2踢掉
		mockApp1 = new MockApp("deviceSn", manualMode, true);
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
	public void testOneNormalApp() throws InterruptedException
	{
		MockApp app1 = new MockApp(demoDeviceSn, "NormalAndQuit");
		app1.setWebsocketLink("ws://192.81.135.31/renhai/websocket");
		app1.connect(true);
		
		while (app1.getBusinessStatus() != MockAppConsts.MockAppBusinessStatus.Ended)
		{
			Thread.sleep(5000);
			
			if (app1.getBusinessStatus() != MockAppConsts.MockAppBusinessStatus.Ended)
			{
				app1.sendServerDataSyncRequest();
			}
		}
	}
	
	@Test
	public void testBehaviorMode() throws InterruptedException
	{
		MockApp app1 = new MockApp(demoDeviceSn, "NormalAndQuit");
		app1.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		//app1.setWebsocketLink("ws://192.81.135.31/renhai/websocket");
		app1.connect(true);
		
		MockApp app2 = new MockApp(demoDeviceSn2, "NormalAndQuit");
		app2.setWebsocketLink("ws://127.0.0.1/renhai/websocket");
		//app2.setWebsocketLink("ws://192.81.135.31/renhai/websocket");
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
		OutputMsgExecutorPool.instance.startService();
		InputMsgExecutorPool.instance.startService();
		OnlineDevicePool.instance.startService();
		
		MockApp app1 = new MockApp(demoDeviceSn);
		app1.stopAutoReply();
		app1.syncDevice();
		app1.chooseBusiness(BusinessType.Random);
		
		MockApp app2 = new MockApp(demoDeviceSn2);
		app2.stopAutoReply();
		app2.syncDevice();
		app2.chooseBusiness(BusinessType.Random);
		
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
	public void testDbCache3()
	{
		Globalinterestlabel globalInterest;
		String tempStr = "音乐";
		globalInterest = DBModule.instance.interestLabelCache.getObject(tempStr);
		
		if (globalInterest == null)
		{
			globalInterest = new Globalinterestlabel();
			globalInterest.setInterestLabelName(tempStr);
			globalInterest.setGlobalMatchCount(0);
			
			DBModule.instance.interestLabelCache.putObject(tempStr, globalInterest);
			
			Globalinterestlabel globalInterest2 = DBModule.instance.interestLabelCache.getObject(tempStr);
			System.out.print("=========id before save:" + globalInterest2.getGlobalInterestLabelId() + "\n");
			
			DAOWrapper.cache(globalInterest);
			DAOWrapper.flushToDB();
		}
		
		Globalinterestlabel globalInterest2 = DBModule.instance.interestLabelCache.getObject(tempStr);
		System.out.print("=========id after save:" + globalInterest2.getGlobalInterestLabelId() + "\n");
	}
	
	@Test
	public void testDbCache2()
	{
		DbObjectCache<String> cache = new DbObjectCache<>(null);
		cache.setCapacity(GlobalSetting.DBSetting.DeviceCacheCount);
		
		String temp;
		Random random = new Random();
		for (int i = 0; i < GlobalSetting.DBSetting.DeviceCacheCount + 50; i++)
		{
			temp = Integer.toString(i);
			cache.putObject(temp, temp);
			
			int k = random.nextInt(20);
			for (int j = 0; j < k; j++)
			{
				cache.getObject(temp);
			}
		}
	}
	
	@Test
	public void testDBCache()
	{
		Device device = DBModule.instance.deviceCache.getObject(demoDeviceSn);
		
		device = DBModule.instance.deviceCache.getObject(demoDeviceSn2);
		
		Devicecard card = device.getDeviceCard();
		card.setAppVersion("30.0");
		
		DAOWrapper.cache(device);
		//DAOWrapper.flushToDB();
		
		Profile profile = device.getProfile();
		profile.setActive("No");
		
		DAOWrapper.cache(profile);
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
		
		MockApp app2 = new MockApp(demoDeviceSn2);
		app2.syncDevice();
		
		app1.chooseBusiness(BusinessType.Interest);
		app1.sendServerDataSyncRequest();
		
		app1.chooseBusiness(BusinessType.Interest);
		
		app1.chooseBusiness(BusinessType.Random);
		
		app1.matchStart();
		
		app1.setBusinessType(Consts.BusinessType.Random);
		app1.sendServerDataSyncRequest();
		
		app2.chooseBusiness(BusinessType.Interest);
		app1.sendServerDataSyncRequest();
		
		app1.unchooseBusiness();
		app1.sendServerDataSyncRequest();
		
		app1.setBusinessType(Consts.BusinessType.Interest);
		app1.unchooseBusiness();
		app1.sendServerDataSyncRequest();
		
		app2.unchooseBusiness();
		app1.sendServerDataSyncRequest();
	}
	
	@Test
	public void testAppDataSync() throws InterruptedException
	{
		MockApp app1 = new MockApp(demoDeviceSn, manualMode);
		app1.setWebsocketLink("ws://192.81.135.31/renhai/websocket");
		app1.connect(true);
		
		app1.syncDevice();
		
		Thread.sleep(1000);
		
		app1.syncDevice();
	}
	
	@Test
	public void testServerDataSync()
	{
		//MockApp app1 = new MockApp(demoDeviceSn,manualMode);
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
		app1.chooseBusiness(BusinessType.Random);
		
		MockApp app2 = new MockApp(demoDeviceSn2);
		app2.syncDevice();
		app2.chooseBusiness(BusinessType.Random);
		
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
	public void testDuplicatedGlobalLabel()
	{
		SqlSession session = DAOWrapper.getSession();
		try
		{
			Globalinterestlabel globalInterest = new Globalinterestlabel();
			globalInterest.setInterestLabelName("音乐");
			globalInterest.setGlobalMatchCount(0);
			
			GlobalinterestlabelMapper mapper = session.getMapper(GlobalinterestlabelMapper.class);
			mapper.insert(globalInterest);
			session.commit();
		}
		catch(PersistenceException en)
		{
			System.out.print("========OK!\n");
			en.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}
	}

	@Test
	public void testRandomInterestLabel()
	{
		Device device = DBModule.instance.deviceCache.getObject(demoDeviceSn);
		Collection<Interestlabelmap> labelSet = device.getProfile().getInterestCard().getInterestLabelMapSet();
		LinkedList<String> orgLabels = new LinkedList<>();
		for (Interestlabelmap map : labelSet)
		{
			orgLabels.add(map.getGlobalLabel().getInterestLabelName());
		}
		
		Random random = new Random();
		LinkedList<String> labels = new LinkedList<>();
		while (orgLabels.size() > 0)
		{
			labels.add(orgLabels.remove(random.nextInt(orgLabels.size())));
		}
		
		for (String label : labels)
		{
			logger.debug(label);
		}
	}
	
	@Test
	public void testSelectWebrtcsession()
	{
		//Webrtcsession rtc = WebRTCSessionPool.instance.createWebRTCSession();
		//DAOWrapper.flushToDB();
		
		SqlSession session = DAOWrapper.getSession();
		WebrtcsessionMapper mapper = session.getMapper(WebrtcsessionMapper.class);
		List<Webrtcsession> list = mapper.selectAll(1);
		for (Webrtcsession rtcSession : list)
		{
			System.out.println(rtcSession.getWebrtcsession());
		}
	}
	
	@Test
	public void testQueryDeviceWithOrderBy()
	{
		SqlSession session = DAOWrapper.getSession();
		DeviceMapper mapper = session.getMapper(DeviceMapper.class);
		Device device = mapper.selectByStringKey(demoDeviceSn);
		IDeviceWrapper deviceWrapper = new DeviceWrapper(new MockWebSocketConnection());
		deviceWrapper.setDevice(device);
		System.out.print(JSON.toJSONString(deviceWrapper.toJSONObject(), true));
		
		MockApp mockApp = new MockApp(demoDeviceSn);
		mockApp.syncDevice();
		deviceWrapper = OnlineDevicePool.instance.getDevice(demoDeviceSn);
		System.out.print(JSON.toJSONString(deviceWrapper.toJSONObject(), true));
	}
}
