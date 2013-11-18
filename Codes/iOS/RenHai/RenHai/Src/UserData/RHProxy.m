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

@property (nonatomic) ServerServiceStatus serviceStatus;
@property (strong, nonatomic) RHServiceAddress* serviceAddress;
@property (strong, nonatomic) RHStatusPeriod* statusPeriod;

@end

@implementation RHProxy

@synthesize serviceStatus = _serviceStatus;
@synthesize serviceAddress = _serviceAddress;
@synthesize statusPeriod = _statusPeriod;

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
        NSNumber* oStatus = (NSNumber*)[dic objectForKey:MESSAGE_KEY_SERVICESTATUS];
        if (nil != oStatus)
        {
            _serviceStatus = oStatus.integerValue;
        }
        
        NSDictionary* serviceAddressDic = [dic objectForKey:MESSAGE_KEY_SERVICEADDRESS];
        if (nil != serviceAddressDic)
        {
            _serviceAddress = [[RHServiceAddress alloc] init];
            [_serviceAddress fromJSONObject:serviceAddressDic];
        }
        
        NSDictionary* statusPeriodDic = [dic objectForKey:MESSAGE_KEY_STATUSPERIOD];
        if (nil != statusPeriodDic)
        {
            _statusPeriod = [[RHStatusPeriod alloc] init];
            [_statusPeriod fromJSONObject:statusPeriodDic];
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSNumber* oStatus = [NSNumber numberWithInteger:_serviceStatus];
    [dic setObject:oStatus forKey:MESSAGE_KEY_SERVICESTATUS];
    
    id oNull = [NSNull null];
    if (nil != _serviceAddress)
    {
        NSDictionary* serviceAddressDic = _serviceAddress.toJSONObject;
        [dic setObject:serviceAddressDic forKey:MESSAGE_KEY_SERVICEADDRESS];
    }
    else
    {
        [dic setObject:oNull forKey:MESSAGE_KEY_SERVICEADDRESS];
    }
    
    if (nil != _statusPeriod)
    {
        NSDictionary* statusPeriodDic = _statusPeriod.toJSONObject;
        [dic setObject:statusPeriodDic forKey:MESSAGE_KEY_STATUSPERIOD];
    }
    else
    {
        [dic setObject:oNull forKey:MESSAGE_KEY_STATUSPERIOD];
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
