//
//  RHMatchedCondition.h
//  RenHai
//
//  Created by DENG KE on 13-11-14.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

@interface RHMatchedCondition : NSObject <CBJSONable>

@property (strong, nonatomic, readonly) RHInterestLabel* interestLabel;

@end
