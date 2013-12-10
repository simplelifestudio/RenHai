//
//  BusinessStatusModule.h
//  RenHai
//
//  Created by DENG KE on 13-10-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

#import "CBSharedInstance.h"

#import "BusinessStatus.h"

@interface BusinessStatusModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

-(BusinessStatusIdentifier) currentBusinessStatusIdentifier;
-(BusinessStatus*) currentBusinessStatus;

-(void) recordAppMessage:(AppMessageIdentifier) appMessageId;

-(void) recordServerNotification:(ServerNotificationIdentifier) serverNotificationId;

-(void) recordRemoteStatusAbnormal;

@end
