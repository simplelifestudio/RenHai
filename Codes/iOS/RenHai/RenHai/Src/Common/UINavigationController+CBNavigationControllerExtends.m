//
//  UINavigationController+CBNavigationControllerExtends.m
//  RenHai
//
//  Created by DENG KE on 13-10-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UINavigationController+CBNavigationControllerExtends.h"

@implementation UINavigationController (CBNavigationControllerExtends)

-(BOOL) containsViewController:(UIViewController*) controller
{
    BOOL flag = NO;
    
    NSArray* controllers = self.viewControllers;
    for (UIViewController* vc in controllers)
    {
        if (vc == controller)
        {
            flag = YES;
            break;
        }
    }
    
    return flag;
}

@end
