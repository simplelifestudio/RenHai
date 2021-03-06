//
//  OpenTokAgent.m
//  RenHai
//
//  Created by DENG KE on 13-11-14.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "OpenTokAgent.h"

@interface OpenTokAgent()
{
    OTSession* _session;
    OTPublisher* _publisher;
    OTSubscriber* _subscriber;
    
    BOOL _isPublisherViewReady;
    BOOL _isSubscriberViewReady;
    
    BOOL _isUnpublishNecessary;
}

@end

@implementation OpenTokAgent

@synthesize openTokDelegate = _openTokDelegate;

#pragma mark - Public Methods

-(void) connectWithAPIKey:(NSString*) apiKey sessionId:(NSString*) sessionId token:(NSString*) token
{
    @try
    {
        _session = [[OTSession alloc] initWithSessionId:sessionId delegate:self];
        [_session connectWithApiKey:apiKey token:token];
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

-(void) disconnect
{
    @try
    {
        if (_isUnpublishNecessary)
        {
            [self _unpublish];
        }
        
        if (nil != _session)
        {
            [_session disconnect];
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

-(OTVideoView*) publisherView
{
    OTVideoView* view = nil;
    
    if (_isPublisherViewReady)
    {
        view = _publisher.view;
    }
    
    return view;
}

-(OTVideoView*) subscriberView
{
    OTVideoView* view = nil;
    
    if (_isSubscriberViewReady)
    {
        view = _subscriber.view;
    }
    
    return view;
}

-(void) pausePublisher:(BOOL) pause
{
    @try
    {
        if (_isPublisherViewReady)
        {
            [_publisher setPublishVideo:!pause];
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

-(void) pauseSubscriber:(BOOL) pause
{
    @try
    {
        if (_isSubscriberViewReady)
        {
            [_subscriber setSubscribeToVideo:!pause];
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

-(void) mutePublisher:(BOOL) mute
{
    @try
    {
        if (_isPublisherViewReady)
        {
            [_publisher setPublishAudio:!mute];
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

-(void) muteSubscriber:(BOOL) mute
{
    @try
    {
        if (_isSubscriberViewReady)
        {
            [_subscriber setSubscribeToAudio:!mute];
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

#pragma mark - OTSessionDelegate methods

- (void)sessionDidConnect:(OTSession*)session
{
    DDLogInfo(@"WebRTC sessionDidConnect: %@", session.sessionId);
    DDLogInfo(@"- connectionId: %@", session.connection.connectionId);
    DDLogInfo(@"- creationTime: %@", session.connection.creationTime);
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate sessionDidConnect];
    }
    
    [self _publish];
}

- (void)sessionDidDisconnect:(OTSession*)session
{
    DDLogInfo(@"WebRTC sessionDidDisconnect: %@", session.sessionId);
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate sessionDidDisconnect];
    }
}

- (void)session:(OTSession*)session didFailWithError:(OTError*)error
{
    DDLogWarn(@"WebRTC sessionDidFailWithError:");
    DDLogWarn(@"- error code: %d", error.code);
    DDLogWarn(@"- description: %@", error.localizedDescription);
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate sessionDidFailWithError];
    }
}

- (void)session:(OTSession*)session didReceiveStream:(OTStream*)stream
{
    DDLogInfo(@"WebRTC sessionDidReceiveStream:");
    DDLogInfo(@"- connection.connectionId: %@", stream.connection.connectionId);
    DDLogInfo(@"- connection.creationTime: %@", stream.connection.creationTime);
    DDLogInfo(@"- session.sessionId: %@", stream.session.sessionId);
    DDLogInfo(@"- streamId: %@", stream.streamId);
    DDLogInfo(@"- type %@", stream.type);
    DDLogInfo(@"- creationTime %@", stream.creationTime);
    DDLogInfo(@"- name %@", stream.name);
    DDLogInfo(@"- hasAudio %@", (stream.hasAudio ? @"YES" : @"NO"));
    DDLogInfo(@"- hasVideo %@", (stream.hasVideo ? @"YES" : @"NO"));
    
    @try
    {
        BOOL flag1 = [stream.connection.connectionId isEqualToString: _session.connection.connectionId];
        
        if (!flag1)
        {
            if (!_subscriber)
            {
                _subscriber = [[OTSubscriber alloc] initWithStream:stream delegate:self];
                _subscriber.subscribeToAudio = YES;
                _subscriber.subscribeToVideo = YES;
                
                if (nil != _openTokDelegate)
                {
                    [_openTokDelegate sessionDidReceivePartnerStream];
                }
            }
            DDLogInfo(@"WebRTC subscriber.session.sessionId: %@", _subscriber.session.sessionId);
            DDLogInfo(@"- stream.streamId: %@", _subscriber.stream.streamId);
            DDLogInfo(@"- subscribeToAudio %@", (_subscriber.subscribeToAudio ? @"YES" : @"NO"));
            DDLogInfo(@"- subscribeToVideo %@", (_subscriber.subscribeToVideo ? @"YES" : @"NO"));
        }
        else
        {
            if (nil != _openTokDelegate)
            {
                [_openTokDelegate sessionDidReceiveSelfStream];
            }
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

- (void)session:(OTSession*)session didDropStream:(OTStream*)stream
{
    DDLogWarn(@"WebRTC sessionDidDropStream (%@)", stream.streamId);
    
    BOOL flag1 = (nil != _subscriber) ? YES : NO;
    BOOL flag2 = [_subscriber.stream.streamId isEqualToString: stream.streamId];
    
    if (flag1 && flag2)
    {
        _subscriber = nil;
        
        if (nil != _openTokDelegate)
        {
            [_openTokDelegate sessionDidDropPartnerStream];
        }
    }
}

- (void)session:(OTSession *)session didCreateConnection:(OTConnection *)connection
{
    DDLogInfo(@"WebRTC sessionDidCreateConnection (%@)", connection.connectionId);
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate sessionDidPartnerConnected];
    }
}

- (void) session:(OTSession *)session didDropConnection:(OTConnection *)connection
{
    DDLogInfo(@"WebRTC sessionDidDropConnection (%@)", connection.connectionId);
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate sessionDidPartnerDisconnected];
    }
}

#pragma mark - OTPublisherDelegate methods

- (void)publisher:(OTPublisher*)publisher didFailWithError:(OTError*) error
{
    DDLogWarn(@"WebRTC publisher: %@ didFailWithError:", publisher);
    DDLogWarn(@"- error code: %d", error.code);
    DDLogWarn(@"- description: %@", error.localizedDescription);
    
    _isUnpublishNecessary = YES;
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate publisherDidFailWithError];
    }
}

- (void)publisherDidStartStreaming:(OTPublisher *)publisher
{
    DDLogInfo(@"WebRTC publisherDidStartStreaming: %@", publisher);
    DDLogInfo(@"- publisher.session: %@", publisher.session.sessionId);
    DDLogInfo(@"- publisher.name: %@", publisher.name);
}

-(void)publisherDidStopStreaming:(OTPublisher*)publisher
{
    DDLogWarn(@"WebRTC publisherDidStopStreaming:%@", publisher);
}

#pragma mark - OTSubscriberDelegate methods

- (void)subscriberDidConnectToStream:(OTSubscriber*)subscriber
{
    DDLogInfo(@"WebRTC subscriberDidConnectToStream (%@)", subscriber.stream.connection.connectionId);
    
    _isSubscriberViewReady = YES;
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate subscriberDidConnectToStream];
    }
}

- (void)subscriberVideoDataReceived:(OTSubscriber*)subscriber
{
    DDLogInfo(@"WebRTC subscriberVideoDataReceived (%@)", subscriber.stream.streamId);
}

- (void)stream:(OTStream*)stream didChangeVideoDimensions:(CGSize)dimensions
{
    DDLogInfo(@"WebRTC didChangeVideoDimensions:(width: %lf, height: %lf) ", dimensions.width, dimensions.height);
    
    if (stream.streamId == _subscriber.stream.streamId)
    {
        if (nil != _openTokDelegate)
        {
            [_openTokDelegate subscriberDidChangeVideoDimensions:dimensions];
        }
    }
}

- (void)subscriber:(OTSubscriber *)subscriber didFailWithError:(OTError *)error
{
    DDLogWarn(@"WebRTC subscriber: %@ didFailWithError: ", subscriber.stream.streamId);
    DDLogWarn(@"- code: %d", error.code);
    DDLogWarn(@"- description: %@", error.localizedDescription);
    
    if (nil != _openTokDelegate)
    {
        [_openTokDelegate subscriberDidFailWithError];
    }
}

#pragma mark - Private Methods

-(void) _publish
{
    @try
    {
        _publisher = [[OTPublisher alloc] initWithDelegate:self name:UIDevice.currentDevice.name];
        _publisher.publishAudio = YES;
        _publisher.publishVideo = YES;
        [_session publish:_publisher];
        _isPublisherViewReady = YES;
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

-(void) _unpublish
{
    @try
    {
        [_subscriber close];
        _subscriber = nil;
        
        if (!_isUnpublishNecessary)
        {
            [_session unpublish:_publisher];
            _publisher = nil;
        }
        
        _isSubscriberViewReady = NO;
        _isPublisherViewReady = NO;
        _isUnpublishNecessary = NO;
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

- (void)_updateSubscriber
{
    @try
    {
        for (NSString* streamId in _session.streams)
        {
            OTStream* stream = [_session.streams valueForKey:streamId];
            if (stream.connection.connectionId != _session.connection.connectionId)
            {
                _subscriber = [[OTSubscriber alloc] initWithStream:stream delegate:self];
                break;
            }
        }
    }
    @catch (NSException *exception)
    {
        DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
    }
    @finally
    {
        
    }
}

@end
