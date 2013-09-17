//
//  ImpressViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ImpressViewController_iPhone.h"

#import "GUIModule.h"
#import "GUIStyle.h"

#import "RHCollectionLabelCell_iPhone.h"

#import "ImpressSectionHeaderView_iPhone.h"

#define SECTION_COUNT 3

#define SECTION_INDEX_ASSESSES 0
#define SECTION_ASSESSES_ITEMCOUNT 3

#define SECTION_INDEX_CHATS 1
#define SECTION_CHATS_ITEMCOUNT 3

#define SECTION_INDEX_LABELS 2

@interface ImpressViewController_iPhone ()
{
    GUIModule* _guiModule;
    
    UIRefreshControl* _refresher;
}

@end

@implementation ImpressViewController_iPhone

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    
    [self _setupCollectionView];
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
            return 9;
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

    cell.textField.text = @"印象标签";
    cell.countLabel.text = @"9";
    
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

#pragma mark - Private Methods

-(void)_setupCollectionView
{
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    [self.collectionView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSSECTIONHEADERVIEW bundle:nil];
    [self.collectionView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSSECTIONHEADERVIEW];
    
    [self _setupRefresher];
}

-(void)_setupRefresher
{
    [self _resetRefresher];
    [_refresher addTarget:self action:@selector(_onPullToRefresh) forControlEvents:UIControlEventValueChanged];
    [self.collectionView addSubview:_refresher];
}

-(void)_onPullToRefresh
{
    _refresher.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Common_Refreshing", nil)];
    
    [self performSelector:@selector(_refreshImpressData) withObject:nil afterDelay:DELAY_UIREFRESHCONTROL_SHOW];
}

-(void)_resetRefresher
{
    if (nil == _refresher)
    {
        _refresher = [[UIRefreshControl alloc] init];
    }
    
    [self performSelector:@selector(_resetRefresherTitle) withObject:nil afterDelay:DELAY_UIREFRESHCONTROL_RESET];
}

-(void)_resetRefresherTitle
{
    _refresher.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Common_PullToRefresh", nil)];
}

-(void)_refreshImpressData
{
    sleep(1);
    
    [_refresher endRefreshing];
    
    [self _resetRefresher];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_IMPRESS;
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

@end
