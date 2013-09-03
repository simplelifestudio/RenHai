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

typedef enum{SERVICE_NORMAL = 0, SERVICE_FORBIDDEN} SERVICE_STATUS;

@interface RHDeviceCard : NSObject <CBJSONable>

@property (nonatomic) NSInteger deviceId;
@property (nonatomic, strong) NSString* deviceSn;
@property (nonatomic, strong) NSDate* registerTime;
@property (nonatomic) SERVICE_STATUS serviceStatus;
@property (nonatomic, strong) NSDate* forbiddenExpiredDate;
@property (nonatomic, strong) NSString* deviceModel;
@property (nonatomic, strong) NSString* osVersion;
@property (nonatomic, strong) NSString* appVersion;
//@property (nonatomic) CLLocationCoordinate2D location;
@property (nonatomic) BOOL isJailed;

@end
