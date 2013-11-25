//
//  ChatWaitViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatWaitViewController_iPhone.h"

#import "CBUIUtils.h"
#import "UINavigationController+CBNavigationControllerExtends.h"

#import "GUIModule.h"
#import "CommunicationModule.h"
#import "UserDataModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#define DELAY CIRCLE_ANIMATION_DISPLAY

#define INTERVAL_ALOHA 60

#define DELAY_MATCHSTART 2.0f

typedef enum
{
    ChatWaitStatus_WaitForMatch = 0,
    ChatWaitStatus_Matched,
    ChatWaitStatus_Cancel
}
ChatWaitStatus;

@interface ChatWaitViewController_iPhone ()
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    UserDataModule* _userDataModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    NSTimer* _timer;
    NSUInteger _count;
    
    NSTimer* _alohaTimer;
    
    NSMutableString* _consoleInfo;
    
    volatile BOOL _leavePoolFlag;
    volatile BOOL _matchStartFlag;
    volatile BOOL _didOnSessionBind;
    
    volatile BOOL _isDeciding;
    
    volatile BOOL _hasRequestedMatchStart;
}

@end

@implementation ChatWaitViewController_iPhone

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
    DDLogInfo(@"#####ChatWait: viewWillAppear");
    
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:YES];
    
    [self resetPage];
}

- (void)viewWillDisappear:(BOOL)animated
{
    DDLogInfo(@"#####ChatWait: viewWillDisappear");
    
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    DDLogInfo(@"#####ChatWait: viewDidAppear");
    
    [super viewDidAppear:animated];
    
    [self pageWillLoad];
}

- (void)viewDidDisappear:(BOOL)animated
{
    DDLogInfo(@"#####ChatWait: viewDidDisappear");
    
    [super viewDidDisappear:animated];
    
    [self pageWillUnload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - IBActions

-(IBAction)didPressActionButton:(id)sender
{
    [self _updateUIWithChatWaitStatus:ChatWaitStatus_Cancel];
    
    [self _startLeavingPool];
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    [self _updateUIWithChatWaitStatus:ChatWaitStatus_WaitForMatch];
    
    _leavePoolFlag = NO;
    _matchStartFlag = NO;
    _didOnSessionBind = NO;
    
    _isDeciding = NO;
    
    _hasRequestedMatchStart = NO;
    
    _count = 0;
    [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
}

-(void) pageWillLoad
{
    [self _clockStart];
    
    [self _activateAlohaTimer];
    
    [self _registerNotifications];
}

-(void) pageWillUnload
{
    [self _unregisterNotifications];
    
    [self _deactivateAlohaTimer];
    
    [self _clockCancel];
}

-(void) onSessionBound
{
    if (!_didOnSessionBind && !_isDeciding)
    {
        [self _updateUIWithChatWaitStatus:ChatWaitStatus_Matched];
        
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatConfirm];
        
        _didOnSessionBind = YES;
    }
}

#pragma mark - Private Methods

- (void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
}

- (void) _clockStart
{
    [self _clockCancel];
    
    NSTimeInterval interval = 1.0;
    _timer = [NSTimer timerWithTimeInterval:interval target:self selector:@selector(_clockTick) userInfo:nil repeats:YES];
    NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
    [currentRunLoop addTimer:_timer forMode:NSDefaultRunLoopMode];
    [_timer fire];
}

- (void) _clockTick
{
    [self _updateCountLabel];
    
    _count++;
    
    if (_count > DELAY_MATCHSTART)
    {
        [self _requestMatchStart];
    }
}

- (void) _clockCancel
{
    if (nil != _timer)
    {
        [_timer invalidate];
        _timer = nil;
    }
}

- (void) _startLeavingPool
{
    _isDeciding = YES;
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_LeavePool device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _leavePoolFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_UnchooseBusiness];
            }
            failureCompletionBlock:^(){
                _leavePoolFlag = NO;
            }
            afterCompletionBlock:^(){
                [self _finishLeavingPool];
            }
         ];
    
    }];
}

