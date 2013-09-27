//
//  CommunicationModuleTest.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CommunicationModuleTest.h"

#import "SRWebSocket.h"

#import "CBDateUtils.h"
#import "CBStringUtils.h"

#import "WebSocketAgent.h"
#import "RHJSONMessage.h"
#import "RHDevice.h"
#import "UserDataModule.h"

@interface CommunicationModuleTest()
{
    RHDevice* _device;
}

@end

@implementation CommunicationModuleTest

-(void) setUp
{
    _device = [[RHDevice alloc] init];
}

-(void) tearDown
{
    
}

-(void) testAlohaRequest
{    
    WebSocketAgent* agent = [self _connectWebSocket];
    
    dispatch_async(dispatch_queue_create("testQueue", DISPATCH_QUEUE_SERIAL), ^(){
        @try
        {
            RHJSONMessage* alohaReuqestMessage = [RHJSONMessage newAlohaRequestMessage:_device];
            
            RHJSONMessage* responseMessage = [agent syncMessage:alohaReuqestMessage syncInMainThread:NO];
            
            GHTestLog(@"Sent Message: %@", alohaReuqestMessage.toJSONString);            
            
            GHTestLog(@"Received Message: %@", responseMessage.toJSONString);
        }
        @catch (NSException *exception)
        {
            GHTestLog(@"Caught Exception: %@", exception.debugDescription);
        }
        @finally
        {
            sleep(16);
            [self _disconnectWebSocket:agent];
        }
    });
}

-(void) testAppDataSyncRequest_TotalSync
{
    RHJSONMessage* appDataSyncRequestMessage = [RHJSONMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:_device];
    
    GHTestLog(@"AppDataSyncRequestType_TotalSync: %@", appDataSyncRequestMessage.toJSONString);
}

-(void) testAppDataSyncRequest_DeviceCardSync
{
    RHJSONMessage* appDataSyncRequestMessage = [RHJSONMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_DeviceCardSync device:_device];
    
    GHTestLog(@"AppDataSyncRequest_DeviceCardSync: %@", appDataSyncRequestMessage.toJSONString);
}

-(void) testAppDataSyncRequest_ImpressCardSync
{
    RHJSONMessage* appDataSyncRequestMessage = [RHJSONMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_ImpressCardSync device:_device];
    
    GHTestLog(@"AppDataSyncRequest_ImpressCardSync: %@", appDataSyncRequestMessage.toJSONString);
}

-(void) testAppDataSyncRequest_InterestCardSync
{
    RHJSONMessage* appDataSyncRequestMessage = [RHJSONMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_InterestCardSync device:_device];
    
    GHTestLog(@"AppDataSyncRequest_InterestCardSync: %@", appDataSyncRequestMessage.toJSONString);
}

-(void) testServerDataSyncRequest_TotalSync
{
    RHJSONMessage* serverDataSyncRequestMessage = [RHJSONMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:_device info:nil];
    
    GHTestLog(@"ServerDataSyncRequest_TotalSync: %@", serverDataSyncRequestMessage.toJSONString);
}

-(void) testServerDataSyncRequest_DeviceCountSync
{
    RHJSONMessage* serverDataSyncRequestMessage = [RHJSONMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceCountSync device:_device info:nil];
    
    GHTestLog(@"ServerDataSyncRequest_DeviceCountSync: %@", serverDataSyncRequestMessage.toJSONString);
}

-(void) testServerDataSyncRequest_DeviceCapacitySync
{
    RHJSONMessage* serverDataSyncRequestMessage = [RHJSONMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceCapacitySync device:_device info:nil];
    
    GHTestLog(@"ServerDataSyncRequest_DeviceCapacitySync: %@", serverDataSyncRequestMessage.toJSONString);
}

-(void) testServerDataSyncRequest_InterestLabelListSync
{
    NSNumber* oCurrent = [NSNumber numberWithInt:10];
    NSDate* startTime = [NSDate date];
    NSString* sStartTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_FORMAT andDate:startTime];
    NSDate* endTime = [NSDate dateWithTimeIntervalSinceNow:60*60*24*7];
    NSString* sEndTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_FORMAT andDate:endTime];
    NSDictionary* info = [NSDictionary dictionaryWithObjects:@[oCurrent, sStartTime, sEndTime] forKeys:@[MESSAGE_KEY_CURRENT, MESSAGE_KEY_STARTTIME, MESSAGE_KEY_ENDTIME]];
    
    RHJSONMessage* serverDataSyncRequestMessage = [RHJSONMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_InterestLabelListSync device:_device info:info];
    
    GHTestLog(@"ServerDataSyncRequest_InterestLabelListSync: %@", serverDataSyncRequestMessage.toJSONString);
}

-(void) testAppDataSyncRequest
{
    WebSocketAgent* agent = [self _connectWebSocket];
    
    dispatch_async(dispatch_queue_create("testQueue", DISPATCH_QUEUE_SERIAL), ^(){
        @try
        {
            RHJSONMessage* deviceCardUpdateMessage = [self _createDeviceCardUpdateMessage];
            
            RHJSONMessage* responseMessage = [agent syncMessage:deviceCardUpdateMessage syncInMainThread:NO];
            
            GHTestLog(@"Sent Message: %@", deviceCardUpdateMessage.toJSONString);
            
            GHTestLog(@"Received Message: %@", responseMessage.toJSONString);
        }
        @catch (NSException *exception)
        {
            GHTestLog(@"Caught Exception: %@", exception.debugDescription);
        }
        @finally
        {
            sleep(16);
            [self _disconnectWebSocket:agent];
        }
    });
}

-(void) testRootDataTree
{
    RHDevice* device = [[RHDevice alloc] init];
    GHTestLog(@"device json string: %@", device.toJSONString);
}

#pragma mark - Private Methods

-(WebSocketAgent*) _connectWebSocket
{
    WebSocketAgent* webSocketAgent = [[WebSocketAgent alloc] init];
    [webSocketAgent connectWebSocket];
    GHTestLog(@"WebSocket opened.");
    
    sleep(1);
    
    return webSocketAgent;
}

-(void) _disconnectWebSocket:(WebSocketAgent*) webSocketAgent
{
    [webSocketAgent closeWebSocket];
    GHTestLog(@"WebSocket closed.");
}

-(RHJSONMessage*) _createDeviceCardUpdateMessage
{
    RHDevice* device = [[RHDevice alloc] init];
//    RHDeviceCard* deviceCard = device.deviceCard;
//    NSDictionary* deviceCardDic = deviceCard.toJSONObject;
    
    NSDictionary* body = [NSDictionary dictionaryWithObject:device.toJSONObject forKey:MESSAGE_KEY_DATAQUERY];
    
    NSString* messageSn = [RHJSONMessage generateMessageSn];
    NSDictionary* header = [RHJSONMessage constructMessageHeader:MessageType_AppRequest messageId:MessageId_AppDataSyncRequest messageSn:messageSn deviceId:device.deviceId deviceSn:device.deviceSn];
    RHJSONMessage* requestMessage = [RHJSONMessage constructWithMessageHeader:header messageBody:body enveloped:YES];
    
//    NSLog(@"device's json: %@", device.toJSONString);
    
    return requestMessage;
}


@end
