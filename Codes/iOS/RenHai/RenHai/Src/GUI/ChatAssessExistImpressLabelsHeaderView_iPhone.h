//
//  ChatAssessExistImpressLabelsHeaderView_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-11-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ChatAssessExistImpressLabelsHeaderView_iPhone : UICollectionReusableView

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@property (weak, nonatomic) IBOutlet FUIButton *cloneButton;

- (IBAction)didPressCloneButton:(id)sender;

@end
