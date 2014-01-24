/**
 * TestKeywordFilter.java
 * 
 * History:
 *     2014-1-19: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.simplelife.renhai.server.business.KeywordFilter;
import com.simplelife.renhai.server.test.MockApp;
import com.simplelife.renhai.server.test.MockAppConsts;
import com.simplelife.renhai.server.util.ComparableResult;
import com.simplelife.renhai.server.util.Consts.BusinessType;

/**
 * 
 */
public class TestKeywordFilter
{
	@Test
	public void testKeyFilter()
	{
		JSONArray array = new JSONArray();
		array.add("һҹ��");
		array.add("Լ��");
		array.add("����");
		array.add("ë��");
		KeywordFilter.init(array);
		
		ArrayList<String> tmpKey = new ArrayList<>();
		tmpKey.add("һҹ��");
		tmpKey.add("����");
		tmpKey.add("Լ��");
		tmpKey.add("ϰ��ƽ");
		tmpKey.add("����");
		tmpKey.add("��ɽ");
		
		System.out.println("Before filter: " + tmpKey.toString());
		KeywordFilter.removeFilteredKeyword(tmpKey);
		System.out.println("After filter: " + tmpKey.toString());
	}
	
	@Test
	public void testKeyFilter2()
	{
		JSONArray array = new JSONArray();
		array.add("һҹ��");
		array.add("Լ��");
		array.add("����");
		array.add("ë��");
		KeywordFilter.init(array);
		
		LinkedList<ComparableResult> list = new LinkedList<>();
		
		ComparableResult tmpLabel = new ComparableResult();
		tmpLabel.setField("һҹ��");
		tmpLabel.setCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new ComparableResult();
		tmpLabel.setField("����");
		tmpLabel.setCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new ComparableResult();
		tmpLabel.setField("Լ��");
		tmpLabel.setCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new ComparableResult();
		tmpLabel.setField("ϰ��ƽ");
		tmpLabel.setCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new ComparableResult();
		tmpLabel.setField("����");
		tmpLabel.setCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new ComparableResult();
		tmpLabel.setField("��ɽ");
		tmpLabel.setCount(20);
		list.add(tmpLabel);
		
		
		System.out.print("Before filter: ");
		for (int i = list.size() -1; i >=0; i--)
		{
			System.out.print(list.get(i).toString() + ",");
		}
		System.out.print("\r\n");
		
		KeywordFilter.filterHotLabel(list);
		System.out.print("After filter: ");
		for (int i = list.size() -1; i >=0; i--)
		{
			System.out.print(list.get(i).getField().toString() + ",");
		}
		System.out.print("\r\n");
	}
	
	@Test
	public void testHotLabel()
	{
		//String serverIp = "192.168.1.2";
		//String serverIp = "106.186.16.174";
		String serverIp = "192.81.135.31";
		MockApp.mockAppExecutePool.startService();
		MockApp app1 = new MockApp(
				"MockApp1", 
				MockAppConsts.MockAppBehaviorMode.Manual.name(), 
				"ws://" + serverIp +"/renhai/websocket", 
				"һҹ��,����,��ɽ,Լ��,ģ��");
		app1.syncDevice();
		app1.chooseBusiness(BusinessType.Interest);
		
		MockApp app2 = new MockApp(
				"MockApp2", 
				MockAppConsts.MockAppBehaviorMode.Manual.name(), 
				"ws://" + serverIp +"/renhai/websocket", 
				"����,����,��ɽ,Լ��,ģ��");
		app2.syncDevice();
		app2.chooseBusiness(BusinessType.Interest);
		
		app1.sendServerDataSyncRequest();
	}
}
