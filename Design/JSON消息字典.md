
#<center>RenHai系统JSON消息字典

#消息分类 MessageType
消息分类编号即代码枚举值
###0. Unknown类
###1. AppRequest类
###2. AppResponse类
###3. ServerNotification类
###4. ServerResponse类

#消息列表 MessageId
消息编号即代码枚举值，且编号的第一位映射了消息分类编号

##AppRequest类消息
App主动发往Server的消息
###100. 测试请求 AlohaRequest
###101. App数据同步请求 AppDataSyncRequest
###102. Server数据同步请求 ServerDataSyncRequest
###103. 业务会话请求 BusinessSessionRequest

##AppResponse类消息
App接收到Server消息后，向Server回应的消息
###200. 超时响应 AppTimeoutResponse
###201. 错误响应 AppErrorResponse
###202. 业务会话通知响应 BusinessSessionNotificationResponse

##ServerNotification类消息
Server主动发往App的消息
###300. 业务会话通知 BusinessSessionNotification
###301. 广播通知 BroadcastNotification

##ServerResponse类消息
Server接收到App消息后，向App回应的消息
###400. 超时响应 ServerTimeoutResponse
###401. 错误响应 ServerErrorResponse
###402. 测试响应 AlohaResponse
###403. App数据同步响应 AppDataSyncResponse
###404. Server数据同步响应 ServerDataSyncResponse
###405. 业务会话响应 BusinessSessionResponse

#消息配对
##100. 测试请求 AlohaRequest <--> 402. 测试响应 AlohaResponse
##101. App数据同步请求 AppDataSyncRequest <--> 403. App数据同步响应
##102. Server数据同步请求 ServerDataSyncRequest <--> 404. Server数据同步响应 ServerDataSyncResponse
##103. 业务会话请求 BusinessSessionRequest <--> 业务会话响应 BusinessSessionResponse
##300. 业务会话通知 BusinessSessionNotification <--> 业务会话通知响应 BusinessSessionNotificationResponse

#消息格式 MessageFormat
以"jsonEnvelop"作为JSON消息的信封外套，对应的值即实际的业务消息（已编码/已加密/已压缩）
<pre><code>
{
	"jsonEnvelope": // 信封外套
	{
		"header": // 业务消息头部
		{
			"messageType":"1", // 消息类型
			"messageSn":"AFLNWERJL3203598FDLGSLDF", // 消息序列号（请求和响应一对消息使用同一个序列号）
			"messageId":"100", // 消息编号
			"deviceId":"1234", // 设备编号（数据库字段）
			"deviceSn":"ABCD77631GGWQ", // 设备唯一序列号
			"timeStamp":"2013-08-14 21:18:49" // 时间戳（App启动后首次收到Server发来的消息时需要计算两边时间差并保存留用）
    	},
    	"body": // 业务消息主体
    	{		
			"param1":"value1",
			"param2":"value2",
			"paramn":"valuen"
    	}
	}
}
</code></pre>

#数据结构体

