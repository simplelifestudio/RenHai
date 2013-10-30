//
//  BusinessStatus.m
//  RenHai
//
//  Created by DENG KE on 13-10-29.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "BusinessStatus.h"

typedef void (^AppMessageHandleBlock)(AppMessageIdentifier);
typedef void (^ServerNotificationHandleBlock)(ServerNotificationIdentifier);

@interface BusinessStatus()
{
    BusinessStatusIdentifier _businessStatusId;
    
    NSMutableArray* _appMessageRecords;
    NSMutableArray* _serverNotificationRecords;

    AppMessageHandleBlock _appMessageHandleBlock;
    ServerNotificationHandleBlock _serverNotificationHandleBlock;
}

@end

@implementation BusinessStatus

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        [self _setupInstance];
    }
    
    return self;
}

-(id) initWithIdentifier:(BusinessStatusIdentifier) identifier appMessageHandleBlock:(void(^)(AppMessageIdentifier appMessageId)) appMessageHandleBlock serverNotificationHandleBlock:(void(^)(ServerNotificationIdentifier serverNotificationId)) serverNotificationHandleBlock
{
    _businessStatusId = identifier;
    
    _appMessageHandleBlock = appMessageHandleBlock;
    
    _serverNotificationHandleBlock = serverNotificationHandleBlock;
    
    return [self init];
}

-(void) recordAppMessage:(AppMessageIdentifier) appMessageId
{
    NSNumber* oId = [NSNumber numberWithInt:appMessageId];
    @synchronized(_appMessageRecords)
    {
        [_appMessageRecords addObject:oId];
    }
    
    [self _onAppMessage:appMessageId];
}

-(void) recordServerNotification:(ServerNotificationIdentifier) serverNotificationId
{
    NSNumber* oId = [NSNumber numberWithInt:serverNotificationId];
    @synchronized(_serverNotificationRecords)
    {
        [_serverNotificationRecords addObject:oId];
    }
    
    [self _onServerNotification:serverNotificationId];
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _appMessageRecords = [NSMutableArray array];
    _serverNotificationRecords = [NSMutableArray array];
}

-(void) _onAppMessage:(AppMessageIdentifier) appMessageId
{
    if (nil != _appMessageHandleBlock)
    {
        _appMessageHandleBlock(appMessageId);
    }
}

-(void) _onServerNotification:(ServerNotificationIdentifier) serverNotificationId
{
    if (nil != _serverNotificationHandleBlock)
    {
        _serverNotificationHandleBlock(serverNotificationId);
    }
}

-(BusinessStatusIdentifier) identifier
{
    return _businessStatusId;
}

-(NSString*) identifierString
{
    return [NSString stringWithFormat:@"%d", _businessStatusId];
}

-(NSNumber*) identifierObject
{
    return [NSNumber numberWithInt:_businessStatusId];
}

-(NSArray*) appMessageRecords
{
    return [NSArray arrayWithArray:_appMessageRecords];
}

-(NSArray*) serverNotificationRecords
{
    return [NSArray arrayWithArray:_serverNotificationRecords];
}

-(AppMessageIdentifier) latestAppMessageRecord
{
    NSNumber* oRecord = (NSNumber*)[_appMessageRecords lastObject];
    return oRecord.intValue;
}

-(ServerNotificationIdentifier) latestServerNotificationRecord
{
    NSNumber* oRecord = (NSNumber*)[_serverNotificationRecords lastObject];
    return oRecord.intValue;
}

-(void) clearAllRecords
{
    @synchronized(_appMessageRecords)
    {
        [_appMessageRecords removeAllObjects];
    }
    
    @synchronized(_serverNotificationRecords)
    {
        [_serverNotificationRecords removeAllObjects];        
    }
}

@end
