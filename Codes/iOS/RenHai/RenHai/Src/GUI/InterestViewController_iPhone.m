//
//  InterestViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "InterestViewController_iPhone.h"

#import "UserDataModule.h"
#import "CommunicationModule.h"
#import "GUIModule.h"
#import "GUIStyle.h"

#import "UIViewController+CWPopup.h"

#import "RHInterestLabel.h"
#import "RHCollectionLabelCell_iPhone.h"
#import "RHLabelManageViewController_iPhone.h"
#import "ServerInterestLabelsViewLayout.h"

#define INTERESTLABELS_SECTION_COUNT 1
#define SERVERINTERESTLABELS_SECTION_COUNT 1

#define INTERESTLABELS_SECTION_INDEX_INTERESTLABELS 0
#define INTERESTLABELS_SECTION_ITEMCOUNT_INTERESTLABELS 6

#define SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS 0
#define SERVERINTERESTLABELS_SECTION_ITEMCOUNT_SERVERINTERESTLABELS_3_5 9
#define SERVERINTERESTLABELS_SECTION_ITEMCOUNT_SERVERINTERESTLABELS_4 12

@interface InterestViewController_iPhone () <InterestLabelsHeaderViewDelegate, ServerInterestLabelsHeaderViewDelegate, RHLabelManageDelegate, UIGestureRecognizerDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    
    InterestLabelsHeaderView_iPhone* _interestLabelsHeaderView;
    ServerInterestLabelsHeaderView_iPhone* _serverInterestLabelsHeaderView;
    
    BOOL _allowCloneLabel;
    
    UITapGestureRecognizer* _singleTapGesturer;
    UITapGestureRecognizer* _doubleTapGesturer;
    UILongPressGestureRecognizer* _longPressGesturer;
}

@end

@implementation InterestViewController_iPhone

@synthesize interestLabelsView = _interestLabelsView;
@synthesize serverInterestLabelsView = _serverInterestLabelsView;
@synthesize pageControl = _pageControl;

#pragma mark - Public Methods

- (void)awakeFromNib
{
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

-(void) viewDidAppear:(BOOL)animated
{    
    [super viewDidAppear:animated];
    
    [self _refreshInterestLabelsView];
    
    [self _refreshServerInterestLabelsView];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    [self _setupCollectionView];
    
    [self _setupGestuers];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_INTEREST;
    
    [self.navigationController.navigationBar configureFlatNavigationBarWithColor:FLATUI_COLOR_NAVIGATIONBAR_MAIN];
    if (![UIDevice isRunningOniOS7AndLater])
    {
        [self.navigationItem.leftBarButtonItem configureFlatButtonWithColor:FLATUI_COLOR_BARBUTTONITEM highlightedColor:FLATUI_COLOR_BARBUTTONITEM_HIGHLIGHTED cornerRadius:FLATUI_CORNERRADIUS_BARBUTTONITEM];
    }
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if ([self.navigationController.revealController hasLeftViewController])
    {
        UIBarButtonItem* leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:sidebarMenuIcon_portrait landscapeImagePhone:sidebarMenuIcon_landscape style:UIBarButtonItemStylePlain target:self action:@selector(_leftBarButtonItemClicked:)];
        
        self.navigationItem.leftBarButtonItem = leftBarButtonItem;
    }
}

-(void)_setupGestuers
{
    _singleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didSingleTapped:)];
    _singleTapGesturer.delegate = self;
    _singleTapGesturer.numberOfTapsRequired = 1;
    [self.view addGestureRecognizer:_singleTapGesturer];
    
    _doubleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didDoubleTapped:)];
    _doubleTapGesturer.delegate = self;
    _doubleTapGesturer.numberOfTapsRequired = 2;
    [self.view addGestureRecognizer:_doubleTapGesturer];
    
//    _longPressGesturer = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(_didLongPressed:)];
//    _longPressGesturer.delegate = self;
//    _longPressGesturer.minimumPressDuration = 1.0f;
//    [self.view addGestureRecognizer:_longPressGesturer];
//    
//    [_singleTapGesturer requireGestureRecognizerToFail:_longPressGesturer];
}

