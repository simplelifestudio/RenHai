//
//  RenHaiCommonModuleTest.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-3.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RenHaiCommonModuleTest.h"

#import "CBJSONUtils.h"

#import "TestTitle.h"

@interface RenHaiCommonModuleTest()

@end

@implementation RenHaiCommonModuleTest

-(void) testNSDictionaryToJSONString
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];    
    [dic setObject:@"Patrick" forKey:@"name"];
    [dic setObject:@"Male" forKey:@"sex"];
    
    NSString* jsonString = [CBJSONUtils toJSONString:dic];
    
    NSLog(@"JSON String: %@", jsonString);
}

-(void) testJSONStringToNSDictionary
{
    NSString* jsonString = @"{\"sex\":\"Male\",\"name\":\"Patrick\"}";
    NSDictionary* dic = [CBJSONUtils toJSONObject:jsonString];
    
    NSLog(@"JSON Object: %@", dic);
}

-(void) testDuplicateMemberDeclaration
{
    TestTitle* o = [[TestTitle alloc] init];
    o.obj.password = @"Aloha";
    NSLog(@"password = %@", [o objPassword]);
}

@end
