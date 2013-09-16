//
//  RHTableViewLabelCell_iPhone.m
//  Seeds
//
//  Created by Patrick Deng on 13-7-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "RHTableViewLabelCell_iPhone.h"

#import "GUIStyle.h"

@implementation RHTableViewLabelCell_iPhone

@synthesize majorLabel = _majorLabel;
@synthesize minorLabel = _minorLabel;

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
    [GUIStyle formatFlatUILabel:_majorLabel textColor:COLOR_TEXT_INFO];
    [GUIStyle formatFlatUILabel:_minorLabel textColor:COLOR_TEXT_INFO];
    
    [RHTableViewLabelCell_iPhone configureFlatCellWithColor:[UIColor greenColor] selectedColor:FLATUI_COLOR_TABLECELL_SELECTED style:UITableViewCellStyleValue1 reuseIdentifier:@"RHTableViewLabelCell_iPhone"];
}

@end