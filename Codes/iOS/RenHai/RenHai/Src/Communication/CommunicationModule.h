//
//  CommunicationModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

#import "CBSharedInstance.h"

#import "HTTPAgent.h"
#import "WebSocketAgent.h"

#define BASEURL_HTTP_SERVER @"http://192.81.135.31/RenHai/"
#define REMOTEPATH_SERVICE_HTTP @"httpService"

#define BASEURL_WEBSOCKET_SERVER @"ws://192.81.135.31/RenHai/"
#define REMOTEPATH_SERVICE_WEBSOCKET @"websocketService"

#define WEBSOCKET_COMM_TIMEOUT 5.0
#define HTTP_COMM_TIMEOUT 7.0

@interface CommunicationModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (nonatomic, strong) HTTPAgent* httpCommAgent;
@property (nonatomic, strong) WebSocketAgent* webSocketCommAgent;

@end
