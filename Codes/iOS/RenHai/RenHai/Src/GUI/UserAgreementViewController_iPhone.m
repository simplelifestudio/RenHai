//
//  UserAgreementViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-22.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserAgreementViewController_iPhone.h"

#import "CBUIUtils.h"
#import "CBDateUtils.h"
#import "CBStringUtils.h"
#import "UINavigationController+CBNavigationControllerExtends.h"
#import "UIViewController+CWPopup.h"
#import "UIViewController+CBUIViewControlerExtends.h"

#import "GUIModule.h"
#import "CommunicationModule.h"
#import "UserDataModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

@interface UserAgreementViewController_iPhone () <UIScrollViewDelegate, FTCoreTextViewDelegate>
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    UserDataModule* _userDataModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
}

@end

@implementation UserAgreementViewController_iPhone

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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)dismissUserAgreement
{
    [self dismissViewControllerAnimated:NO completion:nil];
}

#pragma mark - IBActions

- (IBAction)didPressAcceptButton:(id)sender
{
    [_appDataModule recordUserAgreementAccepted];    
    
    [self dismissUserAgreement];
}

- (IBAction)didPressDeclineButton:(id)sender
{
    [_appDataModule resetUserAgreementAccepted];
    
    [CBAppUtils exitApp];
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGPoint contentOffsetPoint = _scrollView.contentOffset;

    CGRect frame = _scrollView.frame;
    
    if (contentOffsetPoint.y == _scrollView.contentSize.height - frame.size.height || _scrollView.contentSize.height < frame.size.height)
    {
        _acceptButton.enabled = YES;
    }
}

#pragma mark - FTCoreTextViewDelegate

- (void)coreTextView:(FTCoreTextView *)acoreTextView receivedTouchOnData:(NSDictionary *)data
{
    NSURL *url = [data objectForKey:FTCoreTextDataURL];
        
    if (url)
    {
        [[UIApplication sharedApplication] openURL:url];
    }
}

#pragma mark - Private Methods

- (void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];

    _statusbarLabel.backgroundColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
    _titleLabel.backgroundColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
    
    _scrollView.delegate = self;
    _textView.delegate = self;
    
    [self _setupActionButtons];
    
    [self _formatFlatUI];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    
	[_textView fitToSuggestedHeight];
    
    [_scrollView setContentSize:CGSizeMake(CGRectGetWidth(_scrollView.bounds), CGRectGetMaxY(_textView.frame))];
}

- (void) _formatFlatUI
{
    _titleLabel.text = NSLocalizedString(@"UserAgreement_Title", nil);
//    _textView.text = NSLocalizedString(@"UserAgreement_Text", nil);
//    _textView.font = [UIFont flatFontOfSize:FLATUI_FONT_NORMAL];
    
    NSString* text = [CBStringUtils textFromTextFileNamed:@"useragreement.html"];
    _textView.text = text;
    FTCoreTextStyle* style = [FTCoreTextStyle styleWithName:FTCoreTextTagDefault];
    style.font = [UIFont systemFontOfSize:FLATUI_FONT_NORMAL];
    
    FTCoreTextStyle *aStyle = [FTCoreTextStyle styleWithName:FTCoreTextTagLink];
    aStyle.name = @"a";
    aStyle.underlined = NO;
    aStyle.color = [UIColor blueColor];
    [_textView changeDefaultTag:FTCoreTextTagLink toTag:@"a"];
    
    [_textView addStyles:@[style, aStyle]];    
}

- (void) _setupActionButtons
{
    _acceptButton.buttonColor = FLATUI_COLOR_BUTTONPROCESS;
    [_acceptButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_acceptButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    [_acceptButton setTitle:NSLocalizedString(@"UserAgreement_Action_Accept", nil) forState:UIControlStateNormal];
    _acceptButton.enabled = YES;

    _declineButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    [_declineButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_declineButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    [_declineButton setTitle:NSLocalizedString(@"UserAgreement_Action_Decline", nil) forState:UIControlStateNormal];
}

@end
