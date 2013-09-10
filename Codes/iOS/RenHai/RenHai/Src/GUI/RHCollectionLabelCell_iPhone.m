//
//  RHCollectionLabelCell_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHCollectionLabelCell_iPhone.h"

#import "GUIStyle.h"

@implementation RHCollectionLabelCell_iPhone

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self _setupComponents];
    }
    return self;
}

- (void)awakeFromNib
{
    [self _setupComponents];
    
    [super awakeFromNib];
}

#pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark - Private Methods

-(void) _setupComponents
{
    _textField.delegate = self;
    
    [self _formatFlatUI];
}

-(void) _formatFlatUI
{

}

@end
