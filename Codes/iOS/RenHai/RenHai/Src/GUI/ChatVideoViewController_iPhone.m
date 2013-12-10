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
#import "WebRTCModule.h"
#import "OpenTokAgent.h"

#import "ChatMessageSendView_iPhone.h"

#define DELAY_ENDCHAT 1.0f

#define INTERVAL_TOOLBARDISPLAYTICK 1
#define INTERVAL_CHATMESSAGE_DISPLAY 60
#define INTERVAL_ALOHA 60

#define _TOOLBAR_DISPLAY_PERIOD 3

#define BORDERWIDTH_VIDEOVIEW 2.5f;
#define CORNERRADIUS_VIDEOVIEW 3.0f;
#define BORDERCOLOR_VIDEOVIEW FLATUI_COLOR_MAJOR_F

#define NIB_CHATMESSAGESENDVIEW @"ChatMessageSendView_iPhone"

@interface ChatVideoViewController_iPhone () <OpenTokDelegate, UIGestureRecognizerDelegate, ChatMessageSendDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    WebRTCModule* _webRTCModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    NSUInteger _count;
    
    NSTimer* _alohaTimer;
    
    volatile BOOL _selfEndChatFlag;
    volatile BOOL _partnerEndChatFlag;
    volatile BOOL _partnerLostFlag;
    
    volatile BOOL _isSelfDeciding;
    NSCondition* _decideLock;
    
    volatile BOOL _isInitialized;
    
    volatile BOOL _isSelfVideoOpen;
    
    volatile BOOL _isChatMessageEnabled;
    
    UITapGestureRecognizer* _singleTapGesturer;
    UIPanGestureRecognizer* _panGesturer;
    
    NSTimer* _toolbarDisplayTimer;
    
    OTVideoView* _subscriberView;
    OTVideoView* _publisherView;
    
    ChatMessageSendView_iPhone* _sendChatMessageView;
}

@end

@implementation ChatVideoViewController_iPhone

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
    
    [self pageWillUnload];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self pageWillLoad];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

-(void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation
                                        duration:(NSTimeInterval)duration
{
    /*
    if (toInterfaceOrientation == UIInterfaceOrientationLandscapeLeft)
    {
        [_subscriberView setCenter:_parterVideoView.center];
        [_subscriberView setTransform:CGAffineTransformMakeRotation(M_PI * 1.5)];
    }
    else if (toInterfaceOrientation == UIInterfaceOrientationLandscapeRight)
    {
        [_subscriberView setCenter:_parterVideoView.center];
        [_subscriberView setTransform:CGAffineTransformMakeRotation(M_PI_2)];
    }
    else if (toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown)
    {
        [_subscriberView setCenter:_parterVideoView.center];
        [_subscriberView setTransform:CGAffineTransformMakeRotation(M_PI)];
    }
    else
    {
        [_subscriberView setCenter:_parterVideoView.center];
        [_subscriberView setTransform:CGAffineTransformMakeRotation(M_PI * 2)];
    }
    */
}

//-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
//{
//    [self.view.subviews enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop)
//     {
//         [_sendChatMessageView resignFirstResponder];
//     }];
//}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Prepairing", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Prepairing", nil);
    
    _selfEndChatFlag = NO;
    _partnerEndChatFlag = NO;
    
    _partnerLostFlag = NO;
    
    [self _resetSelfVieoToOpen];
    
    _isSelfDeciding = NO;
    
    _isChatMessageEnabled = YES;
    
    _count = 0;
    [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];

    [self _setNavigationBarAndToolbarHidden:NO];
    self.navigationItem.title = NSLocalizedString(@"ChatVideo_Title", nil);
    [self.navigationItem setHidesBackButton:YES];
    
    [_sendChatMessageView resignFirstResponder];
}

-(void) pageWillLoad
{
    [self _clockStart];
    
    [self _checkIsOthersideLost];
    
    [self _checkIsOthersideChatMessageUnread];    
    
    [self _registerNotifications];
    
    [self _connectWebRTC];
}

