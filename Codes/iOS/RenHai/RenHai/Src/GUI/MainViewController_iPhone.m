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
    navigationVC.navigationBarHidden = NO;
    navigationVC.navigationBar.translucent = NO;
    navigationVC.navigationBar.backgroundColor = [UIColor whiteColor];
    
    HomeViewController_iPhone* homeVC = _guiModule.homeViewController;

    [self setLeftViewController:_guiModule.leftbarViewController];
    [self setRightViewController:nil];
    [self setFrontViewController:navigationVC];

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
    
    [CBUIUtils setRootController:self];
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
    //每次当navigation中的界面切换，设为空。本次赋值只在程序初始化时执行一次
    static UIViewController *lastController = nil;
    
    //若上个view不为空
    if (lastController != nil)
    {
        //若该实例实现了viewWillDisappear方法，则调用
        if ([lastController respondsToSelector:@selector(viewDidDisappear:)])
        {
            [lastController viewDidDisappear:animated];
        }
    }
    
    //将当前要显示的view设置为lastController，在下次view切换调用本方法时，会执行viewWillDisappear
    lastController = viewController;
    
    [viewController viewDidAppear:animated];
}

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    //每次当navigation中的界面切换，设为空。本次赋值只在程序初始化时执行一次
    static UIViewController *lastController = nil;
    
    //若上个view不为空
    if (lastController != nil)
    {
        //若该实例实现了viewWillDisappear方法，则调用
        if ([lastController respondsToSelector:@selector(viewWillDisappear:)])
        {
            [lastController viewWillDisappear:animated];
        }
    }
    
    //将当前要显示的view设置为lastController，在下次view切换调用本方法时，会执行viewWillDisappear
    lastController = viewController;
    
    [viewController viewWillAppear:animated];
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
