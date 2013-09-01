//
//  UIColor+CBColors.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "UIColor+CBColors.h"

@implementation UIColor (CBColors)

// Thanks to http://stackoverflow.com/questions/3805177/how-to-convert-hex-rgb-color-codes-to-uicolor
+(UIColor*) colorWithHexCode:(NSString *)hexString
{
    NSString *cleanString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    if([cleanString length] == 3)
    {
        cleanString = [NSString stringWithFormat:@"%@%@%@%@%@%@",
                       [cleanString substringWithRange:NSMakeRange(0, 1)],[cleanString substringWithRange:NSMakeRange(0, 1)],
                       [cleanString substringWithRange:NSMakeRange(1, 1)],[cleanString substringWithRange:NSMakeRange(1, 1)],
                       [cleanString substringWithRange:NSMakeRange(2, 1)],[cleanString substringWithRange:NSMakeRange(2, 1)]];
    }
    
    if([cleanString length] == 6)
    {
        cleanString = [cleanString stringByAppendingString:@"ff"];
    }
    
    unsigned int baseValue;
    [[NSScanner scannerWithString:cleanString] scanHexInt:&baseValue];
    
    float red = ((baseValue >> 24) & 0xFF)/255.0f;
    float green = ((baseValue >> 16) & 0xFF)/255.0f;
    float blue = ((baseValue >> 8) & 0xFF)/255.0f;
    float alpha = ((baseValue >> 0) & 0xFF)/255.0f;
    
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

+(UIColor*) Snow
{
    return [UIColor colorWithHexCode:@"FFFAFA"];
}

+(UIColor*) GhostWhite
{
    return [UIColor colorWithHexCode:@"F8F8FF"];
}

+(UIColor*) WhiteSmoke
{
    return [UIColor colorWithHexCode:@"F5F5F5"];
}

+(UIColor*) Gainsboro
{
    return [UIColor colorWithHexCode:@"DCDCDC"];
}

+(UIColor*) FloralWhite
{
    return [UIColor colorWithHexCode:@"FFFAF0"];
}

+(UIColor*) OldLace
{
    return [UIColor colorWithHexCode:@"FDF5E6"];
}

+(UIColor*) Line
{
    return [UIColor colorWithHexCode:@"FAF0E6"];
}

+(UIColor*) AntiqueWhite
{
    return [UIColor colorWithHexCode:@"FAEBD7"];
}

+(UIColor*) PapayaWhip
{
    return [UIColor colorWithHexCode:@"FFEFD5"];
}

+(UIColor*) BlancedAlmond
{
    return [UIColor colorWithHexCode:@"FFEBCD"];
}

+(UIColor*) Bisque
{
    return [UIColor colorWithHexCode:@"FFE4C4"];
}

+(UIColor*) PeachPuff
{
    return [UIColor colorWithHexCode:@"FFDAB9"];
}

+(UIColor*) NavajoWhite
{
    return [UIColor colorWithHexCode:@"FFDEAD"];
}

+(UIColor*) Moccasin
{
    return [UIColor colorWithHexCode:@"FFE4B5"];
}

+(UIColor*) Cornsilk
{
    return [UIColor colorWithHexCode:@"FFF8DC"];
}

+(UIColor*) Ivory
{
    return [UIColor colorWithHexCode:@"FFFFF0"];
}

+(UIColor*) LemonChiffon
{
    return [UIColor colorWithHexCode:@"FFFACD"];
}

+(UIColor*) Seashell
{
    return [UIColor colorWithHexCode:@"FFF5EE"];
}

+(UIColor*) Honeydew
{
    return [UIColor colorWithHexCode:@"F0FFF0"];
}

+(UIColor*) MintCream
{
    return [UIColor colorWithHexCode:@"F5FFFA"];
}

+(UIColor*) Azure
{
    return [UIColor colorWithHexCode:@"F0FFFF"];
}

+(UIColor*) AliceBlue
{
    return [UIColor colorWithHexCode:@"F0F8FF"];
}

+(UIColor*) Lavender
{
    return [UIColor colorWithHexCode:@"E6E6FA"];
}

+(UIColor*) LavenderBlush
{
    return [UIColor colorWithHexCode:@"FFF0F5"];
}

+(UIColor*) MistyRose
{
    return [UIColor colorWithHexCode:@"FFE4E1"];
}

+(UIColor*) White
{
    return [UIColor colorWithHexCode:@"FFFFFF"];
}

+(UIColor*) Black
{
    return [UIColor colorWithHexCode:@"000000"];
}

+(UIColor*) DarkSlateGray
{
    return [UIColor colorWithHexCode:@"2F4F4F"];
}

+(UIColor*) DimGrey
{
    return [UIColor colorWithHexCode:@"696969"];
}

+(UIColor*) SlateGrey
{
    return [UIColor colorWithHexCode:@"708090"];
}

+(UIColor*) LightSlateGray
{
    return [UIColor colorWithHexCode:@"778899"];
}

+(UIColor*) Grey
{
    return [UIColor colorWithHexCode:@"BEBEBE"];
}

+(UIColor*) LightGray
{
    return [UIColor colorWithHexCode:@"D3D3D3"];
}

+(UIColor*) MidnightBlue
{
    return [UIColor colorWithHexCode:@"191970"];
}

+(UIColor*) NavyBlue
{
    return [UIColor colorWithHexCode:@"000080"];
}

+(UIColor*) CornflowerBlue
{
    return [UIColor colorWithHexCode:@"6495ED"];
}

+(UIColor*) DarkSlateBlue
{
    return [UIColor colorWithHexCode:@"483D8B"];
}

+(UIColor*) SlateBlue
{
    return [UIColor colorWithHexCode:@"6A5ACD"];
}

+(UIColor*) MediumSlateBlue
{
    return [UIColor colorWithHexCode:@"7B68EE"];
}

+(UIColor*) LightSlateBlue
{
    return [UIColor colorWithHexCode:@"8470FF"];
}

+(UIColor*) MediumBlue
{
    return [UIColor colorWithHexCode:@"0000CD"];
}

+(UIColor*) RoyalBlue
{
    return [UIColor colorWithHexCode:@"4169E1"];
}

+(UIColor*) Blue
{
    return [UIColor colorWithHexCode:@"0000FF"];
}

+(UIColor*) DodgerBlue
{
    return [UIColor colorWithHexCode:@"1E90FF"];
}

+(UIColor*) DeepSkyBlue
{
    return [UIColor colorWithHexCode:@"00BFFF"];
}

+(UIColor*) SkyBlue
{
    return [UIColor colorWithHexCode:@"87CEEB"];
}

+(UIColor*) LightSkyBlue
{
    return [UIColor colorWithHexCode:@"87CEFA"];
}

+(UIColor*) SteelBlue
{
    return [UIColor colorWithHexCode:@"4682B4"];
}

+(UIColor*) LightSteelBlue
{
    return [UIColor colorWithHexCode:@"B0C4DE"];
}

+(UIColor*) LightBlue
{
    return [UIColor colorWithHexCode:@"ADD8E6"];
}

@end
