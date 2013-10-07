//
//  HelpViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface HelpViewController_iPhone : UIViewController <UIScrollViewDelegate>

@property (strong, nonatomic) IBOutlet UIView *helpView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@property (weak, nonatomic) IBOutlet FUIButton *closeButton;
@property (weak, nonatomic) IBOutlet UIPageControl *pageControl;

- (IBAction)onClickCloseButton:(id)sender;
- (IBAction)onPageTurn:(id)sender;

- (void)resetDisplayStatus;

@end
