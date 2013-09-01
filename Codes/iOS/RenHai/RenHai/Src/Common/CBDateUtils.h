//
//  CBDateUtils.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#define STANDARD_DATE_TIME_FORMAT @"yyyy-MM-dd HH:mm:ss"
#define STANDARD_DATE_FORMAT @"yyyy-MM-dd"
#define SHORT_DATE_FORMAT @"M-dd"

@interface CBDateUtils : NSObject

+(NSString*) dateString:(NSTimeZone*) timeZone andFormat:(NSString*) format andDate:(NSDate*) date;

+(NSString*) dateStringInLocalTimeZone:(NSString*) format andDate:(NSDate*) date;

+(NSString*) dateStringInLocalTimeZoneWithStandardFormat:(NSDate*) date;

+(NSDate *) dateFromStringWithFormat:(NSString *)dateString andFormat:(NSString *) formatString;

+(NSArray*) lastThreeDays;

+(NSArray*) lastThreeDayStrings:(NSArray*) lastThreeDays formatString:(NSString*) formatString;

+(NSString*) shortDateString:(NSDate*) date;

+(NSInteger) dayDiffBetweenTwoDays:(NSDate*) dateA dateB:(NSDate*) dateB;

@end
