//
//  RHImpressCard.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHImpressCard.h"

#define SERIALIZE_KEY_CARDID @"ImpressCard.cardId"
#define SERIALIZE_KEY_LABELIST @"ImpressCard.labelList"
#define SERIALIZE_KEY_CHATTOTALCOUNT @"ImpressCard.chatTotalCount"
#define SERIALIZE_KEY_CHATTOTALDURATION @"ImpressCard.chatTotalDuration"
#define SERIALIZE_KEY_CHATLOSSCOUNT @"ImpressCard.chatLossCount"

#define JSON_KEY_CARDID @"cardId"
#define JSON_KEY_LABELLIST @"labelList"
#define JSON_KEY_CHATTOTALCOUNT @"chatTotalCount"
#define JSON_KEY_CHATTOTALDURATION @"chatTotalDuration"
#define JSON_KEY_CHATLOSSCOUNT @"chatLossCount"

@implementation RHImpressCard

@synthesize cardId = _cardId;
@synthesize labelList = _labelList;
@synthesize chatTotalCount = _chatTotalCount;
@synthesize chatTotalDuration = _chatTotalDuration;
@synthesize chatLossCount = _chatLossCount;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _labelList = [NSMutableArray array];
    }
    
    return self;
}

#pragma mark - CBJSONable

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSNumber* oCardId = [NSNumber numberWithInteger:_cardId];
    [dic setObject:oCardId forKey:JSON_KEY_CARDID];
    
    [dic setObject:_labelList forKey:JSON_KEY_LABELLIST];
    
    NSNumber* oChatTotalCount = [NSNumber numberWithInteger:_chatTotalCount];
    [dic setObject:oChatTotalCount forKey:JSON_KEY_CHATTOTALCOUNT];
    
    NSNumber* oChatTotalDuration = [NSNumber numberWithInteger:_chatTotalDuration];
    [dic setObject:oChatTotalDuration forKey:JSON_KEY_CHATTOTALDURATION];
    
    NSNumber* oChatLossCount = [NSNumber numberWithInteger:_chatLossCount];
    [dic setObject:oChatLossCount forKey:JSON_KEY_CHATLOSSCOUNT];
    
    return dic;
}

#pragma mark - CBSerializable

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        NSNumber* oCardId = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CARDID];
        _cardId = oCardId.integerValue;
        
        _labelList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_LABELIST];
        
        NSNumber* oChatTotalCount = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CHATTOTALCOUNT];
        _chatTotalCount = oChatTotalCount.integerValue;
        
        NSNumber* oChatTotalDuration = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CHATTOTALDURATION];
        _chatTotalDuration = oChatTotalDuration.integerValue;
        
        NSNumber* oChatLossCount = [aDecoder decodeObjectForKey:SERIALIZE_KEY_CHATLOSSCOUNT];
        _chatLossCount = oChatLossCount.integerValue;
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    NSNumber* oCardId = [NSNumber numberWithInteger:_cardId];
    [aCoder encodeObject:oCardId forKey:SERIALIZE_KEY_CARDID];
    
    [aCoder encodeObject:_labelList forKey:SERIALIZE_KEY_LABELIST];
    
    NSNumber* oChatTotalCount = [NSNumber numberWithInteger:_chatTotalCount];
    [aCoder encodeObject:oChatTotalCount forKey:SERIALIZE_KEY_CHATTOTALCOUNT];
    
    NSNumber* oChatTotalDuration = [NSNumber numberWithInteger:_chatTotalDuration];
    [aCoder encodeObject:oChatTotalDuration forKey:SERIALIZE_KEY_CHATTOTALDURATION];
    
    NSNumber* oChatLossCount = [NSNumber numberWithInteger:_chatLossCount];
    [aCoder encodeObject:oChatLossCount forKey:SERIALIZE_KEY_CHATLOSSCOUNT];
}

@end
