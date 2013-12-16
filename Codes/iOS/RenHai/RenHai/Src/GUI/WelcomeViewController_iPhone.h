//
//  WelcomeViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-12-16.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WelcomeViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet UITextView *textView;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusbarLabel;
@property (weak, nonatomic) IBOutlet FUIButton *actionButton;

- (IBAction)didPressActionButton:(id)sender;

- (void)dismissWelcome;

@end
