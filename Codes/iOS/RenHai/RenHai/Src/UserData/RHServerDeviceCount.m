//
//  RHServerDeviceCount.m
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHServerDeviceCount.h"

#import "CBJSONUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_ONLINE @"deviceCount.online"
#define SERIALIZE_KEY_RANDOM @"deviceCountrandom"
#define SERIALIZE_KEY_INTEREST @"deviceCount.interest"
#define SERIALIZE_KEY_CHAT @"deviceCount.chat"
#define SERIALIZE_KEY_RANDOMCHAT @"deviceCount.randomChat"
#define SERIALIZE_KEY_INTERESTCHAT @"deviceCount.interestChat"

@interface RHServerDeviceCount()
{
    
}

@property (nonatomic, readwrite) NSUInteger online;
@property (nonatomic, readwrite) NSUInteger random;
@property (nonatomic, readwrite) NSUInteger interest;
@property (nonatomic, readwrite) NSUInteger chat;
@property (nonatomic, readwrite) NSUInteger randomChat;
@property (nonatomic, readwrite) NSUInteger interestChat;

@end

@implementation RHServerDeviceCount

@synthesize online = _online;
@synthesize random = _random;
@synthesize interest = _interest;
@synthesize chat = _chat;
@synthesize randomChat = _randomChat;
@synthesize interestChat = _interestChat;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {

    }
    
    return self;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oOnline = [dic objectForKey:MESSAGE_KEY_ONLINE];
        if (nil != oOnline)
        {
            _online = ([NSNull null] != oOnline) ? ((NSNumber*)oOnline).integerValue : 0;
        }
        
        id oRandom = [dic objectForKey:MESSAGE_KEY_RANDOM];
        if (nil != oRandom)
        {
            _random = ([NSNull null] != oRandom) ? ((NSNumber*)oRandom).integerValue : 0;
        }
        
        id oInterest = [dic objectForKey:MESSAGE_KEY_INTEREST];
        if (nil != oInterest)
        {
            _interest = ([NSNull null] != oInterest) ? ((NSNumber*)oInterest).integerValue : 0;
        }
        
        id oChat = [dic objectForKey:MESSAGE_KEY_CHAT];
        if (nil != oChat)
        {
            _chat = ([NSNull null] != oChat) ? ((NSNumber*)oChat).integerValue : 0;
        }
        
        id oRandomChat = [dic objectForKey:MESSAGE_KEY_RANDOMCHAT];
        if (nil != oRandomChat)
        {
            _randomChat = ([NSNull null] != oRandomChat) ? ((NSNumber*)oRandomChat).integerValue : 0;
        }
        
        id oInterestChat = [dic objectForKey:MESSAGE_KEY_INTERESTCHAT];
        if (nil != oInterestChat)
        {
            _interestChat = ([NSNull null] != oInterestChat) ? ((NSNumber*)oInterestChat).integerValue : 0;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oOnline = [self _getOOnline];
    [dic setObject:oOnline forKey:MESSAGE_KEY_ONLINE];
    
    id oRandom = [self _getORandom];
    [dic setObject:oRandom forKey:MESSAGE_KEY_RANDOM];
    
    id oInterest = [self _getOInterest];
    [dic setObject:oInterest forKey:MESSAGE_KEY_INTEREST];
    
    id oChat = [self _getOChat];
    [dic setObject:oChat forKey:MESSAGE_KEY_CHAT];
    
    id oRandomChat = [self _getORandomChat];
    [dic setObject:oRandomChat forKey:MESSAGE_KEY_RANDOMCHAT];
    
    id oInterestChat = [self _getOInterestChat];
    [dic setObject:oInterestChat forKey:MESSAGE_KEY_INTERESTCHAT];
    
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
        _online = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_ONLINE];
        _random = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_RANDOM];
        _interest = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_INTEREST];
        _chat = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CHAT];
        _randomChat = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_RANDOMCHAT];
        _interestChat = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_INTERESTCHAT];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_online forKey:SERIALIZE_KEY_ONLINE];
    [aCoder encodeInteger:_random forKey:SERIALIZE_KEY_RANDOM];
    [aCoder encodeInteger:_interest forKey:SERIALIZE_KEY_INTEREST];
    [aCoder encodeInteger:_chat forKey:SERIALIZE_KEY_CHAT];
    [aCoder encodeInteger:_randomChat forKey:SERIALIZE_KEY_RANDOMCHAT];
    [aCoder encodeInteger:_interestChat forKey:SERIALIZE_KEY_INTERESTCHAT];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHServerDeviceCount*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

#pragma mark - Private Methods

-(id) _getOOnline
{
    return [NSNumber numberWithInteger:_online];
}

-(id) _getORandom
{
    return [NSNumber numberWithInteger:_random];
}

-(id) _getOInterest
{
    return [NSNumber numberWithInteger:_interest];
}

-(id) _getOChat
{
    return [NSNumber numberWithInteger:_chat];
}

-(id) _getORandomChat
{
    return [NSNumber numberWithInteger:_randomChat];
}

-(id) _getOInterestChat
{
    return [NSNumber numberWithInteger:_interestChat];
}

@end
