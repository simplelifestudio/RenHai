//
//  ChatMessageViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-8.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatMessageViewController_iPhone.h"

#import "GUIModule.h"
#import "CommunicationModule.h"
#import "UserDataModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#define DELAY_POP 0.5f
#define DELAY_STATUS_UPDATE 0.15f
#define ANIMATION_POP 0.5f
#define ANIMATION_DISMISS 0.5f

@interface ChatMessageViewController_iPhone ()
{
    GUIModule* _guiModule;
    CommunicationModule* _commModule;
    UserDataModule* _userDataModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    volatile BOOL _isViewControllerVisible;
}

@end

@implementation ChatMessageViewController_iPhone

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

- (void)viewDidAppear:(BOOL)animated
{
    _isViewControllerVisible = YES;
    
    [super viewDidAppear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    _isViewControllerVisible = NO;
    
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void) popConnectView:(UIViewController*) presentingViewController animated:(BOOL) animated
{
    if (!_isViewControllerVisible)
    {
        [NSThread sleepForTimeInterval:DELAY_POP];
        
        UIViewController* rootVC = [CBUIUtils getRootController];
        
        presentingViewController = (nil != presentingViewController) ? presentingViewController : rootVC;
        
        __block CALayer* keyLayer = [UIApplication sharedApplication].keyWindow.layer;
        
        if (animated)
        {
            CATransition* transition = [CATransition animation];
            transition.duration = ANIMATION_POP;
            transition.type = kCATransitionMoveIn;
            transition.subtype = kCATransitionFromTop;
            [keyLayer addAnimation:transition forKey:kCATransition];
            transition.removedOnCompletion = YES;
        }
        
        [presentingViewController presentViewController:self animated:NO completion:^(){
            
        }];
    }
}

- (void) dismissConnectView:(BOOL) animated
{
    [CBAppUtils asyncProcessInMainThread:^(){
        if (_isViewControllerVisible)
        {

            
            [self _resetInstance];
        }
    }];
}

#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    UserDataModule* userDataModule = [UserDataModule sharedInstance];
    RHBusinessSession* businessSession = userDataModule.businessSession;
    NSUInteger messageCount = businessSession.chatMessages.count;
    
    return messageCount;
}

#pragma mark - JSMessagesViewDelegate

- (void)didSendText:(NSString *)text
{
    UserDataModule* userDataModule = [UserDataModule sharedInstance];
    RHBusinessSession* businessSession = userDataModule.businessSession;

    [businessSession addChatMessageWithSender:ChatMessageSender_Self andText:text];
        
    if((businessSession.chatMessageCount - 1) % 2)
    {
        [JSMessageSoundEffect playMessageSentSound];
        
//        [self.subtitles addObject:arc4random_uniform(100) % 2 ? kSubtitleCook : kSubtitleWoz];
    }
    else
    {
        [JSMessageSoundEffect playMessageReceivedSound];
        
//        [self.subtitles addObject:kSubtitleJobs];
    }
    
    [self finishSend];
    [self scrollToBottomAnimated:YES];
}

- (JSBubbleMessageType)messageTypeForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return (indexPath.row % 2) ? JSBubbleMessageTypeIncoming : JSBubbleMessageTypeOutgoing;
}

- (UIImageView *)bubbleImageViewWithType:(JSBubbleMessageType)type
                       forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.row % 2)
    {
        return [JSBubbleImageViewFactory bubbleImageViewForType:type
                                                          color:[UIColor js_bubbleLightGrayColor]];
    }
    
    return [JSBubbleImageViewFactory bubbleImageViewForType:type
                                                      color:[UIColor js_bubbleBlueColor]];
}

- (JSMessagesViewTimestampPolicy)timestampPolicy
{
    return JSMessagesViewTimestampPolicyEveryThree;
}

- (JSMessagesViewAvatarPolicy)avatarPolicy
{
    return JSMessagesViewAvatarPolicyAll;
}

