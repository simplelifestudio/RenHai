//
//  ChatWizardController.h
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "ChatWaitViewController_iPhone.h"
#import "ChatConfirmViewController_iPhone.h"
#import "ChatWebRTCViewController_iPhone.h"
#import "ChatImpressViewController_iPhone.h"

typedef enum
{
    ChatWizardStatus_ChatWait = 0,
    ChatWizardStatus_ChatConfirm,
    ChatWizardStatus_ChatWebRTC,
    ChatWizardStatus_ChatImpress
}
ChatWizardStatus;

@interface ChatWizardController : UINavigationController

@property (nonatomic, strong) ChatWaitViewController_iPhone* chatWaitViewController;
@property (nonatomic, strong) ChatConfirmViewController_iPhone* chatConfirmViewContorller;
@property (nonatomic, strong) ChatWebRTCViewController_iPhone* chatWebRTCViewController;
@property (nonatomic, strong) ChatImpressViewController_iPhone* chatImpressViewController;

-(void) wizardProcess:(ChatWizardStatus) status;

@end
