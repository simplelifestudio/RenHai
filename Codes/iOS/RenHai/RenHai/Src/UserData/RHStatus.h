//
//  RHStatus.h
//  RenHai
//
//  Created by DENG KE on 13-12-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"
#import "RHStatusPeriod.h"

@interface RHStatus : NSObject <CBJSONable>

@property (nonatomic, readonly) ServerServiceStatus serviceStatus;
@property (strong, nonatomic, readonly) RHStatusPeriod* statusPeriod;

@end
