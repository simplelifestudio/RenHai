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

import com.simplelife.renhai.server.business.KeywordFilter;
import com.simplelife.renhai.server.business.pool.HotLabel;
import com.simplelife.renhai.server.test.MockApp;
import com.simplelife.renhai.server.test.MockAppConsts;
import com.simplelife.renhai.server.util.Consts.BusinessType;

/**
 * 
 */
public class TestKeywordFilter
{
	@Test
	public void testKeyFilter()
	{
		String filterString = "һҹ��,Լ��,����";
		KeywordFilter.init(filterString);
		ArrayList<String> tmpKey = new ArrayList<>();
		tmpKey.add("һҹ��");
		tmpKey.add("����");
		tmpKey.add("Լ��");
		tmpKey.add("ϰ��ƽ");
		tmpKey.add("����");
		tmpKey.add("��ɽ");
		
		System.out.println("filterString:" + filterString);
		System.out.println("Before filter: " + tmpKey.toString());
		KeywordFilter.removeFilteredKeyword(tmpKey);
		System.out.println("After filter: " + tmpKey.toString());
	}
	
	@Test
	public void testKeyFilter2()
	{
		String filterString = "һҹ��,Լ��,����,ë��";
		KeywordFilter.init(filterString);
		
		LinkedList<HotLabel> list = new LinkedList<>();
		
		HotLabel tmpLabel = new HotLabel();
		tmpLabel.setLabelName("һҹ��");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("����");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("Լ��");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("ϰ��ƽ");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("����");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("��ɽ");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		
		System.out.println("filterString:" + filterString);
		System.out.print("Before filter: ");
		for (int i = list.size() -1; i >=0; i--)
		{
			System.out.print(list.get(i).getLabelName() + ",");
		}
		System.out.print("\r\n");
		
		KeywordFilter.filterHotLabel(list);
		System.out.print("After filter: ");
		for (int i = list.size() -1; i >=0; i--)
		{
			System.out.print(list.get(i).getLabelName() + ",");
		}
		System.out.print("\r\n");
	}
	
	@Test
	public void testHotLabel()
	{
		MockApp.mockAppExecutePool.startService();
		MockApp app1 = new MockApp(
				"MockApp1", 
				MockAppConsts.MockAppBehaviorMode.Manual.name(), 
				"ws://106.186.16.174/renhai/websocket", 
				"һҹ��,����,��ɽ,Լ��,ģ��");
		app1.syncDevice();
		app1.chooseBusiness(BusinessType.Interest);
		
		MockApp app2 = new MockApp(
				"MockApp2", 
				MockAppConsts.MockAppBehaviorMode.Manual.name(), 
				"ws://106.186.16.174/renhai/websocket", 
				"����,����,��ɽ,Լ��,ģ��");
		app2.syncDevice();
		app2.chooseBusiness(BusinessType.Interest);
		
		app1.sendServerDataSyncRequest();
	}
}
