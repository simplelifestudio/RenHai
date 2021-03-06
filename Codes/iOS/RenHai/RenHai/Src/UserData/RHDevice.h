//
//  RHDevice.h
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHDeviceCard.h"
#import "RHProfile.h"

@interface RHDevice : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic) NSInteger deviceId;
@property (nonatomic, strong) NSString* deviceSn;

@property (nonatomic, strong) RHDeviceCard* deviceCard;
@property (nonatomic, strong) RHProfile* profile;

-(NSString*) shortDeviceSn;

@end
