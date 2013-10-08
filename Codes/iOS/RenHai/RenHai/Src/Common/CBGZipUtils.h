//
//  CBGZipUtils.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "zlib.h"

@interface CBGZipUtils : NSObject

+(NSData*) gzipData:(NSData*) pUncompressedData;
+(NSData*) uncompressZippedData:(NSData*) compressedData;

//+(NSString*) newZipFileWithFiles:(NSString*) zipFilePath zipFiles:(NSArray*) files;

@end
