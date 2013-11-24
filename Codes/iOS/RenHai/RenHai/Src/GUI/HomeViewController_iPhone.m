//
//  HomeViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "HomeViewController_iPhone.h"

#import "CBUIUtils.h"
#import "CBMathUtils.h"
#import "UINavigationController+CBNavigationControllerExtends.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#define INTERVAL_ENTERBUTTON_TRACK CIRCLE_ANIMATION_DISPLAY
#define INTERVAL_DATASYNC 5.0f

typedef enum
{
    EnterOperationStatus_Ready = 0,
    EnterOperationStatus_InProcess,
    EnterOperationStatus_Success,
    EnterOperationStatus_Fail
}
EnterOperationStatus;

@interface HomeViewController_iPhone () <CBRoundProgressViewDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    NSTimer* _enterButtonTimer;
    
    NSTimer* _dataSyncTimer;
    
    volatile BOOL _enterPoolFlag;
    NSCondition* _enterPoolFlagBlock;
    
    volatile BOOL _reachFlag;
    NSCondition* _reachBlock;
}

@end

@implementation HomeViewController_iPhone

@synthesize enterButtonProgressView = _enterButtonProgressView;
@synthesize enterButton = _enterButton;
@synthesize enterLabel = _enterLabel;
@synthesize helpButton = _helpButton;

@synthesize onlineDeviceCountUnit1 = _onlineDeviceCountUnit1;
@synthesize onlineDeviceCountUnit2 = _onlineDeviceCountUnit2;
@synthesize onlineDeviceCountUnit3 = _onlineDeviceCountUnit3;
@synthesize onlineDeviceCountUnit4 = _onlineDeviceCountUnit4;
@synthesize onlineDeviceCountUnit5 = _onlineDeviceCountUnit5;

@synthesize chatDeviceCountUnit1 = _chatDeviceCountUnit1;
@synthesize chatDeviceCountUnit2 = _chatDeviceCountUnit2;
@synthesize chatDeviceCountUnit3 = _chatDeviceCountUnit3;
@synthesize chatDeviceCountUnit4 = _chatDeviceCountUnit4;
@synthesize chatDeviceCountUnit5 = _chatDeviceCountUnit5;

@synthesize versionLabel = _versionLabel;

@synthesize onlineDeviceCountLabel = _onlineDeviceCountLabel;
@synthesize chatDeviceCountLabel = _chatDeviceCountLabel;

@synthesize bannerView = _bannerView;

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
}

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _updateUIWithServerData];
    [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Ready];
    
    [self _activateDataSyncTimer];
    
    [self _registerNotifications];
    
    [_bannerView scrollLabelIfNeeded];
}

-(void) viewWillDisappear:(BOOL)animated
{
    [self _deactivateDataSyncTimer];
    
    [self _unregisterNotifications];
    
    [super viewWillDisappear:animated];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    
    _enterPoolFlag = NO;
    _enterPoolFlagBlock = [[NSCondition alloc] init];
    _reachFlag = NO;
    _reachBlock = [[NSCondition alloc] init];
    
    [self _setupNavigationBar];
    [self _setupView];
}

-(void)_setupView
{
    _onlineDeviceCountLabel.text = NSLocalizedString(@"Home_OnlineCount", nil);
    _chatDeviceCountLabel.text = NSLocalizedString(@"Home_ChatCount", nil);
    
    _enterButtonProgressView.startAngle = - M_PI_2;
    _enterButtonProgressView.tintColor = FLATUI_COLOR_PROGRESS;
//    _enterButtonProgressView.trackColor = FLATUI_COLOR_PROGRESS_TRACK;
    _enterButtonProgressView.roundProgressViewDelegate = self;
    
    _versionLabel.text = _appDataModule.appVersion;
    
    [self _setupBannerView];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_HOME;
}

