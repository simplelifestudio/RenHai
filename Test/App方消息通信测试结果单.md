#<center>App方消息通信测试结果单</center>

##1. AlohaRequest
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 20:25:13.472",
            "messageType": 1,
            "messageId": 100,
            "messageSn": "C2B38679BPIV4M93",
            "deviceId": 0
        },
        "body": {
            "content": "Hello Server!"
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "messageSn": "C2B38679BPIV4M93",
            "timeStamp": "2013-09-28 20:25:13.614",
            "messageType": 4,
            "messageId": 402,
            "deviceSn": null,
            "deviceId": null
        },
        "body": {
            "content": "Hello App!"
        }
    }
}
</code></pre>
####测试结果
符合预期，最好可以将deviceSn和deviceId设定为一致

##2. AppDataSyncRequest
###1. TotalSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 20:40:22.884",
            "messageType": 1,
            "messageId": 101,
            "messageSn": "40I60C6DVRPW7137",
            "deviceId": 0
        },
        "body": {
            "dataUpdate": {
                "device": {
                    "deviceCard": {
                        "isJailed": false,
                        "appVersion": "0.1",
                        "deviceModel": "Simulator",
                        "osVersion": "6.1"
                    },
                    "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A"
                }
            },
            "dataQuery": {
                "deviceCard": null,
                "profile": null,
                "deviceSn": null,
                "deviceId": null
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "messageSn": "40I60C6DVRPW7137",
            "timeStamp": "2013-09-28 20:40:23.028",
            "messageType": 4,
            "messageId": 401,
            "deviceSn": null,
            "deviceId": null
        },
        "body": {
            "errorDescription": "isJailed must be 0 or 1.",
            "receivedMessage": "AppDataSyncRequest",
            "errorCode": "1103"
        }
    }
}
</code></pre>
####测试结果：
#####1. 讨论isJailed字段值为0/1还是true/false
#####2. receivedMessage字段的值应该是整个消息（原文）
#####3. ServerErrorResponse的deviceSn和deviceId最好也能补上
###2. DeviceCardSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 20:51:51.591",
            "messageType": 1,
            "messageId": 101,
            "messageSn": "C5X6POX3LPTE508U",
            "deviceId": 0
        },
        "body": {
            "dataUpdate": {
                "device": {
                    "deviceCard": {
                        "isJailed": false,
                        "appVersion": "0.1",
                        "deviceModel": "Simulator",
                        "osVersion": "6.1"
                    }
                }
            },
            "dataQuery": {
                "deviceId": null,
                "deviceSn": null,
                "deviceCard": null
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "messageSn": "C5X6POX3LPTE508U",
            "timeStamp": "2013-09-28 20:51:51.732",
            "messageType": 4,
            "messageId": 401,
            "deviceSn": null,
            "deviceId": null
        },
        "body": {
            "errorDescription": "labelOrder must be provided for device.",
            "receivedMessage": "AppDataSyncRequest",
            "errorCode": "1103"
        }
    }
}
</code></pre>
####测试结果：
#####1. DeviceCardSync应该和InterestCard的labelOrder无关，Server需要修改并检查可能存在的类似情况
###3. ImpressCardSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 21:00:35.520",
            "messageType": 1,
            "messageId": 101,
            "messageSn": "HS2OJYQ2QI8300YN",
            "deviceId": 0
        },
        "body": {
            "dataQuery": {
                "device": {
                    "profile": {
                        "impressCard": null
                    }
                }
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": null,
            "timeStamp": "2013-09-28 21:00:44.521",
            "messageType": 4,
            "messageId": 400,
            "messageSn": "HS2OJYQ2QI8300YN",
            "deviceId": 0
        },
        "body": {}
    }
}
</code></pre>
####测试结果：
无法收到响应消息，通信超时，Server需要检查
###4. InterestCardSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 21:03:09.741",
            "messageType": 1,
            "messageId": 101,
            "messageSn": "XEBQM043A1X215CM",
            "deviceId": 0
        },
        "body": {
            "dataUpdate": {
                "device": {
                    "profile": {
                        "interestCard": null
                    }
                }
            },
            "dataQuery": {
                "device": {
                    "profile": {
                        "interestCard": null
                    }
                }
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "messageSn": "XEBQM043A1X215CM",
            "timeStamp": "2013-09-28 21:03:09.882",
            "messageType": 4,
            "messageId": 401,
            "deviceSn": null,
            "deviceId": null
        },
        "body": {
            "errorDescription": "labelOrder must be provided for device.",
            "receivedMessage": "AppDataSyncRequest",
            "errorCode": "1103"
        }
    }
}
</code></pre>
####测试结果：
不正确，App和Server需要讨论

