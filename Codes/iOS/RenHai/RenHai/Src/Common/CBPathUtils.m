//
//  CBPathUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBPathUtils.h"

@implementation CBPathUtils

+(NSString*) documentsDirectoryPath
{
    NSArray* documentsPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* documentDirectory = [documentsPath objectAtIndex:0];
    
    return documentDirectory;
}

+(BOOL) createDirectoryWithFullPath:(NSString*) fullPath
{
    BOOL flag = NO;
    
    NSFileManager* fm = [NSFileManager defaultManager];
    NSError* error = nil;
    if ([fm fileExistsAtPath:fullPath])
    {
        flag = YES;
    }
    else
    {
        flag = [fm createDirectoryAtPath:fullPath withIntermediateDirectories:NO attributes:nil error:&error];
    }
    
    return flag;
}

@end
