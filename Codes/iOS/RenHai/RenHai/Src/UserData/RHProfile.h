//
//  RHProfile.h
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHInterestCard.h"
#import "RHImpressCard.h"

typedef enum
{
    ServiceStatusInvalid = 0,
    ServiceStatusNormal = 1,
    ServiceStatusBanned = 2
}
RHProfileServieStatus;

@interface RHProfile : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic) NSInteger profileId;
@property (nonatomic) RHProfileServieStatus serviceStatus;
@property (nonatomic, strong) NSDate* unbanDate;
@property (nonatomic) BOOL active;
@property (nonatomic, strong) NSDate* createTime;
@property (nonatomic, strong) RHInterestCard* interestCard;
@property (nonatomic, strong) RHImpressCard* impressCard;

+(NSString*) serviceStatusString:(RHProfileServieStatus) status;

@end
