//
//  LoggerModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "LoggerModule.h"

@interface LoggerModule()
{
    DDFileLogger* _fileLogger;
}

@end

@implementation LoggerModule

SINGLETON(LoggerModule)

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"Logger Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"Logger Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    [DDLog addLogger:[DDASLLogger sharedInstance]];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    
    if (nil == _fileLogger)
    {
        _fileLogger = [[DDFileLogger alloc] init];
        _fileLogger.rollingFrequency = 60 * 60 * 24; // 24 hour rolling
        _fileLogger.logFileManager.maximumNumberOfLogFiles = 7;
        
        [DDLog addLogger:_fileLogger];
    }
    
    [[DDTTYLogger sharedInstance] setColorsEnabled:YES];
    
    DDLogVerbose(@"Logger Level is :%d", ddLogLevel);
}

-(void) releaseModule
{
    [DDLog removeAllLoggers];
    
    [super releaseModule];
}

-(void) startService
{
    DDLogVerbose(@"Module:%@ is started.", self.moduleIdentity);
    
    [super startService];
}

-(void) processService
{
    MODULE_DELAY
}

@end
