//
//  RHJSONMessage.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHJSONMessage.h"

#import "CBJSONUtils.h"
#import "CBStringUtils.h"
#import "CBDateUtils.h"
#import "CBSecurityUtils.h"

static BOOL s_messageEncrypted;

@interface RHJSONMessage()
{
    
}

@property (nonatomic, strong, readwrite) NSDictionary* header;
@property (nonatomic, strong, readwrite) NSDictionary* body;

@end

@implementation RHJSONMessage

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

+(RHJSONMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body enveloped:(BOOL) enveloped;
{
    RHJSONMessage* message = [[RHJSONMessage alloc] init];
    
    message.header = header;
    message.body = body;
    
    message.enveloped = enveloped;
    
    return message;
}

+(RHJSONMessage*) constructWithContent:(NSDictionary*) content enveloped:(BOOL) enveloped;
{
    if (enveloped)
    {
        content = [content objectForKey:MESSAGE_KEY_ENVELOPE];
    }

    NSDictionary* header = [content objectForKey:MESSAGE_KEY_HEADER];
    NSDictionary* body = [content objectForKey:MESSAGE_KEY_BODY];
    
    RHJSONMessage* message = [RHJSONMessage constructWithMessageHeader:header messageBody:body enveloped:enveloped];
    
    return message;
}

+(RHJSONMessage*) constructWithString:(NSString*) jsonString enveloped:(BOOL)enveloped
{
    NSDictionary* content = [CBJSONUtils toJSONObject:jsonString];
    
    if (enveloped)
    {
        content = [content objectForKey:MESSAGE_KEY_ENVELOPE];
    }
    
    RHJSONMessage* message = nil;
    
    if (nil != jsonString && 0 < jsonString.length)
    {
        if ([RHJSONMessage isMessageNeedEncrypt])
        {
            jsonString = [CBSecurityUtils decryptByDESAndDecodeByBase64:jsonString key:MESSAGE_SECURITY_KEY];
        }
        
        NSDictionary* dic = [CBJSONUtils toJSONObject:jsonString];
        message = [RHJSONMessage constructWithContent:dic enveloped:NO];
    }
    
    return message;
}

+(RHMessageErrorCode) verify:(RHJSONMessage*) message
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

+(NSDictionary*) constructMessageHeader:(RHMessageType) messageType messageId:(RHMessageId) messageId messageSn:(NSString*) messageSn deviceId:(NSInteger) deviceId deviceSn:(NSString*) deviceSn timeStamp:(NSDate*) timeStamp
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
    
    timeStamp = (nil != timeStamp) ? timeStamp : [NSDate date];
    NSString* sTimeStamp = [CBDateUtils dateStringInLocalTimeZone:FULL_DATE_TIME_FORMAT andDate:timeStamp];
    
    [header setObject:sTimeStamp forKey:MESSAGE_KEY_TIMESTAMP];
    
    return header;
}

+(RHJSONMessage*) newAlohaRequestMessage:(RHDevice*) device;
{
    NSAssert(nil != device, @"Device can not be null!");
    
    NSString* messageSn = [RHJSONMessage generateMessageSn];
    NSInteger deviceId = 0;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHJSONMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_AlohaRequest messageSn:messageSn deviceId:deviceId deviceSn:deviceSn timeStamp:nil];
    
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    [messageBody setObject:@"Aloha RenHai Server" forKey:MESSAGE_KEY_CONTENT];
    
    RHJSONMessage* message = [RHJSONMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    message.enveloped = YES;
    
    return message;
}

+(RHJSONMessage*) newServerTimeoutResponseMessage:(NSString*) messageSn
{
    NSInteger deviceId = 0;
    NSString* deviceSn = nil;
    NSDictionary* messageHeader = [RHJSONMessage constructMessageHeader:MessageType_ServerResponse messageId:MessageId_ServerTimeoutResponse    messageSn:messageSn deviceId:deviceId deviceSn:deviceSn timeStamp:nil];
    
    NSDictionary* messageBody = [NSDictionary dictionary];
    
    RHJSONMessage* message = [RHJSONMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return message;
}

+(RHJSONMessage*) newAppDataSyncRequestMessage:(AppDataSyncRequestType) type device:(RHDevice*) device;
{
    NSAssert(nil != device, @"Device can not be null!");
    
    RHJSONMessage* appDataSyncRequestMessage = nil;
    
    NSInteger deviceId = 0;
    NSString* deviceSn = device.deviceSn;
    NSString* messageSn = [RHJSONMessage generateMessageSn];
    NSDictionary* messageHeader = [RHJSONMessage constructMessageHeader:MessageType_ServerResponse messageId:MessageId_ServerTimeoutResponse    messageSn:messageSn deviceId:deviceId deviceSn:deviceSn timeStamp:nil];
    
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
    
    appDataSyncRequestMessage = [RHJSONMessage constructWithMessageHeader:messageHeader messageBody:messageBody enveloped:YES];
    
    return appDataSyncRequestMessage;
}

+(BOOL) isServerTimeoutResponseMessage:(RHJSONMessage*) message
{
    BOOL flag = NO;
    
    if (nil != message && message.messageId == MessageId_ServerTimeoutResponse)
    {
        flag = YES;
    }
    
    return flag;
}

+(BOOL) isServerErrorResponseMessage:(RHJSONMessage*) message
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
