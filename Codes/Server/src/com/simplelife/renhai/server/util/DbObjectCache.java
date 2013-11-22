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
		
		public Counter(String key)
		{
			this.key = key;
		}
		
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

	public boolean putObject(String key, T obj)
	{
		if (hashMap.containsKey(key))
		{
			Object existObj = hashMap.get(key);
			if (existObj != obj)
			{
				//logger.error("There is different object with same key <{}> in DB loading cache.", key);
				return false;
			}
			else
			{
				return true;
			}
		}
		countMap.put(key, new Counter(key));
		hashMap.put(key, obj);
		
		if (hashMap.size() > capacity + GlobalSetting.DBSetting.CacheCompressBuffer)
		{
			compressCache();
		}
		return true;
	}
	
	private void compressCache()
	{
		Object[] counters = countMap.values().toArray();
		Arrays.sort(counters);
		Counter counter;
		for (int i = 0; i < capacity; i++)
		{
			counter = (Counter) counters[i];
			countMap.remove(counter.key);
			hashMap.remove(counter.key);
		}
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
				countMap.put(key, new Counter(key));
				hashMap.put(key, obj);
				
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
			
			// If there is global label object with same label name in cache
			// replace global label by existent global label object  
			boolean isNewObject = DBModule.instance.impressLabelCache.putObject(impressLabel.getImpressLabelName(), impressLabel);
			if (!isNewObject)
			{
				Globalimpresslabel oldLabel = DBModule.instance.impressLabelCache.getObject(impressLabel.getImpressLabelName());
				map.setGlobalLabel(oldLabel);
			}
		}
		
		Set<Interestlabelmap> interestMaps = device.getProfile().getInterestCard().getInterestLabelMapSet();
		Globalinterestlabel interestLabel;
		for (Interestlabelmap map : interestMaps)
		{
			interestLabel = map.getGlobalLabel();
			// If there is global label object with same label name in cache
			// replace global label by existent global label object  
			boolean isNewObject = DBModule.instance.interestLabelCache.putObject(interestLabel.getInterestLabelName(), interestLabel);
			if (!isNewObject)
			{
				Globalinterestlabel oldLabel = DBModule.instance.interestLabelCache.getObject(interestLabel.getInterestLabelName());
				map.setGlobalLabel(oldLabel);
			}
		}
	}
}
