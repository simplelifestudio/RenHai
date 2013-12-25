//
//  ConnectViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-10-5.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ConnectViewController_iPhone.h"

#import "CBUIUtils.h"
#import "CBDateUtils.h"
#import "CBNetworkUtils.h"
#import "UINavigationController+CBNavigationControllerExtends.h"
#import "UIViewController+CWPopup.h"
#import "UIViewController+CBUIViewControlerExtends.h"

#import "GUIModule.h"
#import "CommunicationModule.h"
#import "UserDataModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#define DELAY_POP 0.5f
#define DELAY_STATUS_UPDATE 0.15f
#define ANIMATION_POP 0.5f
#define ANIMATION_DISMISS 0.5f

typedef enum
{
    ConnectStatus_Ready = 0,
    ConnectStatus_NeedWiFi,
    ConnectStatus_ProxySyncing,
    ConnectStatus_ProxySyncedNormal,
    ConnectStatus_ProxySyncedMaintenance_BeforePeriod,
    ConnectStatus_ProxySyncedMaintenance_InPeriod,
    ConnectStatus_ProxySyncFailed,
    ConnectStatus_Connecting,
    ConnectStatus_Connected,
    ConnectStatus_ConnectFailed,
    ConnectStatus_AppDataSyncing,
    ConnectStatus_AppDataSynced,
    ConnectStatus_AppDataSyncFailed,
    ConnectStatus_AppDataUpdating,
    ConnectStatus_AppDataUpdated,
    ConnectStatus_AppDataUpdateFailed,
    ConnectStatus_ServerDataSyncing,
    ConnectStatus_ServerDataSynced,
    ConnectStatus_ServerDataSyncFailed,
    ConnectStatus_Cancel
}
ConnectStatus;

@interface ConnectViewController_iPhone ()
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    UserDataModule* _userDataModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    NSTimer* _timer;
    NSUInteger _count;
    NSInvocationOperation* _timerStartOperation;
    NSInvocationOperation* _timerStopOperation;
 
    volatile BOOL _isOperationQueueCancelled;
    
    NSOperationQueue* _operationQueue;
    volatile BOOL _isProxyDataSyncSuccess;
    volatile BOOL _isConnectServerSuccess;
    volatile BOOL _isAppDataSyncSuccess;
    volatile BOOL _isAppDataUpdateNecessary;
    volatile BOOL _isAppDataUpdateSuccess;
    volatile BOOL _isServerDataSyncSuccess;
    NSInvocationOperation* _proxyDataSyncOperation;
    NSInvocationOperation* _connectOperaton;
    NSInvocationOperation* _appDataSyncOperation;
    NSInvocationOperation* _serverSyncOperation;
    NSInvocationOperation* _appDataUpdateOperation;
    
    NSMutableString* _consoleInfo;
}

@end

@implementation ConnectViewController_iPhone

@synthesize infoLabel = _infoLabel;
@synthesize infoTextView = _infoTextView;
@synthesize countLabel = _countLabel;
@synthesize actionButton = _actionButton;

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
    
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self _setupInstance];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _registerNotifications];
    
    [self _fireOperationQueue];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [self _unregisterNotifications];
    
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void) popConnectView:(UIViewController*) presentingViewController animated:(BOOL) animated
{
//    WelcomeViewController_iPhone* welcomeVC = _guiModule.welcomeViewController;
//    if ([welcomeVC isVisible])
//    {
//        [welcomeVC dismissWelcome];
//    }
    
//    [NSThread sleepForTimeInterval:DELAY_POP];
    
    if (![self isVisible])
    {
        [_statusModule recordAppMessage:AppMessageIdentifier_Disconnect];
        
        UIViewController* rootVC = [CBUIUtils getRootController];
//        presentingViewController = (nil != presentingViewController) ? presentingViewController : rootVC;
        
        __block CALayer* keyLayer = [UIApplication sharedApplication].keyWindow.layer;
        
        if (NO)
        {
            CATransition* transition = [CATransition animation];
            transition.duration = ANIMATION_POP;
            transition.type = kCATransitionMoveIn;
            transition.subtype = kCATransitionFromTop;
            [keyLayer addAnimation:transition forKey:kCATransition];
            transition.removedOnCompletion = YES;
        }

        [rootVC presentViewController:self animated:animated completion:^(){

        }];
    }
}

