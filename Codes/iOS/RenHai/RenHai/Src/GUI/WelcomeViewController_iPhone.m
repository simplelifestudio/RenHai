//
//  WelcomeViewController.m
//  RenHai
//
//  Created by DENG KE on 13-12-16.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "WelcomeViewController_iPhone.h"

#import "CBUIUtils.h"
#import "CBDateUtils.h"
#import "UINavigationController+CBNavigationControllerExtends.h"
#import "UIViewController+CWPopup.h"
#import "UIViewController+CBUIViewControlerExtends.h"

#import "GUIModule.h"
#import "CommunicationModule.h"
#import "UserDataModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

@interface WelcomeViewController_iPhone ()
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    UserDataModule* _userDataModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
}

@end

@implementation WelcomeViewController_iPhone

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
    
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
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
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (void)dismissWelcome
{
    [self dismissViewControllerAnimated:TRUE completion:nil];
}

#pragma mark - IBAction Methods

- (IBAction)didPressActionButton:(id)sender
{
    [self dismissWelcome];
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
    
    [self _setupActionButtons];
    
    [self _formatFlatUI];
}

- (void) _formatFlatUI
{
    _infoLabel.text = NSLocalizedString(@"Welcome_Title", nil);
    _textView.text = NSLocalizedString(@"Welcome_Text", nil);
    _textView.font = [UIFont flatFontOfSize:FLATUI_FONT_NORMAL];
}

- (void) _setupActionButtons
{
    _actionButton.buttonColor = FLATUI_COLOR_BUTTONPROCESS;
    [_actionButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_actionButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    [_actionButton setTitle:NSLocalizedString(@"Welcome_Action_Ok", nil) forState:UIControlStateNormal];
}

@end
