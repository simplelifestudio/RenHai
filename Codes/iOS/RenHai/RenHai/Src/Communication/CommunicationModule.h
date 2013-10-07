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

#define WEBSOCKET_COMM_TIMEOUT 9
#define HTTP_COMM_TIMEOUT 7.0

#define NOTIFICATION_ID_RHSERVER @"RHServerNotification"

#define MESSAGE_NEED_ENCRYPT 0

@interface CommunicationModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (nonatomic, strong) HTTPAgent* httpCommAgent;
@property (nonatomic, strong) WebSocketAgent* webSocketCommAgent;

-(BOOL) connectWebSocket;
-(void) disconnectWebSocket;
-(BOOL) isWebSocketConnected;

-(RHMessage*) sendMessage:(RHMessage*) message;

@end