-(void) pageWillUnload
{
    [self _unregisterNotifications];
    
    [self _deactivateAlohaTimer];
    
    [self _disconnectWebRTC];
    
    [self _setNavigationBarAndToolbarHidden:YES];
    
    [self _clockCancel];
}

-(void) onOthersideEndChat
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Disconnected", nil);
    
    [self _remoteEndChat];
}

-(void) onOthersideLost
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Lost", nil);
    
    [self _remoteEndChat];
}

-(void) onOthersideChatMessage
{
    [self _showNewChatMessage];
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _isInitialized = YES;
    
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    _webRTCModule = [WebRTCModule sharedInstance];
    
    _isSelfDeciding = NO;
    _decideLock = [[NSCondition alloc] init];
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Prepairing", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Prepairing", nil);
    
    _selfVideoView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
    _selfVideoView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
    _selfVideoView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
    
    _isChatMessageEnabled = YES;
    
    [self _setupGesturers];
    
    [self _setupNavigationBar];
    [self _setupActionButtons];
    
    [self _setupChatMessageSendView];
    
    [self _setupChatMessageViewController];
    
    [self _formatFlatUI];
}

-(void)_setupChatMessageSendView
{
    if (nil == _sendChatMessageView)
    {
        _sendChatMessageView = [CBUIUtils componentFromNib:NIB_CHATMESSAGESENDVIEW owner:self options:nil];
        [self.view addSubview:_sendChatMessageView];        
        
        CGRect oldFrame = _sendChatMessageView.frame;
        CGRect newFrame = CGRectMake(_selfVideoButton.frame.origin.x, _selfVideoButton.frame.origin.y - oldFrame.size.height * 2, oldFrame.size.width, oldFrame.size.height);
        _sendChatMessageView.frame = newFrame;
        
        _sendChatMessageView.hidden = YES;
        
        _sendChatMessageView.sendDelegate = self;
    }
}

-(void)_setupChatMessageViewController
{
    UIStoryboard* storyboard = [UIStoryboard storyboardWithName:STORYBOARD_IPHONE bundle:nil];
    _chatMessageViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATMESSAGE_IPHONE];
}

-(void)_formatFlatUI
{
    _countLabel.textColor = FLATUI_COLOR_TEXT_LOG;
}

- (void) _setupNavigationBar
{
    [self.navigationController.navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_NAVIGATIONBAR_CHATWIZARD];
}

- (void) _setupActionButtons
{
    [_selfVideoButton setTitle:NSLocalizedString(@"ChatVideo_SelfVideo", nil) forState:UIControlStateNormal];
    [_chatMessageButton setTitle:NSLocalizedString(@"ChatVideo_ChatMessage", nil) forState:UIControlStateNormal];
    [_endChatButton setTitle:NSLocalizedString(@"ChatVideo_Action_End", nil) forState:UIControlStateNormal];
    
    _selfVideoButton.buttonColor = FLATUI_COLOR_BUTTONNORMAL;
    [_selfVideoButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_selfVideoButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    _chatMessageButton.buttonColor = FLATUI_COLOR_BUTTONNORMAL;
    [_chatMessageButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_chatMessageButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    _endChatButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    [_endChatButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_endChatButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
}

-(void) _setupGesturers
{
    _singleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didSingleTapped:)];
    _singleTapGesturer.delegate = self;
    _singleTapGesturer.numberOfTapsRequired = 1;
    [_maskView addGestureRecognizer:_singleTapGesturer];
    
    _panGesturer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(_didPanned:)];
    [_panGesturer setMaximumNumberOfTouches:1];
    [_panGesturer setMinimumNumberOfTouches:1];
    [_maskView addGestureRecognizer:_panGesturer];
    
    [_singleTapGesturer requireGestureRecognizerToFail:_panGesturer];
}

-(void) _didSingleTapped:(UITapGestureRecognizer*) recognizer
{
    if (!_sendChatMessageView.hidden)
    {
         [_sendChatMessageView resignFirstResponder];
        return;
    }
    
    BOOL oldStatus = [self _isNavigationBarAndToolbarHidden];
    [self _setNavigationBarAndToolbarHidden:!oldStatus];
}

