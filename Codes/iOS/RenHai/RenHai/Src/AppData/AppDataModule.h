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

#pragma mark - Common
-(NSMutableDictionary*) persistentDomainForName:(NSString*) name;
-(void) setValueForKeyInPersistentDomain:(id) value forKey:(NSString*) key inPersistentDomain:(NSString*) domain;
-(id) getValueForKeyInPersistentDomain:(NSString*) key inPersistentDomain:(NSString*) domain;
-(void) resetDefaultsInPersistentDomain:(NSString*) domain;
-(void) resetDefaults;

#pragma mark - App
-(BOOL) isAppLaunchedBefore;
-(void) recordAppLaunchedBefore;

@end
