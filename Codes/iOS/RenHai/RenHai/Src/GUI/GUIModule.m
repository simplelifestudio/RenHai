//
//  GUIModule.m
//  Seeds
//
//  Created by DENG KE on 13-5-22.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "GUIModule.h"

#import "AFNetworkActivityIndicatorManager.h"

#import "CBHUDAgent.h"
#import "CBUIUtils.h"
#import "UIViewController+CBUIViewControlerExtends.h"
#import "UINavigationController+CBNavigationControllerExtends.h"
#import "UIFont+FlatUI.h"

#import "GUIStyle.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#import "RHTableViewLabelCell_iPhone.h"

#import "CommunicationModule.h"

@interface GUIModule()
{
    AFNetworkActivityIndicatorManager* _networkActivityIndicator;
    
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
}

@end

@implementation GUIModule

SINGLETON(GUIModule)

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"GUI Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"GUI Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _networkActivityIndicator = [AFNetworkActivityIndicatorManager sharedManager];
    
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];

    [self _setupUIAppearance];
    
    [self _setupViewControllersFromStoryboard];
}

-(void) releaseModule
{
    [_HUDAgent releaseResources];
    
    [self _unregisterNotifications];
    
    [super releaseModule];
}

-(void) startService
{
    DDLogInfo(@"Module:%@ is started.", self.moduleIdentity);
        
    [super startService];
    
    [self keepScreenAlwaysOn:SCREEN_ALWAYS_ON];
    
    [_networkActivityIndicator setEnabled:YES];

    [self _registerNotifications];
}

-(void) processService
{
    MODULE_DELAY    
}

-(CBHUDAgent*) HUDAgent
{
    if (nil == _HUDAgent && nil != _mainViewController.view)
    {
        UIView* view = _mainViewController.view;
        _HUDAgent = [[CBHUDAgent alloc] initWithUIView:view];
    }
    
    return _HUDAgent;
}

-(void) showHUD:(NSString *)status delay:(NSInteger)seconds
{
    [self showHUD:status minorStatus:nil delay:seconds];
}

-(void) showHUD:(NSString*) majorStauts minorStatus:(NSString*) minorStatus delay:(NSInteger)seconds
{
    [self.HUDAgent showHUD:majorStauts minorStatus:minorStatus delay:seconds];
}

- (BOOL) isNetworkActivityIndicatorVisible
{
    BOOL flag = [UIApplication sharedApplication].networkActivityIndicatorVisible;
    
    return flag;
}

- (void) setNetworkActivityIndicatorVisible:(BOOL) flag
{
    if ((flag != [self isNetworkActivityIndicatorVisible]) && (![_networkActivityIndicator isNetworkActivityIndicatorVisible]))
    {
        [UIApplication sharedApplication].networkActivityIndicatorVisible = flag;
    }
}

- (void) keepScreenAlwaysOn:(BOOL)on
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:on];
}

#pragma mark - UIApplicationDelegate

-(void)applicationWillResignActive:(UIApplication *)application
{
    
}

-(void)applicationDidEnterBackground:(UIApplication *)application
{

}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
}

-(void)applicationWillEnterForeground:(UIApplication *)application
{

}

#pragma mark - Private Methods

-(UIViewController*) _currentRootViewController
{
    return _mainViewController;
}

-(void) _setupViewControllersFromStoryboard
{
    if ([UIDevice isRunningOniOS7AndLater])
    {
        UIWindow* window = [CBUIUtils getKeyWindow];
        window.tintColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
    }
    
    UIStoryboard* storyboard = [UIStoryboard storyboardWithName:STORYBOARD_IPHONE bundle:nil];
    
    _connectViewController = [[ConnectViewController_iPhone alloc] initWithNibName:NIB_CONNECTVIEWCONTROLLER bundle:nil];
    
    _leftbarViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_LEFTBAR_IPHONE];
    _navigationController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_NAVIGATION];
    
    _homeViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_HOME_IPHONE];
    _deviceViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_DEVICE_IPHONE];
    _interestViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_INTEREST_IPHONE];
    _impressViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_IMPRESS_IPHONE];
    _helpViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_HELP_IPHONE];
    _configViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CONFIG_IPHONE];
    _warningViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_WARNING_IPHONE];
    
    _chatWizardController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATWIZARD];
    
    [self _assembleMainViewController];
}

-(void) _assembleMainViewController
{
    _mainViewController = [MainViewController_iPhone revealControllerWithFrontViewController:_navigationController leftViewController:_leftbarViewController];
    _mainViewController.allowsOverdraw = YES;
    _mainViewController.disablesFrontViewInteraction = YES;
}

-(void) _registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NOTIFICATION_ID_RHSERVERDISCONNECTED object:nil];
}

-(void) _onNotifications:(NSNotification*) notification
{
    if (nil != notification)
    {
        NSString* notificationName = notification.name;
        if ([notificationName isEqualToString:NOTIFICATION_ID_RHSERVERDISCONNECTED])
        {
            [_statusModule recordAppMessage:AppMessageIdentifier_Disconnect];
            
            UIViewController* rootVC = [CBUIUtils getRootController];
            if ([rootVC isVisible])
            {
                [_connectViewController popConnectView:rootVC animated:YES];                
            }
        }
    }
}

-(void) _setupUIAppearance
{
    [[UINavigationBar appearance] setTitleTextAttributes:
        [NSDictionary dictionaryWithObjectsAndKeys:
         FLATUI_COLOR_TEXT_INFO,
         UITextAttributeTextColor,
         FLATUI_COLOR_CLEAR,
         UITextAttributeTextShadowColor ,
         nil,
         UITextAttributeTextShadowOffset,
         nil,
         UITextAttributeFont,
         nil
        ]
    ];

    [[UINavigationBar appearance] setTintColor:FLATUI_COLOR_TINT_NAVIGATIONBAR];
    [[UIBarButtonItem appearance] setTintColor:FLATUI_COLOR_TINT_BARBUTTONITEM];
}

@end
