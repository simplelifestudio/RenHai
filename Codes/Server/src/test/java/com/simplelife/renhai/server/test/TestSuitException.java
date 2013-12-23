/**
 * Exception.java
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
	Test04SyncDeviceNormal.class, 
	Test08TimeoutAfterSyncDevice.class, 
	Test09TimeoutWaitForMatch.class,
	Test10FailToNotifyA.class, 
	Test11FailToNotifyBoth.class, 
	Test12TimeoutNotifyA.class,
	Test13FailToNotifyB.class, 
	Test14TimeoutNotifyB.class, 
	Test15FailToNotifyAAfterBAgree.class,
	Test16TimeoutNotifyAAfterBAgree.class, 
	Test17ChatALoseConnection.class,
	Test18AssessALoseConnection.class 
}
)
public class TestSuitException
{
	
}
