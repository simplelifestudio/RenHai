//
//  RHImpressLabel.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHImpressLabel : NSObject <CBJSONable>

@property (nonatomic) NSUInteger labelId;
@property (nonatomic, strong) NSString* name;
@property (nonatomic) NSUInteger assessedCount;
@property (nonatomic) NSDate* updateTime;
@property (nonatomic) NSUInteger assessCount;


@end
