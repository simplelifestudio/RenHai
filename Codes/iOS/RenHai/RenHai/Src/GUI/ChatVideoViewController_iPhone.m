//
//  ChatVideoViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatVideoViewController_iPhone.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#define DELAY_ENDCHAT 1.0f

#define INTERVAL_ALOHA 30

@interface ChatVideoViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    NSTimer* _alohaTimer;
    
    volatile BOOL _selfEndChatFlag;
    volatile BOOL _partnerEndChatFlag;
    
    volatile BOOL _isDeciding;
}

@end

@implementation ChatVideoViewController_iPhone

@synthesize selfStatusLabel = _selfStatusLabel;
@synthesize partnerStatusLabel = _partnerStatusLabel;

@synthesize endChatButton = _endChatButton;

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:YES];
    
//    [self resetPage];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
//    [self _checkIsOthersideLost];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_VideoOpened", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_VideoOpened", nil);
    
    _selfEndChatFlag = NO;
    _partnerEndChatFlag = NO;
    
    _isDeciding = NO;
    
    _endChatButton.hidden = NO;
}

-(void) pageWillLoad
{
    [self _checkIsOthersideLost];
    
    [self _activateAlohaTimer];
    
    [self _registerNotifications];
}

-(void) pageWillUnload
{
    [self _unregisterNotifications];
    
    [self _deactivateAlohaTimer];
}

-(void) onOthersideEndChat
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_VideoClosed", nil);
    
    [self _endChat];
}

-(void) onOthersideLost
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Lost", nil);
    
    [self _endChat];
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_VideoOpened", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_VideoOpened", nil);
    
    [_endChatButton setTitle:NSLocalizedString(@"ChatVideo_Action_End", nil) forState:UIControlStateNormal];
}

-(void) _moveToChatAssessView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatAssess];
    }];
}

-(void) _endChat
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_VideoClosed", nil);
        _endChatButton.hidden = YES;
    }];
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        @synchronized(self)
        {
            if (_isDeciding)
            {
                return;
            }
            else
            {
                _isDeciding = YES;
            }
        }
        
        [NSThread sleepForTimeInterval:DELAY_ENDCHAT];
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_EndChat device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _selfEndChatFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_EndChat];
                [self _moveToChatAssessView];
            }
            failureCompletionBlock:^(){
                _selfEndChatFlag = NO;
            }
            afterCompletionBlock:nil
         ];
    }];
}

-(void) _checkIsOthersideLost
{
    BusinessStatusModule* statusModule = [BusinessStatusModule sharedInstance];
    BusinessStatus* currentStatus = statusModule.currentBusinessStatus;
    ServerNotificationIdentifier serverNotificationId = currentStatus.latestServerNotificationRecord;
    if (serverNotificationId == ServerNotificationIdentifier_OthersideLost)
    {
        [self onOthersideLost];
    }
    else if (serverNotificationId == ServerNotificationIdentifier_OthersideEndChat)
    {
        [self onOthersideEndChat];
    }
}

-(void)_activateAlohaTimer
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [self _deactivateAlohaTimer];
        
        _alohaTimer = [[NSTimer alloc] initWithFireDate:[NSDate distantPast] interval:INTERVAL_ALOHA target:self selector:@selector(_aloha) userInfo:nil repeats:YES];
        
        NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
        [currentRunLoop addTimer:_alohaTimer forMode:NSRunLoopCommonModes];
        [currentRunLoop run];
    }];
}

-(void)_deactivateAlohaTimer
{
    if (nil != _alohaTimer)
    {
        [_alohaTimer invalidate];
        _alohaTimer = nil;
    }
}

-(void) _aloha
{
    [_commModule alohaRequest:_userDataModule.device];
}

-(void)_registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_deactivateAlohaTimer)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_activateAlohaTimer) name:UIApplicationDidBecomeActiveNotification object:nil];
}

-(void)_unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - IBActions

- (IBAction)didPressEndChatButton:(id)sender
{
    [self _endChat];
}

@end