- (void) dismissConnectView:(BOOL) animated
{
    [CBAppUtils asyncProcessInMainThread:^(){
        if ([self isVisible])
        {
            UIViewController* rootVC = [CBUIUtils getRootController];
            MainViewController_iPhone* mainVC = _guiModule.mainViewController;
            
            [self _clockCancel];
            
            __block CALayer* keyLayer = [UIApplication sharedApplication].keyWindow.layer;
            
            if (NO)
            {
                CATransition* transition = [CATransition animation];
                transition.duration = ANIMATION_DISMISS;
                transition.type = kCATransitionReveal;
                transition.subtype = kCATransitionFromBottom;
                [keyLayer addAnimation:transition forKey:kCATransition];
                transition.removedOnCompletion = YES;
            }
            
            [self dismissViewControllerAnimated:animated completion:^(){
                if (rootVC == mainVC)
                {
                    mainVC.view.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
                    [mainVC resignPresentationModeEntirely:YES animated:NO completion:nil];
                }
            }];
            
            [mainVC switchToMainScene];
            
            [self _resetInstance];
        }
    }];
}

#pragma mark - IBAction Methods

- (IBAction)didPressActionButton:(id)sender
{
    [_guiModule playSound:SOUNDID_CONNECT_RETRY];
    
    [self _fireOperationQueue];
}

#pragma mark - Private Methods

- (void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];

    _statusbarLabel.backgroundColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
    
    _infoLabel.backgroundColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
    
    _isOperationQueueCancelled = NO;
    
    [self _resetInstance];

    [self _setupActionButtons];
    
    [self _formatFlatUI];
}

- (void) _formatFlatUI
{
    _countLabel.textColor = FLATUI_COLOR_TEXT_LOG;
}

- (void) _setupActionButtons
{
    _actionButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    [_actionButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_actionButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
}

- (void)_resetInstance
{
    _count = 0;
    [self _updateCountLabel];
    
    [self _updateUIWithConnectStatus:ConnectStatus_Ready];
}

- (void) _clockStart
{
    [self _clockCancel];
    
    NSTimeInterval interval = 1.0;
    _timer = [NSTimer timerWithTimeInterval:interval target:self selector:@selector(_clockClick) userInfo:nil repeats:YES];
    NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
    [currentRunLoop addTimer:_timer forMode:NSDefaultRunLoopMode];
    [_timer fire];
}

- (void) _clockClick
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
}

- (void) _updateCountLabel
{
    [CBAppUtils asyncProcessInMainThread:^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
    }];
}

