//
//  RHJSONMessage.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHJSONMessage.h"

#import "CBJSONUtils.h"
#import "CBSecurityUtils.h"

static BOOL s_messageEncrypted;

@implementation RHJSONMessage

+(void) setMessageNeedEncrypt:(BOOL) encrypt
{
    s_messageEncrypted = encrypt;
}

+(BOOL) isMessageNeedEncrypt
{
    return s_messageEncrypted;
}

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
        if ([RHJSONMessage isMessageNeedEncrypt])
        {
            jsonString = [CBSecurityUtils decryptByDESAndDecodeByBase64:jsonString key:JSONMESSAGE_SECURITY_KEY];
        }
        
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