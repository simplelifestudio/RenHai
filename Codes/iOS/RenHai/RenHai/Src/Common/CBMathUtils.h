//
//  CBMathUtils.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CBMathUtils : NSObject

+(NSString*) readableStringFromBytesSize:(unsigned long long) bytesSize;

+(NSArray*) splitIntegerByUnit:(NSInteger) intVal array:(NSMutableArray*) unitVals reverseOrder:(BOOL) reverseOrder;

@end
