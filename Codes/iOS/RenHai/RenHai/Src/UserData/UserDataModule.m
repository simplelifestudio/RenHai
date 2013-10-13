//
//  UserDataModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
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

#define ARCHIVE_DEVICE_NAME @"device.dat"
#define ARCHIVE_SERVER_NAME @"server.dat"

@interface UserDataModule()
{
    NSString* _dataDir;
}

@end

@implementation UserDataModule

SINGLETON(UserDataModule)

@synthesize device = _device;
@synthesize server = _server;

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
    
    BOOL loadFlag = [self loadUserData];
    if (!loadFlag)
    {
        [self initUserData];
    }
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
    
    NSString* file = [self.dataDirectory stringByAppendingPathComponent:ARCHIVE_DEVICE_NAME];
    
    flag = [NSKeyedArchiver archiveRootObject:_device toFile:file];
    
    return flag;
}

-(BOOL) loadUserData
{
    BOOL flag = NO;
    
    NSString* file = [self.dataDirectory stringByAppendingPathComponent:ARCHIVE_DEVICE_NAME];
    
    @try
    {
        _device = [NSKeyedUnarchiver unarchiveObjectWithFile:file];
        
        [self _initServerData];
    }
    @catch (NSException *exception)
    {
        DDLogWarn(@"Failed to load user data from: %@", file);
        DDLogWarn(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
    
    flag = (nil != _device) ? YES : NO;
    
    return flag;
}

-(void) initUserData
{
    _device = [[RHDevice alloc] init];
    
    [self _initServerData];
}

#pragma mark - Private Methods

-(void) _initServerData
{
    _server = [[RHServer alloc] init];
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
