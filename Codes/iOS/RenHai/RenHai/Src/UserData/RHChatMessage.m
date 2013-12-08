//
//  RHChatMessage.m
//  RenHai
//
//  Created by DENG KE on 13-12-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHChatMessage.h"

@interface RHChatMessage()
{
    
}

@property (nonatomic) ChatMessageSender sender;
@property (strong, nonatomic) NSDate* timpStamp;
@property (strong, nonatomic) NSString* text;

@end

@implementation RHChatMessage

-(id) initWithSender:(ChatMessageSender) sender andText:(NSString*) text
{
    if (self = [super init])
    {
        _sender = sender;
        _text = text;
        _timpStamp = [NSDate date];
    }
    
    return self;
}

-(NSString*) read
{
    if (!_hasRead)
    {
        _hasRead = YES;
    }
    
    return _text;
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHChatMessage*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

@end
