/**
 * SqlUtil.java
 * 
 * History:
 *     2013-10-31: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.db;

import java.util.Set;

import org.slf4j.Logger;

/**
 * 
 */
public class SqlUtil
{
	private static Logger logger = DBModule.instance.getLogger();
	private static void appendSave(StringBuilder strBui, Integer value)
	{
		if (value == null)
		{
			strBui.append("null,");
		}
		else
		{
			strBui.append(value);
			strBui.append(",");
		}
	}
	
	private static void appendSave(StringBuilder strBui, Long value)
	{
		if (value == null)
		{
			strBui.append("null,");
		}
		else
		{
			strBui.append(value);
			strBui.append(",");
		}
	}
	
	private static void appendSave(StringBuilder strBui, String value)
	{
		if (value == null)
		{
			strBui.append("null,");
		}
		else
		{
			strBui.append("'");
			strBui.append(value);
			strBui.append("',");
		}
	}
	
	private static void appendColumn(StringBuilder strBui, String column)
	{
		strBui.append(column);
		strBui.append(",");
	}
	
	private static void appendUpdate(StringBuilder strBui, String column, String value)
	{
		if (value == null)
		{
			strBui.append(column);
			strBui.append("=null,");
			return;
		}
		
		strBui.append(column);
		strBui.append("='");
		strBui.append(value);
		strBui.append("',");
	}
	
	private static void appendUpdate(StringBuilder strBui, String column, Integer value)
	{
		if (value == null)
		{
			strBui.append(column);
			strBui.append("=null,");
			return;
		}
		
		strBui.append(column);
		strBui.append("=");
		strBui.append(value);
		strBui.append(",");
	}
	
	private static void appendUpdate(StringBuilder strBui, String column, Long value)
	{
		if (value == null)
		{
			strBui.append(column);
			strBui.append("=null,");
			return;
		}
		
		strBui.append(column);
		strBui.append("=");
		strBui.append(value);
		strBui.append(",");
	}

	public static String getDeviceSaveSql(Device device)
	{
		StringBuilder strBui = new StringBuilder();
		getDeviceSaveSql(device, strBui);
		return strBui.toString();
	}
	public static void getDeviceSaveSql(Device device, StringBuilder strBui)
	{
		if (device.getDeviceId() == null)
		{
			strBui.append("insert into ");
			strBui.append(TableName.Device);
			strBui.append("(");
			strBui.append(TableColumnName.DeviceSn);
			strBui.append(") values ('");
			
			strBui.append(device.getDeviceSn());
			strBui.append("');");
		}
		
		getDeviceCardSaveSql(device.getDevicecard(), strBui, device.getDeviceId());
		getProfileSaveSql(device.getProfile(), strBui);
	}
	
	
	private static void getDeviceCardSaveSql(Devicecard card, StringBuilder strBui, Integer deviceId)
	{
		if (card.getDeviceCardId() != null)
		{
			strBui.append("update ");
			strBui.append(TableName.DeviceCard);
			strBui.append(" set ");
			appendUpdate(strBui, TableColumnName.RegisterTime, card.getRegisterTime());
			appendUpdate(strBui, TableColumnName.DeviceModel, card.getDeviceModel());
			appendUpdate(strBui, TableColumnName.OsVersion, card.getOsVersion());
			appendUpdate(strBui, TableColumnName.AppVersion, card.getAppVersion());
			appendUpdate(strBui, TableColumnName.Location, card.getLocation());
			appendUpdate(strBui, TableColumnName.IsJailed, card.getIsJailed());
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(" where ");
			strBui.append(TableColumnName.DeviceId);
			strBui.append("=");
			strBui.append(card.getDeviceCardId());
			strBui.append(";");
			return;
		}
		
		if (deviceId == null)
		{
			strBui.append("select @");
			strBui.append(TableColumnName.DeviceId);
			strBui.append(" \\:=(select max(");
			strBui.append(TableColumnName.DeviceId);
			strBui.append(") from ");
			strBui.append(TableName.Device);
			strBui.append(");");
		}
		
		strBui.append("insert into ");
		strBui.append(TableName.DeviceCard);
		strBui.append("(");
		appendColumn(strBui, TableColumnName.DeviceId);
		appendColumn(strBui, TableColumnName.RegisterTime);
		appendColumn(strBui, TableColumnName.DeviceModel);
		appendColumn(strBui, TableColumnName.OsVersion);
		appendColumn(strBui, TableColumnName.AppVersion);
		appendColumn(strBui, TableColumnName.Location);
		appendColumn(strBui, TableColumnName.IsJailed);
		strBui.deleteCharAt(strBui.length() - 1);
		
		strBui.append(") values (");
		if (deviceId == null)
		{
			strBui.append("@");
			appendColumn(strBui, TableColumnName.DeviceId);
		}
		else
		{
			appendSave(strBui, deviceId);
		}
		
		appendSave(strBui, card.getRegisterTime());
		appendSave(strBui, card.getDeviceModel());
		appendSave(strBui, card.getOsVersion());
		appendSave(strBui, card.getAppVersion());
		appendSave(strBui, card.getLocation());
		appendSave(strBui, card.getIsJailed());
		strBui.deleteCharAt(strBui.length() - 1);
		strBui.append(");");
	}
	
