//
//  RHProfile.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHProfile.h"

#import "CBJSONUtils.h"

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

-(id) _getOUnbanDate
{
    id oUnbanDate = nil;
    
    if (nil == _unbanDate)
    {
        oUnbanDate = [NSNull null];
    }
    else
    {
        oUnbanDate = _unbanDate;
    }
    
    return oUnbanDate;
}

-(id) _getOCreateTime
{
    id oRegisterTime = nil;
    
    if (nil == _createTime)
    {
        oRegisterTime = [NSNull null];
    }
    else
    {
        oRegisterTime = _createTime;
    }
    
    return oRegisterTime;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oProfileId = [self _getOProfileId];
    [dic setObject:oProfileId forKey:JSON_KEY_PROFILEID];
    
    NSNumber* oServiceStatus = [self _getOServiceStatus];
    [dic setObject:oServiceStatus forKey:JSON_KEY_SERVICESTATUS];
    
    id oUnbanDate = [self _getOUnbanDate];
    [dic setObject:oUnbanDate forKey:JSON_KEY_UNBANDATE];
    
    NSNumber* oActive = [NSNumber numberWithBool:_active];
    [dic setObject:oActive forKey:JSON_KEY_ACTIVE];
    
    id oCreateTime = [self _getOCreateTime];
    [dic setObject:oCreateTime forKey:JSON_KEY_CREATETIME];

    NSDictionary* interestCardDic = _interestCard.toJSONObject;
    [dic setObject:interestCardDic forKey:JSON_KEY_INTERESTCARD];
    
    NSDictionary* impressCardDic = _impressCard.toJSONObject;
    [dic setObject:impressCardDic forKey:JSON_KEY_IMPRESSCARD];
    
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
        _profileId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_PROFILEID];
        
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
    [aCoder encodeInteger:_profileId forKey:SERIALIZE_KEY_PROFILEID];
    
    [aCoder encodeInt:_serviceStatus forKey:SERIALIZE_KEY_SERVICESTATUS];
    
    [aCoder encodeObject:_unbanDate forKey:SERIALIZE_KEY_UNBANDATE];
    
    [aCoder encodeBool:_active forKey:SERIALIZE_KEY_ACTIVE];
    
    [aCoder encodeObject:_createTime forKey:SERIALIZE_KEY_CREATETIME];
    
    [aCoder encodeObject:_interestCard forKey:SERIALIZE_KEY_INTERESTCARD];
    
    [aCoder encodeObject:_impressCard forKey:SERIALIZE_KEY_IMPRESSCARD];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
//    RHProfile* copy = [[RHProfile alloc] init];
//    
//    copy.profileId = _profileId;
//    copy.serviceStatus = _serviceStatus;
//    copy.unbanDate = [_unbanDate copy];
//    copy.active = _active;
//    copy.createTime = [_createTime copy];
//    copy.interestCard = [_interestCard copy];
//    copy.impressCard = [_impressCard copy];
//    
//    return copy;

    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHProfile*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
//    RHProfile* mutableCopy = [[RHProfile alloc] init];
//    
//    mutableCopy.profileId = _profileId;
//    mutableCopy.serviceStatus = _serviceStatus;
//    mutableCopy.unbanDate = [_unbanDate mutableCopy];
//    mutableCopy.active = _active;
//    mutableCopy.createTime = [_createTime mutableCopy];
//    mutableCopy.interestCard = [_interestCard mutableCopy];
//    mutableCopy.impressCard = [_impressCard mutableCopy];
//    
//    return mutableCopy;

    return [self copyWithZone:zone];
}

@end
