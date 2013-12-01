//
//  UIDevice+CBDeviceExtends.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#define IS_IPAD1_OR_2_OR_MINI ([UIDevice currentResolution] == UIDevice_iPhoneStandardRes)
#define IS_IPHONE4_OR_4S ([UIDevice currentResolution] == UIDevice_iPhoneHiRes)
#define IS_IPHONE5 ([UIDevice currentResolution] == UIDevice_iPhoneTallerHiRes)

enum
{
    // iPhone 1,3,3GS 标准分辨率(320x480px)
    UIDevice_iPhoneStandardRes      = 1,
    // iPhone 4,4S 高清分辨率(640x960px)
    UIDevice_iPhoneHiRes            = 2,
    // iPhone 5 高清分辨率(640x1136px)
    UIDevice_iPhoneTallerHiRes      = 3,
    // iPad 1,2 标准分辨率(1024x768px)
    UIDevice_iPadStandardRes        = 4,
    // iPad 3 High Resolution(2048x1536px)
    UIDevice_iPadHiRes              = 5
};

typedef NSUInteger UIDeviceResolution;

@interface UIDevice (CBDeviceExtends)
{
    
}

+ (UIDeviceResolution) currentResolution;

+ (BOOL)isRunningOniPhone5;

+ (BOOL)isRunningOniPhone;

+ (BOOL)isRunningOniOS6AndLater;

+ (NSString*) deviceModel;

+ (NSString*) deviceSimpleModel;

+ (NSString*) osVersion;

+ (BOOL) isJailed;

+ (NSString*) identifierForVendor;

@end
