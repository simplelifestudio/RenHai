//
//  ChatConfirmViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "ChatWizardController.h"

@interface ChatConfirmViewController_iPhone : UIViewController <UICollectionViewDelegate, UICollectionViewDataSource, ChatWizardPage>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UILabel *partnerStatusLabel;
@property (weak, nonatomic) IBOutlet UILabel *selfStatusLabel;

@property (weak, nonatomic) IBOutlet FUIButton *agreeChatButton;
@property (weak, nonatomic) IBOutlet FUIButton *rejectChatButton;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;

- (IBAction)didPressAgreeChatButton:(id)sender;
- (IBAction)didPressRejectChatButton:(id)sender;

@end
