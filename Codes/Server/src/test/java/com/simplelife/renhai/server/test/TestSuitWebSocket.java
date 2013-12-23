/**
 * TestSuitWebSocket.java
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
	Test01KeepWebsocket.class, 
	Test02CreateRealConnection.class 
}
)
public class TestSuitWebSocket
{
	
}
