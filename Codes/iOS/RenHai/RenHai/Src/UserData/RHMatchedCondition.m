//
//  RHMatchedCondition.m
//  RenHai
//
//  Created by DENG KE on 13-11-14.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHMatchedCondition.h"

#import "CBJSONUtils.h"

@interface RHMatchedCondition()
{
    
}

@property (strong, nonatomic) RHInterestLabel* interestLabel;

@end

@implementation RHMatchedCondition

@synthesize interestLabel = _interestLabel;

#pragma mark - Public Methods

-(id) init
{
    if (self == [super init])
    {
        _interestLabel = [[RHInterestLabel alloc] init];
    }
    
    return self;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        [_interestLabel fromJSONObject:dic];
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSDictionary* interestLabelDic = [_interestLabel toJSONObject];
    [dic setObject:interestLabelDic forKey:MESSAGE_KEY_MATCHEDCONDITION];
    
    return dic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

@end
