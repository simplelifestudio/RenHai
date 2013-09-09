//
//  GUIModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"
#import "CBHUDAgent.h"

#import "MainViewController_iPhone.h"
#import "LeftBarViewController_iPhone.h"
#import "RHNavigationController.h"
#import "HomeViewController_iPhone.h"
#import "DeviceViewController_iPhone.h"
#import "InterestViewController_iPhone.h"
#import "ImpressViewController_iPhone.h"
#import "HelpViewController_iPhone.h"
#import "WarningViewController_iPhone.h"
#import "ConfigViewController_iPhone.h"

#import "ChatWaitViewController_iPhone.h"
#import "ChatConfirmViewController_iPhone.h"
#import "ChatWebRTCViewController_iPhone.h"
#import "ChatImpressViewController_iPhone.h"

#define STORYBOARD_IPHONE @"MainStoryboard_iPhone"

#define SEGUE_ID_SPLASH_IPHONE2MAIN_IPHONE @"splash_iphone2main_iphone"

#define STORYBOARD_ID_SPLASH_IPHONE @"splash_iphone"
#define STORYBOARD_ID_MAIN_IPHONE @"main_iphone"
#define STORYBOARD_ID_LEFTBAR_IPHONE @"leftbar_iphone"
#define STORYBOARD_ID_NAVIGATION @"navigation"
#define STORYBOARD_ID_HOME_IPHONE @"home_iphone"
#define STORYBOARD_ID_DEVICE_IPHONE @"device_iphone"
#define STORYBOARD_ID_IMPRESS_IPHONE @"impress_iphone"
#define STORYBOARD_ID_INTEREST_IPHONE @"interest_iphone"
#define STORYBOARD_ID_CONFIG_IPHONE @"config_iphone"
#define STORYBOARD_ID_HELP_IPHONE @"help_iphone"
#define STORYBOARD_ID_WARNING_IPHONE @"warning_iphone"
#define STORYBOARD_ID_CHATWAIT_IPHONE @"chatwait_iphone"
#define STORYBOARD_ID_CHATCONFIRM_IPHONE @"chatconfirm_iphone"
#define STORYBOARD_ID_CHATWEBRTC_IPHONE @"chatwebrtc_iphone"
#define STORYBOARD_ID_CHATIMPRESS_IPHONE @"chatimpress_iphone"

#define LEFTBAR_WIDTH_IPHONE 120

#define NAVIGATIONBAR_TITLE_HOME NSLocalizedString(@"LeftBar_Home", nil)
#define NAVIGATIONBAR_TITLE_DEVICE NSLocalizedString(@"LeftBar_Device", nil)
#define NAVIGATIONBAR_TITLE_INTEREST NSLocalizedString(@"LeftBar_Interest", nil)
#define NAVIGATIONBAR_TITLE_IMPRESS NSLocalizedString(@"LeftBar_Impress", nil)
#define NAVIGATIONBAR_TITLE_CONFIG NSLocalizedString(@"LeftBar_Config", nil)
#define NAVIGATIONBAR_TITLE_HELP NSLocalizedString(@"LeftBar_Help", nil)

typedef enum
{
    LEFTBAR_CELL_HOME = 0,
    LEFTBAR_CELL_DEVICE,
    LEFTBAR_CELL_IMPRESS,
    LEFTBAR_CELL_INTEREST,
    LEFTBAR_CELL_CONFIG,
    LEFTBAR_CELL_HELP,
    LEFTBAR_CELL_COUNT
}
LEFTBAR_CELL_ID;

@interface GUIModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (nonatomic, strong) MainViewController_iPhone* mainViewController;
@property (nonatomic, strong) LeftBarViewController_iPhone* leftbarViewController;
@property (nonatomic, strong) RHNavigationController* navigationController;
@property (nonatomic, strong) HomeViewController_iPhone* homeViewController;
@property (nonatomic, strong) InterestViewController_iPhone* interestViewController;
@property (nonatomic, strong) ImpressViewController_iPhone* impressViewController;
@property (nonatomic, strong) DeviceViewController_iPhone* deviceViewController;
@property (nonatomic, strong) ConfigViewController_iPhone* configViewController;
@property (nonatomic, strong) HelpViewController_iPhone* helpViewController;
@property (nonatomic, strong) WarningViewController_iPhone* warningViewController;

@property (nonatomic, strong) ChatWaitViewController_iPhone* chatWaitViewController;
@property (nonatomic, strong) ChatConfirmViewController_iPhone* chatConfirmViewContorller;
@property (nonatomic, strong) ChatWebRTCViewController_iPhone* chatWebRTCViewController;
@property (nonatomic, strong) ChatImpressViewController_iPhone* chatImpressViewController;

@property (strong, nonatomic) CBHUDAgent* HUDAgent;

-(void) showHUD:(NSString*) majorStauts minorStatus:(NSString*) minorStatus delay:(NSInteger)seconds;
-(void) showHUD:(NSString*) status delay:(NSInteger) seconds;

- (BOOL) isNetworkActivityIndicatorVisible;
- (void) setNetworkActivityIndicatorVisible:(BOOL) flag;

@end
