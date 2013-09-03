//
//  RHImpressLabel.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHImpressLabel.h"

#define SERIALIZE_KEY_LABELID @"ImpressLabel.labelId"
#define SERIALIZE_KEY_COUNT @"ImpressLabel.count"
#define SERIALIZE_KEY_UPDATETIME @"ImpressLabel.updateTime"
#define SERIALIZE_KEY_ASSESSCOUNT @"ImpressLabel.assessCount"
#define SERIALIZE_KEY_NAME @"ImpressLabel.name"

#define JSON_KEY_LABELID @"labelId"
#define JSON_KEY_COUNT @"count"
#define JSON_KEY_UPDATETIME @"updateTime"
#define JSON_KEY_ASSESSCOUNT @"assessCount"
#define JSON_KEY_NAME @"name"

@implementation RHImpressLabel

@synthesize labelId = _labelId;
@synthesize count = _count;
@synthesize updateTime = _updateTime;
@synthesize assessCount = _assessCount;
@synthesize name = _name;

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
    
    NSNumber* oLabelId = [NSNumber numberWithInteger:_labelId];
    [dic setObject:oLabelId forKey:JSON_KEY_LABELID];
    
    NSNumber* oCount = [NSNumber numberWithInteger:_count];
    [dic setObject:oCount forKey:JSON_KEY_COUNT];

    [dic setObject:_updateTime forKey:JSON_KEY_UPDATETIME];
    
    NSNumber* oAssessCount = [NSNumber numberWithInteger:_assessCount];
    [dic setObject:oAssessCount forKey:JSON_KEY_ASSESSCOUNT];
    
    [dic setObject:_name forKey:JSON_KEY_NAME];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oLabelId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELID];
        _labelId = oLabelId.integerValue;
        
        NSNumber* oCount = [aDecoder decodeObjectForKey:SERIALIZE_KEY_COUNT];
        _count = oCount.integerValue;
        
        _updateTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_UPDATETIME];
        
        NSNumber* oAssessCount = [aDecoder decodeObjectForKey:SERIALIZE_KEY_ASSESSCOUNT];
        _assessCount = oAssessCount.integerValue;
        
        _name = [aDecoder decodeObjectForKey:SERIALIZE_KEY_NAME];
    }
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    NSNumber* oLabelId = [NSNumber numberWithInteger:_labelId];
    [aCoder encodeObject:oLabelId forKey:SERIALIZE_KEY_LABELID];
    
    NSNumber* oCount = [NSNumber numberWithInteger:_count];
    [aCoder encodeObject:oCount forKey:SERIALIZE_KEY_COUNT];
    
    [aCoder encodeObject:_updateTime forKey:SERIALIZE_KEY_UPDATETIME];
    
    NSNumber* oAssessCount = [NSNumber numberWithInteger:_assessCount];
    [aCoder encodeObject:oAssessCount forKey:SERIALIZE_KEY_ASSESSCOUNT];
    
    [aCoder encodeObject:_name forKey:SERIALIZE_KEY_NAME];
}

@end
