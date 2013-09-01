//
//  CBModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#define MODULE_DELAY usleep(100000);

@protocol CBModule <NSObject>

@property BOOL isIndividualThreadNecessary;
@property BOOL keepAlive;

@property (nonatomic, strong) NSString *moduleIdentity;
@property (nonatomic, strong) NSThread *serviceThread;

@property (nonatomic) float moduleWeightFactor;

-(void) initModule;
-(void) startService;
-(void) processService;
-(void) pauseService;
-(void) serviceWithIndividualThread;
-(void) serviceWithCallingThread;
-(void) stopService;
-(void) releaseModule;

@end