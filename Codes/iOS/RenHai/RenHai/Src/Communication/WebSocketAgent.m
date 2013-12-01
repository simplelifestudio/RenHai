//
//  WebSocketAgent.m
//  RenHai
//
//  Created by DENG KE on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "WebSocketAgent.h"

#import "AFHTTPClient.h"
#import "AFJSONRequestOperation.h"

#import "CBJSONUtils.h"
#import "CBSecurityUtils.h"

#import "CommunicationModule.h"
#import "UserDataModule.h"

#define PING_TEXT @"#####RenHai-App-Ping#####"
#define PONG_LOG 0
#define PING_ACTIVATE 1
#define MESSAGE_LOG 1
#define SERVERNOTIFICATION_LOG 0

#define MESSAGE_ENCRYPT_NECESSARY 1

@interface WebSocketAgent()
{
    SRWebSocket* _webSocket;
    NSTimer* _pingTimer;
    NSMutableDictionary* _messageLockSet;
    NSMutableDictionary* _responseMessageSet;
    
    NSCondition* _openLock;
    
    UserDataModule* _userDataModule;
}

@end

@implementation WebSocketAgent

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        [self _initInstance];
    }
    
    return self;
}

-(BOOL) openWebSocket
{
    [self closeWebSocket];
    
    RHProxy* proxy = _userDataModule.proxy;
    RHServiceAddress* address = proxy.serviceAddress;
    NSString* remotePath = address.fullAddress;

    if (nil != address && nil != remotePath)
    {
        _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:remotePath]]];
        _webSocket.delegate = self;
        
        NSTimeInterval timeout = WEBSOCKET_OPEN_TIMEOUT;
        NSDate* startTimeStamp = [NSDate date];
        NSDate* endTimeStamp = [NSDate dateWithTimeInterval:timeout sinceDate:startTimeStamp];
        
        [_webSocket open];
        
        [_openLock lock];
        BOOL flag = [_openLock waitUntilDate:endTimeStamp];
        [_openLock unlock];
        
        return flag;
    }
    
    return NO;
}

-(void) closeWebSocket
{
    [self stopPing];
    
    _webSocket.delegate = nil;
    [_webSocket close];
    _webSocket = nil;
}

-(void) startPing
{
    [_pingTimer invalidate];
    _pingTimer = [NSTimer scheduledTimerWithTimeInterval:HEARTBEATPING_PERIOD target:self selector:@selector(_heartBeatPing) userInfo:nil repeats:YES];
}

-(void) stopPing
{
    [_pingTimer invalidate];
}

-(SRReadyState) webSocketState
{
    return _webSocket.readyState;
}

-(RHMessage*) syncMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread
{
    RHMessage* responseMessage = [self requestSync:SERVICE_TARGET_WEBSOCKET requestMessage:requestMessage syncInMainThread:syncInMainThread];
    
    return responseMessage;
}

-(RHMessage*) syncMessage:(RHMessage*) requestMessage
{
    RHMessage* responseMessage = [self requestSync:SERVICE_TARGET_WEBSOCKET requestMessage:requestMessage];
    
    return responseMessage;
}

-(void) asyncMessage:(RHMessage*) requestMessage
{
    BOOL isLegalMessage = [RHMessage isLegalMessage:requestMessage];
    if (!isLegalMessage)
    {
        return;
    }

    NSDate* timeStamp = [NSDate date];
    [requestMessage setTimeStamp:timeStamp];
    
//    NSString* jsonString = requestMessage.toJSONString;
//    [self _sendJSONStringToWebSocket:jsonString];
    
    [self _sendRHMessageToWebSocket:requestMessage];
}

#pragma mark - CBJSONMessageComm

-(BOOL) isMessageNeedEncrypt
{
    return MESSAGE_ENCRYPT_NECESSARY;
}

// Warning: This method CAN NOT be invoked in Main Thread!
-(RHMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHMessage*) requestMessage
{
    return [self requestSync:serviceTarget requestMessage:requestMessage syncInMainThread:NO];
}

