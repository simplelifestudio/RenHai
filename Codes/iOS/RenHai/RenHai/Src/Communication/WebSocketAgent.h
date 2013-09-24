//
//  WebSocketAgent.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SRWebSocket.h"

#import "RHJSONMessage.h"
#import "CBJSONMessageComm.h"

#define SERVICE_TARGET_WEBSOCKET @"websocketService"

#define HEARTBEATPING_PERIOD 5.0

@interface WebSocketAgent : NSObject <SRWebSocketDelegate, CBJSONMessageComm>

-(void) connectWebSocket;
-(void) closeWebSocket;
-(RHJSONMessage*) syncMessage:(RHJSONMessage*) requestMessage;
-(RHJSONMessage*) syncMessage:(RHJSONMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread;
-(void) asyncMessage:(RHJSONMessage*) requestMessage;

@end