-(void)_setupBannerView
{
    RHProxy* proxy = _userDataModule.proxy;
    
    NSString* text = @"";
    
    switch (proxy.serviceStatus)
    {
        case ServerServiceStatus_Normal:
        {
            text = NSLocalizedString(@"Home_Banner_NoInfo", nil);
            break;
        }
        case ServerServiceStatus_Maintenance:
        {
            RHStatusPeriod* period = proxy.statusPeriod;
            NSString* localBeginTimeStr = period.localBeginTimeString;
            NSString* localEndTimeStr = period.localEndTimeString;
            NSString* periodStr = [NSString stringWithFormat:NSLocalizedString(@"Home_Banner_Maintenance", nil), localBeginTimeStr, localEndTimeStr];
            text = periodStr;
            break;
        }
        default:
        {
            break;
        }
    }

    _bannerView.text = text;
    //    _bannerView.textColor = [UIColor blueColor];
    _bannerView.labelSpacing = 50; // distance between start and end labels
    _bannerView.pauseInterval = 1.5; // seconds of pause before scrolling starts again
    _bannerView.scrollSpeed = 60; // pixels per second
    _bannerView.textAlignment = NSTextAlignmentCenter; // centers text when no auto-scrolling is applied
    _bannerView.fadeLength = 18.0f;
    _bannerView.font = [UIFont systemFontOfSize:BIG_FONT];
    _bannerView.scrollDirection = CBAutoScrollDirectionLeft;
    
    [_bannerView observeApplicationNotifications];
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if ([self.navigationController.revealController hasLeftViewController])
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
//    _enterLabel.hidden = YES;
    _helpButton.enabled = NO;
    _versionLabel.enabled = NO;
    [self _updateUIWithEnterOperationStatus:EnterOperationStatus_InProcess];
    
    self.navigationItem.leftBarButtonItem.enabled = NO;
    
    UIPanGestureRecognizer* gesturer = self.navigationController.revealController.revealPanGestureRecognizer;
    gesturer.enabled = NO;
}

-(void)_unlockViewController
{
    _enterButton.highlighted = NO;
    _enterButton.enabled = YES;
//    _enterLabel.hidden = NO;
    _helpButton.enabled = NO;
    _versionLabel.enabled = YES;
    
    self.navigationItem.leftBarButtonItem.enabled = YES;
    
    UIPanGestureRecognizer* gesturer = self.navigationController.revealController.revealPanGestureRecognizer;
    gesturer.enabled = YES;
}

-(void)_enterButtonTimerStarted
{
    [self _enterButtonTimerFinished];
    
    _enterButtonTimer = [NSTimer scheduledTimerWithTimeInterval:INTERVAL_ENTERBUTTON_TRACK target:self selector:@selector(_enterButtonTimerUpdated) userInfo:nil repeats:YES];
}

static float progress = 0.0;
-(void)_enterButtonTimerUpdated
{
    [_enterButtonProgressView setProgress:progress animated:YES];
    progress+=INTERVAL_ENTERBUTTON_TRACK;
}

-(void)_enterButtonTimerFinished
{
    [_enterButtonTimer invalidate];
    
    progress = 0.0;
    [_enterButtonProgressView setProgress:progress animated:NO];
}

-(void)_activateDataSyncTimer
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [self _deactivateDataSyncTimer];
        
        _dataSyncTimer = [[NSTimer alloc] initWithFireDate:[NSDate distantPast] interval:INTERVAL_DATASYNC target:self selector:@selector(_remoteServerDataSync) userInfo:nil repeats:YES];
        NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
        [currentRunLoop addTimer:_dataSyncTimer forMode:NSDefaultRunLoopMode];
        [currentRunLoop run];
    }];
}

-(void)_deactivateDataSyncTimer
{
    if (nil != _dataSyncTimer)
    {
        [_dataSyncTimer invalidate];
        _dataSyncTimer = nil;
    }
}

- (void)_remoteServerDataSync
{
    RHDevice* device = _userDataModule.device;
    
    RHMessage* requestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:device info:nil];
    
    [_commModule serverDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* serverDic){
            RHServer* server = _userDataModule.server;
            @try
            {
                [server fromJSONObject:serverDic];
                
                [self _updateUIWithServerData];
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        failureCompletionBlock:nil
        afterCompletionBlock:nil
     ];
}

-(void) _updateUIWithServerData
{
    [self _updateUIWithOnlineDeviceCount];
    [self _updateUIWithInterestChatDeviceCount];
}

-(void) _updateUIWithOnlineDeviceCount
{
    [CBAppUtils asyncProcessInMainThread:^(){
        RHServer* server = _userDataModule.server;
        if (nil != server)
        {
            NSArray* unitLabels = @[_onlineDeviceCountUnit1, _onlineDeviceCountUnit2, _onlineDeviceCountUnit3, _onlineDeviceCountUnit4, _onlineDeviceCountUnit5];
            
            NSUInteger chatCount = server.deviceCount.online;
            NSArray* unitVals = [CBMathUtils splitIntegerByUnit:chatCount array:nil reverseOrder:YES];
            
            for (int i = 0; i < unitVals.count; i++)
            {
                NSNumber* unitVal = (NSNumber*)unitVals[i];
                UILabel* label = (UILabel*)unitLabels[i];
                label.text = [NSString stringWithFormat:@"%d", unitVal.integerValue];
            }
        }
    }];
}

