//
//  RHTableViewSwitcherCell_iPhone.h
//  Seeds
//
//  Created by Patrick Deng on 13-7-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RHTableViewSwitcherCell_iPhone : UITableViewCell

+(RHTableViewSwitcherCell_iPhone*) configureFlatCellWithColor:(UIColor *)color selectedColor:(UIColor *)selectedColor style:(UITableViewCellStyle)style reuseIdentifier:(NSString*)reuseIdentifier;

@property (nonatomic, weak) IBOutlet FUISwitch *switcher;
@property (nonatomic, weak) IBOutlet UILabel *switcherLabel;

@end
