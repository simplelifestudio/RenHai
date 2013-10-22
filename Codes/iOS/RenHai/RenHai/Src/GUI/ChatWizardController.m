//
//  ChatWizardController.m
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatWizardController.h"

#import "UINavigationController+CBNavigationControllerExtends.h"

#import "GUIModule.h"
#import "CommunicationModule.h"

@interface ChatWizardController ()
{
    ChatWaitViewController_iPhone* _chatWaitViewController;
    ChatConfirmViewController_iPhone* _chatConfirmViewContorller;
    ChatWebRTCViewController_iPhone* _chatWebRTCViewController;
    ChatImpressViewController_iPhone* _chatImpressViewController;
    
    id<ChatWizardPage> _currentPage;
}

@end

@implementation ChatWizardController

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
    
    [self _registerNotifications];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [self _unregisterNotifications];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void) wizardProcess:(ChatWizardStatus) status
{
    UIViewController* presentedVC = nil;
    
    switch (status)
    {
        case ChatWizardStatus_ChatWait:
        {
            presentedVC = _chatWaitViewController;
            break;
        }
        case ChatWizardStatus_ChatConfirm:
        {
            presentedVC = _chatConfirmViewContorller;
            break;
        }
        case ChatWizardStatus_ChatWebRTC:
        {
            presentedVC = _chatWebRTCViewController;
            break;
        }
        case ChatWizardStatus_ChatImpress:
        {
            presentedVC = _chatImpressViewController;
            break;
        }
        default:
        {
            break;
        }
    }
    
    if (nil != presentedVC)
    {
        if (nil != _currentPage)
        {
            [_currentPage pageWillUnload];        
        }
        
        if ([self containsViewController:presentedVC])
        {
            [self popToViewController:presentedVC animated:YES];
        }
        else
        {
            [self pushViewController:presentedVC animated:YES];
        }
        
        _currentPage = (id<ChatWizardPage>)presentedVC;
        [_currentPage resetPage];
        [_currentPage pageWillLoad];        
    }
}

#pragma mark - Private Methods

- (void)_setupInstance
{
    UIStoryboard* storyboard = [UIStoryboard storyboardWithName:STORYBOARD_IPHONE bundle:nil];
    
    _chatWaitViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATWAIT_IPHONE];
    _chatConfirmViewContorller = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATCONFIRM_IPHONE];
    _chatWebRTCViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATWEBRTC_IPHONE];
    _chatImpressViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATIMPRESS_IPHONE];
    
    self.modalPresentationStyle = UIModalPresentationFormSheet;
    self.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    
    [self addChildViewController:_chatWaitViewController];
    [self addChildViewController:_chatConfirmViewContorller];
    [self addChildViewController:_chatWebRTCViewController];
    [self addChildViewController:_chatImpressViewController];
    
    _currentPage = nil;
}

-(void) _registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_SESSIONBOUND object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_OTHERSIDEAGREED object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_OTHERSIDEREJECTED object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_OTHERSIDELOST object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onNotifications:) name:NOTIFICATION_ID_OTHERSIDEENDCHAT object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void) _onNotifications:(NSNotification*) notification
{
    if (nil != notification)
    {
        if ([notification.name isEqualToString:NOTIFICATION_ID_SESSIONBOUND])
        {
            if (nil != _currentPage && [_currentPage respondsToSelector:@selector(onSessionBound)])
            {
                [_currentPage onSessionBound];
            }
        }
        else if ([notification.name isEqualToString:NOTIFICATION_ID_OTHERSIDEAGREED])
        {
            if (nil != _currentPage && [_currentPage respondsToSelector:@selector(onOthersideAgreed)])
            {
                [_currentPage onOthersideAgreed];
            }
        }
        else if ([notification.name isEqualToString:NOTIFICATION_ID_OTHERSIDEREJECTED])
        {
            if (nil != _currentPage && [_currentPage respondsToSelector:@selector(onOthersideRejected)])
            {
                [_currentPage onOthersideRejected];
            }
        }
        else if ([notification.name isEqualToString:NOTIFICATION_ID_OTHERSIDELOST])
        {
            if (nil != _currentPage && [_currentPage respondsToSelector:@selector(onOthersideLost)])
            {
                [_currentPage onOthersideLost];
            }
        }
        else if ([notification.name isEqualToString:NOTIFICATION_ID_OTHERSIDEENDCHAT])
        {
            if (nil != _currentPage && [_currentPage respondsToSelector:@selector(onOthersideEndChat)])
            {
                [_currentPage onOthersideEndChat];
            }
        }
    }
}

@end
