//
//  WebSocketAgent.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "WebSocketAgent.h"

#import "AFHTTPClient.h"
#import "AFJSONRequestOperation.h"

#import "CommunicationModule.h"
#import "CBJSONUtils.h"

#define PING_TEXT @"#####RenHai-App-Ping#####"

@interface WebSocketAgent()
{
    SRWebSocket* _webSocket;
    NSTimer* _pingTimer;
    NSMutableDictionary* _messageLockSet;
    NSMutableDictionary* _responseMessageSet;
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

-(void) connectWebSocket
{
    [self closeWebSocket];
    
    NSString* remotePath = [BASEURL_WEBSOCKET_SERVER stringByAppendingString:REMOTEPATH_SERVICE_WEBSOCKET];
    
    _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:remotePath]]];
    _webSocket.delegate = self;
    
    [_webSocket open];
    
    [_pingTimer invalidate];
    _pingTimer = [NSTimer scheduledTimerWithTimeInterval:HEARTBEATPING_PERIOD target:self selector:@selector(_heartBeatPing) userInfo:nil repeats:YES];
}

-(void) closeWebSocket
{
    _webSocket.delegate = nil;
    [_webSocket close];
    _webSocket = nil;
}

-(RHJSONMessage*) syncMessage:(RHJSONMessage*) requestMessage
{
    RHJSONMessage* responseMessage = [self requestSync:SERVICE_TARGET_WEBSOCKET requestMessage:requestMessage];
    
    return responseMessage;
}

-(void) asyncMessage:(RHJSONMessage*) requestMessage
{
    NSString* jsonString = [CBJSONUtils toJSONString:requestMessage.toJSONObject];
    [_webSocket send:jsonString];
}

#pragma mark - CBJSONMessageComm

// Warning: This method CAN NOT be invoked in Main Thread!
-(RHJSONMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHJSONMessage*) requestMessage
{
    if ([[NSThread currentThread] isMainThread])
    {
        DDLogWarn(@"Warning: This method CAN NOT be invoked in Main Thread!");
        return nil;
    }
    
    NSTimeInterval timeout = WEBSOCKET_COMM_TIMEOUT;
    NSDate* startTimeStamp = [NSDate date];
    NSDate* endTimeStamp = [NSDate dateWithTimeInterval:timeout sinceDate:startTimeStamp];
    
    RHJSONMessage* responseMessage = nil;
    
    NSString* messageSn = requestMessage.messageSn;
    NSCondition* _messageLock = [self _newMessageLock:messageSn];
    
    NSString* jsonString = requestMessage.toJSONString;
    [_webSocket send:jsonString];
    
    [_messageLock lock];
    BOOL flag = [_messageLock waitUntilDate:endTimeStamp];
    [_messageLock unlock];
    [self _releaseMessageLock:_messageLock];
    
    if (!flag)
    {
        responseMessage = [RHJSONMessage newServerTimeoutResponseMessage];
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
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error;
{
    DDLogWarn(@"Websocket Failed With Error: %@", error);
    
    [self closeWebSocket];
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message;
{
    DDLogInfo(@"WebSocket Received Message: \"%@\"", message);
    
    RHJSONMessage* jsonMessage = [RHJSONMessage constructWithString:message];
    NSString* messageSn = jsonMessage.messageSn;
    
    switch (jsonMessage.messageType)
    {
        case ServerResponseMessage:
        {
            [self _saveResponseMessage:jsonMessage];
            
            NSCondition* _messageLock = [self _getMessageLock:messageSn];
            [_messageLock lock];
            [_messageLock signal];
            [_messageLock unlock];
            
            break;
        }
        case ServerNotificationMessage:
        {
#warning TODO:
            break;
        }
        case UnknownMessage:
        {
#warning TODO:
            break;
        }
        default:
        {
#warning TODO:
            break;
        }
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)data
{
    NSString* str = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    DDLogInfo(@"WebSocket Received Pong \"%@\"", str);
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean;
{
    DDLogInfo(@"WebSocket Closed");
 
    [self closeWebSocket];
}

#pragma mark - Private Methods

-(void) _initInstance
{
    _messageLockSet = [NSMutableDictionary dictionary];
    _responseMessageSet = [NSMutableDictionary dictionary];
    
    [self connectWebSocket];
}

- (void) _heartBeatPing
{
    if (nil != _webSocket)
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

-(void) _saveResponseMessage:(RHJSONMessage*) message
{
    if (nil != message && nil != message.messageSn && 0 < message.messageSn.length)
    {
        @synchronized(_responseMessageSet)
        {
            [_responseMessageSet setObject:message forKey:message.messageSn];            
        }
    }
}

-(RHJSONMessage*) _getResponseMessage:(NSString*) messageSn
{
    RHJSONMessage* message = nil;
    
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
