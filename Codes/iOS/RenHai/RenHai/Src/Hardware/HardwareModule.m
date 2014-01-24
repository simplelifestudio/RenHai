//
//  HardwareModule.m
//  RenHai
//
//  Created by DENG KE on 14-1-23.
//  Copyright (c) 2014年 Simplelife Studio. All rights reserved.
//

#import "HardwareModule.h"

#import "CommunicationModule.h"

@interface HardwareModule()
{
    
}

@end

@implementation HardwareModule

SINGLETON(HardwareModule)

#pragma mark - Public Methods

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"Hardware Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"Hardware Module Thread", nil)];
    [self setKeepAlive:FALSE];
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

-(void) enableProximitySensor
{
    [[UIDevice currentDevice] setProximityMonitoringEnabled:YES];
}

-(void) disableProximitySensor
{
    [[UIDevice currentDevice] setProximityMonitoringEnabled:NO];
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
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_onNotifications:)
                                                 name:NOTIFICATION_ID_PROXIMITYSTATEDIDCHANGE
                                               object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NOTIFICATION_ID_PROXIMITYSTATEDIDCHANGE object:nil];
}

-(void) _onNotifications:(NSNotification*) notification
{
    if (nil != notification)
    {
        NSString* notificationName = notification.name;
        if ([notificationName isEqualToString:NOTIFICATION_ID_PROXIMITYSTATEDIDCHANGE])
        {
            [self _proximitySensorStateChange];
        }
    }
}

-(void)_proximitySensorStateChange
{
    if ([[UIDevice currentDevice] proximityState] == YES)
    {
        DDLogInfo(@"Device is close to user");
        
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_FRONTCAMERAINVALID object:nil];
    }
    else
    {
        DDLogInfo(@"Device is not close to user");
        
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_FRONTCAMERAVALID object:nil];
    }
}

@end
