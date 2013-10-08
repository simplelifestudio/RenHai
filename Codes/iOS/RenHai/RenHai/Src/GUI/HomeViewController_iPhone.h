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

@property (weak, nonatomic) IBOutlet UILabel *onlineDeviceCountUnit5;
@property (weak, nonatomic) IBOutlet UILabel *onlineDeviceCountUnit4;
@property (weak, nonatomic) IBOutlet UILabel *onlineDeviceCountUnit3;
@property (weak, nonatomic) IBOutlet UILabel *onlineDeviceCountUnit2;
@property (weak, nonatomic) IBOutlet UILabel *onlineDeviceCountUnit1;


- (IBAction)onPressEnterButton:(id)sender;
- (IBAction)onPressHelpButton:(id)sender;

@end
