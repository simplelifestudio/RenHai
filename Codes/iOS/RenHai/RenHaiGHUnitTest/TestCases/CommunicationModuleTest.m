//
//  CommunicationModuleTest.m
//  RenHai
//
//  Created by DENG KE on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CommunicationModuleTest.h"

#import "SRWebSocket.h"

#import "CBAppUtils.h"
#import "CBDateUtils.h"
#import "CBStringUtils.h"

#import "CommunicationModule.h"
#import "UserDataModule.h"
#import "LoggerModule.h"

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
    return NO;
}

-(void) setUpClass
{
    _device = [[RHDevice alloc] init];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onMessage:) name:NOTIFICATION_ID_RHSERVER object:nil];
}

-(void) tearDownClass
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void) setUp
{

}

-(void) tearDown
{

}

-(void) failWithException:(NSException *)exception
{
    GHTestLog(@"Caught Exception: %@", exception.debugDescription);
}

#pragma mark - Test Methods

-(void) testAloha
{
    RHMessage* alohaReuqestMessage = [RHMessage newAlohaRequestMessage:_device];
    
    [self _sendMessageThroughWebSocket:alohaReuqestMessage selector:@selector(testAloha)];
}

-(void) testAppDataSync_Total
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSync_Total)];
}

-(void) testAppDataSync_DeviceCard
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_DeviceCardSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSync_DeviceCard)];
}

-(void) testAppDataSync_ImpressCard
{
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_ImpressCardSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSync_ImpressCard)];
}

-(void) testAppDataSync_InterestCard
{
    NSDictionary* info = [NSDictionary dictionaryWithObjects:@[@"Topic6", @"Topic7", @"Topic8"] forKeys:@[@"Topic6", @"Topic7", @"Topic8"]];
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_InterestCardSync device:_device info:info];
    
    [self _sendMessageThroughWebSocket:appDataSyncRequestMessage selector:@selector(testAppDataSync_InterestCard)];
}

-(void) testServerDataSync_Total
{
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSync_Total)];
}

-(void) testServerDataSync_DeviceCount
{
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceCountSync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSync_DeviceCount)];
}

-(void) testServerDataSync_DeviceCapacity
{
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceCapacitySync device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSync_DeviceCapacity)];
}

-(void) testServerDataSync_InterestLabelList
{
    NSNumber* oCurrent = [NSNumber numberWithInt:10];
    NSDate* startTime = [NSDate date];
    NSString* sStartTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_FORMAT andDate:startTime];
    NSDate* endTime = [NSDate dateWithTimeIntervalSinceNow:60*60*24*7];
    NSString* sEndTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_FORMAT andDate:endTime];
    NSDictionary* info = [NSDictionary dictionaryWithObjects:@[oCurrent, sStartTime, sEndTime] forKeys:@[MESSAGE_KEY_CURRENT, MESSAGE_KEY_STARTTIME, MESSAGE_KEY_ENDTIME]];
    
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_InterestLabelListSync device:_device info:info];
    
    [self _sendMessageThroughWebSocket:serverDataSyncRequestMessage selector:@selector(testServerDataSync_InterestLabelList)];
}

-(void) testBusinessSession_EnterPool
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_EnterPool device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_EnterPool)];
}

-(void) testBusinessSession_LeavePool
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_LeavePool device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_LeavePool)];
}

-(void) testBusinessSession_AgreeChat
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_AgreeChat device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_AgreeChat)];
}

-(void) testBusinessSession_EndChat
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_EndChat device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_EndChat)];
}

-(void) testBusinessSession_AssessAndContinue
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_AssessAndContinue device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_AssessAndContinue)];
}

-(void) testBusinessSession_AssessAndQuit
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_AssessAndQuit device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_AssessAndQuit)];
}

-(void) testBusinessSession_RejectChat
{
    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_RejectChat device:_device info:nil];
    
    [self _sendMessageThroughWebSocket:businessSessionRequestMessage selector:@selector(testBusinessSession_RejectChat)];
}

