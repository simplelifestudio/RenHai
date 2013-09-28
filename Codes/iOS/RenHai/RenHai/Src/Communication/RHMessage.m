//
//  RHJSONMessage.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHMessage.h"

#import "CBJSONUtils.h"
#import "CBStringUtils.h"
#import "CBDateUtils.h"
#import "CBSecurityUtils.h"

static BOOL s_messageEncrypted;

@interface RHMessage()
{
    
}

@property (nonatomic, strong, readwrite) NSDictionary* header;
@property (nonatomic, strong, readwrite) NSDictionary* body;

@end

@implementation RHMessage

+(void) setMessageNeedEncrypt:(BOOL) encrypt
{
    s_messageEncrypted = encrypt;
}

+(BOOL) isMessageNeedEncrypt
{
    return s_messageEncrypted;
}

+(NSString*) generateMessageSn
{
    return [CBStringUtils randomString:MESSAGE_MESSAGESN_LENGTH];
}

+(RHMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body enveloped:(BOOL) enveloped;
{
    RHMessage* message = [[RHMessage alloc] init];
    
    message.header = header;
    message.body = body;
    
    message.enveloped = enveloped;
    
    return message;
}

+(RHMessage*) constructWithContent:(NSDictionary*) content enveloped:(BOOL) enveloped;
{
    if (enveloped)
    {
        content = [content objectForKey:MESSAGE_KEY_ENVELOPE];
    }

    NSDictionary* header = [content objectForKey:MESSAGE_KEY_HEADER];
    NSDictionary* body = [content objectForKey:MESSAGE_KEY_BODY];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:header messageBody:body enveloped:enveloped];
    
    return message;
}

+(RHMessage*) constructWithString:(NSString*) jsonString enveloped:(BOOL)enveloped
{
    NSDictionary* content = [CBJSONUtils toJSONObject:jsonString];
    
    if (enveloped)
    {
        content = [content objectForKey:MESSAGE_KEY_ENVELOPE];
    }
    
    RHMessage* message = nil;
    
    if (nil != jsonString && 0 < jsonString.length)
    {
        if ([RHMessage isMessageNeedEncrypt])
        {
            jsonString = [CBSecurityUtils decryptByDESAndDecodeByBase64:jsonString key:MESSAGE_SECURITY_KEY];
        }
        
        NSDictionary* dic = [CBJSONUtils toJSONObject:jsonString];
        message = [RHMessage constructWithContent:dic enveloped:NO];
    }
    
    return message;
}

+(RHMessageErrorCode) verify:(RHMessage*) message
{
    RHMessageErrorCode error = ErrorCode_ServerLegalResponse;
    
    if (nil != message)
    {
        RHMessageId messageId = message.messageId;
        if (messageId == MessageId_ServerErrorResponse)
        {
            error = ErrorCode_ServerErrorResponse;
        }
        else if (messageId == MessageId_ServerTimeoutResponse)
        {
            error = ErrorCode_ServerTimeout;
        }
        else if (messageId == MessageId_Unknown)
        {
            error = ErrorCode_ServerIllegalResponse;
        }
    }
    else
    {
        error = ErrorCode_ServerNullResponse;
    }
    
    return error;
}

+(NSDictionary*) constructMessageHeader:(RHMessageType) messageType messageId:(RHMessageId) messageId messageSn:(NSString*) messageSn deviceId:(NSInteger) deviceId deviceSn:(NSString*) deviceSn
{
    NSMutableDictionary* header = [NSMutableDictionary dictionary];
    
    NSNumber* oMessageType = [NSNumber numberWithInt:messageType];
    [header setObject:oMessageType forKey:MESSAGE_KEY_MESSAGETYPE];
    
    NSNumber* oMessageId = [NSNumber numberWithInt:messageId];
    [header setObject:oMessageId forKey:MESSAGE_KEY_MESSAGEID];
    
    if (nil == messageSn)
    {
        [header setObject:[NSNull null] forKey:MESSAGE_KEY_MESSAGESN];
    }
    else
    {
        [header setObject:messageSn forKey:MESSAGE_KEY_MESSAGESN];
    }

    NSNumber* oDeviceId = [NSNumber numberWithInt:deviceId];
    [header setObject:oDeviceId forKey:MESSAGE_KEY_DEVICEID];
    
    if (nil == deviceSn)
    {
        [header setObject:[NSNull null] forKey:MESSAGE_KEY_DEVICESN];
    }
    else
    {
        [header setObject:deviceSn forKey:MESSAGE_KEY_DEVICESN];
    }
    
    [header setObject:[NSNull null] forKey:MESSAGE_KEY_TIMESTAMP];
    
    return header;
}

