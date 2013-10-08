//
//  HomeViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "CBRoundProgressView.h"
#import "OBShapedButton.h"

@interface HomeViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet CBRoundProgressView *enterButtonProgressView;
@property (weak, nonatomic) IBOutlet OBShapedButton *enterButton;
@property (weak, nonatomic) IBOutlet FUIButton *helpButton;
@property (weak, nonatomic) IBOutlet UILabel *enterLabel;

- (IBAction)onPressEnterButton:(id)sender;
- (IBAction)onPressHelpButton:(id)sender;

@end
