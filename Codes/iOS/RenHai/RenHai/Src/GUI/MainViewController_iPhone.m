//
//  MainViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "MainViewController_iPhone.h"

#import "CBUIUtils.h"
#import "GUIModule.h"
#import "CommunicationModule.h"
#import "AppDataModule.h"
#import "UINavigationController+CBNavigationControllerExtends.h"
#import "UIViewController+CBUIViewControlerExtends.h"

@interface MainViewController_iPhone ()
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
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
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void) switchToMainScene:(MainSceneStatus) mainSceneStatus
{
    BOOL isAppLaunchedBefore = [_appDataModule isAppLaunchedBefore];
    if (!isAppLaunchedBefore)
    {
        [_appDataModule recordAppLaunchedBefore];
    }
    else
    {
        
    }
    
    RHNavigationController* navigationVC = _guiModule.navigationController;
    navigationVC.navigationBarHidden = NO;
    navigationVC.navigationBar.translucent = NO;
    
    UIViewController* vc = nil;
    
    switch (mainSceneStatus)
    {
        case MAINSCENESTATUS_HOME:
        {
            vc = _guiModule.homeViewController;
            break;
        }
        case MAINSCENESTATUS_IMPRESS:
        {
            vc = _guiModule.impressViewController;
            break;
        }
        case MAINSCENESTATUS_INTEREST:
        {
            vc = _guiModule.interestViewController;
            break;
        }
        default:
        {
            vc = _guiModule.homeViewController;
            break;
        }
    }
    
    [self setLeftViewController:_guiModule.leftbarViewController];
    [self setRightViewController:nil];
    [self setFrontViewController:navigationVC];
    
    UIViewController* topVC = navigationVC.topViewController;
    if (topVC != vc)
    {
        if ([navigationVC containsViewController:vc])
        {
            [navigationVC popToViewController:vc animated:NO];
        }
        else
        {
            [navigationVC pushViewController:vc animated:NO];
        }
    }
    
    [CBUIUtils setRootController:self];
}

-(void) switchToMainScene
{
    [self switchToMainScene:MAINSCENESTATUS_HOME];
}

-(void) switchToChatScene
{
    ChatWizardController* chatWizard = _guiModule.chatWizardController;
    
//    [self setLeftViewController:nil];
//    [self setRightViewController:nil];
//    [self setFrontViewController:chatWizard];
    
    [CBUIUtils setRootController:chatWizard];
    
    [chatWizard wizardProcess:ChatWizardStatus_ChatWait];
}

-(void) enableGesturers
{
    self.revealPanGestureRecognizer.enabled = YES;
    self.revealResetTapGestureRecognizer.enabled = YES;
}

-(void) disableGesturers
{
    self.revealPanGestureRecognizer.enabled = NO;
    self.revealResetTapGestureRecognizer.enabled = NO;
}

#pragma mark - UINavigationControllerDelegate

-(void)navigationController:(UINavigationController *)navigationController didShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
//    //每次当navigation中的界面切换，设为空。本次赋值只在程序初始化时执行一次
//    static UIViewController *lastController = nil;
//    
//    //若上个view不为空
//    if (lastController != nil)
//    {
//        //若该实例实现了viewWillDisappear方法，则调用
//        if ([lastController respondsToSelector:@selector(viewDidDisappear:)])
//        {
//            [lastController viewDidDisappear:animated];
//        }
//    }
//    
//    //将当前要显示的view设置为lastController，在下次view切换调用本方法时，会执行viewWillDisappear
//    lastController = viewController;
//    
//    [viewController viewDidAppear:animated];
}

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
//    //每次当navigation中的界面切换，设为空。本次赋值只在程序初始化时执行一次
//    static UIViewController *lastController = nil;
//    
//    //若上个view不为空
//    if (lastController != nil)
//    {
//        //若该实例实现了viewWillDisappear方法，则调用
//        if ([lastController respondsToSelector:@selector(viewWillDisappear:)])
//        {
//            [lastController viewWillDisappear:animated];
//        }
//    }
//    
//    //将当前要显示的view设置为lastController，在下次view切换调用本方法时，会执行viewWillDisappear
//    lastController = viewController;
//    
//    [viewController viewWillAppear:animated];
}

#pragma mark - ScreenOrientation Methods

- (BOOL) shouldAutorotate
{
	return NO;
}

- (NSUInteger)supportedInterfaceOrientations
{
	return UIInterfaceOrientationMaskPortrait;
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation
{
	return UIInterfaceOrientationPortrait;
}

#pragma mark - Private Methods

@end
