//
//  GUIStyle.h
//  Seeds
//
//  Created by Patrick Deng on 13-5-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "FlatUIKit.h"

// UI Style Definitions based on FlatUIKit

#define COLOR_DEEP [UIColor colorFromHexCode:@"1C1C1C"]
#define COLOR_MID [UIColor colorWithRed:78.0/255.0 green:156.0/255.0 blue:206.0/255.0 alpha:1.0]
#define COLOR_LIGHT [UIColor colorFromHexCode:@"DCDCDC"]

#define COLOR_B_DEEP [UIColor colorFromHexCode:@"4F4F4F"]
#define COLOR_B_LIGHT [UIColor silverColor]

#define FLATUI_COLOR_VIEW_BACKGROUND COLOR_LIGHT
#define FLATUI_COLOR_PROGRESS COLOR_MID
#define FLATUI_COLOR_PROGRESS_TRACK COLOR_B_LIGHT

#define FLATUI_COLOR_TABLECELL COLOR_LIGHT
#define FLATUI_COLOR_TABLECELL_SELECTED COLOR_B_DEEP
#define FLATUI_COLOR_TABLE_SEPERATOR COLOR_B_LIGHT

#define FLATUI_COLOR_BUTTON COLOR_DEEP
#define FLATUI_COLOR_BUTTON_SHADOW COLOR_MID
#define FLATUI_COLOR_BUTTON_TEXT COLOR_LIGHT
#define FLATUI_COLOR_BUTTON_TEXT_HIGHLIGHTED COLOR_B_DEEP
#define FLATUI_COLOR_BUTTON_TEXT_DISABLED COLOR_B_DEEP

#define FLATUI_COLOR_NAVIGATIONBAR COLOR_MID
#define FLATUI_COLOR_TOOLBAR COLOR_MID

#define FLATUI_COLOR_LABEL COLOR_MID
#define FLATUI_COLOR_LABEL_SHADOW COLOR_DEEP
#define FLATUI_COLOR_LABEL_TEXT COLOR_B_DEEP

#define FLATUI_COLOR_BARBUTTONITEM COLOR_DEEP
#define FLATUI_COLOR_BARBUTTONITEM_HIGHLIGHTED COLOR_B_DEEP

#define COLOR_TEXT_WARNING [UIColor redColor]
#define COLOR_TEXT_LOG COLOR_MID
#define COLOR_TEXT_INFO COLOR_DEEP

#define COLOR_IMAGEVIEW_BACKGROUND [UIColor colorWithRed:0.752941 green:0.752941 blue:0.752941 alpha:0.5]


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
+(void) formatFlatUITableViewCell:(UITableViewCell*) cell backColor:(UIColor*) backColor selectedColor:(UIColor*) selectedColor style:(UITableViewCellStyle)style reuseIdentifier:(NSString*)reuseIdentifier;

@end
