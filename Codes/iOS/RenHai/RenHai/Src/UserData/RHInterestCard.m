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

#import "RHJSONMessage.h"

#define SERIALIZE_KEY_CARDID @"interestCard.interestCardId"
#define SERIALIZE_KEY_LABELLIST @"interestCard.interestLabelList"

@interface RHInterestCard()
{
    NSMutableDictionary* _labelList;
}

@end

@implementation RHInterestCard

@synthesize interestCardId = _interestCardId;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {

    }
    
    return self;
}

-(NSArray*) getLabelList
{
    NSMutableArray* list = [NSMutableArray array];
    
    if (nil != _labelList)
    {
        [_labelList enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
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
        
        [_labelList setObject:label forKey:labelName];
        
        flag = YES;
    }
    
    return flag;
}

-(BOOL) removeLabel:(NSString*) labelName
{
    BOOL flag = NO;
    
    if ([self isLabelExists:labelName])
    {
        [_labelList removeObjectForKey:labelName];

        flag = YES;
    }
    
    return flag;
}

-(BOOL) isLabelExists:(NSString*) labelName
{
    if (nil == _labelList)
    {
        _labelList = [NSMutableDictionary dictionary];
    }
    
    __block BOOL flag = NO;
    
    if (nil != labelName && 0 < labelName.length)
    {
        [_labelList enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
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

-(id) _getOLabelList
{
    id oLabelList = nil;
    
    if (nil == _labelList)
    {
        oLabelList = [NSNull null];
    }
    else
    {
        oLabelList = _labelList;
    }
    
    return oLabelList;
}

-(id) _getOLabelListDic
{
    id labelListDic = nil;
    
    id oLabelList = [self _getOLabelList];
    if (oLabelList == [NSNull null])
    {
        labelListDic = oLabelList;
    }
    else
    {
        labelListDic = [NSMutableArray arrayWithCapacity:_labelList.count];
        for (RHInterestLabel* label in _labelList)
        {
            NSDictionary* labelDic = label.toJSONObject;
            [labelListDic addObject:labelDic];
        }
    }
    
    return labelListDic;
}

#pragma mark - CBJSONable

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

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        _interestCardId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CARDID];
        
        _labelList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELLIST];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_interestCardId forKey:SERIALIZE_KEY_CARDID];
    
    [aCoder encodeObject:_labelList forKey:SERIALIZE_KEY_LABELLIST];
}

@end
