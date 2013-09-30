//
//  RHInterestLabel.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHInterestLabel.h"

#import "CBJSONUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_LABELID @"interestLabel.labelId"
#define SERIALIZE_KEY_NAME @"interestLabel.interestLabelName"
#define SERIALIZE_KEY_GLOBALMATCHCOUNT @"interestLabel.globalMatchCount"
#define SERIALIZE_KEY_ORDER @"interestLabel.labelOrder"
#define SERIALIZE_KEY_MATCHCOUNT @"interestLabel.matchCount"
#define SERIALIZE_KEY_VALIDFLAG @"interestLabel.validFlag"

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
        _validFlag = YES;
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

-(NSNumber*) _getOValidFlag
{
    return [NSNumber numberWithInt:_validFlag];
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];

    id oLabelId = [self _getOLabelId];
    [dic setObject:oLabelId forKey:MESSAGE_KEY_INTERESTLABELID];

    [dic setObject:_labelName forKey:MESSAGE_KEY_INTERESTLABELNAME];

    NSNumber* oGlobalMatchCount = [NSNumber numberWithInteger:_globalMatchCount];
    [dic setObject:oGlobalMatchCount forKey:MESSAGE_KEY_GLOBALMATCHCOUNT];
    
    NSNumber* oOrder = [NSNumber numberWithInteger:_labelOrder];
    [dic setObject:oOrder forKey:MESSAGE_KEY_INTERESTLABELORDER];
    
    NSNumber* oMatchCount = [NSNumber numberWithInteger:_matchCount];
    [dic setObject:oMatchCount forKey:MESSAGE_KEY_MATCHCOUNT];
    
    NSNumber* oValidFlag = [self _getOValidFlag];
    [dic setObject:oValidFlag forKey:MESSAGE_KEY_VALIDFLAG];
    
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

        _labelName = [aDecoder decodeObjectForKey:SERIALIZE_KEY_NAME];
        
        _globalMatchCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_GLOBALMATCHCOUNT];
        
        _labelOrder = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_ORDER];
        
        _matchCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_MATCHCOUNT];
        
        _validFlag = [aDecoder decodeIntForKey:SERIALIZE_KEY_VALIDFLAG];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_labelId forKey:SERIALIZE_KEY_LABELID];
    
    [aCoder encodeObject:_labelName forKey:SERIALIZE_KEY_NAME];
    
    [aCoder encodeInteger:_globalMatchCount forKey:SERIALIZE_KEY_GLOBALMATCHCOUNT];
    
    [aCoder encodeInteger:_labelOrder forKey:SERIALIZE_KEY_ORDER];
    
    [aCoder encodeInteger:_matchCount forKey:SERIALIZE_KEY_MATCHCOUNT];
    
    [aCoder encodeInt:_validFlag forKey:SERIALIZE_KEY_VALIDFLAG];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
//    RHInterestLabel* copy = [[RHInterestLabel alloc] init];
//
//    copy.labelId = _labelId;
//    copy.labelName = [_labelName copy];
//    copy.globalMatchCount = _globalMatchCount;
//    copy.labelOrder = _labelOrder;
//    copy.matchCount = _matchCount;
//    copy.validFlag = _validFlag;
//    
//    return copy;

    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHInterestLabel*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
//    RHInterestLabel* mutableCopy = [[RHInterestLabel alloc] init];
//    
//    mutableCopy.labelId = _labelId;
//    mutableCopy.labelName = [_labelName mutableCopy];
//    mutableCopy.globalMatchCount = _globalMatchCount;
//    mutableCopy.labelOrder = _labelOrder;
//    mutableCopy.matchCount = _matchCount;
//    mutableCopy.validFlag = _validFlag;
//    
//    return mutableCopy;

    return [self copyWithZone:zone];
}

@end
