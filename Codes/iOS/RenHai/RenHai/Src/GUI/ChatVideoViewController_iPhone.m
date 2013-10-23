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

@interface ChatVideoViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    volatile BOOL _selfEndChatFlag;
    volatile BOOL _partnerEndChatFlag;
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
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
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
    
    _endChatButton.hidden = NO;
}

-(void) pageWillLoad
{
    
}

-(void) pageWillUnload
{
    
}

-(void) onOthersideEndChat
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_VideoClosed", nil);
    }];
}

-(void) onOthersideLost
{
    [CBAppUtils asyncProcessInMainThread:^(){
        _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_VideoClosed", nil);
    }];
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_VideoOpened", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatVideo_PartnerStatus_VideoOpened", nil);
    
    [_endChatButton setTitle:NSLocalizedString(@"ChatVideo_Action_End", nil) forState:UIControlStateNormal];
}

-(void) _moveToChatImpressView
{
    [CBAppUtils asyncProcessInMainThread:^(){
    
        [_appDataModule updateAppBusinessStatus:AppBusinessStatus_ChatEndCompleleted];
        
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatImpress];
    
    }];
}

-(void) _endChat
{
    [CBAppUtils asyncProcessInMainThread:^(){
    
        _selfStatusLabel.text = NSLocalizedString(@"ChatVideo_SelfStatus_VideoClosed", nil);
        _endChatButton.hidden = YES;
        
    }];
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
    
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
                    
                    [self _moveToChatImpressView];
                }
                else
                {
                    
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

#pragma mark - IBActions

- (IBAction)didPressEndChatButton:(id)sender
{
    [self _endChat];
}

@end
