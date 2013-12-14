//
//  RHLabelManageViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-11-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHLabelManageViewController_iPhone.h"

#import "UIDevice+CBDeviceExtends.h"
#import <CWPopup/UIViewController+CWPopup.h>

#import "CBStringUtils.h"
#import "GUIModule.h"

#define LENGTH_LIMIT_HANZI 5

#define MOVE_DURATION_FOR_KEYBOARD 0.3f

@interface RHLabelManageViewController_iPhone () <UITextFieldDelegate>
{
    BOOL _hasMovedForOffset;
    
    BOOL _isKeyboardOpen;
}

@property (strong, nonatomic) NSString* oldLabel;
@property (strong, nonatomic) NSString* managedLabel;
@property (nonatomic) ManageMode manageMode;
@property (strong, nonatomic) id<RHLabelManageDelegate> manageDelegate;

@end

@implementation RHLabelManageViewController_iPhone

+(RHLabelManageViewController_iPhone*) newLabelManageViewController:(id<RHLabelManageDelegate>) manageDelegate
{
    RHLabelManageViewController_iPhone* vc = nil;
    
    vc = [[RHLabelManageViewController_iPhone alloc] initWithNibName:NIB_LABELMANAGEVIEWCONTROLLER bundle:nil];
    vc.manageMode = ManageMode_NewLabel;
    vc.manageDelegate = manageDelegate;
    vc.oldLabel = nil;
    
    return vc;
}

+(RHLabelManageViewController_iPhone*) modifyLabelManagerViewController:(id<RHLabelManageDelegate>) manageDelegate label:(NSString*) label
{
    RHLabelManageViewController_iPhone* vc = nil;

    vc = [[RHLabelManageViewController_iPhone alloc] initWithNibName:NIB_LABELMANAGEVIEWCONTROLLER bundle:nil];
    vc.manageMode = ManageMode_ModifyLabel;
    vc.manageDelegate = manageDelegate;
    vc.oldLabel = label;
    
    return vc;
}

#pragma mark - Public Methods

@synthesize titleLabel = _titleLabel;
@synthesize textField = _textField;
@synthesize saveButton = _saveButton;
@synthesize cancelButton = _cancelButton;
@synthesize limitCountLabel = _limitCountLabel;

@synthesize oldLabel = _oldLabel;
@synthesize managedLabel = _managedLabel;
@synthesize manageMode = _manageMode;
@synthesize manageDelegate = _manageDelegate;

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
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [self _setupInstance];
    
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self _registerNotifications];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [_textField becomeFirstResponder];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [self _unregisterNotifications];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    BOOL flag = NO;

    NSString* text = _textField.text;
    NSInteger textNumber = [CBStringUtils calculateTextNumber:text];
    if (nil != text && 0 < text.length && textNumber <= LENGTH_LIMIT_HANZI)
    {
        [_textField resignFirstResponder];
        flag = YES;
    }
    
    if (flag)
    {
        [self didPressSaveButton:_saveButton];
    }
    
    return flag;
}

#pragma mark - IBActions

- (IBAction)didPressSaveButton:(id)sender
{
    NSString* text = _textField.text;
    NSInteger textNumber = [CBStringUtils calculateTextNumber:text];
    if (nil != text && 0 < text.length && textNumber <= LENGTH_LIMIT_HANZI)
    {
        _managedLabel = text;
        [_textField resignFirstResponder];
        
        if (nil != _manageDelegate)
        {
            [_manageDelegate didLabelManageDone:_manageMode newLabel:_managedLabel oldLabel:_oldLabel];
        }
    }
}

