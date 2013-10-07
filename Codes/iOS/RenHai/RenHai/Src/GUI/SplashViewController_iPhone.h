//
//  SplashViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SplashViewController_iPhone : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *label1;
@property (weak, nonatomic) IBOutlet UILabel *label2;
@property (weak, nonatomic) IBOutlet UILabel *label3;
@property (weak, nonatomic) IBOutlet UILabel *studioLabel;

-(void) loadAnyNecessaryStuff;
-(void) startFadingSplashScreen;
-(void) finishFadingSplashScreen;

@end
