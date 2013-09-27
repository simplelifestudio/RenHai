//
//  CBJSONMessageComm.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

@protocol CBJSONMessageComm <NSObject>

// Warning: This method CAN NOT be invoked in Main Thread!
-(RHMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHMessage*) requestMessage;
-(RHMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread;

@optional
-(void) requestAsync:(NSString*) serviceTarget
      requestMessage: (RHMessage*) requestMessage
             success:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, id JSON))success
             failure:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id JSON))failure;

@end
