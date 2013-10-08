//
//  RHServerInterestLabelList.h
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHServerInterestLabelList : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic, readonly) NSArray* current;
@property (nonatomic, readonly) NSDate* historyStartTime;
@property (nonatomic, readonly) NSDate* historyEndTime;

@end
