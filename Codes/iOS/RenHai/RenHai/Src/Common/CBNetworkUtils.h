//
//  CBNetworkUtils.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

//#import "Reachability.h"

@interface CBNetworkUtils : NSObject

+(NSString*) hostNameInWiFi;

+(BOOL) isWiFiEnabled;
+(BOOL) is3GEnabled;

+(BOOL) isInternetConnectable:(NSString*) hostName;

@end
