//
//  CBSecurityUtils.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CBSecurityUtils : NSObject

+(NSString*) encryptByDESAndEncodeByBase64:(NSString*)plainText key:(NSString*)key;
+(NSString*) decryptByDESAndDecodeByBase64:(NSString*)cipherText key:(NSString*)key;

@end
