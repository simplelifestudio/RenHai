//
//  RHImpressCard.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHImpressLabel.h"

@interface RHImpressCard : NSObject <CBJSONable>

@property (nonatomic) NSUInteger impressCardId;

@property (nonatomic, strong, readonly) NSArray* assessLabelList;
@property (nonatomic, strong) NSMutableArray* impressLabelList;

@property (nonatomic) NSUInteger chatTotalCount;
@property (nonatomic) NSUInteger chatTotalDuration;
@property (nonatomic) NSUInteger chatLossCount;

@end
