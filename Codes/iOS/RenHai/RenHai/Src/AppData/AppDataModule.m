
//
//  AppDataModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "AppDataModule.h"

#import "SSKeychain.h"

#import "UIDevice+CBDeviceExtends.h"

#define PERSISTENTDOMAIN_APPDATA @"com.simplelife.RenHai.appdata"
#define APPDATA_KEY_APPLAUNCHEDBEFORE @"appLaunchedBefore"

@interface AppDataModule()
{
    NSUserDefaults* _userDefaults;
    
//    AppBusinessStatus _appBusinessStatus;
}

@end

@implementation AppDataModule

SINGLETON(AppDataModule)

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"AppData Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"AppData Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _userDefaults = [NSUserDefaults standardUserDefaults];
    
//    _appBusinessStatus = AppBusinessStatus_Disconnected;
}

-(void) releaseModule
{
    [super releaseModule];
}

-(void) startService
{
    DDLogInfo(@"Module:%@ is started.", self.moduleIdentity);
    
    [super startService];
}

-(void) processService
{
    MODULE_DELAY
}

#pragma mark - Common

-(void) resetDefaults
{
    [self resetDefaultsInPersistentDomain:PERSISTENTDOMAIN_APPDATA];
}

-(void) resetDefaultsInPersistentDomain:(NSString*) domain
{
    if (nil != domain && 0 < domain.length)
    {
        [_userDefaults removePersistentDomainForName:domain];
        NSDictionary* newDic = [NSDictionary dictionary];
        [_userDefaults setPersistentDomain:newDic forName:domain];
        DDLogInfo(@"AppData in persistent domain: %@ has been reset.", domain);
    }
}

-(NSMutableDictionary*) persistentDomainForName:(NSString*) name
{
    NSMutableDictionary* mutableDic = nil;
    
    if (nil != name && 0 < name.length)
    {
        mutableDic = [NSMutableDictionary dictionary];
        NSDictionary* dic = [_userDefaults persistentDomainForName:name];
        if (nil != dic)
        {
            [mutableDic setDictionary:dic];
        }
    }
    
    return mutableDic;
}

-(void) setValueForKeyInPersistentDomain:(id) value forKey:(NSString*) key inPersistentDomain:(NSString*) domain
{
    NSMutableDictionary* dic = [self persistentDomainForName:domain];
    if (nil != dic && nil != value && nil != key)
    {
        [dic setObject:value forKey:key];
        [_userDefaults setPersistentDomain:dic forName:domain];
        [_userDefaults synchronize];
    }
}

-(id) getValueForKeyInPersistentDomain:(NSString*) key inPersistentDomain:(NSString*) domain
{
    id value = nil;
    
    if (nil != domain && 0 < domain.length && nil != key && 0 < key.length)
    {
        NSMutableDictionary* dic = [self persistentDomainForName:domain];
        value = [dic objectForKey:key];
    }
    
    return value;
}

#pragma mark - App

-(BOOL) isAppLaunchedBefore
{
    BOOL flag = NO;
    
    id value = [self getValueForKeyInPersistentDomain:APPDATA_KEY_APPLAUNCHEDBEFORE inPersistentDomain:PERSISTENTDOMAIN_APPDATA];
    if ([value isEqualToString:@"YES"])
    {
        flag = YES;
    }
    
    return flag;
}

-(void) recordAppLaunchedBefore
{
    NSString* sVal = @"YES";
    [self setValueForKeyInPersistentDomain:sVal forKey:APPDATA_KEY_APPLAUNCHEDBEFORE inPersistentDomain:PERSISTENTDOMAIN_APPDATA];
}

#pragma mark - Device
-(NSString*) deviceSn
{
    NSString* idfv = [SSKeychain passwordForService:KEYCHAIN_SERVICE_DEVICE account:KEYCHAIN_ACCOUNT_IDFV];
    if (nil == idfv)
    {
        idfv = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
        [SSKeychain setPassword:idfv forService:KEYCHAIN_SERVICE_DEVICE account:KEYCHAIN_ACCOUNT_IDFV];
    }
    return idfv;
}

-(NSString*) deviceModel
{
    return [UIDevice deviceSimpleModel];
}

-(NSString*) osVersion
{
    return [UIDevice osVersion];
}

-(NSString*) appVersion
{
    NSString* version = [[[NSBundle mainBundle] infoDictionary] objectForKey:BUNDLE_KEY_SHORTVERSION];
    return version;
}

-(NSUInteger) appBuild
{
    NSString* build = [[[NSBundle mainBundle] infoDictionary] objectForKey:BUNDLE_KEY_BUNDLEVERSION];
    return build.intValue;
}

-(NSString*) appFullVerion
{
    NSMutableString* fullVersion = [NSMutableString string];
    
    [fullVersion appendString:self.appVersion];
    [fullVersion appendString:@"-b"];
    [fullVersion appendString:[NSString stringWithFormat:@"%d", self.appBuild]];
    
    return fullVersion;
}

-(BOOL) isJailed
{
    BOOL isJailed = [UIDevice isJailed];
    return isJailed;
}

#pragma mark - UIApplicationDelegate

-(void)applicationWillResignActive:(UIApplication *)application
{
    
}

-(void)applicationDidEnterBackground:(UIApplication *)application
{
    
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    
}

-(void)applicationWillEnterForeground:(UIApplication *)application
{
    
}

@end