-(void) _didPanned:(UIPanGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_selfVideoView.frame, locationTouch))
    {
        locationTouch = [_selfVideoView convertPoint:locationTouch fromView:self.view];
        
        if (recognizer.state == UIGestureRecognizerStateChanged || recognizer.state == UIGestureRecognizerStateEnded)
        {
            [self _relayoutSelfVideoViewByDragging:recognizer];
        }
    }
}

-(void) _relayoutSelfVideoViewByBarsShowingOrHidding:(BOOL) hidden
{
    UIView* parentView = self.view;
    CGFloat parentViewHeight = parentView.frame.size.height;
    
    UIView* draggedView = _selfVideoView;
    CGFloat draggedViewCenterX = draggedView.center.x;
    CGFloat draggedViewCenterY = draggedView.center.y;
    CGFloat draggedViewHeight = draggedView.frame.size.height;
    
    CGFloat draggedViewHeightHalf = draggedViewHeight / 2;
    
    CGFloat actionBarHeight = _endChatButton.frame.size.height;

    CGPoint draggedViewNewCenterPoint = CGPointMake(draggedViewCenterX, draggedViewCenterY);

    if (hidden)
    {
        if (0 >= (draggedViewNewCenterPoint.y - draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = draggedViewHeightHalf + 1;
        }
        else if ((parentViewHeight - actionBarHeight) <= (draggedViewNewCenterPoint.y + draggedViewHeightHalf + 64))
        {
            draggedViewNewCenterPoint.y = parentViewHeight - draggedViewHeightHalf;
        }
    }
    else
    {
        parentViewHeight = parentViewHeight - actionBarHeight;
        
        if (0 >= (draggedViewNewCenterPoint.y - draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = draggedViewHeightHalf + 1;
        }
        else if (parentViewHeight < (draggedViewNewCenterPoint.y + draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = parentViewHeight - draggedViewHeightHalf;
        }
    }

    [draggedView setCenter:draggedViewNewCenterPoint];
}

-(void) _relayoutSelfVideoViewByDragging:(UIPanGestureRecognizer*) recognizer
{
    UIView* parentView = self.view;
    CGFloat parentViewWidth = parentView.frame.size.width;
    CGFloat parentViewHeight = parentView.frame.size.height;
    
    UIView* draggedView = _selfVideoView;
    CGFloat draggedViewCenterX = draggedView.center.x;
    CGFloat draggedViewCenterY = draggedView.center.y;
    CGFloat draggedViewWidth = draggedView.frame.size.width;
    CGFloat draggedViewHeight = draggedView.frame.size.height;
    
    //这里取得的参照坐标系是parentView的坐标
    CGPoint draggedViewOffsetInParentView = [recognizer translationInView:parentView];
    
    //通过计算偏移量来设定draggedView的新坐标
    CGPoint draggedViewNewCenterPoint = CGPointMake(draggedViewCenterX + draggedViewOffsetInParentView.x, draggedViewCenterY + draggedViewOffsetInParentView.y);
    
    CGFloat draggedViewWidthHalf = draggedViewWidth / 2;
    CGFloat draggedViewHeightHalf = draggedViewHeight / 2;
    
    //边界保护
    if (0 >= (draggedViewNewCenterPoint.x - draggedViewWidthHalf))
    {
        draggedViewNewCenterPoint.x = draggedViewWidthHalf + 1;
    }
    else if (parentViewWidth <= (draggedViewNewCenterPoint.x + draggedViewWidthHalf))
    {
        draggedViewNewCenterPoint.x = parentViewWidth - draggedViewWidthHalf - 1;
    }
    
    if ([self _isNavigationBarAndToolbarHidden])
    {
        if (0 >= (draggedViewNewCenterPoint.y - draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = draggedViewHeightHalf + 1;
        }
        else if (parentViewHeight < (draggedViewNewCenterPoint.y + draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = parentViewHeight - draggedViewHeightHalf;
        }
    }
    else
    {
        CGFloat actionBarHeight = _endChatButton.frame.size.height;
        
        if (0 >= (draggedViewNewCenterPoint.y - draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = draggedViewHeightHalf + 1;
        }
        else if ((parentViewHeight - actionBarHeight) < (draggedViewNewCenterPoint.y + draggedViewHeightHalf))
        {
            draggedViewNewCenterPoint.y = parentViewHeight - actionBarHeight - draggedViewHeightHalf;
        }
    }
    
    //设定新中心点
    [draggedView setCenter:draggedViewNewCenterPoint];
    DDLogVerbose(@"draggedView new point is at: %lf, %lf", draggedViewNewCenterPoint.x, draggedViewNewCenterPoint.y);
    
    //初始化sender中的坐标位置。如果不初始化，移动坐标会一直积累起来
    [recognizer setTranslation:CGPointMake(0, 0) inView:parentView];
}

-(void) _moveToChatAssessView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatAssess];
    }];
}

-(BOOL) _isNavigationBarAndToolbarHidden
{
    return _endChatButton.hidden;
}

-(void) _setNavigationBarAndToolbarHidden:(BOOL) hidden
{
    if (hidden)
    {
        [self _deactivateToolbarDisplayTimer];
    }
    else
    {
        [self _activateToolbarDisplayTimer];
    }
    
    [self.navigationController setNavigationBarHidden:YES animated:NO];
    _selfVideoButton.hidden = YES;
    _chatMessageButton.hidden = hidden;
    _endChatButton.hidden = hidden;
    
    if (_subscriberView)
    {
        _subscriberView.frame = CGRectMake(0, 0, _parterVideoView.frame.size.width, _parterVideoView.frame.size.height);
    }
    
    if (_publisherView)
    {
        _publisherView.frame = CGRectMake(0, 0, _selfVideoView.frame.size.width, _selfVideoView.frame.size.height);
    }

    [self _relayoutSelfVideoViewByBarsShowingOrHidding:hidden];
}

-(void) _remoteEndChat
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _isChatMessageEnabled = NO;
        [_sendChatMessageView resignFirstResponder];
        
        [[MessageBarManager sharedInstance] dismissAllMessages];
        
        _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Disconnected", nil);
        [self _resetSelfVideoToClose];
        
        [self _setNavigationBarAndToolbarHidden:NO];
    }];
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        [_decideLock lock];
        if (_isSelfDeciding)
        {
            [_decideLock wait];
        }
        else
        {
            _isSelfDeciding = YES;
        }
        
        [NSThread sleepForTimeInterval:DELAY_ENDCHAT];
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        NSString* businessSessionId = businessSession.businessSessionId;
        RHBusinessType businessType = businessSession.businessType;
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:BusinessSessionRequestType_EndChat device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _selfEndChatFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_EndChat];
                [self _moveToChatAssessView];
            }
            failureCompletionBlock:^(){
                _selfEndChatFlag = NO;
            }
            afterCompletionBlock:^(){
               
            }
         ];
        
        _isSelfDeciding = NO;
        [_decideLock signal];
        [_decideLock unlock];
    }];
}

