//
//  GUIModule.m
//  Seeds
//
//  Created by Patrick Deng on 13-5-22.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "GUIModule.h"

#import "AFNetworkActivityIndicatorManager.h"

#import "CBHUDAgent.h"
#import "CBUIUtils.h"

#import "UIFont+FlatUI.h"


@interface GUIModule()
{
    AFNetworkActivityIndicatorManager* _networkActivityIndicator;
}

@end

@implementation GUIModule
{

}

@synthesize HUDAgent = _HUDAgent;

SINGLETON(GUIModule)

-(void) initModule
{
    [self setModuleIdentity:NSLocalizedString(@"GUI Module", nil)];
    [self.serviceThread setName:NSLocalizedString(@"GUI Module Thread", nil)];
    [self setKeepAlive:FALSE];
    
    _networkActivityIndicator = [AFNetworkActivityIndicatorManager sharedManager];    
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
    if ((nil == _HUDAgent))
    {
#warning HomeViewController need be here.
        UIView* view = nil;
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
#warning HomeViewController need be here.
//    UIViewController* rootVC = (nil != _homeViewController) ? _homeViewController : _helpViewController;
//    return rootVC;
    return nil;
}

@end
