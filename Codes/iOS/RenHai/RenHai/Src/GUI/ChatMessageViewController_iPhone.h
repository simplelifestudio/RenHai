//
//  ChatMessageViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-12-8.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "JSMessagesViewController.h"

#import "ChatWizardController.h"

@interface ChatMessageViewController_iPhone : JSMessagesViewController <JSMessagesViewDelegate, JSMessagesViewDataSource>

- (void) popConnectView:(UIViewController*) presentingViewController animated:(BOOL) animated;
- (void) dismissConnectView:(BOOL) animated;

@end