- (void) _remoteSendChatMessage:(RHChatMessage*) message
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        UserDataModule* userDataModule = [UserDataModule sharedInstance];
        RHBusinessSession* businessSession = userDataModule.businessSession;
        RHDevice* device = userDataModule.device;
        
        [businessSession addChatMessage:message];
        
        NSString* businessSessionId = businessSession.businessSessionId;
        RHBusinessType businessType = businessSession.businessType;
        BusinessSessionRequestType businessSessionRequestType = BusinessSessionRequestType_ChatMessage;
        NSDictionary* info = [NSDictionary dictionaryWithObject:message.text forKey:MESSAGE_KEY_CHATMESSAGE];
        
        CommunicationModule* commModule = [CommunicationModule sharedInstance];
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:businessSessionRequestType device:device info:info];
        [commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){

            }
            failureCompletionBlock:^(){

            }
            afterCompletionBlock:^(){
              [CBAppUtils asyncProcessInMainThread:^(){
                  
              }];
            }
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

- (void) _checkIsOthersideChatMessageUnread
{
    RHBusinessSession* businessSession = _userDataModule.businessSession;
    if ([businessSession hasNewChatMessage])
    {
        [self _showNewChatMessage];        
    }
}

static NSInteger _kToolbarDisplaySeconds = 0;
-(void)_toolbarDisplayTimerClick
{
    if (_TOOLBAR_DISPLAY_PERIOD <= _kToolbarDisplaySeconds)
    {
        [self _setNavigationBarAndToolbarHidden:YES];
    }
    else
    {
        _kToolbarDisplaySeconds++;
    }
}

