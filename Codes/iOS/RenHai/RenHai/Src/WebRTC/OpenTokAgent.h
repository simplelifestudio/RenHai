//
//  OpenTokAgent.h
//  RenHai
//
//  Created by DENG KE on 13-11-14.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Opentok/Opentok.h>

@protocol OpenTokDelegate

-(void) sessionDidConnect;
-(void) sessionDidDisconnect;
-(void) sessionDidFailWithError;

-(void) sessionDidReceiveSelfStream;
-(void) sessionDidReceivePartnerStream;

-(void) sessionDidDropPartnerStream;
-(void) sessionDidPartnerConnected;
-(void) sessionDidPartnerDisConnected;

-(void) publisherDidFailWithError;

-(void) subscriberDidConnectToStream;
-(void) subscriberDidFailWithError;

@end

@interface OpenTokAgent : NSObject <OTSessionDelegate, OTSubscriberDelegate, OTPublisherDelegate>

@property (strong, nonatomic) id<OpenTokDelegate> openTokDelegate;

-(void) connectWithAPIKey:(NSString*) apiKey sessionId:(NSString*) sessionId token:(NSString*) token;
-(void) disconnect;

-(OTVideoView*) publisherView;
-(OTVideoView*) subscriberView;

@end