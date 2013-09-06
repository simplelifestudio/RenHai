//
//  MainViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "MainViewController_iPhone.h"

#import "GUIModule.h"

@interface MainViewController_iPhone ()

@end

@implementation MainViewController_iPhone

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
    
    [self setMinimumWidth:LEFTBAR_WIDTH_IPHONE maximumWidth:LEFTBAR_WIDTH_IPHONE forViewController:self.leftViewController];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}

@end
