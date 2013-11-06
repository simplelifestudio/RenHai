//
//  ChatAssessViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ChatAssessViewController_iPhone.h"

#import "UserDataModule.h"
#import "AppDataModule.h"
#import "CommunicationModule.h"
#import "GUIModule.h"
#import "GUIStyle.h"
#import "BusinessStatusModule.h"

#import "RHCollectionLabelCell_iPhone.h"
#import "ChatAssessAddImpressLabelsHeaderView_iPhone.h"
#import "ChatAssessExistImpressLabelsHeaderView_iPhone.h"

#define ASSESSLABELSVIEW_SECTION_COUNT 1
#define ADDIMPRESSLABELSVIEW_SECTION_COUNT 1
#define EXISTIMPRESSLABELSVIEW_SECTION_COUNT 1

#define ASSESSLABELSVIEW_SECTION_INDEX_ASSESSLABELS 0
#define ASSESSLABELSVIEW_SECTION_ITEMCOUNT_ASSESSLABELS 3

#define ADDIMPRESSLABELSVIEW_SECTION_INDEX_ADDIMPRESSLABELS 0
#define ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS 3

#define EXISTIMPRESSLABELSVIEW_SECTION_INDEX_EXISTIMPRESSLABELS 0
#define EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS 6

#define DELAY_REFRESH 0.5f

#define COUNTDOWN_SECONDS 180

#define COLOR_ASSESSLABEL_SELECTED [UIColor redColor]

@interface ChatAssessViewController_iPhone () <RHCollectionLabelCellEditingDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    ImpressLabelsHeaderView_iPhone* _assessLabelsHeaderView;
    ChatAssessAddImpressLabelsHeaderView_iPhone* _addImpressLabelsHeaderView;
    ChatAssessExistImpressLabelsHeaderView_iPhone* _existImpressLabelsHeaderView;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    NSArray* _assessLabelNames;
    NSMutableArray* _impressLabelNames;
    
    NSMutableArray* _addImpressLabels;
    
    volatile BOOL _selfAssessedFlag;
    
    volatile BOOL _isDeciding;
}

@end

@implementation ChatAssessViewController_iPhone

@synthesize assessLabelsView = _assessLabelsView;
@synthesize addImpressLabelsView = _addImpressLabelsView;
@synthesize existImpressLabelsView = _existImpressLabelsView;

@synthesize countLabel = _countLabel;

@synthesize continueButton = _continueButton;
@synthesize finishButton = _finishButton;

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
    
//    [self resetPage];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
//    [self _clockStart];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
//    [self _clockCancel];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - ChatWizardPage

-(void) resetPage
{
    [_continueButton setTitle:NSLocalizedString(@"ChatAssess_Action_Continue", nil) forState:UIControlStateNormal];
    [_finishButton setTitle:NSLocalizedString(@"ChatAssess_Action_Finish", nil) forState:UIControlStateNormal];
    
    _selfAssessedFlag = NO;
    
    _isDeciding = NO;
    
    [_addImpressLabels removeAllObjects];
    
    [self _setCountdownSeconds:COUNTDOWN_SECONDS];
}

-(void) pageWillLoad
{
    [self _clockStart];
}

-(void) pageWillUnload
{
    [self _clockCancel];
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    _guiModule = [GUIModule sharedInstance];
    _statusModule = [BusinessStatusModule sharedInstance];
    
    _addImpressLabels = [NSMutableArray array];
    
    [self _setupCollectionView];
}

