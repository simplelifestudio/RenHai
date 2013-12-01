//
//  CommunicationModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CommunicationModule.h"

#import "Reachability.h"

#import "GUIModule.h"

#define REACHABILITY_HOST @"www.apple.com"
#define  _HUD_DISPLAY 1

@interface CommunicationModule()
{

}

@end

@implementation CommunicationModule

SINGLETON(CommunicationModule)

@synthesize httpCommAgent = _httpCommAgent;
@synthesize webSocketCommAgent = _webSocketCommAgent;

#pragma mark - Public Methods

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"Communication Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"Communication Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _httpCommAgent = [[HTTPAgent alloc] init];
    _webSocketCommAgent = [[WebSocketAgent alloc] init];
}

- (void)registerReachability
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reachabilityChanged:)
                                                 name:kReachabilityChangedNotification
                                               object:nil];
    
    Reachability* hostReach = [Reachability reachabilityWithHostname:REACHABILITY_HOST];
    [hostReach startNotifier];    
}

- (void) unregisterReachability
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)reachabilityChanged:(NSNotification *)note
{
    Reachability* curReach = [note object];
    NSParameterAssert([curReach isKindOfClass: [Reachability class]]);
    NetworkStatus status = [curReach currentReachabilityStatus];
    [curReach connectionRequired];

//    GUIModule* guiModule = [GUIModule sharedInstance];
    
    switch (status)
    {
        case NotReachable:
        {
//            [guiModule showHUD:NSLocalizedString(@"Communication_InternetDisconnected", nil) delay:_HUD_DISPLAY];
            
            DDLogWarn(@"App's reachability changed to 'NotReachable'.");
            break;
        }
        case ReachableViaWiFi:
        {
//            [guiModule showHUD:NSLocalizedString(@"Communication_WiFiConnected", nil) delay:_HUD_DISPLAY];
            
            DDLogWarn(@"App's reachability changed to 'ReachableViaWiFi'.");
            break;
        }
        case ReachableViaWWAN:
        {
//            [guiModule showHUD:NSLocalizedString(@"Communication_3G/GPRSConnected", nil) delay:_HUD_DISPLAY];
            
            DDLogWarn(@"App's reachability changed to 'ReachableViaWWAN'.");
            break;
        }
        default:
        {
            break;
        }
    }
}

-(void) releaseModule
{
    [self unregisterReachability];
    
    [self disconnectWebSocket];
    
    [super releaseModule];
}

-(void) startService
{
    DDLogInfo(@"Module:%@ is started.", self.moduleIdentity);
    
    [super startService];
}

-(void) processService
{
    MODULE_DELAY
    
    [self registerReachability];
}

-(BOOL) connectWebSocket
{
    BOOL flag = NO;
    
    if (![self isWebSocketConnected])
    {
        flag = [_webSocketCommAgent openWebSocket];
    }
    else
    {
        flag = (_webSocketCommAgent.webSocketState == SR_OPEN);
    }
    
    return flag;
}

-(void) disconnectWebSocket
{
    if ([self isWebSocketConnected])
    {
        [_webSocketCommAgent closeWebSocket];
    }
}

-(BOOL) isWebSocketConnected
{
    BOOL flag = NO;
    
    if (nil != _webSocketCommAgent && _webSocketCommAgent.webSocketState == SR_OPEN)
    {
        flag = YES;
    }
    
    return flag;
}

-(RHMessage*) syncSendWebSocketMessage:(RHMessage*) requestMessage
{
    RHMessage* responseMessagge = nil;
    
    BOOL isConnected = [self isWebSocketConnected];
    if (isConnected)
    {        
        responseMessagge = [_webSocketCommAgent syncMessage:requestMessage syncInMainThread:NO];
    }
    
    return responseMessagge;
}

-(void) asyncSendWebSocketMessage:(RHMessage*) message
{
    BOOL isConnected = [self isWebSocketConnected];
    if (isConnected)
    {
        [_webSocketCommAgent asyncMessage:message];
    }
}

-(RHMessage*) syncSendHttpMessage:(RHMessage *)message
{
    RHMessage* responseMessage = [_httpCommAgent syncMessage:message];
    
    return responseMessage;
}

