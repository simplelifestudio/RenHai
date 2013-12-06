//
//  RHAddress.m
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHAddress.h"

#import "CBJSONUtils.h"

#define LOCAL_SERVER_ENABLED 0
#define LOCAL_SERVER_IP @"192.168.1.2"

@interface RHAddress()
{
    
}

@property (strong, nonatomic) NSString* protocol;
@property (strong, nonatomic) NSString* ip;
@property (nonatomic) NSUInteger port;
@property (strong, nonatomic) NSString* path;

@end

@implementation RHAddress

@synthesize protocol = _protocol;
@synthesize ip = _ip;
@synthesize port = _port;
@synthesize path = _path;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        [self _setupInstance];
    }
    
    return self;
}

-(NSString*) fullAddress
{
    NSMutableString* address = [NSMutableString string];
    
    NSString* protocol = (nil != _protocol) ? _protocol : @"http";
    if (nil != protocol)
    {
        [address appendString:protocol];
        [address appendString:@"://"];
        if (nil != _ip)
        {
            [address appendString:_ip];
            
            if (0 < _port)
            {
                [address appendString:@":"];
                [address appendString:[NSString stringWithFormat:@"%d", _port]];
                
                if (nil != _path)
                {
                    [address appendString:_path];
                }
            }
        }
    }
    
    return address;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        _ip = [dic objectForKey:MESSAGE_KEY_IP];
        if (LOCAL_SERVER_ENABLED)
        {
            _ip = LOCAL_SERVER_IP;
        }
        
        NSNumber* oPort = (NSNumber*)[dic objectForKey:MESSAGE_KEY_PORT];
        if (nil != oPort)
        {
            _port = oPort.integerValue;
        }
        
        _path = [dic objectForKey:MESSAGE_KEY_PATH];
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    if (nil != _ip)
    {
        [dic setObject:_ip forKey:MESSAGE_KEY_IP];
    }
    
    if (0 < _port)
    {
        NSNumber* oPort = [NSNumber numberWithInteger:_port];
        [dic setObject:oPort forKey:MESSAGE_KEY_PORT];
    }
    
    if (nil != _path)
    {
        [dic setObject:_path forKey:MESSAGE_KEY_PATH];
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
