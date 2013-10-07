//
//  ViewController.m
//  RenHaiGHUnitTest
//
//  Created by DENG KE on 13-8-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ViewController.h"

#import "LoggerModule.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"

@interface ViewController ()
{
    LoggerModule* _loggerModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
}

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void) viewWillAppear:(BOOL)animated
{
//    _loggerModule = [LoggerModule sharedInstance];
//    [_loggerModule initModule];
//    
//    _userDataModule = [UserDataModule sharedInstance];
//    [_userDataModule initModule];
//    [_userDataModule initUserData];
//    
//    _commModule = [CommunicationModule sharedInstance];
//    [_commModule initModule];
    
    [super viewWillAppear:animated];
}

- (void) viewWillDisappear:(BOOL)animated
{
//    [_commModule releaseModule];
//    
//    [_userDataModule releaseModule];
//    
//    [_loggerModule releaseModule];
    
    [super viewWillDisappear:animated];
}

@end
