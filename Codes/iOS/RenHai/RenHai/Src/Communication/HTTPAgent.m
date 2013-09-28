//
//  HTTPAgent.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-4.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "HTTPAgent.h"

#import "AFHTTPClient.h"
#import "AFJSONRequestOperation.h"

#import "CBSecurityUtils.h"

#import "CommunicationModule.h"

@implementation HTTPAgent

+(NSMutableURLRequest*) constructURLRequest:(RHMessage*) message serviceTarget:(NSString*) serviceTarget
{
    NSAssert(nil != message, @"Nil JSON Message");
    
    NSMutableURLRequest* request = nil;
    
    AFHTTPClient* httpClient = [[AFHTTPClient alloc] initWithBaseURL:[NSURL URLWithString:BASEURL_HTTP_SERVER]];
    [httpClient setParameterEncoding:AFJSONParameterEncoding];
    
    if ([serviceTarget isEqualToString:REMOTEPATH_SERVICE_HTTP])
    {
        NSString* jsonString = message.toJSONString;
        if ([RHMessage isMessageNeedEncrypt])
        {
            jsonString = [CBSecurityUtils encryptByDESAndEncodeByBase64:jsonString key:MESSAGE_SECURITY_KEY];
        }
        
        NSDictionary* params = [NSDictionary dictionaryWithObject:jsonString forKey:MESSAGE_KEY_ENVELOPE];
        
        request = [httpClient
                   requestWithMethod:@"POST"
                   path:REMOTEPATH_SERVICE_HTTP
                   parameters:params];
    }
    
    return request;
}

#pragma mark - Public Methods

-(RHMessage*) syncMessage:(RHMessage*) requestMessage
{
    RHMessage* responseMessage = [self requestSync:REMOTEPATH_SERVICE_HTTP requestMessage:requestMessage];
    
    return responseMessage;
}

-(RHMessage*) syncMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread
{
    RHMessage* responseMessage = [self requestSync:REMOTEPATH_SERVICE_HTTP requestMessage:requestMessage syncInMainThread:syncInMainThread];
    
    return responseMessage;
}

#pragma mark - CBJSONMessageComm

-(void) requestAsync:(NSString*) serviceTarget
      requestMessage:(RHMessage*) requestMessage
             success:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, id JSON))success
             failure:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id JSON))failure
{
    NSMutableURLRequest* request = [HTTPAgent constructURLRequest:requestMessage serviceTarget:serviceTarget];
    AFJSONRequestOperation *operation = [AFJSONRequestOperation JSONRequestOperationWithRequest:request success:success failure:failure];
    [operation start];
}

// Warning: This method CAN NOT be invoked in Main Thread!
-(RHMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHMessage*) requestMessage
{
    return [self requestSync:serviceTarget requestMessage:requestMessage syncInMainThread:NO];
}

-(RHMessage*) requestSync:(NSString*) serviceTarget requestMessage:(RHMessage*) requestMessage syncInMainThread:(BOOL) syncInMainThread
{
    if (!syncInMainThread && [[NSThread currentThread] isMainThread])
    {
        DDLogWarn(@"Warning: This method CAN NOT be invoked in Main Thread!");
        return nil;
    }
    
    NSTimeInterval timeout = HTTP_COMM_TIMEOUT;
    NSDate* startTimeStamp = [NSDate date];
    NSDate* endTimeStamp = [NSDate dateWithTimeInterval:timeout sinceDate:startTimeStamp];
    
    [requestMessage setTimeStamp:startTimeStamp];
    
    __block RHMessage* responseMessage = nil;
    
    __block NSCondition* lock = [[NSCondition alloc] init];
    
    id syncSuccessBlock = ^(NSURLRequest* request, NSHTTPURLResponse* response, id JSON)
    {
        NSDictionary* content = (NSDictionary*)JSON;
        content = [content objectForKey:MESSAGE_KEY_ENVELOPE];
        responseMessage = [RHMessage constructWithContent:content enveloped:NO];
        
        [lock lock];
        [lock signal];
        [lock unlock];
    };
    
    id syncFailureBlock = ^(NSURLRequest* request, NSHTTPURLResponse* response, NSError* error, id JSON)
    {
        NSDictionary* content = (NSDictionary*)JSON;
        content = [content objectForKey:MESSAGE_KEY_ENVELOPE];
        responseMessage = [RHMessage constructWithContent:content enveloped:NO];
        
        [lock lock];
        [lock signal];
        [lock unlock];
    };
    
    NSMutableURLRequest* request = [HTTPAgent constructURLRequest:requestMessage serviceTarget:serviceTarget];
    AFJSONRequestOperation *operation = [AFJSONRequestOperation JSONRequestOperationWithRequest:request success:syncSuccessBlock failure:syncFailureBlock];
    [operation start];
    
    [lock lock];
    BOOL flag = [lock waitUntilDate:endTimeStamp];
    [lock unlock];
    
    if (!flag)
    {
        [operation cancel];
        responseMessage = [RHMessage newServerTimeoutResponseMessage:requestMessage.messageSn];
        [responseMessage setTimeStamp:endTimeStamp];
    }
    
    return responseMessage;
}

@end
