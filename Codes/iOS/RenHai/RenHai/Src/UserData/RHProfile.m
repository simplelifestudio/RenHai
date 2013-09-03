//
//  RHProfile.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHProfile.h"

#define SERIALIZE_KEY_PROFILEID @"Profile.profileId"
#define SERIALIZE_KEY_INTERESTCARD @"Profile.interestCard"
#define SERIALIZE_KEY_IMPRESSCARD @"Profile.impressCard"
#define SERIALIZE_KEY_LASTACTIVITYTIME @"Profile.lastActivityTime"
#define SERIALIZE_KEY_CREATETIME @"Profile.createTime"

#define JSON_KEY_PROFILEID @"profileId"
#define JSON_KEY_INTERESTCARD @"interestCard"
#define JSON_KEY_IMPRESSCARD @"impressCard"
#define JSON_KEY_LASTACTIVITYTIME @"lastActivityTime"
#define JSON_KEY_CREATETIME @"createTime"

@implementation RHProfile

@synthesize profileId = _profileId;
@synthesize interestCard = _interestCard;
@synthesize impressCard = _impressCard;
@synthesize lastActivityTime = _lastActivityTime;
@synthesize createTime = _createTime;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _interestCard = [[RHInterestCard alloc] init];
        _impressCard = [[RHImpressCard alloc] init];
    }
    
    return self;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSNumber* oProfileId = [NSNumber numberWithInt:_profileId];
    [dic setObject:oProfileId forKey:JSON_KEY_PROFILEID];
    
    [dic setObject:_interestCard forKey:JSON_KEY_INTERESTCARD];
    
    [dic setObject:_impressCard forKey:JSON_KEY_IMPRESSCARD];
    
    [dic setObject:_lastActivityTime forKey:JSON_KEY_LASTACTIVITYTIME];
    
    [dic setObject:_createTime forKey:JSON_KEY_CREATETIME];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oProfileId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_PROFILEID];
        _profileId = oProfileId.integerValue;
        
        _interestCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_INTERESTCARD];
        
        _impressCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_IMPRESSCARD];
        
        _lastActivityTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LASTACTIVITYTIME];
        
        _createTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CREATETIME];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    NSNumber* oProfileId = [NSNumber numberWithInteger:_profileId];
    [aCoder encodeObject:oProfileId forKey:SERIALIZE_KEY_PROFILEID];
    
    [aCoder encodeObject:_interestCard forKey:SERIALIZE_KEY_INTERESTCARD];
    
    [aCoder encodeObject:_impressCard forKey:SERIALIZE_KEY_IMPRESSCARD];
    
    [aCoder encodeObject:_lastActivityTime forKey:SERIALIZE_KEY_LASTACTIVITYTIME];
    
    [aCoder encodeObject:_createTime forKey:SERIALIZE_KEY_CREATETIME];
}

@end
