//
//  RHDeviceCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHDeviceCard.h"

#define SERIALIZE_KEY_DEVICEID @"DeviceCard.deviceId"
#define SERIALIZE_KEY_DEVICESN @"DeviceCard.deviceSn"
#define SERIALIZE_KEY_REGISTERTIME @"DeviceCard.registerTime"
#define SERIALIZE_KEY_SERVICESTATUS @"DeviceCard.serviceStatus"
#define SERIALIZE_KEY_FORBIDDENEXPIREDDATE @"DeviceCard.forbiddenExpiredDate"
#define SERIALIZE_KEY_DEVICEMODEL @"DeviceCard.deviceModel"
#define SERIALIZE_KEY_OSVERSION @"DeviceCard.osVersion"
#define SERIALIZE_KEY_APPVERSION @"DeviceCard.appVersion"
#define SERIALIZE_KEY_LOCATION @"DeviceCard.location"
#define SERIALIZE_KEY_ISJAILED @"DeviceCard.isJailed"

#define JSON_KEY_DEVICEID @"deviceId"
#define JSON_KEY_DEVICESN @"deviceSn"
#define JSON_KEY_REGISTERTIME @"registerTime"
#define JSON_KEY_SERVICESTATUS @"serviceStatus"
#define JSON_KEY_FORBIDDENEXPIREDDATE @"forbiddenExpiredDate"
#define JSON_KEY_DEVICEMODEL @"deviceModel"
#define JSON_KEY_OSVERSION @"osVersion"
#define JSON_KEY_APPVERSION @"appVersion"
#define JSON_KEY_LOCATION @"location"
#define JSON_KEY_ISJAILED @"isJailed"

@implementation RHDeviceCard

/*
 @property (nonatomic) NSInteger deviceId;
 @property (nonatomic, strong) NSString* deviceSn;
 @property (nonatomic, strong) NSDate* registerTime;
 @property (nonatomic) SERVICE_STATUS serviceStatus;
 @property (nonatomic, strong) NSDate* forbiddenExpiredDate;
 @property (nonatomic, strong) NSString* deviceModel;
 @property (nonatomic, strong) NSString* osVersion;
 @property (nonatomic, strong) NSString* appVersion;
 @property (nonatomic) CLLocationCoordinate2D location;
 @property (nonatomic) BOOL isJailed;
 
 */

@synthesize deviceId = _deviceId;
@synthesize deviceSn = _deviceSn;
@synthesize registerTime = _registerTime;
@synthesize serviceStatus = _serviceStatus;
@synthesize forbiddenExpiredDate = _forbiddenExpiredDate;
@synthesize deviceModel = _deviceModel;
@synthesize osVersion = _osVersion;
@synthesize appVersion = _appVersion;
//@synthesize location = _location;
@synthesize isJailed = _isJailed;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        
    }
    
    return self;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSNumber* oDeviceId = [NSNumber numberWithInteger:_deviceId];
    [dic setObject:oDeviceId forKey:JSON_KEY_DEVICEID];
    
    [dic setObject:_deviceSn forKey:JSON_KEY_DEVICESN];
    
    [dic setObject:_registerTime forKey:JSON_KEY_REGISTERTIME];
    
    NSNumber* oServiceStatus = [NSNumber numberWithInt:_serviceStatus];
    [dic setObject:oServiceStatus forKey:JSON_KEY_SERVICESTATUS];
    
    [dic setObject:_forbiddenExpiredDate forKey:JSON_KEY_FORBIDDENEXPIREDDATE];
    
    [dic setObject:_deviceModel forKey:JSON_KEY_DEVICEMODEL];
    
    [dic setObject:_osVersion forKey:JSON_KEY_OSVERSION];
    
    [dic setObject:_appVersion forKey:JSON_KEY_APPVERSION];
    
    NSNumber* oIsJailed = [NSNumber numberWithBool:_isJailed];
    [dic setObject:oIsJailed forKey:JSON_KEY_ISJAILED];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oDeviceId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICEID];
        _deviceId = oDeviceId.integerValue;
        
        _deviceSn = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICESN];
        
        _registerTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_REGISTERTIME];
        
        NSNumber* oServiceStatus = [aDecoder decodeObjectForKey:SERIALIZE_KEY_SERVICESTATUS];
        _serviceStatus = oServiceStatus.intValue;
        
        _forbiddenExpiredDate = [aDecoder decodeObjectForKey:SERIALIZE_KEY_FORBIDDENEXPIREDDATE];
        
        _deviceModel = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICEMODEL];
        
        _osVersion = [aDecoder decodeObjectForKey:SERIALIZE_KEY_OSVERSION];
        
        _appVersion = [aDecoder decodeObjectForKey:SERIALIZE_KEY_APPVERSION];
        
        NSNumber* oIsJailed = [aDecoder decodeObjectForKey:SERIALIZE_KEY_ISJAILED];
        _isJailed = oIsJailed.boolValue;
    }
    
    return nil;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    NSNumber* oDeviceId = [NSNumber numberWithInteger:_deviceId];
    [aCoder encodeObject:oDeviceId forKey:SERIALIZE_KEY_DEVICEID];
    
    [aCoder encodeObject:_deviceSn forKey:SERIALIZE_KEY_DEVICESN];
    
    [aCoder encodeObject:_registerTime forKey:SERIALIZE_KEY_REGISTERTIME];
    
    NSNumber* oServiceStatus = [NSNumber numberWithInt:_serviceStatus];
    [aCoder encodeObject:oServiceStatus forKey:SERIALIZE_KEY_SERVICESTATUS];
    
    [aCoder encodeObject:_forbiddenExpiredDate forKey:SERIALIZE_KEY_FORBIDDENEXPIREDDATE];
    
    [aCoder encodeObject:_deviceModel forKey:SERIALIZE_KEY_DEVICEMODEL];
    
    [aCoder encodeObject:_osVersion forKey:SERIALIZE_KEY_OSVERSION];
    
    [aCoder encodeObject:_appVersion forKey:SERIALIZE_KEY_APPVERSION];
    
    NSNumber* oIsJailed = [NSNumber numberWithBool:_isJailed];
    [aCoder encodeObject:oIsJailed forKey:SERIALIZE_KEY_ISJAILED];
}

@end
