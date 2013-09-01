//
//  CBStringUtils.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#define STR_COLON @":"
#define STR_COLON_FULLWIDTH_1 @"︰"
#define STR_COLON_FULLWIDTH_2 @"："

#define STR_BRACKET_LEFT_1 @"["
#define STR_BRACKET_RIGHT_1 @"]"

#define STR_BRACKET_LEFT_2 @"【"
#define STR_BRACKET_RIGHT_2 @"】"

#define STR_RETURN @"\r"
#define STR_NEWLINE @"\n"

#define STR_SPACE @" "

@interface CBStringUtils : NSObject

+(BOOL) isSubstringIncluded:(NSString*) parentString subString:(NSString*)subString;
+(NSString*) trimString:(NSString*) string;
+(NSString*) replaceSubString:(NSString*) newSubString oldSubString:(NSString*)oldSubString string:(NSString*) string;

+(NSString*) parseByte2HexString:(Byte *) bytes;
+(NSString*) parseByteArray2HexString:(Byte[]) bytes;

@end
