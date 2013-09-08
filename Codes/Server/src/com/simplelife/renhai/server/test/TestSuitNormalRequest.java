/**
 * TestSuitNormalRequest.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses(
{
	Test05SyncDeviceForbidden.class,
	Test06SyncDeviceUnForbidden.class,
	Test07MaxOnlineDevicePool.class,
	Test19MatchConfirmAReject.class,
	Test20MatchConfirmBReject.class,
	Test21UpdateInterestcard.class,
	Test22Assess.class,
	Test23NormalProcessAndStatistics.class,
	Test24MaxBusinessDevicePool.class 
}
)
public class TestSuitNormalRequest
{
	
}
