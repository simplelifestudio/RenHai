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
#import "CBAutoScrollLabel.h"

#define CURRENT_BUSINESSPOOL BusinessType_Interest

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

@property (weak, nonatomic) IBOutlet UILabel *chatDeviceCountUnit5;
@property (weak, nonatomic) IBOutlet UILabel *chatDeviceCountUnit4;
@property (weak, nonatomic) IBOutlet UILabel *chatDeviceCountUnit3;
@property (weak, nonatomic) IBOutlet UILabel *chatDeviceCountUnit2;
@property (weak, nonatomic) IBOutlet UILabel *chatDeviceCountUnit1;

@property (weak, nonatomic) IBOutlet UILabel *versionLabel;

@property (weak, nonatomic) IBOutlet UILabel *onlineDeviceCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *chatDeviceCountLabel;

@property (weak, nonatomic) IBOutlet CBAutoScrollLabel *bannerView;

- (IBAction)onPressEnterButton:(id)sender;
- (IBAction)onPressHelpButton:(id)sender;

@end
