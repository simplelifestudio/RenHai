//
//  ChatWizardController.h
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChatWizardPage <NSObject>

@required
-(void) resetPage;
-(void) pageWillLoad;
-(void) pageWillUnload;

@optional
-(void) onSessionBound;
-(void) onOthersideAgreed;
-(void) onOthersideRejected;
-(void) onOthersideLost;
-(void) onOthersideEndChat;

@end

typedef enum
{
    ChatWizardStatus_ChatWait = 0,
    ChatWizardStatus_ChatConfirm,
    ChatWizardStatus_ChatWebRTC,
    ChatWizardStatus_ChatImpress
}
ChatWizardStatus;

@interface ChatWizardController : UINavigationController

-(void) wizardProcess:(ChatWizardStatus) status;

@end
