/**
 * BusinessSessionResponse.java
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
public class BusinessSessionResponse extends ServerJSONMessage
{
	public BusinessSessionResponse(IDeviceWrapper deviceWrapper)
	{
		super(deviceWrapper);
	}
}
