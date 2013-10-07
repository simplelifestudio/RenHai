//
//  RHServerDeviceCapacity.m
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHServerDeviceCapacity.h"

#import "CBJSONUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_ONLINE @"deviceCapacity.online"
#define SERIALIZE_KEY_RANDOM @"deviceCapacity.random"
#define SERIALIZE_KEY_INTEREST @"deviceCapacity.interest"

@interface RHServerDeviceCapacity()
{
    
}

@property (nonatomic, readwrite) NSUInteger online;
@property (nonatomic, readwrite) NSUInteger random;
@property (nonatomic, readwrite) NSUInteger interest;

@end

@implementation RHServerDeviceCapacity

@synthesize online = _online;
@synthesize random = _random;
@synthesize interest = _interest;

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
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_online forKey:SERIALIZE_KEY_ONLINE];
    [aCoder encodeInteger:_random forKey:SERIALIZE_KEY_RANDOM];
    [aCoder encodeInteger:_interest forKey:SERIALIZE_KEY_INTEREST];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHServerDeviceCapacity*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
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

@end
