//
//  RHAlertViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 14-1-21.
//  Copyright (c) 2014年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol RHAlertDelegate <NSObject>

@required
-(void) didAckAlert;

@end

@interface RHAlertViewController_iPhone : UIViewController

+(RHAlertViewController_iPhone*) newAlertViewController:(id<RHAlertDelegate>) delegate alertText:(NSString*) alertText;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet FUIButton *ackButton;

- (IBAction)didPressAckButton:(id)sender;

@end
