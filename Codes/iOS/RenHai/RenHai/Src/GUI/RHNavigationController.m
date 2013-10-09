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
#import "CommunicationModule.h"
#import "GUIStyle.h"

@interface RHNavigationController ()
{
    GUIModule* _guiModule;
    AppDataModule* _appDataModule;
    CommunicationModule* _commModule;
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

-(void) viewDidAppear:(BOOL)animated
{
    [self _arrangeConnectViewController];
    
    [super viewDidAppear:animated];
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
    _commModule = [CommunicationModule sharedInstance];
}

-(void) _arrangeRootViewController
{
    [self setNavigationBarHidden:NO];
    [self popToRootViewControllerAnimated:NO];
    [self pushViewController:_guiModule.homeViewController animated:NO];
    [_guiModule.mainViewController showViewController:self animated:NO completion:nil];
}

-(void) _arrangeConnectViewController
{
    BOOL isAppLaunchedBefore = [_appDataModule isAppLaunchedBefore];
    if (!isAppLaunchedBefore)
    {
        [self presentViewController:_guiModule.helpViewController animated:YES completion:nil];
    }
    else
    {
        if (![_commModule isWebSocketConnected])
        {
            ConnectViewController_iPhone* _connectViewController = _guiModule.connectViewController;
            [_connectViewController popConnectView:_guiModule.mainViewController animated:YES];
        }        
    }
}

@end
