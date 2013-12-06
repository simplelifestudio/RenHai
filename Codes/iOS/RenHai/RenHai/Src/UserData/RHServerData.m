//
//  RHServerData.m
//  RenHai
//
//  Created by DENG KE on 13-10-7.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHServerData.h"

#import "CBJSONUtils.h"

#import "RHMessage.h"

#define SERIALIZE_KEY_DEVICECOUNT @"server.devcieCount"
#define SERIALIZE_KEY_DEVICECAPACITY @"server.deviceCapacity"
#define SERIALIZE_KEY_INTERESTLABELLIST @"server.interestLabelList"

@interface RHServerData()
{
    
}

@property (nonatomic, strong) RHServerDeviceCount* deviceCount;
@property (nonatomic, strong) RHServerDeviceCapacity* deviceCapacity;
@property (nonatomic, strong) RHServerInterestLabelList* interestLabelList;

@end

@implementation RHServerData

@synthesize deviceCount = _deviceCount;
@synthesize deviceCapacity = _deviceCapacity;
@synthesize interestLabelList = _interestLabelList;

#pragma mark - Public Methods

-(id) init
{
    if (self = [super init])
    {
        _deviceCount = [[RHServerDeviceCount alloc] init];
        _deviceCapacity = [[RHServerDeviceCapacity alloc] init];
        _interestLabelList = [[RHServerInterestLabelList alloc] init];
    }
    
    return self;
}

#pragma mark - CBJSONable

-(void) fromJSONObject:(NSDictionary *)dic
{
    if (nil != dic)
    {
        id oDeviceCount = [dic objectForKey:MESSAGE_KEY_DEVICECOUNT];
        if (nil != oDeviceCount)
        {
            _deviceCount = [[RHServerDeviceCount alloc] init];
            [_deviceCount fromJSONObject:oDeviceCount];
        }
        else
        {
            _deviceCount = nil;
        }

        id oDeviceCapacity = [dic objectForKey:MESSAGE_KEY_DEVICECAPACITY];
        if (nil != oDeviceCapacity)
        {
            _deviceCapacity = [[RHServerDeviceCapacity alloc] init];
            [_deviceCapacity fromJSONObject:oDeviceCapacity];
        }
        else
        {
            _deviceCapacity = nil;
        }
        
        id oInterestLabelList = [dic objectForKey:MESSAGE_KEY_INTERESTLABELLIST];
        if (nil != oInterestLabelList)
        {
            _interestLabelList = [[RHServerInterestLabelList alloc] init];
            [_interestLabelList fromJSONObject:oInterestLabelList];
        }
        else
        {
            _interestLabelList = nil;
        }
    }
}

-(NSDictionary*) toJSONObject
{
    NSMutableDictionary* dic = [NSMutableDictionary dictionary];
    
    NSDictionary* deviceCountDic = [_deviceCount toJSONObject];
    [dic setObject:deviceCountDic forKey:MESSAGE_KEY_DEVICECOUNT];
    
    NSDictionary* deviceCapacityDic = [_deviceCapacity toJSONObject];
    [dic setObject:deviceCapacityDic forKey:MESSAGE_KEY_DEVICECAPACITY];
    
    NSDictionary* interestLabelListDic = [_interestLabelList toJSONObject];
    [dic setObject:interestLabelListDic forKey:MESSAGE_KEY_INTERESTLABELLIST];
    
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
        _deviceCount = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICECOUNT];
        _deviceCapacity = [aDecoder decodeObjectForKey:SERIALIZE_KEY_DEVICECAPACITY];
        _interestLabelList = [aDecoder decodeObjectForKey:SERIALIZE_KEY_INTERESTLABELLIST];
    }
    
    return self;
}

-(void) encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeObject:_deviceCount forKey:SERIALIZE_KEY_DEVICECOUNT];
    [aCoder encodeObject:_deviceCapacity forKey:SERIALIZE_KEY_DEVICECAPACITY];
    [aCoder encodeObject:_interestLabelList forKey:SERIALIZE_KEY_INTERESTLABELLIST];
}

#pragma mark - NSCopying

-(id) copyWithZone:(struct _NSZone *)zone
{
    NSData* data = [NSKeyedArchiver archivedDataWithRootObject:self];
    return (RHServerData*)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

#pragma mark - NSMutableCopying

-(id) mutableCopyWithZone:(struct _NSZone *)zone
{
    return [self copyWithZone:zone];
}

#pragma mark - Private Methods

@end
