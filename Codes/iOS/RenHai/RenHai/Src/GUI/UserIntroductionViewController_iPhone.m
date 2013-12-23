//
//  UserIntroductionViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-22.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserIntroductionViewController_iPhone.h"

#import "UserIntroductionPage_iPhone.h"

#import "AppDataModule.h"

@interface UserIntroductionViewController_iPhone ()
{
    AppDataModule* _appDataModule;
    
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

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
     
    [_introView showInView:self.view animateDuration:0];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - EAIntroDelegate

- (void)introDidFinish:(EAIntroView *)introView
{
    [self _dismiss];
}

- (void)intro:(EAIntroView *)introView pageAppeared:(EAIntroPage *)page withIndex:(NSInteger)pageIndex
{
    
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _appDataModule = [AppDataModule sharedInstance];
    
    EAIntroPage* page01 = [EAIntroPage pageWithCustomViewFromNibNamed:@"UserIntroductionPage_iPhone"];
    UserIntroductionPage_iPhone* customView01 = (UserIntroductionPage_iPhone*)page01.customView;
    [customView01 installTitle:@"XXXXX"];
    [customView01 installImage:@"help_01.png"];
    [customView01 installText:@"help_01.txt"];

    EAIntroPage* page02 = [EAIntroPage pageWithCustomViewFromNibNamed:@"UserIntroductionPage_iPhone"];
    UserIntroductionPage_iPhone* customView02 = (UserIntroductionPage_iPhone*)page02.customView;
    [customView02 installImage:@"help_02.png"];
    [customView02 installText:@"help_02"];
    
    EAIntroPage* page03 = [EAIntroPage pageWithCustomViewFromNibNamed:@"UserIntroductionPage_iPhone"];
    UserIntroductionPage_iPhone* customView03 = (UserIntroductionPage_iPhone*)page03.customView;
    [customView03 installImage:@"help_03.png"];
    [customView03 installText:@"help_03"];
    
    _introView = [[EAIntroView alloc] initWithFrame:self.view.bounds andPages:@[page01, page02, page03]];
    [_introView setDelegate:self];
    [_introView.skipButton setTitle:NSLocalizedString(@"UserIntroduction_Action_Skip", nil) forState:UIControlStateNormal];
    [_introView.skipButton setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
    [_introView.pageControl setBackgroundColor:[UIColor redColor]];
    _introView.pageControl.alpha = 0.5f;
    _introView.pageControlY = 20;
}

- (void)_dismiss
{
    BOOL isAppLaunchedBefore = [_appDataModule isAppLaunchedBefore];
    if (!isAppLaunchedBefore)
    {
        [_appDataModule recordAppLaunchedBefore];
    }
    else
    {
        
    }
    
    [self dismissViewControllerAnimated:NO completion:nil];
    [_introView setCurrentPageIndex:0];
}

@end
