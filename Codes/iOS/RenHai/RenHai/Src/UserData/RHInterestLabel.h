//
//  RHInterestLabel.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHInterestLabel : NSObject <CBJSONable>

@property (nonatomic) NSUInteger labelId;
@property (nonatomic, strong) NSString* labelName;
@property (nonatomic) NSUInteger globalMatchCount;
@property (nonatomic) NSUInteger labelOrder;
@property (nonatomic) NSUInteger matchCount;
@property (nonatomic) BOOL validFlag;

@end
