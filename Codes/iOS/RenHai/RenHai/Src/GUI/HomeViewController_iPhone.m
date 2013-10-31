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
#define INTERVAL_DATASYNC 10

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

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _updateUIWithServerData];
    [self _updateUIWithEnterOperationStatus:EnterOperationStatus_Ready];
    
    [NSThread detachNewThreadSelector:@selector(_activateDataSyncTimer) toTarget:self withObject:nil];
    
    [self _registerNotifications];
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
    
    [self _setupNavigationBar];
    [self _setupView];
}

-(void)_setupView
{
    _enterButtonProgressView.startAngle = - M_PI_2;
    _enterButtonProgressView.tintColor = FLATUI_COLOR_PROGRESS;
//    _enterButtonProgressView.trackColor = FLATUI_COLOR_PROGRESS_TRACK;
    _enterButtonProgressView.roundProgressViewDelegate = self;
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_HOME;
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if (self.navigationController.revealController.type & PKRevealControllerTypeLeft)
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
    _helpButton.enabled = YES;
    
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
    [self _deactivateDataSyncTimer];
    
    _dataSyncTimer = [NSTimer timerWithTimeInterval:INTERVAL_DATASYNC target:self selector:@selector(_serverDataSync) userInfo:nil repeats:YES];
    
    NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
    [currentRunLoop addTimer:_dataSyncTimer forMode:NSDefaultRunLoopMode];
    [currentRunLoop run];
}

-(void)_deactivateDataSyncTimer
{
    if (nil != _dataSyncTimer)
    {
        [_dataSyncTimer invalidate];
        _dataSyncTimer = nil;
    }
}

- (void)_serverDataSync
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

-(void)_startEnteringPool
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
    
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_EnterPool device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _enterPoolFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_EnterPool];
            }
            failureCompletionBlock:^(){
                _enterPoolFlag = NO;
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
    
    if (_enterPoolFlag)
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
