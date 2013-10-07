//
//  UserDataModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#import "RHDevice.h"

@interface UserDataModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (atomic, strong, readonly) RHDevice* device;

-(NSString*) dataDirectory;

-(BOOL) saveUserData;
-(BOOL) loadUserData;
-(void) initUserData;

@end
