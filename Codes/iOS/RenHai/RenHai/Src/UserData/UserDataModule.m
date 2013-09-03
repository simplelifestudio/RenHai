//
//  UserDataModule.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserDataModule.h"

#import "CBFileUtils.h"

#import "RHDevice.h"
#import "RHDeviceCard.h"
#import "RHInterestCard.h"
#import "RHInterestLabel.h"
#import "RHImpressCard.h"
#import "RHImpressLabel.h"

#define ARCHIVE_FILE_NAME @"device.dat"

@interface UserDataModule()
{
    NSString* _dataDir;
}

@end

@implementation UserDataModule

SINGLETON(UserDataModule)

@synthesize device = _device;

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"UserData Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"UserData Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    [CBFileUtils createDirectory:self.dataDirectory];
    DDLogVerbose(@"App Sandbox Path: %@", NSHomeDirectory());    
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

#pragma mark - Public Methods

-(NSString*) dataDirectory
{
    if (nil == _dataDir)
    {
        NSString* rootDir = NSHomeDirectory();
        NSString* docDir = [rootDir stringByAppendingPathComponent:@"Documents"];
        _dataDir = [docDir stringByAppendingPathComponent:@"Data"];
    }
    
    return _dataDir;
}

-(BOOL) saveUserData
{
    BOOL flag = NO;
    
    NSString* file = [self.dataDirectory stringByAppendingPathComponent:ARCHIVE_FILE_NAME];
    
    flag = [NSKeyedArchiver archiveRootObject:_device toFile:file];
    
    return flag;
}

-(BOOL) loadUserData
{
    BOOL flag = NO;
    
    NSString* file = [self.dataDirectory stringByAppendingPathComponent:ARCHIVE_FILE_NAME];
    
    _device = [NSKeyedUnarchiver unarchiveObjectWithFile:file];
    
    flag = (nil != _device) ? YES : NO;
    
    return flag;
}

-(void) initUserData
{
    _device = [[RHDevice alloc] init];
}

-(void) updateUserData
{
    
}

-(void) syncUserData
{
    
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
