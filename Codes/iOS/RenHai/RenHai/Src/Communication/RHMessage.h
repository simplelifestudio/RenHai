//
//  RHJSONMessage.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHDevice.h"

#define MESSAGE_SECURITY_KEY @"19890604"

#define MESSAGE_MESSAGESN_LENGTH 16

/*
 Device Data Struct
 */

// Envelope
#define MESSAGE_KEY_ENVELOPE @"jsonEnvelope"

// Message
#define MESSAGE_KEY_HEADER @"header"
#define MESSAGE_KEY_BODY @"body"
#define MESSAGE_KEY_CONTENT @"content"
#define MESSAGE_KEY_DATAQUERY @"dataQuery"
#define MESSAGE_KEY_DATAUPDATE @"dataUpdate"

// Header
#define MESSAGE_KEY_MESSAGETYPE @"messageType"
#define MESSAGE_KEY_MESSAGEID @"messageId"
#define MESSAGE_KEY_MESSAGESN @"messageSn"
#define MESSAGE_KEY_TIMESTAMP @"timeStamp"

// Device
#define MESSAGE_KEY_DEVICE @"device"
#define MESSAGE_KEY_DEVICEID @"deviceId"
#define MESSAGE_KEY_DEVICESN @"deviceSn"

// DeviceCard
#define MESSAGE_KEY_DEVICECARD @"deviceCard"
#define MESSAGE_KEY_DEVICECARDID @"deviceCardId"
#define MESSAGE_KEY_REGISTERTIME @"registerTime"
#define MESSAGE_KEY_DEVICEMODEL @"deviceModel"
#define MESSAGE_KEY_OSVERSION @"osVersion"
#define MESSAGE_KEY_APPVERSION @"appVersion"
#define MESSAGE_KEY_ISJAILED @"isJailed"

// Profile
#define MESSAGE_KEY_PROFILE @"profile"
#define MESSAGE_KEY_PROFILEID @"profileId"
#define MESSAGE_KEY_SERVICESTATUS @"serviceStatus"
#define MESSAGE_KEY_UNBANDATE @"unbanDate"
#define MESSAGE_KEY_ACTIVE @"active"
#define MESSAGE_KEY_CREATETIME @"createTime"
#define MESSAGE_KEY_IMPRESSCARD @"impressCard"
#define MESSAGE_KEY_INTERESTCARD @"interestCard"

// ImpressCard
#define MESSAGE_KEY_IMPRESSCARDID @"impressCardId"
#define MESSAGE_KEY_ASSESSLABELLIST @"assessLabelList"
#define MESSAGE_KEY_IMPRESSLABELLIST @"impressLabelList"
#define MESSAGE_KEY_CHATTOTALCOUNT @"chatTotalCount"
#define MESSAGE_KEY_CHATTOTALDURATION @"chatTotalDuration"
#define MESSAGE_KEY_CHATLOSSCOUNT @"chatLossCount"

// InterestCard
#define MESSAGE_KEY_INTERESTCARDID @"interestCardId"
#define MESSAGE_KEY_INTERESTLABELLIST @"interestLabelList"

// ImpressLabel
#define MESSAGE_KEY_IMPRESSLABELID @"globalImpressLabelId"
#define MESSAGE_KEY_ASSESSEDCOUNT @"assessedCount"
#define MESSAGE_KEY_UPDATETIME @"updateTime"
#define MESSAGE_KEY_ASSESSCOUNT @"assessCount"
#define MESSAGE_KEY_IMPRESSLABELNAME @"impressLabelName"

// InterestLabel
#define MESSAGE_KEY_INTERESTLABELID @"globalInterestLabelId"
#define MESSAGE_KEY_INTERESTLABELNAME @"interestLabelName"
#define MESSAGE_KEY_GLOBALMATCHCOUNT @"globalMatchCount"
#define MESSAGE_KEY_INTERESTLABELORDER @"labelOrder"
#define MESSAGE_KEY_MATCHCOUNT @"matchCount"
#define MESSAGE_KEY_VALIDFLAG @"validFlag"

/*
 Server Data Struct
 */

// DeviceCount
#define MESSAGE_KEY_DEVICECOUNT @"deviceCount"
#define MESSAGE_KEY_ONLINE @"online"
#define MESSAGE_KEY_RANDOM @"random"
#define MESSAGE_KEY_INTEREST @"interest"
#define MESSAGE_KEY_CHAT @"chat"
#define MESSAGE_KEY_RANDOMCHAT @"randomChat"
#define MESSAGE_KEY_INTERESTCHAT @"interestChat"

// DeviceCapacity
#define MESSAGE_KEY_DEVICECAPACITY @"deviceCapacity"

// InterestLabelList
#define MESSAGE_KEY_CURRENT @"current"
#define MESSAGE_KEY_HISTORY @"history"
#define MESSAGE_KEY_STARTTIME @"startTime"
#define MESSAGE_KEY_ENDTIME @"endTime"

/*
 Business Session Data Struct
 */
