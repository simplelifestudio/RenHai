//
//  RHBusinessSession.m
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHBusinessSession.h"

@implementation RHBusinessSession

@synthesize businessSessionId = _businessSessionId;
@synthesize businessType = _businessType;
@synthesize chatParters = _chatParters;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        
    }
    
    return self;
}

#pragma mark - Private Methods

@end
