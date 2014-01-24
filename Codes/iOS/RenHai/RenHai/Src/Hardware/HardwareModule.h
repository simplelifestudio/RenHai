//
//  HardwareModule.h
//  RenHai
//
//  Created by DENG KE on 14-1-23.
//  Copyright (c) 2014年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBModuleAbstractImpl.h"

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

@interface HardwareModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

-(void) enableProximitySensor;
-(void) disableProximitySensor;

@end
