//
//  ConnectViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-10-5.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ConnectViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *statusbarLabel;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UITextView *infoTextView;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (weak, nonatomic) IBOutlet FUIButton *actionButton;

- (IBAction)didPressActionButton:(id)sender;

- (void) popConnectView:(UIViewController*) presentingViewController animated:(BOOL) animated;
- (void) dismissConnectView:(BOOL) animated;

@end
