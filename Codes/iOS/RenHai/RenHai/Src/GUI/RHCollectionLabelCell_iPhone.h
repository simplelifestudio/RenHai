//
//  RHCollectionLabelCell_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@class RHCollectionLabelCell_iPhone;

@protocol RHCollectionLabelCellEditingDelegate <NSObject>

@required
-(void) onTextFieldDoneEditing:(RHCollectionLabelCell_iPhone*)cell labelName:(NSString*) labelName;

@optional
-(void) didDelete;

@end

typedef enum
{
    CellMode_Normal = 0,
    CellMode_Delete,
    CellMode_Order
}
CellMode;

@interface RHCollectionLabelCell_iPhone : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;

@property (strong, nonatomic) id<RHCollectionLabelCellEditingDelegate> editingDelegate;

@property (nonatomic) CellMode cellMode;
@property (nonatomic) BOOL shakeCell;

- (IBAction)textFieldDoneEditing:(id)sender;

@end
