//
//  HomeViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "HomeViewController_iPhone.h"

#import "CBMathUtils.h"
#import "UINavigationController+CBNavigationControllerExtends.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"

#define INTERVAL_ENTERBUTTON_TRACK 0.1
#define INTERVAL_DATASYNC 5

@interface HomeViewController_iPhone () <CBRoundProgressViewDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    
    RHDevice* _device;
    RHServer* _server;
    
    NSTimer* _enterButtonTimer;
    
    NSTimer* _dataSyncTimer;
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

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _activateDataSyncTimer];
}

-(void) viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];

    [self _deactivateDataSyncTimer];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    
    _device = _userDataModule.device;
    _server = _userDataModule.server;
    
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
    _enterLabel.hidden = YES;
    _helpButton.enabled = NO;
    
    self.navigationItem.leftBarButtonItem.enabled = NO;
    
    UIPanGestureRecognizer* gesturer = self.navigationController.revealController.revealPanGestureRecognizer;
    gesturer.enabled = NO;
}

-(void)_unlockViewController
{
    _enterButton.highlighted = NO;
    _enterButton.enabled = YES;
    _enterLabel.hidden = NO;
    _helpButton.enabled = YES;
    
    self.navigationItem.leftBarButtonItem.enabled = YES;
    
    UIPanGestureRecognizer* gesturer = self.navigationController.revealController.revealPanGestureRecognizer;
    gesturer.enabled = YES;
}

-(void)_enterButtonTimerStarted
{
    [self _enterButtonTimerFinished];
    
    _enterButtonTimer = [NSTimer scheduledTimerWithTimeInterval:INTERVAL_ENTERBUTTON_TRACK target:self selector:@selector(_enterButtonTimerUpdated) userInfo:nil repeats:YES];
    [_enterButtonTimer fire];
}

static float progress = 0.1;
-(void)_enterButtonTimerUpdated
{
    [_enterButtonProgressView setProgress:progress animated:YES];
    progress+=0.1;
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
    
    _dataSyncTimer = [NSTimer scheduledTimerWithTimeInterval:INTERVAL_DATASYNC target:self selector:@selector(_serverDataSync) userInfo:nil repeats:YES];
    [_dataSyncTimer fire];
}

-(void)_deactivateDataSyncTimer
{
    if (nil != _dataSyncTimer)
    {
        [_dataSyncTimer invalidate];
        _dataSyncTimer = nil;
    }
}

-(void)_serverDataSync
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){

        RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:_device info:nil];
        RHMessage* serverDataSyncResponseMessage = [_commModule sendMessage:serverDataSyncRequestMessage];
        
        if (serverDataSyncResponseMessage.messageId == MessageId_ServerDataSyncResponse)
        {
            NSDictionary* messageBody = serverDataSyncResponseMessage.body;
            NSDictionary* serverDic = messageBody;
            
            RHServer* server = _userDataModule.server;
            @try
            {
                [server fromJSONObject:serverDic];
                [self _updateUIWithOnlineDeviceCount];
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
            
        }
        else if (serverDataSyncResponseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (serverDataSyncResponseMessage.messageId == MessageId_ServerTimeoutResponse)
        {
            
        }
        else
        {
            
        }
    });
}

-(void) _updateUIWithOnlineDeviceCount
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        
        NSArray* unitLabels = @[_onlineDeviceCountUnit1, _onlineDeviceCountUnit2, _onlineDeviceCountUnit3, _onlineDeviceCountUnit4, _onlineDeviceCountUnit5];
        
        NSUInteger onlineCount = _server.deviceCount.online;
        NSArray* unitVals = [CBMathUtils splitIntegerByUnit:onlineCount array:nil reverseOrder:YES];
        
        for (int i = 0; i < unitVals.count; i++)
        {
            NSNumber* unitVal = (NSNumber*)unitVals[i];
            UILabel* label = (UILabel*)unitLabels[i];
            label.text = [NSString stringWithFormat:@"%d", unitVal.integerValue];
        }
    });
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
//    RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Random operationType:BusinessSessionRequestType_EnterPool device:_device info:nil];
//    RHMessage* businessSessionResponseMessage = [_commModule sendMessage:businessSessionRequestMessage];
}

- (void) progressFinished
{
    [self _enterButtonTimerFinished];
    
    [self performSelector:@selector(_unlockViewController) withObject:self afterDelay:0.0];
}

@end
