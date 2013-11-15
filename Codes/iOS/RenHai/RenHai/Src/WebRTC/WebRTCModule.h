//
//  WebRTCModule.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#import "OpenTokAgent.h"

@interface WebRTCModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (strong, nonatomic, readonly) OpenTokAgent *openTokAgent;

-(void) connectAndPublishOnWebRTC:(NSString*) apiKey sessionId:(NSString*) sessionId token:(NSString*) token;
-(void) unpublishAndDisconnectOnWebRTC;

-(void) registerWebRTCDelegate:(id<OpenTokDelegate>) delegate;

@end
