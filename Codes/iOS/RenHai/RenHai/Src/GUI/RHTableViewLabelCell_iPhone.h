//
//  RHTableViewLabelCell_iPhone.h
//  Seeds
//
//  Created by DENG KE on 13-7-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RHTableViewLabelCell_iPhone : UITableViewCell

+(RHTableViewLabelCell_iPhone*) configureFlatCellWithColor:(UIColor *)color selectedColor:(UIColor *)selectedColor style:(UITableViewCellStyle)style reuseIdentifier:(NSString*)reuseIdentifier;

@property (weak, nonatomic) IBOutlet UILabel *majorLabel;
@property (weak, nonatomic) IBOutlet UILabel *minorLabel;

@end
