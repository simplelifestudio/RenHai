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

@end

@interface RHCollectionLabelCell_iPhone : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;

@property (strong, nonatomic) id<RHCollectionLabelCellEditingDelegate> editingDelegate;

@property (nonatomic) BOOL isEmptyCell;

- (IBAction)textFieldDoneEditing:(id)sender;



@end
