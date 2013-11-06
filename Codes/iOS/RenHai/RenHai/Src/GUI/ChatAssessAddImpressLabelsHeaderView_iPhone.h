//
//  ChatAssessAddImpressLabelsHeaderView_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-11-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ChatAssessAddImpressLabelsHeaderView_iPhone : UICollectionReusableView

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet FUIButton *addButton;
@property (weak, nonatomic) IBOutlet FUIButton *delButton;

- (IBAction)didPressAddButton:(id)sender;
- (IBAction)didPressDelButton:(id)sender;

@end
