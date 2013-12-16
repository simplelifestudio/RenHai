//
//  BusinessStatusModule.m
//  RenHai
//
//  Created by DENG KE on 13-10-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "BusinessStatusModule.h"

#import "CommunicationModule.h"

#define LIMIT_REMOTESTATUSABNORMAL 3

@interface BusinessStatusModule()
{
    BusinessStatus* _currentBusinessStatus;
    
    NSMutableDictionary* _businessStatusMap;
    
    NSMutableArray* _remoteCommunicationAbnormalRecords;
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
    
    _remoteCommunicationAbnormalRecords = [NSMutableArray array];
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

-(void) recordCommunicateAbnormal:(AppMessageIdentifier) appMessageId
{
    DDLogWarn(@"Recorded remote status abnormal caused by app message: %d", appMessageId);

    BOOL flag = NO;
    
    NSNumber* oAppMessageId = [NSNumber numberWithInt:appMessageId];
    
    @synchronized(_remoteCommunicationAbnormalRecords)
    {
        NSNumber* lastAppMessageId = [_remoteCommunicationAbnormalRecords lastObject];
        if (nil != lastAppMessageId && lastAppMessageId.intValue == oAppMessageId.intValue)
        {
            flag = YES;
        }
        
        [_remoteCommunicationAbnormalRecords addObject:oAppMessageId];
        [_remoteCommunicationAbnormalRecords removeAllObjects];
    }
    
    if (flag)
    {
        [self _triggerBusinessStatusErrorByRemoteStatusAbnormal:appMessageId];
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
                        // IGNORE
                        break;
                    }
                    // M4
                    case AppMessageIdentifier_ChooseBusiness:
                    {
                        // IGNORE
                        break;
                    }
                    // M5
                    case AppMessageIdentifier_MatchStart:
                    {
                        // IGNORE
                        break;
                    }
                    // M6
                    case AppMessageIdentifier_AgreeChat:
                    {
                        // IGNORE
                        break;
                    }
                    // M7
                    case AppMessageIdentifier_RejectChat:
                    {
                        // IGNORE
                        break;
                    }
                    // M8
                    case AppMessageIdentifier_EndChat:
                    {
                        // IGNORE
                        break;
                    }
                    // M9
                    case AppMessageIdentifier_AssessAndContinue:
                    {
                        // IGNORE
                        break;
                    }
                    // M10
                    case AppMessageIdentifier_AssessAndQuit:
                    {
                        // IGNORE
                        break;
                    }
                    // M11
                    case AppMessageIdentifier_UnbindSession:
                    {
                        // IGNORE
                        break;
                    }
                    // M12
                    case AppMessageIdentifier_Aloha:
                    {
                        // IGNORE
                        break;
                    }
                    // M13
                    case AppMessageIdentifier_UnchooseBusiness:
                    {
                        // IGNORE
                        break;
                    }
                    // M14
                    case AppMessageIdentifier_ChatMessage:
                    {
                        // IGNORE
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
                        // IGNORE
                        break;
                    }
                    // E1
                    case ServerNotificationIdentifier_OthersideAgreeChat:
                    {
                        // IGNORE
                        break;
                    }
                    // E2
                    case ServerNotificationIdentifier_OthersideRejectChat:
                    {
                        // IGNORE
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
                    // E5
                    case ServerNotificationIdentifier_OthersideChatMessage:
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
                          // IGNORE
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppDataSynced];
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          // IGNORE
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_ChooseBusiness:
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
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
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
    
    // S2: AppDataSynced
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_AppDataSynced
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
                          // IGNORE
                          break;
                      }
                      // M2
                      case AppMessageIdentifier_AppDataSync:
                      {
                          // IGNORE
                          break;
                      }
                      // M3
                      case AppMessageIdentifier_ServerDataSync:
                      {
                          // IGNORE
                          break;
                      }
                      // M4
                      case AppMessageIdentifier_ChooseBusiness:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_BusinessChoosed];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          // IGNORE
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
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
                          // IGNORE
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
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
    
    // S3: BusinessChoosed
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_BusinessChoosed
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
                      case AppMessageIdentifier_ChooseBusiness:
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppDataSynced];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
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
                          // IGNORE
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
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
                      case AppMessageIdentifier_ChooseBusiness:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          // IGNORE
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppDataSynced];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
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
                          [self _updateBusinessStatus:BusinessStatusIdentifier_SessionBoundAcked];
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
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
    
    // S5: SessionBoundAcked
    status = [[BusinessStatus alloc]
              initWithIdentifier:BusinessStatusIdentifier_SessionBoundAcked
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
                      case AppMessageIdentifier_ChooseBusiness:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M5
                      case AppMessageIdentifier_MatchStart:
                      {
                          // IGNORE
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
                          [self _updateBusinessStatus:BusinessStatusIdentifier_BusinessChoosed];
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
                              [self _updateBusinessStatus:BusinessStatusIdentifier_BusinessChoosed];
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppDataSynced];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
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
                      // E5
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
                      case AppMessageIdentifier_ChooseBusiness:
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
                      {
                          // IGNORE
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
                      // E5
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
                      case AppMessageIdentifier_ChooseBusiness:
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
                          [self _updateBusinessStatus:BusinessStatusIdentifier_BusinessChoosed];
                          break;
                      }
                      // M10
                      case AppMessageIdentifier_AssessAndQuit:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_AppDataSynced];
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
                      case AppMessageIdentifier_UnchooseBusiness:
                      {
                          [self _updateBusinessStatus:BusinessStatusIdentifier_Disconnected];
                          break;
                      }
                      // M14
                      case AppMessageIdentifier_ChatMessage:
                      {
                          [self _triggerBusinessStatusErrorByAppMessage:appMessageId];
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
                      // E5
                      case ServerNotificationIdentifier_OthersideChatMessage:
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
    [CBAppUtils assert:NO logFormatString:@"Error App Message:%d in BusinessStatus: %d", appMessageId, _currentBusinessStatus.identifier];
}

-(void) _triggerBusinessStatusErrorByServerNotification:(ServerNotificationIdentifier) serverNotificationId
{
    [CBAppUtils assert:NO logFormatString:@"Error Server Notification:%d in BusinessStatus: %d", serverNotificationId, _currentBusinessStatus.identifier];
}

-(void) _triggerBusinessStatusErrorByRemoteStatusAbnormal:(AppMessageIdentifier) appMessageId
{
    DDLogError(@"Reached limit of remote communication abnormal with App Message: %d and Business Status:%d", appMessageId, _currentBusinessStatus.identifier);
    
//    [CBAppUtils assert:NO logFormatString:@"Reached limit of remote business status abnormal with App Message: %d", appMessageId];
    
    NSNumber* oBusinessStatusId = [NSNumber numberWithInt:_currentBusinessStatus.identifier];
    NSNumber* oAppMessageId = [NSNumber numberWithInt:appMessageId];
    
    NSDictionary* info = [NSDictionary dictionaryWithObjects:@[oAppMessageId, oBusinessStatusId] forKeys:@[NOTIFICATION_INFOID_APPMESSAGEID, NOTIFICATION_INFOID_BUSINESSSTATUSID]];
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ID_REMOTECOMMUNICATIONABNORMAL object:self userInfo:info];
}

@end
