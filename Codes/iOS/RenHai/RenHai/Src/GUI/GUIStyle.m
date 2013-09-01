//
//  GUIStyle.m
//  Seeds
//
//  Created by Patrick Deng on 13-5-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "GUIStyle.h"

#import <QuartzCore/QuartzCore.h>

@implementation GUIStyle

// Static block
+(void) initialize
{
}

+(void) formatFlatUIButton:(FUIButton*) button buttonColor:(UIColor*) buttonColor shadowColor:(UIColor*) shadowColor shadowHeight:(CGFloat) shadowHeight cornerRadius:(CGFloat) cornerRadius titleColor:(UIColor*) titleColor highlightedTitleColor:(UIColor*) highlightedTitleColor
{
    if (button)
    {
        button.buttonColor = buttonColor;
        button.shadowColor = shadowColor;
        button.shadowHeight = shadowHeight;
        button.cornerRadius = cornerRadius;
        // button.titleLabel.font = [UIFont flatFontOfSize:24];
        [button setTitleColor:titleColor forState:UIControlStateNormal];
        [button setTitleColor:highlightedTitleColor forState:UIControlStateHighlighted];
    }
}

+(void) formatFlatUILabel:(UILabel*) label textColor:(UIColor *)textColor
{
    if (label)
    {
        label.textColor = textColor;
    }
}

+(void) formatFlatUIProgressView:(UIProgressView*) progress
{
    if (progress)
    {
        [progress configureFlatProgressViewWithTrackColor:FLATUI_COLOR_PROGRESS_TRACK progressColor:FLATUI_COLOR_PROGRESS];
    }
}

+(void) formatFlatUINavigationBar:(UINavigationBar*) navigationBar
{
    if (navigationBar)
    {
        [navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_NAVIGATIONBAR];
        navigationBar.titleTextAttributes = @{//UITextAttributeFont: [UIFont boldFlatFontOfSize:18],
                                                                        UITextAttributeTextColor: FLATUI_COLOR_BUTTON_TEXT};
    }
}

+(void) formatFlatUIBarButtonItem:(UIBarButtonItem*) buttonItem
{
    if (buttonItem)
    {
        [buttonItem configureFlatButtonWithColor:FLATUI_COLOR_BARBUTTONITEM highlightedColor:FLATUI_COLOR_BARBUTTONITEM_HIGHLIGHTED cornerRadius:FLATUI_CORNER_RADIUS];
        [buttonItem removeTitleShadow];
    }
}

+(void) formatFlatUIToolbar:(UIToolbar*) toolbar
{
    if (toolbar)
    {
        [toolbar configureFlatToolbarWithColor:FLATUI_COLOR_TOOLBAR];
    }
}

+(void) formatFlatUITableViewCell:(UITableViewCell*) cell backColor:(UIColor*) backColor selectedColor:(UIColor*) selectedColor style:(UITableViewCellStyle)style reuseIdentifier:(NSString*)reuseIdentifier
{
    if (cell)
    {
        [UITableViewCell configureFlatCellWithColor:backColor selectedColor:selectedColor style:style reuseIdentifier:reuseIdentifier];
    }
}

@end
