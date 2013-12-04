//
//  LeftBarTableCell_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "LeftBarTableCell_iPhone.h"

#import "GUIStyle.h"

@implementation LeftBarTableCell_iPhone

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self)
    {
        // Initialization code
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
//    [self configureFlatCellWithColor:FLATUI_COLOR_LEFTBAR_BACKGROUND selectedColor:FLATUI_COLOR_LEFTBAR_BACKGROUND_SELECTED];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

@end
