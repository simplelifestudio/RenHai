//
//  RHInterestCard.m
//  RenHai
//
//  Created by DENG KE on 13-9-2.
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

@property (nonatomic, strong) NSMutableArray* labelList;

@end

@implementation RHInterestCard

@synthesize interestCardId = _interestCardId;
@synthesize labelList = _labelList;

#pragma mark - Public Methods

+(NSArray*) orderLabelList:(NSArray*) labelList
{
    NSSortDescriptor *sortDescriptor = sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"labelOrder"
                                                                                    ascending:YES];
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    NSArray *sortedArray = [labelList sortedArrayUsingDescriptors:sortDescriptors];
    
    return sortedArray;
}

-(id) init
{
    if (self = [super init])
    {
        
    }
    
    return self;
}

-(BOOL) addLabel:(NSString*) labelName
{
    BOOL flag = NO;
    
    if (![self isLabelExists:labelName])
    {
        RHInterestLabel* label = [[RHInterestLabel alloc] init];
        label.labelName = labelName;
        
        if (nil == _labelList)
        {
            _labelList = [NSMutableArray array];
        }
        [_labelList addObject:label];
        
        flag = YES;
        
        [self _recaculateLabelOrders];
    }
    
    return flag;
}

-(BOOL) removeLabelByName:(NSString*) labelName
{
    BOOL flag = NO;
    
    RHInterestLabel* label = [self getLabelByName:labelName];
    if (nil != label)
    {
        [_labelList removeObject:label];
        flag = YES;
        
        [self _recaculateLabelOrders];
    }
    
    return flag;
}

-(BOOL) removeLabelByIndex:(NSUInteger)index
{
    BOOL flag = NO;
    
    if (index < _labelList.count)
    {
        [_labelList removeObjectAtIndex:index];
        flag = YES;
        
        [self _recaculateLabelOrders];
    }
    
    return flag;
}

-(BOOL) isLabelExists:(NSString*) labelName
{
    BOOL flag = NO;
    
    flag = (nil != [self getLabelByName:labelName]) ? YES : NO;
    
    return flag;
}

-(BOOL) insertLabelByName:(NSString*) labelName index:(NSInteger) index
{
    BOOL flag = NO;
    
    if (nil != labelName && 0 < labelName.length && index <= _labelList.count)
    {
        NSInteger labelIndex = [self getLabelIndex:labelName];
        
        if (labelIndex > 0)
        {
            [self reorderLabel:labelIndex toIndex:index];
        }
        else
        {
            RHInterestLabel* label = [[RHInterestLabel alloc] init];
            label.labelName = labelName;
            
            [_labelList insertObject:label atIndex:index];
            
            [self _recaculateLabelOrders];
        }
    }
    
    return flag;
}

-(RHInterestLabel*) getLabelByName:(NSString *)labelName
{
    __block RHInterestLabel* label = nil;
    
    if (nil != labelName && 0 < labelName.length)
    {
        [_labelList enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop){
            RHInterestLabel* tempLabel = (RHInterestLabel*)obj;
            if ([tempLabel.labelName isEqualToString:labelName])
            {
                label = tempLabel;
                *stop = YES;
            }
        }];
    }
    
    return label;
}

-(RHInterestLabel*) getLabelByIndex:(NSUInteger) index
{
    __block RHInterestLabel* label = nil;
    
    if (_labelList.count > index)
    {
        label = _labelList[index];
    }
    
    return label;
}

-(NSInteger) getLabelIndex:(NSString*) labelName
{
    __block NSInteger index = -1;
    
    if (nil != labelName && 0 < labelName.length)
    {
        [_labelList enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop){
            RHInterestLabel* tempLabel = (RHInterestLabel*)obj;
            if ([tempLabel.labelName isEqualToString:labelName])
            {
                index = idx;
                *stop = YES;
            }
        }];
    }
    
    return index;
}

-(void) reorderLabel:(NSUInteger) fromIndex toIndex:(NSUInteger) toIndex
{
    if (fromIndex >= _labelList.count)
    {
        DDLogWarn(@"fromIndex: %d is out of labelList's bound: %d", fromIndex, _labelList.count);
        return;
    }
    
    if (toIndex >= _labelList.count)
    {
        DDLogWarn(@"toIndex: %d is out of labelList's bound: %d", toIndex, _labelList.count);
        return;
    }
    
    RHInterestLabel* label = [_labelList objectAtIndex:fromIndex];
    [_labelList removeObjectAtIndex:fromIndex];
    [_labelList insertObject:label atIndex:toIndex];
    
    [self _recaculateLabelOrders];
}

#pragma mark - Private Methods

-(void) _recaculateLabelOrders
{
    NSUInteger idx = 0;
    for (RHInterestLabel* label in _labelList)
    {
        label.labelOrder = idx;
        idx++;
    }
}

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
    id oLabelList = (nil != _labelList) ? _labelList : [NSNull null];
    
    return oLabelList;
}

-(id) _getOLabelListDic
{
    id labelListDic = nil;
    id oLabelList = [self _getOLabelList];
    if ([NSNull null] != oLabelList)
    {
        labelListDic = [NSMutableArray array];
        NSArray* array = (NSArray*)oLabelList;
        for (RHInterestLabel* label in array)
        {
            NSDictionary* labelDic = label.toJSONObject;
            [labelListDic addObject:labelDic];
        }
    }
    else
    {
        labelListDic = [NSNull null];
    }
    
    return labelListDic;
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
        
        id oLabelArray = [dic objectForKey:MESSAGE_KEY_INTERESTLABELLIST];
        if (nil != oLabelArray)
        {
            if ([NSNull null] != oLabelArray)
            {
                NSMutableArray* labelArray = (NSMutableArray*)oLabelArray;
                NSMutableArray* array = [NSMutableArray arrayWithCapacity:labelArray.count];
                for (NSDictionary* labelDic in labelArray)
                {
                    RHInterestLabel* label = [[RHInterestLabel alloc] init];
                    [label fromJSONObject:labelDic];
                    
                    [array addObject:label];
                }
                
                NSArray* orderedLabelList = [RHInterestCard orderLabelList:array];
                _labelList = [NSMutableArray arrayWithArray:orderedLabelList];
            }
            else
            {
                _labelList = nil;
            }
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oCardId = [self _getOCardId];
    [dic setObject:oCardId forKey:MESSAGE_KEY_INTERESTCARDID];
    
    id oLabelListDic = [self _getOLabelListDic];
    [dic setObject:oLabelListDic forKey:MESSAGE_KEY_INTERESTLABELLIST];
    
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
        NSMutableArray* array = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELLIST];
        NSArray* orderedLabelList = [RHInterestCard orderLabelList:array];
        _labelList = [NSMutableArray arrayWithArray:orderedLabelList];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_interestCardId forKey:SERIALIZE_KEY_CARDID];
    [aCoder encodeObject:_labelList forKey:SERIALIZE_KEY_LABELLIST];
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
