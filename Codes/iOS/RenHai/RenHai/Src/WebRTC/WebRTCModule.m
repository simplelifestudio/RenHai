//
//  WebRTCModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "WebRTCModule.h"

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
