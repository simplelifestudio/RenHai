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
		public static int VideoChatDuration = 10 * 1000;
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
		SessionBoundReceived(6),
		SessionBoundReplied(7),
		AgreeChatReqSent(8),
		AgreeChatResReceived(9),
		RejectChatReqSent(8),
		RejectChatResReceived(9),
		EndChatReqSent(10),
		EndChatResReceived(11),
		AssessReqSent(12),
		AssessResReceived(13),
		Ended(14);
		
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
		AlohaRequest(200),
		AppDataSyncRequest(201),
		AgreeChat(202),
		RejectChat(203),
		ServerDataSyncRequest(204),
		EnterPool(205),
		LeavePool(206),
		EndChat(207),
		AssessAndContinue(208),
		AssessAndQuit(209),
		BusinessSessionNotificationResponse(202);
		
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
		Slave("Slave"),
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
