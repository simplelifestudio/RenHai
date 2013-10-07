//
//  CBStringUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBStringUtils.h"

@implementation CBStringUtils

+(BOOL) isSubstringIncluded:(NSString*) parentString subString:(NSString*)subString
{
    BOOL flag = NO;
    
    if (nil != subString && nil != parentString)
    {
        NSRange range = [parentString rangeOfString:subString];
        if (0 < range.length)
        {
            flag = YES;
        }
    }
    
    return flag;
}

+(NSString*) trimString:(NSString*) string
{
    if (nil != string)
    {
        unichar spaceChar = ' ';
        unichar nbspChar = 160;
        
        unichar c;
        while (0 < string.length)
        {
            c = [string characterAtIndex:0];
            if (c == spaceChar || c == nbspChar)
            {
                string = [string substringFromIndex:1];
            }
            else
            {
                break;
            }
        }
        
        while (1 < string.length)
        {
            c = [string characterAtIndex:string.length - 1];
            if (c == spaceChar || c == nbspChar)
            {
                string = [string substringToIndex:string.length - 1];
            }
            else
            {
                break;
            }
        }
    }
    
    return string;
}

+(NSString*) replaceSubString:(NSString*) newSubString oldSubString:(NSString*)oldSubString string:(NSString*) string
{
    NSAssert(nil != string, @"Illegal main string.");
    NSAssert(nil != oldSubString, @"Illegal old sub string.");
    NSAssert(nil != newSubString, @"Illegal new sub string.");
    
    NSMutableString* mainStringCopy = [NSMutableString stringWithString:string];
    NSRange range = [mainStringCopy rangeOfString:oldSubString];
    if (0 < range.length)
    {
        [mainStringCopy replaceCharactersInRange:range withString:newSubString];
    }
    
    return mainStringCopy;
}

+(NSString*) parseByte2HexString:(Byte *) bytes
{
    NSMutableString* hexStr = [[NSMutableString alloc]init];
    int i = 0;
    if(bytes)
    {
        while (bytes[i] != '\0')
        {
            NSString* hexByte = [NSString stringWithFormat:@"%x", bytes[i] & 0xff];///16进制数
            if([hexByte length] == 1)
            {
                [hexStr appendFormat:@"0%@", hexByte];
            }
            else
            {
                [hexStr appendFormat:@"%@", hexByte];
            }
            
            i++;
        }
    }
    
    DDLogVerbose(@"bytes 的16进制数为:%@", hexStr);
    return hexStr;
}

+(NSString*) parseByteArray2HexString:(Byte[]) bytes
{
    NSMutableString* hexStr = [[NSMutableString alloc]init];
    int i = 0;
    if(bytes)
    {
        while (bytes[i] != '\0')
        {
            NSString *hexByte = [NSString stringWithFormat:@"%x", bytes[i] & 0xff];///16进制数
            if([hexByte length] == 1)
            {
                [hexStr appendFormat:@"0%@", hexByte];
            }
            else
            {
                [hexStr appendFormat:@"%@", hexByte];
            }
            
            i++;
        }
    }
    
    DDLogVerbose(@"bytes 的16进制数为:%@",hexStr);
    return hexStr;
}

+(NSString*) randomString:(NSUInteger)length
{
    char data[length];
    for (int x = 0; x < length ; x++)
    {
        bool isChar = arc4random_uniform(2);
        if (isChar)
        {
            data[x] = (char)('A' + arc4random_uniform(26));
        }
        else
        {
            data[x] = (char)('0' + (arc4random_uniform(10)));
        }
    }
    NSString* randomStr = [[NSString alloc] initWithBytes:data length:length encoding:NSUTF8StringEncoding];
    
    return randomStr;
}

@end
