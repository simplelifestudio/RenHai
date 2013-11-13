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

#import "UIViewController+CWPopup.h"

#import "RHCollectionLabelCell_iPhone.h"
#import "RHLabelManageViewController_iPhone.h"
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

#define COUNTDOWN_SECONDS 300

#define COLOR_ASSESSLABEL_SELECTED [UIColor redColor]

@interface ChatAssessViewController_iPhone () <ChatAssessAddImpressLabelsHeaderViewDelegate, ChatAssessExistImpressLabelsHeaderViewDelegate, RHLabelManageDelegate, UIGestureRecognizerDelegate>
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

    NSMutableArray* _addImpressLabelNames;
    
    NSUInteger _assessLabelPosition;
    
    volatile BOOL _selfAssessedFlag;
    
    volatile BOOL _isDeciding;
    
    BOOL _allowCloneLabel;
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
    _continueButton.hidden = NO;
    _finishButton.hidden = NO;
    
    _selfAssessedFlag = NO;
    
    _isDeciding = NO;
    
    _assessLabelPosition = 0;
    
    _allowCloneLabel = NO;    
    
    [_addImpressLabelNames removeAllObjects];
    
    [self _refreshAssessLabelsView];
    [self _refershAddImpressLabelsView];
    [self _refreshExistImpressLabelsView];
    
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
    
    _addImpressLabelNames = [NSMutableArray array];
    
    _assessLabelPosition = 0;
    
    [_continueButton setTitle:NSLocalizedString(@"ChatAssess_Action_Continue", nil) forState:UIControlStateNormal];
    [_finishButton setTitle:NSLocalizedString(@"ChatAssess_Action_Finish", nil) forState:UIControlStateNormal];
    
    [self _setupCollectionView];
    
    UITapGestureRecognizer* singleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didSingleTapped:)];
    singleTapGesturer.delegate = self;
    singleTapGesturer.numberOfTapsRequired = 1;
    [self.view addGestureRecognizer:singleTapGesturer];
    
    UITapGestureRecognizer* doubleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didDoubleTapped:)];
    doubleTapGesturer.delegate = self;
    doubleTapGesturer.numberOfTapsRequired = 2;
    [self.view addGestureRecognizer:doubleTapGesturer];
}

