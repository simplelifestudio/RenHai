//
//  CommunicationModule.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

#import "CBSharedInstance.h"

#import "HTTPAgent.h"
#import "WebSocketAgent.h"

#define BASEURL_HTTP_SERVER @"http://192.81.135.31/RenHai/"
#define REMOTEPATH_SERVICE_HTTP @"httpService"

#define BASEURL_WEBSOCKET_SERVER @"ws://192.81.135.31/renhai/"
#define REMOTEPATH_SERVICE_WEBSOCKET @"websocket"

#define WEBSOCKET_OPEN_TIMEOUT 4.5f
#define WEBSOCKET_COMM_TIMEOUT 9.0f
#define HTTP_COMM_TIMEOUT 9.0f

#define MESSAGE_NEED_ENCRYPT 0

#define NOTIFICATION_ID_RHSERVERNOTIFICATION @"RHServerNotification"
#define NOTIFICATION_ID_RHSERVERDISCONNECTED @"RHServerDisconnected"

#define NOTIFICATION_ID_SESSIONBOUND @"RHServerSessionBound"
#define NOTIFICATION_ID_OTHERSIDEAGREED @"RHServerSessionOtherAgreed"
#define NOTIFICATION_ID_OTHERSIDEREJECTED @"RHServerSessionOtherRejected"
#define NOTIFICATION_ID_OTHERSIDELOST @"RHServerSessionOthersideLost"
#define NOTIFICATION_ID_OTHERSIDEENDCHAT @"RHServerSessionOthersideEndChat"

@interface CommunicationModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (nonatomic, strong) HTTPAgent* httpCommAgent;
@property (nonatomic, strong) WebSocketAgent* webSocketCommAgent;

-(BOOL) connectWebSocket;
-(void) disconnectWebSocket;
-(BOOL) isWebSocketConnected;

-(RHMessage*) syncSendWebSocketMessage:(RHMessage*) message;
-(void) asyncSendWebSocketMessage:(RHMessage*) message;

-(RHMessage*) syncSendHttpMessage:(RHMessage *)message;

-(void)proxyDataSyncRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)(NSDictionary* deviceDic)) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock;

-(void)alohaRequest:(RHDevice*) device;

-(void)appDataSyncRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)(NSDictionary* deviceDic)) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock;

-(void)serverDataSyncRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)(NSDictionary* serverDic)) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock;

-(void)businessSessionRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)()) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock;

@end
