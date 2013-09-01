//
//  CBSecurityUtils.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBSecurityUtils.h"

#import <CommonCrypto/CommonCrypto.h>
#import "GTMBase64.h"

@implementation CBSecurityUtils

static Byte iv[8] = {1, 9, 8, 9, 0, 6, 0, 4};

+(NSString*) encryptByDESAndEncodeByBase64:(NSString*)plainText key:(NSString*)key
{
    NSString* ciphertext = nil;
    
    NSData* data = [plainText dataUsingEncoding:NSUTF8StringEncoding];
    NSUInteger bufferSize = ([data length] + kCCKeySizeDES) & ~(kCCKeySizeDES - 1);
    char buffer[bufferSize];
    memset(buffer, 0, sizeof(buffer));
    size_t bufferNumBytes;
    
    CCCryptorStatus cryptStatus = CCCrypt(kCCEncrypt,
                                         kCCAlgorithmDES,
                                         kCCOptionPKCS7Padding,
                                         [key UTF8String],
                                         kCCKeySizeDES,
                                         iv,
                                         [data bytes],
                                         [data length],
                                         buffer,
                                         bufferSize,
                                         &bufferNumBytes);
    
    if (cryptStatus == kCCSuccess)
    {
        NSData* data = [NSData dataWithBytes:buffer length:(NSUInteger)bufferNumBytes];
        ciphertext = [GTMBase64 stringByEncodingData:data];
    }
    return ciphertext;    
}

+(NSString*) decryptByDESAndDecodeByBase64:(NSString*)cipherText key:(NSString*)key
{
    NSData* data = [GTMBase64 decodeString:cipherText];
    
    NSUInteger bufferSize = ([data length] + kCCKeySizeDES) & ~(kCCKeySizeDES -1);
    char buffer[bufferSize];
    memset(buffer, 0, sizeof(buffer));
    size_t bufferNumBytes;
    
    CCCryptorStatus cryptStatus = CCCrypt(kCCDecrypt,
                                         kCCAlgorithmDES,
                                         kCCOptionPKCS7Padding,
                                         [key UTF8String],
                                         kCCKeySizeDES,
                                         iv,
                                         [data bytes],
                                         [data length],
                                         buffer,
                                         bufferSize,
                                         &bufferNumBytes);
    
    NSString* plainText = nil;
    if (cryptStatus == kCCSuccess)
    {
        NSData* plainData = [NSData dataWithBytes:buffer length:(NSUInteger)bufferNumBytes];
        plainText = [[NSString alloc] initWithData:plainData encoding:NSUTF8StringEncoding];
    }
    return plainText;
}

@end
