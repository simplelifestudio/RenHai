//
//  UserDataModule.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserDataModule.h"

#import "CBFileUtils.h"

#import "RHDevice.h"
#import "RHDeviceCard.h"
#import "RHInterestCard.h"
#import "RHInterestLabel.h"
#import "RHImpressCard.h"
#import "RHImpressLabel.h"

#import "CommunicationModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#define ARCHIVE_DEVICE_NAME @"device.dat"
#define ARCHIVE_SERVER_NAME @"server.dat"

@interface UserDataModule()
{
    NSString* _dataDir;
    
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
}

@end

@implementation UserDataModule

SINGLETON(UserDataModule)

@synthesize device = _device;
@synthesize server = _server;
@synthesize businessSession = _businessSession;

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"UserData Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"UserData Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    
    [CBFileUtils createDirectory:self.dataDirectory];
    DDLogVerbose(@"App Sandbox Path: %@", NSHomeDirectory());    
}

-(void) releaseModule
{
    [self _unregisterNotifications];
    
    [super releaseModule];
}

-(void) startService
{
    DDLogVerbose(@"Module:%@ is started.", self.moduleIdentity);
    
    [super startService];
    
    BOOL loadFlag = [self loadUserData];
    if (!loadFlag)
    {
        [self initUserData];
    }
    
    [self _initBusinessSessionData];
    [self _initServerData];
    
    [self _registerNotifications];
}

-(void) processService
{
    MODULE_DELAY
}

#pragma mark - Public Methods

-(NSString*) dataDirectory
{
    if (nil == _dataDir)
    {
        NSString* rootDir = NSHomeDirectory();
        NSString* docDir = [rootDir stringByAppendingPathComponent:@"Documents"];
        _dataDir = [docDir stringByAppendingPathComponent:@"Data"];
    }
    
    return _dataDir;
}

-(BOOL) saveUserData
{
    BOOL flag = NO;
    
    NSString* file = [self.dataDirectory stringByAppendingPathComponent:ARCHIVE_DEVICE_NAME];
    
    flag = [NSKeyedArchiver archiveRootObject:_device toFile:file];
    
    return flag;
}

-(BOOL) loadUserData
{
    BOOL flag = NO;
    
    NSString* file = [self.dataDirectory stringByAppendingPathComponent:ARCHIVE_DEVICE_NAME];
    
    @try
    {
        _device = [NSKeyedUnarchiver unarchiveObjectWithFile:file];
        
        [self _initServerData];
    }
    @catch (NSException *exception)
    {
        DDLogWarn(@"Failed to load user data from: %@", file);
        DDLogWarn(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
    
    flag = (nil != _device) ? YES : NO;
    
    return flag;
}

-(void) initUserData
{
    [self _initDeviceData];
}

#pragma mark - Private Methods

-(void) _initDeviceData
{
    _device = [[RHDevice alloc] init];
}

-(void) _initBusinessSessionData
{
    _businessSession = [[RHBusinessSession alloc] init];
}

-(void) _initServerData
{
    _server = [[RHServer alloc] init];
}

-(void) _registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_RHSERVERNOTIFICATION object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void) _onNotifications:(NSNotification*) notification
{
    if (nil != notification)
    {
        if ([notification.name isEqualToString:NOTIFICATION_ID_RHSERVERNOTIFICATION])
        {
            RHMessage* message = (RHMessage*)notification.object;
            
            switch (message.messageId)
            {
                case MessageId_BusinessSessionNotification:
                {
                    [self _processBusinessSessionNotification:message];
                    
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }
}

-(void) _processBusinessSessionNotification:(RHMessage*) message
{
    if (nil != message)
    {
        NSDictionary* messageBody = message.body;
        
        NSString* businessSessionId = [messageBody objectForKey:MESSAGE_KEY_BUSINESSSESSIONID];
        NSNumber* oBusinessType = [messageBody objectForKey:MESSAGE_KEY_BUSINESSTYPE];
        NSNumber* oOperationType = [messageBody objectForKey:MESSAGE_KEY_OPERATIONTYPE];
        
        RHBusinessType businessType = oBusinessType.intValue;
        BusinessSessionNotificationType notificationType = oOperationType.intValue;

        switch (notificationType)
        {
            case BusinessSessionNotificationType_SessionBound:
            {
                NSDictionary* sessionDic = [messageBody objectForKey:MESSAGE_KEY_OPERATIONINFO];
                _businessSession.businessSessionId = businessSessionId;
                _businessSession.businessType = businessType;
                _businessSession.operationType = notificationType;
                
                [_businessSession fromJSONObject:sessionDic];
            
                [_statusModule recordServerNotification:ServerNotificationIdentifier_SessionBound];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_SESSIONBOUND object:self];
                
                break;
            }
            case BusinessSessionNotificationType_OthersideRejected:
            {
                [_statusModule recordServerNotification:ServerNotificationIdentifier_OthersideRejectChat];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_OTHERSIDEREJECTED object:self];
                
                break;
            }
            case BusinessSessionNotificationType_OthersideAgreed:
            {
                [_statusModule recordServerNotification:ServerNotificationIdentifier_OthersideAgreeChat];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_OTHERSIDEAGREED object:self];
                
                break;
            }
            case BusinessSessionNotificationType_OthersideLost:
            {
                [_statusModule recordServerNotification:ServerNotificationIdentifier_OthersideLost];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_OTHERSIDELOST object:self];
                
                break;
            }
            case BusinessSessionNotificationType_OthersideEndChat:
            {
                [_statusModule recordServerNotification:ServerNotificationIdentifier_OthersideEndChat];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_OTHERSIDEENDCHAT object:self];
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
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
