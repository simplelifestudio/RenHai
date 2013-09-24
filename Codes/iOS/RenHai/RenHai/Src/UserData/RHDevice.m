//
//  RHDevice.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHDevice.h"

#import "SSKeychain.h"

#import "AppDataModule.h"

#define SERIALIZE_KEY_DEVICEID @"device.deviceId"
#define SERIALIZE_KEY_DEVICESN @"device.deviceSn"
#define SERIALIZE_KEY_DEVICECARD @"device.deviceCard"
#define SERIALIZE_KEY_PROFILELIST @"device.profileList"

#define JSON_KEY_DEVICEID @"deviceId"
#define JSON_KEY_DEVICESN @"deviceSn"
#define JSON_KEY_DEVICECARD @"deviceCard"
#define JSON_KEY_PROFILELIST @"profileList"

@implementation RHDevice

@synthesize deviceId = _deviceId;
@synthesize deviceSn = _deviceSn;

@synthesize deviceCard = _deviceCard;
@synthesize profileList = _profileList;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _deviceSn = [self _computeDeviceSn];
        
        _deviceCard = [[RHDeviceCard alloc] init];
        
        _profileList = [NSMutableArray array];        
        RHProfile* profile = [[RHProfile alloc] init];
        [_profileList addObject:profile];
    }
    
    return self;
}

-(RHProfile*) currentProfile
{
    RHProfile* profile = nil;
    
    profile = (0 < _profileList.count) ? [_profileList objectAtIndex:0] : nil;
    
    return profile;
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
    
    [dic setObject:_deviceCard forKey:JSON_KEY_DEVICECARD];
    
    [dic setObject:_profileList forKey:JSON_KEY_PROFILELIST];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oDeviceId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICEID];
        if (nil != oDeviceId)
        {
            _deviceId = oDeviceId.integerValue;
        }
        
        _deviceSn = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICESN];
        
        _deviceCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICECARD];
        
        _profileList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_PROFILELIST];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    id oDeviceId = [self _getODeviceId];
    [aCoder encodeObject:oDeviceId forKey:SERIALIZE_KEY_DEVICEID];
    
    [aCoder encodeObject:_deviceSn forKey:SERIALIZE_KEY_DEVICESN];
    
    [aCoder encodeObject:_deviceCard forKey:SERIALIZE_KEY_DEVICECARD];
    
    [aCoder encodeObject:_profileList forKey:SERIALIZE_KEY_PROFILELIST];
}

@end
