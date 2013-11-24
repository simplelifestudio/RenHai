//
//  BusinessStatus.h
//  RenHai
//
//  Created by DENG KE on 13-10-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum
{
    BusinessStatusIdentifier_Disconnected = 0,
    BusinessStatusIdentifier_Connected,
    BusinessStatusIdentifier_AppDataSynced,
    BusinessStatusIdentifier_BusinessChoosed,
    BusinessStatusIdentifier_MatchStarted,
    BusinessStatusIdentifier_SessionBoundAcked,
    BusinessStatusIdentifier_ChatAllAgreed,
    BusinessStatusIdentifier_ChatEnded
}
BusinessStatusIdentifier;

typedef enum
{
    UIIdentifier_ConnectView = 0,
    UIIdentifier_HomeView,
    UIIdentifier_ChatWaitView,
    UIIdentifier_ChatConfirmView,
    UIIdentifier_ChatVideoView,
    UIIdentifier_ChatAssessView
}
UIIdentifier;

typedef enum
{
    AppMessageIdentifier_Disconnect = 0,
    AppMessageIdentifier_Connect,
    AppMessageIdentifier_AppDataSync,
    AppMessageIdentifier_ServerDataSync,
    AppMessageIdentifier_ChooseBusiness,
    AppMessageIdentifier_MatchStart,
    AppMessageIdentifier_AgreeChat,
    AppMessageIdentifier_RejectChat,
    AppMessageIdentifier_EndChat,
    AppMessageIdentifier_AssessAndContinue,
    AppMessageIdentifier_AssessAndQuit,
    AppMessageIdentifier_UnbindSession,
    AppMessageIdentifier_Aloha,
    AppMessageIdentifier_UnchooseBusiness
}
AppMessageIdentifier;

typedef enum
{
    ServerNotificationIdentifier_SessionBound = 0,
    ServerNotificationIdentifier_OthersideAgreeChat,
    ServerNotificationIdentifier_OthersideRejectChat,
    ServerNotificationIdentifier_OthersideEndChat,
    ServerNotificationIdentifier_OthersideLost
}
ServerNotificationIdentifier;

@interface BusinessStatus : NSObject

-(id) initWithIdentifier:(BusinessStatusIdentifier) identifier appMessageHandleBlock:(void(^)(AppMessageIdentifier appMessageId)) appMessageHandleBlock serverNotificationHandleBlock:(void(^)(ServerNotificationIdentifier serverNotificationId)) serverNotificationHandleBlock;

-(void) recordAppMessage:(AppMessageIdentifier) appMessageId;
-(void) recordServerNotification:(ServerNotificationIdentifier) serverNotificationId;

-(BusinessStatusIdentifier) identifier;
-(NSString*) identifierString;
-(NSNumber*) identifierObject;

-(NSArray*) appMessageRecords;
-(NSArray*) serverNotificationRecords;

-(AppMessageIdentifier) latestAppMessageRecord;
-(ServerNotificationIdentifier) latestServerNotificationRecord;

-(void) clearAllRecords;

@end
