//
//  CBModuleAbstractImpl.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

@implementation CBModuleAbstractImpl

// Members of CBModule protocol
@synthesize isIndividualThreadNecessary;
@synthesize keepAlive;

@synthesize moduleIdentity;
@synthesize serviceThread;

@synthesize moduleWeightFactor = _moduleWeightFactor;

- (id)initWithIsIndividualThreadNecessary:(BOOL) necessary
{
    self.isIndividualThreadNecessary = necessary;
    
    return [self init];
}

- (id)init
{
    self = [super init];
    if (self) 
    {
        _moduleWeightFactor = 0.1;
    }
    
    return self;
}

- (void)dealloc
{
    [self releaseModule];
}

// Method of CBModule protocol
-(void) initModule
{
    [self setModuleIdentity:MODULE_IDENTITY_ABSTRACT_IMPL];
    [self.serviceThread setName:MODULE_IDENTITY_ABSTRACT_IMPL];
    [self setKeepAlive:FALSE];
}

// Method of CBModule protocol
-(void) releaseModule
{

}

// Method of CBModule protocol
-(void) startService
{    
    self.keepAlive = TRUE;

    if (self.isIndividualThreadNecessary) 
    {
        self.serviceThread = [[NSThread alloc] initWithTarget:self selector:@selector(processService) object:nil];
        
        [self.serviceThread start];
    }
    else
    {
        [self processService];
    }
}

// Method of CBModule protocol
-(void) pauseService
{

}

// Method of CBModule protocol
-(void) serviceWithIndividualThread
{
    DDLogInfo(@"Module:%@ is in service with individual thread.", self.moduleIdentity);
    // Insert business logic here
    // ***** WARNING: Codes should release CPU control in intermittently! *****
}

// Method of CBModule protocol
-(void) serviceWithCallingThread
{
    DDLogInfo(@"Module:%@ is in service with calling thread.", self.moduleIdentity);
    // Insert business logic here
}

// Method of CBModule protocol
-(void) processService
{
    if(self.isIndividualThreadNecessary)
    {
        // Every NSThread need an individual NSAutoreleasePool to manage memory.
        @autoreleasepool
        {
            while (self.keepAlive)
            {
                [self serviceWithIndividualThread];
            }
        }
    }
    else
    {
        [self serviceWithCallingThread];
    }
}

// Method of CBModule protocol
-(void) stopService
{
    self.keepAlive = FALSE;
}

@end
