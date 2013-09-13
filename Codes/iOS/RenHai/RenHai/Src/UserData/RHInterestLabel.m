//
//  RHInterestLabel.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHInterestLabel.h"

#define SERIALIZE_KEY_LABELID @"interestLabel.labelId"
#define SERIALIZE_KEY_NAME @"interestLabel.interestLabelName"
#define SERIALIZE_KEY_GLOBALMATCHCOUNT @"interestLabel.globalMatchCount"
#define SERIALIZE_KEY_ORDER @"interestLabel.labelOrder"
#define SERIALIZE_KEY_MATCHCOUNT @"interestLabel.matchCount"
#define SERIALIZE_KEY_VALIDFLAG @"interestLabel.validFlag"


#define JSON_KEY_LABELID @"globalInterestLabelId"
#define JSON_KEY_NAME @"impressLabelName"
#define JSON_KEY_GLOBALMATCHCOUNT @"globalMatchCount"
#define JSON_KEY_ORDER @"labelOrder"
#define JSON_KEY_MATCHCOUNT @"matchCount"
#define JSON_KEY_VALIDFLAG @"validFlag"

@implementation RHInterestLabel

@synthesize labelId = _labelId;
@synthesize labelName = _labelName;
@synthesize globalMatchCount = _globalMatchCount;
@synthesize labelOrder = _labelOrder;
@synthesize matchCount = _matchCount;
@synthesize validFlag = _validFlag;

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

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];

    id oLabelId = [self _getOLabelId];
    [dic setObject:oLabelId forKey:SERIALIZE_KEY_LABELID];

    [dic setObject:_labelName forKey:SERIALIZE_KEY_NAME];

    NSNumber* oGlobalMatchCount = [NSNumber numberWithInteger:_globalMatchCount];
    [dic setObject:oGlobalMatchCount forKey:SERIALIZE_KEY_GLOBALMATCHCOUNT];
    
    NSNumber* oOrder = [NSNumber numberWithInteger:_labelOrder];
    [dic setObject:oOrder forKey:SERIALIZE_KEY_ORDER];
    
    NSNumber* oMatchCount = [NSNumber numberWithInteger:_matchCount];
    [dic setObject:oMatchCount forKey:SERIALIZE_KEY_MATCHCOUNT];
    
    NSNumber* oValidFlag = [NSNumber numberWithBool:_validFlag];
    [dic setObject:oValidFlag forKey:SERIALIZE_KEY_VALIDFLAG];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oLabelId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELID];
        _labelId = oLabelId.integerValue;

        _labelName = [aDecoder decodeObjectForKey:SERIALIZE_KEY_NAME];
        
        _globalMatchCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_GLOBALMATCHCOUNT];
        
        _labelOrder = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_ORDER];
        
        _matchCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_MATCHCOUNT];
        
        _validFlag = [aDecoder decodeBoolForKey:SERIALIZE_KEY_VALIDFLAG];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    id oLabelId = [self _getOLabelId];
    [aCoder encodeObject:oLabelId forKey:SERIALIZE_KEY_LABELID];

    [aCoder encodeObject:_labelName forKey:SERIALIZE_KEY_NAME];
    
    [aCoder encodeInteger:_globalMatchCount forKey:SERIALIZE_KEY_GLOBALMATCHCOUNT];
    
    [aCoder encodeInteger:_labelOrder forKey:SERIALIZE_KEY_ORDER];
    
    [aCoder encodeInteger:_matchCount forKey:SERIALIZE_KEY_MATCHCOUNT];
    
    [aCoder encodeBool:_validFlag forKey:SERIALIZE_KEY_VALIDFLAG];
}

@end
