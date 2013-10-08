//
//  CBAppUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBAppUtils.h"

@implementation CBAppUtils

+(void) exitApp
{
    exit(0);
}

+(void) asyncProcessInBackgroundThread:(void(^)()) block
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), block);
}

+(void) asyncProcessInMainThread:(void(^)()) block
{
    dispatch_async(dispatch_get_main_queue(), block);
}

@end
