//
//  RHInterestLabel.m
//  RenHai
//
//  Created by DENG KE on 13-9-2.
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
        _validFlag = ValidFlag;
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

-(NSNumber*) _getOGlobalMatchCount
{
    return [NSNumber numberWithInteger:_globalMatchCount];
}

-(NSNumber*) _getOLabelOrder
{
    return [NSNumber numberWithInteger:_labelOrder];
}

-(NSNumber*) _getOMatchCount
{
    return [NSNumber numberWithInteger:_matchCount];
}

-(NSNumber*) _getOValidFlag
{
    return [NSNumber numberWithInt:_validFlag];
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oLabelId = [dic objectForKey:MESSAGE_KEY_INTERESTLABELID];
        if (nil != oLabelId)
        {
            _labelId = ([NSNull null] != oLabelId) ? ((NSNumber*)oLabelId).integerValue : 0;
        }
        
        NSString* labelName = [dic objectForKey:MESSAGE_KEY_INTERESTLABELNAME];
        if (nil != labelName)
        {
            _labelName = labelName;
        }
        
        id oGlobalMatchCount = [dic objectForKey:MESSAGE_KEY_GLOBALMATCHCOUNT];
        if (nil != oGlobalMatchCount)
        {
            _globalMatchCount = ([NSNull null] != oLabelId) ? ((NSNumber*)oLabelId).integerValue : 0;
        }
        
        id oLabelOrder = [dic objectForKey:MESSAGE_KEY_INTERESTLABELORDER];
        if (nil != oGlobalMatchCount)
        {
            _labelOrder = ([NSNull null] != oLabelOrder) ? ((NSNumber*)oLabelOrder).integerValue : 0;
        }
        
        id oMatchCount = [dic objectForKey:MESSAGE_KEY_MATCHCOUNT];
        if (nil != oMatchCount)
        {
            _matchCount = ([NSNull null] != oMatchCount) ? ((NSNumber*)oMatchCount).integerValue : 0;
        }
        
        id oValidFlag = [dic objectForKey:MESSAGE_KEY_VALIDFLAG];
        if (nil != oValidFlag)
        {
            _validFlag = ([NSNull null] != oValidFlag) ? ((NSNumber*)oValidFlag).integerValue : 0;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];

    id oLabelId = [self _getOLabelId];
    [dic setObject:oLabelId forKey:MESSAGE_KEY_INTERESTLABELID];

    [dic setObject:_labelName forKey:MESSAGE_KEY_INTERESTLABELNAME];

    id oGlobalMatchCount = [self _getOGlobalMatchCount];
    [dic setObject:oGlobalMatchCount forKey:MESSAGE_KEY_GLOBALMATCHCOUNT];
    
    id oLabelOrder = [self _getOLabelOrder];
    [dic setObject:oLabelOrder forKey:MESSAGE_KEY_INTERESTLABELORDER];
    
    id oMatchCount = [self _getOMatchCount];
    [dic setObject:oMatchCount forKey:MESSAGE_KEY_MATCHCOUNT];
    
    id oValidFlag = [self _getOValidFlag];
    [dic setObject:oValidFlag forKey:MESSAGE_KEY_VALIDFLAG];
    
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
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHInterestLabel*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

@end
