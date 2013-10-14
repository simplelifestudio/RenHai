//
//  RHBusinessSession.h
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

@interface RHBusinessSession : NSObject

@property (nonatomic, strong) NSString* businessSessionId;
@property (nonatomic) RHBusinessType businessType;
@property (nonatomic, strong) NSArray* chatParters;

@end
