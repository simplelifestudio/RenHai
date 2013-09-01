//
//  AppDelegate.m
//  RenHai
//
//  Created by Patrick Deng on 13-8-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "AppDelegate.h"

#import <Crashlytics/Crashlytics.h>

#import "LoggerModule.h"
#import "CommunicationModule.h"
#import "AppDataModule.h"
#import "UserDataModule.h"
#import "WebRTCModule.h"
#import "GUIModule.h"

@implementation AppDelegate

@synthesize moduleManager = _moduleManager;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    
    // Modules initialization
    _moduleManager = [CBModuleManager sharedInstance];
    
    id<CBModule> loggerModule = [LoggerModule sharedInstance];
    loggerModule.moduleWeightFactor = 0.1;
    [_moduleManager registerModule:loggerModule];

    id<CBModule> webRTCModule = [WebRTCModule sharedInstance];
    webRTCModule.moduleWeightFactor = 0.1;
    [_moduleManager registerModule:webRTCModule];
    
    id<CBModule> communicationModule = [CommunicationModule sharedInstance];
    communicationModule.moduleWeightFactor = 0.2;
    [_moduleManager registerModule:communicationModule];

    id<CBModule> appDataModule = [AppDataModule sharedInstance];
    appDataModule.moduleWeightFactor = 0.1;
    [_moduleManager registerModule:appDataModule];
    
    id<CBModule> userDataModule = [UserDataModule sharedInstance];
    userDataModule.moduleWeightFactor = 0.1;
    [_moduleManager registerModule:userDataModule];
    
    id<CBModule> guiModule = [GUIModule sharedInstance];
    guiModule.moduleWeightFactor = 0.4;
    [_moduleManager registerModule:guiModule];
    
    [_moduleManager initModules];
    
    DDLogVerbose(@"App Sandbox Path: %@", NSHomeDirectory());
    
    [Crashlytics startWithAPIKey:@"592220da47f22b9cdb4a9df47ea79170d94a150a"];
    
    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
