//
//  RHServer.h
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

@interface RHServer : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic, strong) RHServerDeviceCount* deviceCount;
@property (nonatomic, strong) RHServerDeviceCapacity* deviceCapacity;
@property (nonatomic, strong) RHServerInterestLabelList* interestLabelList;

@end