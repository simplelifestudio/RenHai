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
#define EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS_3_5 6
#define EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS_4 12

#define DELAY_REFRESH 0.5f

#define COUNTDOWN_SECONDS 300

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
    
//    NSUInteger _assessLabelPosition;
    
    BOOL _allowCloneLabel;
    
    UITapGestureRecognizer* _singleTapGesturer;
    UITapGestureRecognizer* _doubleTapGesturer;
    UILongPressGestureRecognizer* _longPressGesturer;
    
    volatile BOOL _assessSuccessFlag;
    
    volatile BOOL _isLabelManaging;
}

@end

@implementation ChatAssessViewController_iPhone

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
    
    [self _deselectCollectionView:_assessLabelsView exceptIndexPath:nil];
    [self _deselectCollectionView:_addImpressLabelsView exceptIndexPath:nil];
    [self _deselectCollectionView:_existImpressLabelsView exceptIndexPath:nil];
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

#pragma mark - ChatWizardPage

-(void) resetPage
{
    _assessSuccessFlag = NO;
    
    _continueButton.hidden = NO;
    _finishButton.hidden = NO;
    
//    _assessLabelPosition = 0;
    
    _allowCloneLabel = NO;    
    
    [_addImpressLabelNames removeAllObjects];
    
    [self _refreshAssessLabelsView];
    [self _refershAddImpressLabelsView];
    [self _refreshExistImpressLabelsView];
    
    [self _setCountdownSeconds:COUNTDOWN_SECONDS];
    
    self.navigationItem.title = NSLocalizedString(@"ChatAssess_Title", nil);
    [self.navigationController setNavigationBarHidden:NO];
    [self.navigationItem setHidesBackButton:YES];
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
    
//    _assessLabelPosition = 0;
    
    [self _setLabelManagingStatus:NO];
    
    [self _setupCollectionView];
    
    [self _setupGesturers];
    
    [self _setupNavigationBar];
    
// 已在IB上设置
//    if ([UIDevice isRunningOniOS7AndLater])
//    {
//        self.edgesForExtendedLayout = UIRectEdgeNone;
//        self.extendedLayoutIncludesOpaqueBars = NO;
//        self.automaticallyAdjustsScrollViewInsets = NO;
//    }
    
    [self _setupActionButtons];
}

