//
//  InterestViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "InterestViewController_iPhone.h"

#import "UserDataModule.h"

#import "GUIModule.h"
#import "GUIStyle.h"

#import "RHCollectionLabelCell_iPhone.h"

#define SECTION_COUNT_INTERESTCOLLECTIONVIEW 1
#define ITEM_COUNT_INTERESTCOLLECTIONVIEW 9

#define SECTION_COUNT_SERVERINTERESTCOLLECTIONVIEW 1
#define ITEM_COUNT_SERVERINTERESTCOLLECTIONVIEW 6

@interface InterestViewController_iPhone ()
{
    UserDataModule* _userDataModule;
    GUIModule* _guiModule;
    
    UIRefreshControl* _interestRefresher;
    UIRefreshControl* _serverInterestRefresher;
}

@end

@implementation InterestViewController_iPhone

@synthesize interestLabelCollectionView = _interestLabelCollectionView;
@synthesize serverInterestLabelCollectionView = _serverInterestLabelCollectionView;

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    _userDataModule = [UserDataModule sharedInstance];
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    [self _setupCollectionView];
}

#pragma mark - Private Methods

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_INTEREST;
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if (self.navigationController.revealController.type & PKRevealControllerTypeLeft)
    {
        UIBarButtonItem* leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:sidebarMenuIcon_portrait landscapeImagePhone:sidebarMenuIcon_landscape style:UIBarButtonItemStylePlain target:self action:@selector(_leftBarButtonItemClicked:)];
        
        self.navigationItem.leftBarButtonItem = leftBarButtonItem;
    }
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
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    
    [_interestLabelCollectionView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL];
    [_serverInterestLabelCollectionView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL];
    
    [_interestLabelCollectionView registerClass:[InterestLabelsHeaderView_iPhone class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW];
    [_serverInterestLabelCollectionView registerClass:[ServerInterestLabelsHeaderView_iPhone class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW];
    
    [self _setupRefreshers];
}

-(void)_setupRefreshers
{
//    [self _resetInterestRefresher];
//    [_interestRefresher addTarget:self action:@selector(_onInterestPullToRefresh) forControlEvents:UIControlEventValueChanged];
//    [_interestLabelCollectionView addSubview:_interestRefresher];
    
//    [self _resetServerInterestRefresher];
//    [_serverInterestRefresher addTarget:self action:@selector(_onServerInterestPullToRefresh) forControlEvents:UIControlEventValueChanged];
//    [_serverInterestLabelCollectionView addSubview:_serverInterestRefresher];
}

-(void)_onInterestPullToRefresh
{
    _interestRefresher.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Common_Refreshing", nil)];
    
    [self performSelector:@selector(_refreshInterestData) withObject:nil afterDelay:DELAY_UIREFRESHCONTROL_SHOW];
}

-(void)_onServerInterestPullToRefresh
{
    _serverInterestRefresher.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Common_Refreshing", nil)];
    
    [self performSelector:@selector(_refreshServerInterestData) withObject:nil afterDelay:DELAY_UIREFRESHCONTROL_SHOW];
}

-(void)_resetInterestRefresher
{
    if (nil == _interestRefresher)
    {
        _interestRefresher = [[UIRefreshControl alloc] init];
    }
    
    [self performSelector:@selector(_resetInterestRefresherTitle) withObject:nil afterDelay:DELAY_UIREFRESHCONTROL_RESET];    
}

-(void)_resetServerInterestRefresher
{
    if (nil == _serverInterestRefresher)
    {
        _serverInterestRefresher = [[UIRefreshControl alloc] init];
    }
    
    [self performSelector:@selector(_resetServerInterestRefresherTitle) withObject:nil afterDelay:DELAY_UIREFRESHCONTROL_RESET];
}

-(void)_resetInterestRefresherTitle
{
    _interestRefresher.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Common_PullToRefresh", nil)];
}

-(void)_resetServerInterestRefresherTitle
{
    _serverInterestRefresher.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Common_PullToRefresh", nil)];
}

-(void)_refreshInterestData
{
    sleep(1);
    
    [_interestRefresher endRefreshing];
    [self _resetInterestRefresher];
}

-(void)_refreshServerInterestData
{
    sleep(1);
    
    [_serverInterestRefresher endRefreshing];
    [self _resetServerInterestRefresher];
}

#pragma mark - 

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)cv
{
    NSInteger sectionsCount = 0;
    if (cv == _interestLabelCollectionView)
    {
        sectionsCount = SECTION_COUNT_INTERESTCOLLECTIONVIEW;
    }
    else if (cv == _serverInterestLabelCollectionView)
    {
        sectionsCount = SECTION_COUNT_SERVERINTERESTCOLLECTIONVIEW;
    }
    
    return sectionsCount;
}

- (NSInteger)collectionView:(UICollectionView *)cv numberOfItemsInSection:(NSInteger)section
{
    NSInteger itemsCount = 0;
    if (cv == _interestLabelCollectionView)
    {
        itemsCount = ITEM_COUNT_INTERESTCOLLECTIONVIEW;
    }
    else if (cv == _serverInterestLabelCollectionView)
    {
        itemsCount = ITEM_COUNT_SERVERINTERESTCOLLECTIONVIEW;
    }
    return itemsCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)cv cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[cv dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL forIndexPath:indexPath];
    
    if (cv == _interestLabelCollectionView)
    {
        cell.textField.text = @"兴趣标签";
        cell.countLabel.text = @"6";
    }
    else if (cv == _serverInterestLabelCollectionView)
    {
        cell.textField.text = @"热门标签";
        cell.countLabel.text = @"88";
    }
    
    return cell;
}

- (BOOL)collectionView:(LSCollectionViewHelper *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    // Prevent item from being moved to index 0
    //    if (toIndexPath.item == 0) {
    //        return NO;
    //    }
    return YES;
}

- (void)collectionView:(LSCollectionViewHelper *)cv moveItemAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    if ((UICollectionView*)cv == _interestLabelCollectionView)
    {
//        NSMutableArray *data1 = [sections1 objectAtIndex:fromIndexPath.section];
//        NSMutableArray *data2 = [sections1 objectAtIndex:toIndexPath.section];
//        NSString *index = [data1 objectAtIndex:fromIndexPath.item];
//        
//        [data1 removeObjectAtIndex:fromIndexPath.item];
//        [data2 insertObject:index atIndex:toIndexPath.item];
    }
    else if ((UICollectionView*)cv == _serverInterestLabelCollectionView)
    {
//        NSMutableArray *data1 = [sections2 objectAtIndex:fromIndexPath.section];
//        NSMutableArray *data2 = [sections2 objectAtIndex:toIndexPath.section];
//        NSString *index = [data1 objectAtIndex:fromIndexPath.item];
//        
//        [data1 removeObjectAtIndex:fromIndexPath.item];
//        [data2 insertObject:index atIndex:toIndexPath.item];
    }
}

-(UICollectionReusableView *) collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView* reusableView = nil;
    
    if([kind isEqual:UICollectionElementKindSectionHeader])
    {
        if (collectionView == _interestLabelCollectionView)
        {
            if (nil == _interestLabelsHeaderView)
            {
                _interestLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW forIndexPath:indexPath];
            }
            
            reusableView = _interestLabelsHeaderView;
        }
        else if (collectionView == _serverInterestLabelCollectionView)
        {
            if (nil == _serverInterestLabelsHeaderView)
            {
                _serverInterestLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW forIndexPath:indexPath];
            }
            
            reusableView = _serverInterestLabelsHeaderView;
        }
    }

    return reusableView;
}

@end
