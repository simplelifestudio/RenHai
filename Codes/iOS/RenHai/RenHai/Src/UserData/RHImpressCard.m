//
//  RHImpressCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHImpressCard.h"

#import "CBJSONUtils.h"

#define SERIALIZE_KEY_CARDID @"impressCard.impressCardId"
#define SERIALIZE_KEY_ASSESSLABELIST @"impressCard.assessLabelList"
#define SERIALIZE_KEY_IMPRESSLABELIST @"impressCard.impressLabelList"
#define SERIALIZE_KEY_CHATTOTALCOUNT @"impressCard.chatTotalCount"
#define SERIALIZE_KEY_CHATTOTALDURATION @"impressCard.chatTotalDuration"
#define SERIALIZE_KEY_CHATLOSSCOUNT @"impressCard.chatLossCount"

#define JSON_KEY_CARDID @"impressCardId"
#define JSON_KEY_ASSESSLABELLIST @"assessLabelList"
#define JSON_KEY_IMPRESSLABELLIST @"impressLabelList"
#define JSON_KEY_CHATTOTALCOUNT @"chatTotalCount"
#define JSON_KEY_CHATTOTALDURATION @"chatTotalDuration"
#define JSON_KEY_CHATLOSSCOUNT @"chatLossCount"

@implementation RHImpressCard

@synthesize impressCardId = _impressCardId;
@synthesize assessLabelList = _assessLabelList;
@synthesize impressLabelList = _impressLabelList;
@synthesize chatTotalCount = _chatTotalCount;
@synthesize chatTotalDuration = _chatTotalDuration;
@synthesize chatLossCount = _chatLossCount;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _assessLabelList = [NSMutableArray array];
        _impressLabelList = [NSMutableArray array];
    }
    
    return self;
}

#pragma mark - Private Methods

-(id) _getOCardId
{
    id oCardId = nil;
    if (0 >= _impressCardId)
    {
        oCardId = [NSNull null];
    }
    else
    {
        oCardId = [NSNumber numberWithInteger:_impressCardId];
    }
    return oCardId;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oCardId = [self _getOCardId];
    [dic setObject:oCardId forKey:JSON_KEY_CARDID];
    
    NSMutableArray* assessLabelListDic = [NSMutableArray arrayWithCapacity:_assessLabelList.count];
    for (RHImpressLabel* label in _assessLabelList)
    {
        NSDictionary* labelDic = label.toJSONObject;
        [assessLabelListDic addObject:labelDic];
    }
    [dic setObject:assessLabelListDic forKey:JSON_KEY_ASSESSLABELLIST];
    
    NSMutableArray* impressLabelListDic = [NSMutableArray arrayWithCapacity:_impressLabelList.count];
    for (RHImpressLabel* label in _impressLabelList)
    {
        NSDictionary* labelDic = label.toJSONObject;
        [impressLabelListDic addObject:labelDic];
    }
    [dic setObject:impressLabelListDic forKey:JSON_KEY_IMPRESSLABELLIST];
    
    NSNumber* oChatTotalCount = [NSNumber numberWithInteger:_chatTotalCount];
    [dic setObject:oChatTotalCount forKey:JSON_KEY_CHATTOTALCOUNT];
    
    NSNumber* oChatTotalDuration = [NSNumber numberWithInteger:_chatTotalDuration];
    [dic setObject:oChatTotalDuration forKey:JSON_KEY_CHATTOTALDURATION];
    
    NSNumber* oChatLossCount = [NSNumber numberWithInteger:_chatLossCount];
    [dic setObject:oChatLossCount forKey:JSON_KEY_CHATLOSSCOUNT];
    
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
        NSNumber* oCardId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CARDID];
        _impressCardId = oCardId.integerValue;
        
        _assessLabelList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_ASSESSLABELIST];
        
        _impressLabelList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_IMPRESSLABELIST];
        
        _chatTotalCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CHATTOTALCOUNT];
        
        _chatTotalDuration = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CHATTOTALDURATION];
        
        _chatLossCount = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CHATLOSSCOUNT];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    id oCardId = [self _getOCardId];
    [aCoder encodeObject:oCardId forKey:SERIALIZE_KEY_CARDID];

    [aCoder encodeObject:_assessLabelList forKey:SERIALIZE_KEY_ASSESSLABELIST];
    
    [aCoder encodeObject:_impressLabelList forKey:SERIALIZE_KEY_IMPRESSLABELIST];
        
    [aCoder encodeInteger:_chatTotalCount forKey:SERIALIZE_KEY_CHATTOTALCOUNT];

    [aCoder encodeInteger:_chatTotalDuration forKey:SERIALIZE_KEY_CHATTOTALDURATION];
    
    [aCoder encodeInteger:_chatLossCount forKey:SERIALIZE_KEY_CHATLOSSCOUNT];
}

@end
