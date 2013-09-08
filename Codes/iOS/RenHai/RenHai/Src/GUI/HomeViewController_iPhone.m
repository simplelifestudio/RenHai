//
//  HomeViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "HomeViewController_iPhone.h"

#import "GUIModule.h"
#import "GUIStyle.h"

@interface HomeViewController_iPhone () <CBRoundProgressViewDelegate>
{
    GUIModule* _guiModule;
    NSTimer* _enterButtonTimer;
}

@end

@implementation HomeViewController_iPhone

@synthesize enterButtonProgressView = _enterButtonProgressView;
@synthesize enterButton = _enterButton;

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    
    [self _setupView];
}

#pragma mark - Private Methods

-(void)_setupView
{
    _enterButtonProgressView.startAngle = - M_PI_2;
    _enterButtonProgressView.tintColor = FLATUI_COLOR_PROGRESS;
//    _enterButtonProgressView.trackColor = FLATUI_COLOR_PROGRESS_TRACK;
    _enterButtonProgressView.roundProgressViewDelegate = self;
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_HOME;
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if (self.navigationController.revealController.type & PKRevealControllerTypeLeft)
    {
        UIBarButtonItem* leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:sidebarMenuIcon_portrait landscapeImagePhone:sidebarMenuIcon_landscape style:UIBarButtonItemStylePlain target:self action:@selector(_leftBarButtonItemClicked:)];
        
        self.navigationItem.leftBarButtonItem = leftBarButtonItem;
    }
}

-(void)_leftBarButtonItemClicked:(id)sender
{
    if (self.navigationController.revealController.focusedController == self.navigationController.revealController.leftViewController)
    {
        [self.navigationController.revealController showViewController:self.navigationController.revealController.frontViewController];
    }
    else
    {
        [self.navigationController.revealController showViewController:self.navigationController.revealController.leftViewController];
    }
}

-(void)_lockViewController
{
    _enterButton.highlighted = YES;
    _enterButton.enabled = NO;
    
    self.navigationItem.leftBarButtonItem.enabled = NO;
    
    UIPanGestureRecognizer* gesturer = self.navigationController.revealController.revealPanGestureRecognizer;
    gesturer.enabled = NO;
}

-(void)_unlockViewController
{
    _enterButton.highlighted = NO;
    _enterButton.enabled = YES;
    
    self.navigationItem.leftBarButtonItem.enabled = YES;
    
    UIPanGestureRecognizer* gesturer = self.navigationController.revealController.revealPanGestureRecognizer;
    gesturer.enabled = YES;
}

static float progress = 0.1;
-(void)_timerUpdated
{
    [_enterButtonProgressView setProgress:progress animated:YES];
    progress+=0.1;
}

-(void)_timerFinished
{
    [_enterButtonTimer invalidate];
    
    progress = 0.0;
    [_enterButtonProgressView setProgress:progress animated:NO];
}

#pragma mark - IBActions

- (IBAction)onPressEnterButton:(id)sender
{
    [self performSelector:@selector(_lockViewController) withObject:self afterDelay:0.0];
    
    [self _timerFinished];
    
    _enterButtonTimer = [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(_timerUpdated) userInfo:nil repeats:YES];
    [_enterButtonTimer fire];
}

#pragma mark - CBRoundProgressViewDelegate

- (void) progressStarted
{
    
}

- (void) progressFinished
{
    [self _timerFinished];      
    
    [self performSelector:@selector(_unlockViewController) withObject:self afterDelay:0.0];
}

@end
