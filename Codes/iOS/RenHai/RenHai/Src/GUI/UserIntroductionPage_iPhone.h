//
//  UserIntroductionPage_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-12-22.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <FTCoreTextView.h>

@interface UserIntroductionPage_iPhone : UIView

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet FTCoreTextView *textView;

@end
