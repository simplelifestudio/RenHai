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

#define COUNT_FLOATINGLABELS_3_5 20
#define COUNT_FLOATINGLABELS_4 25
#define ROUHEIGHT_FLOATINGLABEL 35

typedef enum
{
    ChatWaitStatus_WaitForMatch = 0,
    ChatWaitStatus_Matched,
    ChatWaitStatus_Cancel
}
ChatWaitStatus;

@interface ChatWaitViewController_iPhone () <FloatingCloudsViewDelegate>
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

@property (nonatomic, strong) FloatingCloudsView *labelCloudView;

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
    [super viewWillAppear:animated];
    
    [self resetPage];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self pageWillLoad];
}

- (void)viewDidDisappear:(BOOL)animated
{
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

    [self _computeLabelCloudView];
    
    self.navigationItem.title = NSLocalizedString(@"ChatWait_WaitForMatch", nil);    
    [self.navigationController setNavigationBarHidden:NO];
    [self.navigationItem setHidesBackButton:YES];
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
    
    [self _stopLabelCloudViewFloating];
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
    
    [self _setupLabelCloudView];
    
    [self _setupNavigationBar];
    
    [self _setupActionButtons];

    [self _formatFlatUI];
}

-(void)_formatFlatUI
{
    _countLabel.textColor = FLATUI_COLOR_TEXT_LOG;
}

- (void) _setupActionButtons
{
    [_actionButton setTitle:NSLocalizedString(@"ChatWait_Action_Cancel", nil) forState:UIControlStateNormal];
    _actionButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    [_actionButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_actionButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
}

- (void) _setupNavigationBar
{
    [self.navigationController.navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_NAVIGATIONBAR_CHATWIZARD];
}

- (void) _setupLabelCloudView
{
    _labelCloudView = [[FloatingCloudsView alloc] initWithSuperview:_labelCloudContainer];
    _labelCloudView.delegate = self;
    _labelCloudView.backgroundColor = FLATUI_COLOR_CLEAR;
    _labelCloudView.randomColors = @[[UIColor Grey31], [UIColor Grey41], [UIColor Grey51], [UIColor Grey61], [UIColor Grey71]];
}

- (void) _computeLabelCloudView
{
    RHServerData* server = _userDataModule.server;
    RHServerInterestLabelList* olabelList = server.interestLabelList;
    NSArray* labelList = olabelList.current;
    NSMutableArray* labelNameList = [NSMutableArray arrayWithCapacity:labelList.count];
    
    RHInterestCard* interestCard = _userDataModule.device.profile.interestCard;
    NSArray* selfList = interestCard.labelList;
    
    for (RHInterestLabel* label in selfList)
    {
        [labelNameList addObject:label.labelName];
    }
    
    for (RHInterestLabel* label in labelList)
    {
        if (![labelNameList containsObject:label.labelName])
        {
            [labelNameList addObject:label.labelName];
        }
    }
    
    NSUInteger requireLabelCount = 0;
    if (IS_IPHONE5)
    {
        requireLabelCount = COUNT_FLOATINGLABELS_4;
    }
    else if (IS_IPHONE4_OR_4S)
    {
        requireLabelCount = COUNT_FLOATINGLABELS_3_5;
    }
    else if (IS_IPAD1_OR_2_OR_MINI)
    {
        requireLabelCount = COUNT_FLOATINGLABELS_3_5;
    }
    
    if (requireLabelCount > labelNameList.count)
    {
        int rest = requireLabelCount - labelNameList.count;
        
        while (0 < rest)
        {
            NSString* s = NSLocalizedString(@"ChatWait_Label_Default", nil);
            [labelNameList addObject:s];
            
            rest--;
        }
    }
    _labelCloudView.contents = [labelNameList subarrayWithRange:NSMakeRange(0, requireLabelCount)];

    [self _startLabelCloudViewFloating];
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
    
    _actionButton.hidden = YES;
    
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
            [self _clockCancel];
            
            [_guiModule.mainViewController switchToMainScene];
        }
        else
        {

        }
        
        _actionButton.hidden = NO;
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
            isActionButtonHide = NO;
            isTextClear = NO;
            break;
        }
        case ChatWaitStatus_Cancel:
        {
            infoText = NSLocalizedString(@"ChatWait_Cancel", nil);
            infoDetailText = NSLocalizedString(@"ChatWait_Cancel_Detail", nil);
            isActionButtonHide = NO;
            isTextClear = NO;
            break;
        }
        default:
        {
            break;
        }
    }

    self.navigationItem.title = infoText;
    
    _actionButton.titleLabel.text = actionButtonTitle;
    _actionButton.hidden = isActionButtonHide;
}

-(void)_activateAlohaTimer
{
//    [CBAppUtils asyncProcessInBackgroundThread:^(){
//        [self _deactivateAlohaTimer];
//        
//        _alohaTimer = [[NSTimer alloc] initWithFireDate:[NSDate distantPast] interval:INTERVAL_ALOHA target:self selector:@selector(_remoteAloha) userInfo:nil repeats:YES];
//
//        NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
//        [currentRunLoop addTimer:_alohaTimer forMode:NSRunLoopCommonModes];
//        [currentRunLoop run];
//    }];
}

-(void)_deactivateAlohaTimer
{
//    if (nil != _alohaTimer)
//    {
//        [_alohaTimer invalidate];
//        _alohaTimer = nil;
//    }
}

-(void) _remoteAloha
{
    DDLogVerbose(@"#####ChatWait-Aloha");
    
    [_commModule alohaRequest:_userDataModule.device];
}

-(void)_startLabelCloudViewFloating
{
    _labelCloudView.rowHeight = ROUHEIGHT_FLOATINGLABEL;
    [_labelCloudView show];
    [_labelCloudView beginAnimation];
}

-(void)_stopLabelCloudViewFloating
{
    [_labelCloudView stopAnimation];
}

-(void)_registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_deactivateAlohaTimer)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_activateAlohaTimer) name:UIApplicationDidBecomeActiveNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_stopLabelCloudViewFloating)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_startLabelCloudViewFloating) name:UIApplicationDidBecomeActiveNotification object:nil];
}

-(void)_unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - FloatingCloudsViewDelegate

- (void)didTapLabel:(UILabel *)label
{
    
}

@end
