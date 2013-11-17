//
//  UserDataModule.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#import "RHProxy.h"
#import "RHDevice.h"
#import "RHServer.h"
#import "RHBusinessSession.h"

@interface UserDataModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (atomic, strong, readonly) RHProxy* proxy;
@property (atomic, strong, readonly) RHDevice* device;
@property (atomic, strong, readonly) RHServer* server;
@property (atomic, strong, readonly) RHBusinessSession* businessSession;

-(NSString*) dataDirectory;

-(BOOL) saveUserData;
-(BOOL) loadUserData;
-(void) initUserData;

@end
