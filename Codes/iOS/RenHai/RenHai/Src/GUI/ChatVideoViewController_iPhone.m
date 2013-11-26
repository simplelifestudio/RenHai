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

#define DELAY_ENDCHAT 1.0f

#define INTERVAL_TOOLBARDISPLAYTICK 1
#define INTERVAL_ALOHA 60

#define _TOOLBAR_DISPLAY_PERIOD 3

#define BORDERWIDTH_VIDEOVIEW 3.0f;
#define CORNERRADIUS_VIDEOVIEW 5.0f;
#define BORDERCOLOR_VIDEOVIEW SECONARDY_COLOR_LIGHT

@interface ChatVideoViewController_iPhone () <OpenTokDelegate, UIGestureRecognizerDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    WebRTCModule* _webRTCModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    NSTimer* _alohaTimer;
    
    volatile BOOL _selfEndChatFlag;
    volatile BOOL _partnerEndChatFlag;
    
    volatile BOOL _isSelfDeciding;
    NSCondition* _decideLock;
    
    volatile BOOL _isInitialized;
    
    volatile BOOL _isSelfVideoOpen;
    
    UITapGestureRecognizer* _singleTapGesturer;
    UIPanGestureRecognizer* _panGesturer;
    
    NSTimer* _toolbarDisplayTimer;
}

@end

@implementation ChatVideoViewController_iPhone

@synthesize selfStatusLabel = _selfStatusLabel;
@synthesize partnerStatusLabel = _partnerStatusLabel;

@synthesize selfVideoView = _selfVideoView;
@synthesize parterVideoView = _parterVideoView;

@synthesize maskView = _maskView;

@synthesize endChatButtonItem = _endChatButtonItem;
@synthesize selfVideoButtonItem = _selfVideoButtonItem;

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
    // Return YES for supported orientations
//    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone)
//    {
//        return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
//    }
//    else
//    {
        return YES;
//    }
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Prepairing", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Prepairing", nil);
    
    _selfEndChatFlag = NO;
    _partnerEndChatFlag = NO;
    
    _isSelfVideoOpen = NO;
    _selfVideoView.hidden = !_isSelfVideoOpen;
    
    _isSelfDeciding = NO;

    [self _setToolbarHidden:YES];
    
    _selfVideoView.autoresizingMask = UIViewAutoresizingFlexibleTopMargin| UIViewAutoresizingFlexibleLeftMargin;
}

-(void) pageWillLoad
{
    [self _checkIsOthersideLost];
    
    [self _activateAlohaTimer];
    
    [self _registerNotifications];
    
    [self _connectWebRTC];
}

-(void) pageWillUnload
{
    [self _unregisterNotifications];
    
    [self _deactivateAlohaTimer];
    
    [self _disconnectWebRTC];
    
    [self _setToolbarHidden:YES];
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
    [_webRTCModule registerWebRTCDelegate:self];
    
    _isSelfDeciding = NO;
    _decideLock = [[NSCondition alloc] init];
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Prepairing", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Prepairing", nil);
    
    _endChatButtonItem.title = NSLocalizedString(@"ChatVideo_Action_End", nil);
    _selfVideoButtonItem.title = NSLocalizedString(@"ChatVideo_SelfVideo", nil);
    
    _selfVideoView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
    _selfVideoView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
    _selfVideoView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
//
//    _parterVideoView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
//    _parterVideoView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
//    _parterVideoView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
    
    [self _setupGesturers];
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
    BOOL oldStatus = [self _isToolbarHidden];
    [self _setToolbarHidden:!oldStatus];
}

