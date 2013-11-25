//
//  ChatConfirmViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatConfirmViewController_iPhone.h"

#import "CBDateUtils.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"
#import "AppDataModule.h"
#import "BusinessStatusModule.h"

#import "RHCollectionLabelCell_iPhone.h"

#import "ImpressLabelsHeaderView_iPhone.h"

#define SECTION_COUNT 2

#define SECTION_INDEX_ASSESSES 0
#define SECTION_ASSESSES_ITEMCOUNT 6

#define SECTION_INDEX_LABELS 1
#define SECTION_IMPRESSES_ITEMCOUNT 6

#define COUNTDOWN_SECONDS 600

#define DELAY_DECIDE 0.5f
#define DELAY_UNBINDSESSION 1.0f
#define DELAY_AGREECHAT 1.0f
#define DELAY_REJECTCHAT 1.0f

@interface ChatConfirmViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    volatile BOOL _selfAgreeChatFlag;
    volatile BOOL _selfRejectChatFlag;
    volatile BOOL _selfUnbindSessionFlag;
    
    volatile BOOL _partnerAgreeChatFlag;
    volatile BOOL _partnerRejectChatFlag;
    volatile BOOL _partnerLostFlag;
    
    volatile BOOL _isDeciding;
}

@end

@implementation ChatConfirmViewController_iPhone

@synthesize viewTitleLabel = _viewTitleLabel;
@synthesize collectionView = _collectionView;

@synthesize selfStatusLabel = _selfStatusLabel;
@synthesize partnerStatusLabel = _partnerStatusLabel;

@synthesize countLabel = _countLabel;

@synthesize agreeChatButton = _agreeChatButton;
@synthesize rejectChatButton = _rejectChatButton;

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

- (void)viewWillAppear:(BOOL)animated
{
    DDLogInfo(@"#####ChatConfirm: viewWillAppear");
    
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:YES];
    
    [self resetPage];
}

- (void)viewWillDisappear:(BOOL)animated
{
    DDLogInfo(@"#####ChatConfirm: viewWillDisappear");
    
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    DDLogInfo(@"#####ChatConfirm: viewDidAppear");
    
    [super viewDidAppear:animated];
    
    [self pageWillLoad];
}