-(void)_activateToolbarDisplayTimer
{
//    [CBAppUtils asyncProcessInBackgroundThread:^(){
//        [self _deactivateToolbarDisplayTimer];
//        
//        _toolbarDisplayTimer = [[NSTimer alloc] initWithFireDate:[NSDate distantPast] interval:INTERVAL_TOOLBARDISPLAYTICK target:self selector:@selector(_toolbarDisplayTimerClick) userInfo:nil repeats:YES];
//        
//        NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
//        [currentRunLoop addTimer:_toolbarDisplayTimer forMode:NSRunLoopCommonModes];
//        [currentRunLoop run];
//    }];
}

-(void)_deactivateToolbarDisplayTimer
{
//    _kToolbarDisplaySeconds = 0;
//    
//    if (nil != _toolbarDisplayTimer)
//    {
//        [_toolbarDisplayTimer invalidate];
//        _toolbarDisplayTimer = nil;
//    }
}

-(void)_activateAlohaTimer
{
    _alohaTimer = [NSTimer scheduledTimerWithTimeInterval:INTERVAL_ALOHA target:self selector:@selector(_remoteAloha) userInfo:nil repeats:YES];
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
//    DDLogVerbose(@"#####ChatVideo-Aloha");
//    [_commModule alohaRequest:_userDataModule.device];
}

-(void)_registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_deactivateAlohaTimer)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_activateAlohaTimer) name:UIApplicationDidBecomeActiveNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_deactivateToolbarDisplayTimer)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_activateToolbarDisplayTimer) name:UIApplicationDidBecomeActiveNotification object:nil];
}

-(void)_unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)_connectWebRTC
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [_webRTCModule registerWebRTCDelegate:self];
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        RHWebRTC* webrtc = businessSession.webrtc;
        
        NSString* apiKey = [NSString stringWithFormat:@"%@", webrtc.apiKey];
        NSString* sessionId = webrtc.sessionId;
        NSString* token = webrtc.token;
        
        if (STATIC_OPENTOK_ACCOUNT)
        {
            apiKey = kApiKey;
            sessionId = kSessionId;
            token = kToken;
        }
        
        [_webRTCModule connectAndPublishOnWebRTC:apiKey sessionId:sessionId token:token];
    }];
}

-(void)_disconnectWebRTC
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [_webRTCModule unregisterWebRTCDelegate];
        [_webRTCModule unpublishAndDisconnectOnWebRTC];
    }];
}

- (void) _clockStart
{
    [self _clockCancel];
    
    NSTimeInterval interval = 1.0;
    _timer = [NSTimer timerWithTimeInterval:interval target:self selector:@selector(_clockTick) userInfo:nil repeats:YES];
    NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
    [currentRunLoop addTimer:_timer forMode:NSDefaultRunLoopMode];
    [_timer fire];
    
    _countLabel.hidden = NO;
}

- (void) _clockTick
{
    [self _updateCountLabel];
    
    _count++;
}

- (void) _clockCancel
{
    if (nil != _timer)
    {
        [_timer invalidate];
        _timer = nil;
    }
    
    _countLabel.hidden = YES;
}

- (void) _updateCountLabel
{
    [CBAppUtils asyncProcessInMainThread:^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
    }];
}

- (void) _resetSelfVideoToClose
{
    _isSelfVideoOpen = NO;
    _selfVideoView.hidden = !_isSelfVideoOpen;
}

- (void) _resetSelfVieoToOpen
{
    _isSelfVideoOpen = YES;
    _selfVideoView.hidden = !_isSelfVideoOpen;
}

- (void) _switchSelfVideoOpenOrClose
{
    _isSelfVideoOpen = !_isSelfVideoOpen;
    _selfVideoView.hidden = !_isSelfVideoOpen;
}

