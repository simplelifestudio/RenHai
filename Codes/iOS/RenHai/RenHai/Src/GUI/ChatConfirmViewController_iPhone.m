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
#define SECTION_IMPRESSES_ITEMCOUNT_4 9
#define SECTION_IMPRESSES_ITEMCOUNT_3_5 6

#define COUNTDOWN_SECONDS 20

#define DELAY_DECIDE 0.5f
#define DELAY_UNBINDSESSION 1.0f
#define DELAY_AGREECHAT 1.0f
#define DELAY_REJECTCHAT 1.0f

typedef enum
{
    ChoiceType_Considering = 0,
    ChoiceType_Agreed,
    ChoiceType_Rejected,
    ChoiceType_Lost
}
ChoiceType;

typedef enum
{
    ChoiceProcessStatus_Standby = 0,
    ChoiceProcessStatus_Done
}
ChoiceProcessStatus;

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
    
    NSConditionLock* _choiceLock;
}

@end

@implementation ChatConfirmViewController_iPhone

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
    [super viewWillAppear:animated];
    
    [self resetPage];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self pageWillLoad];
}

- (void)viewDidDisappear:(BOOL)animated
{
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
    
    NSUInteger requireCount = 0;
    if (IS_IPHONE5)
    {
        requireCount = SECTION_IMPRESSES_ITEMCOUNT_4;
    }
    else
    {
        requireCount = SECTION_IMPRESSES_ITEMCOUNT_3_5;
    }
    
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            return SECTION_ASSESSES_ITEMCOUNT;
        }
        case SECTION_INDEX_LABELS:
        {
            NSArray* impressLabelList = [impressCard topImpressLabelList:requireCount];
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

    NSUInteger requireCount = 0;
    if (IS_IPHONE5)
    {
        requireCount = SECTION_IMPRESSES_ITEMCOUNT_4;
    }
    else
    {
        requireCount = SECTION_IMPRESSES_ITEMCOUNT_3_5;
    }

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
                    labelCountStr = [CBDateUtils timeStringHMSWithMilliseconds:labelCount];
                    break;
                }
                case 5:
                {
                    labelName = NSLocalizedString(@"Impress_ChatLossRate", nil);
                    labelCount = impressCard.chatLossCount;
                    labelCountStr = impressCard.chatLossRateString;
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
            NSArray* impressLabelList = [impressCard topImpressLabelList:requireCount];
            
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
    
    cell.customBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUND;
    cell.customSelectedBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUNDSELECTED;
    cell.selected = NO;
    
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
                headerView.titleLabel.text = NSLocalizedString(@"ChatConfirm_ImpressAssesses", nil);
                break;
            }
            case SECTION_INDEX_LABELS:
            {
                headerView.titleLabel.text = NSLocalizedString(@"ChatConfirm_ImpressLabels", nil);
                break;
            }
            default:
            {
                break;
            }
        }
        
        reusableView = headerView;
    }
    
    reusableView.backgroundColor = FLATUI_COLOR_UICOLLECTIONREUSABLEVIEW_BACKGROUND;
    
    return reusableView;
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    [self _updateSelfChocieStatus:ChoiceType_Considering];
    [self _updatePartnerChoiceStatus:ChoiceType_Considering];
    
    _selfAgreeChatFlag = NO;
    _selfRejectChatFlag = NO;
    _selfUnbindSessionFlag = NO;
    
    _partnerAgreeChatFlag = NO;
    _partnerRejectChatFlag = NO;
    _partnerLostFlag = NO;
    
    _choiceLock = [[NSConditionLock alloc] initWithCondition:ChoiceProcessStatus_Standby];
    
    _agreeChatButton.hidden = NO;
    _rejectChatButton.hidden = NO;
    
    [self _setCountdownSeconds:COUNTDOWN_SECONDS];
    
    [self _refreshCollectionView];
    
    UserDataModule* m = [UserDataModule sharedInstance];
    RHBusinessSession* session = m.businessSession;
    RHMatchedCondition* matchedCondition = session.matchedCondition;
    RHInterestLabel* interestLabel = matchedCondition.interestLabel;
    NSString* str = [NSString stringWithFormat:NSLocalizedString(@"ChatConfirm_Title", nil), interestLabel.labelName];
    self.navigationItem.title = str;
    [self.navigationItem setHidesBackButton:YES];
    [self.navigationController setNavigationBarHidden:NO];
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
    if (!_partnerAgreeChatFlag)
    {
        [_guiModule playSound:SOUNDID_CHATCONFIRM_ACCEPTED];
        
        _partnerAgreeChatFlag = YES;
        
        [self _updatePartnerChoiceStatus:ChoiceType_Agreed];
        
        if (_selfAgreeChatFlag)
        {
            [self _moveToChatVideoView];
        }
    }
}

-(void) onOthersideRejected
{
    if (!_partnerRejectChatFlag)
    {
        [_guiModule playSound:SOUNDID_CHATCONFIRM_REJECTED];
        
        _partnerRejectChatFlag = YES;
        
        [self _updatePartnerChoiceStatus:ChoiceType_Rejected];
        
        [self _remoteUnbindSession];
    }
}

