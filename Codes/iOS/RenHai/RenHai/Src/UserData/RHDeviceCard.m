//
//  RHDeviceCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHDeviceCard.h"

#import "CBJSONUtils.h"

#import "AppDataModule.h"
#import "RHMessage.h"

#define SERIALIZE_KEY_DEVICECARDID @"deviceCard.deviceCardId"
#define SERIALIZE_KEY_REGISTERTIME @"deviceCard.registerTime"
#define SERIALIZE_KEY_DEVICEMODEL @"deviceCard.deviceModel"
#define SERIALIZE_KEY_OSVERSION @"deviceCard.osVersion"
#define SERIALIZE_KEY_APPVERSION @"deviceCard.appVersion"
#define SERIALIZE_KEY_ISJAILED @"deviceCard.isJailed"

@implementation RHDeviceCard

@synthesize deviceCardId = _deviceCardId;
@synthesize registerTime = _registerTime;
@synthesize deviceModel = _deviceModel;
@synthesize osVersion = _osVersion;
@synthesize appVersion = _appVersion;
@synthesize isJailed = _isJailed;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        AppDataModule* appDataModule = [AppDataModule sharedInstance];
        
        _deviceModel = appDataModule.deviceModel;
        _osVersion = appDataModule.osVersion;
        _appVersion = appDataModule.appVersion;
        _isJailed = appDataModule.isJailed;
    }
    
    return self;
}

#pragma mark - Private Methods

-(id) _getODeviceCardId
{
    id oDeviceCardId = nil;
    if (0 >= _deviceCardId)
    {
        oDeviceCardId = [NSNull null];
    }
    else
    {
        oDeviceCardId = [NSNumber numberWithInteger:_deviceCardId];
    }
    return oDeviceCardId;
}

-(id) _getORegisterTime
{
    id oRegisterTime = nil;
    
    if (nil == _registerTime)
    {
        oRegisterTime = [NSNull null];
    }
    else
    {
        oRegisterTime = _registerTime;
    }
    
    return oRegisterTime;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oDeviceCardId = [self _getODeviceCardId];
    [dic setObject:oDeviceCardId forKey:MESSAGE_KEY_DEVICECARDID];
    
    id oRegisterTime = [self _getORegisterTime];
    [dic setObject:oRegisterTime forKey:MESSAGE_KEY_REGISTERTIME];
    
    [dic setObject:_deviceModel forKey:MESSAGE_KEY_DEVICEMODEL];
    
    [dic setObject:_osVersion forKey:MESSAGE_KEY_OSVERSION];
    
    [dic setObject:_appVersion forKey:MESSAGE_KEY_APPVERSION];
    
    NSNumber* oIsJailed = [NSNumber numberWithBool:_isJailed];
    [dic setObject:oIsJailed forKey:MESSAGE_KEY_ISJAILED];
    
    return dic;
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
        _deviceCardId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_DEVICECARDID];
        
        _registerTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_REGISTERTIME];
        
        _deviceModel = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICEMODEL];
        
        _osVersion = [aDecoder decodeObjectForKey:SERIALIZE_KEY_OSVERSION];
        
        _appVersion = [aDecoder decodeObjectForKey:SERIALIZE_KEY_APPVERSION];
        
        _isJailed = [aDecoder decodeBoolForKey:SERIALIZE_KEY_ISJAILED];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_deviceCardId forKey:SERIALIZE_KEY_DEVICECARDID];
    
    [aCoder encodeObject:_registerTime forKey:SERIALIZE_KEY_REGISTERTIME];
    
    [aCoder encodeObject:_deviceModel forKey:SERIALIZE_KEY_DEVICEMODEL];
    
    [aCoder encodeObject:_osVersion forKey:SERIALIZE_KEY_OSVERSION];
    
    [aCoder encodeObject:_appVersion forKey:SERIALIZE_KEY_APPVERSION];
    
    [aCoder encodeBool:_isJailed forKey:SERIALIZE_KEY_ISJAILED];
}

@end
