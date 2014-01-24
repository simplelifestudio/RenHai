//
//  RHAlertViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 14-1-21.
//  Copyright (c) 2014年 Simplelife Studio. All rights reserved.
//

#import "RHAlertViewController_iPhone.h"

#import <CWPopup/UIViewController+CWPopup.h>

#import "GUIModule.h"

@interface RHAlertViewController_iPhone ()
{
    
}

@property (strong, nonatomic) id<RHAlertDelegate> alertDelegate;

@end

@implementation RHAlertViewController_iPhone

+(RHAlertViewController_iPhone*) newAlertViewController:(id<RHAlertDelegate>) delegate alertText:(NSString*) alertText;
{
    RHAlertViewController_iPhone* vc = nil;
    
    vc = [[RHAlertViewController_iPhone alloc] initWithNibName:NIB_ALERTVIEWCONTROLLER bundle:nil];
    vc.alertDelegate = delegate;
    
    vc.infoLabel.text = alertText;
    
    return vc;
}

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        
    }
    return self;
}

- (void) awakeFromNib
{
    [self _setupInstance];
    
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - IBActions

- (IBAction)didPressAckButton:(id)sender
{
    
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _titleLabel.text = NSLocalizedString(@"Alert_Title", nil);
    
    _infoLabel.text = @"";
    
    [self _setupActionButtons];
    
    [self _formatFlatUI];
}

-(void)_formatFlatUI
{
    _titleLabel.backgroundColor = FLATUI_COLOR_LABELMANAGER_LABEL;
    self.view.backgroundColor = FLATUI_COLOR_LABELMANAGER_BACKGROUND;
}

-(void)_setupActionButtons
{
    [_ackButton setTitle:NSLocalizedString(@"Alert_Action_Ack", nil) forState:UIControlStateNormal];
    
    _ackButton.buttonColor = FLATUI_COLOR_BUTTONNORMAL;
    if ([UIDevice isRunningOniOS7AndLater])
    {
        _ackButton.highlightedColor = FLATUI_COLOR_BUTTONHIGHLIGHTED;
    }
    [_ackButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_ackButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
}

@end
