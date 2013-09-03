//
//  RHInterestLabel.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHInterestLabel.h"

#define SERIALIZE_KEY_LABELID @"InterestLabel.labelId"
#define SERIALIZE_KEY_ORDER @"InterestLabel.order"
#define SERIALIZE_KEY_MATCHCOUNT @"InterestLabel.matchCount"
#define SERIALIZE_KEY_NAME @"InterestLabel.name"

#define JSON_KEY_LABELID @"labelId"
#define JSON_KEY_ORDER @"order"
#define JSON_KEY_MATCHCOUNT @"matchCount"
#define JSON_KEY_NAME @"name"

@implementation RHInterestLabel

@synthesize labelId = _labelId;
@synthesize order = _order;
@synthesize matchCount = _matchCount;
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
    [dic setObject:oLabelId forKey:SERIALIZE_KEY_LABELID];
    
    NSNumber* oOrder = [NSNumber numberWithInteger:_order];
    [dic setObject:oOrder forKey:SERIALIZE_KEY_ORDER];
    
    NSNumber* oMatchCount = [NSNumber numberWithInteger:_matchCount];
    [dic setObject:oMatchCount forKey:SERIALIZE_KEY_MATCHCOUNT];
    
    [dic setObject:_name forKey:SERIALIZE_KEY_NAME];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oLabelId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELID];
        _labelId = oLabelId.integerValue;
        
        NSNumber* oOrder = [aDecoder decodeObjectForKey:SERIALIZE_KEY_ORDER];
        _order = oOrder.integerValue;
        
        NSNumber* oMatchCount = [aDecoder decodeObjectForKey:SERIALIZE_KEY_MATCHCOUNT];
        _matchCount = oMatchCount.integerValue;
        
        _name = [aDecoder decodeObjectForKey:SERIALIZE_KEY_NAME];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    NSNumber* oLabelId = [NSNumber numberWithInteger:_labelId];
    [aCoder encodeObject:oLabelId forKey:SERIALIZE_KEY_LABELID];
    
    NSNumber* oOrder = [NSNumber numberWithInteger:_order];
    [aCoder encodeObject:oOrder forKey:SERIALIZE_KEY_ORDER];
    
    NSNumber* oMatchCount = [NSNumber numberWithInteger:_matchCount];
    [aCoder encodeObject:oMatchCount forKey:SERIALIZE_KEY_MATCHCOUNT];
    
    [aCoder encodeObject:_name forKey:SERIALIZE_KEY_NAME];
}

@end
