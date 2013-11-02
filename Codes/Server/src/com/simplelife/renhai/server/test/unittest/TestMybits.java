/**
 * TestMybits.java
 * 
 * History:
 *     2013-11-1: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceMapper;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Profile;

/**
 * 
 */
public class TestMybits
{
	@Test
	public void testJoinQuery()
	{
		String resource = "mybatis.xml";
	    Reader reader = null;
		try
		{
			reader = Resources.getResourceAsReader(resource);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
	    SqlSessionFactory factory = builder.build(reader);
	    
	    SqlSession session = factory.openSession();
	    
	    DeviceMapper mapper = session.getMapper(DeviceMapper.class);
	    
		Device device = mapper.selectWholeDeviceByDeviceSn("chen");
		System.out.print("===========" + device.getDeviceCard().getAppVersion() + "\n");
	}
	@Test
	public void testInsert()
	{
		try
		{
			String resource = "mybatis.xml";
		    Reader reader = null;
			try
			{
				reader = Resources.getResourceAsReader(resource);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		    SqlSessionFactory factory = builder.build(reader);
		    
		    SqlSession session = factory.openSession();
		    
		    DeviceMapper mapper = session.getMapper(DeviceMapper.class);
		    Device device = mapper.selectWholeDeviceByDeviceSn("chen");
		    
		    Devicecard card = new Devicecard();
		    device.setDeviceCard(card);
		    Profile profile = new Profile();
		    device.setProfile(profile);
		    
		    card.setAppVersion("chenappversion");
		    card.setDeviceModel("chenmodel");
		    card.setIsJailed("No");
		    card.setLocation(null);
		    card.setOsVersion("chenosversion");
		    card.setRegisterTime(new Long(1234567890));
		    
		    profile.setActive("Yes");
		    profile.setCreateTime(System.currentTimeMillis());
		    profile.setLastActivityTime(System.currentTimeMillis());
		    profile.setServiceStatus("Normal");
		    profile.setUnbanDate(null);
		    
		    device.save(session);
		    
		    session.commit();
		    session.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
