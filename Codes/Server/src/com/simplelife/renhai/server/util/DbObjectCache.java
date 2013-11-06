/**
 * DbObjectCache.java
 * 
 * History:
 *     2013-11-6: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.log.FileLogger;

/**
 * 
 */
public class DbObjectCache<T>
{
	private class Counter implements Comparable<Counter>
	{
		public String key;
		public int referCount = 1;
		
		public void increaseCount()
		{
			referCount++;
		}

		@Override
		public int compareTo(Counter o)
		{
			return referCount-o.referCount;
		}
	}
	private int capacity;
	private final int capacityBuffer = 30; 
	private ConcurrentHashMap<String, T> hashMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Counter> countMap = new ConcurrentHashMap<>();
	//private ICachableMapper mapper;
	private Class mapperClass;
	private Logger logger = BusinessModule.instance.getLogger();
	
	public DbObjectCache(Class mapperClass)
	{
		this.mapperClass = mapperClass;
	}
	
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}
	
	public int getCapacity()
	{
		HashMap<String, Integer> tempMap = new HashMap<>();
		tempMap.values().toArray();
		return capacity;
	}

	public void putObject(String key, T obj)
	{
		if (hashMap.containsKey(key))
		{
			Object existObj = hashMap.get(key);
			if (existObj != obj)
			{
				//logger.error("There is different object with same key <{}> in DB loading cache.", key);
				return;
			}
		}
		hashMap.put(key, obj);
		countMap.put(key, new Counter());
		
		if (hashMap.size() > capacity + capacityBuffer)
		{
			compressCache();
		}
	}
	
	private void compressCache()
	{
		/*
		Counter[] counters = new Counter[countMap.values().size()];
		countMap.values().toArray(counters);
		Arrays.sort(counters);z
		
		Counter counter;
		for (int i = 0; i < capacity; i++)
		{
			counter = counters[i];
			countMap.remove(counter.key);
			hashMap.remove(counter.key);
		}
		*/
	}
	
	public T getObject(String key)
	{
		if (hashMap.containsKey(key))
		{
			Counter counter = countMap.get(key);
			counter.increaseCount();
			return hashMap.get(key);
		}
		
		logger.debug("Load object with key {} from DB", key);
		SqlSession session = null;
		T obj = null;
		try
		{
			session = DAOWrapper.getSession();
			ICachableMapper mapper = session.getMapper(mapperClass);
			obj = (T) mapper.selectByStringKey(key);
			if (obj != null)
			{
				hashMap.put(key, obj);
				countMap.put(key, new Counter());
				
				if (obj instanceof Device)
				{
					cacheGlobalLabels((Device)obj);
				}
			}
		}
		catch(Exception re)
		{
			FileLogger.printStackTrace(re);
		}
		finally
		{
			if (session != null)
			{
				session.close();
			}
		}
		return obj;
	}
	
	private void cacheGlobalLabels(Device device)
	{
		Set<Impresslabelmap> impressMaps = device.getProfile().getImpressCard().getImpressLabelMapSet();
		Globalimpresslabel impressLabel;
		for (Impresslabelmap map : impressMaps)
		{
			impressLabel = map.getGlobalLabel();
			DBModule.instance.impressLabelCache.putObject(impressLabel.getImpressLabelName(), impressLabel);
		}
		
		Set<Interestlabelmap> interestMaps = device.getProfile().getInterestCard().getInterestLabelMapSet();
		Globalinterestlabel interestLabel;
		for (Interestlabelmap map : interestMaps)
		{
			interestLabel = map.getGlobalLabel();
			DBModule.instance.interestLabelCache.putObject(interestLabel.getInterestLabelName(), interestLabel);
		}
	}
}
