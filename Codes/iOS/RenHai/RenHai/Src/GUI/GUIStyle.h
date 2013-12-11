//
//  GUIStyle.h
//  Seeds
//
//  Created by DENG KE on 13-5-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "FlatUIKit.h"
#import "KXKiOS7Colors.h"
#import "KXKiOSGradients.h"

// UI Style Definitions based on FlatUIKit

// Font Set
#define FLATUI_FONT_GIANT 20
#define FLATUI_FONT_BIG 18
#define FLATUI_FONT_NORMAL 16
#define FLATUI_FONT_SMALL 14

// Color Set
#define FLATUI_COLOR_MAJOR_A [UIColor colorWithHexCode:@"04AEEE"] // 蓝
#define FLATUI_COLOR_MAJOR_B [UIColor colorWithHexCode:@"FF6347"] // 红
#define FLATUI_COLOR_MAJOR_C [UIColor colorWithHexCode:@"54FF9F"] // 绿
#define FLATUI_COLOR_MAJOR_D [UIColor Grey21] // 黑
#define FLATUI_COLOR_MAJOR_E [UIColor Grey51] // 灰
#define FLATUI_COLOR_MAJOR_F [UIColor cloudsColor] // 白

#define FLATUI_COLOR_SUCCESS FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_WARNING FLATUI_COLOR_MAJOR_B
#define FLATUI_COLOR_CLEAR [UIColor clearColor]
#define FLATUI_COLOR_WHITE [UIColor whiteColor]

// Text
#define FLATUI_COLOR_TEXT_WARN FLATUI_COLOR_MAJOR_B
#define FLATUI_COLOR_TEXT_LOG FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_TEXT_INFO FLATUI_COLOR_MAJOR_D

// Others
#define FLATUI_CORNERRADIUS_BARBUTTONITEM 0.3f

// UINavigationBar
#define FLATUI_COLOR_NAVIGATIONBAR_MAIN FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_NAVIGATIONBAR_CHATWIZARD FLATUI_COLOR_MAJOR_B
#define FLATUI_COLOR_TINT_NAVIGATIONBAR FLATUI_COLOR_MAJOR_A

// UIBarbuttonItem
#define FLATUI_COLOR_BARBUTTONITEM FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_BARBUTTONITEM_HIGHLIGHTED FLATUI_COLOR_MAJOR_D
#define FLATUI_COLOR_TINT_BARBUTTONITEM FLATUI_COLOR_MAJOR_D

// UIButton
#define FLATUI_COLOR_BUTTONNORMAL FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_BUTTONPROCESS FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_BUTTONROLLBACK FLATUI_COLOR_MAJOR_B
#define FLATUI_COLOR_BUTTONTITLE FLATUI_COLOR_MAJOR_F
#define FLATUI_COLOR_BUTTONHIGHLIGHTED FLATUI_COLOR_MAJOR_F

// TableCell
#define FLATUI_COLOR_COLLECTIONCELL FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_COLLECTIONCELL_SELECTED FLATUI_COLOR_MAJOR_B

// UIView
#define FLATUI_COLOR_UIVIEW_BACKGROUND FLATUI_COLOR_WHITE

// UICollectionReusableView
#define FLATUI_COLOR_UICOLLECTIONREUSABLEVIEW_BACKGROUND FLATUI_COLOR_MAJOR_F

// TextField
#define FLATUI_COLOR_TEXTFIELD_BORDER FLATUI_COLOR_WHITE
#define FLATUI_COLOR_TEXTFIELD_BACKGROUND FLATUI_COLOR_WHITE
#define FLATUI_WIDTH_TEXTFIELD_BORDER 1.0f

// View: LabelManager
#define FLATUI_COLOR_LABELMANAGER_BACKGROUND FLATUI_COLOR_MAJOR_F
#define FLATUI_COLOR_LABELMANAGER_LABEL FLATUI_COLOR_MAJOR_F

// View: Leftbar
#define FLATUI_COLOR_LEFTBAR_CELL_TEXT FLATUI_COLOR_TEXT_INFO
#define FLATUI_COLOR_LEFTBAR_CELL_TEXT_SELECTED FLATUI_COLOR_TEXT_INFO
#define FLATUI_COLOR_LEFTBAR_CELL_BACKGROUND FLATUI_COLOR_WHITE
#define FLATUI_COLOR_LEFTBAR_CELL_BACKGROUND_SELECTED FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_LEFTBAR_CELL_BACKGROUND_SELECTING FLATUI_COLOR_MAJOR_F

// View: Home
#define FLATUI_COLOR_TINT_PROGRESS FLATUI_COLOR_MAJOR_B

// View: Device
#define FLATUI_COLOR_TABLECELL FLATUI_COLOR_MAJOR_E
#define FLATUI_COLOR_TABLECELL_SELECTED FLATUI_COLOR_MAJOR_A

// View: ChatConfirm
#define FLATUI_COLOR_CHATCONFIRMTYPE_CONSIDERING FLATUI_COLOR_MAJOR_F
#define FLATUI_COLOR_CHATCONFIRMTYPE_AGREED FLATUI_COLOR_MAJOR_A
#define FLATUI_COLOR_CHATCONFIRMTYPE_REJECTED FLATUI_COLOR_MAJOR_B
#define FLATUI_COLOR_CHATCONFIRMTYPE_LOST FLATUI_COLOR_MAJOR_B

// View: ChatVideo
#define FLATUI_COLOR_CHATMESSAGESENDER_BACKGROUND FLATUI_COLOR_MAJOR_F
#define FLATUI_COLOR_CHATMESSAGE_BACKGROUND FLATUI_COLOR_MAJOR_F

@interface GUIStyle : NSObject

+(UIImage*) sidebarMenuIconPortrait;
+(UIImage*) sidebarMenuIconLandscape;

@end