- (void)viewDidDisappear:(BOOL)animated
{
    DDLogInfo(@"#####ChatConfirm: viewDidDisappear");
    
    [super viewDidDisappear:animated];
    
    [self pageWillUnload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - UICollectionViewDelegate

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    RHBusinessSession* businessSession = _userDataModule.businessSession;
    RHDevice* device = businessSession.device;
    RHProfile* profile = device.profile;
    RHImpressCard* impressCard = profile.impressCard;
    
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            return SECTION_ASSESSES_ITEMCOUNT;
        }
        case SECTION_INDEX_LABELS:
        {
            NSArray* impressLabelList = [impressCard topImpressLabelList:SECTION_IMPRESSES_ITEMCOUNT];
            return  impressLabelList.count;
        }
        default:
        {
            return 0;
        }
    }
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL forIndexPath:indexPath];
    
    RHBusinessSession* businessSession = _userDataModule.businessSession;
    RHDevice* device = businessSession.device;
    RHProfile* profile = device.profile;
    RHImpressCard* impressCard = profile.impressCard;
    
    NSString* labelName = nil;
    NSInteger labelCount = -1;
    NSString* labelCountStr = nil;
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            NSArray* assessLabelList = impressCard.assessLabelList;
         
            switch (row)
            {
                case 0:
                {
                }
                case 1:
                {
                }
                case 2:
                {
                    RHImpressLabel* assessLabel = assessLabelList[row];
                    labelName = [RHImpressLabel assessLabelName:assessLabel.labelName];
                    labelCount = assessLabel.assessedCount;
                    break;
                }
                case 3:
                {
                    labelName = NSLocalizedString(@"Impress_ChatTotalCount", nil);
                    labelCount = impressCard.chatTotalCount;
                    break;
                }
                case 4:
                {
                    labelName = NSLocalizedString(@"Impress_ChatTotalDuration", nil);
                    labelCount = impressCard.chatTotalDuration;
                    labelCountStr = [CBDateUtils timeStringWithMilliseconds:labelCount];
                    break;
                }
                case 5:
                {
                    labelName = NSLocalizedString(@"Impress_ChatLossCount", nil);
                    labelCount = impressCard.chatLossCount;
                    break;
                }
                default:
                {
                    break;
                }
            }
            
            break;
        }
        case SECTION_INDEX_LABELS:
        {
            NSArray* impressLabelList = [impressCard topImpressLabelList:SECTION_IMPRESSES_ITEMCOUNT];
            
            RHImpressLabel* impressLabel = nil;
            if (0 < impressLabelList.count && row < impressLabelList.count)
            {
                impressLabel = impressLabelList[row];
                
                labelName = impressLabel.labelName;
                labelCount = impressLabel.assessedCount;
            }
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    cell.userInteractionEnabled = NO;
    cell.textField.text = labelName;
    if (0 <= labelCount)
    {
        if (nil == labelCountStr)
        {
            labelCountStr = [NSString stringWithFormat:@"%d", labelCount];
        }
        
        cell.countLabel.text = labelCountStr;
    }
    else
    {
        cell.countLabel.text = @"";
    }
    
    return cell;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return SECTION_COUNT;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView* reusableView = nil;
    
    NSInteger section = indexPath.section;
    
    if ([kind isEqualToString:UICollectionElementKindSectionHeader])
    {
        ImpressLabelsHeaderView_iPhone* headerView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
        
        switch (section)
        {
            case SECTION_INDEX_ASSESSES:
            {
                headerView.titleLabel.text = NSLocalizedString(@"Impress_Assesses", nil);
                break;
            }
            case SECTION_INDEX_LABELS:
            {
                headerView.titleLabel.text = NSLocalizedString(@"Impress_Labels", nil);
                break;
            }
            default:
            {
                break;
            }
        }
        
        reusableView = headerView;
    }
    
    
    return reusableView;
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    UserDataModule* m = [UserDataModule sharedInstance];
    RHBusinessSession* session = m.businessSession;
    RHMatchedCondition* matchedCondition = session.matchedCondition;
    RHInterestLabel* interestLabel = matchedCondition.interestLabel;
    NSString* str = [NSString stringWithFormat:NSLocalizedString(@"ChatConfirm_Title", nil), interestLabel.labelName];
    _viewTitleLabel.text = str;
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatConfirm_SelfStatus_Undecided", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Undecided", nil);
    
    _selfAgreeChatFlag = NO;
    _selfRejectChatFlag = NO;
    _selfUnbindSessionFlag = NO;
    
    _partnerAgreeChatFlag = NO;
    _partnerRejectChatFlag = NO;
    _partnerLostFlag = NO;
    
    _isDeciding = NO;
    
    _agreeChatButton.hidden = NO;
    _rejectChatButton.hidden = NO;
    
    [self _setCountdownSeconds:COUNTDOWN_SECONDS];
    
    [_collectionView reloadData];
}

-(void) pageWillLoad
{
    [self _clockStart];
    
    [self _checkIsOthersideAlreadyDecided];
}

-(void) pageWillUnload
{
    [self _clockCancel];
}

-(void) onOthersideAgreed
{
    _partnerAgreeChatFlag = YES;
    
    _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Agreed", nil);
    
    if (_selfAgreeChatFlag)
    {
        [self _moveToChatVideoView];
    }
}

-(void) onOthersideRejected
{
    _partnerRejectChatFlag = YES;
    
    _agreeChatButton.hidden = YES;
    _rejectChatButton.hidden = YES;
    
    _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Rejected", nil);
    
    [self _clockCancel];
    
    [self _remoteUnbindSession];
}

-(void) onOthersideLost
{
    _partnerLostFlag = YES;
    
    _agreeChatButton.hidden = YES;
    _rejectChatButton.hidden = YES;
    
    _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Lost", nil);
    
    [self _clockCancel];
    
    [self _remoteUnbindSession];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    
    [self _setupCollectionView];
    
    [_agreeChatButton setTitle:NSLocalizedString(@"ChatConfirm_Action_Agree", nil) forState:UIControlStateNormal];
    [_rejectChatButton setTitle:NSLocalizedString(@"ChatConfirm_Action_Reject", nil) forState:UIControlStateNormal];
}

-(void)_setupCollectionView
{
    _collectionView.dataSource = self;
    _collectionView.delegate = self;
    
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    [_collectionView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSLABELSHEADERVIEW bundle:nil];
    [_collectionView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
}

- (void) _clockStart
{
    [self _clockCancel];
    
    NSTimeInterval interval = 1.0;
    _timer = [NSTimer timerWithTimeInterval:interval target:self selector:@selector(_clockClick) userInfo:nil repeats:YES];
    NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
    [currentRunLoop addTimer:_timer forMode:NSDefaultRunLoopMode];
    [_timer fire];
}

- (void) _clockClick
{
    if (0 == _countdownSeconds)
    {
        [self _clockCancel];
        
        [self _clockCountFinished];
    }
    else
    {
        _countdownSeconds--;
    }
    
    [CBAppUtils asyncProcessInMainThread:^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _countdownSeconds]];
    }];
}

