//
//  RHImpressCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHImpressCard.h"

#import "CBJSONUtils.h"
#import "RHMessage.h"

#define SERIALIZE_KEY_CARDID @"impressCard.impressCardId"
#define SERIALIZE_KEY_ASSESSLABELIST @"impressCard.assessLabelList"
#define SERIALIZE_KEY_IMPRESSLABELIST @"impressCard.impressLabelList"
#define SERIALIZE_KEY_CHATTOTALCOUNT @"impressCard.chatTotalCount"
#define SERIALIZE_KEY_CHATTOTALDURATION @"impressCard.chatTotalDuration"
#define SERIALIZE_KEY_CHATLOSSCOUNT @"impressCard.chatLossCount"

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

-(id) _getOChatTotalCount
{
    return [NSNumber numberWithInteger:_chatTotalCount];
}

-(id) _getOChatTotalDuration
{
    return [NSNumber numberWithInteger:_chatTotalDuration];
}

-(id) _getOChatLossCount
{
    return [NSNumber numberWithInteger:_chatLossCount];
}

-(id) _getOImpressLabelList
{
    id oLabelList = nil;
    
    if (nil == _impressLabelList)
    {
        oLabelList = [NSNull null];
    }
    else
    {
        oLabelList = _impressLabelList;
    }
    
    return oLabelList;
}

-(id) _getOImpressLabelListDic
{
    id labelListDic = nil;
    
    id oLabelList = [self _getOImpressLabelList];
    if (oLabelList == [NSNull null])
    {
        labelListDic = oLabelList;
    }
    else
    {
        labelListDic = [NSMutableArray arrayWithCapacity:_impressLabelList.count];
        for (RHImpressLabel* label in _impressLabelList)
        {
            NSDictionary* labelDic = label.toJSONObject;
            [labelListDic addObject:labelDic];
        }
    }
    
    return labelListDic;
}

-(id) _getOAssessLabelList
{
    id oLabelList = nil;
    
    if (nil == _assessLabelList)
    {
        oLabelList = [NSNull null];
    }
    else
    {
        oLabelList = _assessLabelList;
    }
    
    return oLabelList;
}

-(id) _getOAssessLabelListDic
{
    id labelListDic = nil;
    
    id oLabelList = [self _getOAssessLabelList];
    if (oLabelList == [NSNull null])
    {
        labelListDic = oLabelList;
    }
    else
    {
        labelListDic = [NSMutableArray arrayWithCapacity:_assessLabelList.count];
        for (RHImpressLabel* label in _assessLabelList)
        {
            NSDictionary* labelDic = label.toJSONObject;
            [labelListDic addObject:labelDic];
        }
    }
    
    return labelListDic;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oCardId = [dic objectForKey:MESSAGE_KEY_IMPRESSCARDID];
        if (nil != oCardId)
        {
            _impressCardId = ([NSNull null] != oCardId) ? ((NSNumber*)oCardId).integerValue : 0;
        }
        
        id assessLabelListArray = [dic objectForKey:MESSAGE_KEY_ASSESSLABELLIST];
        if (nil != assessLabelListArray)
        {
            if ([NSNull null] != assessLabelListArray)
            {
                NSMutableArray* labelArray = [NSMutableArray array];
                
                NSArray* dicArray = (NSArray*)assessLabelListArray;
                for (NSDictionary* labelDic in dicArray)
                {
                    RHImpressLabel* label = [[RHImpressLabel alloc] init];
                    [label fromJSONObject:labelDic];
                    
                    [labelArray addObject:label];
                }
                
                _assessLabelList = labelArray;
            }
            else
            {
                _assessLabelList = nil;
            }
        }
        
        id impressLabelListArray = [dic objectForKey:MESSAGE_KEY_IMPRESSLABELLIST];
        if (nil != impressLabelListArray)
        {
            if ([NSNull null] != impressLabelListArray)
            {
                NSMutableArray* labelArray = [NSMutableArray array];
                
                NSArray* dicArray = (NSArray*)impressLabelListArray;
                for (NSDictionary* labelDic in dicArray)
                {
                    RHImpressLabel* label = [[RHImpressLabel alloc] init];
                    [label fromJSONObject:labelDic];
                    
                    [labelArray addObject:label];
                }
                
                _impressLabelList = labelArray;
            }
            else
            {
                _impressLabelList = nil;
            }
        }
        
        id oChatTotalCount = [dic objectForKey:MESSAGE_KEY_CHATTOTALCOUNT];
        if (nil != oChatTotalCount)
        {
            _chatTotalCount = ([NSNull null] != oChatTotalCount) ? ((NSNumber*)oChatTotalCount).integerValue : 0;
        }
        
        id oChatTotalDuration = [dic objectForKey:MESSAGE_KEY_CHATTOTALDURATION];
        if (nil != oChatTotalDuration)
        {
            _chatTotalDuration = ([NSNull null] != oChatTotalDuration) ? ((NSNumber*)oChatTotalDuration).integerValue : 0;
        }
        
        id oChatLossCount = [dic objectForKey:MESSAGE_KEY_CHATLOSSCOUNT];
        if (nil != oChatLossCount)
        {
            _chatLossCount = ([NSNull null] != oChatLossCount) ? ((NSNumber*)oChatLossCount).integerValue : 0;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oCardId = [self _getOCardId];
    [dic setObject:oCardId forKey:MESSAGE_KEY_IMPRESSCARDID];
    
    id assessLabelListDic = [self _getOAssessLabelListDic];
    [dic setObject:assessLabelListDic forKey:MESSAGE_KEY_ASSESSLABELLIST];
    
    id impressLabelListDic = [self _getOImpressLabelListDic];
    [dic setObject:impressLabelListDic forKey:MESSAGE_KEY_IMPRESSLABELLIST];
    
    id oChatTotalCount = [self _getOChatTotalCount];
    [dic setObject:oChatTotalCount forKey:MESSAGE_KEY_CHATTOTALCOUNT];
    
    id oChatTotalDuration = [self _getOChatTotalDuration];
    [dic setObject:oChatTotalDuration forKey:MESSAGE_KEY_CHATTOTALDURATION];
    
    id oChatLossCount = [self _getOChatLossCount];
    [dic setObject:oChatLossCount forKey:MESSAGE_KEY_CHATLOSSCOUNT];
    
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
        _impressCardId = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CARDID];        
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
    [aCoder encodeInteger:_impressCardId forKey:SERIALIZE_KEY_CARDID];
    [aCoder encodeObject:_assessLabelList forKey:SERIALIZE_KEY_ASSESSLABELIST];
    [aCoder encodeObject:_impressLabelList forKey:SERIALIZE_KEY_IMPRESSLABELIST];
    [aCoder encodeInteger:_chatTotalCount forKey:SERIALIZE_KEY_CHATTOTALCOUNT];
    [aCoder encodeInteger:_chatTotalDuration forKey:SERIALIZE_KEY_CHATTOTALDURATION];
    [aCoder encodeInteger:_chatLossCount forKey:SERIALIZE_KEY_CHATLOSSCOUNT];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHImpressCard*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

@end