###1. 设备数据结构体
100%映射自Server的数据表设计
<pre><code>
{
	"device":
	{
		"deviceId":"",
		"deviceSn":"",
		"deviceCard":
		{
			"deviceCardId":"",
			"deviceId":"",
			"registerTime":"",
			"deviceModel":"",
			"osVersion":"",
			"appVersion":"",
			"location":"",
			"isJailed":""
		},
		"profile":
		{
			"profileId":"",
			"serviceStatus":"",
			"unbanDate":"",
			"lastActivityTime":"",
			"createTime":"",
			"active":"",
			"interestCard":
			{
				"interestCardId":"",
				"interestLabelList":
				[
					{
						"globalInterestLabelId":"",
						"interestLabel":"",
						"globalMatchCount":"",
						"order":"",
						"matchCount":"",
						"validFlag":""
					},
					{
						"globalInterestLabelId":"",
						"interestLabel":"",
						"globalMatchCount":"",
						"order":"",
						"matchCount":"",
						"validFlag":""
					},
					{
						"globalInterestLabelId":"",
						"interestLabel":"",
						"globalMatchCount":"",
						"order":"",
						"matchCount":"",
						"validFlag":""
					}
				]
			},
			"impressCard":
			{
				"impressCardId":"",
				"chatTotalCount":"",
				"chatTotalDuration":"",
				"chatLossCount":"",
				"assessLabelList":
				{
					{
						"impressLabel":"",
						"globalImpressLabelId":"",
						"assessedCount":"",
						"updateTime":"",
						"assessCount":""
					},
					{
						"impressLabel":"",
						"globalImpressLabelId":"",
						"assessedCount":"",
						"updateTime":"",
						"assessCount":""
					},
					{
						"impressLabel":"",
						"globalImpressLabelId":"",
						"assessedCount":"",
						"updateTime":"",
						"assessCount":""
					}					
				}
				"impressLabelList":
				[
					{
						"impressLabel":"",
						"globalImpressLabelId":"",
						"assessedCount":"",
						"updateTime":"",
						"assessCount":""
					},
					{
						"impressLabel":"",
						"globalImpressLabelId":"",
						"assessedCount":"",
						"updateTime":"",
						"assessCount":""
					},
					{
						"impressLabel":"",
						"globalImpressLabelId":"",
						"assessedCount":"",
						"updateTime":"",
						"assessCount":""
					}
				]
			}
		}
	}
}
</code></pre>

###2. Server统计数据结构体
<pre><code>
{
	"body":
	{
		"deviceCount":
		{
			"online":"",
			"random":"",
			"interest":"",
			"chat":"",
			"randomChat":"",
			"interestChat":""
		},
		"interestLabelList":
		{
			"current":"10",
			"history":
			{
				"startTime":"",
				"endTime":""
			}
		}
	}
}
</code></pre>

#数据操作
App与Server通过消息交互完成的数据操作
##1. 查询 dataQuery
##2. 更新 dataUpdate

#数据格式

##1. 成功:1/失败:0
##2. 是:1/否:0
##3. 业务会话请求目标 Random:1, Interest:2
##4. App业务会话请求动作 EnterPool:1, LeavePool:2, AgreeChat:3, RejectChat:4, EndChat:5, AssessAndContinue:6, AssessAndQuit:7
##5. Server业务会话通知动作 SessionBinded:1, OthersideRejected:2, OthersideAgreed:3, OthersideLost
##6. Profile服务状态 正常:1/禁聊:0
##7. 日期时间格式 2013-09-11 16:05:38

#消息列表
以下消息均略去header部分
###100. 测试请求 AlohaRequest
<pre><code>
{
    "body":
    {
    	"content":"Hello Server!"
    }
}
</code></pre>

###402. 测试响应 AlohaResponse
<pre><code>
{
    "body":
    {
    	"content":"Hello App!"
    }
}
</code></pre>

