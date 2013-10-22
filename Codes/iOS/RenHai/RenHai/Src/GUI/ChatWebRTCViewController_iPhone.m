//
//  ChatWebRTCViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatWebRTCViewController_iPhone.h"

@interface ChatWebRTCViewController_iPhone ()

@end

@implementation ChatWebRTCViewController_iPhone

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    
}

-(void) pageWillLoad
{
    
}

-(void) pageWillUnload
{
    
}

-(void) onOthersideEndChat
{
    
}

-(void) onOthersideLost
{
    
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    
}

@end
