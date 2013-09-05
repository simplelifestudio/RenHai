//
//  CBJSONMessageComm.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHJSONMessage.h"

@protocol CBJSONMessageComm <NSObject>

// Warning: This method CAN NOT be invoked in Main Thread!
-(RHJSONMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHJSONMessage*) requestMessage;

@optional
-(void) requestAsync:(NSString*) serviceTarget
      requestMessage: (RHJSONMessage*) requestMessage
             success:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, id JSON))success
             failure:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id JSON))failure;

@end
