//
//  RHLabelManageViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-11-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHLabelManageViewController_iPhone.h"

#import "CBStringUtils.h"
#import "GUIModule.h"

#define LENGTH_LIMIT_HANZI 5

#define MOVE_OFFSET_FOR_KEYBOARD_3_5 80
#define MOVE_OFFSET_FOR_KEYBOARD_4 40

#define MOVE_DURATION_FOR_KEYBOARD 0.3f

@interface RHLabelManageViewController_iPhone () <UITextFieldDelegate>
{
    BOOL _hasMovedForOffset;
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
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [_textField becomeFirstResponder];    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - UITextFieldDelegate

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
//    BOOL flag = NO;
//    
//    @try
//    {
//        NSMutableString* text = [_textField.text mutableCopy];
//        NSInteger oldTextNumber = [CBStringUtils calculateTextNumber:text];
//        
//        [text replaceCharactersInRange:range withString:string];
//        NSInteger newTextNumber = [CBStringUtils calculateTextNumber:text];
//        
//        flag = (newTextNumber <= LENGTH_LIMIT || newTextNumber < oldTextNumber);
//        
//        flag = YES;
//    }
//    @catch (NSException *exception)
//    {
//        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
//        
//        flag = NO;
//    }
//    @finally
//    {
//
//    }
//    
//    return flag;
    
    return YES;
}

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

- (BOOL)textFieldShouldBeginEditing:(id)sender
{
    if (!_hasMovedForOffset)
    {
        _hasMovedForOffset = YES;
        
#warning Should replace with API but not hard code
        NSUInteger requireCount = 0;
        if (IS_IPHONE5)
        {
            requireCount = MOVE_OFFSET_FOR_KEYBOARD_4;
        }
        else
        {
            requireCount = MOVE_OFFSET_FOR_KEYBOARD_3_5;
        }
        
        self.view.center = CGPointMake(self.view.center.x, self.view.center.y - requireCount);
    }
    
    return YES;
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
    
    [self _updateLimitCountLabel];
    
    [self _setupActionButtons];
    
    [self _formatFlatUI];
}

-(void)_formatFlatUI
{
    _titleLabel.backgroundColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
    self.view.backgroundColor = FLATUI_COLOR_NAVIGATIONBAR_MAIN;
}

-(void)_setupActionButtons
{
    [_saveButton setTitle:NSLocalizedString(@"LabelManage_Action_Save", nil) forState:UIControlStateNormal];
    [_cancelButton setTitle:NSLocalizedString(@"LabelManage_Action_Cancel", nil) forState:UIControlStateNormal];
    
    _saveButton.buttonColor = FLATUI_COLOR_BUTTONPROCESS;
    [_saveButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_saveButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    _cancelButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
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

@end
