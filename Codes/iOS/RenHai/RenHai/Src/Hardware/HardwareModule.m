//
//  HardwareModule.m
//  RenHai
//
//  Created by DENG KE on 14-1-23.
//  Copyright (c) 2014年 Simplelife Studio. All rights reserved.
//

#import "HardwareModule.h"

#import "CommunicationModule.h"
//#import "CoreLocation.h"

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
//    NSBundle *b = [NSBundle bundleWithPath:@"/System/Library/Frameworks/CoreLocation.framework"];
//    BOOL success = [b load];
//    Class CLLocationManager = NSClassFromString(@"CLLocationManager");
//    SEL setAuthorizationStatus = NSSelectorFromString(@"setAuthorizationStatus");
////+ (void)setAuthorizationStatus:(_Bool)arg1 forBundleIdentifier:(id)arg2;
////    [CCLocationManager setAuthorizationStatus:YES forBundleIdentifier:[NSBundle mainBundle]];
//    
    
//    while (1){
//        CLLocationManager *locationManager = [[CLLocationManager alloc] init];
//        locationManager.desiredAccuracy = kCLLocationAccuracyBest;
//        [locationManager startUpdatingLocation];
//        
//        CLLocation *location = locationManager.location;
//        
//        if ([CLLocationManager authorizationStatus] != kCLAuthorizationStatusAuthorized)
//        {
//            NSLog(@"%i", [CLLocationManager authorizationStatus]);
//            if( [CLLocationManager authorizationStatus] == 2)
//            {
//                system("ps ax | grep locationd");
//                system("launchctl unload /System/Library/LaunchDaemons/com.apple.locationd.plist");
//                NSString* plistPath = @"/var/root/Library/Caches/locationd/clients.plist";
//                NSMutableDictionary* plist = [NSMutableDictionary dictionaryWithContentsOfFile: plistPath];
//                NSMutableDictionary *myBundle = [plist objectForKey:[[NSBundle mainBundle] bundleIdentifier]];
//                [myBundle removeObjectForKey:@"TimeMissing"];
//                NSLog(@"Relaunching");
//                [plist setObject:myBundle forKey:[[NSBundle mainBundle] bundleIdentifier]];
//                [plist writeToFile:@"/var/root/Library/Caches/locationd/clients.plist" atomically:YES];
//                system("launchctl load /System/Library/LaunchDaemons/com.apple.locationd.plist");
//            }
//            
//        }
//        
//        NSLog(@"Coordinates are %f %f %f %f",
//              location.coordinate.latitude,
//              location.coordinate.longitude,
//              location.horizontalAccuracy,
//              location.verticalAccuracy);
//        
//        if ( location.coordinate.latitude == 0 && location.coordinate.longitude == 0)
//        {
//            NSLog(@"Nulss");
//            system("launchctl unload /System/Library/LaunchDaemons/com.apple.locationd.plist");
//            system("launchctl load /System/Library/LaunchDaemons/com.apple.locationd.plist");
//        }
//        else
//        {
//            NSLog(@"Got IT");
//            [locationManager stopUpdatingLocation];
//            break;
//        }
//        sleep(10);
//    }
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
