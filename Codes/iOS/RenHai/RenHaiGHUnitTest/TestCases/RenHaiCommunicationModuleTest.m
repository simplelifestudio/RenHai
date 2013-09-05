//
//  RenHaiCommunicationModuleTest.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RenHaiCommunicationModuleTest.h"

#import "CBStringUtils.h"

@implementation RenHaiCommunicationModuleTest

-(void) testRandomString
{
    NSString *randomStr = [CBStringUtils randomString:32];
    NSLog(@"random string is %@", randomStr);
}

@end
