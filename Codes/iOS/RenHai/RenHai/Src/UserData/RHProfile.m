//
//  RHProfile.m
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHProfile.h"

#import "CBJSONUtils.h"
#import "CBDateUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_PROFILEID @"profile.profileId"
#define SERIALIZE_KEY_SERVICESTATUS @"profile.serviceStatus"
#define SERIALIZE_KEY_UNBANDATE @"profile.unbanDate"
#define SERIALIZE_KEY_ACTIVE @"profile.active"
#define SERIALIZE_KEY_CREATETIME @"profile.createTime"
#define SERIALIZE_KEY_INTERESTCARD @"profile.interestCard"
#define SERIALIZE_KEY_IMPRESSCARD @"profile.impressCard"

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

-(id) _getOServiceStatus
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
        oUnbanDate = [CBDateUtils dateStringInLocalTimeZoneWithFormat:FULL_DATE_TIME_FORMAT andDate:_unbanDate];
    }
    
    return oUnbanDate;
}

-(id) _getOActive
{
    return [NSNumber numberWithBool:_active];
}

-(id) _getOCreateTime
{
    id oCreateTime = nil;
    
    if (nil == _createTime)
    {
        oCreateTime = [NSNull null];
    }
    else
    {
        oCreateTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:FULL_DATE_TIME_FORMAT andDate:_createTime];
    }
    
    return oCreateTime;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oProfileId = [dic objectForKey:MESSAGE_KEY_PROFILEID];
        if (nil != oProfileId)
        {
            _profileId = ([NSNull null] != oProfileId) ? ((NSNumber*)oProfileId).integerValue : 0;
        }
        
        id oServiceStatus = [dic objectForKey:MESSAGE_KEY_SERVICESTATUS];
        if (nil != oServiceStatus)
        {
            _serviceStatus = ([NSNull null] != oServiceStatus) ? ((NSNumber*)oServiceStatus).integerValue : 0;
        }
        
        id oUnbanDate = [dic objectForKey:MESSAGE_KEY_UNBANDATE];
        if (nil != oUnbanDate)
        {
            _unbanDate = ([NSNull null] != oUnbanDate) ? [CBDateUtils dateFromStringWithFormat:oUnbanDate andFormat:FULL_DATE_TIME_FORMAT] : nil;
        }
        
        id oActive = [dic objectForKey:MESSAGE_KEY_ACTIVE];
        if (nil != oActive)
        {
            _active = ([NSNull null] != oActive) ? ((NSNumber*)oActive).boolValue : 0;
        }
        
        id oCreateTime = [dic objectForKey:MESSAGE_KEY_CREATETIME];
        if (nil != oCreateTime)
        {
            _createTime = ([NSNull null] != oCreateTime) ? [CBDateUtils dateFromStringWithFormat:oCreateTime andFormat:FULL_DATE_TIME_FORMAT] : nil;
        }
        
        id oInterestCard = [dic objectForKey:MESSAGE_KEY_INTERESTCARD];
        if (nil != oInterestCard)
        {
            if ([NSNull null] != oInterestCard)
            {
                _interestCard = [[RHInterestCard alloc] init];
                [_interestCard fromJSONObject:oInterestCard];
            }
            else
            {
                _interestCard = nil;
            }
        }
        
        id oImpressCard = [dic objectForKey:MESSAGE_KEY_IMPRESSCARD];
        if (nil != oImpressCard)
        {
            if ([NSNull null] != oImpressCard)
            {
                _impressCard = [[RHImpressCard alloc] init];
                [_impressCard fromJSONObject:oImpressCard];
            }
            else
            {
                _impressCard = nil;
            }
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oProfileId = [self _getOProfileId];
    [dic setObject:oProfileId forKey:MESSAGE_KEY_PROFILEID];
    
    id oServiceStatus = [self _getOServiceStatus];
    [dic setObject:oServiceStatus forKey:MESSAGE_KEY_SERVICESTATUS];
    
    id oUnbanDate = [self _getOUnbanDate];
    [dic setObject:oUnbanDate forKey:MESSAGE_KEY_UNBANDATE];
    
    id oActive = [self _getOActive];
    [dic setObject:oActive forKey:MESSAGE_KEY_ACTIVE];
    
    id oCreateTime = [self _getOCreateTime];
    [dic setObject:oCreateTime forKey:MESSAGE_KEY_CREATETIME];

    NSDictionary* interestCardDic = _interestCard.toJSONObject;
    [dic setObject:interestCardDic forKey:MESSAGE_KEY_INTERESTCARD];
    
    NSDictionary* impressCardDic = _impressCard.toJSONObject;
    [dic setObject:impressCardDic forKey:MESSAGE_KEY_IMPRESSCARD];
    
    return dic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

#pragma mark - NSCoding

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
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHProfile*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

@end