+(RHMessage*) newAlohaRequestMessage:(RHDevice*) device;
{
    NSAssert(nil != device, @"Device can not be null!");
    
    NSString* messageSn = [RHMessage generateMessageSn];
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_AlohaRequest messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    [messageBody setObject:@"Hello Server!" forKey:MESSAGE_KEY_CONTENT];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    message.enveloped = YES;
    
    return message;
}

+(RHMessage*) newServerTimeoutResponseMessage:(NSString*) messageSn
{
    NSInteger deviceId = 0;
    NSString* deviceSn = nil;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_ServerResponse messageId:MessageId_ServerTimeoutResponse    messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSDictionary* messageBody = [NSDictionary dictionary];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return message;
}

+(RHMessage*) newAppDataSyncRequestMessage:(AppDataSyncRequestType) type device:(RHDevice*) device;
{
    NSAssert(nil != device, @"Device can not be null!");
    
    RHMessage* appDataSyncRequestMessage = nil;
    
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSString* messageSn = [RHMessage generateMessageSn];
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_AppDataSyncRequest    messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    
    NSMutableDictionary* dataQuery = [NSMutableDictionary dictionary];
    NSMutableDictionary* dataUpdate = [NSMutableDictionary dictionary];
    
    id oNull = [NSNull null];
    NSMutableDictionary* dataSource = [NSMutableDictionary dictionaryWithDictionary:device.toJSONObject];
    switch (type)
    {
        case AppDataSyncRequestType_TotalSync:
        {
            // dataUpdate
            NSMutableDictionary* dataUpdateSource = [dataSource copy];
            NSMutableDictionary* deviceSource = [dataUpdateSource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_PROFILE];
            NSMutableDictionary* deviceCardSource = [deviceSource objectForKey:MESSAGE_KEY_DEVICECARD];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_DEVICECARDID];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_REGISTERTIME];
            dataUpdate = dataUpdateSource;
            
            // dataQuery
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_DEVICEID];
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_DEVICESN];
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_DEVICECARD];
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_PROFILE];
            
            break;
        }
        case AppDataSyncRequestType_DeviceCardSync:
        {
            // dataUpdate
            NSMutableDictionary* dataUpdateSource = [dataSource copy];
            NSMutableDictionary* deviceSource = [dataUpdateSource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICESN];
            [deviceSource removeObjectForKey:MESSAGE_KEY_PROFILE];
            NSMutableDictionary* deviceCardSource = [deviceSource objectForKey:MESSAGE_KEY_DEVICECARD];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_DEVICECARDID];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_REGISTERTIME];
            dataUpdate = dataUpdateSource;
            
            // dataQuery
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_DEVICEID];
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_DEVICESN];
            [dataQuery setObject:oNull forKey:MESSAGE_KEY_DEVICECARD];
            break;
        }
        case AppDataSyncRequestType_ImpressCardSync:
        {
            // dataUpdate
            dataUpdate = nil;
            
            // dataQuery
            NSMutableDictionary* dataQuerySource = [dataSource copy];
            NSMutableDictionary* deviceSource = [dataQuerySource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICESN];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICECARD];
            NSMutableDictionary* profileSource = [deviceSource objectForKey:MESSAGE_KEY_PROFILE];
            [profileSource removeObjectForKey:MESSAGE_KEY_PROFILEID];
            [profileSource removeObjectForKey:MESSAGE_KEY_SERVICESTATUS];
            [profileSource removeObjectForKey:MESSAGE_KEY_UNBANDATE];
            [profileSource removeObjectForKey:MESSAGE_KEY_ACTIVE];
            [profileSource removeObjectForKey:MESSAGE_KEY_CREATETIME];
            [profileSource removeObjectForKey:MESSAGE_KEY_INTERESTCARD];
            [profileSource setObject:oNull forKey:MESSAGE_KEY_IMPRESSCARD];
            dataQuery = dataQuerySource;
            
            break;
        }
        case AppDataSyncRequestType_InterestCardSync:
        {
            // dataUpdate
            NSMutableDictionary* dataUpdateSource = [dataSource copy];
            NSMutableDictionary* deviceSource = [dataUpdateSource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICESN];
            NSMutableDictionary* profileSource = [deviceSource objectForKey:MESSAGE_KEY_PROFILE];
            [profileSource removeObjectForKey:MESSAGE_KEY_PROFILEID];
            [profileSource removeObjectForKey:MESSAGE_KEY_SERVICESTATUS];
            [profileSource removeObjectForKey:MESSAGE_KEY_UNBANDATE];
            [profileSource removeObjectForKey:MESSAGE_KEY_ACTIVE];
            [profileSource removeObjectForKey:MESSAGE_KEY_CREATETIME];
            [profileSource removeObjectForKey:MESSAGE_KEY_IMPRESSCARD];
            dataUpdate = dataUpdateSource;
            
            // dataQuery
            NSMutableDictionary* dataQuerySource = [dataSource copy];
            deviceSource = [dataQuerySource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICESN];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICECARD];
            profileSource = [deviceSource objectForKey:MESSAGE_KEY_PROFILE];
            [profileSource removeObjectForKey:MESSAGE_KEY_PROFILEID];
            [profileSource removeObjectForKey:MESSAGE_KEY_SERVICESTATUS];
            [profileSource removeObjectForKey:MESSAGE_KEY_UNBANDATE];
            [profileSource removeObjectForKey:MESSAGE_KEY_ACTIVE];
            [profileSource removeObjectForKey:MESSAGE_KEY_CREATETIME];
            [profileSource removeObjectForKey:MESSAGE_KEY_IMPRESSCARD];
            [profileSource setObject:oNull forKey:MESSAGE_KEY_INTERESTCARD];
            dataQuery = dataQuerySource;
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    if (nil != dataUpdate && 0 < dataUpdate.count)
    {
        [messageBody setObject:dataUpdate forKey:MESSAGE_KEY_DATAUPDATE];
    }
    if (nil != dataQuery &&  0 < dataQuery.count)
    {
        [messageBody setObject:dataQuery forKey:MESSAGE_KEY_DATAQUERY];
    }
    
    appDataSyncRequestMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return appDataSyncRequestMessage;
}

