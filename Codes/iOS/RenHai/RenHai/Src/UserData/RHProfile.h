//
//  RHProfile.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHInterestCard.h"
#import "RHImpressCard.h"

@interface RHProfile : NSObject <CBJSONable>

@property (nonatomic) NSInteger profileId;
@property (nonatomic, strong) RHInterestCard* interestCard;
@property (nonatomic, strong) RHImpressCard* impressCard;
@property (nonatomic, strong) NSDate* lastActivityTime; // This field might not be necessary in App.
@property (nonatomic, strong) NSDate* createTime;

@end
