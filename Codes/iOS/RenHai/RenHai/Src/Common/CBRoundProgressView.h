//
//  CBRoundProgressView.h
//  RenHai
//
//  Created by DENG KE on 13-9-8.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol CBRoundProgressViewDelegate <NSObject>

- (void) progressStarted;
- (void) progressFinished;

@end


@interface CBRoundProgressView : UIView <UIAppearanceContainer>

@property (nonatomic, assign) float progress;   // 0 .. 1
- (void) setProgress:(float)progress animated:(BOOL)animated;

@property (nonatomic, assign) float startAngle; // 0..2π 
@property (nonatomic, retain) UIColor *tintColor UI_APPEARANCE_SELECTOR;
@property (nonatomic, retain) UIColor *trackColor UI_APPEARANCE_SELECTOR;

@property (nonatomic, strong) id<CBRoundProgressViewDelegate> roundProgressViewDelegate;

@end
