//
//  RHProxy.h
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

#import "RHServiceAddress.h"
#import "RHStatusPeriod.h"

@interface RHProxy : NSObject <CBJSONable>

@property (nonatomic, readonly) ServerServiceStatus serviceStatus;
@property (strong, nonatomic, readonly) RHServiceAddress* serviceAddress;
@property (strong, nonatomic, readonly) RHStatusPeriod* statusPeriod;

@end
