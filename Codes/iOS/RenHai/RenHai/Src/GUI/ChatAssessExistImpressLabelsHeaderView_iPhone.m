//
//  ChatAssessExistImpressLabelsHeaderView_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-11-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatAssessExistImpressLabelsHeaderView_iPhone.h"

@implementation ChatAssessExistImpressLabelsHeaderView_iPhone

@synthesize titleLabel = _titleLabel;
@synthesize cloneButton = _cloneButton;

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

- (IBAction)didPressRefreshButton:(id)sender
{

}

- (IBAction)didPressCloneButton:(id)sender
{
    if (nil != _operationDelegate)
    {
        [_operationDelegate didCloneImpressLabel];
    }
}

@end
