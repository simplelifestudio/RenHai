//
//  GUIModule.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#import "GUIStyle.h"

#import "ConnectViewController_iPhone.h"
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
#import "WelcomeViewController_iPhone.h"
#import "UserAgreementViewController_iPhone.h"
#import "UserIntroductionViewController_iPhone.h"

#import "ChatWizardController.h"

#import "MessageBarManager.h"

#define STORYBOARD_IPHONE @"MainStoryboard_iPhone"

#define SEGUE_ID_SPLASH_IPHONE2MAIN_IPHONE @"splash_iphone2main_iphone"

#define STORYBOARD_ID_SPLASH_IPHONE @"splash_iphone"
#define STORYBOARD_ID_CONNECT_IPHONE @"connect_iphone"
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
#define STORYBOARD_ID_WELCOME_IPHONE @"welcome_iphone"
#define STORYBOARD_ID_USERAGREEMENT_IPHONE @"useragreement_iphone"
#define STORYBOARD_ID_USERINTRODUCTION_IPHONE @"userintroduction_iphone"

#define STORYBOARD_ID_CHATWIZARD @"chatwizard"
#define STORYBOARD_ID_CHATWAIT_IPHONE @"chatwait_iphone"
#define STORYBOARD_ID_CHATCONFIRM_IPHONE @"chatconfirm_iphone"
#define STORYBOARD_ID_CHATVIDEO_IPHONE @"chatvideo_iphone"
#define STORYBOARD_ID_CHATMESSAGE_IPHONE @"chatmessage_iphone"
#define STORYBOARD_ID_CHATASSESS_IPHONE @"chatassess_iphone"

#define NIB_CONNECTVIEWCONTROLLER @"ConnectViewController_iPhone"

#define NAVIGATIONBAR_TITLE_HOME NSLocalizedString(@"LeftBar_Home", nil)
#define NAVIGATIONBAR_TITLE_DEVICE NSLocalizedString(@"LeftBar_Device", nil)
#define NAVIGATIONBAR_TITLE_INTEREST NSLocalizedString(@"LeftBar_Interest", nil)
#define NAVIGATIONBAR_TITLE_IMPRESS NSLocalizedString(@"LeftBar_Impress", nil)
#define NAVIGATIONBAR_TITLE_CONFIG NSLocalizedString(@"LeftBar_Config", nil)
#define NAVIGATIONBAR_TITLE_HELP NSLocalizedString(@"LeftBar_Help", nil)

#define NIB_COLLECTIONCELL_LABEL @"RHCollectionLabelCell_iPhone"

#define COLLECTIONCELL_ID_INTERESTLABEL @"RHCollectionLabelCell_iPhone"
#define COLLECTIONCELL_ID_IMPRESSLABEL @"RHCollectionLabelCell_iPhone"

#define REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW @"InterestLabelsHeaderView"
#define REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW @"ServerInterestLabelsHeaderView"
#define REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW @"ImpressLabelsHeaderView"
#define REUSABLEVIEW_ID_ADDIMPRESSLABELSHEADERVIEW @"AddImpressLabelsHeaderView"
#define REUSABLEVIEW_ID_EXISTIMPRESSLABELSHEADERVIEW @"ExistImpressLabelsHeaderView"

#define NIB_CHATVIDEOVIEWCONTROLLER @"ChatVideoViewController_iPhone"

#define NIB_IMPRESSLABELSHEADERVIEW @"ImpressLabelsHeaderView_iPhone"
#define NIB_ADDIMPRESSLABELSHEADERVIEW @"ChatAssessAddImpressLabelsHeaderView_iPhone"
#define NIB_EXISTIMPRESSLABELSHEADERVIEW @"ChatAssessExistImpressLabelsHeaderView_iPhone"

#define NIB_INTERESTLABELSHEADERVIEW @"InterestLabelsHeaderView_iPhone"
#define NIB_SERVERINTERESTLABELSHEADERVIEW @"ServerInterestLabelsHeaderView_iPhone"

#define NIB_LABELMANAGEVIEWCONTROLLER @"RHLabelManageViewController_iPhone"

#define NIB_ALERTVIEWCONTROLLER @"RHAlertViewController_iPhone"

#define NIB_USERINTRODUCTIONVIEWCONTROLLER @"UserIntroductionPage_iPhone"

#define DELAY_UIREFRESHCONTROL_SHOW 0
#define DELAY_UIREFRESHCONTROL_RESET 0

#define TABLECELL_ID_LABELER @"RHTableViewLabelCell_iPhone"
#define NIB_TABLECELL_LABELER @"RHTableViewLabelCell_iPhone"

#define TABLECELL_ID_SWITCHER @"RHTableViewSwitcherCell_iPhone"
#define NIB_TABLECELL_SWITCHER @"RHTableViewSwitcherCell_iPhone"

#define TABLECELL_ID_DEVICEITEM @"RHTableViewLabelCell_iPhone"

#define HELPSCREEN_DISPLAY_SECONDS 9.0f

#define HELPVIEW_ON_APPFIRSTLAUNCHED 1

#define SCREEN_ALWAYS_ON 1

#define LEFTBAR_WIDTH_IPHONE 200

#ifdef DEBUG
#define SPLASHVIEW_HIDE 1
#else
#define SPLASHVIEW_HIDE 0
#endif

typedef enum
{
    SOUNDID_ERROR = 0,
    SOUNDID_CONNECT_RETRY,
    SOUNDID_LABELMANAGED,
    SOUNDID_CHOOSEBUSINESS,
    SOUNDID_UNCHOOSEBUSINESS,
    SOUNDID_SESSIONBOUND,
    SOUNDID_CHATCONFIRM_ACCEPTED,
    SOUNDID_CHATCONFIRM_REJECTED,
    SOUNDID_CHATMESSSAGE_SENT,
    SOUNDID_CHATMESSAGE_RECEIVED,
    SOUNDID_CHATVIDEO_CHATMESSAGE,
    SOUNDID_CHATVIDEO_ENDCHAT,
    SOUNDID_CHATASSESS_CONTINUE,
    SOUNDID_CHATASSESS_QUIT,
}
RHSoundId;

@interface GUIModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (nonatomic, strong) ConnectViewController_iPhone* connectViewController;
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
@property (nonatomic, strong) ChatWizardController* chatWizardController;
@property (nonatomic, strong) WelcomeViewController_iPhone* welcomeViewController;
@property (nonatomic, strong) UserAgreementViewController_iPhone* userAgreementViewController;
@property (nonatomic, strong) UserIntroductionViewController_iPhone* userIntroductionViewController;


-(BOOL) isNetworkActivityIndicatorVisible;
-(void) setNetworkActivityIndicatorVisible:(BOOL) flag;

-(void) keepScreenAlwaysOn:(BOOL) on;

-(void) playSoundAndVibrate:(RHSoundId) soundId vibrate:(BOOL) vibrate;
-(void) playSound:(RHSoundId) soundId;

-(void) showAppMessage:(MessageBarMessageType) messageType messageTitle:(NSString*) messageTitle messageText:(NSString*) messageText visibleDuration:(CGFloat) visibleDuration callBackBlock:(void (^)())callbackBlock;
-(void) dismissAllAppMessages;

@end
