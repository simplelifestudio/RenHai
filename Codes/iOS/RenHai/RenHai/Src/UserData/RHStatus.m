//
//  RHStatus.m
//  RenHai
//
//  Created by DENG KE on 13-12-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHStatus.h"

@interface RHStatus()
{
    
}

@property (nonatomic) ServerServiceStatus serviceStatus;
@property (strong, nonatomic) RHStatusPeriod* statusPeriod;

@end

@implementation RHStatus

@synthesize serviceStatus = _serviceStatus;
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

        NSDictionary* statusPeriodDic = [dic objectForKey:MESSAGE_KEY_STATUSPERIOD];
        if (nil != statusPeriodDic)
        {
            _statusPeriod = [[RHStatusPeriod alloc] init];
            [_statusPeriod fromJSONObject:statusPeriodDic];
        }
        else
        {
            _statusPeriod = nil;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSNumber* oStatus = [NSNumber numberWithInteger:_serviceStatus];
    [dic setObject:oStatus forKey:MESSAGE_KEY_SERVICESTATUS];
    
    id oNull = [NSNull null];
    NSNumber* oServiceStatus = [NSNumber numberWithInteger:_serviceStatus];
    [dic setObject:oServiceStatus forKey:MESSAGE_KEY_SERVICESTATUS];
    
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
