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

#import "ChatWaitViewController_iPhone.h"
#import "ChatConfirmViewController_iPhone.h"
#import "ChatVideoViewController_iPhone.h"
#import "ChatMessageViewController_iPhone.h"
#import "ChatAssessViewController_iPhone.h"

#define ALLOW_CHATVIDEO_ROTATION 0

@interface ChatWizardController ()
{
    ChatWaitViewController_iPhone* _chatWaitViewController;
    ChatConfirmViewController_iPhone* _chatConfirmViewContorller;
    ChatVideoViewController_iPhone* _chatVideoViewController;
    ChatAssessViewController_iPhone* _chatAssessViewController;
    
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
        case ChatWizardStatus_ChatVideo:
        {
            presentedVC = _chatVideoViewController;
            break;
        }
        case ChatWizardStatus_ChatAssess:
        {
            presentedVC = _chatAssessViewController;
            break;
        }
        default:
        {
            break;
        }
    }
    
    if (nil != presentedVC)
    {
//        if (nil != _currentPage)
//        {
//            [_currentPage pageWillUnload];
//        }
        
        if ([self containsViewController:presentedVC])
        {
            [self popToViewController:presentedVC animated:YES];
        }
        else
        {
            [self pushViewController:presentedVC animated:YES];
        }
        
        _currentPage = (id<ChatWizardPage>)presentedVC;
//        [_currentPage resetPage];
//        [_currentPage pageWillLoad];
    }
}

#pragma mark - UINavigationControllerDelegate

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
//    static UIViewController *lastController = nil;
//    
//    if (nil != lastController)
//    {
//        if ([lastController respondsToSelector:@selector(viewWillDisappear:)])
//        {
//            [lastController viewWillDisappear:animated];
//        }
//    }
//    
//    lastController = viewController;
//    
//    [viewController viewWillAppear:animated];
}

- (void)navigationController:(UINavigationController *)navigationController didShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
//    static UIViewController *lastController = nil;
//    
//    if (nil != lastController)
//    {
//        if ([lastController respondsToSelector:@selector(viewDidDisappear:)])
//        {
//            [lastController viewDidDisappear:animated];
//        }
//    }
//    
//    lastController = viewController;
//    
//    [viewController viewDidAppear:animated];
}

#pragma mark - Private Methods

- (void)_setupInstance
{
    UIStoryboard* storyboard = [UIStoryboard storyboardWithName:STORYBOARD_IPHONE bundle:nil];
    
    _chatWaitViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATWAIT_IPHONE];
    _chatConfirmViewContorller = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATCONFIRM_IPHONE];
    _chatVideoViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATVIDEO_IPHONE];
    _chatAssessViewController = [storyboard instantiateViewControllerWithIdentifier:STORYBOARD_ID_CHATASSESS_IPHONE];
    
    self.modalPresentationStyle = UIModalPresentationFormSheet;
    self.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    
    [self addChildViewController:_chatWaitViewController];
    [self addChildViewController:_chatConfirmViewContorller];
    [self addChildViewController:_chatVideoViewController];
    [self addChildViewController:_chatAssessViewController];
    
    _currentPage = nil;
    
    self.delegate = self;
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
        else if ([notification.name isEqualToString:NOTIFICATION_ID_OTHERSIDECHATMESSAGE])
        {
            if (nil != _currentPage && [_currentPage respondsToSelector:@selector(onOthersideChatMessage)])
            {
                [_currentPage onOthersideChatMessage];
            }
        }
    }
}

#pragma mark - ScreenOrientation Methods

- (BOOL) shouldAutorotate
{
    BOOL flag = NO;
    
    if (self.topViewController == _chatVideoViewController)
    {
        flag = YES;
    }
    
#ifdef ALLOW_CHATVIDEO_ROTATION
    flag = NO;
#endif
    
	return flag;
}

- (NSUInteger)supportedInterfaceOrientations
{
#ifdef ALLOW_CHATVIDEO_ROTATION
    if (self.topViewController == _chatVideoViewController)
    {
        return UIInterfaceOrientationMaskAll;
    }
    else
    {
        return UIInterfaceOrientationMaskPortrait;
    }
#else
    return UIInterfaceOrientationMaskPortrait;    
#endif
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation
{
	return UIInterfaceOrientationPortrait;
}

@end
