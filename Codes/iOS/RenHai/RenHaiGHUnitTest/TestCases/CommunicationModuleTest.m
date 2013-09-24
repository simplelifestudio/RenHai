//
//  CommunicationModuleTest.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CommunicationModuleTest.h"

#import "SRWebSocket.h"

#import "CBStringUtils.h"

#import "CommunicationModule.h"
#import "WebSocketAgent.h"
#import "RHJSONMessage.h"

@interface CommunicationModuleTest()
{
    
}

@end

@implementation CommunicationModuleTest

-(void) testAlohaRequest
{    
    WebSocketAgent* webSocketAgent = [[WebSocketAgent alloc] init];
    [webSocketAgent connectWebSocket];
    NSLog(@"WebSocket opened.");
    sleep(1);
    
    dispatch_async(dispatch_queue_create("testQueue", DISPATCH_QUEUE_SERIAL), ^(){
        @try
        {
            RHJSONMessage* alohaReuqestMessage = [RHJSONMessage newAlohaRequestMessage];
            
            RHJSONMessage* responseMessage = [webSocketAgent syncMessage:alohaReuqestMessage syncInMainThread:NO];
            
            NSLog(@"Sent Message: %@", alohaReuqestMessage.toJSONString);            
            
            NSLog(@"Received Message: %@", responseMessage.toJSONString);
        }
        @catch (NSException *exception)
        {
            NSLog(@"Caught Exception: %@", exception.debugDescription);
        }
        @finally
        {
            sleep(16);
            [webSocketAgent closeWebSocket];
            NSLog(@"WebSocket closed.");
        }
    });
}


@end
