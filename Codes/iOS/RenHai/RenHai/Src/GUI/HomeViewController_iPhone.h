//
//  HomeViewController_iPhone.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "CBRoundProgressView.h"
#import "OBShapedButton.h"

@interface HomeViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet CBRoundProgressView *enterButtonProgressView;
@property (weak, nonatomic) IBOutlet OBShapedButton *enterButton;

- (IBAction)onPressEnterButton:(id)sender;

@end