-(void)_leftBarButtonItemClicked:(id)sender
{
    if (self.navigationController.revealController.focusedController == self.navigationController.revealController.leftViewController)
    {
        [self.navigationController.revealController showViewController:self.navigationController.revealController.frontViewController];
    }
    else
    {
        [self.navigationController.revealController showViewController:self.navigationController.revealController.leftViewController];
    }
}

-(void)_setupCollectionView
{
    _interestLabelsView.delegate = self;
    _interestLabelsView.dataSource = self;
    
    _serverInterestLabelsView.delegate = self;
    _serverInterestLabelsView.dataSource = self;
    
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    [_interestLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL];
    [_serverInterestLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL];
    
    nib = [UINib nibWithNibName:NIB_INTERESTLABELSHEADERVIEW bundle:nil];
    [_interestLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW];
    
    nib = [UINib nibWithNibName:NIB_SERVERINTERESTLABELSHEADERVIEW bundle:nil];
    [_serverInterestLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW];
}

-(void)_setupInterestLabelsHeaderView:(UICollectionView*) collectionView kind:(NSString*) kind atIndexPath:(NSIndexPath*) indexPath
{
    if (nil == _interestLabelsHeaderView)
    {
        _interestLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW forIndexPath:indexPath];
        _interestLabelsHeaderView.headerTitleLabel.text = NSLocalizedString(@"Interest_InterestLabels", nil);
        [_interestLabelsHeaderView.createButton setTitle:NSLocalizedString(@"Interest_Action_CreateInterestLabel", nil) forState:UIControlStateNormal];
        [_interestLabelsHeaderView.delButton setTitle:NSLocalizedString(@"Interest_Action_DeleteInterestLabel", nil) forState:UIControlStateNormal];
        _interestLabelsHeaderView.operationDelegate = self;
        
        [self _refreshInterestLabelsHeaderViewActions];
    }
}

-(void)_setupServerInterestLabelsHeaderView:(UICollectionView*) collectionView kind:(NSString*) kind atIndexPath:(NSIndexPath*) indexPath
{
    if (nil == _serverInterestLabelsHeaderView)
    {
        _serverInterestLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW forIndexPath:indexPath];
        _serverInterestLabelsHeaderView.headerTitleLabel.text = NSLocalizedString(@"Interest_CurrentHotInterestLabels", nil);
        [_serverInterestLabelsHeaderView.cloneButton setTitle:NSLocalizedString(@"Interest_Action_CloneServerInterestLabel", nil) forState:UIControlStateNormal];
        [_serverInterestLabelsHeaderView.refreshButton setTitle:NSLocalizedString(@"Interest_Action_RefreshServerInterestLabels", nil) forState:UIControlStateNormal];
        _serverInterestLabelsHeaderView.operationDelegate = self;
        
        [self _refreshServerInterestLabelsHeaderViewActions];
    }
}

