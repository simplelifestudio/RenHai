//
//  CBRoundProgressLayer.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-8.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBRoundProgressLayer.h"

@implementation CBRoundProgressLayer

@synthesize progress;
@synthesize startAngle;
@synthesize tintColor;
@synthesize trackColor;

- (id) initWithLayer:(id)layer
{
    self = [super initWithLayer:layer];
    if(self)
    {
        // Typically, the method is called to create the Presentation layer.
        // We must copy the parameters to look the same.
        if([layer isKindOfClass:[CBRoundProgressLayer class]])
        {
            CBRoundProgressLayer *otherLayer = layer;
            self.progress = otherLayer.progress;
            self.startAngle = otherLayer.startAngle;
            self.tintColor = otherLayer.tintColor;
            self.trackColor = otherLayer.trackColor;
        }
    }
    
    return self;
}

+ (BOOL) needsDisplayForKey:(NSString *)key
{
    if([key isEqualToString:@"progress"])
        return YES;
    else
        return [super needsDisplayForKey:key];
}

- (void) drawInContext:(CGContextRef)context
{
    CGFloat radius = MIN(self.bounds.size.width, self.bounds.size.height)/2.0;
    CGPoint center = {self.bounds.size.width/2.0, self.bounds.size.height/2.0};
    
    // Background circle
    CGRect circleRect = {center.x-radius, center.y-radius, radius*2.0, radius*2.0};
    CGContextAddEllipseInRect(context, circleRect);
    
    CGContextSetFillColorWithColor(context, trackColor.CGColor);
    CGContextFillPath(context);
    
    // Elapsed arc
    CGContextAddArc(context, center.x, center.y, radius, startAngle, startAngle + progress*2.0*M_PI, 0);
    CGContextAddLineToPoint(context, center.x, center.y);
    CGContextClosePath(context);
    
    CGContextSetFillColorWithColor(context, tintColor.CGColor);
    CGContextFillPath(context);
}

@end
