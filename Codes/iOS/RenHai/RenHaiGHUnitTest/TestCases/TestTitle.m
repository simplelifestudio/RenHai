//
//  TestTitle.m
//  RenHai
//
//  Created by DENG KE on 13-9-8.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "TestTitle.h"

@interface TestTitle()

@property (nonatomic, strong, readwrite) TestObj* obj;

@end

@implementation TestTitle

-(id) init
{
    if (self = [super init])
    {
        _obj = [[TestObj alloc] init];
    }
    return self;
}

-(NSString*) objPassword
{
    return _obj.password;
}

@end
