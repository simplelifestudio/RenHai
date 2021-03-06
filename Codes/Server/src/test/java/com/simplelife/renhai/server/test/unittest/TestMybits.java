/**
 * TestMybits.java
 * 
 * History:
 *     2013-11-1: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

/**
 * 
 */
public class TestMybits
{
	/*
	@Test
	public void testExecuteSql()
	{
		String sql = "update devicecard set registerTime=" + System.currentTimeMillis() 
				+ " where deviceId = 2";
		
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
	    session.update(sql);
	    session.close();
	}
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
	    
		Device device = DBModule.instance.deviceCache.getObject("demoDeviceSn");
		Collection<Interestlabelmap> labels = device.getProfile().getInterestCard().getInterestLabelMapSet();
		for (Interestlabelmap label : labels)
		{
			Globalinterestlabel insLabel = DBModule.instance.interestLabelCache.getObject(label.getGlobalLabel().getInterestLabelName());
			System.out.print("===========interest label: " + insLabel.getInterestLabelName() + "\n");
		}
		
		Collection<Impresslabelmap> impresslabels = device.getProfile().getImpressCard().getImpressLabelMapSet();
		for (Impresslabelmap label : impresslabels)
		{
			Globalimpresslabel impLabel = DBModule.instance.impressLabelCache.getObject(label.getGlobalLabel().getImpressLabelName());
			System.out.print("===========impress label: " + impLabel.getImpressLabelName() + "\n");
		}
		
		
		session.close();
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
		    Device device = mapper.selectByStringKey("chen");
		    
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
	*/
	public void testInsert()
	{
	}
}
