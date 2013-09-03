//
//  RHInterestCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHInterestCard.h"

#import "RHInterestLabel.h"

#define SERIALIZE_KEY_CARDID @"InterestCard.cardId"
#define SERIALIZE_KEY_LABELLIST @"InterestCard.labelList"

#define JSON_KEY_CARDID @"cardId"
#define JSON_KEY_LABELLIST @"labelList"

@interface RHInterestCard()
{
    NSMutableDictionary* _labelList;
}

@end

@implementation RHInterestCard

@synthesize cardId = _cardId;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _labelList = [NSMutableDictionary dictionary];
    }
    
    return self;
}

-(NSArray*) getLabelList
{
    NSMutableArray* list = [NSMutableArray array];
    
    [_labelList enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
        [list addObject:obj];
    }];
    
    return list;
}

-(BOOL) addLabel:(NSString*) labelName
{
    BOOL flag = NO;
    
    if (![self isLabelExists:labelName])
    {
        RHInterestLabel* label = [[RHInterestLabel alloc] init];
        label.name = labelName;
        
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
    __block BOOL flag = NO;
    
    if (nil != labelName && 0 < labelName.length)
    {
        [_labelList enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL* stop){
            RHInterestLabel* label = (RHInterestLabel*)obj;
            if ([label.name isEqualToString:labelName])
            {
                flag = YES;
                *stop = YES;
            }
        }];
    }
    
    return flag;
}

#pragma mark - Private Methods

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSNumber* oCardId = [NSNumber numberWithInteger:_cardId];
    [dic setObject:oCardId forKey:JSON_KEY_CARDID];
    
    [dic setObject:_labelList forKey:JSON_KEY_LABELLIST];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oCardId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CARDID];
        _cardId = oCardId.integerValue;
        
        _labelList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELLIST];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    NSNumber* oCardId = [NSNumber numberWithInteger:_cardId];
    [aCoder encodeObject:oCardId forKey:SERIALIZE_KEY_CARDID];
    
    [aCoder encodeObject:_labelList forKey:SERIALIZE_KEY_LABELLIST];
}

@end
