//
//  RHWebRTC.m
//  RenHai
//
//  Created by DENG KE on 13-11-14.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHWebRTC.h"

#import "CBJSONUtils.h"

@interface RHWebRTC()
{
    
}

@property (strong, nonatomic) NSString* apiKey;
@property (strong, nonatomic) NSString* sessionId;
@property (strong, nonatomic) NSString* token;

@end

@implementation RHWebRTC

@synthesize apiKey = _apiKey;
@synthesize sessionId = _sessionId;
@synthesize token = _token;

#pragma mark - Public Methods

-(id) init
{
    if (self == [super init])
    {
        
    }
    
    return self;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        NSString* apiKey = [dic objectForKey:MESSAGE_KEY_APIKEY];
        if (nil != apiKey)
        {
            _apiKey = apiKey;
        }
        
        NSString* sessionId = [dic objectForKey:MESSAGE_KEY_SESSIONID];
        if (nil != sessionId)
        {
            _sessionId = sessionId;
        }
        
        NSString* token = [dic objectForKey:MESSAGE_KEY_TOKEN];
        if (nil != token)
        {
            _token = token;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    if (nil != _apiKey)
    {
        [dic setObject:_apiKey forKey:MESSAGE_KEY_APIKEY];
    }
    
    if (nil != _sessionId)
    {
        [dic setObject:_sessionId forKey:MESSAGE_KEY_SESSIONID];
    }
    
    if (nil != _token)
    {
        [dic setObject:_token forKey:MESSAGE_KEY_TOKEN];
    }
    
    return dic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

@end
