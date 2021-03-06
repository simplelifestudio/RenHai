//
//  WebSocketAgent.h
//  RenHai
//
//  Created by DENG KE on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SRWebSocket.h"

#import "RHMessage.h"
#import "CBJSONMessageComm.h"

#define SERVICE_TARGET_WEBSOCKET @"websocketService"

#define HEARTBEATPING_PERIOD 4.0f

#define NOTIFICATION_PREFIX_MESSAGEID @"MessageId_"

@interface WebSocketAgent : NSObject <SRWebSocketDelegate, CBJSONMessageComm>

-(BOOL) openWebSocket;
-(void) closeWebSocket;

-(void) startPing;
-(void) stopPing;

-(SRReadyState) webSocketState;

-(RHMessage*) syncMessage:(RHMessage*) requestMessage;
-(RHMessage*) syncMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread;
-(void) asyncMessage:(RHMessage*) requestMessage;

@end
