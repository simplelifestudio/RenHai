//
//  CBSharedInstance.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

//#define DEFINE_SINGLETON_FOR_HEADER(className) \
//\
//+ (className*) sharedInstance;

#define SINGLETON(className) \
\
+ (id) sharedInstance \
{ \
    static className* sharedInstance = nil; \
    static dispatch_once_t onceToken; \
    dispatch_once \
    ( \
        &onceToken, \
        ^ \
        { \
            sharedInstance = [[self alloc] init]; \
            if([sharedInstance respondsToSelector:@selector(instanceInit)]) \
            { \
                [sharedInstance instanceInit]; \
            } \
        } \
    ); \
    \
    return sharedInstance; \
}

@protocol CBSharedInstance <NSObject>

+(id) sharedInstance;

@optional
-(void) instanceInit;

@end
