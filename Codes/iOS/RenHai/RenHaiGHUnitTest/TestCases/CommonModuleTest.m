//
//  CommonModuleTest.m
//  RenHai
//
//  Created by DENG KE on 13-9-3.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CommonModuleTest.h"

#import "CBJSONUtils.h"
#import "CBStringUtils.h"
#import "CBMathUtils.h"

#import "TestTitle.h"

@interface CommonModuleTest()

@end

@implementation CommonModuleTest

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

-(void) testRandomString
{
    NSString *randomStr = [CBStringUtils randomString:32];
    NSLog(@"random string is %@", randomStr);
}

-(void) testNullValueInJSON
{
    NSDictionary* dic = [NSDictionary dictionaryWithObject:[NSNull null] forKey:@"testKey"];
    NSString* str = [CBJSONUtils toJSONString:dic];
    NSLog(@"[NSNull null] in NSDictionary: %@", dic);
    NSLog(@"[NSNull null] in JSON string: %@", str);
}

-(void) testSplitIntegerByUnit
{
    NSInteger intVal = 12345;
    NSMutableArray* unitVals = [NSMutableArray array];
    NSArray* array = [CBMathUtils splitIntegerByUnit:intVal array:unitVals];
    GHTestLog(@"array: %@", array);
}

@end
