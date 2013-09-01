//
//  CBNotificationListenable.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol CBNotificationListenable <NSObject>

-(void) listenNotifications;

-(void) unlistenNotifications;

-(void) onNotificationReceived:(NSNotification*) notification;

@end