-(RHMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL)syncInMainThread
{
    if (!syncInMainThread && [[NSThread currentThread] isMainThread])
    {
        DDLogWarn(@"Warning: This method CAN NOT be invoked in Main Thread!");
        return nil;
    }
    
    BOOL isLegalMessage = [RHMessage isLegalMessage:requestMessage];
    if (!isLegalMessage)
    {
        return nil;
    }
    
    NSTimeInterval timeout = WEBSOCKET_COMM_TIMEOUT;
    NSDate* startTimeStamp = [NSDate date];
    NSDate* endTimeStamp = [NSDate dateWithTimeInterval:timeout sinceDate:startTimeStamp];
    
    RHMessage* responseMessage = nil;
    
    NSString* messageSn = requestMessage.messageSn;
    NSCondition* _messageLock = [self _newMessageLock:messageSn];
    
    [requestMessage setTimeStamp:startTimeStamp];

//    NSString* jsonString = requestMessage.toJSONString;
//    [self _sendJSONStringToWebSocket:jsonString];

    [self _sendRHMessageToWebSocket:requestMessage];
    
    [_messageLock lock];
    BOOL flag = [_messageLock waitUntilDate:endTimeStamp];
    [_messageLock unlock];
    [self _releaseMessageLock:_messageLock];
    
    if (!flag)
    {
        responseMessage = [RHMessage newServerTimeoutResponseMessage:messageSn device:_userDataModule.device];
        [responseMessage setTimeStamp:endTimeStamp];
    }
    else
    {
        responseMessage = [self _getResponseMessage:messageSn];
    }
    
    return responseMessage;
}

#pragma mark - SRWebSocketDelegate

