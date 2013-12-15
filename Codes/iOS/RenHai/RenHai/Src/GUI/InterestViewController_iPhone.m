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
#import "BusinessStatusModule.h"

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
    BusinessStatusModule* _statusModule;
    
    InterestLabelsHeaderView_iPhone* _interestLabelsHeaderView;
    ServerInterestLabelsHeaderView_iPhone* _serverInterestLabelsHeaderView;
    
    BOOL _allowCloneLabel;
    
    UITapGestureRecognizer* _singleTapGesturer;
    UITapGestureRecognizer* _doubleTapGesturer;
    UILongPressGestureRecognizer* _longPressGesturer;
    
    volatile BOOL _isLabelManaging;
}

@end

@implementation InterestViewController_iPhone

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
    
    [self _deselectCollectionView:_interestLabelsView exceptIndexPath:nil];
    [self _deselectCollectionView:_serverInterestLabelsView exceptIndexPath:nil];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _guiModule = [GUIModule sharedInstance];
    
    [self _setLabelManagingStatus:NO];
    
    [self _setupNavigationBar];
    [self _setupCollectionView];
    
    [self _setupGesturers];
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

-(void)_setupGesturers
{
    UIView* touchView = self.navigationController.view;

    _singleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didSingleTapped:)];
    _singleTapGesturer.delegate = self;
    _singleTapGesturer.numberOfTapsRequired = 1;
    [touchView addGestureRecognizer:_singleTapGesturer];
    
    _doubleTapGesturer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didDoubleTapped:)];
    _doubleTapGesturer.delegate = self;
    _doubleTapGesturer.numberOfTapsRequired = 2;
    [touchView addGestureRecognizer:_doubleTapGesturer];
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
    _interestLabelsView.allowsMultipleSelection = NO;
    
    _serverInterestLabelsView.delegate = self;
    _serverInterestLabelsView.dataSource = self;
    _serverInterestLabelsView.allowsMultipleSelection = NO;
    
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

-(RHCollectionLabelCell_iPhone*)_selectedCell:(UICollectionView*) collectionView
{
    RHCollectionLabelCell_iPhone* selectedCell = nil;
    
    NSArray* selectedIndexPathes = collectionView.indexPathsForVisibleItems;
    for (NSIndexPath* indexPath in selectedIndexPathes)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView cellForItemAtIndexPath:indexPath];
        if (cell.selected)
        {
            selectedCell = cell;
            break;
        }
    }
    
    return selectedCell;
}

-(void)_deselectCollectionView:(UICollectionView*) collectionView exceptIndexPath:(NSIndexPath*) exceptIndexPath
{
    if (_interestLabelsView == collectionView)
    {
        NSArray* selectedIndexPathes = _interestLabelsView.indexPathsForVisibleItems;
        for (NSIndexPath* indexPath in selectedIndexPathes)
        {
            if (![indexPath isEqual:exceptIndexPath])
            {
                [_interestLabelsView cellForItemAtIndexPath:indexPath].selected = NO;
            }
        }
        
        [self _refreshInterestLabelsHeaderViewActions];
    }
    else if (_serverInterestLabelsView == collectionView)
    {
        NSArray* selectedIndexPathes = _serverInterestLabelsView.indexPathsForVisibleItems;
        for (NSIndexPath* indexPath in selectedIndexPathes)
        {
            if (![indexPath isEqual:exceptIndexPath])
            {
                [_serverInterestLabelsView cellForItemAtIndexPath:indexPath].selected = NO;
            }
        }
        
        if (nil == exceptIndexPath)
        {
            _allowCloneLabel = NO;
        }
        
        [self _refreshServerInterestLabelsHeaderViewActions];
    }
}

