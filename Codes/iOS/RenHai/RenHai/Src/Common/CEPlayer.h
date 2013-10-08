//
//  CEPlayer.h
//  RenHai
//
//  Created by DENG KE on 13-9-8.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@class CEPlayer;

@protocol CEPlayerDelegate <NSObject>

- (void) player:(CEPlayer *)player didReachPosition:(float)position;
- (void) playerDidStop:(CEPlayer *)player;

@end

@interface CEPlayer : NSObject

- (void) play;
- (void) pause;
@property (assign) float position;  // 0..1
@property (retain) id <CEPlayerDelegate> delegate;

@end
