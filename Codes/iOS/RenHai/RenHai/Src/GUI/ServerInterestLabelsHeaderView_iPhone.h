//
//  ServerInterestLabelsHeaderView_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-12.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ServerInterestLabelsHeaderView_iPhone : UICollectionReusableView

@property (weak, nonatomic) IBOutlet UILabel *headerTitleLabel;

@property (weak, nonatomic) IBOutlet FUIButton *refreshButton;
@property (weak, nonatomic) IBOutlet FUIButton *cloneButton;

- (IBAction)didPressRefreshButton:(id)sender;
- (IBAction)didPressCloneButton:(id)sender;

@end
