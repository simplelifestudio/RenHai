//
//  CBRoundProgressLayer.h
//  RenHai
//
//  Created by DENG KE on 13-9-8.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

@interface CBRoundProgressLayer : CALayer

@property (nonatomic, assign) float progress;

@property (nonatomic, assign) float startAngle;
@property (nonatomic, retain) UIColor *tintColor;
@property (nonatomic, retain) UIColor *trackColor;

@end
