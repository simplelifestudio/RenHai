//
//  RHProxy.h
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

#import "RHAddress.h"
#import "RHStatus.h"

@interface RHProxy : NSObject <CBJSONable>

@property (strong, nonatomic, readonly) NSString* serverId;
@property (strong, nonatomic, readonly) RHStatus* status;
@property (strong, nonatomic, readonly) RHAddress* address;
@property (strong, nonatomic, readonly) NSString* broadcast;

@end
