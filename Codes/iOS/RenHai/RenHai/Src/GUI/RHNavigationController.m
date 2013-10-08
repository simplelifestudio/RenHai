//
//  RHNavigationController.m
//  RenHai
//
//  Created by DENG KE on 13-9-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHNavigationController.h"

#import "GUIModule.h"
#import "AppDataModule.h"

#import "GUIStyle.h"

@interface RHNavigationController ()
{
    GUIModule* _guiModule;
    AppDataModule* _appDataModule;
}

@end

@implementation RHNavigationController

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
    
    [self _setupViewController];
    
    [self _arrangeRootViewController];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Private Methods

-(void) _setupViewController
{
    _guiModule = [GUIModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
}

-(void) _arrangeRootViewController
{
    BOOL isAppLaunchedBefore = [_appDataModule isAppLaunchedBefore];
    if (!isAppLaunchedBefore)
    {
        [self setNavigationBarHidden:YES];
        [self pushViewController:_guiModule.helpViewController animated:YES];
    }
    else
    {
        [self setNavigationBarHidden:NO];
        [self popToRootViewControllerAnimated:NO];
        [self pushViewController:_guiModule.homeViewController animated:NO];
        [_guiModule.mainViewController showViewController:self animated:YES completion:nil];
    }
}

@end
