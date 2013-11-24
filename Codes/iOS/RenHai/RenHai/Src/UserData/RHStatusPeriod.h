//
//  RHStatusPeriod.h
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RHMessage.h"

@interface RHStatusPeriod : NSObject <CBJSONable>

@property (strong, nonatomic, readonly) NSTimeZone* timeZone;
@property (nonatomic, readonly) NSDate* beginTime;
@property (nonatomic, readonly) NSDate* endTime;

-(NSTimeZone*) localTimeZone;
-(NSDate*) localBeginTime;
-(NSDate*) localEndTime;

-(NSString*) localBeginTimeString;
-(NSString*) localEndTimeString;

-(BOOL) isInPeriod;

@end
