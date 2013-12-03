//
//  OpenTokAgent.h
//  RenHai
//
//  Created by DENG KE on 13-11-14.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Opentok/Opentok.h>

#define STATIC_OPENTOK_ACCOUNT 0

static NSString* const kApiKey = @"34650792";    // Replace with your OpenTok API key
static NSString* const kSessionId = @"1_MX4zNDU2NjU2Mn5-V2VkIE5vdiAxMyAyMzowMTo1MCBQU1QgMjAxM34wLjcwNzM1MTE1fg"; // Replace with your generated session ID
static NSString* const kToken = @"T1==cGFydG5lcl9pZD0zNDU2NjU2MiZzZGtfdmVyc2lvbj10YnJ1YnktdGJyYi12MC45MS4yMDExLTAyLTE3JnNpZz1jNzE5MzE3NGZiOTVjZjZmMGNiM2FjNjQ5OGM4ZjdlMGM2YmM2NjNkOnJvbGU9cHVibGlzaGVyJnNlc3Npb25faWQ9MV9NWDR6TkRVMk5qVTJNbjUtVjJWa0lFNXZkaUF4TXlBeU16b3dNVG8xTUNCUVUxUWdNakF4TTM0d0xqY3dOek0xTVRFMWZnJmNyZWF0ZV90aW1lPTEzODQ0MTI1MTYmbm9uY2U9MC4wMjE0MjM4ODcwODEyMzY0MzcmZXhwaXJlX3RpbWU9MTM4NzAwNDUxNyZjb25uZWN0aW9uX2RhdGE9";     // Replace with your generated token (use the Dashboard or an OpenTok server-side library)

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
-(void) subscriberDidChangeVideoDimensions:(CGSize)dimensions;

@end

@interface OpenTokAgent : NSObject <OTSessionDelegate, OTSubscriberDelegate, OTPublisherDelegate>

@property (strong, nonatomic) id<OpenTokDelegate> openTokDelegate;

-(void) connectWithAPIKey:(NSString*) apiKey sessionId:(NSString*) sessionId token:(NSString*) token;
-(void) disconnect;

-(OTVideoView*) publisherView;
-(OTVideoView*) subscriberView;

-(void) mutePublisher:(BOOL) mute;
-(void) muteSubscriber:(BOOL) mute;

@end