-(void)_setupCollectionView
{
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    
    [_assessLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    [_addImpressLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    [_existImpressLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSLABELSHEADERVIEW bundle:nil];
    [_assessLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
    [_addImpressLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_ADDIMPRESSLABELSHEADERVIEW];
    [_existImpressLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_EXISTIMPRESSLABELSHEADERVIEW];
    
    _assessLabelsView.dataSource = self;
    _addImpressLabelsView.dataSource = self;
    _existImpressLabelsView.dataSource = self;
    
    _assessLabelsView.delegate = self;
    _addImpressLabelsView.delegate = self;
    _existImpressLabelsView.delegate = self;
    
    _assessLabelNames = @[MESSAGE_KEY_ASSESS_HAPPY, MESSAGE_KEY_ASSESS_SOSO, MESSAGE_KEY_ASSESS_DISGUSTING];
}

#pragma mark - RHCollectionLabelCellEditingDelegate

-(void) onTextFieldDoneEditing:(RHCollectionLabelCell_iPhone*) cell labelName:(NSString*) labelName
{
    if (nil != cell)
    {
        NSIndexPath* indexPath = [_addImpressLabelsView indexPathForCell:cell];
        NSUInteger item = indexPath.item;
        
        if (item < _impressLabelNames.count)
        {
            [_impressLabelNames removeObjectAtIndex:item];
            
            if (nil == labelName || 0 == labelName.length)
            {
                
            }
            else
            {
                [_impressLabelNames insertObject:labelName atIndex:item];
            }
        }
        else
        {
            if (nil == labelName || 0 == labelName.length)
            {
                
            }
            else
            {
                [_impressLabelNames insertObject:labelName atIndex:item];
            }
        }
    }
}

-(void)_moveToChatWaitView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        [_guiModule.chatWizardController wizardProcess:ChatWizardStatus_ChatWait];
    }];
}

-(void)_moveToHomeView
{
    [CBAppUtils asyncProcessInMainThread:^(){
        [_guiModule.mainViewController switchToMainScene];
    }];
}

-(RHImpressCard*) _getPartnerAssessedImpressCard
{
    NSUInteger assessLabelIndex = 0;
    NSArray* indexPathes = _assessLabelsView.indexPathsForSelectedItems;
    if (0 < indexPathes)
    {
        NSIndexPath* indexPath = indexPathes[0];
        assessLabelIndex = indexPath.item;
    }
    
    RHImpressCard* card = [RHImpressCard newImpressCard:_assessLabelNames[assessLabelIndex] impressLabels:@[]];
    
    return card;
}

-(void)_updatePartnerImpressCardWithType:(BusinessSessionRequestType) requestType
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        @synchronized(self)
        {
            if (_isDeciding)
            {
                return;
            }
            else
            {
                _isDeciding = YES;
            }
        }
        
        RHDevice* selfDevice = _userDataModule.device;
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        NSString* businessSessionId = businessSession.businessSessionId;
        RHDevice* partnerDevice = [businessSession getPartner];
        RHProfile* profile = partnerDevice.profile;
        
        RHImpressCard* impressCard = [self _getPartnerAssessedImpressCard];
        profile.impressCard = impressCard;
        
        NSDictionary* info = partnerDevice.toJSONObject;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:CURRENT_BUSINESSPOOL operationType:requestType device:selfDevice info:info];
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                _selfAssessedFlag = YES;
                
                switch (requestType)
                {
                    case BusinessSessionRequestType_AssessAndContinue:
                    {
                        [_statusModule recordAppMessage:AppMessageIdentifier_AssessAndContinue];
                        [self _moveToChatWaitView];
                        break;
                    }
                    case BusinessSessionRequestType_AssessAndQuit:
                    {
                        [_statusModule recordAppMessage:AppMessageIdentifier_AssessAndQuit];
                        [self _moveToHomeView];
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
            }
            failureCompletionBlock:^(){
                _selfAssessedFlag = NO;
            }
            afterCompletionBlock:nil
         ];
    }];
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
    if (!_selfAssessedFlag)
    {
        [self _updatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndContinue];
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

#pragma mark - UICollectionViewDelegate

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (collectionView == _assessLabelsView)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView cellForItemAtIndexPath:indexPath];
        
        [cell setSelected:YES];
    }
    else if (collectionView == _addImpressLabelsView)
    {
        
    }
    else if (collectionView == _existImpressLabelsView)
    {
        
    }
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)cv
{
    NSInteger sectionsCount = 0;
    
    if (cv == _assessLabelsView)
    {
        sectionsCount = ASSESSLABELSVIEW_SECTION_COUNT;
    }
    else if (cv == _addImpressLabelsView)
    {
        sectionsCount = ADDIMPRESSLABELSVIEW_SECTION_COUNT;
    }
    else if (cv == _existImpressLabelsView)
    {
        sectionsCount = EXISTIMPRESSLABELSVIEW_SECTION_COUNT;
    }
    
    return sectionsCount;
}