- (void) _finishLeavingPool
{
    [CBAppUtils asyncProcessInMainThread:^(){
        if (_leavePoolFlag)
        {
            [NSThread sleepForTimeInterval:DELAY];
            
            [self _clockCancel];
            
            [_guiModule.mainViewController switchToMainScene];
        }
        else
        {
            NSAssert(NO, @"Failed to leave pool!");
        }
    }];
}

-(void)_requestMatchStart
{
    if (_hasRequestedMatchStart)
    {
        return;
    }
    else
    {
        _hasRequestedMatchStart = YES;
    }
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_MatchStart device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _matchStartFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_MatchStart];
            }
            failureCompletionBlock:^(){
                _matchStartFlag = NO;
            }
            afterCompletionBlock:nil
         ];
    }];
}

- (void) _updateCountLabel
{
    [CBAppUtils asyncProcessInMainThread:^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
    }];
}

- (void) _updateInfoTextView:(NSString*) info
{
    if (nil == _consoleInfo)
    {
        _consoleInfo = [NSMutableString string];
        
        NSString* originText = _infoTextView.text;
        if (nil != originText && 0 < originText.length)
        {
            [_consoleInfo appendString:originText];
            [_consoleInfo appendString:@"\n"];
        }
    }
    else
    {
        _consoleInfo = [NSMutableString string];
    }
    
    if (nil != info && 0 < info.length)
    {
        [_consoleInfo appendString:info];
        [_consoleInfo appendString:@"\n"];
        
        [_infoTextView setText:_consoleInfo];
    }
    else
    {
        [_infoTextView setText:@""];
    }
}

- (void) _clearInfoTextView
{
    _consoleInfo = [NSMutableString string];
    [self _updateInfoTextView:nil];
}

- (void) _updateUIWithChatWaitStatus:(ChatWaitStatus) status
{
    NSString* infoText = nil;
    NSString* infoDetailText = nil;
    NSString* actionButtonTitle = NSLocalizedString(@"ChatWait_Action_Cancel", nil);
    BOOL isActionButtonHide = NO;
    BOOL isTextClear = NO;
    
    switch (status)
    {
        case ChatWaitStatus_WaitForMatch:
        {
            infoText = NSLocalizedString(@"ChatWait_WaitForMatch", nil);
            infoDetailText = NSLocalizedString(@"ChatWait_WaitForMatch_Detail", nil);
            isActionButtonHide = NO;
            isTextClear = YES;
            break;
        }
        case ChatWaitStatus_Matched:
        {
            infoText = NSLocalizedString(@"ChatWait_Matched", nil);
            infoDetailText = NSLocalizedString(@"ChatWait_Matched_Detail", nil);
            isActionButtonHide = YES;
            isTextClear = NO;
            break;
        }
        case ChatWaitStatus_Cancel:
        {
            infoText = NSLocalizedString(@"ChatWait_Cancel", nil);
            infoDetailText = NSLocalizedString(@"ChatWait_Cancel_Detail", nil);
            isActionButtonHide = YES;
            isTextClear = NO;
            break;
        }
        default:
        {
            break;
        }
    }
    
    _infoLabel.text = infoText;
    if (isTextClear)
    {
        [self _clearInfoTextView];
    }
    [self _updateInfoTextView:infoDetailText];
    _actionButton.titleLabel.text = actionButtonTitle;
    _actionButton.hidden = isActionButtonHide;
}

-(void)_activateAlohaTimer
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [self _deactivateAlohaTimer];
        
        _alohaTimer = [[NSTimer alloc] initWithFireDate:[NSDate distantPast] interval:INTERVAL_ALOHA target:self selector:@selector(_remoteAloha) userInfo:nil repeats:YES];
        
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

-(void) _remoteAloha
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

@end
