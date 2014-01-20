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

#import <PulsingHalo/PulsingHaloLayer.h>

#define INTERVAL_ENTERBUTTON_TRACK CIRCLE_ANIMATION_DISPLAY
#define INTERVAL_DATASYNC 3

#define HALO_SPEED_FAST 8
#define HALO_SPEED_SLOW 2
#define HALO_RADIUS_3_5 100
#define HALO_RADIUS_4 120
#define HALO_COLOR_FAST FLATUI_COLOR_MAJOR_A
#define HALO_COLOR_SLOW FLATUI_COLOR_MAJOR_A

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
    
    volatile BOOL _reachFlag;
    NSCondition* _reachLock;
}

@property (nonatomic, strong) PulsingHaloLayer* haloLayer;

@end

@implementation HomeViewController_iPhone

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
}

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self _updateBannerView];
    
    [self _updateUIWithServerData];
    [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Ready];
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _activateDataSyncTimer];
    
    [self _registerNotifications];
    
    [_bannerView scrollLabelIfNeeded];
    
    [_guiModule.mainViewController enableGesturers];
    
    [self _startHalo];
}

-(void) viewWillDisappear:(BOOL)animated
{
    [self _stopHalo];
    
    [self _deactivateDataSyncTimer];
    
    [self _unregisterNotifications];
    
    [super viewWillDisappear:animated];
}

-(void) dealloc
{
    [self _deactivateDataSyncTimer];
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
    _reachFlag = NO;
    _reachLock = [[NSCondition alloc] init];
    
    _helpButton.enabled = YES;
//    _helpButton.buttonColor = FLATUI_COLOR_BUTTONPROCESS;
//    [_helpButton setTitle:NSLocalizedString(@"Home_Help", nil) forState:UIControlStateNormal];
//    [_helpButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
//    [_helpButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    [self _setupNavigationBar];
    [self _setupView];
    
    [self _formatFlatUI];
}

-(void)_formatFlatUI
{
    _onlineDeviceCountUnit1.textColor = FLATUI_COLOR_TEXT_LOG;
    _onlineDeviceCountUnit2.textColor = FLATUI_COLOR_TEXT_LOG;
    _onlineDeviceCountUnit3.textColor = FLATUI_COLOR_TEXT_LOG;
    _onlineDeviceCountUnit4.textColor = FLATUI_COLOR_TEXT_LOG;
    _onlineDeviceCountUnit5.textColor = FLATUI_COLOR_TEXT_LOG;
    
    _chatDeviceCountUnit1.textColor = FLATUI_COLOR_TEXT_WARN;
    _chatDeviceCountUnit2.textColor = FLATUI_COLOR_TEXT_WARN;
    _chatDeviceCountUnit3.textColor = FLATUI_COLOR_TEXT_WARN;
    _chatDeviceCountUnit4.textColor = FLATUI_COLOR_TEXT_WARN;
    _chatDeviceCountUnit5.textColor = FLATUI_COLOR_TEXT_WARN;
}

-(void)_setupView
{
    _onlineDeviceCountLabel.text = NSLocalizedString(@"Home_OnlineCount", nil);
    _chatDeviceCountLabel.text = NSLocalizedString(@"Home_ChatCount", nil);
    
    _enterButtonProgressView.startAngle = - M_PI_2;
    _enterButtonProgressView.tintColor = FLATUI_COLOR_TINT_PROGRESS;
//    _enterButtonProgressView.trackColor = FLATUI_COLOR_PROGRESS_TRACK;
    _enterButtonProgressView.roundProgressViewDelegate = self;
    
    _versionLabel.text = _appDataModule.appFullVerion;
    
    [_enterButton setImage:[UIImage imageNamed:@"enterbutton_highlighted.png"] forState:UIControlStateHighlighted];
    
    _helpButton.exclusiveTouch = YES;
    _enterButton.exclusiveTouch = YES;
    
    _haloLayer = [PulsingHaloLayer layer];
    _haloLayer.pulseInterval = 0;
    _haloLayer.position = _enterButton.center;
    _haloLayer.backgroundColor = HALO_COLOR_SLOW.CGColor;

    if (IS_IPHONE5)
    {
        _haloLayer.radius = HALO_RADIUS_4;
    }
    else
    {
        _haloLayer.radius = HALO_RADIUS_3_5;
    }
    
    _haloLayer.speed = HALO_SPEED_SLOW;
    
    [self _updateBannerView];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_HOME;

    [self.navigationController.navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_TINT_NAVIGATIONBAR];
    if (![UIDevice isRunningOniOS7AndLater])
    {
        [self.navigationItem.leftBarButtonItem configureFlatButtonWithColor:FLATUI_COLOR_BARBUTTONITEM highlightedColor:FLATUI_COLOR_BARBUTTONITEM_HIGHLIGHTED cornerRadius:FLATUI_CORNERRADIUS_BARBUTTONITEM];
    }
}

