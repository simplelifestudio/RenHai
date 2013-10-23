//
//  ChatConfirmViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatConfirmViewController_iPhone.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"
#import "AppDataModule.h"

#import "RHCollectionLabelCell_iPhone.h"

#import "ImpressSectionHeaderView_iPhone.h"

#define SECTION_COUNT 3

#define SECTION_INDEX_ASSESSES 0
#define SECTION_ASSESSES_ITEMCOUNT 3

#define SECTION_INDEX_CHATS 1
#define SECTION_CHATS_ITEMCOUNT 3

#define SECTION_INDEX_LABELS 2
#define SECTION_IMPRESSES_ITEMCOUNT 3

#define COUNTDOWN_SECONDS 9

@interface ChatConfirmViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    volatile BOOL _selfAgreeChatFlag;
    volatile BOOL _selfRejectChatFlag;
    volatile BOOL _selfUnbindSessionFlag;
    
    volatile BOOL _partnerAgreeChatFlag;
    volatile BOOL _partnerRejectChatFlag;
    volatile BOOL _partnerLostFlag;
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
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - UICollectionViewDataDelegate

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            return SECTION_ASSESSES_ITEMCOUNT;
        }
        case SECTION_INDEX_CHATS:
        {
            return SECTION_CHATS_ITEMCOUNT;
        }
        case SECTION_INDEX_LABELS:
        {
            //            NSArray* impressLabelList = [_impressCard topImpressLabelList:SECTION_IMPRESSES_ITEMCOUNT];
            //            return impressLabelList.count;
            return SECTION_IMPRESSES_ITEMCOUNT;
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
    RHDevice* device = [businessSession getPartner];
    RHProfile* profile = device.profile;
    RHImpressCard* impressCard = profile.impressCard;
    
    BOOL isEmptyCell = NO;
    
    NSString* labelName = nil;
    NSInteger labelCount = -1;
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            NSArray* assessLabelList = impressCard.assessLabelList;
            RHImpressLabel* assessLabel = assessLabelList[row];
            labelName = [RHImpressLabel assessLabelName:assessLabel.labelName];
            labelCount = assessLabel.assessedCount;
            
            break;
        }
        case SECTION_INDEX_CHATS:
        {
            switch (row)
            {
                case 0:
                {
                    labelName = NSLocalizedString(@"Impress_ChatTotalCount", nil);
                    labelCount = impressCard.chatTotalCount;
                    break;
                }
                case 1:
                {
                    labelName = NSLocalizedString(@"Impress_ChatTotalDuration", nil);
                    labelCount = impressCard.chatTotalDuration;
                    break;
                }
                case 2:
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
            if (0 < impressLabelList.count && row <= impressLabelList.count)
            {
                impressLabel = impressLabelList[row];
                
                labelName = impressLabel.labelName;
                labelCount = impressLabel.assessedCount;
            }
            else
            {
                labelName = NSLocalizedString(@"Impress_Empty", nil);
                isEmptyCell = YES;
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
        cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
    }
    else
    {
        cell.countLabel.text = @"";
    }
    
    cell.isEmptyCell = isEmptyCell;
    
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
        ImpressSectionHeaderView_iPhone* sectionHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSSECTIONHEADERVIEW forIndexPath:indexPath];
        
        switch (section)
        {
            case SECTION_INDEX_ASSESSES:
            {
                sectionHeaderView.titleLabel.text = NSLocalizedString(@"Impress_Assesses", nil);
                break;
            }
            case SECTION_INDEX_CHATS:
            {
                sectionHeaderView.titleLabel.text = NSLocalizedString(@"Impress_Chats", nil);
                break;
            }
            case SECTION_INDEX_LABELS:
            {
                sectionHeaderView.titleLabel.text = NSLocalizedString(@"Impress_Labels", nil);
                break;
            }
            default:
            {
                break;
            }
        }
        
        reusableView = sectionHeaderView;
    }
    
    
    return reusableView;
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    _viewTitleLabel.text = NSLocalizedString(@"ChatConfirm_Title", nil);
    
    _selfStatusLabel.text = NSLocalizedString(@"ChatConfirm_SelfStatus_Undecided", nil);
    _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Undecided", nil);
    
    _selfAgreeChatFlag = NO;
    _selfRejectChatFlag = NO;
    _selfUnbindSessionFlag = NO;
    
    _partnerAgreeChatFlag = NO;
    _partnerRejectChatFlag = NO;
    _partnerLostFlag = NO;
    
    _agreeChatButton.hidden = NO;
    _rejectChatButton.hidden = NO;
    
    [self setCountdownSeconds:COUNTDOWN_SECONDS];
}

-(void) pageWillLoad
{
    [self _clockStart];
}

-(void) pageWillUnload
{
    [self _clockCancel];
}

-(void) onOthersideAgreed
{
    _partnerAgreeChatFlag = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^(){
        _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Agreed", nil);
    });
    
    if (_selfAgreeChatFlag)
    {
        [self _moveToChatVideoView];
    }
}

-(void) onOthersideRejected
{
    _partnerRejectChatFlag = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^(){
        
        _agreeChatButton.enabled = NO;
        _rejectChatButton.enabled = NO;
        
        _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Rejected", nil);
        
    });
    
    [self _clockCancel];
    
    [self _unbindSession];
}

