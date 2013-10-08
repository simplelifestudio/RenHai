//
//  CBMathUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBMathUtils.h"

@implementation CBMathUtils

+ (NSString*) readableStringFromBytesSize:(unsigned long long) bytesSize
{
    float floatSize = bytesSize;
    
    if (bytesSize < 1023)
    {
        return([NSString stringWithFormat:@"%llu bytes", bytesSize]);
    }
    
    floatSize = floatSize / 1024;
    if (floatSize < 1023)
    {
        return([NSString stringWithFormat:@"%1.1f KB", floatSize]);
    }
    
    floatSize = floatSize / 1024;
    if (floatSize < 1023)
    {
        return([NSString stringWithFormat:@"%1.1f MB", floatSize]);
    }
    
    floatSize = floatSize / 1024;
    return([NSString stringWithFormat:@"%1.1f GB", floatSize]);
}

+(NSArray*) splitIntegerByUnit:(NSInteger) intVal array:(NSMutableArray*) unitVals
{
    if (nil == unitVals)
    {
        unitVals = [NSMutableArray array];
    }
    
    if (10 > intVal)
    {
        [unitVals insertObject:[NSNumber numberWithInteger:intVal] atIndex:0];
        
        return unitVals;
    }
    else
    {
        NSInteger unitVal = intVal % 10;
        [unitVals insertObject:[NSNumber numberWithInteger:unitVal] atIndex:0];
    }
    
    intVal = intVal / 10;
    [CBMathUtils splitIntegerByUnit:intVal array:unitVals];
    
    return unitVals;
}

@end
