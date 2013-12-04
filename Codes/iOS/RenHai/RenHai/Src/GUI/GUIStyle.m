//
//  GUIStyle.m
//  Seeds
//
//  Created by DENG KE on 13-5-2.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "GUIStyle.h"

#import <QuartzCore/QuartzCore.h>

static UIImage* sidebarMenuIconPortrait;
static UIImage* sidebarMenuIconLandscape;

@implementation GUIStyle

// Static block
+(void) initialize
{
    if (nil == sidebarMenuIconPortrait)
    {
        sidebarMenuIconPortrait = [UIImage imageNamed:@"sidebar_menu_icon_portrait"];

    }
    
    if (nil == sidebarMenuIconLandscape)
    {
       sidebarMenuIconLandscape = [UIImage imageNamed:@"sidebar_menu_icon_landscape"];        
    }
}

+(UIImage*) sidebarMenuIconPortrait
{
    return sidebarMenuIconPortrait;
}

+(UIImage*) sidebarMenuIconLandscape
{
    return sidebarMenuIconLandscape;
}

@end
