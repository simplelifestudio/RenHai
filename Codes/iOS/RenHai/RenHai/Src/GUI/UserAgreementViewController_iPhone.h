//
//  UserAgreementViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-12-22.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <FTCoreTextView.h>

@interface UserAgreementViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *statusbarLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet FUIButton *acceptButton;
@property (weak, nonatomic) IBOutlet FUIButton *declineButton;
@property (weak, nonatomic) IBOutlet FTCoreTextView *textView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

- (IBAction)didPressAcceptButton:(id)sender;
- (IBAction)didPressDeclineButton:(id)sender;

- (void)dismissUserAgreement;

@end