- (void) _setupActionButtons
{
    [_continueButton setTitle:NSLocalizedString(@"ChatAssess_Action_Continue", nil) forState:UIControlStateNormal];
    [_finishButton setTitle:NSLocalizedString(@"ChatAssess_Action_Finish", nil) forState:UIControlStateNormal];
    
    _continueButton.buttonColor = FLATUI_COLOR_BUTTONPROCESS;
    [_continueButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_continueButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    _finishButton.buttonColor = FLATUI_COLOR_BUTTONROLLBACK;
    [_finishButton setTitleColor:FLATUI_COLOR_TEXT_INFO forState:UIControlStateNormal];
    [_finishButton setTitleColor:FLATUI_COLOR_BUTTONTITLE forState:UIControlStateHighlighted];
    
    [_continueButton setExclusiveTouch:YES];    
    [_finishButton setExclusiveTouch:YES];
}

- (void) _setupNavigationBar
{
    [self.navigationController.navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_NAVIGATIONBAR_CHATWIZARD];
}

-(void)_setupGesturers
{
    UIView* touchView = _guiModule.chatWizardController.view;//self.view;
    
    _singleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didSingleTapped:)];
    _singleTapGesturer.delegate = self;
    _singleTapGesturer.numberOfTapsRequired = 1;
    [touchView addGestureRecognizer:_singleTapGesturer];
    
    _doubleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didDoubleTapped:)];
    _doubleTapGesturer.delegate = self;
    _doubleTapGesturer.numberOfTapsRequired = 2;
    [touchView addGestureRecognizer:_doubleTapGesturer];
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
    
    _assessLabelsView.allowsMultipleSelection = NO;
    _addImpressLabelsView.allowsMultipleSelection = NO;
    _existImpressLabelsView.allowsMultipleSelection = NO;
    
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

    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[self _selectedCell:_assessLabelsView];
    NSIndexPath* indexPath = [_assessLabelsView indexPathForCell:cell];
    if (nil != indexPath)
    {
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
    [CBAppUtils asyncProcessInMainThread:^(){
        _continueButton.hidden = YES;
        _finishButton.hidden = YES;
    }];
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){

        RHDevice* selfDevice = _userDataModule.device;
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        NSString* businessSessionId = businessSession.businessSessionId;
        RHBusinessType businessType = businessSession.businessType;
        RHDevice* partnerDevice = businessSession.device;
        RHProfile* profile = partnerDevice.profile;
        
        RHImpressCard* impressCard = [self _getPartnerAssessedImpressCard];
        profile.impressCard = impressCard;
        
        NSDictionary* info = partnerDevice.toJSONObject;
        
        RHMessage* requestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:businessType operationType:requestType device:selfDevice info:info];
        
        __block AppMessageIdentifier appMessageId = AppMessageIdentifier_Disconnect;
        
        [_commModule businessSessionRequest:requestMessage
            successCompletionBlock:^(){
                
                switch (requestType)
                {
                    case BusinessSessionRequestType_AssessAndContinue:
                    {
                        appMessageId = AppMessageIdentifier_AssessAndContinue;
                        
                        [_statusModule recordAppMessage:appMessageId];
                        [self _moveToChatWaitView];
                        break;
                    }
                    case BusinessSessionRequestType_AssessAndQuit:
                    {
                        appMessageId = AppMessageIdentifier_AssessAndQuit;
                        
                        [_statusModule recordAppMessage:appMessageId];
                        [self _moveToHomeView];
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                
                _assessSuccessFlag = YES;
            }
            failureCompletionBlock:^(){
                _assessSuccessFlag = NO;
                [_statusModule recordCommunicateAbnormal:appMessageId];
            }
            afterCompletionBlock:^(){
                [CBAppUtils asyncProcessInMainThread:^(){
                    _continueButton.hidden = _assessSuccessFlag;
                    _finishButton.hidden = _assessSuccessFlag;
                }];
            }
         ];
    }];
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

}

- (void) _setCountdownSeconds:(NSUInteger) seconds
{
    if (0 < seconds)
    {
        _countdownSeconds = seconds;
        [_countLabel setText:[NSString stringWithFormat:@"%d", _countdownSeconds]];
    }
}

-(RHCollectionLabelCell_iPhone*)_selectedCell:(UICollectionView*) collectionView
{
    RHCollectionLabelCell_iPhone* selectedCell = nil;
    
    NSArray* selectedIndexPathes = collectionView.indexPathsForSelectedItems;
    for (NSIndexPath* indexPath in selectedIndexPathes)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView cellForItemAtIndexPath:indexPath];
        if (nil != cell)
        {
            selectedCell = cell;
            break;
        }
    }
    
    return selectedCell;
}

-(void)_resetAssessLabelsViewCellAsDefault:(RHCollectionLabelCell_iPhone*) cell indexPath:(NSIndexPath*) indexPath
{
    NSString* name = _assessLabelNames[indexPath.row];
    if ([name isEqualToString:MESSAGE_KEY_ASSESS_HAPPY])
    {
        [_assessLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
    }
    else
    {
        [_assessLabelsView deselectItemAtIndexPath:indexPath animated:NO];
    }
}

-(void)_resetAssessLabelsViewAsDefault
{
    NSArray* indexPathes = _assessLabelsView.indexPathsForVisibleItems;
    
    for (NSIndexPath* indexPath in indexPathes)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_assessLabelsView cellForItemAtIndexPath:indexPath];

        [self _resetAssessLabelsViewCellAsDefault:cell indexPath:indexPath];
    }
}

