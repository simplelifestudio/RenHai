//
//  RHDevice.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHDeviceCard.h"
#import "RHProfile.h"

@interface RHDevice : NSObject <CBJSONable>

@property (nonatomic, strong) RHDeviceCard* deviceCard;
@property (nonatomic, strong) NSMutableArray* profileList;

-(RHProfile*) currentProfile;

@end