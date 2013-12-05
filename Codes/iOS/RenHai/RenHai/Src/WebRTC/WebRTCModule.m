//
//  WebRTCModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "WebRTCModule.h"

#import "CommunicationModule.h"

@interface WebRTCModule()
{
    
}

@property (strong, nonatomic) OpenTokAgent *openTokAgent;

@end

@implementation WebRTCModule

@synthesize openTokAgent = _openTokAgent;

SINGLETON(WebRTCModule)

#pragma mark - Public Methods

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"WebRTC Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"WebRTC Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _openTokAgent = [[OpenTokAgent alloc] init];
}

-(void) releaseModule
{
    [self _unregisterNotifications];
    
    [super releaseModule];
}

-(void) startService
{
    DDLogInfo(@"Module:%@ is started.", self.moduleIdentity);
    
    [super startService];
    
    [self _registerNotifications];
}

-(void) processService
{
    MODULE_DELAY
}

-(void) connectAndPublishOnWebRTC:(NSString*) apiKey sessionId:(NSString*) sessionId token:(NSString*) token
{
    [_openTokAgent connectWithAPIKey:apiKey sessionId:sessionId token:token];
}

-(void) unpublishAndDisconnectOnWebRTC
{
    [_openTokAgent disconnect];
}

-(void) registerWebRTCDelegate:(id<OpenTokDelegate>) delegate
{
    _openTokAgent.openTokDelegate = delegate;
}

-(void) unregisterWebRTCDelegate
{
    _openTokAgent.openTokDelegate = nil;
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

#pragma mark - Private Methods

-(void) _registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil];
}

-(void) _onNotifications:(NSNotification*) notification
{
    if (nil != notification)
    {
        NSString* notificationName = notification.name;
        if ([notificationName isEqualToString:NOTIFICATION_ID_RHSERVERDISCONNECTED])
        {
            [self unpublishAndDisconnectOnWebRTC];
        }
    }
}

@end
