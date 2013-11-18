//
//  HTTPAgent.h
//  RenHai
//
//  Created by DENG KE on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONMessageComm.h"

@interface HTTPAgent : NSObject <CBJSONMessageComm>

-(NSMutableURLRequest*) constructURLRequest:(RHMessage*) message serviceTarget:(NSString*) serviceTarget;

-(RHMessage*) syncMessage:(RHMessage*) requestMessage;
-(RHMessage*) syncMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread;

@end
