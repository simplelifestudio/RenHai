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
    
    [RHMessage setMessageNeedEncrypt:MESSAGE_NEED_ENCRYPT];
    
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
            
//            NSNotification* notification = [NSNotification notificationWithName:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil userInfo:nil];
//            [[NSNotificationCenter defaultCenter] postNotification:notification];
            
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
    DDLogVerbose(@"Module:%@ is started.", self.moduleIdentity);
    
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
    
//    [_webSocketCommAgent stopPing];
    
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

-(RHMessage*) sendMessage:(RHMessage*) requestMessage
{
    RHMessage* responseMessagge = nil;
    
    BOOL isConnected = [self isWebSocketConnected];
    if (isConnected)
    {        
        responseMessagge = [_webSocketCommAgent syncMessage:requestMessage syncInMainThread:NO];
    }
    
    return responseMessagge;
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
