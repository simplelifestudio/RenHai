//
//  RHLabelManageViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-11-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum
{
    ManageMode_NewLabel = 0,
    ManageMode_ModifyLabel
}
ManageMode;

@protocol RHLabelManageDelegate <NSObject>

@required
-(void) didLabelManageDone:(ManageMode) mode newLabel:(NSString*) newLabel oldLabel:(NSString*) oldLabel;
-(void) didLabelManageCancel;

@end

@interface RHLabelManageViewController_iPhone : UIViewController

+(RHLabelManageViewController_iPhone*) newLabelManageViewController:(id<RHLabelManageDelegate>) manageDelegate;
+(RHLabelManageViewController_iPhone*) modifyLabelManagerViewController:(id<RHLabelManageDelegate>) manageDelegate label:(NSString*) label;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet FUIButton *saveButton;
@property (weak, nonatomic) IBOutlet FUIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UILabel *limitCountLabel;

- (IBAction)didPressSaveButton:(id)sender;
- (IBAction)didPressCancelButton:(id)sender;

@end