##3. ServerDataSyncRequest
###1. TotalSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 21:14:06.045",
            "messageType": 1,
            "messageId": 102,
            "messageSn": "6DK5N865IVMXI665",
            "deviceId": 0
        },
        "body": {
            "deviceCapacity": {
                "online": null,
                "interest": null,
                "random": null
            },
            "deviceCount": {
                "chat": null,
                "interest": null,
                "randomChat": null,
                "online": null,
                "interestChat": null,
                "random": null
            },
            "interestLabelList": {
                "history": {
                    "startTime": null,
                    "endTime": null
                },
                "current": null
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": null,
            "timeStamp": "2013-09-28 21:16:01.852",
            "messageType": 4,
            "messageId": 400,
            "messageSn": "6DK5N865IVMXI665",
            "deviceId": 0
        },
        "body": {}
    }
}
</code></pre>
####测试结果：
无法收到响应消息，通信超时，Server需要检查
###2. DeviceCountSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 21:17:23.750",
            "messageType": 1,
            "messageId": 102,
            "messageSn": "Q3J5J4JQCPS4DM8O",
            "deviceId": 0
        },
        "body": {
            "deviceCount": {
                "chat": null,
                "interest": null,
                "randomChat": null,
                "online": null,
                "interestChat": null,
                "random": null
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": null,
            "timeStamp": "2013-09-28 21:17:32.751",
            "messageType": 4,
            "messageId": 400,
            "messageSn": "Q3J5J4JQCPS4DM8O",
            "deviceId": 0
        },
        "body": {}
    }
}
</code></pre>
####测试结果：
无法收到响应消息，通信超时，Server需要检查
###3. DeviceCapacitySync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 21:18:47.236",
            "messageType": 1,
            "messageId": 102,
            "messageSn": "D3KXIE0591M85VE8",
            "deviceId": 0
        },
        "body": {
            "deviceCapacity": {
                "online": null,
                "interest": null,
                "random": null
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": null,
            "timeStamp": "2013-09-28 21:18:56.236",
            "messageType": 4,
            "messageId": 400,
            "messageSn": "D3KXIE0591M85VE8",
            "deviceId": 0
        },
        "body": {}
    }
}
</code></pre>
####测试结果：
无法收到响应消息，通信超时，Server需要检查
###4. InterestLabelListSync
####发出消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": "794AF3A8-FFB0-4997-B9DE-CD4CFB68252A",
            "timeStamp": "2013-09-28 21:20:05.715",
            "messageType": 1,
            "messageId": 102,
            "messageSn": "7MB269WE70026R7Q",
            "deviceId": 0
        },
        "body": {
            "interestLabelList": {
                "history": {
                    "startTime": "2013-09-28",
                    "endTime": "2013-10-05"
                },
                "current": 10
            }
        }
    }
}
</code></pre>
####收到消息：
<pre><code>
{
    "jsonEnvelope": {
        "header": {
            "deviceSn": null,
            "timeStamp": "2013-09-28 21:20:14.715",
            "messageType": 4,
            "messageId": 400,
            "messageSn": "7MB269WE70026R7Q",
            "deviceId": 0
        },
        "body": {}
    }
}
</code></pre>
####测试结果：
无法收到响应消息，通信超时，Server需要检查

##4. BusinessSessionRequest
###1.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：
###2.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：
###3.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：
###4.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：
###5.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：
###6.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：
###7.
####发出消息：
<pre><code>
</code></pre>
####收到消息：
<pre><code>
</code></pre>
####测试结果：