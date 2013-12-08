/**
 * IServer.java
 * 
 * History:
 *     2013-12-7: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 */
public interface IServer extends Comparable<IServer>
{
	String getId();
	boolean checkVersion(String version, int build);
	JSONObject getResponse();
	void init(JSONObject obj);
}