+(RHMessage*) newServerDataSyncRequestMessage:(ServerDataSyncRequestType) type device:(RHDevice*) device info:(NSDictionary *)info
{
    NSAssert(nil != device, @"Device can not be null!");
    
    NSString* messageSn = [RHMessage generateMessageSn];
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_ServerDataSyncRequest messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    id oNull = [NSNull null];
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];

    switch (type)
    {
        case ServerDataSyncRequestType_TotalSync:
        {
            NSMutableDictionary* deviceCount = [NSMutableDictionary dictionary];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_ONLINE];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_RANDOM];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_INTEREST];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_CHAT];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_RANDOMCHAT];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_INTERESTCHAT];
            [messageBody setObject:deviceCount forKey:MESSAGE_KEY_DEVICECOUNT];
            
            NSMutableDictionary* deviceCapacity = [NSMutableDictionary dictionary];
            [deviceCapacity setObject:oNull forKey:MESSAGE_KEY_ONLINE];
            [deviceCapacity setObject:oNull forKey:MESSAGE_KEY_RANDOM];
            [deviceCapacity setObject:oNull forKey:MESSAGE_KEY_INTEREST];
            [messageBody setObject:deviceCapacity forKey:MESSAGE_KEY_DEVICECAPACITY];
            
            id oCurrent = oNull;
            id oStartTime = oNull;
            id oEndTime = oNull;
            if (nil != info)
            {
                NSNumber* obj = [info objectForKey:MESSAGE_KEY_CURRENT];
                oCurrent = (nil != obj) ? obj : oNull;
                
                obj = [info objectForKey:MESSAGE_KEY_STARTTIME];
                oStartTime = (nil != obj) ? obj : oNull;
                
                obj = [info objectForKey:MESSAGE_KEY_ENDTIME];
                oEndTime = (nil != obj) ? obj : oNull;
            }
            
            NSMutableDictionary* interestLabelList = [NSMutableDictionary dictionary];
            [interestLabelList setObject:oCurrent forKey:MESSAGE_KEY_CURRENT];
            NSMutableDictionary* history = [NSMutableDictionary dictionary];
            [history setObject:oStartTime forKey:MESSAGE_KEY_STARTTIME];
            [history setObject:oEndTime forKey:MESSAGE_KEY_ENDTIME];
            [interestLabelList setObject:history forKey:MESSAGE_KEY_HISTORY];
            [messageBody setObject:interestLabelList forKey:MESSAGE_KEY_INTERESTLABELLIST];
            
            break;
        }
        case ServerDataSyncRequestType_DeviceCountSync:
        {
            NSMutableDictionary* deviceCount = [NSMutableDictionary dictionary];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_ONLINE];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_RANDOM];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_INTEREST];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_CHAT];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_RANDOMCHAT];
            [deviceCount setObject:oNull forKey:MESSAGE_KEY_INTERESTCHAT];
            [messageBody setObject:deviceCount forKey:MESSAGE_KEY_DEVICECOUNT];
            
            break;
        }
        case ServerDataSyncRequestType_DeviceCapacitySync:
        {
            NSMutableDictionary* deviceCapacity = [NSMutableDictionary dictionary];
            [deviceCapacity setObject:oNull forKey:MESSAGE_KEY_ONLINE];
            [deviceCapacity setObject:oNull forKey:MESSAGE_KEY_RANDOM];
            [deviceCapacity setObject:oNull forKey:MESSAGE_KEY_INTEREST];
            [messageBody setObject:deviceCapacity forKey:MESSAGE_KEY_DEVICECAPACITY];
            
            break;
        }
        case ServerDataSyncRequestType_InterestLabelListSync:
        {
            id oCurrent = oNull;
            id oStartTime = oNull;
            id oEndTime = oNull;
            if (nil != info)
            {
                NSNumber* obj = [info objectForKey:MESSAGE_KEY_CURRENT];
                oCurrent = (nil != obj) ? obj : oNull;
                
                obj = [info objectForKey:MESSAGE_KEY_STARTTIME];
                oStartTime = (nil != obj) ? obj : oNull;
                
                obj = [info objectForKey:MESSAGE_KEY_ENDTIME];
                oEndTime = (nil != obj) ? obj : oNull;
            }
            
            NSMutableDictionary* interestLabelList = [NSMutableDictionary dictionary];
            [interestLabelList setObject:oCurrent forKey:MESSAGE_KEY_CURRENT];
            NSMutableDictionary* history = [NSMutableDictionary dictionary];
            [history setObject:oStartTime forKey:MESSAGE_KEY_STARTTIME];
            [history setObject:oEndTime forKey:MESSAGE_KEY_ENDTIME];
            [interestLabelList setObject:history forKey:MESSAGE_KEY_HISTORY];
            [messageBody setObject:interestLabelList forKey:MESSAGE_KEY_INTERESTLABELLIST];
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    RHMessage* serverDataSyncRequestMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return serverDataSyncRequestMessage;
}

