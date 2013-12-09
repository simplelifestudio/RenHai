//
//  ChatMessageSendView_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-12-8.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChatMessageSendDelegate <NSObject>

-(void) onSendMessage:(NSString*) text;

@end

@interface ChatMessageSendView_iPhone : UIView

@property (strong, nonatomic) id<ChatMessageSendDelegate> sendDelegate;

@property (weak, nonatomic) IBOutlet FUITextField *textField;
@property (weak, nonatomic) IBOutlet FUIButton *sendButton;

- (IBAction)didPressSendButton:(id)sender;

@end
