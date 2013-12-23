//
//  UserIntroductionViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-22.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserIntroductionViewController_iPhone.h"

@interface UserIntroductionViewController_iPhone ()
{
    EAIntroView* _introView;
}

@end

@implementation UserIntroductionViewController_iPhone

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {

    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    UIViewController* rootVC = [CBUIUtils getRootController];
    UIView* rootView = rootVC.view;
    [_introView showInView:rootView animateDuration:0.3];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - EAIntroDelegate

#pragma mark - Private Methods

-(void) _setupInstance
{
    UIViewController* rootVC = [CBUIUtils getRootController];
    UIView* rootView = rootVC.view;
    
    EAIntroPage* page01 = [EAIntroPage pageWithCustomViewFromNibNamed:@"UserIntroductionPage_iPhone"];
    EAIntroPage* page02 = [EAIntroPage pageWithCustomViewFromNibNamed:@"UserIntroductionPage_iPhone"];
    EAIntroPage* page03 = [EAIntroPage pageWithCustomViewFromNibNamed:@"UserIntroductionPage_iPhone"];
    
    _introView = [[EAIntroView alloc] initWithFrame:rootView.bounds andPages:@[page01, page02, page03]];
    [_introView setDelegate:self];
    [_introView.skipButton setTitle:NSLocalizedString(@"UserIntroduction_Action_Skip", nil) forState:UIControlStateNormal];
}

@end
