//
//  RHJSONMessage.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

//// Message Type
//#define JSONMESSAGE_TYPE_APPREQUEST @"AppRequest"
//#define JSONMESSAGE_TYPE_SERVERRESPONSE @"ServerResponse"
//#define JSONMESSAGE_TYPE_SERVERNOTIFICATION @"ServerNotification"
//#define JSONMESSAGE_TYPE_APPRESPONSE @"AppResponse"
//#define JSONMESSAGE_TYPE_UNKNOWN @"Unknown"

// App->Server->App
#define JSONMESSAGE_SERVERERRORRESPONSE @"ServerErrorResponse"
#define JSONMESSAGE_SERVERTIMEOUTRESPONSE @"ServerTimeoutResponse"

#define JSONMESSAGE_ALOHAREQUEST @"AlohaRequest"
#define JSONMESSAGE_ALOHARESPONSE @"AlohaResponse"
#define JSONMESSAGE_APPDATASYNCREQUEST @"AppDataSyncRequest"
#define JSONMESSAGE_APPDATASYNCRESPONSE @"AppDataSyncResponse"
#define JSONMESSAGE_SERVERDATASYNCREQUEST @"ServerDataSyncRequest"
#define JSONMESSAGE_SERVERDATASYNCRESPONSE @"ServerDataSyncResponse"
#define JSONMESSAGE_BUSINESSSESSIONREQUEST @"BusinessSessionRequest"
#define JSONMESSAGE_BUSINESSSESSIONRESPONSE @"BusinessSessionResponse"

// Server->App->Server
#define JSONMESSAGE_BUSINESSSESSIONNOTIFICATION @"BusinessSessionNotification"
#define JSONMESSAGE_BUSINESSSESSIONNOTIFICATIONRESPONSE @"BusinessSessionNotificationResponse"
#define JSONMESSAGE_BROADCASTNOTIFICATION @"BroadcastNotification"
#define JSONMESSAGE_BROADCASTNOTIFICATIONRESPONSE @"BroadcastNotificationResponse"

#define JSONMESSAGE_KEY_HEADER @"header"
#define JSONMESSAGE_KEY_BODY @"body"

#define JSONMESSAGE_KEY_MESSAGETYPE @"messageType"
#define JSONMESSAGE_KEY_MESSAGEID @"messageId"
#define JSONMESSAGE_KEY_MESSAGESN @"messageSn"
#define JSONMESSAGE_KEY_TIMESTAMP @"timeStamp"

#define JSONMESSAGE_KEY_DEVICEID @"deviceId"
#define JSONMESSAGE_KEY_DEVICESN @"deviceSn"

#define JSONMESSAGE_KEY_CONTENT @"content"
#define JSONMESSAGE_KEY_DEVICECARD @"deviceCard"
#define JSONMESSAGE_KEY_INTERESTCARD @"interestCard"
#define JSONMESSAGE_KEY_IMPRESSCARD @"impressCard"

typedef enum
{
    UnknownMessage = 0,
    AppRequestMessage = 1,
    ServerResponseMessage = 2,
    ServerNotificationMessage = 3,
    AppResponseMessage = 4
}
RHJSONMessageType;

typedef enum
{
    ServerLegalResponse,
    ServerNullResponse,
    ServerIllegalResponse, // 消息的语法（基本格式）错误
    ServerErrorResponse, // 消息的语意（业务逻辑）错误
    ServerTimeout
}
RHJSONMessageErrorCode;

@interface RHJSONMessage : NSObject <CBJSONable>

+(RHJSONMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body;
+(RHJSONMessage*) constructWithContent:(NSDictionary*) content;
+(RHJSONMessage*) constructWithString:(NSString*) jsonString;
+(RHJSONMessageErrorCode) verify:(RHJSONMessage*) message;

+(RHJSONMessage*) newServerTimeoutResponseMessage;
+(BOOL) isServerTimeoutResponseMessage:(RHJSONMessage*) message;
+(BOOL) isServerErrorResponseMessage:(RHJSONMessage*) message;

@property (nonatomic, strong) NSDictionary* header;
@property (nonatomic, strong) NSDictionary* body;

-(NSString*) messageSn;
-(NSString*) messageId;

-(RHJSONMessageType) messageType;

@end