- (void) _clockCancel
{
    if (nil != _timer)
    {
        [_timer invalidate];
    }
}

- (void) _clockCountFinished
{
    if (!_selfAgreeChatFlag)
    {
        [self _remoteAgreeChat];
    }
}

- (void) _setCountdownSeconds:(NSUInteger) seconds
{
    if (0 < seconds)
    {
        _countdownSeconds = seconds;
        [_countLabel setText:[NSString stringWithFormat:@"%d", _countdownSeconds]];
    }
}

-(void)_remoteAgreeChat
{
    _agreeChatButton.hidden = YES;
    _rejectChatButton.hidden = YES;
    _selfStatusLabel.text = NSLocalizedString(@"ChatConfirm_SelfStatus_Agreed", nil);
    
    [self _clockCancel];
    
    _isDeciding = YES;
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        [NSThread sleepForTimeInterval:DELAY_AGREECHAT];
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        NSString* businessSessionId = businessSession.businessSessionId;
        RHBusinessType businessType = businessSession.businessType;
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:BusinessSessionRequestType_AgreeChat device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _selfAgreeChatFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_AgreeChat];

                if (_partnerAgreeChatFlag)
                {
                  [self _moveToChatVideoView];
                }
            }
            failureCompletionBlock:^(){
                _selfAgreeChatFlag = NO;
            }
            afterCompletionBlock:^(){
                _isDeciding = NO;
            }
         ];
    }];
}

- (void) _remoteRejectChat
{
    _agreeChatButton.hidden = YES;
    _rejectChatButton.hidden = YES;
    _selfStatusLabel.text = NSLocalizedString(@"ChatConfirm_SelfStatus_Rejected", nil);
    
    _isDeciding = YES;
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
    
        [NSThread sleepForTimeInterval:DELAY_REJECTCHAT];
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        NSString* businessSessionId = businessSession.businessSessionId;
        RHBusinessType businessType = businessSession.businessType;
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:BusinessSessionRequestType_RejectChat device:device info:nil];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _selfRejectChatFlag = YES;
                [_statusModule recordAppMessage:AppMessageIdentifier_RejectChat];
                [self _moveToChatWaitView];
            }
            failureCompletionBlock:^(){
                _selfRejectChatFlag = NO;
            }
            afterCompletionBlock:^(){
                _isDeciding = NO;
            }
         ];
    }];
}

- (void) _remoteUnbindSession
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        while (_isDeciding)
        {
            [NSThread sleepForTimeInterval:DELAY_DECIDE];
        }
        
        if (!_selfRejectChatFlag)
        {
            [NSThread sleepForTimeInterval:DELAY_UNBINDSESSION];
            
            RHBusinessSession* businessSession = _userDataModule.businessSession;
            NSString* businessSessionId = businessSession.businessSessionId;
            RHBusinessType businessType = businessSession.businessType;
            
            RHDevice* device = _userDataModule.device;
            
            RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:BusinessSessionRequestType_UnbindSession device:device info:nil];
            
            [_commModule businessSessionRequest:requestMessage
                successCompletionBlock:^(){
                    _selfUnbindSessionFlag = YES;
                    [_statusModule recordAppMessage:AppMessageIdentifier_UnbindSession];
                    [self _moveToChatWaitView];
                }
                failureCompletionBlock:^(){
                    _selfUnbindSessionFlag = NO;
                }
                afterCompletionBlock:nil
             ];
        }
    }];
}

- (void) _moveToChatVideoView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatVideo];
    }];
}

-(void) _moveToChatWaitView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatWait];
    }];
}

-(void) _checkIsOthersideAlreadyDecided
{
    BusinessStatusModule* statusModule = [BusinessStatusModule sharedInstance];
    BusinessStatus* currentStatus = statusModule.currentBusinessStatus;
    ServerNotificationIdentifier serverNotificationId = currentStatus.latestServerNotificationRecord;
    if (serverNotificationId == ServerNotificationIdentifier_OthersideAgreeChat)
    {
        [self onOthersideAgreed];
    }
    else if (serverNotificationId == ServerNotificationIdentifier_OthersideRejectChat)
    {
        [self onOthersideRejected];
    }
    else if (serverNotificationId == ServerNotificationIdentifier_OthersideLost)
    {
        [self onOthersideLost];
    }
}

#pragma mark - IBActions

- (IBAction)didPressAgreeChatButton:(id)sender
{
    [self _remoteAgreeChat];
}

- (IBAction)didPressRejectChatButton:(id)sender
{
    [self _remoteRejectChat];
}

@end
