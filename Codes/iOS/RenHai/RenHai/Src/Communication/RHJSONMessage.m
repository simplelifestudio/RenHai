//
//  RHJSONMessage.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHJSONMessage.h"

#import "CBJSONUtils.h"

@implementation RHJSONMessage

+(RHJSONMessage*) constructWithMessageHeader:(NSDictionary*) header messageBody:(NSDictionary*) body
{
    RHJSONMessage* message = [[RHJSONMessage alloc] init];
    
    message.header = header;
    message.body = body;
    
    return message;
}

+(RHJSONMessage*) constructWithContent:(NSDictionary*) content
{
    NSDictionary* header = [content objectForKey:JSONMESSAGE_KEY_HEADER];
    NSDictionary* body = [content objectForKey:JSONMESSAGE_KEY_BODY];
    
    RHJSONMessage* message = [RHJSONMessage constructWithMessageHeader:header messageBody:body];
    
    return message;
}

+(RHJSONMessage*) constructWithString:(NSString*) jsonString
{
    RHJSONMessage* message = nil;
    
    if (nil != jsonString && 0 < jsonString.length)
    {
        NSDictionary* dic = [CBJSONUtils toJSONObject:jsonString];
        message = [RHJSONMessage constructWithContent:dic];
    }
    
    return message;
}

+(RHJSONMessageErrorCode) verify:(RHJSONMessage*) message
{
    RHJSONMessageErrorCode error = ServerLegalResponse;
    
    if (nil != message)
    {
        NSString* messageId = message.messageId;
        NSString* messageSn = message.messageSn;
        if (nil != messageId && 0 < messageId.length && nil != messageSn && 0 < messageSn.length)
        {
            if ([messageId isEqualToString:JSONMESSAGE_SERVERERRORRESPONSE])
            {
                error = ServerErrorResponse;
            }
            else if ([messageId isEqualToString:JSONMESSAGE_SERVERTIMEOUTRESPONSE])
            {
                error = ServerTimeout;
            }
        }
        else
        {
            error = ServerIllegalResponse;
        }
    }
    else
    {
        error = ServerNullResponse;
    }
    
    return error;
}

+(RHJSONMessage*) newServerTimeoutResponseMessage
{
    NSString* messageType = [NSString stringWithFormat:@"%d", ServerResponseMessage];
    NSDictionary* dic = [NSDictionary dictionaryWithObjects:@[messageType, JSONMESSAGE_SERVERTIMEOUTRESPONSE] forKeys:@[JSONMESSAGE_KEY_MESSAGETYPE, JSONMESSAGE_KEY_MESSAGEID]];
    
    RHJSONMessage* message = [RHJSONMessage constructWithMessageHeader:dic messageBody:nil];
    
    return message;
}

+(BOOL) isServerTimeoutResponseMessage:(RHJSONMessage*) message
{
    BOOL flag = NO;
    
    if (nil != message && [message.messageId isEqualToString:JSONMESSAGE_SERVERTIMEOUTRESPONSE])
    {
        flag = YES;
    }
    
    return flag;
}

+(BOOL) isServerErrorResponseMessage:(RHJSONMessage*) message
{
    BOOL flag = NO;
    
    if (nil != message && [message.messageId isEqualToString:JSONMESSAGE_SERVERERRORRESPONSE])
    {
        flag = YES;
    }
    
    return flag;
}

@synthesize header = _header;
@synthesize body = _body;

#pragma mark - Public Methods

-(NSString*) messageSn
{
    return [_header objectForKey:JSONMESSAGE_KEY_MESSAGESN];
}

-(NSString*) messageId
{    
    return [_header objectForKey:JSONMESSAGE_KEY_MESSAGEID];
}

-(RHJSONMessageType) messageType
{
    NSString* str = [_header objectForKey:JSONMESSAGE_KEY_MESSAGETYPE];
    return [str intValue];
}

//-(RHJSONMessageType) messageType
//{
//    RHJSONMessageType type = UnknownMessage;
//    
//    NSString* messageId = [self messageId];
//    if (nil != messageId && 0 < messageId.length)
//    {
//        BOOL flag1 = [messageId isEqualToString:JSONMESSAGE_ALOHAREQUEST];
//        BOOL flag2 = [messageId isEqualToString:JSONMESSAGE_APPDATASYNCREQUEST];
//        BOOL flag3 = [messageId isEqualToString:JSONMESSAGE_BUSINESSSESSIONREQUEST];
//        BOOL flag4 = [messageId isEqualToString:JSONMESSAGE_SERVERDATASYNCREQUEST];
//        BOOL appRequestTypeFlag = flag1 | flag2 | flag3 | flag4;
//        if (appRequestTypeFlag)
//        {
//            type = AppRequestMessage;
//        }
//        else
//        {
//            BOOL flag5 = [messageId isEqualToString:JSONMESSAGE_ALOHARESPONSE];
//            BOOL flag6 = [messageId isEqualToString:JSONMESSAGE_APPDATASYNCRESPONSE];
//            BOOL flag7 = [messageId isEqualToString:JSONMESSAGE_BUSINESSSESSIONRESPONSE];
//            BOOL flag8 = [messageId isEqualToString:JSONMESSAGE_SERVERDATASYNCRESPONSE];
//            BOOL flag9 = [messageId isEqualToString:JSONMESSAGE_SERVERERRORRESPONSE];
//            BOOL flag10 = [messageId isEqualToString:JSONMESSAGE_SERVERTIMEOUTRESPONSE];
//            BOOL serverResponseTypeFlag = flag5 | flag6 | flag7 | flag8 | flag9 | flag10;
//            if (serverResponseTypeFlag)
//            {
//                type = ServerResponseMessage;
//            }
//            else
//            {
//                BOOL flag11 = [messageId isEqualToString:JSONMESSAGE_BUSINESSSESSIONNOTIFICATION];
//                BOOL flag12 = [messageId isEqualToString:JSONMESSAGE_BROADCASTNOTIFICATION];
//                BOOL serverNotificationTypeFlag = flag11 | flag12;
//                if (serverNotificationTypeFlag)
//                {
//                    type = ServerNotificationMessage;
//                }
//                else
//                {
//                    BOOL flag13 = [messageId isEqualToString:JSONMESSAGE_BUSINESSSESSIONNOTIFICATIONRESPONSE];
//                    BOOL flag14 = [messageId isEqualToString:JSONMESSAGE_BROADCASTNOTIFICATIONRESPONSE];
//                    BOOL appResponseTypeFlag = flag13 | flag14;
//                    if (appResponseTypeFlag)
//                    {
//                        type = AppResponseMessage;
//                    }
//                }
//            }
//        }
//    }
//
//    return type;
//}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSDictionary* content = [NSDictionary dictionaryWithObjects:@[_header, _body] forKeys:@[JSONMESSAGE_KEY_HEADER, JSONMESSAGE_KEY_BODY]];
    
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