- (IBAction)didPressCancelButton:(id)sender
{
    if (nil != _manageDelegate)
    {
        [_manageDelegate didLabelManageCancel];
    }
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    switch (_manageMode)
    {
        case ManageMode_NewLabel:
        {
            _titleLabel.text = NSLocalizedString(@"LabelManage_NewLabel", nil);
            break;
        }
        case ManageMode_ModifyLabel:
        {
            _titleLabel.text = NSLocalizedString(@"LabelManage_ModifyLabel", nil);
            break;
        }
        default:
        {
            break;
        }
    }
    
    _textField.delegate = self;
    _textField.placeholder = NSLocalizedString(@"LabelManage_LengthLimit", nil);
    _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    _textField.returnKeyType = UIReturnKeyDone;
    if (nil != _oldLabel && 0 < _oldLabel.length)
    {
        _textField.text = _oldLabel;
    }
    [_textField addTarget:self action:@selector(_textEditingChanged:) forControlEvents:UIControlEventEditingChanged];
    
    _hasMovedForOffset = NO;
    
    _isKeyboardOpen = NO;
    
    [self _updateLimitCountLabel];
    
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
    _textField.layer.masksToBounds = YES;
    _textField.layer.borderColor = FLATUI_COLOR_TEXTFIELD_BORDER.CGColor;
    _textField.layer.borderWidth = FLATUI_WIDTH_TEXTFIELD_BORDER;
    _textField.backgroundColor = FLATUI_COLOR_TEXTFIELD_BACKGROUND;
    
    [_saveButton setTitle:NSLocalizedString(@"LabelManage_Action_Save", nil) forState:UIControlStateNormal];
    [_cancelButton setTitle:NSLocalizedString(@"LabelManage_Action_Cancel", nil) forState:UIControlStateNormal];
    
    _saveButton.buttonColor = FLATUI_COLOR_BUTTONNORMAL;
    if ([UIDevice isRunningOniOS7AndLater])
    {
        _saveButton.highlightedColor = FLATUI_COLOR_BUTTONHIGHLIGHTED;
    }
    [_saveButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_saveButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];

    _cancelButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    if ([UIDevice isRunningOniOS7AndLater])
    {
        _cancelButton.highlightedColor = FLATUI_COLOR_BUTTONHIGHLIGHTED;
    }
    [_cancelButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_cancelButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
}

-(void) _textEditingChanged:(UITextField*) textField
{
    [self _updateLimitCountLabel];
}

-(void) _updateLimitCountLabel
{
    NSString* text = _textField.text;
    NSInteger textNumber = [CBStringUtils calculateTextNumber:text];
    NSInteger limitCount = LENGTH_LIMIT_HANZI - textNumber;
    if (0 <= limitCount)
    {
        [_limitCountLabel setTextColor:FLATUI_COLOR_TEXT_INFO];
    }
    else
    {
        [_limitCountLabel setTextColor:FLATUI_COLOR_TEXT_WARN];
    }
    [_limitCountLabel setText:[NSString stringWithFormat:@"%d", limitCount]];
}

-(void) _registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

-(void) _unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)_keyboardWillShow:(NSNotification*)notification
{
    CGRect _keyboardRect = [[[notification userInfo] objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGRect _oldFrame = self.view.frame;
    
    CGFloat yOffset = 0;
    yOffset = ((_oldFrame.origin.y + _oldFrame.size.height) - _keyboardRect.origin.y);
#warning DIRTY
    GUIModule* guiModule = [GUIModule sharedInstance];
    BOOL flag1 = (nil != self.manageDelegate);
    UIViewController* presentingVC = (UIViewController*)self.manageDelegate;
    UIViewController* parentVCOfPresentingVC = presentingVC.parentViewController;
    BOOL flag2 = parentVCOfPresentingVC == guiModule.navigationController;
    if (flag1 && flag2)
    {
        if (![UIDevice isRunningOniOS7AndLater])
        {
            yOffset += [UIApplication sharedApplication].statusBarFrame.size.height;
        }
    }

    self.view.frame = CGRectMake(_oldFrame.origin.x, _oldFrame.origin.y - yOffset, _oldFrame.size.width, _oldFrame.size.height);
    
    DDLogVerbose(@"LabelManager's frame = %f - %f - %f - %f", self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, self.view.frame.size.height);
    DDLogVerbose(@"LabelManager's superView's frame = %f - %f - %f - %f", self.view.superview.frame.origin.x, self.view.superview.frame.origin.y, self.view.superview.frame.size.width, self.view.superview.frame.size.height);
    DDLogVerbose(@"LabelManager's superView's superView's frame = %f - %f - %f - %f", self.view.superview.superview.frame.origin.x, self.view.superview.superview.frame.origin.y, self.view.superview.superview.frame.size.width, self.view.superview.superview.frame.size.height);
    
    CGPoint center = self.view.center;
    DDLogVerbose(@"Center in view = %f - %f", center.x, center.y);
    CGPoint superCenter = [self.view convertPoint:center toView:self.view.superview];
    DDLogVerbose(@"Center in superView = %f - %f", superCenter.x, superCenter.y);
    CGPoint superSuperCenter = [self.view convertPoint:center toView:self.view.superview.superview];
    DDLogVerbose(@"Center in superSuperView = %f - %f", superSuperCenter.x, superSuperCenter.y);
}

- (void)_keyboardWillHide:(NSNotification*)notification
{
    _isKeyboardOpen = NO;
}

@end