-(void) onOthersideLost
{
    if (!_partnerLostFlag)
    {
        [_guiModule playSound:SOUNDID_CHATCONFIRM_REJECTED];
        
        _partnerLostFlag = YES;
        
        [self _updatePartnerChoiceStatus:ChoiceType_Lost];
        
        [self _remoteUnbindSession];
    }
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

    [self _setupNavigationBar];
    
    [self _setupActionButtons];
    
    [self _formatFlatUI];
}

-(void)_formatFlatUI
{
    _countLabel.textColor = FLATUI_COLOR_TEXT_WARN;
}

- (void) _setupActionButtons
{
    [_agreeChatButton setTitle:NSLocalizedString(@"ChatConfirm_Action_Agree", nil) forState:UIControlStateNormal];
    [_rejectChatButton setTitle:NSLocalizedString(@"ChatConfirm_Action_Reject", nil) forState:UIControlStateNormal];
    
    _agreeChatButton.buttonColor = FLATUI_COLOR_BUTTONPROCESS;
    [_agreeChatButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_agreeChatButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    _rejectChatButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    [_rejectChatButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_rejectChatButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    [_agreeChatButton setExclusiveTouch:YES];
    [_rejectChatButton setExclusiveTouch:YES];
}

- (void) _setupNavigationBar
{
    [self.navigationController.navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_NAVIGATIONBAR_CHATWIZARD];
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
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        [CBAppUtils asyncProcessInMainThread:^(){
            _agreeChatButton.hidden = YES;
            _rejectChatButton.hidden = YES;
        }];
        
        DDLogVerbose(@"_remoteAgreeChat #1");
        [_choiceLock lock];
        
        DDLogVerbose(@"_remoteAgreeChat #2");
        if (_choiceLock.condition == ChoiceProcessStatus_Done)
        {
            DDLogVerbose(@"_remoteAgreeChat #3A");
            [_choiceLock unlock];
        }
        else if (_choiceLock.condition == ChoiceProcessStatus_Standby)
        {
            DDLogVerbose(@"_remoteAgreeChat #3B");
            RHBusinessSession* businessSession = _userDataModule.businessSession;
            NSString* businessSessionId = businessSession.businessSessionId;
            RHBusinessType businessType = businessSession.businessType;
            
            RHDevice* device = _userDataModule.device;
            
            RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:BusinessSessionRequestType_AgreeChat device:device info:nil];
            
            [_commModule businessSessionRequest:requestMessage
                successCompletionBlock:^(){
                    
                    [CBAppUtils asyncProcessInMainThread:^(){
                        [self _updateSelfChocieStatus:ChoiceType_Agreed];
                    }];
                    
                    [_choiceLock unlockWithCondition:ChoiceProcessStatus_Done];
                    DDLogVerbose(@"_remoteAgreeChat #3BA");
                    
                    _selfAgreeChatFlag = YES;
                    [_statusModule recordAppMessage:AppMessageIdentifier_AgreeChat];
                    
                    if (_partnerAgreeChatFlag)
                    {
                        [self _moveToChatVideoView];
                    }
                }
                failureCompletionBlock:^(){
                    _selfAgreeChatFlag = NO;
                    [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_AgreeChat];
                    
                    [_choiceLock unlockWithCondition:ChoiceProcessStatus_Standby];
                    DDLogVerbose(@"_remoteAgreeChat #3BB");
                    
                    [CBAppUtils asyncProcessInMainThread:^(){
                        _agreeChatButton.hidden = NO;
                        _rejectChatButton.hidden = NO;
                    }];
                }
                afterCompletionBlock:^(){

                }
             ];
        }
        else
        {
            DDLogVerbose(@"_remoteAgreeChat #3C");
            [_choiceLock unlock];
        }
    }];
}

- (void) _remoteRejectChat
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){

        [CBAppUtils asyncProcessInMainThread:^(){
            _agreeChatButton.hidden = YES;
            _rejectChatButton.hidden = YES;
        }];
        
        DDLogVerbose(@"_remoteRejectChat #1");
        [_choiceLock lock];
        DDLogVerbose(@"_remoteRejectChat #2");
        if (_choiceLock.condition == ChoiceProcessStatus_Done)
        {
            DDLogVerbose(@"_remoteRejectChat #3A");
            [_choiceLock unlock];
        }
        else if (_choiceLock.condition == ChoiceProcessStatus_Standby)
        {
            DDLogVerbose(@"_remoteRejectChat #3B");
            RHBusinessSession* businessSession = _userDataModule.businessSession;
            NSString* businessSessionId = businessSession.businessSessionId;
            RHBusinessType businessType = businessSession.businessType;
            
            RHDevice* device = _userDataModule.device;
            
            RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:BusinessSessionRequestType_RejectChat device:device info:nil];
            
            [_commModule businessSessionRequest:requestMessage
                 successCompletionBlock:^(){
                     [CBAppUtils asyncProcessInMainThread:^(){
                         [self _updateSelfChocieStatus:ChoiceType_Rejected];
                     }];
                     
                     _selfRejectChatFlag = YES;
                     
                     [_choiceLock unlockWithCondition:ChoiceProcessStatus_Done];
                     DDLogVerbose(@"_remoteRejectChat #3BA");
                     
                     [_statusModule recordAppMessage:AppMessageIdentifier_RejectChat];
                     [self _moveToChatWaitView];
                 }
                 failureCompletionBlock:^(){
                     _selfRejectChatFlag = NO;
                     
                     [_choiceLock unlockWithCondition:ChoiceProcessStatus_Standby];
                     DDLogVerbose(@"_remoteRejectChat #3BB");
                     
                     [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_RejectChat];
                     
                     [CBAppUtils asyncProcessInMainThread:^(){
                         _agreeChatButton.hidden = NO;
                         _rejectChatButton.hidden = NO;
                     }];
                 }
                 afterCompletionBlock:^(){
                     
                 }
             ];
        }
        else
        {
            [_choiceLock unlock];
            DDLogVerbose(@"_remoteRejectChat #3C");
        }
    }];
}

