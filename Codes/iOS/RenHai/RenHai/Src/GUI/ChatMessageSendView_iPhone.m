//
//  ChatMessageSendView_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-8.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatMessageSendView_iPhone.h"

#import "GUIStyle.h"

@interface ChatMessageSendView_iPhone() <UITextFieldDelegate>
{

}

@end

@implementation ChatMessageSendView_iPhone

#pragma mark - Public Methods

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        
    }
    return self;
}

- (void) awakeFromNib
{
    [super awakeFromNib];
    
    [self _setupInstance];
}

-(void)_setupInstance
{
    self.backgroundColor = FLATUI_COLOR_CHATMESSAGESENDER_BACKGROUND;
    
    _textField.delegate = self;
    _textField.returnKeyType = UIReturnKeySend;
    _textField.placeholder = NSLocalizedString(@"ChatVideo_ChatMessageSendTip", nil);
    _textField.clearButtonMode = UITextFieldViewModeWhileEditing;

    _textField.layer.masksToBounds = YES;
    _textField.layer.borderColor = FLATUI_COLOR_TEXTFIELD_BORDER.CGColor;
    _textField.layer.borderWidth = 1.0f;
    _textField.backgroundColor = FLATUI_COLOR_TEXTFIELD_BACKGROUND;
    
    [self _setupActionButtons];
    
    [self _registerNotifications];
}

-(void)_setupActionButtons
{
    [_sendButton setTitle:NSLocalizedString(@"ChatVideo_Action_Send", nil) forState:UIControlStateNormal];
    
    _sendButton.buttonColor = FLATUI_COLOR_BUTTONNORMAL;
    _sendButton.highlightedColor = FLATUI_COLOR_BUTTONHIGHLIGHTED;
    [_sendButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_sendButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
}

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

-(BOOL)resignFirstResponder
{
    [self.textField resignFirstResponder];
    self.hidden = YES;
    
    return [super resignFirstResponder];
}

#pragma mark - IBActions

- (IBAction)didPressSendButton:(id)sender
{
    NSString* text = _textField.text;
    if (nil != text && 0 < text.length)
    {
        if (_sendDelegate)
        {
            [_sendDelegate onSendMessage:_textField.text];
        }
        
        _textField.text = @"";
        
        [self resignFirstResponder];    
    }
}

#pragma mark - Private Methods

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
    CGRect _oldFrame = self.frame;
    
    [UIView animateWithDuration:0.3
                                  delay:0
                                options:UIViewAnimationOptionCurveEaseInOut
                             animations:^{
                                 self.frame = CGRectMake(0, _keyboardRect.origin.y - _oldFrame.size.height, _oldFrame.size.width, _oldFrame.size.height);
                             } completion:nil];
    

}

- (void)_keyboardWillHide:(NSNotification*)notification
{
    CGRect _oldFrame = self.frame;
    
    [UIView animateWithDuration:0.3
                          delay:0
                        options:UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         self.frame = CGRectMake(0, self.superview.frame.size.height - _oldFrame.size.height * 2, _oldFrame.size.width, _oldFrame.size.height);
                     } completion:nil];
}

#pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    BOOL flag = NO;
    
    NSString* text = _textField.text;
    if (nil != text && 0 < text.length)
    {
        [_textField resignFirstResponder];
        flag = YES;
    }
    
    if (flag)
    {
        [self didPressSendButton:_sendButton];
    }
    
    return flag;
}

@end
