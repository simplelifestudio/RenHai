//
//  RHChatMessage.h
//  RenHai
//
//  Created by DENG KE on 13-12-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum
{
    ChatMessageSender_Self = 0,
    ChatMessageSender_Partner
}
ChatMessageSender;

@interface RHChatMessage : NSObject <NSCopying>

@property (nonatomic, readonly) ChatMessageSender sender;
@property (strong, nonatomic, readonly) NSDate* timpStamp;
@property (strong, nonatomic, readonly) NSString* text;
@property (nonatomic) BOOL hasRead;

-(id) initWithSender:(ChatMessageSender) sender andText:(NSString*) text;

-(NSString*) read;

@end
