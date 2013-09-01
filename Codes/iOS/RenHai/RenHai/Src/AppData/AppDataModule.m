//
//  AppDataModule.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "AppDataModule.h"

@implementation AppDataModule

SINGLETON(AppDataModule)

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"AppData Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"AppData Module Thread", nil)];
    [self setKeepAlive:FALSE];
}

-(void) releaseModule
{
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
