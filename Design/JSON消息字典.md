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

#消息格式 MessageFormat
以"jsonEnvelop"作为JSON消息的信封外套，对应的值即实际的业务消息（已编码/已加密/已压缩）
<pre><code>
{
	"jsonEnvelope": // 信封外套
	{
		"header":
		{
			"messageType":"0",
			"messageSn":"AFLNWERJL3203598FDLGSLDF",
			"messageId":"102",
			"deviceId":"1234",
			"deviceSn":"ABCD77631GGWQ",
			"timeStamp":"2013-08-14 21:18:49" // App启动后首次收到Server发来的消息时需要计算两边时间差并保存留用
    	},
    	"body": 
    	{		
			"param1":"value1",
			"param2":"value2",
			"paramn":"valuen"
    	}
	}
}
</code></pre>

#消息列表
###201. App错误响应 AppErrorResposne
<pre><code>
{
	"header":
	{
		"messageType":"4",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"100",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
    },
    "body":
    {
		"receivedMessage":"BusinessSessionNotification",
		"errorCode":"-7",
      	"errorDescription":"Empty Content"
    }
}
</code></pre>

###401. Server错误响应 ServerErrorResponse
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"101",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
    },
    "body":
    {
		"receivedMessage":"AlohaRequest",
		"errorCode":"-7",
      	"errorDescription":"Empty Content"
    }
}
</code></pre>

###100. 测试请求 AlohaRequest
<pre><code>
{
	"header":
	{
		"messageType":"1",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"102",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
    },
    "body":
    {
    	"content":"Hello Server!"
    }
}
</code></pre>

###402. 测试响应 AlohaResponse
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"103",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
    },
    "body":
    {
    	"content":"Hello App!"
    }
}
</code></pre>

###101. App数据同步请求 AppDataSyncRequest
<pre><code>
{
	"header":
	{
		"messageType":"1",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"104",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	},
	"body":
	{
    	"dataQuery":
    	{
    		"deviceCard":
    		{
    		},
    		"impressCard":
    		{
    			"labelListCount":"10" // 最小值为3
			},
			"interestCard":
			{
				"labelListCount":"5"
			}		
		},
		"dataUpdate":
		{	
			"deviceCard":
			{
				"osVersion":"iOS 6.1.2",
				"appVersion":"1.2",
				"isJailed":"No",
				"location":"22.511962,113.380301"
			},
			"interestCard":
			{
				"soccer":
				{
					"order":"0",
				},
				"music":
				{
					"order":"1",
				}
			}
		}
    }
}
</code></pre>

###403. App数据同步响应 AppDataSyncResponse
<pre><code>
{
	"header":
	{
		"messageType":"2",    
		"messageSn":"AFLNWERJL3203598FDLGSLDF",		"messageId":"105",    
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
    },
    "body": // 将App发来的update部分在内存数据中完成后，与query部分一并发回给App
    {
		"deviceCard":
		{
			"serviceStatus":"Normal", 
			"registerTime":"2013-08-07 14:01:39",
			"forbiddenExpiredDate":"",
			"profileId""="4587"
		},
		"impressCard":
		{
			"impressCardId":"9631",
			"chatTotalCount":"47",
			"totalChatDuration":"1288",
			"chatLossCount":"9",
			"labelList":
			{
				{"impressId":"4421", "impressName":"绅士", "count":"7", "assessCount":"3"},
				{"impressId":"9976", "impressName":"风趣", "count":"3", "assessCount":"2"}
			}
		},
		"interestCard":
		{
			"soccer":
			{
				"order":"0",
				"matchCount":"7"
			},
			"music":
			{
				"order":"1",
				"matchCount":"3"
			}
		}	
    }
}
</code></pre>

###102. Server数据同步请求 ServerDataSyncRequest
<pre><code>
{
	"header":
	{
		"messageType":"1",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"106",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body":
	{
		"onlineDeviceCount":"",
		"interestDeviceCount":"",
		"chatDeviceCount":"",
		"interestChatDeviceCount":"",
		"currentHotInterestLabels":"", // 这里需要指定要多少个标签，这一层的结构就破坏了
		"historyHotInterestLabels":""
	}
}
</code></pre>

###404. Server数据同步响应 ServerDataSyncResponse
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"107",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body":
	{
		"onlineDeviceCount":"3601"
	}
}

###300. 业务会话通知 BusinessSessionNotification
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"107",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body": 
	{
		"sessionId":"9861ASFDE"，
		"businessType":"Interest",
		"operationType":"SessionBinded" // 这里存放业务会话的操作
		"operationValue":
		{
			// 这里存放匹配对象的信息
		}
	}
}
</code></pre>

###202. 业务会话通知响应 BusinessSessionNotificationResponse
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"107",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body": 
	{
		"businessSessionId":"9861ASFDE",
		"businessType":"Interest",
		"operationType":"Received", // 这里存放App需要向Server告知的信息
		"operationValue":"" 
	}
}
</code></pre>

###103. 业务会话请求 BusinessSessionRequest
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"107",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body": 
	{
		"businessSessionId":"",
		"businessType":"Interest", // businessType取值范围：EnterPool, LeavePool, AgreeChat, RejectChat, EndChat, Assess, AssessAndQuit
		"operationType":"EnterPool", // 这里存放App需要向Server告知的信息
		"operationValue":"" 
	}	
}
</code></pre>


###405. 业务会话响应 BusinessSessionResponse
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"107",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body": 
	{
		"businessSessionId":"9861ASFDE",
		"businessType":"Interest",
		"operationType":"EnterPool", // 这里存放Server需要向App告知的信息
		"operationValue":"Success" 
	}	
}
</code></pre>

###301. Server广播通知 BroadcastNotification
<pre><code>
{
	"header":
	{
		"messageType":"2",
		"messageSn":"AFLNWERJL3203598FDLGSLDF",
		"messageId":"107",
		"deviceId":"1234",
		"deviceSn":"ABCD77631GGWQ",
		"timeStamp":"2013-08-14 21:18:49"
	}
	"body": 
	{
		"content":"This is a broadcast message!"
	}
}
</code></pre>
