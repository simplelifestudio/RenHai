//
//  RHDataUtils.m
//  RenHai
//
//  Created by DENG KE on 13-11-25.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHDataUtils.h"

@implementation RHDataUtils

+(NSArray*) sortedLabelList:(NSArray*) labelList sortKey:(NSString*) sortKey ascending:(BOOL) ascending
{
    NSSortDescriptor* sortDescriptor = sortDescriptor = [[NSSortDescriptor alloc] initWithKey:sortKey
                                                                                    ascending:ascending];
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    NSArray *sortedArray = [[NSMutableArray alloc] initWithArray:[labelList sortedArrayUsingDescriptors:sortDescriptors]];
    
    return sortedArray;
}

-(id) init
{
    return nil;
}

@end