-(void) onOthersideLost
{
    _partnerLostFlag = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^(){
        
        _agreeChatButton.enabled = NO;
        _rejectChatButton.enabled = NO;
        
        _partnerStatusLabel.text = NSLocalizedString(@"ChatConfirm_PartnerStatus_Lost", nil);
        
    });
    
    [self _clockCancel];
    
    [self _unbindSession];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    
    [self _setupCollectionView];
    
    [_agreeChatButton setTitle:NSLocalizedString(@"ChatConfirm_Action_Agree", nil) forState:UIControlStateNormal];
    [_rejectChatButton setTitle:NSLocalizedString(@"ChatConfirm_Action_Reject", nil) forState:UIControlStateNormal];
}

-(void)_setupCollectionView
{
    _collectionView.dataSource = self;
    
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    [_collectionView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSSECTIONHEADERVIEW bundle:nil];
    [_collectionView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSSECTIONHEADERVIEW];
}

- (void) _clockStart
{
    [self _clockCancel];
    
    NSTimeInterval interval = 1.0;
    _timer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(_clockClick) userInfo:nil repeats:YES];
}

- (void) _clockClick
{
    if (0 == _countdownSeconds)
    {
        [self _clockCancel];
    }
    else
    {
        _countdownSeconds--;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _countdownSeconds]];
    });
}

- (void) _clockCancel
{
    if (nil != _timer)
    {
        [_timer invalidate];
    }
}

- (void) setCountdownSeconds:(NSUInteger) seconds
{
    if (0 < seconds)
    {
        _countdownSeconds = seconds;
        [_countLabel setText:[NSString stringWithFormat:@"%d", _countdownSeconds]];
    }
}

- (void) _agreeChat
{
    _agreeChatButton.hidden = YES;
    _rejectChatButton.hidden = YES;
    _selfStatusLabel.text = NSLocalizedString(@"ChatConfirm_SelfStatus_Agreed", nil);
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_AgreeChat device:device info:nil];
        
        RHMessage* responseMessage = [_commModule sendMessage:businessSessionRequestMessage];
        if (responseMessage.messageId == MessageId_BusinessSessionResponse)
        {
            NSDictionary* messageBody = responseMessage.body;
            NSDictionary* businessSessionDic = messageBody;
            
            @try
            {
                NSNumber* oOperationValue = [businessSessionDic objectForKey:MESSAGE_KEY_OPERATIONVALUE];
                BusinessSessionOperationValue operationValue = oOperationValue.intValue;
                
                if (operationValue == BusinessSessionOperationValue_Success)
                {
                    _selfAgreeChatFlag = YES;
                    
                    if (_partnerAgreeChatFlag)
                    {
                        [self _moveToChatVideoView];
                    }
                }
                else
                {
                    
                }
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        else if (responseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
        {
            
        }
        else
        {
            
        }
    });
}

- (void) _rejectChat
{
    _agreeChatButton.hidden = YES;
    _rejectChatButton.hidden = YES;
    _selfStatusLabel.text = NSLocalizedString(@"ChatConfirm_SelfStatus_Rejected", nil);
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_RejectChat device:device info:nil];
        
        RHMessage* responseMessage = [_commModule sendMessage:businessSessionRequestMessage];
        if (responseMessage.messageId == MessageId_BusinessSessionResponse)
        {
            NSDictionary* messageBody = responseMessage.body;
            NSDictionary* businessSessionDic = messageBody;
            
            @try
            {
                NSNumber* oOperationValue = [businessSessionDic objectForKey:MESSAGE_KEY_OPERATIONVALUE];
                BusinessSessionOperationValue operationValue = oOperationValue.intValue;
                
                if (operationValue == BusinessSessionOperationValue_Success)
                {   
                    _selfRejectChatFlag = YES;
                    
                    [self _moveToChatWaitView];
                }
                else
                {
                    
                }
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        else if (responseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
        {
            
        }
        else
        {
            
        }
    });
}

- (void) _unbindSession
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        
        RHDevice* device = _userDataModule.device;
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:nil businessType:CURRENT_BUSINESSPOOL operationType:BusinessSessionRequestType_UnbindSession device:device info:nil];
        
        RHMessage* responseMessage = [_commModule sendMessage:businessSessionRequestMessage];
        if (responseMessage.messageId == MessageId_BusinessSessionResponse)
        {
            NSDictionary* messageBody = responseMessage.body;
            NSDictionary* businessSessionDic = messageBody;
            
            @try
            {
                NSNumber* oOperationValue = [businessSessionDic objectForKey:MESSAGE_KEY_OPERATIONVALUE];
                BusinessSessionOperationValue operationValue = oOperationValue.intValue;
                
                if (operationValue == BusinessSessionOperationValue_Success)
                {
                    _selfUnbindSessionFlag = YES;
                    
                    [self _moveToChatWaitView];
                }
                else
                {
                    
                }
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        else if (responseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
        {
            
        }
        else
        {
            
        }
    });
    
}

- (void) _moveToChatVideoView
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        
        [_appDataModule updateAppBusinessStatus:AppBusinessStatus_ChatAgreeCompleted];
        
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatVideo];
    });
}

- (void) _moveToChatWaitView
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        
        [_appDataModule updateAppBusinessStatus:AppBusinessStatus_EnterPoolCompleted];
        
        ChatWizardController* chatWizard = _guiModule.chatWizardController;
        [chatWizard wizardProcess:ChatWizardStatus_ChatWait];
    });
}

#pragma mark - IBActions

- (IBAction)didPressAgreeChatButton:(id)sender
{
    [self _agreeChat];
}

- (IBAction)didPressRejectChatButton:(id)sender
{
    [self _rejectChat];
}

@end
