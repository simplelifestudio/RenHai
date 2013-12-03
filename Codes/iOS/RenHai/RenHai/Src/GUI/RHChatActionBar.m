//
//  RHChatActionBar.m
//  RenHai
//
//  Created by DENG KE on 13-12-3.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHChatActionBar.h"

#import "GUIStyle.h"

@implementation RHChatActionBar

-(id)initWithFrame:(CGRect)aRect
{
    if((self = [super initWithFrame:aRect]))
    {
        self.opaque = NO;
        self.backgroundColor = FLATUI_COLOR_CLEAR;
        self.clearsContextBeforeDrawing = YES;
    }
    
    return self;
}

-(void)drawRect:(CGRect)rect
{
    
}

@end
