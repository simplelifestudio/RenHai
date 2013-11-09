//
//  RHCollectionLabelCell_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum
{
    CellMode_Normal = 0,
    CellMode_Delete,
    CellMode_Sort
}
CellMode;

@interface RHCollectionLabelCell_iPhone : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;

@property (nonatomic) CellMode cellMode;
@property (nonatomic) BOOL shakeCell;

- (IBAction)textFieldDoneEditing:(id)sender;

- (NSString*) labelName;

@end
