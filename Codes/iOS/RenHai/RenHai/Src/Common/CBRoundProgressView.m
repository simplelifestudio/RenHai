//
//  CBRoundProgressView.m
//  RenHai
//
//  Created by DENG KE on 13-9-8.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBRoundProgressView.h"
#import "CBRoundProgressLayer.h"

@interface CBRoundProgressView ()
{
    BOOL _progressStarted;
}

- (void) _initIVars;

@end

@implementation CBRoundProgressView

+ (Class) layerClass
{
    return [CBRoundProgressLayer class];
}

@synthesize roundProgressViewDelegate = _roundProgressViewDelegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self _initIVars];
    }
    return self;
}

- (id) initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if(self)
    {
        [self _initIVars];
    }
    return self;
}

- (void) _initIVars
{
    _progressStarted = NO;
    
    self.backgroundColor = [UIColor clearColor];
    self.opaque = NO;
    self.tintColor = [UIColor colorWithRed:0.2 green:0.45 blue:0.8 alpha:1.0];
    self.trackColor = [UIColor whiteColor];
}

- (float) progress
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    return layer.progress;
}

- (void) setProgress:(float)progress
{
    BOOL growing = progress > self.progress;
    [self setProgress:progress animated:growing];
}

- (void) setProgress:(float)progress animated:(BOOL)animated
{
    // Coerce the value
    if(progress <= 0.0f)
    {
        progress = 0.0f;
        _progressStarted = NO;
    }
    else if(progress > 1.0f)
    {
        progress = 1.0f;
    }
    
    if (progress > 0.0f && progress < 1.0f && !_progressStarted)
    {
        _progressStarted = YES;
        [_roundProgressViewDelegate progressStarted];
    }
    
    // Apply to the layer
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    if(animated)
    {
        CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"progress"];
        animation.duration = 0.25;
        animation.fromValue = [NSNumber numberWithFloat:layer.progress];
        animation.toValue = [NSNumber numberWithFloat:progress];
        [layer addAnimation:animation forKey:@"progressAnimation"];
        
        layer.progress = progress;
    }
    else
    {
        layer.progress = progress;
        [layer setNeedsDisplay];
    }
    
    if (progress == 1.0f)
    {
        [NSTimer scheduledTimerWithTimeInterval:0.25f target:_roundProgressViewDelegate selector:@selector(progressFinished) userInfo:nil repeats:NO];
    }
}

- (UIColor *)tintColor
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    return layer.tintColor;
}

- (void) setTintColor:(UIColor *)tintColor
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    layer.tintColor = tintColor;
    [layer setNeedsDisplay];
}

- (UIColor *)trackColor
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    return layer.trackColor;
}

- (void) setTrackColor:(UIColor *)trackColor
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    layer.trackColor = trackColor;
    [layer setNeedsDisplay];
}

- (float) startAngle
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    return layer.startAngle;
}

- (void) setStartAngle:(float)startAngle
{
    CBRoundProgressLayer *layer = (CBRoundProgressLayer *)self.layer;
    layer.startAngle = startAngle;
    [layer setNeedsDisplay];
}

@end
