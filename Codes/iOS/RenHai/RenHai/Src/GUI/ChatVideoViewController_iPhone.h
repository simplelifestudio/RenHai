//
//  ChatVideoViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "ChatWizardController.h"

#import "RHChatActionBar.h"

@interface ChatVideoViewController_iPhone : UIViewController <ChatWizardPage>

@property (weak, nonatomic) IBOutlet UIView *maskView;

@property (weak, nonatomic) IBOutlet FUIButton *endChatButton;
@property (weak, nonatomic) IBOutlet FUIButton *selfVideoButton;

- (IBAction)didPressEndChatButton:(id)sender;
- (IBAction)didPressSelfVideoButton:(id)sender;

@property (weak, nonatomic) IBOutlet UILabel *selfStatusLabel;
@property (weak, nonatomic) IBOutlet UILabel *partnerStatusLabel;

@property (weak, nonatomic) IBOutlet UIView *selfVideoView;
@property (weak, nonatomic) IBOutlet UIView *parterVideoView;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;

@end
