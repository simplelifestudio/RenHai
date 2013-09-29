//
//  CommunicationModuleTest.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CommunicationModuleTest.h"

#import "SRWebSocket.h"

#import "CBAppUtils.h"
#import "CBDateUtils.h"
#import "CBStringUtils.h"

#import "CommunicationModule.h"
#import "UserDataModule.h"

#define ASYNCOPERATION_WAIT_TIMEOUT WEBSOCKET_COMM_TIMEOUT*2

@interface CommunicationModuleTest()
{
    RHDevice* _device;
    WebSocketAgent* _agent;
}

@end

@implementation CommunicationModuleTest

#pragma mark - Framework Methods

-(BOOL) shouldRunOnMainThread
{
    return YES;
}

-(void) prepare
{
    [super prepare];
}

-(void) setUp
{
    _device = [[RHDevice alloc] init];
}

-(void) tearDown
{

}

-(void) failWithException:(NSException *)exception
{
    GHTestLog(@"Caught Exception: %@", exception.debugDescription);
}

#pragma mark - Test Methods

-(void) testAlohaRequest
{
    RHMessage* alohaReuqestMessage = [RHMessage newAlohaRequestMessage:_device];
    
    [self _sendMessageThroughWebSocket:alohaReuqestMessage selector:@selector(testAlohaRequest)];
}

-(void) testAppDataSyncRequest_TotalSync
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:_device];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSyncRequest_TotalSync)];
}

-(void) testAppDataSyncRequest_DeviceCardSync
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_DeviceCardSync device:_device];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSyncRequest_DeviceCardSync)];
}

-(void) testAppDataSyncRequest_ImpressCardSync
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_ImpressCardSync device:_device];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSyncRequest_ImpressCardSync)];
}

-(void) testAppDataSyncRequest_InterestCardSync
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_InterestCardSync device:_device];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSyncRequest_InterestCardSync)];
}

-(void) testServerDataSyncRequest_TotalSync
{
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSyncRequest_TotalSync)];
}

-(void) testServerDataSyncRequest_DeviceCountSync
{
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceCountSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSyncRequest_DeviceCountSync)];
}

-(void) testServerDataSyncRequest_DeviceCapacitySync
{
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceCapacitySync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSyncRequest_DeviceCapacitySync)];
}

-(void) testServerDataSyncRequest_InterestLabelListSync
{
    NSNumber* oCurrent = [NSNumber numberWithInt:10];
    NSDate* startTime = [NSDate date];
    NSString* sStartTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_FORMAT andDate:startTime];
    NSDate* endTime = [NSDate dateWithTimeIntervalSinceNow:60*60*24*7];
    NSString* sEndTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_FORMAT andDate:endTime];
    NSDictionary* info = [NSDictionary dictionaryWithObjects:@[oCurrent, sStartTime, sEndTime] forKeys:@[MESSAGE_KEY_CURRENT, MESSAGE_KEY_STARTTIME, MESSAGE_KEY_ENDTIME]];
    
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_InterestLabelListSync device:_device info:info];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testAlohaRequest)];
}

-(void) testBusinessSessionRequest
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Interest operationType:BusinessSessionRequestType_EnterPool device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testAlohaRequest)];
}

-(void) testBusinessSessionNotificationResponse
{
    NSString* businessSessionId = @"ASDFJL12DD";
    
    RHMessage* businessSessionNotificationResponseMessage = [RHMessage newBusinessSessionNotificationResponseMessage:businessSessionId businessType:BusinessType_Interest operationType:BusinessSessionNotificationType_OthersideAgreed operationValue:BusinessSessionOperationValue_Success device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionNotificationResponseMessage selector:@selector(testAlohaRequest)];
}

#pragma mark - Private Methods

-(BOOL) _connectWebSocket
{
    if (nil != _agent)
    {
        [_agent closeWebSocket];
        _agent = nil;
    }
    
    _agent = [[WebSocketAgent alloc] init];
    BOOL flag = [_agent openWebSocket];

    return flag;
}

-(void) _disconnectWebSocket
{
    if (nil != _agent)
    {
        [_agent closeWebSocket];
        
        GHTestLog(@"WebSocket Closed!");
    }
}

-(void) _sendMessageThroughWebSocket:(RHMessage*) requestMessage selector:(SEL) selector
{
    GHAssertNotNil(requestMessage, @"Request Message can not be null!");
    
    [self prepare];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        BOOL flag = [self _connectWebSocket];
        if (flag)
        {
            GHTestLog(@"WebSocket Opened Successfully!");
            GHTestLog(@"############################################");
            
            [requestMessage setTimeStamp:[NSDate date]];
            GHTestLog(@"Sent Message: %@", requestMessage.toJSONString);
            
            RHMessage* responseMessage = [_agent syncMessage:requestMessage syncInMainThread:NO];
            
            GHTestLog(@"############################################");
            GHTestLog(@"Received Message: %@", responseMessage.toJSONString);
            
            [self _disconnectWebSocket];
        }
        else
        {
            GHTestLog(@"############################################");
            GHTestLog(@"WebSocket Opened Fail!");
        }
        
        [self notify:kGHUnitWaitStatusSuccess forSelector:selector];
    });
    
    [self waitForStatus:kGHUnitWaitStatusSuccess timeout:ASYNCOPERATION_WAIT_TIMEOUT];
}

@end
