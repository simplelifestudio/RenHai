//
//  RHServerData.h
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHServerDeviceCount.h"
#import "RHServerDeviceCapacity.h"
#import "RHServerInterestLabelList.h"

@interface RHServerData : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic, strong, readonly) RHServerDeviceCount* deviceCount;
@property (nonatomic, strong, readonly) RHServerDeviceCapacity* deviceCapacity;
@property (nonatomic, strong, readonly) RHServerInterestLabelList* interestLabelList;

@end
