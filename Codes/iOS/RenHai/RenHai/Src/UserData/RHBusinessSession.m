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
        [self _setupInstance];
    }
    
    return self;
}

-(void) addParter:(RHDevice*) device
{
    if (nil != device)
    {
        [_chatParters addObject:device];
    }
}

-(RHDevice*) getPartner
{
    RHDevice* device = nil;
    
    if (0 < _chatParters.count)
    {
        device = [_chatParters objectAtIndex:0];
    }
    
    return device;
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _chatParters = [NSMutableArray array];
}

@end