	private static void getProfileSaveSql(Profile profile, StringBuilder strBui)
	{
		if (profile.getProfileId() != null)
		{
			strBui.append("update ");
			strBui.append(TableName.Profile);
			strBui.append(" set ");
			appendUpdate(strBui, TableColumnName.ServiceStatus, profile.getServiceStatus());
			appendUpdate(strBui, TableColumnName.UnbanDate, profile.getUnbanDate());
			appendUpdate(strBui, TableColumnName.LastActivityTime, profile.getLastActivityTime());
			appendUpdate(strBui, TableColumnName.CreateTime, profile.getCreateTime());
			appendUpdate(strBui, TableColumnName.Active,profile.getActive());
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(" where ");
			strBui.append(TableColumnName.ProfileId);
			strBui.append("=");
			strBui.append(profile.getProfileId());
			strBui.append(";");
		}
		else
		{
			strBui.append("insert into ");
			strBui.append(TableName.Profile);
			strBui.append("(");
			appendColumn(strBui, TableColumnName.DeviceId);
			appendColumn(strBui, TableColumnName.ServiceStatus);
			appendColumn(strBui, TableColumnName.UnbanDate);
			appendColumn(strBui, TableColumnName.LastActivityTime);
			appendColumn(strBui, TableColumnName.CreateTime);
			appendColumn(strBui, TableColumnName.Active);
			strBui.deleteCharAt(strBui.length() - 1);
	
			// Here supports @DeviceId has been set when saving deviceCard
			strBui.append(") values (@");
			appendColumn(strBui, TableColumnName.DeviceId);
			appendSave(strBui, profile.getServiceStatus());
			appendSave(strBui, profile.getUnbanDate());
			appendSave(strBui, profile.getLastActivityTime());
			appendSave(strBui, profile.getCreateTime());
			appendSave(strBui, profile.getActive());
			strBui.deleteCharAt(strBui.length() - 1);
			strBui.append(");");
		}
		getInterestCardSaveSql(profile.getInterestcard(), strBui);
		getImpressCardSaveSql(profile.getImpresscard(), strBui);
	}
	
	private static void getInterestCardSaveSql(Interestcard card, StringBuilder strBui)
	{
		if (card.getInterestCardId() == null)
		{
			strBui.append("insert into ");
			strBui.append(TableName.InterestCard);
			strBui.append("(");
			appendColumn(strBui, TableColumnName.ProfileId);
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(") select max(");
			appendColumn(strBui, TableColumnName.ProfileId);
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(") from ");
			strBui.append(TableName.Profile);
			strBui.append(";");
		}
		
		Set<Interestlabelmap> labelMapSet = card.getInterestlabelmaps();
		for (Interestlabelmap labelMap : labelMapSet)
		{
			getInterestLabelMapSaveSql(labelMap, labelMap.getGlobalinterestlabel().getInterestLabelName(), strBui, card.getInterestCardId());
		}
	}
	