#define MESSAGE_KEY_BUSINESSSESSIONID @"businessSessionId"
#define MESSAGE_KEY_BUSINESSTYPE @"businessType"
#define MESSAGE_KEY_OPERATIONTYPE @"operationType"
#define MESSAGE_KEY_OPERATIONINFO @"operationInfo"
#define MESSAGE_KEY_OPERATIONVALUE @"operationValue"

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
    ErrorCode_ServerLegalMessage, // 头部合法的消息
    ErrorCode_ServerNullMessage,  // 空消息
    ErrorCode_ServerIllegalMessage, // 消息的语法（基本格式）错误
    ErrorCode_ServerErrorMessage, // 消息的语意（业务逻辑）错误
    ErrorCode_ServerTimeoutMessage, // 超时消息，App端模拟产生
}
RHMessageErrorCode;

typedef enum
{
    BusinessType_Random = 1,
    BusinessType_Interest = 2
}
RHBusinessType;

typedef enum
{
    BusinessSessionRequestType_EnterPool = 1,
    BusinessSessionRequestType__LeavePool,
    BusinessSessionRequestType__AgreeChat,
    BusinessSessionRequestType_RejectChat,
    BusinessSessionRequestType_EndChat,
    BusinessSessionRequestType_AssessAndContinue,
    BusinessSessionRequestType_AssessAndQuit
}
BusinessSessionRequestType;

typedef enum
{
    BusinessSessionNotificationType_SessionBinded = 1,
    BusinessSessionNotificationType_OthersideRejected,
    BusinessSessionNotificationType_OthersideAgreed,
    BusinessSessionNotificationType_OthersideLost,
    BusinessSessionNotificationType_OthersideEndChat = 5 // 临时使用
}
BusinessSessionNotificationType;

typedef enum
{
    BusinessSessionOperationValue_Failed = 0,
    BusinessSessionOperationValue_Success = 1
}
BusinessSessionOperationValue;

typedef enum
{
    AppDataSyncRequestType_TotalSync = 0,
    AppDataSyncRequestType_DeviceCardSync,
    AppDataSyncRequestType_ImpressCardSync,
    AppDataSyncRequestType_InterestCardSync
}
AppDataSyncRequestType;

typedef enum
{
    ServerDataSyncRequestType_TotalSync = 0,
    ServerDataSyncRequestType_DeviceCountSync,
    ServerDataSyncRequestType_DeviceCapacitySync,
    ServerDataSyncRequestType_InterestLabelListSync
}
ServerDataSyncRequestType;

@interface RHMessage : NSObject <CBJSONable>

// MessageNeedEncrypt flag should be same with server side
+(void) setMessageNeedEncrypt:(BOOL) encrypt;
+(BOOL) isMessageNeedEncrypt;

+(NSString*) generateMessageSn;

+(RHMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body enveloped:(BOOL) enveloped;
+(RHMessage*) constructWithContent:(NSDictionary*) content enveloped:(BOOL) enveloped;
+(RHMessage*) constructWithString:(NSString*) jsonString enveloped:(BOOL) enveloped;;

//+(RHMessageErrorCode) verify:(RHMessage*) message;
+(BOOL) isLegalMessage:(RHMessage*) message;

+(NSDictionary*) constructMessageHeader:(RHMessageType) messageType messageId:(RHMessageId) messageId messageSn:(NSString*) messageSn deviceId:(NSInteger) deviceId deviceSn:(NSString*) deviceSn;

+(RHMessage*) newAlohaRequestMessage:(RHDevice*) device;
+(RHMessage*) newServerTimeoutResponseMessage:(NSString*) messageSn;

+(RHMessage*) newAppDataSyncRequestMessage:(AppDataSyncRequestType) type device:(RHDevice*) device info:(NSDictionary*) info;

+(RHMessage*) newServerDataSyncRequestMessage:(ServerDataSyncRequestType) type device:(RHDevice*) device info:(NSDictionary*) info;

+(RHMessage*) newBusinessSessionRequestMessage:(NSString*) businessSessionId businessType:(RHBusinessType) businessType operationType:(BusinessSessionRequestType) operationType device:(RHDevice*) device info:(NSDictionary*) info;
+(RHMessage*) newBusinessSessionNotificationResponseMessage:(NSString*) businessSessionId businessType:(RHBusinessType) businessType operationType:(BusinessSessionNotificationType) operationType operationValue:(BusinessSessionOperationValue) operationValue device:(RHDevice*) device info:(NSDictionary*) info;

+(BOOL) isServerTimeoutResponseMessage:(RHMessage*) message;
+(BOOL) isServerErrorResponseMessage:(RHMessage*) message;

@property (nonatomic) BOOL enveloped;
@property (nonatomic, strong, readonly) NSDictionary* header;
@property (nonatomic, strong, readonly) NSDictionary* body;

-(RHMessageType) messageType;
-(RHMessageId) messageId;
-(NSString*) messageSn;
-(NSInteger) deviceId;
-(NSString*) deviceSn;
-(NSDate*) timeStamp;
-(void) setTimeStamp:(NSDate*) time;

@end