+(RHMessage*) newBusinessSessionRequestMessage:(NSString*) businessSessionId businessType:(RHBusinessType) businessType operationType:(BusinessSessionRequestType) operationType device:(RHDevice*) device info:(NSDictionary*) info
{
    NSAssert(nil != device, @"Device can not be null!");
    
    NSString* messageSn = [RHMessage generateMessageSn];
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_BusinessSessionRequest messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    id oNull = [NSNull null];
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    
    businessSessionId = (nil != businessSessionId) ? businessSessionId : oNull;
    [messageBody setObject:businessSessionId forKey:MESSAGE_KEY_BUSINESSSESSIONID];
    
    NSNumber* oBusinessType = [NSNumber numberWithInt:businessType];
    [messageBody setObject:oBusinessType forKey:MESSAGE_KEY_BUSINESSTYPE];
    
    NSNumber* oOperationType = [NSNumber numberWithInt:operationType];
    [messageBody setObject:oOperationType forKey:MESSAGE_KEY_OPERATIONTYPE];
    
    info = (nil != info) ? info : oNull;
    [messageBody setObject:info forKey:MESSAGE_KEY_OPERATIONINFO];
    
    [messageBody setObject:oNull forKey:MESSAGE_KEY_OPERATIONVALUE];
    
    RHMessage* businessSessionRequestMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return businessSessionRequestMessage;
}

