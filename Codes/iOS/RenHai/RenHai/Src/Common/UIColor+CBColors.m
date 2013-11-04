//
//  UIColor+CBColors.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
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

+(UIColor*) PowderBlue
{
    return [UIColor colorWithHexCode:@"B0E0E6"];
}

+(UIColor*) PaleTurquoise
{
    return [UIColor colorWithHexCode:@"AFEEEE"];
}

+(UIColor*) DarkTurquoise
{
    return [UIColor colorWithHexCode:@"00CED1"];
}

+(UIColor*) MediumTurquoise
{
    return [UIColor colorWithHexCode:@"48D1CC"];
}

+(UIColor*) Turquoise
{
    return [UIColor colorWithHexCode:@"40E0D0"];
}

+(UIColor*) Cyan
{
    return [UIColor colorWithHexCode:@"00FFFF"];
}

+(UIColor*) LightCyan
{
    return [UIColor colorWithHexCode:@"E0FFFF"];
}

+(UIColor*) CadetBlue
{
    return [UIColor colorWithHexCode:@"5F9EA0"];
}

+(UIColor*) MediumAquamarine
{
    return [UIColor colorWithHexCode:@"66CDAA"];
}

+(UIColor*) Aquamarine
{
    return [UIColor colorWithHexCode:@"7FFFD4"];
}

+(UIColor*) DarkGreen
{
    return [UIColor colorWithHexCode:@"006400"];
}

+(UIColor*) DarkOliveGreen
{
    return [UIColor colorWithHexCode:@"556B2F"];
}

+(UIColor*) DarkSeaGreen
{
    return [UIColor colorWithHexCode:@"8FBC8F"];
}

+(UIColor*) SeaGreen
{
    return [UIColor colorWithHexCode:@"2E8B57"];
}

+(UIColor*) MediumSeaGreen
{
    return [UIColor colorWithHexCode:@"3CB371"];
}

+(UIColor*) LightSeaGreen
{
    return [UIColor colorWithHexCode:@"20B2AA"];
}

+(UIColor*) PaleGreen
{
    return [UIColor colorWithHexCode:@"98FB98"];
}

+(UIColor*) SpringGreen
{
    return [UIColor colorWithHexCode:@"00FF7F"];
}

+(UIColor*) LawnGreen
{
    return [UIColor colorWithHexCode:@"7CFC00"];
}

+(UIColor*) Green
{
    return [UIColor colorWithHexCode:@"00FF00"];
}

+(UIColor*) Chartreuse
{
    return [UIColor colorWithHexCode:@"7FFF00"];
}

+(UIColor*) MedSpringGreen
{
    return [UIColor colorWithHexCode:@"00FA9A"];
}

+(UIColor*) GreenYellow
{
    return [UIColor colorWithHexCode:@"ADFF2F"];
}

+(UIColor*) LimeGreen
{
    return [UIColor colorWithHexCode:@"32CD32"];
}

+(UIColor*) YellowGreen
{
    return [UIColor colorWithHexCode:@"9ACD32"];
}

+(UIColor*) ForestGreen
{
    return [UIColor colorWithHexCode:@"228B22"];
}

+(UIColor*) OliveDrab
{
    return [UIColor colorWithHexCode:@"6B8E23"];
}

+(UIColor*) DarkKhaki
{
    return [UIColor colorWithHexCode:@"BDB76B"];
}

+(UIColor*) PaleGoldenrod
{
    return [UIColor colorWithHexCode:@"EEE8AA"];
}

+(UIColor*) LtGoldenrodYello
{
    return [UIColor colorWithHexCode:@"FAFAD2"];
}

+(UIColor*) LightYellow
{
    return [UIColor colorWithHexCode:@"FFFFE0"];
}

+(UIColor*) Yellow
{
    return [UIColor colorWithHexCode:@"FFFF00"];
}

+(UIColor*) Gold
{
    return [UIColor colorWithHexCode:@"FFD700"];
}

+(UIColor*) LightGoldenrod
{
    return [UIColor colorWithHexCode:@"EEDD82"];
}

+(UIColor*) Goldenrod
{
    return [UIColor colorWithHexCode:@"DAA520"];
}

+(UIColor*) DarkGoldenrod
{
    return [UIColor colorWithHexCode:@"B8860B"];
}

+(UIColor*) RosyBrown
{
    return [UIColor colorWithHexCode:@"BC8F8F"];
}

+(UIColor*) IndianRed
{
    return [UIColor colorWithHexCode:@"CD5C5C"];
}

+(UIColor*) SaddleBrown
{
    return [UIColor colorWithHexCode:@"8B4513"];
}

+(UIColor*) Sienna
{
    return [UIColor colorWithHexCode:@"A0522D"];
}

+(UIColor*) Peru
{
    return [UIColor colorWithHexCode:@"CD853F"];
}

+(UIColor*) Burlywood
{
    return [UIColor colorWithHexCode:@"DEB887"];
}

+(UIColor*) Beige
{
    return [UIColor colorWithHexCode:@"F5F5DC"];
}

+(UIColor*) Wheat
{
    return [UIColor colorWithHexCode:@"F5DEB3"];
}

+(UIColor*) SandyBrown
{
    return [UIColor colorWithHexCode:@"F4A460"];
}

+(UIColor*) Tan
{
    return [UIColor colorWithHexCode:@"D2B48C"];
}

+(UIColor*) Chocolate
{
    return [UIColor colorWithHexCode:@"D2691E"];
}

+(UIColor*) Firebrick
{
    return [UIColor colorWithHexCode:@"B22222"];
}

+(UIColor*) Brown
{
    return [UIColor colorWithHexCode:@"A52A2A"];
}

+(UIColor*) DarkSalmon
{
    return [UIColor colorWithHexCode:@"E9967A"];
}

+(UIColor*) Salmon
{
    return [UIColor colorWithHexCode:@"FA8072"];
}

+(UIColor*) LightSalmon
{
    return [UIColor colorWithHexCode:@"FFA07A"];
}

+(UIColor*) Orange
{
    return [UIColor colorWithHexCode:@"FFA500"];
}

+(UIColor*) DarkOrange
{
    return [UIColor colorWithHexCode:@"FF8C00"];
}

+(UIColor*) Coral
{
    return [UIColor colorWithHexCode:@"FF7F50"];
}

@end
