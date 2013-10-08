//
//  RHServerInterestLabelList.m
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHServerInterestLabelList.h"

#import "CBJSONUtils.h"
#import "CBDateUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_CURRENT @"interestLabelList.current"
#define SERIALIZE_KEY_STARTTIME @"interestLabelList.historyStartTime"
#define SERIALIZE_KEY_ENDTIME @"interestLabelList.historyEndTime"

@interface RHServerInterestLabelList()
{
    
}

@property (nonatomic, readwrite) NSUInteger current;
@property (nonatomic, readwrite) NSDate* historyStartTime;
@property (nonatomic, readwrite) NSDate* historyEndTime;

@end

@implementation RHServerInterestLabelList

@synthesize current = _current;
@synthesize historyStartTime = _historyStartTime;
@synthesize historyEndTime = _historyEndTime;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {

    }
    
    return self;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oCurrent = [dic objectForKey:MESSAGE_KEY_CURRENT];
        if (nil != oCurrent)
        {
            _current = ([NSNull null] != oCurrent) ? ((NSNumber*)oCurrent).integerValue : 0;
        }
        
        id oHistory = [dic objectForKey:MESSAGE_KEY_HISTORY];
        if (nil != oHistory)
        {
            id oHistoryStartTime = [dic objectForKey:MESSAGE_KEY_STARTTIME];
            if (nil != oHistoryStartTime)
            {
                _historyStartTime = ([NSNull null] != oHistoryStartTime) ? [CBDateUtils dateFromStringWithFormat:oHistoryStartTime andFormat:FULL_DATE_TIME_FORMAT] : nil;
            }
            else
            {
                _historyStartTime = nil;
            }
            
            id oHistoryEndTime = [dic objectForKey:MESSAGE_KEY_ENDTIME];
            if (nil != oHistoryEndTime)
            {
                _historyEndTime = ([NSNull null] != oHistoryEndTime) ? [CBDateUtils dateFromStringWithFormat:oHistoryEndTime andFormat:FULL_DATE_TIME_FORMAT] : nil;
            }
            else
            {
                _historyEndTime = nil;
            }
        }
        else
        {
            _historyStartTime = nil;
            _historyEndTime = nil;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    id oCurrent = [self _getOCurrent];
    [dic setObject:oCurrent forKey:MESSAGE_KEY_CURRENT];
    
    NSMutableDictionary* historyDic = [NSMutableDictionary dictionary];
    id oHistoryStartTime = [self _getOHistoryStartTime];
    if (nil != oHistoryStartTime)
    {
        [historyDic setObject:oHistoryStartTime forKey:MESSAGE_KEY_STARTTIME];
    }
    id oHistoryEndTime = [self _getOHistoryEndTime];
    if (nil != oHistoryEndTime)
    {
        [historyDic setObject:oHistoryEndTime forKey:MESSAGE_KEY_ENDTIME];
    }
    if (0 < historyDic.count)
    {
        [dic setObject:historyDic forKey:MESSAGE_KEY_HISTORY];
    }
    
    return dic;
}

-(NSString*) toJSONString
{
    NSDictionary* dic = [self toJSONObject];
    NSString* str = [CBJSONUtils toJSONString:dic];
    
    return str;
}

#pragma mark - NSCoding

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init])
    {
        _current = [aDecoder decodeIntegerForKey:SERIALIZE_KEY_CURRENT];
        _historyStartTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_STARTTIME];
        _historyEndTime = [aDecoder decodeObjectForKey:SERIALIZE_KEY_ENDTIME];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInteger:_current forKey:SERIALIZE_KEY_CURRENT];
    [aCoder encodeObject:_historyStartTime forKey:SERIALIZE_KEY_STARTTIME];
    [aCoder encodeObject:_historyEndTime forKey:SERIALIZE_KEY_ENDTIME];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHServerInterestLabelList*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

#pragma mark - Private Methods

-(id) _getOCurrent
{
    return [NSNumber numberWithInteger:_current];
}

-(id) _getOHistoryStartTime
{
    id oStartTime = nil;
    
    if (nil == _historyStartTime)
    {
        oStartTime = [NSNull null];
    }
    else
    {
        oStartTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:FULL_DATE_TIME_FORMAT andDate:_historyStartTime];;
    }
    
    return oStartTime;
}

-(id) _getOHistoryEndTime
{
    id oEndTime = nil;
    
    if (nil == _historyEndTime)
    {
        oEndTime = [NSNull null];
    }
    else
    {
        oEndTime = [CBDateUtils dateStringInLocalTimeZoneWithFormat:FULL_DATE_TIME_FORMAT andDate:_historyEndTime];;
    }
    
    return oEndTime;
}

@end