###101. App数据同步请求 AppDataSyncRequest
<pre><code>
{
	"body":
	{
    	"dataQuery":
    	{
			"device":
			{
				"deviceId":"",
				"deviceSn":"",
				"deviceCard":
				{
				},
				"profile":
				{
					"profileId":"",
					"serviceStatus":"",
					"unbanDate":"",
					"lastActivityTime":"",
					"createTime":"",
					"active":"",
					"interestCard":
					{
						"interestCardId":"",
						"interestLabelList":"9"
					},
					"impressCard":
					{
						"impressCardId":"",
						"chatTotalCount":"",
						"chatTotalDuration":"",
						"chatLossCount":"",
						"assessLabelList":"",
						"impressLabelList":"7"
					}
				}
			}
		},
		"dataUpdate":
		{	
			"device":
			{
				"deviceId":"",
				"deviceSn":"",
				"deviceCard":
				{
					"deviceCardId":"",
					"deviceId":"",
					"registerTime":"",
					"deviceModel":"",
					"osVersion":"",
					"appVersion":"",
					"location":"",
					"isJailed":""
				},
				"profile":
				{
					"profileId":"",
					"serviceStatus":"",
					"unbanDate":"",
					"lastActivityTime":"",
					"createTime":"",
					"active":"",
					"interestCard":
					{
						"interestCardId":"",
						"interestLabelList":
						[
							{
								"globalInterestLabelId":"",
								"interestLabel":"",
								"globalMatchCount":"",
								"order":"",
								"matchCount":"",
								"validFlag":""
							},
							{
								"globalInterestLabelId":"",
								"interestLabel":"",
								"globalMatchCount":"",
								"order":"",
								"matchCount":"",
								"validFlag":""
							},
							{
								"globalInterestLabelId":"",
								"interestLabel":"",
								"globalMatchCount":"",
								"order":"",
								"matchCount":"",
								"validFlag":""
							}
						]
					},
					"impressCard":
					{
						"impressCardId":"",
						"chatTotalCount":"",
						"chatTotalDuration":"",
						"chatLossCount":"",
						"assessLabelList":"",
						"impressLabelList":
						[
							{
								"impressLabel":"",
								"globalImpressLabelId":"",
								"assessedCount":"",
								"updateTime":"",
								"assessCount":""
							},
							{
								"impressLabel":"",
								"globalImpressLabelId":"",
								"assessedCount":"",
								"updateTime":"",
								"assessCount":""
							},
							{
								"impressLabel":"",
								"globalImpressLabelId":"",
								"assessedCount":"",
								"updateTime":"",
								"assessCount":""
							}
						]
					}
				}
			}
		}
    }
}
</code></pre>

###403. App数据同步响应 AppDataSyncResponse
<pre><code>
{
    "body": // 将App发来的update部分在内存数据中完成后，与query部分一并发回给App
    {
    	"dataQuery":
    	{
    	}
    	"dataUpdate":
    	{
    	}
    }
}
</code></pre>

###102. Server数据同步请求 ServerDataSyncRequest
<pre><code>
{
	"body":
	{

	}
}
</code></pre>

###404. Server数据同步响应 ServerDataSyncResponse
{
	"body":
	{
	}
}

###103. 业务会话请求 BusinessSessionRequest
<pre><code>
{
	"body": 
	{
		"businessSessionId":"",
		"businessType":"Interest",
		"operationType":"EnterPool",
		"operationInfo":"",
		"operationValue":"" 
	}	
}
</code></pre>


###405. 业务会话响应 BusinessSessionResponse
<pre><code>
{
	"body": 
	{
		"businessSessionId":"9861ASFDE",
		"businessType":"Interest",
		"operationType":"EnterPool",
		"operationInfo":"",		
		"operationValue":"1" 
	}	
}
</code></pre>

###300. 业务会话通知 BusinessSessionNotification
<pre><code>
{
	"body": 
	{
		"businessSessionId":"9861ASFDE"，
		"businessType":"Interest",
		"operationType":"SessionBinded"
		"operationInfo":
		{
			// 这里存放匹配对象的信息
		},
		"operationValue":""
	}
}
</code></pre>

###202. 业务会话通知响应 BusinessSessionNotificationResponse
<pre><code>
{
	"body": 
	{
		"businessSessionId":"9861ASFDE",
		"businessType":"Interest",
		"operationType":"SessionBinded",
		"operationInfo":"",
		"operationValue":"1" 
	}
}
</code></pre>

###201. App错误响应 AppErrorResposne
<pre><code>
{
    "body":
    {
		"receivedMessage":
		{
			// 此处存放收到的信息原文
		},
		"errorCode":"-7",
      	"errorDescription":"Empty Content"
    }
}
</code></pre>

###401. Server错误响应 ServerErrorResponse
<pre><code>
{
    "body":
    {
		"receivedMessage":
		{
			// 此处存放收到的信息原文		
		},
		"errorCode":"-7",
      	"errorDescription":"Empty Content"
    }
}
</code></pre>


###301. Server广播通知 BroadcastNotification
<pre><code>
{
	"body": 
	{
		"content":"This is a broadcast message!"
	}
}
</code></pre>
