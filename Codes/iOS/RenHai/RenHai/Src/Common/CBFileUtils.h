//
//  CBFileUtils.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CBFileUtils : NSObject

+(NSData*) dataFromFile:(NSString*) filePath;
+(BOOL) dataToFile:(NSData*) data filePath:(NSString*) filePath;
+(NSArray*) filesInDirectory:(NSString*) directoryPath fileExtendName:(NSString*) extendName;
+(NSArray*) directories:(NSString*) directoryPath;

+(NSDate*) fileLastUpdateTime:(NSString*) fileFullPath;
+(BOOL) deleteFile:(NSString*) fileFullPath;
+(BOOL) createFile:(NSString*) fileFullPath content:(id) content;
+(BOOL) createDirectory:(NSString*) dirFullPath;
+(BOOL) isFileExists:(NSString*) fileFullPath;
+(BOOL) isDirectoryExists:(NSString*) dirFullPath;
+(BOOL) deleteDirectory:(NSString*) dirFullPath;
+(BOOL) copyFile:(NSString*) sourcePath targetPath:(NSString*) targetPath;

@end
