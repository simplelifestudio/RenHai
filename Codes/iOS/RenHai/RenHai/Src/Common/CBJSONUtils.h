//
//  CBJSONUtils.h
//  RenHai
//
//  Created by DENG KE on 13-9-3.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CBJSONUtils : NSObject

+(NSString*) toJSONString:(NSDictionary*) dic;
+(NSDictionary*) toJSONObject:(NSString*) jsonString;

@end
