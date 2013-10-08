//
//  WarningViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WarningViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UITextView *infoTextView;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (weak, nonatomic) IBOutlet FUIButton *actionButton;

@property (nonatomic) BOOL hideActionButton;
@property (nonatomic) BOOL hideCountLabel;
@property (nonatomic) BOOL hideInfoTextView;
@property (nonatomic) BOOL hideInfoLabel;

@property (nonatomic, strong) NSString* infoText;
@property (nonatomic, strong) NSString* infoDetailText;

@property (nonatomic, strong) void(^completeBlock)();
@property (nonatomic, strong) BOOL(^processBlock)();

- (IBAction)didPressActionButton:(id)sender;

@end
