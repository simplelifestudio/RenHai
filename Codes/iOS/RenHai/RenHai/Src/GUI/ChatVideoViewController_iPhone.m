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

@interface ChatVideoViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
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
}

-(void) pageWillUnload
{
    
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
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_EndChat device:device info:nil];
        
        RHMessage* responseMessage = [_commModule sendMessage:businessSessionRequestMessage];
        if (responseMessage.messageId == MessageId_BusinessSessionResponse)
        {
            NSDictionary* messageBody = responseMessage.body;
            NSDictionary* businessSessionDic = messageBody;
            
            @try
            {
                NSNumber* oOperationValue = [businessSessionDic objectForKey:MESSAGE_KEY_OPERATIONVALUE];
                BusinessSessionOperationValue operationValue = oOperationValue.intValue;
                
                if (operationValue == BusinessSessionOperationValue_Success)
                {
                    _selfEndChatFlag = YES;
                    [_statusModule recordAppMessage:AppMessageIdentifier_EndChat];
                    
                    [self _moveToChatAssessView];
                }
                else
                {
                    _selfEndChatFlag = NO;
                }
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        else if (responseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
        {
            
        }
        else
        {
            
        }
    
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

#pragma mark - IBActions

- (IBAction)didPressEndChatButton:(id)sender
{
    [self _endChat];
}

@end
