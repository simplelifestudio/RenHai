//
//  CBHUDAgent.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "MBProgressHUD.h"

typedef void(^HUDDisplayBlock)(NSString* majorStauts, NSString* minorStatus, NSInteger seconds);

#define HUD_CENTER_SIZE CGSizeMake(135.f, 135.f)
#define HUD_NOTIFICATION_SIZE CGSizeMake(300, 40)
#define HUD_NOTIFICATION_YOFFSET 250

#define HUD_DISPLAY(x) usleep(x * 1000 * 1000);

@interface CBHUDAgent : NSObject

-(id) initWithUIView:(UIView*) view;

-(void) attachToView:(UIView*) view;
-(void) showHUD:(NSString*) majorStauts minorStatus:(NSString*) minorStatus delay:(NSInteger)seconds;

-(MBProgressHUD*) sharedHUD;

-(void) releaseResources;

@end