	public static void getInterestLabelMapSaveSql(Interestlabelmap labelMap, String globalInterestLabelName, StringBuilder strBui, Integer interestCardId)
	{
		if (labelMap.getInterestLabelMaplId() != null)
		{
			strBui.append("update ");
			strBui.append(TableName.InterestLabelMap);
			strBui.append(" set ");
			appendUpdate(strBui, TableColumnName.LabelOrder, labelMap.getLabelOrder());
			appendUpdate(strBui, TableColumnName.MatchCount, labelMap.getMatchCount());
			appendUpdate(strBui, TableColumnName.ValidFlag, labelMap.getValidFlag());
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(" where ");
			strBui.append(TableColumnName.InterestLabelMapId);
			strBui.append("=");
			strBui.append(labelMap.getInterestLabelMaplId());
			strBui.append(";");
			return;
		}
		
		strBui.append("select @");
		strBui.append(TableColumnName.GlobalInterestLabelId);
		strBui.append(" \\:=(select ");
		strBui.append(TableColumnName.GlobalInterestLabelId);
		strBui.append(" from ");
		strBui.append(TableName.GlobalInterestLabel);
		strBui.append(" where ");
		strBui.append(TableColumnName.InterestLabelName);
		strBui.append("='");
		strBui.append(globalInterestLabelName);
		strBui.append("');");
		
		if (interestCardId == null)
		{
			strBui.append("select @");
			strBui.append(TableColumnName.InterestCardId);
			strBui.append(" \\:=(select max(");
			strBui.append(TableColumnName.InterestCardId);
			strBui.append(") from ");
			strBui.append(TableName.InterestCard);
			strBui.append(");");
		}
		
		strBui.append("insert into ");
		strBui.append(TableName.InterestLabelMap);
		strBui.append("(");
		appendColumn(strBui, TableColumnName.GlobalInterestLabelId);
		appendColumn(strBui, TableColumnName.InterestCardId);
		appendColumn(strBui, TableColumnName.LabelOrder);
		appendColumn(strBui, TableColumnName.MatchCount);
		appendColumn(strBui, TableColumnName.ValidFlag);
		strBui.deleteCharAt(strBui.length() - 1);

		// Here supports @DeviceId has been set when saving deviceCard
		strBui.append(") values (@");
		appendColumn(strBui, TableColumnName.GlobalInterestLabelId);
		
		if (interestCardId == null)
		{
			strBui.append("@");
			strBui.append(TableColumnName.InterestCardId);
			strBui.append(",");
		}
		else
		{
			appendSave(strBui, interestCardId);
		}
		appendSave(strBui, labelMap.getLabelOrder());
		appendSave(strBui, labelMap.getMatchCount());
		appendSave(strBui, labelMap.getValidFlag());
		strBui.deleteCharAt(strBui.length() - 1);
		strBui.append(");");
	}
	