-(void) _updateUIWithInterestChatDeviceCount
{
    [CBAppUtils asyncProcessInMainThread:^(){
        RHServer* server = _userDataModule.server;
        if (nil != server)
        {
            NSArray* unitLabels = @[_chatDeviceCountUnit1, _chatDeviceCountUnit2, _chatDeviceCountUnit3, _chatDeviceCountUnit4, _chatDeviceCountUnit5];
            
            NSUInteger onlineCount = server.deviceCount.chat;
            NSArray* unitVals = [CBMathUtils splitIntegerByUnit:onlineCount array:nil reverseOrder:YES];
            
            for (int i = 0; i < unitVals.count; i++)
            {
                NSNumber* unitVal = (NSNumber*)unitVals[i];
                UILabel* label = (UILabel*)unitLabels[i];
                label.text = [NSString stringWithFormat:@"%d", unitVal.integerValue];
            }
        }
    }];
}

-(void) _updateUIWithEnterOperationStatus:(EnterOperationStatus) status
{
    [CBAppUtils asyncProcessInMainThread:^(){
        switch(status)
        {
            case EnterOperationStatus_Ready:
            {
                _enterLabel.text = NSLocalizedString(@"Home_PressToEnterPool", nil);
                break;
            }
            case EnterOperationStatus_InProcess:
            {
                _enterLabel.text = NSLocalizedString(@"Home_IsEnteringPool", nil);
                break;
            }
            case EnterOperationStatus_Success:
            {
                _enterLabel.text = NSLocalizedString(@"Home_PressToEnterPool", nil);
                break;
            }
            case EnterOperationStatus_Fail:
            {
                _enterLabel.text = NSLocalizedString(@"Home_EnterPoolFailed", nil);
                break;
            }
            default:
            {
                break;
            }
        }
    }];
}

-(void)_updateEnterPoolFlag:(BOOL) flag
{
    [_enterPoolFlagBlock lock];
    
    [self _updateReachFlag:YES];
    
    _enterPoolFlag = flag;
    [_enterPoolFlagBlock signal];
    [_enterPoolFlagBlock unlock];
}

-(BOOL)_checkEnterPoolFlag
{
    [_enterPoolFlagBlock lock];
    
    if (![self _checkReachFlag])
    {
        [_enterPoolFlagBlock wait];
    }
    
    [_enterPoolFlagBlock unlock];
    
    return _enterPoolFlag;
}

-(void)_updateReachFlag:(BOOL) flag
{
    [_reachBlock lock];
    _reachFlag = flag;
    [_reachBlock unlock];
}

-(BOOL)_checkReachFlag
{
    BOOL flag = NO;
    
    [_reachBlock lock];
    flag = _reachFlag;
    [_reachBlock unlock];
    
    return flag;
}

-(void)_startEnteringPool
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
    
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_EnterPool device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                [self _updateEnterPoolFlag:YES];
                [_statusModule recordAppMessage:AppMessageIdentifier_ChooseBusiness];
            }
            failureCompletionBlock:^(){
                [self _updateEnterPoolFlag:NO];
            }
            afterCompletionBlock:nil
         ];
    }];
}

-(void)_finishEnterPool
{
    [CBAppUtils asyncProcessInMainThread:^(){
        MainViewController_iPhone* mainVC = _guiModule.mainViewController;
        [mainVC switchToChatScene];
    }];
}

-(void)_registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_deactivateDataSyncTimer)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_activateDataSyncTimer) name:UIApplicationDidBecomeActiveNotification object:nil];
}

-(void)_unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - IBActions

- (IBAction)onPressEnterButton:(id)sender
{
    [self performSelector:@selector(_lockViewController) withObject:self afterDelay:0.0];
    
    [self _enterButtonTimerStarted];
}

- (IBAction)onPressHelpButton:(id)sender
{
    RHNavigationController* navigationVC = _guiModule.navigationController;
    HelpViewController_iPhone* helpVC = _guiModule.helpViewController;
    
    [helpVC resetDisplayStatus];
    [navigationVC presentViewController:helpVC animated:YES completion:nil];
}

#pragma mark - CBRoundProgressViewDelegate

- (void) progressStarted
{
    [self performSelector:@selector(_startEnteringPool) withObject:self afterDelay:0];
}

- (void) progressFinished
{
    [self _enterButtonTimerFinished];

    [NSThread sleepForTimeInterval:INTERVAL_ENTERBUTTON_TRACK];
    
    [self _unlockViewController];
    
    if ([self _checkEnterPoolFlag])
    {
        [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Success];

        [self _finishEnterPool];
    }
    else
    {
        [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Fail];
    }
}

@end