-(void)_setupCollectionView
{
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    
    [_assessLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    [_addImpressLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    [_existImpressLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSLABELSHEADERVIEW bundle:nil];
    [_assessLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
    
    nib = [UINib nibWithNibName:NIB_ADDIMPRESSLABELSHEADERVIEW bundle:nil];
    [_addImpressLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_ADDIMPRESSLABELSHEADERVIEW];
    
    nib = [UINib nibWithNibName:NIB_EXISTIMPRESSLABELSHEADERVIEW bundle:nil];
    [_existImpressLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_EXISTIMPRESSLABELSHEADERVIEW];
    
    _assessLabelsView.dataSource = self;
    _addImpressLabelsView.dataSource = self;
    _existImpressLabelsView.dataSource = self;
    
    _assessLabelsView.delegate = self;
    _addImpressLabelsView.delegate = self;
    _existImpressLabelsView.delegate = self;
    
    _assessLabelNames = @[MESSAGE_KEY_ASSESS_HAPPY, MESSAGE_KEY_ASSESS_SOSO, MESSAGE_KEY_ASSESS_DISGUSTING];
}

-(void) _setupAddImpressLabelsHeaderView:(UICollectionView*) collectionView kind:(NSString*) kind atIndexPath:(NSIndexPath*) indexPath
{
    if (nil == _addImpressLabelsHeaderView)
    {
        _addImpressLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_ADDIMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
        _addImpressLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_AddImpressLabels", nil);
        [_addImpressLabelsHeaderView.addButton setTitle:NSLocalizedString(@"ChatAssess_Action_AddImpressLabel", nil) forState:UIControlStateNormal];
        [_addImpressLabelsHeaderView.delButton setTitle:NSLocalizedString(@"ChatAssess_Action_DelImpressLabel", nil) forState:UIControlStateNormal];
        _addImpressLabelsHeaderView.operationDelegate = self;
    }
    
    [self _refershAddImpressLabelsViewActions];
}

-(void) _setupExistImpressLabelsHeaderView:(UICollectionView*) collectionView kind:(NSString*) kind atIndexPath:(NSIndexPath*) indexPath
{
    if (nil == _existImpressLabelsHeaderView)
    {
        _existImpressLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_EXISTIMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
        _existImpressLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_ExistImpressLabels", nil);
        [_existImpressLabelsHeaderView.cloneButton setTitle:NSLocalizedString(@"ChatAssess_Action_CopyImpressLabel", nil) forState:UIControlStateNormal];
        _existImpressLabelsHeaderView.operationDelegate = self;
    }
    
    [self _refreshExistImpressLabelsViewActions];
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
    
    NSArray* impressLabels = [self _getAddImpressLabels];
    
    RHImpressCard* card = [RHImpressCard newImpressCard:_assessLabelNames[assessLabelIndex] impressLabels:impressLabels];
    
    return card;
}

-(NSArray*) _getAddImpressLabels
{
    NSMutableArray* labels = [NSMutableArray arrayWithCapacity:_addImpressLabelNames.count];
    
    for (NSString* labelName in _addImpressLabelNames)
    {
        RHImpressLabel* label = [[RHImpressLabel alloc] init];
        label.labelName = labelName;
        
        [labels addObject:label];
    }
    
    return labels;
}

-(void)_remoteUpdatePartnerImpressCardWithType:(BusinessSessionRequestType) requestType
{
    _continueButton.hidden = YES;
    _finishButton.hidden = YES;
    
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
//        [self _remoteUpdatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndQuit];
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

-(void)_didSingleTapped:(UITapGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_assessLabelsView.frame, locationTouch))
    {
        locationTouch = [_assessLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_assessLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _assessLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_assessLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        else
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_assessLabelsView cellForItemAtIndexPath:indexPath];
            if (!cell.isSelected)
            {
                [_assessLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            }
            else
            {
                [_assessLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        [self _refreshAssessLabelsViewActions];
    }
    else if (CGRectContainsPoint(_addImpressLabelsView.frame, locationTouch))
    {
        locationTouch = [_addImpressLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_addImpressLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _addImpressLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_addImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        else
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_addImpressLabelsView cellForItemAtIndexPath:indexPath];
            if (!cell.isSelected)
            {
                [_addImpressLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            }
            else
            {
                [_addImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
            
            NSArray* selectedIndexPathes = _existImpressLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_existImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        [self _refershAddImpressLabelsViewActions];
    }
    else if (CGRectContainsPoint(_existImpressLabelsView.frame, locationTouch))
    {
        locationTouch = [_existImpressLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_existImpressLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _existImpressLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_existImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        else
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_addImpressLabelsView cellForItemAtIndexPath:indexPath];
            if (!cell.isSelected)
            {
                [_existImpressLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
                
                NSString* labelName = cell.labelName;
                
                BOOL hasLabel = [_addImpressLabelNames containsObject:labelName];
                BOOL isFull = (_addImpressLabelNames.count >= EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS);
                _allowCloneLabel = (hasLabel || isFull) ? NO : YES;
            }
            else
            {
                [_existImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
                _allowCloneLabel = NO;
            }
            
            NSArray* selectedIndexPathes = _addImpressLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_addImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        [self _refreshExistImpressLabelsViewActions];
    }
}

-(void)_didDoubleTapped:(UITapGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_addImpressLabelsView.frame, locationTouch))
    {
        locationTouch = [_addImpressLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_addImpressLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil != indexPath)
        {
            NSUInteger section = indexPath.section;
            NSUInteger position = indexPath.item;
            
            switch (section)
            {
                case ADDIMPRESSLABELSVIEW_SECTION_INDEX_ADDIMPRESSLABELS:
                {
                    NSString* labeName = _addImpressLabelNames[position];
                    
                    RHLabelManageViewController_iPhone* labelManagerViewController = [RHLabelManageViewController_iPhone modifyLabelManagerViewController:self label:labeName];
                    [_guiModule.mainViewController presentPopupViewController:labelManagerViewController animated:YES completion:nil];
                    
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }
}

- (void) _refreshAssessLabelsView
{
    [_assessLabelsView reloadData];
    
    [self _refreshAssessLabelsViewActions];
}

- (void) _refreshAssessLabelsViewActions
{
    
}

- (void) _refershAddImpressLabelsView
{
    [_addImpressLabelsView reloadData];
    
    [self _refershAddImpressLabelsViewActions];
}

- (void) _refershAddImpressLabelsViewActions
{
    BOOL allowCreateLabel = YES;
    BOOL allowDeleteLabel = NO;
    
    NSArray* selectedCells = [_addImpressLabelsView indexPathsForSelectedItems];
    if (0 < selectedCells.count && 1 < _addImpressLabelNames.count)
    {
        allowDeleteLabel = YES;
    }
    
    if (ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS <= _addImpressLabelNames.count)
    {
        allowCreateLabel = NO;
    }
    
    _addImpressLabelsHeaderView.addButton.enabled = allowCreateLabel;
    _addImpressLabelsHeaderView.delButton.enabled = allowDeleteLabel;
}

- (void) _refreshExistImpressLabelsView
{
    [_existImpressLabelsView reloadData];
    
    [self _refreshExistImpressLabelsViewActions];
}

- (void) _refreshExistImpressLabelsViewActions
{
    BOOL allowCloneLabel = _allowCloneLabel;
    
    _existImpressLabelsHeaderView.cloneButton.enabled = allowCloneLabel;
}

#pragma mark - UICollectionViewDelegate

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger position = indexPath.item;
    
    if (collectionView == _assessLabelsView)
    {
        _assessLabelPosition = position;
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
        NSArray* addLabelList = _addImpressLabelNames;
        
        itemsCount = (addLabelList.count <= ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS) ? addLabelList.count : ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS;
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
        
        if (position == _assessLabelPosition)
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
        
        NSArray* labelList = _addImpressLabelNames;
        
        if (0 < labelList.count && position < labelList.count)
        {
            labelName = labelList[position];
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
    else if (cv == _existImpressLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        RHDevice* partnerDevice = [businessSession getPartner];
        RHProfile* partnerProfile = partnerDevice.profile;
        RHImpressCard* partnerImpressCard = partnerProfile.impressCard;
        NSArray* labelList = partnerImpressCard.impressLabelList;
        
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
            [self _setupAddImpressLabelsHeaderView:collectionView kind:kind atIndexPath:indexPath];
            
            reusableView = _addImpressLabelsHeaderView;
        }
        else if (collectionView == _existImpressLabelsView)
        {
            [self _setupExistImpressLabelsHeaderView:collectionView kind:kind atIndexPath:indexPath];
            
            reusableView = _existImpressLabelsHeaderView;
        }
    }
    
    return reusableView;
}

#pragma mark - IBActions

- (IBAction)didPressContinueButton:(id)sender
{
    [self _remoteUpdatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndContinue];
}

- (IBAction)didPressFinishButton:(id)sender
{
    [self _remoteUpdatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndQuit];
}

#pragma mark - RHLabelManageDelegate

-(void) didLabelManageDone:(ManageMode) mode newLabel:(NSString*) newLabel oldLabel:(NSString*) oldLabel
{
    switch (mode)
    {
        case ManageMode_NewLabel:
        {
            [_addImpressLabelNames addObject:newLabel];
            
            break;
        }
        case ManageMode_ModifyLabel:
        {
            if (![oldLabel isEqualToString:newLabel])
            {
                NSUInteger labelIndex = [_addImpressLabelNames indexOfObject:oldLabel];
                [_addImpressLabelNames removeObjectAtIndex:labelIndex];
                [_addImpressLabelNames insertObject:newLabel atIndex:labelIndex];
            }
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    [self _refershAddImpressLabelsView];
    
    [_guiModule.mainViewController dismissPopupViewControllerAnimated:YES completion:nil];
}

-(void) didLabelManageCancel
{
    [_guiModule.mainViewController dismissPopupViewControllerAnimated:YES completion:nil];
}

#pragma mark - ChatAssessAddImpressLabelsHeaderViewDelegate

-(void) didCreateImpressLabel
{
    RHLabelManageViewController_iPhone* labelManageVC = [RHLabelManageViewController_iPhone newLabelManageViewController:self];
    [_guiModule.mainViewController presentPopupViewController:labelManageVC animated:YES completion:nil];
}

-(void) didDeleteImpressLabel
{
    NSArray* selectedIndexPathes = _addImpressLabelsView.indexPathsForSelectedItems;
    for (NSIndexPath* indexPath in selectedIndexPathes)
    {
        NSUInteger section = indexPath.section;
        
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_addImpressLabelsView cellForItemAtIndexPath:indexPath];
        NSString* labelName = cell.labelName;
        switch (section)
        {
            case ASSESSLABELSVIEW_SECTION_INDEX_ASSESSLABELS:
            {
                [_addImpressLabelNames removeObject:labelName];
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    [self _refershAddImpressLabelsView];
}

#pragma mark - ChatAssessExistImpressLabelsHeaderViewDelegate

-(void) didCloneImpressLabel
{
    NSArray* selectedIndexPathes = _existImpressLabelsView.indexPathsForSelectedItems;
    for (NSIndexPath* indexPath in selectedIndexPathes)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_existImpressLabelsView cellForItemAtIndexPath:indexPath];
        NSString* labelName = cell.labelName;
        
        [_addImpressLabelNames addObject:labelName];
    }
    
    _allowCloneLabel = NO;
    
    [self _refershAddImpressLabelsView];
    [self _refreshExistImpressLabelsView];
}

@end