- (void) _remoteUnbindSession
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        DDLogVerbose(@"_remoteUnbindSession #1");
        [_choiceLock lock];

        [CBAppUtils asyncProcessInMainThread:^(){
            _agreeChatButton.hidden = YES;
            _rejectChatButton.hidden = YES;
            [self _clockCancel];
        }];
        
        [NSThread sleepForTimeInterval:DELAY_UNBINDSESSION];
        
        if (!_selfRejectChatFlag)
        {
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
                    
                    DDLogVerbose(@"_remoteUnbindSession #2A");
                }
                failureCompletionBlock:^(){
                    _selfUnbindSessionFlag = NO;
                    [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_UnbindSession];
                    
                    [_choiceLock unlock];
                    DDLogVerbose(@"_remoteUnbindSession #2B");

                    [self _remoteUnbindSession];
                }
                afterCompletionBlock:^(){

                }
             ];
        }
        
        [_choiceLock unlockWithCondition:ChoiceProcessStatus_Done];
        DDLogVerbose(@"_remoteUnbindSession #3");
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
        [_guiModule playSound:SOUNDID_CHATCONFIRM_ACCEPTED];
        [self onOthersideAgreed];
    }
    else if (serverNotificationId == ServerNotificationIdentifier_OthersideRejectChat)
    {
        [_guiModule playSound:SOUNDID_CHATCONFIRM_REJECTED];
        [self onOthersideRejected];
    }
    else if (serverNotificationId == ServerNotificationIdentifier_OthersideLost)
    {
        [_guiModule playSound:SOUNDID_CHATCONFIRM_REJECTED];        
        [self onOthersideLost];
    }
}

-(void) _refreshCollectionView
{
    [_collectionView reloadData];    
}

-(void) _updateSelfChocieStatus:(ChoiceType) type
{
    NSString* text = nil;
    UIColor* color = FLATUI_COLOR_CHATCONFIRMTYPE_CONSIDERING;
    switch (type)
    {
        case ChoiceType_Considering:
        {
            text = NSLocalizedString(@"ChatConfirm_SelfStatus_Undecided", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_CONSIDERING;
            break;
        }
        case ChoiceType_Agreed:
        {
            text = NSLocalizedString(@"ChatConfirm_SelfStatus_Agreed", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_AGREED;
            break;
        }
        case ChoiceType_Rejected:
        {
            text = NSLocalizedString(@"ChatConfirm_SelfStatus_Rejected", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_REJECTED;
            break;
        }
        case ChoiceType_Lost:
        {
            text = NSLocalizedString(@"ChatConfirm_SelfStatus_Lost", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_LOST;
            break;
        }
        default:
        {
            break;
        }
    }
    
    _selfStatusLabel.text = text;
    _selfStatusLabel.backgroundColor = color;
}

-(void) _updatePartnerChoiceStatus:(ChoiceType) type
{
    NSString* text = nil;
    UIColor* color = FLATUI_COLOR_CHATCONFIRMTYPE_CONSIDERING;
    switch (type)
    {
        case ChoiceType_Considering:
        {
            text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Undecided", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_CONSIDERING;
            break;
        }
        case ChoiceType_Agreed:
        {
            text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Agreed", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_AGREED;
            break;
        }
        case ChoiceType_Rejected:
        {
            text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Rejected", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_REJECTED;
            break;
        }
        case ChoiceType_Lost:
        {
            text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Lost", nil);
            color = FLATUI_COLOR_CHATCONFIRMTYPE_LOST;
            break;
        }
        default:
        {
            break;
        }
    }
    
    _partnerStatusLabel.text = text;
    _partnerStatusLabel.backgroundColor = color;
}

#pragma mark - IBActions

- (IBAction)didPressAgreeChatButton:(id)sender
{
    [_guiModule playSound:SOUNDID_CHATCONFIRM_ACCEPTED];
    
    [self _remoteAgreeChat];
}

- (IBAction)didPressRejectChatButton:(id)sender
{
    [_guiModule playSound:SOUNDID_CHATCONFIRM_REJECTED];
    
    [self _remoteRejectChat];
}

@end
