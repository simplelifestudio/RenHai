//
//  ChatAssessAddImpressLabelsHeaderView_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-11-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatAssessAddImpressLabelsHeaderView_iPhone.h"

@implementation ChatAssessAddImpressLabelsHeaderView_iPhone

@synthesize titleLabel = _titleLabel;
@synthesize addButton = _addButton;
@synthesize delButton = _delButton;

@synthesize operationDelegate = _operationDelegate;

#pragma mark - Public Methods

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        // Initialization code
    }
    return self;
}

#pragma mark - IBActions

- (IBAction)didPressAddButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didCreateImpressLabel];
    }
}

- (IBAction)didPressDelButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didDeleteImpressLabel];
    }
}

@end
