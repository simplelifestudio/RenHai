//
//  RHDevice.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHDevice.h"

#define SERIALIZE_KEY_DEVICECARD @"Device.deviceCard"
#define SERIALIZE_KEY_PROFILELIST @"Device.profileList"

#define JSON_KEY_DEVICECARD @"deviceCard"
#define JSON_KEY_PROFILELIST @"profileList"

@implementation RHDevice

@synthesize deviceCard = _deviceCard;
@synthesize profileList = _profileList;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _deviceCard = [[RHDeviceCard alloc] init];
        
        _profileList = [NSMutableArray array];        
        RHProfile* profile = [[RHProfile alloc] init];
        [_profileList addObject:profile];
    }
    
    return self;
}

#pragma mark - Public Methods

-(RHProfile*) currentProfile
{
    RHProfile* profile = nil;
    
    profile = (0 < _profileList.count) ? [_profileList objectAtIndex:0] : nil;
    
    return profile;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    [dic setObject:_deviceCard forKey:JSON_KEY_DEVICECARD];
    
    [dic setObject:_profileList forKey:JSON_KEY_PROFILELIST];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        _deviceCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICECARD];
        
        _profileList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_PROFILELIST];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeObject:_deviceCard forKey:SERIALIZE_KEY_DEVICECARD];
    
    [aCoder encodeObject:_profileList forKey:SERIALIZE_KEY_PROFILELIST];
}

@end
