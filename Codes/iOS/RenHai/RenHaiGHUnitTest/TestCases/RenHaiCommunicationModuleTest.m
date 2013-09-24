//
//  RenHaiCommunicationModuleTest.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RenHaiCommunicationModuleTest.h"

#import "SRWebSocket.h"

#import "CBStringUtils.h"

#import "CommunicationModule.h"
#import "WebSocketAgent.h"
#import "RHJSONMessage.h"

@interface RenHaiCommunicationModuleTest()
{
    
}

@end

@implementation RenHaiCommunicationModuleTest

-(void) testRandomString
{
    NSString *randomStr = [CBStringUtils randomString:32];
    NSLog(@"random string is %@", randomStr);
}

-(void) testAlohaRequest
{    
    WebSocketAgent* webSocketAgent = [[WebSocketAgent alloc] init];
    [webSocketAgent connectWebSocket];
    
    sleep(1);
    
    dispatch_async(dispatch_queue_create("testQueue", DISPATCH_QUEUE_SERIAL), ^(){
        @try
        {
            RHJSONMessage* alohaReuqestMessage = [RHJSONMessage newAlohaRequestMessage];
            
            RHJSONMessage* responseMessage = [webSocketAgent syncMessage:alohaReuqestMessage syncInMainThread:NO];
            
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
        }
    });
}


@end
