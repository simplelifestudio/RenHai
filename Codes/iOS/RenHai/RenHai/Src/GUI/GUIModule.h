//
//  GUIModule.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBModuleAbstractImpl.h"
#import "CBSharedInstance.h"

#import "CBHUDAgent.h"

@interface GUIModule : CBModuleAbstractImpl <CBSharedInstance, UIApplicationDelegate>

@property (strong, nonatomic) CBHUDAgent* HUDAgent;

-(void) showHUD:(NSString*) majorStauts minorStatus:(NSString*) minorStatus delay:(NSInteger)seconds;
-(void) showHUD:(NSString*) status delay:(NSInteger) seconds;

- (BOOL) isNetworkActivityIndicatorVisible;
- (void) setNetworkActivityIndicatorVisible:(BOOL) flag;

@end
