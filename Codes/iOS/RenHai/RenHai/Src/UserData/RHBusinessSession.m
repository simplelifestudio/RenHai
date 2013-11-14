//
//  RHBusinessSession.m
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHBusinessSession.h"

#import "CBJSONUtils.h"

@interface RHBusinessSession()
{
    
}

@property (nonatomic, strong) RHDevice* device;
@property (nonatomic, strong) RHMatchedCondition* matchedCondition;
@property (nonatomic, strong) RHWebRTC* webrtc;

@end

@implementation RHBusinessSession

@synthesize businessSessionId = _businessSessionId;
@synthesize businessType = _businessType;
@synthesize operationType = _operationType;

@synthesize device = _device;
@synthesize matchedCondition = _matchedCondition;
@synthesize webrtc = _webrtc;

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
    if (nil != dic)
    {
        id oDevice = [dic objectForKey:MESSAGE_KEY_DEVICE];
        if (nil != oDevice)
        {
            NSDictionary* deviceDic = [NSDictionary dictionaryWithObject:oDevice forKey:MESSAGE_KEY_DEVICE];
            _device = [[RHDevice alloc] init];
            [_device fromJSONObject:deviceDic];
        }
        
        id oMatchedCondition = [dic objectForKey:MESSAGE_KEY_MATCHEDCONDITION];
        if (nil != oMatchedCondition)
        {
            _matchedCondition = [[RHMatchedCondition alloc] init];
            [_matchedCondition fromJSONObject:oMatchedCondition];
        }
        
        id oWebRTC = [dic objectForKey:MESSAGE_KEY_WEBRTC];
        if (nil != oWebRTC)
        {
            _webrtc = [[RHWebRTC alloc] init];
            [_webrtc fromJSONObject:oWebRTC];
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    if (nil != _device)
    {
        NSDictionary* deviceDic = [_device toJSONObject];
        [dic setObject:deviceDic forKey:MESSAGE_KEY_DEVICE];
    }
    
    if (nil != _matchedCondition)
    {
        NSDictionary* matchedConditionDic = [_matchedCondition toJSONObject];
        [dic setObject:matchedConditionDic forKey:MESSAGE_KEY_MATCHEDCONDITION];
    }
    
    if (nil != _webrtc)
    {
        NSDictionary* webrtcDic = [_webrtc toJSONObject];
        [dic setObject:webrtcDic forKey:MESSAGE_KEY_WEBRTC];
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
