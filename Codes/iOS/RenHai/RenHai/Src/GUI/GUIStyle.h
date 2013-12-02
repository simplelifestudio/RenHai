//
//  GUIStyle.h
//  Seeds
//
//  Created by DENG KE on 13-5-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "FlatUIKit.h"

// UI Style Definitions based on FlatUIKit

#define MAJOR_COLOR_DEEP [UIColor colorWithHexCode:@"067AB5"]
#define MAJOR_COLOR_LIGHT [UIColor Grey51]

#define SECONDARY_COLOR_DEEP [UIColor Grey21]
#define SECONDARY_COLOR_LIGHT [UIColor whiteColor]

#define SPECIAL_COLOR_WARNING [UIColor redColor]
#define SPECIAL_COLOR_CLEAR [UIColor clearColor]

#define BIG_FONT 18
#define NORMAL_FONT 16
#define SMALL_FONT 14

#define FLATUI_COLOR_TINT SECONDARY_COLOR_DEEP

#define FLATUI_COLOR_VIEW_BACKGROUND MAJOR_COLOR_LIGHT
#define FLATUI_COLOR_PROGRESS MAJOR_COLOR_DEEP
#define FLATUI_COLOR_PROGRESS_TRACK SECONDARY_COLOR_LIGHT

#define FLATUI_COLOR_LABELCELL_BORDER MAJOR_COLOR_DEEP

#define FLATUI_COLOR_TABLECELL MAJOR_COLOR_LIGHT
#define FLATUI_COLOR_TABLECELL_SELECTED SECONDARY_COLOR_DEEP
#define FLATUI_COLOR_TABLE_SEPERATOR SECONDARY_COLOR_LIGHT

#define FLATUI_COLOR_BUTTON MAJOR_COLOR_DEEP
#define FLATUI_COLOR_BUTTON_SHADOW MAJOR_COLOR_DEEP
#define FLATUI_COLOR_BUTTON_TEXT MAJOR_COLOR_LIGHT
#define FLATUI_COLOR_BUTTON_TEXT_HIGHLIGHTED SECONDARY_COLOR_DEEP
#define FLATUI_COLOR_BUTTON_TEXT_DISABLED COLOR_B_DEEP

#define FLATUI_COLOR_NAVIGATIONBAR MAJOR_COLOR_DEEP
#define FLATUI_COLOR_TOOLBAR MAJOR_COLOR_DEEP

#define FLATUI_COLOR_LABEL MAJOR_COLOR_DEEP
#define FLATUI_COLOR_LABEL_SHADOW MAJOR_COLOR_DEEP
#define FLATUI_COLOR_LABEL_TEXT SECONDARY_COLOR_DEEP

#define FLATUI_COLOR_BARBUTTONITEM MAJOR_COLOR_DEEP
#define FLATUI_COLOR_BARBUTTONITEM_HIGHLIGHTED SECONDARY_COLOR_DEEP

#define FLATUI_COLOR_PAGECONTROL_BACKGROUND MAJOR_COLOR_LIGHT

#define COLOR_TEXT_WARNING SPECIAL_COLOR_WARNING
#define COLOR_TEXT_LOG MAJOR_COLOR_MID
#define COLOR_TEXT_INFO SECONDARY_COLOR_DEEP

#define FLATUI_CORNER_RADIUS 3

@interface GUIStyle : NSObject

+(UIImage*) sidebarMenuIconPortrait;
+(UIImage*) sidebarMenuIconLandscape;

// FlatUI Componenents Formatters
+(void) formatFlatUIButton:(FUIButton*) button buttonColor:(UIColor*) buttonColor shadowColor:(UIColor*) shadowColor shadowHeight:(CGFloat) shadowHeight cornerRadius:(CGFloat) cornerRadius titleColor:(UIColor*) titleColor highlightedTitleColor:(UIColor*) highlightedTitleColor;
+(void) formatFlatUILabel:(UILabel*) label textColor:(UIColor*) textColor;
+(void) formatFlatUIProgressView:(UIProgressView*) progress;
+(void) formatFlatUINavigationBar:(UINavigationBar*) navigationBar;
+(void) formatFlatUIBarButtonItem:(UIBarButtonItem*) buttonItem;
+(void) formatFlatUIToolbar:(UIToolbar*) toolbar;
+(void) formatFlatUITableViewCell:(UITableViewCell*) cell backColor:(UIColor*) backColor selectedColor:(UIColor *)selectedColor reuseIdentifier:(NSString *)reuseIdentifier tableView:(UITableView*) tableView;

@end