	private static void getImpressCardSaveSql(Impresscard card, StringBuilder strBui)
	{
		if (card.getImpressCardId() != null)
		{
			strBui.append("update ");
			strBui.append(TableName.ImpressCard);
			strBui.append(" set ");
			appendUpdate(strBui, TableColumnName.ChatTotalCount, card.getChatTotalCount());
			appendUpdate(strBui, TableColumnName.ChatTotalDuration, card.getChatTotalDuration());
			appendUpdate(strBui, TableColumnName.ChatLossCount, card.getChatLossCount());
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(" where ");
			strBui.append(TableColumnName.ImpressCardId);
			strBui.append("=");
			strBui.append(card.getImpressCardId());
			strBui.append(";");
		}
		else
		{
			strBui.append("select @");
			strBui.append(TableColumnName.ProfileId);
			strBui.append(" \\:=(select max(");
			strBui.append(TableColumnName.ProfileId);
			strBui.append(") from ");
			strBui.append(TableName.Profile);
			strBui.append(");");
			
			strBui.append("insert into ");
			strBui.append(TableName.ImpressCard);
			strBui.append("(");
			appendColumn(strBui, TableColumnName.ProfileId);
			appendColumn(strBui, TableColumnName.ChatTotalCount);
			appendColumn(strBui, TableColumnName.ChatTotalDuration);
			appendColumn(strBui, TableColumnName.ChatLossCount);
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(") values (@");
			appendColumn(strBui, TableColumnName.ProfileId);
			appendSave(strBui, card.getChatTotalCount());
			appendSave(strBui, card.getChatTotalDuration());
			appendSave(strBui, card.getChatLossCount());
			strBui.deleteCharAt(strBui.length() - 1);
			strBui.append(");");
		}
		
		Set<Impresslabelmap> labelMapSet = card.getImpresslabelmaps();
		for (Impresslabelmap labelMap : labelMapSet)
		{
			getImpressLabelMapSaveSql(labelMap, labelMap.getGlobalimpresslabel().getImpressLabelName(), strBui, card.getImpressCardId());
		}
	}

	public static void getImpressLabelMapSaveSql(Impresslabelmap labelMap, String globalImprssLabelName, StringBuilder strBui, Integer impressCardId)
	{
		if (labelMap.getImpressLabelMaplId() != null)
		{
			strBui.append("update ");
			strBui.append(TableName.ImpressLabelMap);
			strBui.append(" set ");
			appendUpdate(strBui, TableColumnName.AssessedCount, labelMap.getAssessedCount());
			appendUpdate(strBui, TableColumnName.UpdateTime, labelMap.getUpdateTime());
			appendUpdate(strBui, TableColumnName.AssessCount, labelMap.getAssessCount());
			strBui.deleteCharAt(strBui.length() - 1);
			
			strBui.append(" where ");
			strBui.append(TableColumnName.ImpressLabelMapId);
			strBui.append("=");
			strBui.append(labelMap.getImpressLabelMaplId());
			strBui.append(";");
			return;
		}
		
		strBui.append("select @");
		strBui.append(TableColumnName.GlobalImpressLabelId);
		strBui.append(" \\:=(select ");
		strBui.append(TableColumnName.GlobalImpressLabelId);
		strBui.append(" from ");
		strBui.append(TableName.GlobalImpressLabel);
		strBui.append(" where ");
		strBui.append(TableColumnName.ImpressLabelName);
		strBui.append("='");
		strBui.append(globalImprssLabelName);
		strBui.append("');");
		
		if (impressCardId == null)
		{
			strBui.append("select @");
			strBui.append(TableColumnName.ImpressCardId);
			strBui.append(" \\:=(select max(");
			strBui.append(TableColumnName.ImpressCardId);
			strBui.append(") from ");
			strBui.append(TableName.ImpressCard);
			strBui.append(");");
		}
		
		strBui.append("insert into ");
		strBui.append(TableName.ImpressLabelMap);
		strBui.append("(");
		appendColumn(strBui, TableColumnName.GlobalImpressLabelId);
		appendColumn(strBui, TableColumnName.ImpressCardId);
		appendColumn(strBui, TableColumnName.AssessedCount);
		appendColumn(strBui, TableColumnName.UpdateTime);
		appendColumn(strBui, TableColumnName.AssessCount);
		strBui.deleteCharAt(strBui.length() - 1);

		// Here supports @DeviceId has been set when saving deviceCard
		strBui.append(") values (@");
		appendColumn(strBui, TableColumnName.GlobalImpressLabelId);
		
		if (impressCardId == null)
		{
			strBui.append("@");
			strBui.append(TableColumnName.ImpressCardId);
			strBui.append(",");
		}
		else
		{
			appendSave(strBui, impressCardId);
		}
		appendSave(strBui, labelMap.getAssessedCount());
		appendSave(strBui, labelMap.getUpdateTime());
		appendSave(strBui, labelMap.getAssessCount());
		strBui.deleteCharAt(strBui.length() - 1);
		strBui.append(");");
	}
}