+(RHMessage*) newBusinessSessionNotificationResponseMessage:(NSString*) businessSessionId businessType:(RHBusinessType) businessType operationType:(BusinessSessionNotificationType) operationType operationValue:(BusinessSessionOperationValue) operationValue device:(RHDevice*) device info:(NSDictionary*) info
{
    NSAssert(nil != device, @"Device can not be null!");
    
    NSString* messageSn = [RHMessage generateMessageSn];
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppResponse messageId:MessageId_BusinessSessionNotificationResponse messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    id oNull = [NSNull null];
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    
    businessSessionId = (nil != businessSessionId) ? businessSessionId : oNull;
    [messageBody setObject:businessSessionId forKey:MESSAGE_KEY_BUSINESSSESSIONID];
    
    NSNumber* oBusinessType = [NSNumber numberWithInt:businessType];
    [messageBody setObject:oBusinessType forKey:MESSAGE_KEY_BUSINESSTYPE];
    
    NSNumber* oOperationType = [NSNumber numberWithInt:operationType];
    [messageBody setObject:oOperationType forKey:MESSAGE_KEY_OPERATIONTYPE];
    
    info = (nil != info) ? info : oNull;
    [messageBody setObject:info forKey:MESSAGE_KEY_OPERATIONINFO];
    
    NSNumber* oOperationValue = [NSNumber numberWithInt:operationValue];
    [messageBody setObject:oOperationValue forKey:MESSAGE_KEY_OPERATIONVALUE];
    
    RHMessage* businessSessionNotificationResponseMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return businessSessionNotificationResponseMessage;
}

+(BOOL) isServerTimeoutResponseMessage:(RHMessage*) message
{
    BOOL flag = NO;
    
    if (nil != message && message.messageId == MessageId_ServerTimeoutResponse)
    {
        flag = YES;
    }
    
    return flag;
}

+(BOOL) isServerErrorResponseMessage:(RHMessage*) message
{
    BOOL flag = NO;
    
    if (nil != message && message.messageId == MessageId_ServerErrorResponse)
    {
        flag = YES;
    }
    
    return flag;
}


@synthesize enveloped = _enveloped;
@synthesize header = _header;
@synthesize body = _body;

#pragma mark - Public Methods

-(RHMessageType) messageType
{
    NSString* str = [_header objectForKey:MESSAGE_KEY_MESSAGETYPE];
    return [str intValue];
}

-(RHMessageId) messageId
{
    NSString* str = [_header objectForKey:MESSAGE_KEY_MESSAGEID];
    RHMessageId messageId = str.intValue;
    
    return messageId;
}

-(NSString*) messageSn
{
    return [_header objectForKey:MESSAGE_KEY_MESSAGESN];
}

-(NSInteger) deviceId
{
    NSString* sDeviceId = [_header objectForKey:MESSAGE_KEY_DEVICEID];
    return sDeviceId.integerValue;
}

-(NSString*) deviceSn
{
    return [_header objectForKey:MESSAGE_KEY_DEVICESN];
}

-(NSDate*) timeStamp
{
    NSString* sTimeStamp = [_header objectForKey:MESSAGE_KEY_TIMESTAMP];
    NSDate* timeStamp = [CBDateUtils dateFromStringWithFormat:sTimeStamp andFormat:FULL_DATE_TIME_FORMAT];
    
    return timeStamp;
}

-(void) setTimeStamp:(NSDate*) time
{
    if (nil != time)
    {
        NSString* sTimeStamp = [CBDateUtils dateStringInLocalTimeZoneWithFormat:FULL_DATE_TIME_FORMAT andDate:time];
        [_header setValue:sTimeStamp forKey:MESSAGE_KEY_TIMESTAMP];
    }
    else
    {
        [_header setValue:[NSNull null] forKey:MESSAGE_KEY_TIMESTAMP];
    }
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSAssert(nil != _header, @"Header part of message can not be null!");
    NSAssert(nil != _body, @"Body part of message can not be null!");
    
    NSDictionary* content = [NSDictionary dictionaryWithObjects:@[_header, _body] forKeys:@[MESSAGE_KEY_HEADER, MESSAGE_KEY_BODY]];
    
    if (_enveloped)
    {
        content = [NSDictionary dictionaryWithObject:content forKey:MESSAGE_KEY_ENVELOPE];
    }
    
    return content;
}

-(NSString*) toJSONString
{
    NSDictionary* content = [self toJSONObject];
    
    NSString* str = [CBJSONUtils toJSONString:content];
    
    return str;
}

#pragma mark - Private Methods

@end
