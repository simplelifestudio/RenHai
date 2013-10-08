//
//  CBAppUtils.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CBAppUtils : NSObject

+(void) exitApp;

+(void) asyncProcessInBackgroundThread:(void(^)()) block;
+(void) asyncProcessInMainThread:(void(^)()) block;

@end
