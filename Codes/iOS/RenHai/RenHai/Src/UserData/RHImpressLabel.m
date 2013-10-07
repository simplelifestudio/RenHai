//
//  RHImpressLabel.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHImpressLabel.h"

#import "CBJSONUtils.h"
#import "CBDateUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_LABELID @"impressLabel.labelId"
#define SERIALIZE_KEY_ASSESSEDCOUNT @"impressLabel.assessedCount"
#define SERIALIZE_KEY_UPDATETIME @"impressLabel.updateTime"
#define SERIALIZE_KEY_ASSESSCOUNT @"impressLabel.assessCount"
#define SERIALIZE_KEY_NAME @"impressLabel.impressLabelName"

@implementation RHImpressLabel

@synthesize labelId = _labelId;
@synthesize assessedCount = _assessedCount;
@synthesize updateTime = _updateTime;
@synthesize assessCount = _assessCount;
@synthesize labelName = _labelName;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        
    }
    
    return self;
}

#pragma mark - Private Methods

-(id) _getOLabelId
{
    id oLabelId = nil;
    
    if (0 >= _labelId)
    {
        oLabelId = [NSNull null];
    }
    else
    {
        oLabelId = [NSNumber numberWithInteger:_labelId];
    }
    
    return oLabelId;
}

-(id) _getOAssessedCount
{
    return [NSNumber numberWithInteger:_assessedCount];
}

-(id) _getOAssessCount
{
    return [NSNumber numberWithInteger:_assessCount];
}

-(id) _getOUpdateTime
{
    id oUpdateTime = nil;
    
    if (nil == _updateTime)
    {
        oUpdateTime = [NSNull null];
    }
    else
    {
        oUpdateTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:FULL_DATE_TIME_FORMAT andDate:_updateTime];;
    }
    
    return oUpdateTime;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary*) dic;
{
    if (nil != dic)
    {
        id oLabelId = [dic objectForKey:MESSAGE_KEY_IMPRESSLABELID];
        if (nil != oLabelId)
        {
            _labelId = ([NSNull null] != oLabelId) ? ((NSNumber*)oLabelId).integerValue : 0;
        }
        
        id oAssessedCount = [dic objectForKey:MESSAGE_KEY_ASSESSEDCOUNT];
        if (nil != oAssessedCount)
        {
            _assessedCount = ([NSNull null] != oAssessedCount) ? ((NSNumber*)oAssessedCount).integerValue : 0;
        }

        id oUpdateTime = [dic objectForKey:MESSAGE_KEY_UPDATETIME];
        if (nil != oUpdateTime)
        {
            _updateTime = [CBDateUtils dateFromStringWithFormat:oUpdateTime andFormat:FULL_DATE_TIME_FORMAT];
        }
        
        id oAssessCount = [dic objectForKey:MESSAGE_KEY_ASSESSCOUNT];
        if (nil != oAssessCount)
        {
            _assessCount = ([NSNull null] != oAssessCount) ? ((NSNumber*)oAssessCount).integerValue : 0;
        }
        
        NSString* labelName = [dic objectForKey:MESSAGE_KEY_IMPRESSLABELNAME];
        if (nil != labelName)
        {
            _labelName = labelName;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oLabelId = [self _getOLabelId];
    [dic setObject:oLabelId forKey:MESSAGE_KEY_INTERESTLABELID];
    
    id oAssessedCount = [self _getOAssessedCount];
    [dic setObject:oAssessedCount forKey:MESSAGE_KEY_ASSESSEDCOUNT];

    id oUpdateTime = [self _getOUpdateTime];
    [dic setObject:oUpdateTime forKey:MESSAGE_KEY_UPDATETIME];
    
    id oAssessCount = [self _getOAssessCount];
    [dic setObject:oAssessCount forKey:MESSAGE_KEY_ASSESSCOUNT];
    
    [dic setObject:_labelName forKey:MESSAGE_KEY_IMPRESSLABELNAME];
    
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
        _labelId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_LABELID];
        _assessedCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_ASSESSEDCOUNT];
        _updateTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_UPDATETIME];
        _assessCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_ASSESSCOUNT];
        _labelName = [aDecoder decodeObjectForKey:SERIALIZE_KEY_NAME];
    }
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_labelId forKey:SERIALIZE_KEY_LABELID];
    [aCoder encodeInteger:_assessedCount forKey:SERIALIZE_KEY_ASSESSEDCOUNT];
    [aCoder encodeObject:_updateTime forKey:SERIALIZE_KEY_UPDATETIME];
    [aCoder encodeInteger:_assessCount forKey:SERIALIZE_KEY_ASSESSCOUNT];
    [aCoder encodeObject:_labelName forKey:SERIALIZE_KEY_NAME];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHImpressLabel*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

@end
