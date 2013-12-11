//
//  RHProxy.m
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHProxy.h"

#import "CBJSONUtils.h"

@interface RHProxy()
{
    
}

@property (strong, nonatomic) NSString* serverId;
@property (strong, nonatomic) RHStatus* status;
@property (strong, nonatomic) RHAddress* address;
@property (strong, nonatomic) NSString* broadcast;

@end

@implementation RHProxy

@synthesize address = _address;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        [self _setupInstance];
    }
    
    return self;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    dic = [dic objectForKey:MESSAGE_KEY_SERVER];
    if (nil != dic)
    {
        NSString* serverId = (NSString*)[dic objectForKey:MESSAGE_KEY_SERVERID];
        if (nil != serverId)
        {
            _serverId = serverId;
        }
        
        NSDictionary* statusDic = [dic objectForKey:MESSAGE_KEY_STATUS];
        if (nil != statusDic)
        {
            _status = [[RHStatus alloc] init];
            [_status fromJSONObject:statusDic];
        }
        
        NSDictionary* addressDic = [dic objectForKey:MESSAGE_KEY_ADDRESS];
        if (nil != addressDic)
        {
            _address = [[RHAddress alloc] init];
            [_address fromJSONObject:addressDic];
        }
        
        NSString* broadcast = (NSString*)[dic objectForKey:MESSAGE_KEY_BROADCAST];
        _broadcast = broadcast;
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oNull = [NSNull null];
    
    [dic setObject:_serverId forKey:MESSAGE_KEY_SERVERID];
    
    if (nil != _status)
    {
        NSDictionary* statusDic = _status.toJSONObject;
        [dic setObject:statusDic forKey:MESSAGE_KEY_STATUS];
    }
    else
    {
        [dic setObject:oNull forKey:MESSAGE_KEY_STATUS];
    }
    
    if (nil != _address)
    {
        NSDictionary* addressDic = _address.toJSONObject;
        [dic setObject:addressDic forKey:MESSAGE_KEY_ADDRESS];
    }
    else
    {
        [dic setObject:oNull forKey:MESSAGE_KEY_ADDRESS];
    }
    
    if (nil != _broadcast)
    {
        [dic setObject:_broadcast forKey:MESSAGE_KEY_BROADCAST];
    }
    else
    {
        [dic setObject:oNull forKey:MESSAGE_KEY_BROADCAST];
    }

    return dic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    
}

@end
