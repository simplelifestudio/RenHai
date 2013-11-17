//
//  OCBasicTest.m
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "OCBasicTest.h"

#import "CBDateUtils.h"

@implementation OCBasicTest

-(void) testTimeZone
{
//    final TimeZone zone = TimeZone.getTimeZone("GMT+8");
    NSTimeZone* tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT+0800"];
    GHTestLog(@"tz is :%@", tz);
    
    NSDate* sourceDate = [NSDate date];
    NSTimeZone* sourceTimeZone = [NSTimeZone timeZoneWithName:@"GMT+0700"];
    
    NSTimeZone* targetTimeZone = [NSTimeZone timeZoneWithName:@"GMT+0800"];
    
    NSDate* targetDate = [CBDateUtils targetDateFromDate:sourceDate sourceTimeZone:sourceTimeZone targetTimeZone:targetTimeZone];
    
    GHTestLog(@"sourceDate: %@", [CBDateUtils dateString:sourceTimeZone andFormat:STANDARD_DATE_TIME_FORMAT andDate:sourceDate]);
    GHTestLog(@"targetDate: %@", [CBDateUtils dateString:targetTimeZone andFormat:STANDARD_DATE_TIME_FORMAT andDate:targetDate]);
    
}

@end
