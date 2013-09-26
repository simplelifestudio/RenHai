//
//  RHDevice.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHDevice.h"

#import "SSKeychain.h"

#import "CBJSONUtils.h"

#import "AppDataModule.h"

#define SERIALIZE_KEY_DEVICEID @"device.deviceId"
#define SERIALIZE_KEY_DEVICESN @"device.deviceSn"
#define SERIALIZE_KEY_DEVICECARD @"device.deviceCard"
#define SERIALIZE_KEY_PROFILE @"device.profile"

#define JSON_KEY_DEVICE @"device"
#define JSON_KEY_DEVICEID @"deviceId"
#define JSON_KEY_DEVICESN @"deviceSn"
#define JSON_KEY_DEVICECARD @"deviceCard"
#define JSON_KEY_PROFILE @"profile"

@implementation RHDevice

@synthesize deviceId = _deviceId;
@synthesize deviceSn = _deviceSn;

@synthesize deviceCard = _deviceCard;
@synthesize profile = _profile;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _deviceSn = [self _computeDeviceSn];
        
        _deviceCard = [[RHDeviceCard alloc] init];
        
        _profile = [[RHProfile alloc] init];
    }
    
    return self;
}

#pragma mark - Private Methods

-(NSString*) _computeDeviceSn
{
    AppDataModule* appDataModule = [AppDataModule sharedInstance];
    return appDataModule.deviceSn;
}

-(id) _getODeviceId
{
    id oDeviceId = nil;
    if (0 >= _deviceId)
    {
        oDeviceId = [NSNull null];
    }
    else
    {
        oDeviceId = [NSNumber numberWithInteger:_deviceId];
    }
    return oDeviceId;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oDeviceId = [self _getODeviceId];
    [dic setObject:oDeviceId forKey:JSON_KEY_DEVICEID];
    
    [dic setObject:_deviceSn forKey:JSON_KEY_DEVICESN];
    
    NSDictionary* deviceCardDic = [_deviceCard toJSONObject];
    [dic setObject:deviceCardDic forKey:JSON_KEY_DEVICECARD];
    
    NSDictionary* profileDic = _profile.toJSONObject;
    [dic setObject:profileDic forKey:JSON_KEY_PROFILE];
    
    NSDictionary* rootDic = [NSDictionary dictionaryWithObject:dic forKey:JSON_KEY_DEVICE];
    
    return rootDic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        _deviceId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_DEVICEID];
        
        _deviceSn = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICESN];
        
        _deviceCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICECARD];
        
        _profile = [aDecoder decodeObjectForKey:SERIALIZE_KEY_PROFILE];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_deviceId forKey:SERIALIZE_KEY_DEVICEID];
    
    [aCoder encodeObject:_deviceSn forKey:SERIALIZE_KEY_DEVICESN];
    
    [aCoder encodeObject:_deviceCard forKey:SERIALIZE_KEY_DEVICECARD];
    
    [aCoder encodeObject:_profile forKey:SERIALIZE_KEY_PROFILE];
}

@end
