//
//  CBJSONUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-3.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "CBJSONUtils.h"

@implementation CBJSONUtils

+(NSString*) toJSONString:(NSDictionary*) dic
{
    NSString* jsonString = nil;
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&error];
    if (!jsonData)
    {
        DDLogWarn(@"JSON error: %@", error.localizedDescription);
    }
    else
    {
        jsonString = [[NSString alloc] initWithBytes:[jsonData bytes] length:[jsonData length] encoding:NSUTF8StringEncoding];
    }
    
    return jsonString;
}

+(NSDictionary*) toJSONObject:(NSString*) jsonString
{
    NSDictionary* dic = nil;
    
    if (nil != jsonString)
    {
        @try
        {
            NSData* jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
            
            NSError* error = nil;
            dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&error];
            
            if(nil == dic)
            {
                DDLogWarn(@"JSON error: %@", error.localizedDescription);
            }
        }
        @catch (NSException *exception)
        {
            DDLogWarn(@"Caught Exception: %@", exception.callStackSymbols);
        }
        @finally
        {
            
        }
    }

    return dic;
}

@end
