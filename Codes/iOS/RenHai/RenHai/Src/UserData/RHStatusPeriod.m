//
//  RHStatusPeriod.m
//  RenHai
//
//  Created by DENG KE on 13-11-17.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHStatusPeriod.h"

#import "CBJSONUtils.h"
#import "CBDateUtils.h"

@interface RHStatusPeriod()
{
    
}

@property (strong, nonatomic) NSTimeZone* timeZone;
@property (nonatomic) NSDate* beginTime;
@property (nonatomic) NSDate* endTime;

@end

@implementation RHStatusPeriod

@synthesize beginTime = _beginTime;
@synthesize endTime = _endTime;
@synthesize timeZone = _timeZone;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        [self _setupInstance];
    }
    
    return self;
}

-(NSTimeZone*) localTimeZone
{
    NSTimeZone* zone = [NSTimeZone defaultTimeZone];
    
    return zone;
}

-(NSDate*) localBeginTime
{
    NSDate* localTime = nil;
    
    if (nil != _beginTime)
    {
        NSTimeZone* targetTimeZone = [self localTimeZone];
        localTime = [CBDateUtils targetDateFromDate:_beginTime sourceTimeZone:_timeZone targetTimeZone:targetTimeZone];
    }
    
    return localTime;
}

-(NSDate*) localEndTime
{
    NSDate* localTime = nil;
    
    if (nil != _endTime)
    {
        NSTimeZone* targetTimeZone = [self localTimeZone];
        localTime = [CBDateUtils targetDateFromDate:_endTime sourceTimeZone:_timeZone targetTimeZone:targetTimeZone];
    }
    
    return localTime;
}

-(NSString*) localBeginTimeString
{
    NSDate* localBeginTime = [self localBeginTime];
    NSString* localBeginTimeStr = [CBDateUtils dateStringInLocalTimeZoneWithFormat:SHORT_DATE_TIME_FORMAT andDate:localBeginTime];
    return localBeginTimeStr;
}

-(NSString*) localEndTimeString
{
    NSDate* localEndTime = [self localEndTime];
    NSString* localEndTimeStr = [CBDateUtils dateStringInLocalTimeZoneWithFormat:SHORT_DATE_TIME_FORMAT andDate:localEndTime];
    return localEndTimeStr;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        NSString* timeZoneStr = [dic objectForKey:MESSAGE_KEY_TIMEZONE];
        _timeZone = [NSTimeZone timeZoneWithAbbreviation:timeZoneStr];

        NSString* beginTimeStr = [dic objectForKey:MESSAGE_KEY_BEGINTIME];
        if (nil != beginTimeStr)
        {
            _beginTime = [CBDateUtils dateFromStringWithFormat:beginTimeStr andFormat:STANDARD_DATE_TIME_FORMAT];
        }

        NSString* endTimeStr = [dic objectForKey:MESSAGE_KEY_ENDTIME];
        if (nil != endTimeStr)
        {
            _endTime = [CBDateUtils dateFromStringWithFormat:endTimeStr andFormat:STANDARD_DATE_TIME_FORMAT];
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    if (nil != _timeZone)
    {
        NSString* timeZoneStr = _timeZone.name;
        [dic setObject:timeZoneStr forKey:MESSAGE_KEY_TIMEZONE];
    }

    if (nil != _beginTime)
    {
        NSString* beginTimeStr = [CBDateUtils dateString:_timeZone andFormat:STANDARD_DATE_TIME_FORMAT andDate:_beginTime];
        [dic setObject:beginTimeStr forKey:MESSAGE_KEY_BEGINTIME];
    }
    
    if (nil != _endTime)
    {
        NSString* endTimeStr = [CBDateUtils dateString:_timeZone andFormat:STANDARD_DATE_TIME_FORMAT andDate:_endTime];
        [dic setObject:endTimeStr forKey:MESSAGE_KEY_ENDTIME];
    }
    
    return dic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    
}

@end