- (NSInteger)collectionView:(UICollectionView *)cv numberOfItemsInSection:(NSInteger)section
{
    NSInteger itemsCount = 0;
    
    RHBusinessSession* businessSession = _userDataModule.businessSession;
    RHDevice* partnerDevice = [businessSession getPartner];
    RHProfile* partnerProfile = partnerDevice.profile;
    RHImpressCard* partnerImpressCard = partnerProfile.impressCard;
    NSArray* impressLabels = partnerImpressCard.impressLabelList;
    
    if (cv == _assessLabelsView)
    {
        itemsCount = ASSESSLABELSVIEW_SECTION_ITEMCOUNT_ASSESSLABELS;
    }
    else if (cv == _addImpressLabelsView)
    {
        itemsCount = (_addImpressLabels.count <= ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS) ? _addImpressLabels.count : ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS;
    }
    else if (cv == _existImpressLabelsView)
    {
        itemsCount = (impressLabels.count <= EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS) ? impressLabels.count : EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS;
    }
    
    return itemsCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)cv cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[cv dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL forIndexPath:indexPath];
    cell.editingDelegate = self;
    
    NSUInteger position = indexPath.item;
    
    if (cv == _assessLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        if (0 < _assessLabelNames.count && position < _assessLabelNames.count)
        {
            NSString* name = _assessLabelNames[position];
            labelName = [RHImpressLabel assessLabelName:name];
        }
        
        cell.textField.text = labelName;
        if (0 <= labelCount)
        {
            cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
        }
        else
        {
            cell.countLabel.text = @"";
        }
        
        if (position == 0)
        {
            [_assessLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            cell.selected = YES;
        }
        
        cell.countLabel.hidden = YES;
        cell.textField.userInteractionEnabled = NO;
    }
    else if (cv == _addImpressLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        NSArray* labelList = _impressLabelNames;
        
        if (0 < labelList.count && position < labelList.count)
        {
            RHImpressLabel* label = labelList[position];
            labelName = label.labelName;
        }
        
        cell.textField.text = labelName;
        if (0 <= labelCount)
        {
            cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
        }
        else
        {
            cell.countLabel.text = @"";
        }
        
        cell.editingDelegate = self;
    }
    else if (cv == _existImpressLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        NSArray* labelList = _impressLabelNames;
        
        if (0 < labelList.count && position < labelList.count)
        {
            RHImpressLabel* label = labelList[position];
            labelName = label.labelName;
        }
        
        cell.textField.text = labelName;
        if (0 <= labelCount)
        {
            cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
        }
        else
        {
            cell.countLabel.text = @"";
        }
    }
    
    return cell;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath
{
    BOOL flag = NO;

    return flag;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    BOOL flag = NO;
    
    return flag;
}

- (void)collectionView:(LSCollectionViewHelper *)cv moveItemAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{

}

-(UICollectionReusableView *) collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView* reusableView = nil;
    
    if([kind isEqual:UICollectionElementKindSectionHeader])
    {
        if (collectionView == _assessLabelsView)
        {
            if (nil == _assessLabelsHeaderView)
            {
                _assessLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
                _assessLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_AssessLabels", nil);
            }
            
            reusableView = _assessLabelsHeaderView;
        }
        else if (collectionView == _addImpressLabelsView)
        {
            if (nil == _addImpressLabelsHeaderView)
            {
                _addImpressLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_ADDIMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
                _addImpressLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_AddImpressLabels", nil);
            }
            
            reusableView = _addImpressLabelsHeaderView;
        }
        else if (collectionView == _existImpressLabelsView)
        {
            if (nil == _existImpressLabelsHeaderView)
            {
                _existImpressLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_EXISTIMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
                _existImpressLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_ExistImpressLabels", nil);
            }
            
            reusableView = _existImpressLabelsHeaderView;
        }
    }
    
    return reusableView;
}

#pragma mark - IBActions

- (IBAction)didPressContinueButton:(id)sender
{
    [self _updatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndContinue];
}

- (IBAction)didPressFinishButton:(id)sender
{
    [self _updatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndQuit];
}

@end