-(void) _didPanned:(UIPanGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:_maskView];
    
    if (CGRectContainsPoint(_parterVideoView.frame, locationTouch))
    {
        locationTouch = [_selfVideoView convertPoint:locationTouch fromView:_parterVideoView];
        
        if (recognizer.state == UIGestureRecognizerStateChanged ||
            recognizer.state == UIGestureRecognizerStateEnded)
        {
            //注意，这里取得的参照坐标系是该对象的上层View的坐标。
            CGPoint offset = [recognizer translationInView:_parterVideoView];
            //通过计算偏移量来设定draggableObj的新坐标
            [_selfVideoView setCenter:CGPointMake(_selfVideoView.center.x + offset.x, _selfVideoView.center.y + offset.y)];
            //初始化sender中的坐标位置。如果不初始化，移动坐标会一直积累起来。
            [recognizer setTranslation:CGPointMake(0, 0) inView:_parterVideoView];
        }
    }
}

-(void) _moveToChatAssessView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatAssess];
    }];
}

-(BOOL) _isToolbarHidden
{
    return self.navigationController.toolbar.hidden;
}

-(void) _setToolbarHidden:(BOOL) hidden
{
    if (hidden)
    {
        [self _deactivateToolbarDisplayTimer];
    }
    else
    {
        [self _activateToolbarDisplayTimer];
    }
    
    [self.navigationController setToolbarHidden:hidden animated:YES];
}

-(void) _remoteEndChat
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Disconnected", nil);
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

static NSInteger _kToolbarDisplaySeconds = 0;
-(void)_toolbarDisplayTimerClick
{
    if (_TOOLBAR_DISPLAY_PERIOD <= _kToolbarDisplaySeconds)
    {
        [self _setToolbarHidden:YES];
    }
    else
    {
        _kToolbarDisplaySeconds++;
    }
}

-(void)_activateToolbarDisplayTimer
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [self _deactivateToolbarDisplayTimer];
        
        _toolbarDisplayTimer = [[NSTimer alloc] initWithFireDate:[NSDate distantPast] interval:INTERVAL_TOOLBARDISPLAYTICK target:self selector:@selector(_toolbarDisplayTimerClick) userInfo:nil repeats:YES];
        
        NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
        [currentRunLoop addTimer:_toolbarDisplayTimer forMode:NSRunLoopCommonModes];
        [currentRunLoop run];
    }];
}

-(void)_deactivateToolbarDisplayTimer
{
    _kToolbarDisplaySeconds = 0;
    
    if (nil != _toolbarDisplayTimer)
    {
        [_toolbarDisplayTimer invalidate];
        _toolbarDisplayTimer = nil;
    }
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
        [_webRTCModule unpublishAndDisconnectOnWebRTC];
    }];
}

#pragma mark - IBActions

- (IBAction)didPressEndChatButton:(id)sender
{
    [self _remoteEndChat];
}

- (IBAction)didPressSelfVideoButton:(id)sender
{
    _isSelfVideoOpen = !_isSelfVideoOpen;
    _selfVideoView.hidden = !_isSelfVideoOpen;
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
    OTVideoView* publisherView = agent.publisherView;
    if (nil != publisherView)
    {
        CGRect superFrame = _selfVideoView.frame;
        CGRect selfFrame = CGRectMake(0, 0, superFrame.size.width, superFrame.size.height);
        
        [publisherView setFrame:selfFrame];
        publisherView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
        publisherView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
        publisherView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
        
        [_selfVideoView addSubview:publisherView];
        
        [_selfVideoView setNeedsDisplay];
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
    OpenTokAgent* agent = _webRTCModule.openTokAgent;
    OTVideoView* subscriberView = agent.subscriberView;
    if (nil != subscriberView)
    {
        CGRect superFrame = _parterVideoView.frame;
        CGRect selfFrame = CGRectMake(0, 0, superFrame.size.width, superFrame.size.height);
        
        [subscriberView setFrame:selfFrame];
        [_parterVideoView addSubview:subscriberView];
        
        [_parterVideoView bringSubviewToFront:_selfVideoView];
        
        [_parterVideoView setNeedsDisplay];
    }
}

-(void) subscriberDidFailWithError
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Failed", nil);
}

@end
