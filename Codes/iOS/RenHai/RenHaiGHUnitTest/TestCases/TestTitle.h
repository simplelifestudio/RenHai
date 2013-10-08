//
//  TestTitle.h
//  RenHai
//
//  Created by DENG KE on 13-9-8.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TestObj.h"

@interface TestTitle : NSObject

@property (nonatomic, strong, readonly) TestObj* obj;

-(NSString*) objPassword;

@end
