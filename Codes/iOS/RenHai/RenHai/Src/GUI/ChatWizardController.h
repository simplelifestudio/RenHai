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
-(void) onOthersideChatMessage;

@end

typedef enum
{
    ChatWizardStatus_ChatWait = 0,
    ChatWizardStatus_ChatConfirm,
    ChatWizardStatus_ChatVideo,
    ChatWizardStatus_ChatAssess
}
ChatWizardStatus;

@interface ChatWizardController : UINavigationController <UINavigationControllerDelegate>

-(void) wizardProcess:(ChatWizardStatus) status;

@end