-(void)_updateBannerView
{
    RHProxy* proxy = _userDataModule.proxy;
    RHStatus* status = proxy.status;
    
    NSMutableString* text = [NSMutableString string];
    
    switch (status.serviceStatus)
    {
        case ServerServiceStatus_Normal:
        {
            break;
        }
        case ServerServiceStatus_Maintenance:
        {
            RHStatusPeriod* period = status.statusPeriod;
            NSString* localBeginTimeStr = period.localBeginTimeString;
            NSString* localEndTimeStr = period.localEndTimeString;
            NSString* periodStr = [NSString stringWithFormat:NSLocalizedString(@"Home_Banner_Maintenance", nil), localBeginTimeStr, localEndTimeStr];
            [text appendString:periodStr];
            break;
        }
        default:
        {
            break;
        }
    }
    
    if (nil != proxy.broadcast)
    {
        [text appendString:@" "];        
        [text appendString:proxy.broadcast];
    }

    _bannerView.text = text;
    //    _bannerView.textColor = [UIColor blueColor];
    _bannerView.labelSpacing = 100; // distance between start and end labels
    _bannerView.pauseInterval = 0; // seconds of pause before scrolling starts again
    _bannerView.scrollSpeed = 25; // pixels per second
    _bannerView.textAlignment = NSTextAlignmentCenter; // centers text when no auto-scrolling is applied
    _bannerView.fadeLength = FLATUI_FONT_NORMAL;
    _bannerView.font = [UIFont systemFontOfSize:FLATUI_FONT_NORMAL];
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
    
    [_guiModule.mainViewController disableGesturers];
    
    _haloLayer.speed = HALO_SPEED_FAST;
    _haloLayer.backgroundColor = HALO_COLOR_FAST.CGColor;
    
    [self _enterButtonTimerStarted];
}

-(void)_unlockViewController
{
    [self _enterButtonTimerFinished];
    
    _enterButton.highlighted = NO;
    _enterButton.enabled = YES;
//    _enterLabel.hidden = NO;
    _helpButton.enabled = YES;
    _versionLabel.enabled = YES;
    
    [_guiModule.mainViewController enableGesturers];
    
    _haloLayer.speed = HALO_SPEED_SLOW;
    _haloLayer.backgroundColor = HALO_COLOR_SLOW.CGColor;
    
    self.navigationItem.leftBarButtonItem.enabled = YES;
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
        [currentRunLoop addTimer:_dataSyncTimer forMode:NSRunLoopCommonModes];
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
    
    RHMessage* requestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_DeviceAllSync device:device info:nil];
    
    [_commModule serverDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* serverDic){
            RHServerData* server = _userDataModule.server;
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
        failureCompletionBlock:^(){
            [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_ServerDataSync];
        }
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
        RHServerData* server = _userDataModule.server;
        if (nil != server)
        {
            NSArray* unitLabels = @[_onlineDeviceCountUnit1, _onlineDeviceCountUnit2, _onlineDeviceCountUnit3, _onlineDeviceCountUnit4, _onlineDeviceCountUnit5];
            
            NSUInteger onlineCount = server.deviceCount.online;
            NSArray* unitVals = [CBMathUtils splitIntegerByUnit:onlineCount array:nil reverseOrder:YES];

            for (int i = 0; i < unitLabels.count ; i++)
            {
                NSNumber* unitVal = [NSNumber numberWithInt:0];
                if (i < unitVals.count)
                {
                    unitVal = unitVals[i];
                }
                UILabel* label = (UILabel*)unitLabels[i];
                label.text = [NSString stringWithFormat:@"%d", unitVal.integerValue];
            }
        }
    }];
}

