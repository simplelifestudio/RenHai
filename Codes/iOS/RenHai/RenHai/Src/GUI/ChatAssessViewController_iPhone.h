//
//  ChatAssessViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "ChatWizardController.h"

#import "ImpressLabelsHeaderView_iPhone.h"

@interface ChatAssessViewController_iPhone : UIViewController <ChatWizardPage, UICollectionViewDataSource, UICollectionViewDelegate, UIScrollViewDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *assessLabelsView;
@property (weak, nonatomic) IBOutlet UICollectionView *addImpressLabelsView;
@property (weak, nonatomic) IBOutlet UICollectionView *existImpressLabelsView;

@property (weak, nonatomic) IBOutlet UILabel *countLabel;

@property (weak, nonatomic) IBOutlet FUIButton *continueButton;
@property (weak, nonatomic) IBOutlet FUIButton *finishButton;

- (IBAction)didPressContinueButton:(id)sender;
- (IBAction)didPressFinishButton:(id)sender;

@end
