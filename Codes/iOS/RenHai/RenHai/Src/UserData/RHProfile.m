//
//  RHProfile.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHProfile.h"

#define SERIALIZE_KEY_PROFILEID @"profile.profileId"
#define SERIALIZE_KEY_SERVICESTATUS @"profile.serviceStatus"
#define SERIALIZE_KEY_UNBANDATE @"profile.unbanDate"
#define SERIALIZE_KEY_ACTIVE @"profile.active"
#define SERIALIZE_KEY_CREATETIME @"profile.createTime"
#define SERIALIZE_KEY_INTERESTCARD @"profile.interestCard"
#define SERIALIZE_KEY_IMPRESSCARD @"profile.impressCard"

#define JSON_KEY_PROFILEID @"profileId"
#define JSON_KEY_SERVICESTATUS @"serviceStatus"
#define JSON_KEY_UNBANDATE @"unbanDate"
#define JSON_KEY_ACTIVE @"active"
#define JSON_KEY_CREATETIME @"createTime"
#define JSON_KEY_INTERESTCARD @"interestCard"
#define JSON_KEY_IMPRESSCARD @"impressCard"

@implementation RHProfile

@synthesize profileId = _profileId;
@synthesize serviceStatus = _serviceStatus;
@synthesize unbanDate = _unbanDate;
@synthesize active = _active;
@synthesize createTime = _createTime;
@synthesize interestCard = _interestCard;
@synthesize impressCard = _impressCard;

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

#pragma mark - Private Methods

-(id) _getOProfileId
{
    id oProfileId = nil;
    if (0 >= _profileId)
    {
        oProfileId = [NSNull null];
    }
    else
    {
        oProfileId = [NSNumber numberWithInteger:_profileId];
    }
    return oProfileId;
}

-(NSNumber*) _getOServiceStatus
{
    return [NSNumber numberWithInt:_serviceStatus];
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oProfileId = [self _getOProfileId];
    [dic setObject:oProfileId forKey:JSON_KEY_PROFILEID];
    
    NSNumber* oServiceStatus = [self _getOServiceStatus];
    [dic setObject:oServiceStatus forKey:JSON_KEY_SERVICESTATUS];
    
    [dic setObject:_unbanDate forKey:JSON_KEY_UNBANDATE];
    
    NSNumber* oActive = [NSNumber numberWithBool:_active];
    [dic setObject:oActive forKey:JSON_KEY_ACTIVE];
    
    [dic setObject:_createTime forKey:JSON_KEY_CREATETIME];

    [dic setObject:_interestCard forKey:JSON_KEY_INTERESTCARD];
    
    [dic setObject:_impressCard forKey:JSON_KEY_IMPRESSCARD];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oProfileId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_PROFILEID];
        _profileId = oProfileId.integerValue;
        
        _serviceStatus = [aDecoder decodeIntForKey:SERIALIZE_KEY_SERVICESTATUS];
        
        _unbanDate = [aDecoder decodeObjectForKey:SERIALIZE_KEY_UNBANDATE];
        
        _active = [aDecoder decodeBoolForKey:SERIALIZE_KEY_ACTIVE];
        
        _createTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CREATETIME];
        
        _interestCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_INTERESTCARD];
        
        _impressCard = [aDecoder decodeObjectForKey:SERIALIZE_KEY_IMPRESSCARD];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    id oProfileId = [self _getOProfileId];
    [aCoder encodeObject:oProfileId forKey:SERIALIZE_KEY_PROFILEID];
    
    [aCoder encodeInt:_serviceStatus forKey:SERIALIZE_KEY_SERVICESTATUS];
    
    [aCoder encodeObject:_unbanDate forKey:SERIALIZE_KEY_UNBANDATE];
    
    [aCoder encodeBool:_active forKey:SERIALIZE_KEY_ACTIVE];
    
    [aCoder encodeObject:_createTime forKey:SERIALIZE_KEY_CREATETIME];
    
    [aCoder encodeObject:_interestCard forKey:SERIALIZE_KEY_INTERESTCARD];
    
    [aCoder encodeObject:_impressCard forKey:SERIALIZE_KEY_IMPRESSCARD];
}

@end
