//
//  RHTableViewSwitcherCell_iPhone.m
//  Seeds
//
//  Created by DENG KE on 13-7-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "RHTableViewSwitcherCell_iPhone.h"

#import "CBUIUtils.h"

#import "GUIModule.h"
#import "GUIStyle.h"

@implementation RHTableViewSwitcherCell_iPhone

@synthesize switcher = _switcher;
@synthesize switcherLabel = _switcherLabel;

#pragma mark - Static Methods

+(RHTableViewSwitcherCell_iPhone*) configureFlatCellWithColor:(UIColor *)color selectedColor:(UIColor *)selectedColor style:(UITableViewCellStyle)style reuseIdentifier:(NSString*)reuseIdentifier
{
    RHTableViewSwitcherCell_iPhone* cell = [CBUIUtils componentFromNib:TABLECELL_ID_SWITCHER owner:nil options:nil];
    
    FUICellBackgroundView* backgroundView = [FUICellBackgroundView new];
    backgroundView.backgroundColor = color;
    cell.backgroundView = backgroundView;
    
    FUICellBackgroundView* selectedBackgroundView = [FUICellBackgroundView new];
    selectedBackgroundView.backgroundColor = selectedColor;
    cell.selectedBackgroundView = selectedBackgroundView;
    
    cell.switcherLabel.backgroundColor = [UIColor clearColor];
    cell.switcherLabel.textColor = COLOR_TEXT_INFO;

    cell.switcher.onColor = FLATUI_COLOR_BUTTON;
    cell.switcher.offColor = FLATUI_COLOR_LABEL;
    cell.switcher.onBackgroundColor = FLATUI_COLOR_BUTTON_SHADOW;
    cell.switcher.offBackgroundColor = FLATUI_COLOR_LABEL_SHADOW;
    cell.switcher.offLabel.font = [UIFont boldFlatFontOfSize:SMALL_FONT];
    cell.switcher.onLabel.font = [UIFont boldFlatFontOfSize:SMALL_FONT];
    
    return cell;
}

#pragma mark - Public Methods

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

- (void)awakeFromNib
{
    [self _formatFlatUI];
    
    [super awakeFromNib];
}

#pragma mark - Private Methods

- (void)_formatFlatUI
{
    
}

@end
