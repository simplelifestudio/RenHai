//
//  ChatWaitViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <FloatingCloudsView.h>

#import "ChatWaitViewController_iPhone.h"
#import "ChatWizardController.h"


@interface ChatWaitViewController_iPhone : UIViewController <ChatWizardPage>

@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UITextView *infoTextView;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (weak, nonatomic) IBOutlet FUIButton *actionButton;

@property (weak, nonatomic) IBOutlet UIView *labelCloudContainer;

- (IBAction)didPressActionButton:(id)sender;

@end
