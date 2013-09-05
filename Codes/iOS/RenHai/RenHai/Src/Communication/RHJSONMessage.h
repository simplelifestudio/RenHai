//
//  RHJSONMessage.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#define JSONMESSAGE_SECURITY_KEY @"19890604"

#define JSON_ENVELOPE @"jsonEnvelope"

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
    MessageType_Unkown = 0,
    MessageType_AppRequest = 1,
    MessageType_ServerResponse = 2,
    MessageType_ServerNotification = 3,
    MessageType_AppResponse = 4
}
RHJSONMessageType;

typedef enum
{
    MessageId_Unknown = 0,
    MessageId_AppTimeoutResponse = 98,
    MessageId_ServerTimeoutResponse = 99,
    MessageId_AppErrorResponse = 100,
    MessageId_ServerErrorResponse = 101,
    MessageId_AlohaRequest = 102,
    MessageId_AlohaResponse = 103,
    MessageId_AppDataSyncRequest = 104,
    MessageId_AppDataSyncResponse = 105,
    MessageId_ServerDataSyncRequest = 106,
    MessageId_ServerDataSyncResponse = 107,
    MessageId_BusinessSessionNotification = 108,
    MessageId_BusinessSessionNotificationResponse = 109,
    MessageId_BusinessSessionRequest = 110,
    MessageId_BusinessSessionResponse = 111,
    MessageId_BroadcastNotification = 112
}
RHJSONMessageId;

typedef enum
{
    ErrorCode_ServerLegalResponse,
    ErrorCode_ServerNullResponse,
    ErrorCode_ServerIllegalResponse, // 消息的语法（基本格式）错误
    ErrorCode_ServerErrorResponse, // 消息的语意（业务逻辑）错误
    ErrorCode_ServerTimeout
}
RHJSONMessageErrorCode;

@interface RHJSONMessage : NSObject <CBJSONable>

// MessageNeedEncrypt flag should be same with server side
+(void) setMessageNeedEncrypt:(BOOL) encrypt;
+(BOOL) isMessageNeedEncrypt;

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
-(RHJSONMessageId) messageId;

-(RHJSONMessageType) messageType;

@end
