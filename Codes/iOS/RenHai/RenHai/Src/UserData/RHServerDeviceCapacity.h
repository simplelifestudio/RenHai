//
//  RHServerDeviceCapacity.h
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHServerDeviceCapacity : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic, readonly) NSUInteger online;
@property (nonatomic, readonly) NSUInteger random;
@property (nonatomic, readonly) NSUInteger interest;

@end
