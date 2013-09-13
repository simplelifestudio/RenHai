//
//  RHCollectionLabelCell_iPhone.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RHCollectionLabelCell_iPhone : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;

- (IBAction)textFieldDoneEditing:(id)sender;

@end
