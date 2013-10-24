//
//  RHImpressCard.h
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHImpressLabel.h"

@interface RHImpressCard : NSObject <CBJSONable, NSCopying, NSMutableCopying>

+(RHImpressCard*) newImpressCard:(NSString*) assessLabel impressLabels:(NSArray*) impressLabels;

@property (nonatomic) NSUInteger impressCardId;

@property (nonatomic, strong) NSArray* assessLabelList;
@property (nonatomic, strong) NSArray* impressLabelList;

@property (nonatomic) NSUInteger chatTotalCount;
@property (nonatomic) NSUInteger chatTotalDuration;
@property (nonatomic) NSUInteger chatLossCount;

-(NSArray*) topImpressLabelList:(NSUInteger) top;

@end