-(void)_didLongPressed:(UILongPressGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_interestLabelsView.frame, locationTouch))
    {
        locationTouch = [_interestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_interestLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        else
        {
            [_interestLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            
            NSArray* selectedIndexPathes = _serverInterestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_serverInterestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
            
            [self _refreshInterestLabelsHeaderViewActions];
        }
    }
    else if (CGRectContainsPoint(_serverInterestLabelsView.frame, locationTouch))
    {
        locationTouch = [_serverInterestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_serverInterestLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _serverInterestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_serverInterestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        else
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_serverInterestLabelsView cellForItemAtIndexPath:indexPath];
            [_serverInterestLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            
            NSString* labelName = cell.labelName;
            
            RHDevice* device = _userDataModule.device;
            RHProfile* profile = device.profile;
            RHInterestCard* interestCard = profile.interestCard;
            BOOL hasLabel = [interestCard isLabelExists:labelName];
            BOOL isFull = (interestCard.labelList.count >= INTERESTLABELS_SECTION_ITEMCOUNT_INTERESTLABELS);
            _allowCloneLabel = (hasLabel || isFull) ? NO : YES;
            
            NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        [self _refreshServerInterestLabelsHeaderViewActions];
    }
}

-(void)_didSingleTapped:(UITapGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_interestLabelsView.frame, locationTouch))
    {
        locationTouch = [_interestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_interestLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {      
                [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        else
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_interestLabelsView cellForItemAtIndexPath:indexPath];
            if (!cell.isSelected)
            {
                [_interestLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            }
            else
            {
                [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        [self _refreshInterestLabelsHeaderViewActions];        
        
        NSArray* selectedIndexPathes = _serverInterestLabelsView.indexPathsForSelectedItems;
        for (NSIndexPath* indexPath in selectedIndexPathes)
        {
            [_serverInterestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
        }
        _allowCloneLabel = NO;
        [self _refreshServerInterestLabelsHeaderViewActions];
    }
    else if (CGRectContainsPoint(_serverInterestLabelsView.frame, locationTouch))
    {
        locationTouch = [_serverInterestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_serverInterestLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil == indexPath)
        {
            NSArray* selectedIndexPathes = _serverInterestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_serverInterestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
            
            selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
            [self _refreshInterestLabelsHeaderViewActions];
        }
        else
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_serverInterestLabelsView cellForItemAtIndexPath:indexPath];
            if (!cell.isSelected)
            {
                [_serverInterestLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
                
                NSString* labelName = cell.labelName;
                
                RHDevice* device = _userDataModule.device;
                RHProfile* profile = device.profile;
                RHInterestCard* interestCard = profile.interestCard;
                BOOL hasLabel = [interestCard isLabelExists:labelName];
                BOOL isFull = (interestCard.labelList.count >= INTERESTLABELS_SECTION_ITEMCOUNT_INTERESTLABELS);
                _allowCloneLabel = (hasLabel || isFull) ? NO : YES;
            }
            else
            {
                [_serverInterestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
                _allowCloneLabel = NO;
            }
            
            NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
            for (NSIndexPath* indexPath in selectedIndexPathes)
            {
                [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
            }
        }
        
        [self _refreshServerInterestLabelsHeaderViewActions];
    }
}

-(void)_didDoubleTapped:(UITapGestureRecognizer*) recognizer
{
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_interestLabelsView.frame, locationTouch))
    {
        locationTouch = [_interestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_interestLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil != indexPath)
        {
            NSUInteger section = indexPath.section;
            NSUInteger position = indexPath.item;
            
            switch (section)
            {
                case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
                {
                    RHDevice* device = _userDataModule.device;
                    RHProfile* profile = device.profile;
                    RHInterestCard* interestCard = profile.interestCard;
                    
                    RHInterestLabel* oLabel = [interestCard getLabelByIndex:position];
                    
                    UIViewController* rootVC = [CBUIUtils getRootController];
                    RHLabelManageViewController_iPhone* labelManagerVC = [RHLabelManageViewController_iPhone modifyLabelManagerViewController:self label:oLabel.labelName];
                    rootVC.useBlurForPopup = YES;
                    [rootVC presentPopupViewController:labelManagerVC animated:YES completion:nil];
                    
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

-(void)_refreshInterestLabelsHeaderViewActions
{
    BOOL allowCreateLabel = YES;
    BOOL allowDeleteLabel = NO;
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    NSArray* labelList = interestCard.labelList;
    
    NSArray* selectedCells = [_interestLabelsView indexPathsForSelectedItems];
    if (0 < selectedCells.count && 1 < labelList.count)
    {
        allowDeleteLabel = YES;
    }
    
    if (INTERESTLABELS_SECTION_ITEMCOUNT_INTERESTLABELS <= labelList.count)
    {
        allowCreateLabel = NO;
    }
    
    _interestLabelsHeaderView.createButton.enabled = allowCreateLabel;
    _interestLabelsHeaderView.delButton.enabled = allowDeleteLabel;
}

-(void)_refreshServerInterestLabelsHeaderViewActions
{
    BOOL allowRefreshLabels = YES;
    BOOL allowCloneLabel = _allowCloneLabel;
    
    _serverInterestLabelsHeaderView.cloneButton.enabled = allowCloneLabel;
    _serverInterestLabelsHeaderView.refreshButton.enabled = allowRefreshLabels;
}

-(void)_refreshInterestLabelsView
{
    [_interestLabelsView reloadData];
    
    [self _refreshInterestLabelsHeaderViewActions];
}

-(void)_refreshServerInterestLabelsView
{
    [_serverInterestLabelsView reloadData];

    [self _refreshServerInterestLabelsHeaderViewActions];
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    NSInteger sectionCount = 0;
    
    if (collectionView == _interestLabelsView)
    {
        sectionCount = INTERESTLABELS_SECTION_COUNT;
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        sectionCount = SERVERINTERESTLABELS_SECTION_COUNT;
    }
    
    return sectionCount;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    NSInteger itemsCount = 0;
    
    if (collectionView == _interestLabelsView)
    {
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                RHDevice* device = _userDataModule.device;
                RHProfile* profile = device.profile;
                RHInterestCard* interestCard = profile.interestCard;
                NSArray* interestLabels = interestCard.labelList;
                
                itemsCount = (interestLabels.count <= INTERESTLABELS_SECTION_ITEMCOUNT_INTERESTLABELS) ? interestLabels.count : INTERESTLABELS_SECTION_ITEMCOUNT_INTERESTLABELS;
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        NSUInteger requireCount = 0;
        if (IS_IPHONE5)
        {
            requireCount = SERVERINTERESTLABELS_SECTION_ITEMCOUNT_SERVERINTERESTLABELS_4;
        }
        else if (IS_IPHONE4_OR_4S)
        {
            requireCount = SERVERINTERESTLABELS_SECTION_ITEMCOUNT_SERVERINTERESTLABELS_3_5;
        }
        else if (IS_IPAD1_OR_2_OR_MINI)
        {
            requireCount = SERVERINTERESTLABELS_SECTION_ITEMCOUNT_SERVERINTERESTLABELS_3_5;
        }
        
        switch (section)
        {
            case SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS:
            {
                RHServerData* server = _userDataModule.server;
                RHServerInterestLabelList* interestLabelList = server.interestLabelList;
                NSArray* currentInterestLabels = interestLabelList.current;
                
                itemsCount = (currentInterestLabels.count <= requireCount) ? currentInterestLabels.count : requireCount;

                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    return itemsCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL forIndexPath:indexPath];
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    RHServerData* server = _userDataModule.server;

    NSUInteger section = indexPath.section;
    NSUInteger position = indexPath.item;
    
    if (collectionView == _interestLabelsView)
    {
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                NSString* labelName = nil;
                NSInteger labelCount = 0;
                
                NSArray* labelList = interestCard.labelList;
                
                RHInterestLabel* label = labelList[position];
                labelName = label.labelName;
                labelCount = label.globalMatchCount;
                
                cell.textField.text = labelName;
//                cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        switch (section)
        {
            case SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS:
            {
                NSString* labelName = nil;
                NSInteger labelCount = 0;
                
                RHServerInterestLabelList* olabelList = server.interestLabelList;
                NSArray* labelList = olabelList.current;

                RHInterestLabel* label = labelList[position];
                labelName = label.labelName;
                labelCount = label.currentProfileCount;
                
                cell.textField.text = labelName;
                cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    return cell;
}

-(UICollectionReusableView *) collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView* reusableView = nil;
    
    NSUInteger section = indexPath.section;
    
    if (collectionView == _interestLabelsView)
    {
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                [self _setupInterestLabelsHeaderView:_interestLabelsView kind:kind atIndexPath:indexPath];
                reusableView = _interestLabelsHeaderView;
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        switch (section)
        {
            case SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS:
            {
                [self _setupServerInterestLabelsHeaderView:_serverInterestLabelsView kind:kind atIndexPath:indexPath];
                reusableView = _serverInterestLabelsHeaderView;
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    return reusableView;
}

#pragma mark - UICollectionViewDataSource_Draggable

- (BOOL)collectionView:(UICollectionView *)collectionView canMoveItemAtIndexPath:(NSIndexPath *)indexPath
{
    BOOL flag = NO;
    
    NSUInteger section = indexPath.section;
    
    if (collectionView == _interestLabelsView)
    {
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                flag = YES;
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        switch (section)
        {
            case SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS:
            {
                flag = NO;
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    return flag;
}

- (BOOL)collectionView:(UICollectionView *)collectionView canMoveItemAtIndexPath:(NSIndexPath *)indexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    BOOL flag = NO;
    
    NSUInteger fromSection = indexPath.section;
    NSUInteger toSection = toIndexPath.section;
    
    if (collectionView == _interestLabelsView)
    {
        if (fromSection != INTERESTLABELS_SECTION_INDEX_INTERESTLABELS || fromSection != toSection)
        {
            flag = NO;
        }
        else
        {
            flag = YES;
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        flag = NO;
    }
    
    return flag;
}

- (void)collectionView:(LSCollectionViewHelper *)cv moveItemAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    if (fromIndexPath == toIndexPath)
    {
        return;
    }
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    NSUInteger fromSection = fromIndexPath.section;
    NSUInteger fromPosition = fromIndexPath.item;
    
    NSUInteger toSection = toIndexPath.section;
    NSUInteger toPosition = toIndexPath.item;
    
    UICollectionView* collectionView = (UICollectionView*)cv;
    
    if (collectionView == _interestLabelsView)
    {
        if (fromSection != toSection)
        {
            return;
        }
        
//        NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
//        for (NSIndexPath* indexPath in selectedIndexPathes)
//        {
//            [_interestLabelsView deselectItemAtIndexPath:indexPath animated:NO];
//        }
//        [_interestLabelsView selectItemAtIndexPath:fromIndexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
        
        switch (fromSection)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                [interestCard reorderLabel:fromPosition toIndex:toPosition];
                
                [self _remoteUpdateInterestCard];
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        
    }
}

- (CGAffineTransform)collectionView:(UICollectionView *)collectionView transformForDraggingItemAtIndexPath:(NSIndexPath *)indexPath duration:(NSTimeInterval *)duration
{
    return CGAffineTransformMakeScale(1.25f, 1.25f);
}

#pragma mark - UICollectionViewDelegate

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    
    if (collectionView == _interestLabelsView)
    {
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        switch (section)
        {
            case SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS:
            {
                break;
            }
            default:
            {
                break;
            }
        }
    }
}

-(void) collectionView:(UICollectionView *)collectionView didDeselectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    
    if (collectionView == _interestLabelsView)
    {
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _serverInterestLabelsView)
    {
        switch (section)
        {
            case SERVERINTERESTLABELS_SECTION_INDEX_SERVERINTERESTLABELS:
            {
                break;
            }
            default:
            {
                break;
            }
        }
    }
}

#pragma mark - InterestLabelsHeaderViewDelegate

-(void) didCreateInterestLabel
{
    UIViewController* rootVC = [CBUIUtils getRootController];
    RHLabelManageViewController_iPhone* labelManageVC = [RHLabelManageViewController_iPhone newLabelManageViewController:self];
    rootVC.useBlurForPopup = YES;
    [rootVC presentPopupViewController:labelManageVC animated:YES completion:nil];
}

-(void) didDeleteInterestLabel
{
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForSelectedItems;
    for (NSIndexPath* indexPath in selectedIndexPathes)
    {
        NSUInteger section = indexPath.section;
        
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_interestLabelsView cellForItemAtIndexPath:indexPath];
        NSString* labelName = cell.labelName;
        switch (section)
        {
            case INTERESTLABELS_SECTION_INDEX_INTERESTLABELS:
            {
                [interestCard removeLabelByName:labelName];
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    [self _remoteUpdateInterestCard];
}

#pragma mark - ServerInterestLabelsHeaderViewDelegate

-(void) didRefreshInterestLabels
{
    [self _remoteQueryServerInterestLabelList];
}

-(void) didCloneInterestLabel
{
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    NSArray* selectedIndexPathes = _serverInterestLabelsView.indexPathsForSelectedItems;
    for (NSIndexPath* indexPath in selectedIndexPathes)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_serverInterestLabelsView cellForItemAtIndexPath:indexPath];
        NSString* labelName = cell.labelName;
        
        [interestCard addLabel:labelName];
    }
    
    [self _remoteUpdateInterestCard];
    
    _allowCloneLabel = NO;
    [self _refreshServerInterestLabelsView];
}

#pragma mark - RHLabelManageDelegate

-(void) didLabelManageDone:(ManageMode) mode newLabel:(NSString*) newLabel oldLabel:(NSString*) oldLabel
{
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    switch (mode)
    {
        case ManageMode_NewLabel:
        {
            [interestCard addLabel:newLabel];
            
            break;
        }
        case ManageMode_ModifyLabel:
        {
            if (![oldLabel isEqualToString:newLabel])
            {
                NSUInteger labelIndex = [interestCard getLabelIndex:oldLabel];
                [interestCard removeLabelByIndex:labelIndex];
                [interestCard insertLabelByName:newLabel index:labelIndex];
            }
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    [self _remoteUpdateInterestCard];
    
    [_guiModule.mainViewController dismissPopupViewControllerAnimated:YES completion:nil];
}

-(void) didLabelManageCancel
{
    [_guiModule.mainViewController dismissPopupViewControllerAnimated:YES completion:nil];
}

#pragma mark - Remote Operations

-(void)_remoteUpdateInterestCard
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        RHDevice* device = _userDataModule.device;
        RHProfile* profile = device.profile;
        RHInterestCard* interestCard = profile.interestCard;
        RHMessage* requestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_InterestCardSync device:device info:nil];
        
        [_commModule appDataSyncRequest:requestMessage
             successCompletionBlock:^(NSDictionary* deviceDic){
                 deviceDic = [deviceDic objectForKey:MESSAGE_KEY_DEVICE];
                 NSDictionary* profileDic = [deviceDic objectForKey:MESSAGE_KEY_PROFILE];
                 NSDictionary* interestCardDic = [profileDic objectForKey:MESSAGE_KEY_INTERESTCARD];
                 @try
                 {
                     [interestCard fromJSONObject:interestCardDic];
                     
                     [_userDataModule saveUserData];
                 }
                 @catch (NSException *exception)
                 {
                     DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
                 }
                 @finally
                 {
                     
                 }
             }
             failureCompletionBlock:^(){
             }
             afterCompletionBlock:^(){
                   [CBAppUtils asyncProcessInMainThread:^(){
                       [self _refreshInterestLabelsView];
                   }];
               }
         ];
    }];
}

-(void)_remoteQueryServerInterestLabelList
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        RHDevice* device = _userDataModule.device;
        RHServerData* server = _userDataModule.server;
        
        RHMessage* requestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_InterestLabelListSync device:device info:nil];
        
        [_commModule serverDataSyncRequest:requestMessage
            successCompletionBlock:^(NSDictionary* serverDic){
                @try
                {
                    [server fromJSONObject:serverDic];
                    
                    [CBAppUtils asyncProcessInMainThread:^(){
                        _allowCloneLabel = NO;
                        [self _refreshServerInterestLabelsView];
                    }];
                }
                @catch (NSException *exception)
                {
                    DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
                }
                @finally
                {
                    
                }
            }
            failureCompletionBlock:^(){
            }
            afterCompletionBlock:^(){
            }
        ];
    }];
}

@end
