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

#define DELAY_MATCHSTART 3.0f

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
    
//    [self _clockStart];
//    
//    [self _checkIsSessionAlreadyBound];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
//    [self _clockCancel];
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
}

-(void) pageWillUnload
{
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
    _timer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(_clockTick) userInfo:nil repeats:YES];
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
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_LeavePool device:device info:nil];
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
                    _leavePoolFlag = YES;
                
                    [_statusModule recordAppMessage:AppMessageIdentifier_LeavePool];
                }
                else
                {
                    _leavePoolFlag = NO;
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
        
        [self _finishLeavingPool];
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

- (void) _requestMatchStart
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
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_MatchStart device:device info:nil];
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
                    _matchStartFlag = YES;
                    
                    [_statusModule recordAppMessage:AppMessageIdentifier_MatchStart];
                }
                else
                {
                    _matchStartFlag = NO;
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

@end
