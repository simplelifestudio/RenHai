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
    
    NSString* remotePath = [BASEURL_WEBSOCKET_SERVER stringByAppendingString:REMOTEPATH_SERVICE_WEBSOCKET];
    
    _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:remotePath]]];
    _webSocket.delegate = self;

    NSTimeInterval timeout = WEBSOCKET_COMM_TIMEOUT;
    NSDate* startTimeStamp = [NSDate date];
    NSDate* endTimeStamp = [NSDate dateWithTimeInterval:timeout sinceDate:startTimeStamp];
    
    [_webSocket open];
    
    [_openLock lock];
    BOOL flag = [_openLock waitUntilDate:endTimeStamp];
    [_openLock unlock];
    
    return flag;
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
    NSString* jsonString = requestMessage.toJSONString;
    [self _sendJSONStringToWebSocket:jsonString];
}

#pragma mark - CBJSONMessageComm

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
    requestMessage.enveloped = YES;
    NSString* jsonString = requestMessage.toJSONString;
    [self _sendJSONStringToWebSocket:jsonString];
    
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
    
    [self startPing];
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error;
{
    DDLogWarn(@"Websocket Failed With Error: %@", error);
    
    [self closeWebSocket];
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message;
{
    DDLogInfo(@"WebSocket Received Message Uncrypted: \"%@\"", message);
    
    NSDictionary* dic = [CBJSONUtils toJSONObject:message];
    RHMessage* jsonMessage = [RHMessage constructWithContent:dic enveloped:YES];
    
    DDLogInfo(@"WebSocket Received Message Decrypted: \"%@\"", jsonMessage.toJSONString);
    
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
                NSNotification* notification = [NSNotification notificationWithName:NOTIFICATION_ID_RHSERVER object:jsonMessage userInfo:nil];
                [[NSNotificationCenter defaultCenter] postNotification:notification];
                
                break;
            }
            default:
            {
                NSAssert(NO, @"Received an unexpected message!");
                break;
            }
        }
    }
    else
    {
        DDLogError(@"Received an illegal message from server: %@", jsonMessage.toJSONString);
        
        RHMessage* message = [RHMessage newAppErrorResponseMessage:jsonMessage.messageSn];
        
        [self performSelectorInBackground:@selector(_sendJSONStringToWebSocket:) withObject:message.toJSONString];
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)data
{
//    NSString* str = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
//    DDLogInfo(@"WebSocket Received Pong \"%@\"", str);
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean;
{
    DDLogInfo(@"WebSocket closed with code: %d, reason: %@, wasClean: %@", code, reason, (wasClean) ? @"YES" : @"NO");
 
    [self closeWebSocket];
}

#pragma mark - Private Methods

-(void) _sendJSONStringToWebSocket:(NSString*) jsonString
{
    if ([RHMessage isMessageNeedEncrypt])
    {
        jsonString = [CBSecurityUtils encryptByDESAndEncodeByBase64:jsonString key:MESSAGE_SECURITY_KEY];
    }
    
    [_webSocket send:jsonString];
}

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
