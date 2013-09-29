//
//  RHDeviceCard.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>
#import <CoreLocation/CoreLocation.h>

#import "CBJSONable.h"

typedef enum
{
    NotJailed = 0,
    Jailed = 1
}
RHDeviceJailStatus;

@interface RHDeviceCard : NSObject <CBJSONable>

@property (nonatomic) NSInteger deviceCardId;
@property (nonatomic, strong) NSDate* registerTime;
@property (nonatomic, strong) NSString* deviceModel;
@property (nonatomic, strong) NSString* osVersion;
@property (nonatomic, strong) NSString* appVersion;
@property (nonatomic) RHDeviceJailStatus isJailed;

@end
