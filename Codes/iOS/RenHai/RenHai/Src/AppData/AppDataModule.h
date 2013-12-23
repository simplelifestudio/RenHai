//
//  AppDataModule.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#define NAMESPACE_APP @"com.simplelife.RenHai"
#define NAMESPACE_APP_CONST_CHARS "com.simplelife.RenHai"
#define MODULE_DELAY usleep(100000);

#define BUNDLE_KEY_SHORTVERSION @"CFBundleShortVersionString"
#define BUNDLE_KEY_BUNDLEVERSION @"CFBundleVersion"

#define KEYCHAIN_SERVICE_DEVICE NAMESPACE_APP
#define KEYCHAIN_ACCOUNT_IDFV @"idfv"

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
-(void) resetAppLaunchedBefore;

-(BOOL) isUserAgreementAccepted;
-(void) recordUserAgreementAccepted;
-(void) resetUserAgreementAccepted;

#pragma mark - Device
-(NSString*) deviceSn;
-(NSString*) deviceModel;
-(NSString*) osVersion;
-(NSString*) appVersion;
-(NSString*) appFullVerion;
-(NSUInteger) appBuild;
-(BOOL) isJailed;

@end
