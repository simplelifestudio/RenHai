//
//  BusinessStatusModule.m
//  RenHai
//
//  Created by DENG KE on 13-10-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "BusinessStatusModule.h"

@interface BusinessStatusModule()
{
    BusinessStatus* _currentBusinessStatus;
    
    NSMutableDictionary* _businessStatusMap;
}

@end

@implementation BusinessStatusModule

SINGLETON(BusinessStatusModule)

#pragma mark - Public Methods

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"BusinessStatus Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"BusinessStatus Module Thread", nil)];
    [self setKeepAlive:FALSE];

    [self _initBusinessStatusMap];
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

-(BusinessStatusIdentifier) currentBusinessStatusIdentifier
{
    return _currentBusinessStatus.identifier;
}

-(BusinessStatus*) currentBusinessStatus
{
    return _currentBusinessStatus;
}

-(void) recordAppMessage:(AppMessageIdentifier) appMessageId
{
    DDLogWarn(@"Recorded new app message: %d in BusinessStatus: %d", appMessageId, _currentBusinessStatus.identifier);
    
    [_currentBusinessStatus recordAppMessage:appMessageId];
}

-(void) recordServerNotification:(ServerNotificationIdentifier) serverNotificationId
{
    DDLogWarn(@"Recorded new server notification: %d in BusinessStatus: %d", serverNotificationId, _currentBusinessStatus.identifier);
    
    [_currentBusinessStatus recordServerNotification:serverNotificationId];
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

#pragma mark - Private Methods

-(void) _initBusinessStatusMap
{
    _businessStatusMap = [NSMutableDictionary dictionary];
    
    // S0: Disconnected
    BusinessStatus* status =
        [[BusinessStatus alloc]
            initWithIdentifier:BusinessStatusIdentifier_Disconnected
            appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                switch (appMessageId)
                {
                    // M0
                    case AppMessageIdentifier_Disconnect:
                    {
                        // IGNORE
                        break;
                    }
                    // M1
                    case AppMessageIdentifier_Connect:
                    {
                        [self _updateBusinessStatus:BusinessStatusIdentifier_Connected];
                        break;
                    }
                    // M2
                    case AppMessageIdentifier_AppDataSync:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M3
                    case AppMessageIdentifier_ServerDataSync:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M4
                    case AppMessageIdentifier_EnterPool:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M5
                    case AppMessageIdentifier_MatchStart:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M6
                    case AppMessageIdentifier_AgreeChat:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M7
                    case AppMessageIdentifier_RejectChat:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M8
                    case AppMessageIdentifier_EndChat:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M9
                    case AppMessageIdentifier_AssessAndContinue:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M10
                    case AppMessageIdentifier_AssessAndQuit:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M11
                    case AppMessageIdentifier_UnbindSession:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M12
                    case AppMessageIdentifier_Aloha:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    // M13
                    case AppMessageIdentifier_LeavePool:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                    default:
                    {
                        [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                        break;
                    }
                }
            }
            serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                switch (serverNotificationId)
                {
                    // E0
                    case ServerNotificationIdentifier_SessionBound:
                    {
                        [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                        break;
                    }
                    // E1
                    case ServerNotificationIdentifier_OthersideAgreeChat:
                    {
                        [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                        break;
                    }
                    // E2
                    case ServerNotificationIdentifier_OthersideRejectChat:
                    {
                        [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                        break;
                    }
                    // E3
                    case ServerNotificationIdentifier_OthersideEndChat:
                    {
                        [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                        break;
                    }
                    // E4
                    case ServerNotificationIdentifier_OthersideLost:
                    {
                        [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                        break;
                    }
                    // E5
                    default:
                    {
                        [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                        break;
                    }
                }                
            }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S1: Connected
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_Connected
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppSyncCompleted];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          // IGNORE
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S2: AppSyncCompleted
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_AppSyncCompleted
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          // IGNORE
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_EnterPoolCompleted];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S3: EnterPoolCompleted
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_EnterPoolCompleted
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_MatchStarted];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppSyncCompleted];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S4: MatchStarted
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_MatchStarted
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppSyncCompleted];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_SessionBoundAcknowledged];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S5: SessionBoundAcknowledged
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_SessionBoundAcknowledged
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          ServerNotificationIdentifier serverNotificationId = [_currentBusinessStatus latestServerNotificationRecord];
                          if (serverNotificationId == ServerNotificationIdentifier_OthersideAgreeChat)
                          {
                              [self _updateBusinessStatus:BusinessStatusIdentifier_ChatAllAgreed];
                          }
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_EnterPoolCompleted];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          ServerNotificationIdentifier serverNotificationId = [_currentBusinessStatus latestServerNotificationRecord];
                          if (serverNotificationId == ServerNotificationIdentifier_OthersideRejectChat || serverNotificationId == ServerNotificationIdentifier_OthersideLost)
                          {
                              [self _updateBusinessStatus:BusinessStatusIdentifier_EnterPoolCompleted];
                          }
                          else
                          {
                              [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          }
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          AppMessageIdentifier appMessageId = [_currentBusinessStatus latestAppMessageRecord];
                          if (appMessageId == AppMessageIdentifier_AgreeChat)
                          {
                              [self _updateBusinessStatus:BusinessStatusIdentifier_ChatAllAgreed];
                          }
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S6: ChatAllAgreed
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_ChatAllAgreed
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_ChatEnded];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          // IGNORE
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      default:
                      {
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // S7: ChatEnded
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_ChatEnded
              appMessageHandleBlock:^(AppMessageIdentifier appMessageId){
                  switch (appMessageId)
                  {
                      // M0
                      case AppMessageIdentifier_Disconnect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M1
                      case AppMessageIdentifier_Connect:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_EnterPool:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M6
                      case AppMessageIdentifier_AgreeChat:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M7
                      case AppMessageIdentifier_RejectChat:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M8
                      case AppMessageIdentifier_EndChat:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M9
                      case AppMessageIdentifier_AssessAndContinue:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_EnterPoolCompleted];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppSyncCompleted];
                          break;
                      }
                      // M11
                      case AppMessageIdentifier_UnbindSession:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M12
                      case AppMessageIdentifier_Aloha:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M13
                      case AppMessageIdentifier_LeavePool:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      default:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                  }
              }
              serverNotificationHandleBlock:^(ServerNotificationIdentifier serverNotificationId){
                  switch (serverNotificationId)
                  {
                      // E0
                      case ServerNotificationIdentifier_SessionBound:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E1
                      case ServerNotificationIdentifier_OthersideAgreeChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E2
                      case ServerNotificationIdentifier_OthersideRejectChat:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                      // E3
                      case ServerNotificationIdentifier_OthersideEndChat:
                      {
                          // IGNORE
                          break;
                      }
                      // E4
                      case ServerNotificationIdentifier_OthersideLost:
                      {
                          // IGNORE
                          break;
                      }
                      default:
                      {
                          [self _triggerBusinessStatusErrorByServerNotification:serverNotificationId];
                          break;
                      }
                  }
              }];
    [_businessStatusMap setObject:status forKey:status.identifierString];
    
    // Init Status: S1
    [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
}

-(void) _updateBusinessStatus:(BusinessStatusIdentifier) identifier
{
    DDLogWarn(@"BusinessStatus changed from: %d to :%d", _currentBusinessStatus.identifier, identifier);
    
    NSString* identifierString = [NSString stringWithFormat:@"%d", identifier];
    BusinessStatus* status = [_businessStatusMap objectForKey:identifierString];
    [status clearAllRecords];
    _currentBusinessStatus = status;
}

-(void) _triggerBusinessStatusErrorByAppMessage:(AppMessageIdentifier) appMessageId
{
    DDLogError(@"Error App Message:%d in BusinessStatus: %d", appMessageId, _currentBusinessStatus.identifier);
    
    NSAssert(NO, @"Error App Message:%d in BusinessStatus: %d", appMessageId, _currentBusinessStatus.identifier);
}

-(void) _triggerBusinessStatusErrorByServerNotification:(ServerNotificationIdentifier) serverNotificationId
{
    DDLogError(@"Error Server Notification:%d in BusinessStatus: %d", serverNotificationId, _currentBusinessStatus.identifier);
    
    NSAssert(NO, @"Error Server Notification:%d in BusinessStatus: %d", serverNotificationId, _currentBusinessStatus.identifier);
}

@end