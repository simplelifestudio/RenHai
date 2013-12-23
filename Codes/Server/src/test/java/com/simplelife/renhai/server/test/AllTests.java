/**
 * AllTests.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses(
{
	TestSuitException.class, 
	TestSuitNormalRequest.class, 
	TestSuitWebSocket.class 
}
)

public class AllTests
{
	public static Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