-(void) _updateUIWithInterestChatDeviceCount
{
    [CBAppUtils asyncProcessInMainThread:^(){
        RHServerData* server = _userDataModule.server;
        if (nil != server)
        {
            NSArray* unitLabels = @[_chatDeviceCountUnit1, _chatDeviceCountUnit2, _chatDeviceCountUnit3, _chatDeviceCountUnit4, _chatDeviceCountUnit5];
            
            NSUInteger chatCount = server.deviceCount.chat;
            NSArray* unitVals = [CBMathUtils splitIntegerByUnit:chatCount array:nil reverseOrder:YES];
            
            for (int i = 0; i < unitLabels.count ; i++)
            {
                NSNumber* unitVal = [NSNumber numberWithInt:0];
                if (i < unitVals.count)
                {
                    unitVal = unitVals[i];
                }
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
    _enterPoolFlag = flag;
}

-(BOOL)_checkEnterPoolFlag
{
    return _enterPoolFlag;
}

-(void)_updateReachFlag:(BOOL) flag
{
    _reachFlag = flag;
}

-(BOOL)_checkReachFlag
{
    return _reachFlag;
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
                
                [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_ChooseBusiness];
            }
            afterCompletionBlock:^(){
                
//                NSTimeInterval timeout = 3;
//                
//                NSDate* startTimeStamp = [NSDate date];
//                NSDate* endTimeStamp = [NSDate dateWithTimeInterval:timeout sinceDate:startTimeStamp];
                
                [_reachLock lock];
                while (![self _checkReachFlag])
                {
//                    [_reachLock waitUntilDate:endTimeStamp];
                    [_reachLock wait];
                }
                [self _updateReachFlag:NO];
                
                [CBAppUtils asyncProcessInMainThread:^(){
                    if ([self _checkEnterPoolFlag])
                    {
                        [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Success];
                        
                        [self _finishEnterPool];
                    }
                    else
                    {
                        [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Fail];
                    }
                    
                    [self _unlockViewController];
                }];
                
                [_reachLock unlock];
            }
         ];
    }];
}

-(void)_finishEnterPool
{
    MainViewController_iPhone* mainVC = _guiModule.mainViewController;
    [mainVC switchToChatScene];
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

-(void)_startHalo
{
    [self.navigationController.view.layer insertSublayer:_haloLayer below:_enterButtonProgressView.layer];
}

-(void)_stopHalo
{
    [_haloLayer removeFromSuperlayer];
}

#pragma mark - IBActions

- (IBAction)onPressEnterButton:(id)sender
{
    [_guiModule playSound:SOUNDID_CHOOSEBUSINESS];
    
    [self performSelector:@selector(_lockViewController) withObject:self afterDelay:0.0];
    
    [self performSelector:@selector(_startEnteringPool) withObject:self afterDelay:0.0];
}

- (IBAction)onPressHelpButton:(id)sender
{
    UIViewController* rootVC = [CBUIUtils getRootController];
    UIViewController* vc = _guiModule.helpViewController;
    [rootVC presentViewController:vc animated:YES completion:nil];
}

#pragma mark - CBRoundProgressViewDelegate

- (void) progressStarted
{

}

- (void) progressFinished
{
    [_reachLock lock];
    [self _updateReachFlag:YES];
    [_reachLock signal];
    [_reachLock unlock];
}

@end