- (void) _updateUIWithConnectStatus:(ConnectStatus) status
{
    [NSThread sleepForTimeInterval:DELAY_STATUS_UPDATE];
    
    [CBAppUtils asyncProcessInMainThread:^(){
        NSString* infoText = nil;
        NSString* infoDetailText = nil;
        NSString* actionButtonTitle = NSLocalizedString(@"Connect_Action_Retry", nil);
        BOOL isActionButtonHide = YES;
        BOOL isTextClear = NO;
        
        switch (status)
        {
            case ConnectStatus_Ready:
            {
                infoText = NSLocalizedString(@"Connect_Ready", nil);
                infoDetailText = NSLocalizedString(@"Connect_Ready_Detail", nil);
                isActionButtonHide = YES;
                isTextClear = YES;
                
                _isOperationQueueCancelled = NO;
                
                break;
            }
            case ConnectStatus_NeedWiFi:
            {
                infoText = NSLocalizedString(@"Connect_NeedWiFi", nil);
                infoDetailText = NSLocalizedString(@"Connect_NeedWiFi_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_ProxySyncing:
            {
                infoText = NSLocalizedString(@"Connect_Checking", nil);
                infoDetailText = NSLocalizedString(@"Connect_Checking_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_ProxySyncedNormal:
            {
                infoText = NSLocalizedString(@"Connect_CheckedNormal", nil);
                infoDetailText = NSLocalizedString(@"Connect_CheckedNormal_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_ProxySyncedMaintenance_BeforePeriod:
            {
                infoText = NSLocalizedString(@"Connect_CheckedMaintenance", nil);
//                infoDetailText = NSLocalizedString(@"Connect_CheckedMaintenance_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_ProxySyncedMaintenance_InPeriod:
            {
                infoText = NSLocalizedString(@"Connect_CheckedMaintenance", nil);
                //                infoDetailText = NSLocalizedString(@"Connect_CheckedMaintenance_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_ProxySyncFailed:
            {
                infoText = NSLocalizedString(@"Connect_CheckFailed", nil);
                infoDetailText = NSLocalizedString(@"Connect_CheckFailed_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_Connecting:
            {
                infoText = NSLocalizedString(@"Connect_Connecting", nil);
                infoDetailText = NSLocalizedString(@"Connect_Connecting_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_Connected:
            {
                infoText = NSLocalizedString(@"Connect_Connected", nil);
                infoDetailText = NSLocalizedString(@"Connect_Connected_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_ConnectFailed:
            {
                infoText = NSLocalizedString(@"Connect_ConnectFailed", nil);
                infoDetailText = NSLocalizedString(@"Connect_ConnectFailed_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_AppDataSyncing:
            {
                infoText = NSLocalizedString(@"Connect_AppDataSyncing", nil);
                infoDetailText = NSLocalizedString(@"Connect_AppDataSyncing_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_AppDataSynced:
            {
                infoText = NSLocalizedString(@"Connect_AppDataSynced", nil);
                infoDetailText = NSLocalizedString(@"Connect_AppDataSynced_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_AppDataSyncFailed:
            {
                infoText = NSLocalizedString(@"Connect_AppDataSyncFailed", nil);
                infoDetailText = NSLocalizedString(@"Connect_AppDataSyncFailed_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_AppDataUpdating:
            {
                infoText = NSLocalizedString(@"Connect_AppDataUpdating", nil);
                infoDetailText = NSLocalizedString(@"Connect_AppDataUpdating_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_AppDataUpdated:
            {
                infoText = NSLocalizedString(@"Connect_AppDataUpdated", nil);
                infoDetailText = NSLocalizedString(@"Connect_AppDataUpdated_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_AppDataUpdateFailed:
            {
                infoText = NSLocalizedString(@"Connect_AppDataUpdateFailed", nil);
                infoDetailText = NSLocalizedString(@"Connect_AppDataUpdateFailed_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_ServerDataSyncing:
            {
                infoText = NSLocalizedString(@"Connect_ServerDataSyncing", nil);
                infoDetailText = NSLocalizedString(@"Connect_ServerDataSyncing_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_ServerDataSynced:
            {
                infoText = NSLocalizedString(@"Connect_ServerDataSynced", nil);
                infoDetailText = NSLocalizedString(@"Connect_ServerDataSynced_Detail", nil);
                isActionButtonHide = YES;
                break;
            }
            case ConnectStatus_ServerDataSyncFailed:
            {
                infoText = NSLocalizedString(@"Connect_ServerDataSyncFailed", nil);
                infoDetailText = NSLocalizedString(@"Connect_ServerDataSyncFailed_Detail", nil);
                isActionButtonHide = NO;
                break;
            }
            case ConnectStatus_Cancel:
            {
                infoText = NSLocalizedString(@"Connect_Cancel", nil);
                infoDetailText = NSLocalizedString(@"Connect_Cancel_Detail", nil);
                isActionButtonHide = NO;
                
                [self _timerStop];
                
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

        [_actionButton setTitle:actionButtonTitle forState:UIControlStateNormal];
        _actionButton.hidden = isActionButtonHide;
    }];
}

- (void)_timerStart
{
    [CBAppUtils asyncProcessInMainThread:^(){
        [self _clockStart];
    }];
}

- (void)_timerStop
{
    [self _clockCancel];
    
    [NSThread sleepForTimeInterval:DELAY_STATUS_UPDATE];
    
    if (_isServerDataSyncSuccess)
    {
        [self dismissConnectView:YES];
    }
    else
    {
        [_commModule disconnectWebSocket];
    }
}

- (void)_remoteProxyDataSync
{
    [self _resetInstance];
    
    if (![CBNetworkUtils isWiFiEnabled])
    {
        [self _updateUIWithConnectStatus:ConnectStatus_NeedWiFi];
        _isProxyDataSyncSuccess = NO;
        return;
    }
    
    [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncing];    

    RHMessage* requestMessage = [RHMessage newProxyDataSyncRequest];
    [_commModule proxyDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* proxyDic){
            
            RHProxy* proxy = _userDataModule.proxy;
            
            @try
            {
                [proxy fromJSONObject:proxyDic];
                
                RHStatus* status = proxy.status;
                
                if(nil == status)
                {
                    [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncFailed];
                    if (nil != proxy.broadcast)
                    {
                        [NSThread sleepForTimeInterval:DELAY_STATUS_UPDATE * 2];
                        [self _updateInfoTextView:proxy.broadcast];    
                    }
                    
                    _isProxyDataSyncSuccess = NO;
                    return;
                }

                switch (status.serviceStatus)
                {
                    case ServerServiceStatus_Maintenance:
                    {
                        RHStatusPeriod* period = status.statusPeriod;
                       
                        NSString* localBeginTimeStr = period.localBeginTimeString;
                        NSString* localEndTimeStr = period.localEndTimeString;
                       
                        NSString* periodStr = [NSString stringWithFormat:NSLocalizedString(@"Connect_CheckedMaintenance_Detail", nil), localBeginTimeStr, localEndTimeStr];
                        [self _updateInfoTextView:periodStr];

                        _isProxyDataSyncSuccess = ![period isInPeriod];

                        if (_isProxyDataSyncSuccess)
                        {
                           [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncedMaintenance_BeforePeriod];
                        }
                        else
                        {
                           [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncedMaintenance_InPeriod];
                        }

                        break;
                    }
                    case ServerServiceStatus_Normal:
                    {
                        [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncedNormal];
                        _isProxyDataSyncSuccess = YES;
                        break;
                    }
                    default:
                    {
                        [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncFailed];
                        _isProxyDataSyncSuccess = NO;
                        break;
                    }
                }
            }
            @catch (NSException *exception)
            {
               DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
               
               [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncFailed];
               _isProxyDataSyncSuccess = NO;
            }
            @finally
            {
               
            }
        }
        failureCompletionBlock:^(){
           [self _updateUIWithConnectStatus:ConnectStatus_ProxySyncFailed];
            _isProxyDataSyncSuccess = NO;
        }
        afterCompletionBlock:^(){
            if (_isOperationQueueCancelled)
            {
                [self _updateUIWithConnectStatus:ConnectStatus_Cancel];
            }
        }
     ];
}

- (void)_remoteConnectServer
{
    if (!_isProxyDataSyncSuccess)
    {
        return;
    }
    
    [self _updateUIWithConnectStatus:ConnectStatus_Connecting];
    
    _isConnectServerSuccess = [_commModule connectWebSocket];
    if (_isConnectServerSuccess)
    {
        [_statusModule recordAppMessage:AppMessageIdentifier_Connect];
        
        [self _updateUIWithConnectStatus:ConnectStatus_Connected];
    }
    else
    {
        [self _updateUIWithConnectStatus:ConnectStatus_ConnectFailed];
    }
    
    if (_isOperationQueueCancelled)
    {
        [self _updateUIWithConnectStatus:ConnectStatus_Cancel];
    }
}

- (void)_remoteAppDataSync
{
    if (!_isConnectServerSuccess)
    {
        return;
    }
    
    [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncing];
    
    RHDevice* device = _userDataModule.device;
    RHMessage* requestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:device info:nil];
    
    [_commModule appDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* deviceDic){
            RHDevice* device = _userDataModule.device;
            @try
            {
                [device fromJSONObject:deviceDic];
                
                [_userDataModule saveUserData];
                
                [self _updateUIWithConnectStatus:ConnectStatus_AppDataSynced];
                
                _isAppDataSyncSuccess = YES;
                
                RHProfile* profile = device.profile;
                RHInterestCard* interestCard = profile.interestCard;
                _isAppDataUpdateNecessary = (0 == interestCard.labelList.count) ? YES : NO;
                
                [_statusModule recordAppMessage:AppMessageIdentifier_AppDataSync];
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
                
                [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncFailed];
                
                _isAppDataSyncSuccess = NO;
            }
            @finally
            {
                
            }
        }
        failureCompletionBlock:^(){
            [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncFailed];
            [_statusModule recordAppMessage:AppMessageIdentifier_Disconnect];
        }
        afterCompletionBlock:^(){
            if (_isOperationQueueCancelled)
            {
                [self _updateUIWithConnectStatus:ConnectStatus_Cancel];
            }
        }
     ];
}

- (void)_remoteAppDataUpdate
{
    if (!_isAppDataSyncSuccess)
    {
        return;
    }
    
    if (!_isAppDataUpdateNecessary)
    {
        _isAppDataUpdateSuccess = YES;
        return;
    }
    
    [self _updateUIWithConnectStatus:ConnectStatus_AppDataUpdating];
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    [interestCard addLabel:NSLocalizedString(@"Connect_DefaultFirstImpressLabel", nil)];
    
    RHMessage* requestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_InterestCardSync device:device info:nil];
    
    [_commModule appDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* deviceDic){
            deviceDic = [deviceDic objectForKey:MESSAGE_KEY_DEVICE];
            NSDictionary* profileDic = [deviceDic objectForKey:MESSAGE_KEY_PROFILE];
            NSDictionary* interestCardDic = [profileDic objectForKey:MESSAGE_KEY_INTERESTCARD];
            @try
            {
                [interestCard fromJSONObject:interestCardDic];

                [_userDataModule saveUserData];

                [self _updateUIWithConnectStatus:ConnectStatus_AppDataUpdated];

                _isAppDataUpdateSuccess = YES;
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);

                [self _updateUIWithConnectStatus:ConnectStatus_AppDataUpdateFailed];

                _isAppDataUpdateSuccess = NO;
                
                [_statusModule recordAppMessage:AppMessageIdentifier_Disconnect];
            }
            @finally
            {
             
            }
        }
        failureCompletionBlock:^(){
         [self _updateUIWithConnectStatus:ConnectStatus_AppDataUpdateFailed];
         [_statusModule recordAppMessage:AppMessageIdentifier_Disconnect];
        }
        afterCompletionBlock:^(){
           if (_isOperationQueueCancelled)
           {
               [self _updateUIWithConnectStatus:ConnectStatus_Cancel];
           }
        }
     ];
}

- (void)_remoteServerDataSync
{
    if (!_isAppDataUpdateSuccess)
    {
        return;
    }
    
    [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncing];
    
    RHDevice* device = _userDataModule.device;
    RHMessage* requestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:device info:nil];
    
    [_commModule serverDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* serverDic){
            RHServerData* server = _userDataModule.server;
            @try
            {
                [server fromJSONObject:serverDic];
                
                [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSynced];
                
                _isServerDataSyncSuccess = YES;
                
                [_statusModule recordAppMessage:AppMessageIdentifier_ServerDataSync];
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
                
                [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncFailed];
                
                _isServerDataSyncSuccess = NO;
            }
            @finally
            {
                
            }
        }
        failureCompletionBlock:^(){
            [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncFailed];
            
            [_statusModule recordAppMessage:AppMessageIdentifier_Disconnect];            
        }
        afterCompletionBlock:^(){
            if (_isOperationQueueCancelled)
            {
                [self _updateUIWithConnectStatus:ConnectStatus_Cancel];
            }
        }
     ];
}

- (void)_fireOperationQueue
{
    [self _clearInfoTextView];
    
    _isProxyDataSyncSuccess = NO;
    _isConnectServerSuccess = NO;
    _isAppDataSyncSuccess = NO;
    _isAppDataUpdateNecessary = NO;
    _isAppDataSyncSuccess = NO;
    _isAppDataUpdateSuccess = NO;
    _isServerDataSyncSuccess = NO;
    
    _timerStartOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_timerStart) object:nil];
    _proxyDataSyncOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_remoteProxyDataSync) object:nil];
    _connectOperaton = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_remoteConnectServer) object:nil];
    _appDataSyncOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_remoteAppDataSync) object:nil];
    _appDataUpdateOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_remoteAppDataUpdate) object:nil];
    _serverSyncOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_remoteServerDataSync) object:nil];
    _timerStopOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_timerStop) object:nil];
    
    [_timerStopOperation addDependency:_serverSyncOperation];
    [_serverSyncOperation addDependency:_appDataUpdateOperation];
    [_appDataUpdateOperation addDependency:_appDataSyncOperation];
    [_appDataSyncOperation addDependency:_connectOperaton];
    [_connectOperaton addDependency:_proxyDataSyncOperation];
    [_proxyDataSyncOperation addDependency:_timerStartOperation];
    
    _operationQueue = [[NSOperationQueue alloc] init];
    _operationQueue.maxConcurrentOperationCount = 1;
    [_operationQueue addOperation:_timerStopOperation];
    [_operationQueue addOperation:_serverSyncOperation];
    [_operationQueue addOperation:_appDataUpdateOperation];
    [_operationQueue addOperation:_appDataSyncOperation];
    [_operationQueue addOperation:_connectOperaton];
    [_operationQueue addOperation:_proxyDataSyncOperation];
    [_operationQueue addOperation:_timerStartOperation];
}

- (void) _dismissOperationQueue
{
    _isOperationQueueCancelled = YES;
    
    if (nil != _operationQueue)
    {
        [_operationQueue cancelAllOperations];
    }
}

- (void) _updateInfoTextView:(NSString*) info
{
    [CBAppUtils asyncProcessInMainThread:^(){
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
        
        if (nil != info && 0 < info.length)
        {
            [_consoleInfo appendString:info];
            [_consoleInfo appendString:@"\n"];
            
            [_infoTextView setText:_consoleInfo];
        }
    }];
}

- (void) _clearInfoTextView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _consoleInfo = [NSMutableString string];
        [self _updateInfoTextView:nil];
    }];
}

-(void)_registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_dismissOperationQueue)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
}

-(void)_unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - ScreenOrientation Methods

- (BOOL) shouldAutorotate
{
	return NO;
}

- (NSUInteger)supportedInterfaceOrientations
{
	return UIInterfaceOrientationMaskPortrait;
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation
{
	return UIInterfaceOrientationPortrait;
}

@end
