/**
 * AppDataSyncResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class AppDataSyncResponse extends ServerJSONMessage
{
	public AppDataSyncResponse(IDeviceWrapper deviceWrapper)
	{
		super(deviceWrapper);
	}
}