- (void) _switchChatMessageSendViewOpenOrClose
{
    BOOL hidden = _sendChatMessageView.hidden;
    _sendChatMessageView.hidden = !hidden;
    
    if (!_sendChatMessageView.hidden)
    {
        [_sendChatMessageView.textField becomeFirstResponder];
    }
}

- (void) _popChatMessageViewController
{
//    [_chatMessageViewController popConnectView:self animated:YES];
//    [_guiModule.chatWizardController pushViewController:_chatMessageViewController animated:YES];
}

- (void) _showNewChatMessage
{
    if (_isChatMessageEnabled)
    {
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        RHChatMessage* chatMessage = [businessSession readChatMessage];
        
        [[MessageBarManager sharedInstance] showMessageWithTitle:NSLocalizedString(@"ChatVideo_PartnerChatMessage", nil)
                                                     description:chatMessage.text
                                                            type:MessageBarMessageTypeInfo
                                                     forDuration:INTERVAL_CHATMESSAGE_DISPLAY];
    }
}

#pragma mark - IBActions

- (IBAction)didPressEndChatButton:(id)sender
{
    [self _remoteEndChat];
}

- (IBAction)didPressChatMessageButton:(id)sender
{
    [self _switchChatMessageSendViewOpenOrClose];
}

- (IBAction)didPressSelfVideoButton:(id)sender
{
    [self _switchSelfVideoOpenOrClose];
}

#pragma mark - OpenTokDelegate

-(void) sessionDidConnect
{

}

-(void) sessionDidDisconnect
{

}

-(void) sessionDidFailWithError
{
    [self _remoteEndChat];
}

-(void) sessionDidReceiveSelfStream
{
    OpenTokAgent* agent = _webRTCModule.openTokAgent;
    _publisherView = agent.publisherView;
    if (nil != _publisherView)
    {
        [agent mutePublisher:YES];
        
        CGRect superFrame = _selfVideoView.frame;
        CGRect selfFrame = CGRectMake(0, 0, superFrame.size.width, superFrame.size.height);
        
        [_publisherView setFrame:selfFrame];
        _publisherView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
        _publisherView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
        _publisherView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
        
        [_selfVideoView addSubview:_publisherView];
        
        [_selfVideoView setNeedsDisplay];
        
        [self _activateAlohaTimer];
    }
}

-(void) sessionDidReceivePartnerStream
{

}

-(void) sessionDidDropPartnerStream;
{
    [self _remoteEndChat];
}

-(void) sessionDidPartnerConnected
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Connected", nil);
}

-(void) sessionDidPartnerDisConnected
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Disconnected", nil);
    
    [self _remoteEndChat];
}

-(void) publisherDidFailWithError;
{
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Failed", nil);
}

-(void) subscriberDidConnectToStream
{
    DDLogVerbose(@"#####WebRTC: Subscriber begins to connect streaming.");
    
    OpenTokAgent* agent = _webRTCModule.openTokAgent;
    _subscriberView = agent.subscriberView;
    if (nil != _subscriberView)
    {
        [agent muteSubscriber:YES];
        
        CGRect superFrame = _parterVideoView.frame;
        CGRect selfFrame = CGRectMake(0, 0, superFrame.size.width, superFrame.size.height);
        
        [_subscriberView setFrame:selfFrame];
        [_parterVideoView addSubview:_subscriberView];
        [_parterVideoView bringSubviewToFront:_selfVideoView];
        
        [self _clockCancel];
    }
    
    DDLogVerbose(@"#####WebRTC: Subscriber finishes to connect streaming.");
}

-(void) subscriberDidChangeVideoDimensions:(CGSize)dimensions
{
    
}

-(void) subscriberDidFailWithError
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Failed", nil);
}

-(void) subscriberDidChangeVideoDimensions
{
    
}

#pragma mark - ChatMessageSendDelegate

-(void) onSendMessage:(NSString *)text
{
    RHChatMessage* chatMessage = [[RHChatMessage alloc] initWithSender:ChatMessageSender_Self andText:text];
    [self _remoteSendChatMessage:chatMessage];
    
//    [self _switchChatMessageSendViewOpenOrClose];
}

@end
