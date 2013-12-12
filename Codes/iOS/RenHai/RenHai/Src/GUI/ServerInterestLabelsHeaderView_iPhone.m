//
//  ServerInterestLabelsHeaderView_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-12.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ServerInterestLabelsHeaderView_iPhone.h"

@implementation ServerInterestLabelsHeaderView_iPhone

#pragma mark - Public Methods

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
    
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self _setupInstance];
}

#pragma mark - Private Methods

-(void) _setupInstance
{

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
