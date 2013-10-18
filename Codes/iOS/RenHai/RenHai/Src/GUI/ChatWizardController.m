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

@interface ChatWizardController ()
{
    
}

@end

@implementation ChatWizardController

@synthesize chatWaitViewController = _chatWaitViewController;
@synthesize chatConfirmViewContorller = _chatConfirmViewContorller;
@synthesize chatWebRTCViewController = _chatWebRTCViewController;
@synthesize chatImpressViewController = _chatImpressViewController;

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
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
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
    
    if ((nil != presentedVC) && [self containsViewController:presentedVC])
    {
        [self popToViewController:presentedVC animated:YES];
    }
    else
    {
        [self pushViewController:presentedVC animated:YES];
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
}

@end
