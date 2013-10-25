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

typedef enum
{
    AppBusinessStatus_Disconnected = 0,
    AppBusinessStatus_Connected,
    AppBusinessStatus_AppDataSyncCompleted,
    AppBusinessStatus_EnterPoolCompleted,
    AppBusinessStatus_SessionBindCompeleted,
    AppBusinessStatus_ChatAgreeCompleted,
    AppBusinessStatus_ChatEndCompleleted,
//    AppBusinessStatus_ChatAssessCompleleted
}
AppBusinessStatus;

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

#pragma mark - Device
-(NSString*) deviceSn;
-(NSString*) deviceModel;
-(NSString*) osVersion;
-(NSString*) appVersion;
-(BOOL) isJailed;

#pragma mark - AppBusinessStatus
-(AppBusinessStatus) currentAppBusinessStatus;
-(void) updateAppBusinessStatus:(AppBusinessStatus) status;

@end
