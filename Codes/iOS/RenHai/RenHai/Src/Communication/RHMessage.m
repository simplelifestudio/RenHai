//
//  RHJSONMessage.m
//  RenHai
//
//  Created by DENG KE on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHMessage.h"

#import "CBJSONUtils.h"
#import "CBStringUtils.h"
#import "CBDateUtils.h"
#import "CBSecurityUtils.h"

#import "NSDictionary+MutableDeepCopy.h"

@interface RHMessage()
{
    
}

@property (nonatomic, strong, readwrite) NSDictionary* header;
@property (nonatomic, strong, readwrite) NSDictionary* body;

@end

@implementation RHMessage

@synthesize header = _header;
@synthesize body = _body;

+(NSString*) generateMessageSn
{
    return [CBStringUtils randomString:MESSAGE_MESSAGESN_LENGTH];
}

+(RHMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body
{
    RHMessage* message = [[RHMessage alloc] init];
    
    message.header = header;
    message.body = body;
    
    return message;
}

+(RHMessage*) constructWithContent:(NSDictionary*) content
{
    RHMessage* message = nil;
    
    if (nil != content)
    {
        NSDictionary* header = [content objectForKey:MESSAGE_KEY_HEADER];
        NSDictionary* body = [content objectForKey:MESSAGE_KEY_BODY];
        
        message = [RHMessage constructWithMessageHeader:header messageBody:body];
    }
    
    return message;
}

+(RHMessage*) constructWithString:(NSString*) jsonString
{
    NSDictionary* content = [CBJSONUtils toJSONObject:jsonString];
    
    RHMessage* message = nil;
    
    if (nil != jsonString && 0 < jsonString.length)
    {
        message = [RHMessage constructWithContent:content];
    }
    
    return message;
}

//+(RHMessageErrorCode) verify:(RHMessage*) message
//{
//    RHMessageErrorCode error = ErrorCode_ServerLegalResponse;
//    
//    if (nil != message)
//    {
//        RHMessageId messageId = message.messageId;
//        if (messageId == MessageId_ServerErrorResponse)
//        {
//            error = ErrorCode_ServerErrorMessage;
//        }
//        else if (messageId == MessageId_ServerTimeoutResponse)
//        {
//            error = ErrorCode_ServerTimeoutMessage;
//        }
//        else if (messageId == MessageId_Unknown)
//        {
//            error = ErrorCode_ServerIllegalMessage;
//        }
//    }
//    else
//    {
//        error = ErrorCode_ServerNullMessage;
//    }
//    
//    return error;
//}

+(BOOL) isLegalMessage:(RHMessage *)message
{
    BOOL flag = NO;
    
    if (nil != message)
    {
        NSDictionary* header = message.header;
        if (nil != header)
        {
            __block BOOL hasMessageTye = NO;
            __block BOOL hasMessageId = NO;
            __block BOOL hasMessageSn = NO;
            __block BOOL hasTimeStamp = NO;
            __block BOOL hasDevieSn = NO;
//            __block BOOL hasDeviceId = NO;
            
            [header enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
                if ([key isEqualToString:MESSAGE_KEY_MESSAGETYPE])
                {
                    hasMessageTye = YES;
                }
                else if ([key isEqualToString:MESSAGE_KEY_MESSAGEID])
                {
                    hasMessageId = YES;
                }
                else if ([key isEqualToString:MESSAGE_KEY_MESSAGESN])
                {
                    hasMessageSn = YES;
                }
                else if ([key isEqualToString:MESSAGE_KEY_TIMESTAMP])
                {
                    hasTimeStamp = YES;
                }
                else if ([key isEqualToString:MESSAGE_KEY_DEVICESN])
                {
                    hasDevieSn = YES;
                }
//                else if ([key isEqualToString:MESSAGE_KEY_DEVICEID])
//                {
//                    hasDeviceId = YES;
//                }
            }];
            
//            flag = (hasMessageTye & hasMessageId & hasMessageSn & hasTimeStamp & hasDevieSn & hasDeviceId) ? YES : NO;
            flag = (hasMessageTye & hasMessageId & hasMessageSn & hasTimeStamp & hasDevieSn) ? YES : NO;            
        }
    }
    
    return flag;
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

+(RHMessage*) newProxyDataSyncRequest
{
    NSString* messageSn = [RHMessage generateMessageSn];
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_ProxyRequest messageId:MessageId_ProxyDataSyncRequest messageSn:messageSn deviceId:0 deviceSn:nil];
    
    NSDictionary* messageBody = [NSDictionary dictionary];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return message;
}

