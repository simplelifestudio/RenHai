//
//  RHJSONMessage.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#define MESSAGE_SECURITY_KEY @"19890604"

#define MESSAGE_MESSAGESN_LENGTH 16

#define MESSAGE_KEY_ENVELOPE @"jsonEnvelope"

#define MESSAGE_KEY_HEADER @"header"
#define MESSAGE_KEY_BODY @"body"

#define MESSAGE_KEY_MESSAGETYPE @"messageType"
#define MESSAGE_KEY_MESSAGEID @"messageId"
#define MESSAGE_KEY_MESSAGESN @"messageSn"
#define MESSAGE_KEY_TIMESTAMP @"timeStamp"

#define MESSAGE_KEY_DEVICEID @"deviceId"
#define MESSAGE_KEY_DEVICESN @"deviceSn"

#define MESSAGE_KEY_CONTENT @"content"
#define MESSAGE_KEY_DEVICECARD @"deviceCard"
#define MESSAGE_KEY_INTERESTCARD @"interestCard"
#define MESSAGE_KEY_IMPRESSCARD @"impressCard"

#define MESSAGE_KEY_CONTENT @"content"

typedef enum
{
    MessageType_Unkown = 0,
    MessageType_AppRequest = 1,
    MessageType_AppResponse = 2,
    MessageType_ServerNotification = 3,
    MessageType_ServerResponse = 4
}
RHMessageType;

typedef enum
{
    // MessageType_Unkown
    MessageId_Unknown = 0,
    // MessageType_AppRequest
    MessageId_AlohaRequest = 100,
    MessageId_AppDataSyncRequest = 101,
    MessageId_ServerDataSyncRequest = 102,
    MessageId_BusinessSessionRequest = 103,
    // MessageType_ServerResponse
    MessageId_AppTimeoutResponse = 200,
    MessageId_AppErrorResponse = 201,
    MessageId_BusinessSessionNotificationResponse = 202,
    // MessageType_ServerNotification
    MessageId_BusinessSessionNotification = 300,
    MessageId_BroadcastNotification = 301,
    // MessageType_AppResponse
    MessageId_ServerTimeoutResponse = 400,
    MessageId_ServerErrorResponse = 401,
    MessageId_AlohaResponse = 402,
    MessageId_AppDataSyncResponse = 403,
    MessageId_ServerDataSyncResponse = 404,
    MessageId_BusinessSessionResponse = 405,
}
RHMessageId;

typedef enum
{
    ErrorCode_ServerLegalResponse,
    ErrorCode_ServerNullResponse,
    ErrorCode_ServerIllegalResponse, // 消息的语法（基本格式）错误
    ErrorCode_ServerErrorResponse, // 消息的语意（业务逻辑）错误
    ErrorCode_ServerTimeout
}
RHMessageErrorCode;

typedef enum
{
    BusinessSessionPool_Random = 1,
    BusinessSessionPool_Interest = 2
}
RHBusinessSessionPool;

typedef enum
{
    AppBusinessSessionAction_EnterPool = 1,
    AppBusinessSessionAction_LeavePool,
    AppBusinessSessionAction_AgreeChat,
    AppBusinessSessionAction_RejectChat,
    AppBusinessSessionAction_EndChat,
    AppBusinessSessionAction_AssessAndContinue,
    AppBusinessSessionAction_AssessAndQuit
}
RHAppBusinessSessionAction;

typedef enum
{
    ServerBusinessSessionAction_SessionBinded = 1,
    ServerBusinessSessionAction_OthersideRejected,
    ServerBusinessSessionAction_OthersideAgreed,
    ServerBusinessSessionAction_OthersideLost,
    ServerBusinessSessionAction_OthersideEndChat = 5 // 临时使用
}
RHServerBusinessSessionAction;

typedef enum
{
    ProfileStatus_Banned = 0,
    ProfileStatus_Normal = 1
}
RHProfileStatus;

@interface RHJSONMessage : NSObject <CBJSONable>

// MessageNeedEncrypt flag should be same with server side
+(void) setMessageNeedEncrypt:(BOOL) encrypt;
+(BOOL) isMessageNeedEncrypt;

+(RHJSONMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body;
+(RHJSONMessage*) constructWithContent:(NSDictionary*) content;
+(RHJSONMessage*) constructWithString:(NSString*) jsonString;
+(RHMessageErrorCode) verify:(RHJSONMessage*) message;

+(NSDictionary*) constructMessageHeader:(RHMessageType) messageType messageId:(RHMessageId) messageId messageSn:(NSString*) messageSn deviceId:(NSInteger) deviceId deviceSn:(NSString*) deviceSn timeStamp:(NSDate*) timeStamp;
+(RHJSONMessage*) newAlohaRequestMessage;
+(RHJSONMessage*) newServerTimeoutResponseMessage;
+(BOOL) isServerTimeoutResponseMessage:(RHJSONMessage*) message;
+(BOOL) isServerErrorResponseMessage:(RHJSONMessage*) message;

@property (nonatomic) BOOL enveloped;
@property (nonatomic, strong, readonly) NSDictionary* header;
@property (nonatomic, strong, readonly) NSDictionary* body;

-(RHMessageType) messageType;
-(RHMessageId) messageId;
-(NSString*) messageSn;
-(NSInteger) deviceId;
-(NSString*) deviceSn;
-(NSDate*) timeStamp;

@end
