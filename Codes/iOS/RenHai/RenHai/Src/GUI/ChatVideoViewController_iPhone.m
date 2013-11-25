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

#define INTERVAL_ALOHA 60

#define BORDERWIDTH_VIDEOVIEW 2.0f;
#define CORNERRADIUS_VIDEOVIEW 2.0f;
#define BORDERCOLOR_VIDEOVIEW MAJOR_COLOR_MID

@interface ChatVideoViewController_iPhone () <OpenTokDelegate>
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
    
    volatile BOOL _isDeciding;
    
    BOOL _isInitialized;
}

@end

@implementation ChatVideoViewController_iPhone

@synthesize selfStatusLabel = _selfStatusLabel;
@synthesize partnerStatusLabel = _partnerStatusLabel;

@synthesize selfVideoView = _selfVideoView;
@synthesize parterVideoView = _parterVideoView;

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

#pragma mark - ChatWizardPage

-(void) resetPage
{
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Prepairing", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Prepairing", nil);
    
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
    
    [self _connectWebRTC];
}

-(void) pageWillUnload
{
    [self _unregisterNotifications];
    
    [self _deactivateAlohaTimer];
    
    [self _disconnectWebRTC];
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
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Prepairing", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Prepairing", nil);
    
    _selfVideoView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
    _selfVideoView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
    _selfVideoView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
    
    _parterVideoView.layer.borderColor = BORDERCOLOR_VIDEOVIEW.CGColor;
    _parterVideoView.layer.borderWidth = BORDERWIDTH_VIDEOVIEW;
    _parterVideoView.layer.cornerRadius = CORNERRADIUS_VIDEOVIEW;
    
    [_endChatButton setTitle:NSLocalizedString(@"ChatVideo_Action_End", nil) forState:UIControlStateNormal];
}

-(void) _moveToChatAssessView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatAssess];
    }];
}

-(void) _remoteEndChat
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_Disconnected", nil);
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

#pragma mark - OpenTokDelegate

-(void) sessionDidConnect
{

}

-(void) sessionDidDisconnect
{

}

-(void) sessionDidFailWithError
{

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
        [_selfVideoView addSubview:publisherView];
        
        UIView* maskView = [[UIView alloc] initWithFrame:selfFrame];
        maskView.alpha = 0.1;
        [_selfVideoView addSubview:maskView];
        
        [_selfVideoView setNeedsDisplay];
    }
}

-(void) sessionDidReceivePartnerStream
{

}

-(void) sessionDidDropPartnerStream;
{

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
        
        UIView* maskView = [[UIView alloc] initWithFrame:selfFrame];
        maskView.alpha = 0.1;
        [_parterVideoView addSubview:maskView];
        
        [_parterVideoView setNeedsDisplay];
    }
}

-(void) subscriberDidFailWithError
{
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_Failed", nil);
}

@end