+(RHMessage*) newAlohaRequestMessage:(RHDevice*) device;
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
    NSString* messageSn = [RHMessage generateMessageSn];
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_AlohaRequest messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    [messageBody setObject:@"Hello Server!" forKey:MESSAGE_KEY_CONTENT];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];

    return message;
}

+(RHMessage*) newServerTimeoutResponseMessage:(NSString*) messageSn device:(RHDevice*) device
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_ServerResponse messageId:MessageId_ServerTimeoutResponse messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSDictionary* messageBody = [NSDictionary dictionary];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return message;
}

+(RHMessage*) newAppErrorResponseMessage:(RHMessage*) receivedMessage
{
    NSInteger deviceId = receivedMessage.deviceId;
    NSString* deviceSn = receivedMessage.deviceSn;
    NSString* messageSn = receivedMessage.messageSn;
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppResponse messageId:MessageId_AppErrorResponse messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSNumber* oErrorCode = [NSNumber numberWithInt:-1];
    NSString* errorDescription = @"Illegal message which can not be recoganized by app.";
    NSDictionary* messageBody = [NSDictionary dictionaryWithObjects:@[oErrorCode, receivedMessage.toJSONString, errorDescription] forKeys:@[MESSAGE_KEY_ERRORCODE, MESSAGE_KEY_RECEIVEDMESSAGE, MESSAGE_KEY_ERRORDESCRIPTION]];
    
    RHMessage* message = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return message;
}

+(RHMessage*) newAppDataSyncRequestMessage:(AppDataSyncRequestType) type device:(RHDevice*) device info:(NSDictionary*) info;
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
    RHMessage* appDataSyncRequestMessage = nil;
    
    NSInteger deviceId = device.deviceId;
    NSString* deviceSn = device.deviceSn;
    NSString* messageSn = [RHMessage generateMessageSn];
    NSDictionary* messageHeader = [RHMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_AppDataSyncRequest    messageSn:messageSn deviceId:deviceId deviceSn:deviceSn];
    
    NSMutableDictionary* messageBody = [NSMutableDictionary dictionary];
    
    NSMutableDictionary* dataQuery = [NSMutableDictionary dictionary];
    NSMutableDictionary* dataUpdate = [NSMutableDictionary dictionary];
    
    id oNull = [NSNull null];
    switch (type)
    {
        case AppDataSyncRequestType_TotalSync:
        {
            NSMutableDictionary* dataSource = [NSMutableDictionary dictionaryWithDictionary:device.toJSONObject];
            // dataUpdate
            NSMutableDictionary* dataUpdateSource = [dataSource mutableDeepCopy];
            NSMutableDictionary* deviceSource = [dataUpdateSource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_PROFILE];
            NSMutableDictionary* deviceCardSource = [deviceSource objectForKey:MESSAGE_KEY_DEVICECARD];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_DEVICECARDID];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_REGISTERTIME];
            dataUpdate = dataUpdateSource;
            
            // dataQuery
            deviceSource = oNull;
            [dataQuery setObject:deviceSource forKey:MESSAGE_KEY_DEVICE];
            
            break;
        }
        case AppDataSyncRequestType_DeviceCardSync:
        {
            NSMutableDictionary* dataSource = [NSMutableDictionary dictionaryWithDictionary:device.toJSONObject];
            // dataUpdate
            NSMutableDictionary* dataUpdateSource = [dataSource mutableDeepCopy];
            NSMutableDictionary* deviceSource = [dataUpdateSource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICESN];
            [deviceSource removeObjectForKey:MESSAGE_KEY_PROFILE];
            NSMutableDictionary* deviceCardSource = [deviceSource objectForKey:MESSAGE_KEY_DEVICECARD];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_DEVICECARDID];
            [deviceCardSource removeObjectForKey:MESSAGE_KEY_REGISTERTIME];
            dataUpdate = dataUpdateSource;
            
            // dataQuery
            deviceSource = [NSMutableDictionary dictionary];
            [deviceSource setObject:oNull forKey:MESSAGE_KEY_DEVICEID];
            [deviceSource setObject:oNull forKey:MESSAGE_KEY_DEVICESN];
            [deviceSource setObject:oNull forKey:MESSAGE_KEY_DEVICECARD];
            [dataQuery setObject:deviceSource forKey:MESSAGE_KEY_DEVICE];
            
            break;
        }
        case AppDataSyncRequestType_ImpressCardSync:
        {
            NSMutableDictionary* dataSource = [NSMutableDictionary dictionaryWithDictionary:device.toJSONObject];
            
            // dataUpdate
            dataUpdate = nil;
            
            // dataQuery
            NSMutableDictionary* dataQuerySource = [dataSource mutableDeepCopy];
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
            NSDictionary* dataSource = [NSDictionary dictionaryWithDictionary:device.toJSONObject];

            // dataUpdate
            NSMutableDictionary* dataUpdateSource = [dataSource mutableDeepCopy];
            NSMutableDictionary* deviceSource = [dataUpdateSource objectForKey:MESSAGE_KEY_DEVICE];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICEID];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICESN];
            [deviceSource removeObjectForKey:MESSAGE_KEY_DEVICECARD];
            NSMutableDictionary* profileSource = [deviceSource objectForKey:MESSAGE_KEY_PROFILE];
            [profileSource removeObjectForKey:MESSAGE_KEY_PROFILEID];
            [profileSource removeObjectForKey:MESSAGE_KEY_SERVICESTATUS];
            [profileSource removeObjectForKey:MESSAGE_KEY_UNBANDATE];
            [profileSource removeObjectForKey:MESSAGE_KEY_ACTIVE];
            [profileSource removeObjectForKey:MESSAGE_KEY_CREATETIME];
            [profileSource removeObjectForKey:MESSAGE_KEY_IMPRESSCARD];
            dataUpdate = dataUpdateSource;
            
            // dataQuery
            NSMutableDictionary* dataQuerySource = [dataSource mutableDeepCopy];
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
    
    appDataSyncRequestMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return appDataSyncRequestMessage;
}

