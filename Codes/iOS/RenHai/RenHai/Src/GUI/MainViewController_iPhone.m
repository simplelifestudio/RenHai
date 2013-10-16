//
//  MainViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "MainViewController_iPhone.h"

#import "GUIModule.h"
#import "UINavigationController+CBNavigationControllerExtends.h"

@interface MainViewController_iPhone ()
{
    GUIModule* _guiModule;
}

@end

@implementation MainViewController_iPhone

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
    
    [self setMinimumWidth:LEFTBAR_WIDTH_IPHONE maximumWidth:LEFTBAR_WIDTH_IPHONE forViewController:self.leftViewController];
    
    _guiModule = [GUIModule sharedInstance];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void) switchToMainScene
{
    RHNavigationController* navigationVC = _guiModule.navigationController;
    HomeViewController_iPhone* homeVC = _guiModule.homeViewController;

    [self setLeftViewController:_guiModule.leftbarViewController];
    [self setRightViewController:nil];
    [self setFrontViewController:navigationVC focusAfterChange:YES completion:nil];

    UIViewController* topVC = navigationVC.topViewController;
    if (topVC != homeVC)
    {
        if ([navigationVC containsViewController:homeVC])
        {
            [navigationVC popToViewController:homeVC animated:NO];
        }
        else
        {
            [navigationVC pushViewController:homeVC animated:NO];
        }
    }
}

-(void) switchToChatScene
{
    [self setLeftViewController:nil];
    [self setRightViewController:nil];
    [self setFrontViewController:_guiModule.chatWizardController focusAfterChange:YES completion:nil];
}

#pragma mark - Private Methods

@end
