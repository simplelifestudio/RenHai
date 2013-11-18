//
//  RHServiceAddress.h
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

@interface RHServiceAddress : NSObject <CBJSONable>

@property (strong, nonatomic, readonly) NSString* protocol;
@property (strong, nonatomic, readonly) NSString* ip;
@property (nonatomic, readonly) NSUInteger port;
@property (strong, nonatomic, readonly) NSString* path;

-(NSString*) fullAddress;

@end
