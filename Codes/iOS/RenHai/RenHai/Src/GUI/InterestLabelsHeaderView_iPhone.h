//
//  InterestLabelsHeaderView_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-12.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol InterestLabelsHeaderViewDelegate

@required
-(void) didCreateInterestLabel;
-(void) didDeleteInterestLabel;
-(void) didOrderInterestLabels;

@end

@interface InterestLabelsHeaderView_iPhone : UICollectionReusableView

@property (weak, nonatomic) IBOutlet UILabel *headerTitleLabel;

@property (weak, nonatomic) IBOutlet FUIButton *createButton;
@property (weak, nonatomic) IBOutlet FUIButton *delButton;
@property (weak, nonatomic) IBOutlet FUIButton *orderButton;

@property (strong, nonatomic) id<InterestLabelsHeaderViewDelegate> operationDelegate;

- (IBAction)didPressCreateButton:(id)sender;
- (IBAction)didPressDelButton:(id)sender;
- (IBAction)didPressOrderButton:(id)sender;

@end