-(void) _responseBusinessSessionNotification:(RHMessage*) businessSessionNotification
{
    NSDictionary* body = businessSessionNotification.body;
    
    NSString* businessSessionId = [body objectForKey:MESSAGE_KEY_BUSINESSSESSIONID];
    RHBusinessType businessType = [((NSNumber*)[body objectForKey:MESSAGE_KEY_BUSINESSTYPE]) intValue];
    BusinessSessionNotificationType operationType = [((NSNumber*)[body objectForKey:MESSAGE_KEY_OPERATIONTYPE]) intValue];
    NSDictionary* operationInfo = [body objectForKey:MESSAGE_KEY_OPERATIONINFO];
    
    RHMessage* businessSessionNotificationResponseMessage = [RHMessage newBusinessSessionNotificationResponseMessage:businessSessionId businessType:businessType operationType:operationType operationValue:BusinessSessionOperationValue_Success device:_device info:operationInfo];
    
    [self _sendMessageThroughWebSocket:businessSessionNotificationResponseMessage selector:nil];
}

-(void) _responseBroadcastNotification:(RHMessage*) broadcastNotification
{
    NSDictionary* body = broadcastNotification.body;
    NSString* content = [body objectForKey:MESSAGE_KEY_CONTENT];
    
    DDLogWarn(@"Received server broadcast: %@", content);
}

-(void) testConnect
{
    [self prepare];
    
    __block BOOL flag = NO;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        flag = [self _connectWebSocket];
        
        [self notify:kGHUnitWaitStatusSuccess forSelector:@selector(testConnect)];
        
        if (flag)
        {
            GHTestLog(@"WebSocket Open Successfully!");
        }
    });
    
    [self waitForStatus:kGHUnitWaitStatusSuccess timeout:ASYNCOPERATION_WAIT_TIMEOUT];
    
    GHAssertTrue(flag, @"WebSocket Open Failed!");
}

-(void) testDisconnect
{
    [self prepare];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        [self _disconnectWebSocket];
        
        [self notify:kGHUnitWaitStatusSuccess forSelector:@selector(testDisconnect)];
        
        GHTestLog(@"WebSocket Close Successfully!");
    });
    
    [self waitForStatus:kGHUnitWaitStatusSuccess timeout:ASYNCOPERATION_WAIT_TIMEOUT];
}

-(void) testStopPing
{
    if (_agent.webSocketState == SR_OPEN)
    {
        GHTestLog(@"WebSocket stops Ping!");
        [_agent stopPing];
    }
    else
    {
        GHAssertTrue(NO, @"WebSocket does not open!");
    }
}

-(void) testStartPing
{
    if (_agent.webSocketState == SR_OPEN)
    {
        GHTestLog(@"WebSocket starts Ping!");
        [_agent startPing];
    }
    else
    {
        GHAssertTrue(NO, @"WebSocket does not open!");
    }
}

#pragma mark - Private Methods

-(void) _onMessage:(NSNotification*) notification
{
    if (nil != notification)
    {
        if ([notification.name isEqualToString:NOTIFICATION_ID_RHSERVER])
        {
            GHTestLog(@"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            RHMessage* message = (RHMessage*)notification.object;
            GHTestLog(@"Received Notification: %@", message.toJSONString);
            
            switch (message.messageId)
            {
                case MessageId_BusinessSessionNotification:
                {
                    [self _responseBusinessSessionNotification:message];
                    
                    break;
                }
                case MessageId_BroadcastNotification:
                {
                    [self _responseBroadcastNotification:message];
                    
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }
}

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
    }
}

-(void) _sendMessageThroughWebSocket:(RHMessage*) requestMessage selector:(SEL) selector
{
    GHAssertNotNil(requestMessage, @"Request message can not be null!");
    
    if (nil != selector)
    {
        [self prepare];
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        if (_agent.webSocketState == SR_OPEN)
        {
            GHTestLog(@"Sent Message: %@", requestMessage.toJSONString);
            
            RHMessage* responseMessage = [_agent syncMessage:requestMessage syncInMainThread:NO];
            
            GHTestLog(@"############################################");
            GHTestLog(@"Received Message: %@", responseMessage.toJSONString);
        }
        else
        {
            GHAssertTrue(NO, @"WebSocket does not open!");
        }
        
        if (nil != selector)
        {
            [self notify:kGHUnitWaitStatusSuccess forSelector:selector];
        }
    });
    
    if (nil != selector)
    {
        [self waitForStatus:kGHUnitWaitStatusSuccess timeout:ASYNCOPERATION_WAIT_TIMEOUT];
    }
}

@end
