//
//  ChatWebRTCViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "ChatWizardController.h"

@interface ChatWebRTCViewController_iPhone : UIViewController <ChatWizardPage>

@property (weak, nonatomic) IBOutlet FUIButton *endChatButton;

- (IBAction)didPressEndChatButton:(id)sender;

@property (weak, nonatomic) IBOutlet UILabel *selfStatusLabel;
@property (weak, nonatomic) IBOutlet UILabel *partnerStatusLabel;


@end
