//
//  LoggerModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#import "DDLog.h"
#import "DDASLLogger.h"
#import "DDTTYLogger.h"
#import "DDFileLogger.h"

/*
    Starting with Xcode 4, the DEBUG preprocessor macro is automatically set when building for debug (as opposed to release). 
    So you can use this to automatically get different log levels depending upon your current build configuration.
 */
//#ifdef DEBUG
//static const int ddLogLevel = LOG_LEVEL_VERBOSE;
//#else
//static const int ddLogLevel = LOG_LEVEL_WARN;
//#endif

@interface LoggerModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@end