- (JSMessagesViewSubtitlePolicy)subtitlePolicy
{
    return JSMessagesViewSubtitlePolicyAll;
}

- (JSMessageInputViewStyle)inputViewStyle
{
    return JSMessageInputViewStyleFlat;
}

- (void)configureCell:(JSBubbleMessageCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if([cell messageType] == JSBubbleMessageTypeOutgoing)
    {
//        [cell.bubbleView setTextColor:[UIColor whiteColor]];
        
        if([cell.bubbleView.textView respondsToSelector:@selector(linkTextAttributes)])
        {
            NSMutableDictionary *attrs = [cell.bubbleView.textView.linkTextAttributes mutableCopy];
            [attrs setValue:[UIColor blueColor] forKey:UITextAttributeTextColor];
            
            cell.bubbleView.textView.linkTextAttributes = attrs;
        }
    }
    
    if(cell.timestampLabel)
    {
        cell.timestampLabel.textColor = [UIColor lightGrayColor];
        cell.timestampLabel.shadowOffset = CGSizeZero;
    }
    
    if(cell.subtitleLabel)
    {
        cell.subtitleLabel.textColor = [UIColor lightGrayColor];
    }
}

//  *** Required if using `JSMessagesViewTimestampPolicyCustom`
//
//  - (BOOL)hasTimestampForRowAtIndexPath:(NSIndexPath *)indexPath
//

//  *** Implement to use a custom send button
//
//  The button's frame is set automatically for you
//
//  - (UIButton *)sendButtonForInputView
//

//  *** Implement to prevent auto-scrolling when message is added
//
- (BOOL)shouldPreventScrollToBottomWhileUserScrolling
{
    return YES;
}

#pragma mark - JSMessagesViewDataSource

- (NSString *)textForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RHChatMessage* message = [self _chatMessageAtIndex:indexPath.row];
    
    return message.text;
}

- (NSDate *)timestampForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RHChatMessage* message = [self _chatMessageAtIndex:indexPath.row];
    
    return message.timpStamp;
}

- (UIImageView *)avatarImageViewForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    NSString *subtitle = [self.subtitles objectAtIndex:indexPath.row];
//    UIImage *image = [self.avatars objectForKey:subtitle];
//    return [[UIImageView alloc] initWithImage:image];
#warning TODO
    return nil;
}

- (NSString *)subtitleForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    return [self.subtitles objectAtIndex:indexPath.row];
#warning TODO
    return nil;
}

#pragma mark - ChatWizardPage

#pragma mark - Private Methods

- (void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    
    self.delegate = self;
    self.dataSource = self;
    
    [super viewDidLoad];
    
    self.title = NSLocalizedString(@"ChatMessage_Title", nil);
    self.messageInputView.textView.placeHolder = NSLocalizedString(@"ChatMessage_NewMessage", nil);
    
    _isViewControllerVisible = NO;
}

- (RHChatMessage*) _chatMessageAtIndex:(NSUInteger) index
{
    UserDataModule* userDataModule = [UserDataModule sharedInstance];
    RHBusinessSession* businessSession = userDataModule.businessSession;
    RHChatMessage* message = [businessSession chatMessageAtIndex:index];
    return message;
}

- (void) _remoteSendChatMessage:(RHChatMessage*) message
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        UserDataModule* userDataModule = [UserDataModule sharedInstance];
        RHBusinessSession* businessSession = userDataModule.businessSession;
        RHDevice* device = userDataModule.device;
        
        NSString* businessSessionId = businessSession.businessSessionId;
        RHBusinessType businessType = businessSession.businessType;
        BusinessSessionRequestType businessSessionRequestType = BusinessSessionRequestType_ChatMessage;
        
        CommunicationModule* commModule = [CommunicationModule sharedInstance];
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:businessSessionRequestType device:device info:nil];
        [commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){

            }
            failureCompletionBlock:^(){

            }
            afterCompletionBlock:^(){
    
            }
         ];
    }];
}

- (void)_resetInstance
{
    _isViewControllerVisible = NO;
}

@end
