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

#define DELAY CIRCLE_ANIMATION_DISPLAY

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
    
    NSTimer* _timer;
    NSUInteger _count;
    
    NSMutableString* _consoleInfo;
    
    BOOL _leavePoolFlag;
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
    [self.navigationController setNavigationBarHidden:YES];
    
    [self _resetInstance];
    
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [self _clockStart];
    
    [super viewDidAppear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    [self _clockCancel];
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

#pragma mark - Private Methods

- (void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    
    _leavePoolFlag = NO;
}

- (void)_resetInstance
{
    [self _updateUIWithChatWaitStatus:ChatWaitStatus_WaitForMatch];
    
    _count = 0;
    [self _updateCountLabel];
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

- (void) _startLeavingPool
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
    
        RHDevice* device = _userDataModule.device;
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:BusinessType_Interest operationType:BusinessSessionRequestType_LeavePool device:device info:nil];
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
        
        [self _finishLeavingPool];
    });

}

- (void) _finishLeavingPool
{
    dispatch_async(dispatch_get_main_queue(), ^(){

        if (_leavePoolFlag)
        {
            [NSThread sleepForTimeInterval:DELAY];            
            
            [self _clockCancel];
            
            [_guiModule.mainViewController switchToMainScene];
            
            [self _resetInstance];
        }
        else
        {
            NSAssert(NO, @"Failed to leave pool!");
        }
        
    });
}

- (void) _updateCountLabel
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
    });
}

- (void) _updateInfoTextView:(NSString*) info
{
    dispatch_async(dispatch_get_main_queue(), ^(){
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
    });
}

- (void) _clearInfoTextView
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        _consoleInfo = [NSMutableString string];
        [self _updateInfoTextView:nil];
    });
}

- (void) _updateUIWithChatWaitStatus:(ChatWaitStatus) status
{
//    [NSThread sleepForTimeInterval:DELAY];
    
//    dispatch_async(dispatch_get_main_queue(), ^(){
    
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
//    });
}

@end
