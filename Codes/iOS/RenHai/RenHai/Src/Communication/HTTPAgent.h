//
//  HTTPAgent.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONMessageComm.h"

@interface HTTPAgent : NSObject <CBJSONMessageComm>

+(NSMutableURLRequest*) constructURLRequest:(RHJSONMessage*) message serviceTarget:(NSString*) serviceTarget;

-(RHJSONMessage*) syncMessage:(RHJSONMessage*) requestMessage;
-(RHJSONMessage*) syncMessage:(RHJSONMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread;

@end
