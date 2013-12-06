/**
 * MockAppConsts.java
 * 
 * History:
 *     2013-10-10: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

/**
 * 
 */
public class MockAppConsts
{
	public static class Setting
	{
		public static int ChatConfirmDuration = 3 * 1000;
		public static int VideoChatDurationMin = 50 * 1000;
		public static int VideoChatDurationMax = 60 * 1000;
		public static int AssessDuration = 10 * 1000;
		
		public static int MaxChatCount = 3;
	}
	
	public enum MockAppBusinessStatus
	{
		Invalid(0),
		Init(1),
		AppDataSyncReqSent(2),
		AppDataSyncResReceived(3),
		EnterPoolReqSent(4),
		EnterPoolResReceived(5),
		MatchStartReqSent(6),
		MatchStartResReceived(7),
		SessionBoundReceived(8),
		SessionBoundReplied(9),
		AgreeChatReqSent(10),
		AgreeChatResReceived(11),
		RejectChatReqSent(10),			// same with AgreeChatReqSent 
		RejectChatResReceived(11),
		EndChatReqSent(12),
		EndChatResReceived(13),
		AssessReqSent(14),
		AssessResReceived(15),
		SessionUnbindReqSent(16),
		SessionUnbindResReceived(17),
		Ended(18);
		
		private int value;
		private MockAppBusinessStatus(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
	}
	
	
	public enum MockAppRequest
	{
		AlohaRequest(1000),
		AppDataSyncRequest(1001),
		AgreeChat(1002),
		RejectChat(1003),
		ServerDataSyncRequest(1004),
		ChooseBusiness(1005),
		MatchStart(1006),
		UnchooseBusiness(1007),
		EndChat(1008),
		AssessAndContinue(1009),
		AssessAndQuit(1010),
		BusinessSessionNotificationResponse(1011),
		SessionUnbind(1012);
		
		private int value ;
		
		private MockAppRequest(int messageId)
		{
			this.value = messageId;
		}
		
		public int getValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(this.value);
		}
		
		public static MockAppRequest parseValue(int value)
		{
			for (MockAppRequest item : values())
			{
				if (item.value == value)
				{
					return item;
				}
			}
			return null;
		}
	}
	
	public enum MockAppBehaviorMode
	{
		Invalid(""),
		Manual("Manual"),
		SendInvalidJSONCommand("SendInvalidJSONCommand"),
		NoAppSyncRequest("NoAppSyncRequest"),
		// Needs to send AppDataSyncRequest from now on
		
		NoEnterPoolRequest("NoEnterPoolRequest"),
		// Needs to request enter pool from now on 
		
		NoResponseForSessionBound("NoResponseForSessionBound"),
		// Needs to response sessionbound from now on
		
		NoRequestOfAgreeChat("NoRequestOfAgreeChat"),
		ConnectLossDuringChatConfirm("ConnectLossDuringChatConfirm"),
		RejectChat("RejectChat"),
		// Needs to agree or reject chat from now on
		
		ConnectLossDuringChat("ConnectLossDuringChat"),
		NoRequestOfAssess("NoRequestOfAgreeChat"),
		// Needs to send assess request from now on
		
		NormalAndContinue("NormalAndContinue"),
		NormalAndQuit("NormalAndQuit");
		// Needs to send everything
		
		private String value;
		private MockAppBehaviorMode(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return value;
		}
		
		public static MockAppBehaviorMode parseFromStringValue(String value)
		{
			for (MockAppBehaviorMode item : values())
			{
				if (item.name().equals(value))
				{
					return item;
				}
			}
			return Invalid;
		}
	}
}
