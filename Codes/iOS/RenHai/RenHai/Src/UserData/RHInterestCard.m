//
//  RHInterestCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHInterestCard.h"

#import "RHInterestLabel.h"

#import "CBJSONUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_CARDID @"interestCard.interestCardId"
#define SERIALIZE_KEY_LABELLIST @"interestCard.interestLabelList"

@interface RHInterestCard()
{
    
}

@property (nonatomic, strong) NSMutableDictionary* labelMap;

@end

@implementation RHInterestCard

@synthesize interestCardId = _interestCardId;
@synthesize labelMap = _labelMap;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {

    }
    
    return self;
}

-(NSArray*) labelList
{
    NSMutableArray* list = [NSMutableArray array];
    
    if (nil != _labelMap)
    {
        [_labelMap enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
            [list addObject:obj];
        }];
    }
    
    return list;
}

-(BOOL) addLabel:(NSString*) labelName
{
    BOOL flag = NO;
    
    if (![self isLabelExists:labelName])
    {
        RHInterestLabel* label = [[RHInterestLabel alloc] init];
        label.labelName = labelName;
        
        if (nil == _labelMap)
        {
            _labelMap = [NSMutableDictionary dictionary];
        }
        [_labelMap setObject:label forKey:labelName];
        
        flag = YES;
    }
    
    return flag;
}

-(BOOL) removeLabel:(NSString*) labelName
{
    BOOL flag = NO;
    
    if ([self isLabelExists:labelName])
    {
        if (nil != _labelMap)
        {
            [_labelMap removeObjectForKey:labelName];
        }

        flag = YES;
    }
    
    return flag;
}

-(BOOL) isLabelExists:(NSString*) labelName
{
    if (nil == _labelMap)
    {
        _labelMap = [NSMutableDictionary dictionary];
    }
    
    __block BOOL flag = NO;
    
    if (nil != labelName && 0 < labelName.length)
    {
        [_labelMap enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
            RHInterestLabel* label = (RHInterestLabel*)obj;
            if ([label.labelName isEqualToString:labelName])
            {
                flag = YES;
                *stop = YES;
            }
        }];
    }
    
    return flag;
}

#pragma mark - Private Methods

-(id) _getOCardId
{
    id oCardId = nil;
    if (0 >= _interestCardId)
    {
        oCardId = [NSNull null];
    }
    else
    {
        oCardId = [NSNumber numberWithInteger:_interestCardId];
    }
    return oCardId;
}

-(id) _getOLabelListDic
{
    id oLabelListDic = nil;
    id oLabelList = [self _getOLabelList];
    if (nil == oLabelList)
    {
        oLabelListDic = [NSNull null];
    }
    else
    {
        oLabelListDic = oLabelList;
    }
    
    return oLabelListDic;
}

-(id) _getOLabelList
{
    id oLabelList = _labelMap;
    if (nil != oLabelList)
    {
        oLabelList = [NSMutableArray arrayWithCapacity:_labelMap.count];
        
        [_labelMap enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
            RHInterestLabel* label = (RHInterestLabel*)obj;            
            NSDictionary* labelDic = label.toJSONObject;
            [oLabelList addObject:labelDic];
        }];
    }
    
    return oLabelList;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oCardId = [dic objectForKey:MESSAGE_KEY_INTERESTCARDID];
        if (nil != oCardId)
        {
            _interestCardId = ([NSNull null] != oCardId) ? ((NSNumber*)oCardId).integerValue : 0;
        }
        
        id labelArray = [dic objectForKey:MESSAGE_KEY_INTERESTLABELLIST];
        if (nil != labelArray)
        {
            if ([NSNull null] != labelArray)
            {
                NSMutableDictionary* map = [NSMutableDictionary dictionary];
                
                NSArray* array = (NSArray*)labelArray;
                for (NSDictionary* labelDic in array)
                {
                    RHInterestLabel* label = [[RHInterestLabel alloc] init];
                    [label fromJSONObject:labelDic];
                    
                    [map setObject:label forKey:label.labelName];
                }
                
                _labelMap = map;
            }
            else
            {
                _labelMap = nil;
            }
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oCardId = [self _getOCardId];
    [dic setObject:oCardId forKey:MESSAGE_KEY_INTERESTCARDID];
    
    id labelListDic = [self _getOLabelListDic];
    [dic setObject:labelListDic forKey:MESSAGE_KEY_INTERESTLABELLIST];
    
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
        _interestCardId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CARDID];
        _labelMap = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELLIST];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_interestCardId forKey:SERIALIZE_KEY_CARDID];
    [aCoder encodeObject:_labelMap forKey:SERIALIZE_KEY_LABELLIST];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHInterestCard*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

@end
