//
//  RHTableViewLabelCell_iPhone.m
//  Seeds
//
//  Created by DENG KE on 13-7-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "RHTableViewLabelCell_iPhone.h"

#import "CBUIUtils.h"

#import "GUIModule.h"
#import "GUIStyle.h"

@implementation RHTableViewLabelCell_iPhone

@synthesize majorLabel = _majorLabel;
@synthesize minorLabel = _minorLabel;

#pragma mark - Static Methods

+(RHTableViewLabelCell_iPhone*) configureFlatCellWithColor:(UIColor *)color selectedColor:(UIColor *)selectedColor style:(UITableViewCellStyle)style reuseIdentifier:(NSString*)reuseIdentifier
{
    RHTableViewLabelCell_iPhone* cell = [CBUIUtils componentFromNib:TABLECELL_ID_LABELER owner:nil options:nil];
    
    FUICellBackgroundView* backgroundView = [FUICellBackgroundView new];
    backgroundView.backgroundColor = color;
    cell.backgroundView = backgroundView;
    
    FUICellBackgroundView* selectedBackgroundView = [FUICellBackgroundView new];
    selectedBackgroundView.backgroundColor = selectedColor;
    cell.selectedBackgroundView = selectedBackgroundView;
    
    cell.majorLabel.backgroundColor = [UIColor clearColor];
    cell.minorLabel.backgroundColor = [UIColor clearColor];
    
    cell.majorLabel.textColor = COLOR_TEXT_INFO;
    cell.minorLabel.textColor = COLOR_TEXT_INFO;
    
    return cell;
}

#pragma mark - Public Methods

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self)
    {
        [self _formatFlatUI];
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

- (void) awakeFromNib
{
    [self _formatFlatUI];
    
    [super awakeFromNib];
}

#pragma mark - Private Methods

-(void) _formatFlatUI
{

}

@end
