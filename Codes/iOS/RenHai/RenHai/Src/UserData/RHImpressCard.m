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
    id oChatTotalCount = nil;
    if (0 >= _chatTotalCount)
    {
        oChatTotalCount = [NSNull null];
    }
    else
    {
        oChatTotalCount = [NSNumber numberWithInteger:_chatTotalCount];
    }
    return oChatTotalCount;
}

-(id) _getOChatTotalDuration
{
    id oChatTotalDuration = nil;
    if (0 >= _chatTotalDuration)
    {
        oChatTotalDuration = [NSNull null];
    }
    else
    {
        oChatTotalDuration = [NSNumber numberWithInteger:_chatTotalDuration];
    }
    return oChatTotalDuration;
}

-(id) _getOChatLossCount
{
    id oChatLossCount = nil;
    if (0 >= _chatLossCount)
    {
        oChatLossCount = [NSNull null];
    }
    else
    {
        oChatLossCount = [NSNumber numberWithInteger:_chatLossCount];
    }
    return oChatLossCount;
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
//    RHImpressCard* copy = [[RHImpressCard alloc] init];
//    
//    copy.impressCardId = _impressCardId;
//    copy.assessLabelList = [_assessLabelList copy];
//    copy.impressLabelList = [_impressLabelList copy];
//    copy.chatTotalCount = _chatTotalCount;
//    copy.chatTotalDuration = _chatTotalDuration;
//    copy.chatLossCount = _chatLossCount;
//    
//    return copy;

    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHImpressCard*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
//    RHImpressCard* mutableCopy = [[RHImpressCard alloc] init];
//
//    mutableCopy.impressCardId = _impressCardId;
//    mutableCopy.assessLabelList = [_assessLabelList mutableCopy];
//    mutableCopy.impressLabelList = [_impressLabelList mutableCopy];
//    mutableCopy.chatTotalCount = _chatTotalCount;
//    mutableCopy.chatTotalDuration = _chatTotalDuration;
//    mutableCopy.chatLossCount = _chatLossCount;
//    
//    return mutableCopy;

    return [self copyWithZone:zone];
}

@end
