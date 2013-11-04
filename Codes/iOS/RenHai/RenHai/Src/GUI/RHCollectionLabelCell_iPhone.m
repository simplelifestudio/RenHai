//
//  RHCollectionLabelCell_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHCollectionLabelCell_iPhone.h"

#import "GUIStyle.h"

@implementation RHCollectionLabelCell_iPhone

@synthesize textField = _textField;
@synthesize countLabel = _countLabel;

@synthesize editingDelegate = _editingDelegate;

@synthesize isEmptyCell = _isEmptyCell;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self _formatFlatUI];
    }
    return self;
}

- (void)awakeFromNib
{
    [self _formatFlatUI];
    
    self.layer.borderWidth=2.0f;
    self.layer.borderColor=[UIColor blueColor].CGColor;
    
    self.layer.cornerRadius = 10.0;
    
    
    [super awakeFromNib];
}

-(void) setSelected:(BOOL)selected
{
    [super setSelected:selected];
    [self setNeedsDisplay];
}

-(void) drawRect:(CGRect)rect
{
    [super drawRect:rect];
    
    if (self.selected)
    {
        self.layer.borderColor = [UIColor redColor].CGColor;
    }
    else
    {
        self.layer.borderColor = [UIColor DodgerBlue].CGColor;
    }
}

#pragma mark - Private Methods

-(void) _formatFlatUI
{

}

- (IBAction)textFieldDoneEditing:(id)sender
{
    [sender resignFirstResponder];
    
    NSString* labelName = _textField.text;
    if (nil == labelName || 0 == labelName.length)
    {
        labelName = NSLocalizedString(@"Interest_Empty", nil);
        _textField.text = labelName;
    }
    
    if (nil != _editingDelegate)
    {
        [_editingDelegate onTextFieldDoneEditing:self labelName:labelName];
    }
}

@end
