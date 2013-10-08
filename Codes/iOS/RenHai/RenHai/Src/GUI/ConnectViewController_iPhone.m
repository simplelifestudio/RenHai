//
//  ConnectViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-10-5.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ConnectViewController_iPhone.h"

#import "CBUIUtils.h"

#import "GUIModule.h"
#import "CommunicationModule.h"
#import "UserDataModule.h"

#define DELAY 1.0f
#define ANIMATION_POP 0.35f
#define ANIMATION_DISMISS 0.35

typedef enum
{
    ConnectStatus_Ready = 0,
    ConnectStatus_Connecting,
    ConnectStatus_Connected,
    ConnectStatus_ConnectFailed,
    ConnectStatus_AppDataSyncing,
    ConnectStatus_AppDataSynced,
    ConnectStatus_AppDataSyncFailed,
    ConnectStatus_ServerDataSyncing,
    ConnectStatus_ServerDataSynced,
    ConnectStatus_ServerDataSyncFailed
}
ConnectStatus;

@interface ConnectViewController_iPhone ()
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    UserDataModule* _userDataModule;
    
    NSTimer* _timer;
    NSUInteger _count;
    NSInvocationOperation* _timerStartOperation;
    NSInvocationOperation* _timerStopOperation;
    
    BOOL _isViewControllerVisible;
 
    NSOperationQueue* _operationQueue;
    volatile BOOL _isConnectServerSuccess;
    volatile BOOL _isAppDataSyncSuccess;
    volatile BOOL _isServerDataSyncSuccess;
    NSInvocationOperation* _connectOperaton;
    NSInvocationOperation* _appSyncOperation;
    NSInvocationOperation* _serverSyncOperation;
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
    _isViewControllerVisible = YES;
    
    [super viewDidAppear:animated];
    
    [self _fireOperationQueue];
}

- (void)viewDidDisappear:(BOOL)animated
{
    _isViewControllerVisible = NO;
    
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void) popConnectView:(UIViewController*) presentingViewController animated:(BOOL) animated
{
    if (!_isViewControllerVisible)
    {
        presentingViewController = (nil != presentingViewController) ? presentingViewController : [CBUIUtils getKeyWindow].rootViewController;
        
        __block CALayer* keyLayer = [UIApplication sharedApplication].keyWindow.layer;
        
        if (animated)
        {
            CATransition* transition = [CATransition animation];
            transition.duration = ANIMATION_POP;
            transition.type = kCATransitionMoveIn;
            transition.subtype = kCATransitionFromTop;
            [keyLayer addAnimation:transition forKey:kCATransition];
            transition.removedOnCompletion = YES;
        }
        
        [presentingViewController presentViewController:self animated:NO completion:^(){
        }];
    }
}

- (void) dismissConnectView:(BOOL) animated
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        if (_isViewControllerVisible)
        {
            [self _clockCancel];
            
            __block CALayer* keyLayer = [UIApplication sharedApplication].keyWindow.layer;
            
            if (animated)
            {
                CATransition* transition = [CATransition animation];
                transition.duration = ANIMATION_DISMISS;
                transition.type = kCATransitionReveal;
                transition.subtype = kCATransitionFromBottom;
                [keyLayer addAnimation:transition forKey:kCATransition];
                transition.removedOnCompletion = YES;
            }
            
            [self dismissViewControllerAnimated:NO completion:^(){
            }];
            
            RHNavigationController* navigationVC = _guiModule.navigationController;
            HomeViewController_iPhone* homeVC = _guiModule.homeViewController;
            if (navigationVC.topViewController != homeVC)
            {
                [_guiModule.navigationController pushViewController:_guiModule.homeViewController animated:NO];
            }
        }
    });
}

#pragma mark - IBAction Methods

- (IBAction)didPressActionButton:(id)sender
{
    [self _fireOperationQueue];
}

#pragma mark - Private Methods

- (void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];

    _isViewControllerVisible = NO;
    
    [self _resetInstance];
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
    _timer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(_clockTick) userInfo:nil repeats:YES];
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
}

- (void) _updateCountLabel
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
    });
}

- (void) _updateInfoTextView:(NSString*) text
{
    [self _updateInfoTextView:text clear:YES];
}

- (void) _updateInfoTextView:(NSString*) text clear:(BOOL) clear
{
    if (clear)
    {
        _infoTextView.text = text;
    }
    else
    {
        NSMutableString* mutableText = [NSMutableString stringWithString:_infoTextView.text];
        [mutableText appendString:text];
        _infoTextView.text = mutableText;
    }
}

