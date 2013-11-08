//
//  InterestLabelsHeaderView_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-12.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "InterestLabelsHeaderView_iPhone.h"

@implementation InterestLabelsHeaderView_iPhone

@synthesize createButton = _createButton;
@synthesize delButton = _delButton;
@synthesize orderButton = _orderButton;

@synthesize operationDelegate = _operationDelegate;

#pragma mark - Public Methods

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
    
    }
    return self;
}

#pragma mark - IBActions

- (IBAction)didPressCreateButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didCreateInterestLabel];
    }
}

- (IBAction)didPressDelButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didDeleteInterestLabel];
    }
}

- (IBAction)didPressOrderButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didOrderInterestLabels];
    }
}

@end