- (void)webSocketDidOpen:(SRWebSocket *)webSocket;
{
    DDLogInfo(@"Websocket Connected");
    
    [_openLock lock];
    [_openLock signal];
    [_openLock unlock];
    
#if PING_ACTIVATE
    [self startPing];
#endif
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error;
{
    DDLogWarn(@"Websocket Failed With Error: %@", error);
    
    [self closeWebSocket];
    
    NSNotification* notification = [NSNotification notificationWithName:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil userInfo:nil];
    [[NSNotificationCenter defaultCenter] postNotification:notification];
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message;
{
#if MESSAGE_LOG
    DDLogWarn(@"WebSocket Received Message Uncrypted: \"%@\"", message);
#endif
    
    RHMessage* jsonMessage = nil;
    NSDictionary* dic = [CBJSONUtils toJSONObject:message];
    if ([self isMessageNeedEncrypt])
    {
        NSString* encryptedString = (NSString*)[dic objectForKey:MESSAGE_KEY_ENVELOPE];
        NSString* decryptedString = [CBSecurityUtils decryptByDESAndDecodeByBase64:encryptedString key:MESSAGE_SECURITY_KEY];
#if MESSAGE_LOG
        DDLogVerbose(@"WebSocket Received Message Decrypted: \"%@\"", decryptedString);
#endif
        dic = [CBJSONUtils toJSONObject:decryptedString];
    }
    else
    {
        dic = (NSDictionary*)[dic objectForKey:MESSAGE_KEY_ENVELOPE];
    }
    jsonMessage = [RHMessage constructWithContent:dic];
    
    BOOL isLegalMessage = [RHMessage isLegalMessage:jsonMessage];
    if (isLegalMessage)
    {
        NSString* messageSn = jsonMessage.messageSn;
        
        switch (jsonMessage.messageType)
        {
            case MessageType_ServerResponse:
            {
                [self _saveResponseMessage:jsonMessage];
                
                NSCondition* _messageLock = [self _getMessageLock:messageSn];
                [_messageLock lock];
                [_messageLock signal];
                [_messageLock unlock];
                
                break;
            }
            case MessageType_ServerNotification:
            {
                DDLogInfo(@"Received Server Notification: %@", jsonMessage.toJSONString);
                
                if (jsonMessage.messageId == MessageId_BusinessSessionNotification)
                {
                    RHMessage* responseMessage = [RHMessage newBusinessSessionNotificationResponseMessage:jsonMessage device:_userDataModule.device];
#if SERVERNOTIFICATION_LOG
                    DDLogInfo(@"Received Server Notification: %@", jsonMessage.toJSONString);
                    DDLogInfo(@"Reply Server NotificationResponse: %@", responseMessage.toJSONString);
#endif
                    [self asyncMessage:responseMessage];
                }
                
                NSNotification* notification = [NSNotification notificationWithName:NOTIFICATION_ID_RHSERVERNOTIFICATION object:jsonMessage userInfo:nil];
                [[NSNotificationCenter defaultCenter] postNotification:notification];
                
                break;
            }
            default:
            {
                [CBAppUtils assert:NO logFormatString:@"Received an unexpected message!"];
                break;
            }
        }
    }
    else
    {
        /*
        RHMessage* message = [RHMessage newAppErrorResponseMessage:jsonMessage.messageSn];
        [self performSelectorInBackground:@selector(_sendJSONStringToWebSocket:) withObject:message.toJSONString];
         */
        [CBAppUtils assert:NO logFormatString:@"Received an illegal message from server: %@", jsonMessage.toJSONString];
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)data
{
#if PONG_LOG
    NSString* str = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    DDLogInfo(@"WebSocket Received Pong \"%@\"", str);
#endif
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean;
{
    DDLogWarn(@"WebSocket closed with code: %d, reason: %@, wasClean: %@", code, reason, (wasClean) ? @"YES" : @"NO");
 
    [self closeWebSocket];
    
    NSNotification* notification = [NSNotification notificationWithName:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil userInfo:nil];
    [[NSNotificationCenter defaultCenter] postNotification:notification];
}

#pragma mark - Private Methods

-(void) _sendRHMessageToWebSocket:(RHMessage*) message
{
    NSDictionary* jsonObject = message.toJSONObject;
    NSString* jsonString = message.toJSONString;
    if ([self isMessageNeedEncrypt])
    {
        jsonString = [CBSecurityUtils encryptByDESAndEncodeByBase64:jsonString key:MESSAGE_SECURITY_KEY];
        jsonObject = [NSDictionary dictionaryWithObject:jsonString forKey:MESSAGE_KEY_ENVELOPE];
    }
    else
    {
        jsonObject = [NSDictionary dictionaryWithObject:jsonObject forKey:MESSAGE_KEY_ENVELOPE];
    }

    jsonString = [CBJSONUtils toJSONString:jsonObject];
    [_webSocket send:jsonString];
}

//-(void) _sendJSONStringToWebSocket:(NSString*) jsonString
//{
//    NSDictionary* content = nil;
//    if ([self isMessageNeedEncrypt])
//    {
//        jsonString = [CBSecurityUtils encryptByDESAndEncodeByBase64:jsonString key:MESSAGE_SECURITY_KEY];
//    }
//
//    NSDictionary* dic = [NSDictionary dictionaryWithObject:jsonString forKey:MESSAGE_KEY_ENVELOPE];
//    jsonString = [CBJSONUtils toJSONString:dic];
//    
//    [_webSocket send:jsonString];
//}

-(void) _initInstance
{
    _openLock = [[NSCondition alloc] init];
    
    _messageLockSet = [NSMutableDictionary dictionary];
    _responseMessageSet = [NSMutableDictionary dictionary];
    
    _userDataModule = [UserDataModule sharedInstance];
}

- (void) _heartBeatPing
{
    if (nil != _webSocket && _webSocket.readyState == SR_OPEN)
    {
        NSData* data = [PING_TEXT dataUsingEncoding:NSASCIIStringEncoding];
        [_webSocket sendPing:data];
    }
}

-(NSCondition*) _newMessageLock:(NSString*) messageSn
{
    NSCondition* lock = nil;
    
    if (nil != messageSn && 0 < messageSn)
    {
        lock = [[NSCondition alloc] init];
        lock.name = messageSn;
        
        @synchronized(_messageLockSet)
        {
            [_messageLockSet setObject:lock forKey:messageSn];
        }
    }
    
    return lock;
}

-(NSCondition*) _getMessageLock:(NSString*) messageSn
{
    NSCondition* lock = nil;
    
    if (nil != messageSn && 0 < messageSn)
    {
        @synchronized(_messageLockSet)
        {
            lock = [_messageLockSet objectForKey:messageSn];
        }
    }
    
    return lock;
}

-(void) _releaseMessageLock:(NSCondition*) lock
{
    if (nil != lock)
    {
        NSString* lockName = lock.name;
        if (nil != lockName && 0 < lockName.length)
        {
            @synchronized(_messageLockSet)
            {
                [_messageLockSet removeObjectForKey:lockName];                
            }
        }
    }
}

-(void) _saveResponseMessage:(RHMessage*) message
{
    if (nil != message && nil != message.messageSn)
    {
        @synchronized(_responseMessageSet)
        {
            [_responseMessageSet setObject:message forKey:message.messageSn];            
        }
    }
}

-(RHMessage*) _getResponseMessage:(NSString*) messageSn
{
    RHMessage* message = nil;
    
    if (nil != messageSn && 0 < messageSn.length)
    {
        @synchronized(_responseMessageSet)
        {
            message = [_responseMessageSet objectForKey:messageSn];
            [_responseMessageSet removeObjectForKey:messageSn];
        }
    }
    
    return message;
}

@end
