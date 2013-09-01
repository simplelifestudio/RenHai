//
//  CBModuleManager.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBModule.h"
#import "CBSharedInstance.h"

@interface CBModuleManager : NSObject <CBSharedInstance, UIApplicationDelegate>

-(void) registerModule:(id<CBModule>) module;
-(void) unregisterModule:(id<CBModule>) module;

-(void) initModules;
-(void) startModuleServices;
-(void) processModuleServices;
-(void) pauseModuleServices;
-(void) stopModuleServices;
-(void) releaseModules;

-(NSArray*) moduleList;

@end