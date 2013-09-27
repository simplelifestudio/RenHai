//
//  RHImpressLabel.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHImpressLabel.h"

#import "CBJSONUtils.h"

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

-(id) _getOUpdateTime
{
    id oUpdateTime = nil;
    
    if (nil == _updateTime)
    {
        oUpdateTime = [NSNull null];
    }
    else
    {
        oUpdateTime = _updateTime;
    }
    
    return oUpdateTime;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oLabelId = [self _getOLabelId];
    [dic setObject:oLabelId forKey:MESSAGE_KEY_IMPRESSLABELID];
    
    NSNumber* oAssessedCount = [NSNumber numberWithInteger:_assessedCount];
    [dic setObject:oAssessedCount forKey:MESSAGE_KEY_ASSESSEDCOUNT];

    id oUpdateTime = [self _getOUpdateTime];
    [dic setObject:oUpdateTime forKey:MESSAGE_KEY_UPDATETIME];
    
    NSNumber* oAssessCount = [NSNumber numberWithInteger:_assessCount];
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

@end
