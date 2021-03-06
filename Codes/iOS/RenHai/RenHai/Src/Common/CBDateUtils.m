//
//  CBDateUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBDateUtils.h"

@implementation CBDateUtils

+(NSString*) dateString:(NSTimeZone*) timeZone andFormat:(NSString*) format andDate:(NSDate*) date
{
    NSDateFormatter *formatter =  [[NSDateFormatter alloc] init];
    [formatter setDateFormat:format];
    
    timeZone = (nil != timeZone) ? timeZone : [NSTimeZone defaultTimeZone];
    [formatter setTimeZone:timeZone];
    
    NSString *localTime = [formatter stringFromDate:date];
    
    return localTime;
}

+(NSString*) dateStringInLocalTimeZoneWithFormat:(NSString*) format andDate:(NSDate*) date
{
    NSTimeZone *localTimeZone = [NSTimeZone localTimeZone];
    return [CBDateUtils dateString: localTimeZone andFormat:format andDate: date];
}

+(NSString*) dateStringInLocalTimeZoneWithStandardFormat:(NSDate*) date
{
    NSTimeZone *localTimeZone = [NSTimeZone localTimeZone];
    return [CBDateUtils dateString:localTimeZone andFormat:STANDARD_DATE_TIME_FORMAT andDate:date];
}

+(NSDate *) dateFromStringWithFormat:(NSString *)dateString andFormat:(NSString *) formatString
{    
	NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setDateFormat: formatString];
	NSDate *date = [dateFormatter dateFromString:dateString];
    
	return date;
}

+(NSArray*) lastThreeDays
{
    NSMutableArray* array = [NSMutableArray arrayWithCapacity:3];
    
    NSTimeInterval secondsPerDay = 24 * 60 * 60;
    
    NSDate *today = [NSDate date];
    
    NSDate *yesterday = [[NSDate alloc]
                         initWithTimeIntervalSinceNow:-secondsPerDay];

    NSDate *theDayBefore = [[NSDate alloc]
                            initWithTimeIntervalSinceNow:-secondsPerDay * 2];

    [array addObject:theDayBefore];
    [array addObject:yesterday];
    [array addObject:today];
    
    return array;
}

+(NSArray*) lastThreeDayStrings:(NSArray*) lastThreeDays formatString:(NSString*) formatString
{
    [CBAppUtils assert:(nil != formatString && 0 < formatString.length) logFormatString:@"Illegal date format string"];
    [CBAppUtils assert:(nil != lastThreeDays && 3 == lastThreeDays.count) logFormatString:@"Illegal last three days array"];
    
    NSMutableArray* dayStrs = [NSMutableArray array];
    
    for (NSDate* day in lastThreeDays)
    {
        NSString* dayStr = [CBDateUtils dateStringInLocalTimeZoneWithFormat:formatString andDate:day];
        [dayStrs addObject:dayStr];
    }
    
    return dayStrs;
}

+(NSString*) shortDateString:(NSDate*) date
{
    NSString* dateStr = [CBDateUtils dateStringInLocalTimeZoneWithFormat:SHORT_DATE_FORMAT andDate:date];
    return dateStr;
}

+(NSInteger) dayDiffBetweenTwoDays:(NSDate*) dateA dateB:(NSDate*) dateB
{
    [CBAppUtils assert:(nil != dateA && nil != dateB) logFormatString:@"Nil date parameter"];
    
    NSInteger dayIntDiff = 0;
    NSTimeInterval timeDiff = [dateA timeIntervalSinceDate:dateB];
    if (0 == timeDiff)
    {
        dayIntDiff = 0;
    }
    else
    {
        NSTimeInterval secondsPerDay = 24 * 60 * 60;
        timeDiff = (timeDiff / secondsPerDay);
        dayIntDiff = (NSInteger)timeDiff;
        if (0 < timeDiff - dayIntDiff)
        {
            dayIntDiff += 1;
        }
    }

    return dayIntDiff;
}

+(NSString*) timeStringDHMSWithMilliseconds:(long long) milliseconds
{
    long days = milliseconds / (1000 * 60 * 60 * 24);
    long hours = (milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (milliseconds % (1000 * 60)) / 1000;
    
    NSString* str = [NSString stringWithFormat:@"%ld-%ld:%ld:%ld", days, hours, minutes, seconds];
    return str;
}

+(NSString*) timeStringHMSWithMilliseconds:(long long) milliseconds
{
    long hours = (milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (milliseconds % (1000 * 60)) / 1000;
    
    NSString* str = [NSString stringWithFormat:@"%ld:%ld:%ld", hours, minutes, seconds];
    return str;
}

+(NSDate*) targetDateFromDate:(NSDate*) sourceDate sourceTimeZone:(NSTimeZone*) sourceTimeZone targetTimeZone:(NSTimeZone*) targetTimeZone;
{
    NSInteger sourceIntervalFromGMT = [sourceTimeZone secondsFromGMT];
    NSInteger targetIntervalFromGMT = [targetTimeZone secondsFromGMT];
    
    NSInteger diff = targetIntervalFromGMT - sourceIntervalFromGMT;
    
    NSDate* targetDate = [sourceDate dateByAddingTimeInterval:diff];
    
    return targetDate;
}

+(BOOL) isDateInLocalTimeZoneDuringPeriod:(NSDate*) date beginTime:(NSDate*) beginTime endTime:(NSDate*) endTime
{
    BOOL flag = NO;
    
    if (nil != date)
    {
        if (nil != beginTime)
        {
            NSComparisonResult result = [date compare:beginTime];
            if (result == NSOrderedSame || result == NSOrderedDescending)
            {
                if (nil != endTime)
                {
                    result = [date compare:endTime];
                    if (result == NSOrderedAscending || result == NSOrderedSame)
                    {
                        flag = YES;
                    }
                }
                else
                {
                    flag = YES;
                }
            }
        }
    }
    
    return flag;
}

- (id)init
{
    return nil;
    // Disable object initialization.
    //    self = [super init];
    //    if (self) 
    //    {
    //        // Initialization code here.
    //    }
    //    
    //    return self;
}

@end
