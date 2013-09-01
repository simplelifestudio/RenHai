//
//  AppDataModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

@interface AppDataModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@end
