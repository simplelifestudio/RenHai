/**
 * TestDBOperations.java
 * 
 * History:
 *     2013-9-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.GlobalimpresslabelDAO;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.util.Consts;

/**
 * 
 */
public class TestDBOperations  extends TestCase
{
	
	@Test
	public void testInvalidJSON_Empty()
	{
		GlobalimpresslabelDAO dao = new GlobalimpresslabelDAO();
		List<Globalimpresslabel> globalLabelList;
		for (Consts.SolidAssessLabel label : Consts.SolidAssessLabel.values())
		{
			if (label == Consts.SolidAssessLabel.Invalid)
			{
				continue;
			}
			
			String strValue = label.getValue();
			Globalimpresslabel impressLabel;
			globalLabelList = dao.findByImpressLabelName(strValue);
			if (globalLabelList.size() == 0)
			{
				impressLabel = new Globalimpresslabel();
				impressLabel.setGlobalAssessCount(0);
				impressLabel.setImpressLabelName(strValue);
			}
			else
			{
				impressLabel = globalLabelList.get(0);
			}
			
			Impresslabelmap labelMap = new Impresslabelmap();
			labelMap.setAssessCount(0);
			labelMap.setAssessedCount(0);
			labelMap.setGlobalimpresslabel(impressLabel);
			//labelMap.setImpresscard(impressCard);
			labelMap.setUpdateTime(System.currentTimeMillis());
			
			//impressLabelMaps.add(labelMap);
		}
	}
	
}
