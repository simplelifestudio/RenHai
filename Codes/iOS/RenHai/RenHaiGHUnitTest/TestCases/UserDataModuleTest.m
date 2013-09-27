//
//  UserDataModuleTest.m
//  RenHai
//
//  Created by DENG KE on 13-9-26.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserDataModuleTest.h"

#import "UserDataModule.h"

@interface UserDataModuleTest()
{
    UserDataModule* _userDataModule;
    
    NSString* _dataDir;
    
    RHDevice* _device;
}

@end

@implementation UserDataModuleTest

-(void) setUp
{
    _userDataModule = [UserDataModule sharedInstance];
    [_userDataModule initModule];
    
    NSString* rootDir = NSHomeDirectory();
    NSString* docDir = [rootDir stringByAppendingPathComponent:@"Documents"];
    _dataDir = [docDir stringByAppendingPathComponent:@"Data"];
    
    _device = [[RHDevice alloc] init];
}

-(void) tearDown
{
    [_userDataModule stopService];
    [_userDataModule releaseModule];
}

-(void) testDeviceSave
{
    [_userDataModule initUserData];    
    BOOL flag = [_userDataModule saveUserData];
    GHAssertTrue(flag, @"UserData Module saved fail!");
}

-(void) testDeviceLoad
{
    BOOL flag = [_userDataModule loadUserData];
    GHAssertTrue(flag, @"UserData Module loaded fail!");
}

-(void) testDataSave
{
    BOOL flag = NO;
    
    NSString* file = [_dataDir stringByAppendingPathComponent:@"testData.dat"];
    GHTestLog(@"saved test data's json string: %@", _device.toJSONString);
    flag = [NSKeyedArchiver archiveRootObject:_device toFile:file];

    GHAssertTrue(flag, @"test data saved fail!");
}

-(void) testDataLoad
{
    BOOL flag = NO;
    
    NSString* file = [_dataDir stringByAppendingPathComponent:@"testData.dat"];
    
    @try
    {
        _device = nil;
        _device = [NSKeyedUnarchiver unarchiveObjectWithFile:file];
    }
    @catch (NSException *exception)
    {
        GHTestLog(@"Caught exception: %@", exception.debugDescription);
    }
    @finally
    {
        
    }
    
    flag = (nil != _device) ? YES : NO;
    if (flag)
    {
        GHTestLog(@"loaded test data's json string: %@", _device.toJSONString);
    }

    GHAssertTrue(flag, @"test data loaded fail!");
}

@end