-(void)proxyDataSyncRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)(NSDictionary* dic)) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock
{
    BOOL isSuccess = NO;
    
    RHMessage* responseMessage = [self syncSendHttpMessage:requestMessage];
    
    NSDictionary* proxyDic = nil;
    
    if (responseMessage.messageId == MessageId_ProxyDataSyncResponse)
    {
        NSDictionary* messageBody = responseMessage.body;
        proxyDic = messageBody;
        
        isSuccess = YES;
    }
    else if (responseMessage.messageId == MessageId_ServerErrorResponse)
    {
        
    }
    else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
    {
        
    }
    else
    {
        
    }
    
    if (isSuccess)
    {
        if (nil != successCompletionBlock)
        {
            successCompletionBlock(proxyDic);
        }
    }
    else
    {
        if (nil != failureCompletionBlock)
        {
            failureCompletionBlock();
        }
    }
    
    if (nil != afterCompletionBlock)
    {
        afterCompletionBlock();
    }
}

-(void)alohaRequest:(RHDevice*) device
{
    RHMessage* requestMessage = [RHMessage newAlohaRequestMessage:device];
    [self asyncSendWebSocketMessage:requestMessage];
}

-(void)appDataSyncRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)(NSDictionary* dic)) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock
{
    BOOL isSuccess = NO;
    
    RHMessage* responseMessage = [self syncSendWebSocketMessage:requestMessage];
    
    NSDictionary* deviceDic = nil;
    
    if (responseMessage.messageId == MessageId_AppDataSyncResponse)
    {
        NSDictionary* messageBody = responseMessage.body;
        deviceDic = [messageBody objectForKey:MESSAGE_KEY_DATAQUERY];
        
        isSuccess = YES;
    }
    else if (responseMessage.messageId == MessageId_ServerErrorResponse)
    {
        
    }
    else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
    {
        
    }
    else
    {
        
    }
    
    if (isSuccess)
    {
        if (nil != successCompletionBlock)
        {
            successCompletionBlock(deviceDic);
        }
    }
    else
    {
        if (nil != failureCompletionBlock)
        {
            failureCompletionBlock();
        }
    }
    
    if (nil != afterCompletionBlock)
    {
        afterCompletionBlock();
    }
}

-(void)serverDataSyncRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)(NSDictionary* dic)) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock
{
    BOOL isSuccess = NO;
    
    RHMessage* responseMessage = [self syncSendWebSocketMessage:requestMessage];
    
    NSDictionary* serverDic = nil;
    
    if (responseMessage.messageId == MessageId_ServerDataSyncResponse)
    {
        NSDictionary* messageBody = responseMessage.body;
        serverDic = messageBody;
        
        isSuccess = YES;
    }
    else if (responseMessage.messageId == MessageId_ServerErrorResponse)
    {
        
    }
    else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
    {
        
    }
    else
    {
        
    }
    
    if (isSuccess)
    {
        if (nil != successCompletionBlock)
        {
            successCompletionBlock(serverDic);
        }
    }
    else
    {
        if (nil != failureCompletionBlock)
        {
            failureCompletionBlock();
        }
    }
    
    if (nil != afterCompletionBlock)
    {
        afterCompletionBlock();
    }
}

-(void)businessSessionRequest:(RHMessage*) requestMessage successCompletionBlock:(void(^)()) successCompletionBlock failureCompletionBlock:(void(^)()) failureCompletionBlock afterCompletionBlock:(void(^)()) afterCompletionBlock
{
    BOOL isSuccess = NO;
    
    RHMessage* responseMessage = [self syncSendWebSocketMessage:requestMessage];
    
    if (responseMessage.messageId == MessageId_BusinessSessionResponse)
    {
        NSDictionary* messageBody = responseMessage.body;
        NSDictionary* businessSessionDic = messageBody;
        
        @try
        {
            NSNumber* oOperationValue = [businessSessionDic objectForKey:MESSAGE_KEY_OPERATIONVALUE];
            BusinessSessionOperationValue operationValue = oOperationValue.intValue;
            
            if (operationValue == BusinessSessionOperationValue_Success)
            {
                isSuccess = YES;
            }
            else
            {
                
            }
        }
        @catch (NSException *exception)
        {
            DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
        }
        @finally
        {
            
        }
    }
    else if (responseMessage.messageId == MessageId_ServerErrorResponse)
    {
        
    }
    else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
    {
        
    }
    else
    {
        
    }
    
    if (isSuccess)
    {
        if (nil != successCompletionBlock)
        {
            successCompletionBlock();
        }
    }
    else
    {
        if (nil != failureCompletionBlock)
        {
            failureCompletionBlock();
        }
    }
    
    if (nil != afterCompletionBlock)
    {
        afterCompletionBlock();
    }
}

#pragma mark - UIApplicationDelegate

-(void)applicationWillResignActive:(UIApplication *)application
{
    
}

-(void)applicationDidEnterBackground:(UIApplication *)application
{
    
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    
}

-(void)applicationWillEnterForeground:(UIApplication *)application
{
    
}

@end
