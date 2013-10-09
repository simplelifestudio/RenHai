//
//  UIViewController+CBUIViewControlerExtends.m
//  RenHai
//
//  Created by DENG KE on 13-10-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UIViewController+CBUIViewControlerExtends.h"

@implementation UIViewController (CBUIViewControlerExtends)

-(BOOL) isVisible
{
    BOOL flag = NO;
    
    if (self.isViewLoaded && self.view.window)
    {
        flag = YES;
    }
    
    return flag;
}

@end
