//
//  GUIModule.m
//  Seeds
//
//  Created by DENG KE on 13-5-22.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "GUIModule.h"

#import "AFNetworkActivityIndicatorManager.h"

#import <JSQSystemSoundPlayer.h>

#import "CBUIUtils.h"
#import "UIViewController+CBUIViewControlerExtends.h"
#import "UINavigationController+CBNavigationControllerExtends.h"
#import "UIFont+FlatUI.h"

#import "GUIStyle.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#import "RHTableViewLabelCell_iPhone.h"

#import "CommunicationModule.h"

#define INTERVAL_ALERTMESSAGE_DISPLAY 1.5f

@interface GUIModule() <FUIAlertViewDelegate>
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

-(void) playSoundAndVibrate:(RHSoundId) soundId vibrate:(BOOL) vibrate
{
    [self playSound:soundId];
    
    if (vibrate)
    {
        [[JSQSystemSoundPlayer sharedPlayer] playVibrateSound];
    }
}

-(void) playSound:(RHSoundId) soundId
{
    NSString* soundFileName = nil;
    
    switch (soundId)
    {
        case SOUNDID_ERROR:
        {
            soundFileName = @"error";
            break;
        }
        case SOUNDID_CONNECT_RETRY:
        {
            soundFileName = @"connect_retry";
            break;
        }
        case SOUNDID_LABELMANAGED:
        {
            soundFileName = @"labelmanaged";
            break;
        }
        case SOUNDID_CHOOSEBUSINESS:
        {
            soundFileName = @"choosebusiness";
            break;
        }
        case SOUNDID_UNCHOOSEBUSINESS:
        {
            soundFileName = @"unchoosebusiness";
            break;
        }
        case SOUNDID_SESSIONBOUND:
        {
            soundFileName = @"sessionbound";
            break;
        }
        case SOUNDID_CHATCONFIRM_ACCEPTED:
        {
            soundFileName = @"chatconfirm_accepted";
            break;
        }
        case SOUNDID_CHATCONFIRM_REJECTED:
        {
            soundFileName = @"chatconfirm_rejected";
            break;
        }
        case SOUNDID_CHATMESSAGE_RECEIVED:
        {
            soundFileName = @"chatmessage_received";
            break;
        }
        case SOUNDID_CHATMESSSAGE_SENT:
        {
            soundFileName = @"chatmessage_sent";
            break;
        }
        case SOUNDID_CHATVIDEO_CHATMESSAGE:
        {
            soundFileName = @"chatvideo_chatmessage";
            break;
        }
        case SOUNDID_CHATVIDEO_ENDCHAT:
        {
            soundFileName = @"chatvideo_endchat";
            break;
        }
        case SOUNDID_CHATASSESS_CONTINUE:
        {
            soundFileName = @"chatassess_continue";
            break;
        }
        case SOUNDID_CHATASSESS_QUIT:
        {
            soundFileName = @"chatassess_quit";
            break;
        }
        default:
        {
            break;
        }
    }
    
    if (nil != soundFileName)
    {

        [[JSQSystemSoundPlayer sharedPlayer] playSoundWithName:soundFileName
                                                     extension:kJSQSystemSoundTypeCAF
                                                    completion:^{
                                                        
                                                    }];
        
//        [[JSQSystemSoundPlayer sharedPlayer] playAlertSoundWithName:soundFileName
//                                                          extension:kJSQSystemSoundTypeCAF];
    }
}

-(void) showAppMessage:(MessageBarMessageType) messageType messageTitle:(NSString*) messageTitle messageText:(NSString*) messageText visibleDuration:(CGFloat) visibleDuration callBackBlock:(void (^)())callbackBlock
{
    MessageBarManager* messageMgr = [MessageBarManager sharedInstance];
    [messageMgr showMessageWithTitle:messageTitle description:messageText type:messageType forDuration:visibleDuration callback:callbackBlock];
}

-(void) dismissAllAppMessages
{
    [[MessageBarManager sharedInstance] dismissAllMessages];
}

#pragma mark - UIApplicationDelegate

-(void)applicationWillResignActive:(UIApplication *)application
{

}

-(void)applicationDidEnterBackground:(UIApplication *)application
{
    [self dismissAllAppMessages];
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
    _welcomeViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_WELCOME_IPHONE];
    _userAgreementViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_USERAGREEMENT_IPHONE];
    _userIntroductionViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_USERINTRODUCTION_IPHONE];
    
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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_REMOTECOMMUNICATIONABNORMAL object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_RHNETWORKPOOR object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void) _onNotifications:(NSNotification*) notification
{
    if (nil != notification)
    {
        UIViewController* rootVC = [CBUIUtils getRootController];
        
        NSString* notificationName = notification.name;
        if ([notificationName isEqualToString:NOTIFICATION_ID_RHSERVERDISCONNECTED])
        {            
            [_connectViewController popConnectView:rootVC animated:YES];
        }
        else if ([notificationName isEqualToString:NOTIFICATION_ID_REMOTECOMMUNICATIONABNORMAL])
        {
            [_connectViewController popConnectView:rootVC animated:YES];
        }
        else if ([notificationName isEqualToString:NOTIFICATION_ID_RHNETWORKPOOR])
        {
            [self showAppMessage:MessageBarMessageTypeError messageTitle:NSLocalizedString(@"Common_Warning", nil) messageText:NSLocalizedString(@"Common_NetworkPoor", nil) visibleDuration:INTERVAL_ALERTMESSAGE_DISPLAY callBackBlock:nil];
        }
    }
}

- (void) _popupAlertWithBusinessStatus:(BusinessStatusIdentifier) businessStatusId andAppMessage:(AppMessageIdentifier) appMessageId
{
//    [self playSound:SOUNDID_ERROR];
//    
//    NSString* str = [NSString stringWithFormat:NSLocalizedString(@"Alert_AppMessage_In_BusinessStatus", nil), businessStatusId, appMessageId];
//    
//    [[MessageBarManager sharedInstance] showMessageWithTitle:NSLocalizedString(@"Alert_BusinessStatusAbnormal", nil)
//                                                 description:str
//                                                        type:MessageBarMessageTypeError
//                                                 forDuration:60];
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

#pragma mark - FUIAlertViewDelegate

- (void)alertView:(FUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    
}

@end