-(void)_didSingleTapped:(UITapGestureRecognizer*) recognizer
{
    if (_isLabelManaging)
    {
        [self _dismissPopupViewController];
        
        return;
    }
    
    CGPoint locationTouch = [recognizer locationInView:self.view];
    
    if (CGRectContainsPoint(_interestLabelsView.frame, locationTouch))
    {
        locationTouch = [_interestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_interestLabelsView indexPathForItemAtPoint:locationTouch];
        
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_interestLabelsView cellForItemAtIndexPath:indexPath];
        if (!cell.isSelected)
        {
            cell.selected = YES;
        }
        else
        {
            cell.selected = NO;
        }
        
        [self _deselectCollectionView:_interestLabelsView exceptIndexPath:indexPath];
        [self _deselectCollectionView:_serverInterestLabelsView exceptIndexPath:nil];
    }
    else if (CGRectContainsPoint(_serverInterestLabelsView.frame, locationTouch))
    {
        locationTouch = [_serverInterestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_serverInterestLabelsView indexPathForItemAtPoint:locationTouch];
        
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_serverInterestLabelsView cellForItemAtIndexPath:indexPath];
        if (!cell.isSelected)
        {
            cell.selected = YES;
            
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
            cell.selected = NO;
            
            _allowCloneLabel = NO;
        }
        
        [self _deselectCollectionView:_interestLabelsView exceptIndexPath:nil];
        [self _deselectCollectionView:_serverInterestLabelsView exceptIndexPath:indexPath];
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
    
    if (CGRectContainsPoint(_interestLabelsView.frame, locationTouch))
    {
        locationTouch = [_interestLabelsView convertPoint:locationTouch fromView:self.view];
        
        NSIndexPath* indexPath = [_interestLabelsView indexPathForItemAtPoint:locationTouch];
        if (nil != indexPath)
        {
            RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[_interestLabelsView cellForItemAtIndexPath:indexPath];
            cell.selected = YES;
            [self _deselectCollectionView:_interestLabelsView exceptIndexPath:indexPath];
            
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

-(void)_refreshInterestLabelsHeaderViewActions
{
    BOOL allowCreateLabel = YES;
    BOOL allowDeleteLabel = NO;
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    NSArray* labelList = interestCard.labelList;
    
    NSArray* indexPathes = [_interestLabelsView indexPathsForVisibleItems];
    for (NSIndexPath* indexPath in indexPathes)
    {
        if ([_interestLabelsView cellForItemAtIndexPath:indexPath].selected)
        {
            allowDeleteLabel = YES;
            break;
        }
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

-(void)_setLabelManagingStatus:(BOOL) isManaging
{
    _isLabelManaging = isManaging;
    
    BOOL userInteractionEnabled = !isManaging;

    _interestLabelsHeaderView.userInteractionEnabled = userInteractionEnabled;
    _interestLabelsView.userInteractionEnabled = userInteractionEnabled;
    
    _serverInterestLabelsHeaderView.userInteractionEnabled = userInteractionEnabled;
    _serverInterestLabelsView.userInteractionEnabled = userInteractionEnabled;
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
        else
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
                labelCount = label.labelOrder + 1;
                
                cell.textField.text = labelName;
//                cell.countLabel.text = [NSString stringWithFormat:@"%d", labelCount];
                
                cell.customBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUND;
                cell.customSelectedBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_APP_BACKGROUNDSELECTED;
                
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
                
                cell.customBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_SERVER_BACKGROUND;
                cell.customSelectedBackgroundColor = FLATUI_COLOR_COLLECTIONCELL_SERVER_BACKGROUNDSELECTED;
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    cell.highlighted = NO;
    cell.selected = NO;
    
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
    
    reusableView.backgroundColor = FLATUI_COLOR_UICOLLECTIONREUSABLEVIEW_BACKGROUND;    
    
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
        
        [self _deselectCollectionView:_interestLabelsView exceptIndexPath:toIndexPath];
        
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

-(void) didDeleteInterestLabel
{
    if (_isLabelManaging)
    {
        return;
    }
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    RHCollectionLabelCell_iPhone* cell = [self _selectedCell:_interestLabelsView];
    if (nil != cell)
    {
        NSString* labelName = cell.labelName;
        [interestCard removeLabelByName:labelName];
        
        [self _remoteUpdateInterestCard];
    }
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
    
    RHCollectionLabelCell_iPhone* cell = [self _selectedCell:_serverInterestLabelsView];
    if (nil != cell)
    {
        NSString* labelName = cell.labelName;
        [interestCard addLabel:labelName];
        
        [self _remoteUpdateInterestCard];
    }
    
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
            [self _setLabelManagingStatus:NO];
        }];
    }
}

#pragma mark - Remote Operations

-(void)_remoteUpdateInterestCard
{
    [_guiModule playSound:SOUNDID_LABELMANAGED];
    
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
                 [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_AppDataSync];
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
                [_statusModule recordCommunicateAbnormal:AppMessageIdentifier_ServerDataSync];
            }
            afterCompletionBlock:^(){
            }
        ];
    }];
}

@end
