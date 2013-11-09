//
//  ServerInterestLabelsHeaderView_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-12.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ServerInterestLabelsHeaderView_iPhone.h"

@implementation ServerInterestLabelsHeaderView_iPhone

@synthesize headerTitleLabel = _headerTitleLabel;

@synthesize refreshButton = _refreshButton;
@synthesize cloneButton = _cloneButton;

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

- (IBAction)didPressRefreshButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didRefreshInterestLabels];
    }
}

- (IBAction)didPressCloneButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didCloneInterestLabel];
    }
}

@end