-(void)_deselectCollectionView:(UICollectionView*) collectionView exceptIndexPath:(NSIndexPath*) exceptIndexPath
{
    if (_assessLabelsView == collectionView)
    {
        NSArray* indexPathes = _assessLabelsView.indexPathsForSelectedItems;
        if (nil == exceptIndexPath)
        {
            [self _resetAssessLabelsViewAsDefault];
        }
        else
        {
            for (NSIndexPath* indexPath in indexPathes)
            {
                if (![indexPath isEqual:exceptIndexPath])
                {
                    [_assessLabelsView deselectItemAtIndexPath:indexPath animated:NO];
                }
            }
        }
        
        [self _refreshAssessLabelsViewActions];
    }
    else if (_addImpressLabelsView == collectionView)
    {
        NSArray* indexPathes = _addImpressLabelsView.indexPathsForSelectedItems;
        for (NSIndexPath* indexPath in indexPathes)
        {
            if (![indexPath isEqual:exceptIndexPath])
            {
                [_addImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        [self _refershAddImpressLabelsViewActions];
    }
    else if (_existImpressLabelsView == collectionView)
    {
        NSArray* indexPathes = _existImpressLabelsView.indexPathsForSelectedItems;
        for (NSIndexPath* indexPath in indexPathes)
        {
            if (![indexPath isEqual:exceptIndexPath])
            {
                [_existImpressLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        if (nil == exceptIndexPath)
        {
            _allowCloneLabel = NO;
        }
        
        [self _refreshExistImpressLabelsViewActions];
    }
}

-(void)_didLongPressed:(UILongPressGestureRecognizer*) recognizer
{

}

-(void)_didSingleTapped:(UITapGestureRecognizer*) recognizer
{
    if (_isLabelManaging)
    {
        [self _dismissPopupViewController];
        
        return;
    }
    
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_assessLabelsView.frame, locationTouch))
    {
        locationTouch = [_assessLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_assessLabelsView indexPathForItemAtPoint:locationTouch];
        [_assessLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
        
        [self _deselectCollectionView:_assessLabelsView exceptIndexPath:indexPath];
        
        [self _refreshAssessLabelsViewActions];
    }
    else if (CGRectContainsPoint(_addImpressLabelsView.frame, locationTouch))
    {
        locationTouch = [_addImpressLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_addImpressLabelsView indexPathForItemAtPoint:locationTouch];
        [_addImpressLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
        
        [self _deselectCollectionView:_addImpressLabelsView exceptIndexPath:indexPath];
        [self _deselectCollectionView:_existImpressLabelsView exceptIndexPath:nil];
    }
    else if (CGRectContainsPoint(_existImpressLabelsView.frame, locationTouch))
    {
        locationTouch = [_existImpressLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_existImpressLabelsView indexPathForItemAtPoint:locationTouch];

        [_existImpressLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
        RHCollectionLabelCell_iPhone* cell = [self _selectedCell:_existImpressLabelsView];
        
        if (nil != cell)
        {
            NSString* labelName = cell.labelName;
            
            BOOL hasLabel = [_addImpressLabelNames containsObject:labelName];
            BOOL isFull = (_addImpressLabelNames.count >= ADDIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_ADDIMPRESSLABELS);
            _allowCloneLabel = (hasLabel || isFull) ? NO : YES;
        }
        else
        {
            _allowCloneLabel = NO;
        }
        
        [self _deselectCollectionView:_addImpressLabelsView exceptIndexPath:nil];
        [self _deselectCollectionView:_existImpressLabelsView exceptIndexPath:indexPath];
    }
}

-(void)_didDoubleTapped:(UITapGestureRecognizer*) recognizer
{
    if (_isLabelManaging)
    {
        [self _dismissPopupViewController];
        
        return;
    }
    
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_addImpressLabelsView.frame, locationTouch))
    {
        locationTouch = [_addImpressLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_addImpressLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil != indexPath)
        {
            [self _deselectCollectionView:_addImpressLabelsView exceptIndexPath:indexPath];
            
            NSUInteger section = indexPath.section;
            NSUInteger position = indexPath.item;
            
            switch (section)
            {
                case ADDIMPRESSLABELSVIEW_SECTION_INDEX_ADDIMPRESSLABELS:
                {
                    NSString* labeName = _addImpressLabelNames[position];
                    
                    UIViewController* rootVC = [CBUIUtils getRootController];
                    RHLabelManageViewController_iPhone* labelManagerVC = [RHLabelManageViewController_iPhone modifyLabelManagerViewController:self label:labeName];
                    rootVC.useBlurForPopup = NO;
                    [rootVC presentPopupViewController:labelManagerVC animated:YES completion:nil];

                    [self _setLabelManagingStatus:YES];
                    
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
    
    [self _resetAssessLabelsViewAsDefault];
    
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
    
    NSArray* indexPathes = [_addImpressLabelsView indexPathsForSelectedItems];
    if (0 < indexPathes.count)
    {
        allowDeleteLabel = YES;
    }
    else
    {
        allowDeleteLabel = NO;
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

-(void)_setLabelManagingStatus:(BOOL) isManaging
{
    _isLabelManaging = isManaging;
    
    BOOL userInteractionEnabled = !isManaging;
    
    _assessLabelsHeaderView.userInteractionEnabled = userInteractionEnabled;
    _assessLabelsView.userInteractionEnabled = userInteractionEnabled;
    
    _addImpressLabelsHeaderView.userInteractionEnabled = userInteractionEnabled;
    _addImpressLabelsView.userInteractionEnabled = userInteractionEnabled;
    
    _existImpressLabelsHeaderView.userInteractionEnabled = userInteractionEnabled;
    _existImpressLabelsView.userInteractionEnabled = userInteractionEnabled;
}

#pragma mark - UICollectionViewDelegate

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (collectionView == _assessLabelsView)
    {

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
    RHDevice* partnerDevice = businessSession.device;
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
        NSUInteger requireCount = 0;
        if (IS_IPHONE5)
        {
            requireCount = EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS_4;
        }
        else
        {
            requireCount = EXISTIMPRESSLABELSVIEW_SECTION_ITEMCOUNT_EXISTIMPRESSLABELS_3_5;
        }
        
        itemsCount = (impressLabels.count <= requireCount) ? impressLabels.count : requireCount;
    }
    
    return itemsCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)cv cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[cv dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL forIndexPath:indexPath];
    
    NSUInteger position = indexPath.item;
    
    if (cv == _assessLabelsView)
    {
        cell.customBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUND;
        cell.customSelectedBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUNDSELECTED;
        
        NSString* labelName = nil;
        NSInteger labelCount = -1;

        NSString* name = _assessLabelNames[position];
        labelName = [RHImpressLabel assessLabelName:name];
        
        [self _resetAssessLabelsViewCellAsDefault:cell indexPath:indexPath];
        
        cell.textField.text = labelName;
        if (0 <= labelCount)
        {
            cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
        }
        else
        {
            cell.countLabel.text = @"";
        }
        
        cell.countLabel.hidden = YES;
        
        cell.selected = NO;
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
        
        cell.customBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUND;
        cell.customSelectedBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUNDSELECTED;
        
        cell.selected = NO;
    }
    else if (cv == _existImpressLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        RHDevice* partnerDevice = businessSession.device;
        RHProfile* partnerProfile = partnerDevice.profile;
        RHImpressCard* partnerImpressCard = partnerProfile.impressCard;
        NSArray* labelList = partnerImpressCard.impressLabelList;
        
        if (0 < labelList.count && position < labelList.count)
        {
            RHImpressLabel* label = labelList[position];
            labelName = label.labelName;
            labelCount = label.assessedCount;
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
        
        cell.customBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUND;
        cell.customSelectedBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUNDSELECTED;
        
        cell.selected = NO;
    }
    
    cell.highlighted = NO;
    
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
    
    reusableView.backgroundColor = FLATUI_COLOR_UICOLLECTIONREUSABLEVIEW_BACKGROUND;
    
    return reusableView;
}

#pragma mark - IBActions

- (IBAction)didPressContinueButton:(id)sender
{
    [_guiModule playSound:SOUNDID_CHATASSESS_CONTINUE];
    
    [self _remoteUpdatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndContinue];
}

- (IBAction)didPressFinishButton:(id)sender
{
    [_guiModule playSound:SOUNDID_CHATASSESS_QUIT];
    
    [self _remoteUpdatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndQuit];
}

#pragma mark - RHLabelManageDelegate

-(void) didLabelManageDone:(ManageMode) mode newLabel:(NSString*) newLabel oldLabel:(NSString*) oldLabel
{
    switch (mode)
    {
        case ManageMode_NewLabel:
        {
            if (![_addImpressLabelNames containsObject:newLabel])
            {
                [_addImpressLabelNames addObject:newLabel];                
            }
            
            break;
        }
        case ManageMode_ModifyLabel:
        {
            if (![oldLabel isEqualToString:newLabel])
            {
                NSUInteger labelIndex = [_addImpressLabelNames indexOfObject:oldLabel];
                
                if ([_addImpressLabelNames containsObject:newLabel])
                {
                    [_addImpressLabelNames removeObjectAtIndex:labelIndex];
                }
                else
                {
                    [_addImpressLabelNames removeObjectAtIndex:labelIndex];
                    [_addImpressLabelNames insertObject:newLabel atIndex:labelIndex];
                }
            }
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    [self _refershAddImpressLabelsView];
    
    [self _dismissPopupViewController];
}

-(void) didLabelManageCancel
{
    [self _dismissPopupViewController];
}

- (void)_dismissPopupViewController
{
    UIViewController* rootVC = [CBUIUtils getRootController];
    if (rootVC.popupViewController != nil)
    {
        [rootVC dismissPopupViewControllerAnimated:YES completion:^{

        }];
    }
    
    [self _setLabelManagingStatus:NO];
}

#pragma mark - ChatAssessAddImpressLabelsHeaderViewDelegate

-(void) didCreateImpressLabel
{
    if (_isLabelManaging)
    {
        return;
    }
    
    UIViewController* rootVC = [CBUIUtils getRootController];
    RHLabelManageViewController_iPhone* labelManageVC = [RHLabelManageViewController_iPhone newLabelManageViewController:self];
    rootVC.useBlurForPopup = NO;
    [rootVC presentPopupViewController:labelManageVC animated:YES completion:nil];

    [self _setLabelManagingStatus:YES];
}

-(void) didDeleteImpressLabel
{
    if (_isLabelManaging)
    {
        return;
    }
    
    RHCollectionLabelCell_iPhone* cell = [self _selectedCell:_addImpressLabelsView];
    if (nil != cell)
    {
        NSString* labelName = cell.labelName;
        [_addImpressLabelNames removeObject:labelName];
    }
    
    [self _refershAddImpressLabelsView];
}

#pragma mark - ChatAssessExistImpressLabelsHeaderViewDelegate

-(void) didCloneImpressLabel
{
    if (_isLabelManaging)
    {
        return;
    }
    
    RHCollectionLabelCell_iPhone* cell = [self _selectedCell:_existImpressLabelsView];
    if (nil != cell)
    {
        NSString* labelName = cell.labelName;
        [_addImpressLabelNames addObject:labelName];
    }
    
    _allowCloneLabel = NO;
    [self _refershAddImpressLabelsView];
    [self _refreshExistImpressLabelsView];
}

@end
