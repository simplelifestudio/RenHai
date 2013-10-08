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

#import "UIFont+FlatUI.h"

#import "GUIStyle.h"

#import "RHTableViewLabelCell_iPhone.h"

#import "CommunicationModule.h"


@interface GUIModule()
{
    AFNetworkActivityIndicatorManager* _networkActivityIndicator;
}

@end

@implementation GUIModule

@synthesize connectViewController = _connectViewController;
@synthesize mainViewController = _mainViewController;
@synthesize leftbarViewController = _leftbarViewController;
@synthesize homeViewController = _homeViewController;
@synthesize deviceViewController = _deviceViewController;
@synthesize interestViewController = _interestViewController;
@synthesize impressViewController = _impressViewController;
@synthesize configViewController = _configViewController;
@synthesize helpViewController = _helpViewController;
@synthesize warningViewController = _warningViewController;

@synthesize chatWaitViewController = _chatWaitViewController;
@synthesize chatConfirmViewContorller = _chatConfirmViewContorller;
@synthesize chatWebRTCViewController = _chatWebRTCViewController;
@synthesize chatImpressViewController = _chatImpressViewController;

@synthesize HUDAgent = _HUDAgent;

SINGLETON(GUIModule)

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"GUI Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"GUI Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _networkActivityIndicator = [AFNetworkActivityIndicatorManager sharedManager];
    
    [self _setupViewControllersFromStoryboard];
}

-(void) releaseModule
{
    [_HUDAgent releaseResources];
    
    [super releaseModule];
}

-(void) startService
{
    DDLogVerbose(@"Module:%@ is started.", self.moduleIdentity);
        
    [super startService];
    
    [_networkActivityIndicator setEnabled:YES];
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
    
    _chatWaitViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATWAIT_IPHONE];
    _chatConfirmViewContorller = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATCONFIRM_IPHONE];
    _chatWebRTCViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATWEBRTC_IPHONE];
    _chatImpressViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATIMPRESS_IPHONE];
    
    [self _assembleMainViewController];
}

-(void) _assembleMainViewController
{
    NSDictionary *options = @{
                              PKRevealControllerAllowsOverdrawKey : [NSNumber numberWithBool:YES],
                              PKRevealControllerDisablesFrontViewInteractionKey : [NSNumber numberWithBool:YES]
                              };
    _mainViewController = [MainViewController_iPhone revealControllerWithFrontViewController:_navigationController leftViewController:_leftbarViewController options:options];
}

@end
