//
//  RHImpressLabel.h
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHImpressLabel : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic) NSUInteger labelId;
@property (nonatomic, strong) NSString* labelName;
@property (nonatomic) NSUInteger assessedCount;
@property (nonatomic) NSDate* updateTime;
@property (nonatomic) NSUInteger assessCount;

+(NSString*) assessLabelName:(NSString*) labelName;

@end
