//
//  RHBusinessSession.h
//  RenHai
//
//  Created by DENG KE on 13-10-13.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

#import "RHMatchedCondition.h"
#import "RHWebRTC.h"

@interface RHBusinessSession : NSObject <CBJSONable>

@property (nonatomic, strong) NSString* businessSessionId;
@property (nonatomic) RHBusinessType businessType;

@property (nonatomic, strong, readonly) RHDevice* device;
@property (nonatomic, strong, readonly) RHMatchedCondition* matchedCondition;
@property (nonatomic, strong, readonly) RHWebRTC* webrtc;

@end