- (void) _updateUIWithConnectStatus:(ConnectStatus) status
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        
        NSString* infoText = nil;
        NSString* infoDetailText = nil;
        NSString* actionButtonTitle = NSLocalizedString(@"Connect_Retry", nil);
        BOOL isActionButtonHide = YES;
        
        switch (status)
        {
            case ConnectStatus_Ready:
            {
                infoText = NSLocalizedString(@"Connect_Ready", nil);
                infoDetailText = NSLocalizedString(@"Connect_Ready_Detail", nil);
                isActionButtonHide = YES;
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
                isActionButtonHide = YES;
                break;
            }
            default:
            {
                break;
            }
        }

        _infoLabel.text = infoText;
        [self _updateInfoTextView:infoDetailText];
        _actionButton.titleLabel.text = actionButtonTitle;
        _actionButton.hidden = isActionButtonHide;
    });
}

- (void)_timerStart
{
    dispatch_sync(dispatch_get_main_queue(), ^(){
        [self _clockStart];
    });
}

- (void)_timerStop
{
    [self _clockCancel];
    
    sleep(DELAY);
    
    if (_isServerDataSyncSuccess)
    {
        [self dismissConnectView:YES];
    }
}

- (void)_connectServer
{
    [self _resetInstance];
    
    sleep(DELAY);
    
    [self _updateUIWithConnectStatus:ConnectStatus_Connecting];
    
    BOOL connected = [_commModule connectWebSocket];
    _isConnectServerSuccess = connected;
    if (connected)
    {
        [self _updateUIWithConnectStatus:ConnectStatus_Connected];
        sleep(DELAY);
    }
    else
    {
        [self _updateUIWithConnectStatus:ConnectStatus_ConnectFailed];
        sleep(DELAY);
    }
}

- (void)_appDataSync
{
    if (!_isConnectServerSuccess)
    {
        return;
    }

    [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncing];
    
    RHDevice* device = _userDataModule.device;
    RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:device info:nil];
    RHMessage* appDataSyncResponseMessage = [_commModule sendMessage:appDataSyncRequestMessage];
    if (appDataSyncResponseMessage.messageId == MessageId_AppDataSyncResponse)
    {
        NSDictionary* messageBody = appDataSyncResponseMessage.body;
        NSDictionary* deviceDic = [messageBody objectForKey:MESSAGE_KEY_DATAQUERY];
        
        RHDevice* device = _userDataModule.device;
        [device fromJSONObject:deviceDic];
        
        [_userDataModule saveUserData];
        
        [self _updateUIWithConnectStatus:ConnectStatus_AppDataSynced];
        
        _isAppDataSyncSuccess = YES;
    }
    else if (appDataSyncResponseMessage.messageId == MessageId_ServerErrorResponse)
    {
        [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncFailed];
    }
    else if (appDataSyncResponseMessage.messageId == MessageId_ServerTimeoutResponse)
    {
        [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncFailed];
    }
    else
    {
        [self _updateUIWithConnectStatus:ConnectStatus_AppDataSyncFailed];
    }
}

- (void)_serverDataSync
{
    if (!_isAppDataSyncSuccess)
    {
        return;
    }
    
    [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncing];
    
    RHDevice* device = _userDataModule.device;
    RHMessage* serverDataSyncRequestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:device info:nil];
    RHMessage* serverDataSyncResponseMessage = [_commModule sendMessage:serverDataSyncRequestMessage];
    if (serverDataSyncResponseMessage.messageId == MessageId_ServerDataSyncResponse)
    {
        NSDictionary* messageBody = serverDataSyncResponseMessage.body;
        NSDictionary* serverDic = messageBody;
        
        RHServer* server = _userDataModule.server;
        [server fromJSONObject:serverDic];
        
        [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSynced];
        
        _isServerDataSyncSuccess = YES;
    }
    else if (serverDataSyncResponseMessage.messageId == MessageId_ServerErrorResponse)
    {
        [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncFailed];
    }
    else if (serverDataSyncResponseMessage.messageId == MessageId_ServerTimeoutResponse)
    {
        [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncFailed];
    }
    else
    {
        [self _updateUIWithConnectStatus:ConnectStatus_ServerDataSyncFailed];
    }
}

- (void)_fireOperationQueue
{
    _isConnectServerSuccess = NO;
    _isAppDataSyncSuccess = NO;
    _isServerDataSyncSuccess = NO;
    
    _timerStartOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_timerStart) object:nil];
    _connectOperaton = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_connectServer) object:nil];
    _appSyncOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_appDataSync) object:nil];
    _serverSyncOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_serverDataSync) object:nil];
    _timerStopOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(_timerStop) object:nil];
    
    [_timerStopOperation addDependency:_serverSyncOperation];
    [_serverSyncOperation addDependency:_appSyncOperation];
    [_appSyncOperation addDependency:_connectOperaton];
    [_connectOperaton addDependency:_timerStartOperation];
    
    _operationQueue = [[NSOperationQueue alloc] init];
    _operationQueue.maxConcurrentOperationCount = 1;
    [_operationQueue addOperation:_timerStopOperation];
    [_operationQueue addOperation:_serverSyncOperation];
    [_operationQueue addOperation:_appSyncOperation];
    [_operationQueue addOperation:_connectOperaton];
    [_operationQueue addOperation:_timerStartOperation];
}

@end