+(RHMessage*) newServerDataSyncRequestMessage:(ServerDataSyncRequestType) type device:(RHDevice*) device info:(NSDictionary *)info
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
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
            
            id oCurrent = [NSNumber numberWithInt:MESSAGE_VAL_CURRENT];
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
            id oCurrent = [NSNumber numberWithInt:MESSAGE_VAL_CURRENT];
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
    
    RHMessage* serverDataSyncRequestMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return serverDataSyncRequestMessage;
}

+(RHMessage*) newBusinessSessionRequestMessage:(NSString*) businessSessionId businessType:(RHBusinessType) businessType operationType:(BusinessSessionRequestType) operationType device:(RHDevice*) device info:(NSDictionary*) info
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
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
    
    RHMessage* businessSessionRequestMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return businessSessionRequestMessage;
}

+(RHMessage*) newBusinessSessionNotificationResponseMessage:(NSString*) businessSessionId businessType:(RHBusinessType) businessType operationType:(BusinessSessionNotificationType) operationType operationValue:(BusinessSessionOperationValue) operationValue device:(RHDevice*) device info:(NSDictionary*) info
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
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
    
    RHMessage* businessSessionNotificationResponseMessage = [RHMessage constructWithMessageHeader:messageHeader messageBody:messageBody];
    
    return businessSessionNotificationResponseMessage;
}

+(RHMessage*) newBusinessSessionNotificationResponseMessage:(RHMessage*) businessSessionNotificationMessage device:(RHDevice*) device
{
    [CBAppUtils assert:(nil != device) logFormatString:@"Device can not be null!"];
    
    RHMessage* response = nil;
    
    if (nil != businessSessionNotificationMessage)
    {
        NSDictionary* header = [[NSMutableDictionary alloc] initWithDictionary:businessSessionNotificationMessage.header copyItems:YES];
        NSString* messageSn = [header objectForKey:MESSAGE_KEY_MESSAGESN];
        
        header = [RHMessage constructMessageHeader:MessageType_AppResponse messageId:MessageId_BusinessSessionNotificationResponse messageSn:messageSn deviceId:device.deviceId deviceSn:device.deviceSn];
        
        NSDictionary* body = [[NSMutableDictionary alloc] initWithDictionary:businessSessionNotificationMessage.body copyItems:YES];
        
        BusinessSessionOperationValue operationValue = BusinessSessionOperationValue_Success;
        NSNumber* oOperationValue = [NSNumber numberWithInt:operationValue];
        [body setValue:oOperationValue forKey:MESSAGE_KEY_OPERATIONVALUE];

        id oNull = [NSNull null];
        [body setValue:oNull forKey:MESSAGE_KEY_OPERATIONINFO];
        
        response = [RHMessage constructWithMessageHeader:header messageBody:body];
    }
    
    return response;
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

-(void) fromJSONObject:(NSDictionary *)dic
{

}

-(NSDictionary*) toJSONObject
{
    [CBAppUtils assert:(nil != _header) logFormatString:@"Header part of message can not be null!"];
    [CBAppUtils assert:(nil != _body) logFormatString:@"Body part of message can not be null!"];
    
    NSDictionary* content = [NSDictionary dictionaryWithObjects:@[_header, _body] forKeys:@[MESSAGE_KEY_HEADER, MESSAGE_KEY_BODY]];
    
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
