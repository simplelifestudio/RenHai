//
//  ChatAssessExistImpressLabelsHeaderView_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-11-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChatAssessExistImpressLabelsHeaderViewDelegate

@required
-(void) didCloneImpressLabel;

@end

@interface ChatAssessExistImpressLabelsHeaderView_iPhone : UICollectionReusableView

@property (strong, nonatomic) id<ChatAssessExistImpressLabelsHeaderViewDelegate> operationDelegate;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet FUIButton *cloneButton;

- (IBAction)didPressCloneButton:(id)sender;

@end
