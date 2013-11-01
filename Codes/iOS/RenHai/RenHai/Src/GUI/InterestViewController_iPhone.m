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

#import "RHInterestLabel.h"

#import "RHCollectionLabelCell_iPhone.h"

#define SECTION_COUNT_INTERESTCOLLECTIONVIEW 1
#define ITEM_COUNT_INTERESTCOLLECTIONVIEW 9

#define SECTION_COUNT_SERVERINTERESTCOLLECTIONVIEW 1
#define ITEM_COUNT_SERVERINTERESTCOLLECTIONVIEW 6

#define DELAY_REFRESH 0.5f

@interface InterestViewController_iPhone () <RHCollectionLabelCellEditingDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    
    UIRefreshControl* _interestRefresher;
    UIRefreshControl* _serverInterestRefresher;
    
    InterestLabelsHeaderView_iPhone* _interestLabelsHeaderView;
    ServerInterestLabelsHeaderView_iPhone* _serverInterestLabelsHeaderView;
}

@end

@implementation InterestViewController_iPhone

@synthesize interestLabelCollectionView = _interestLabelCollectionView;
@synthesize serverInterestLabelCollectionView = _serverInterestLabelCollectionView;

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
}

-(void) viewDidAppear:(BOOL)animated
{    
    [super viewDidAppear:animated];
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _commModule = [CommunicationModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    [self _setupCollectionView];
}

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
    [NSThread sleepForTimeInterval:DELAY_REFRESH];
    
    [_interestRefresher endRefreshing];
    [self _resetInterestRefresher];
}

-(void)_refreshServerInterestData
{
    [NSThread sleepForTimeInterval:DELAY_REFRESH];
    
    [_serverInterestRefresher endRefreshing];
    [self _resetServerInterestRefresher];
}

-(void)_updateInterestCard
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
                    [_interestLabelCollectionView reloadData];
                }];
            }
         ];
    }];
}

-(void)_refreshServerInterestLabelList
{
    RHDevice* device = _userDataModule.device;
    RHServer* server = _userDataModule.server;
    
    RHMessage* requestMessage = [RHMessage newServerDataSyncRequestMessage:ServerDataSyncRequestType_TotalSync device:device info:nil];
    
    [_commModule serverDataSyncRequest:requestMessage
        successCompletionBlock:^(NSDictionary* serverDic){
            @try
            {
                [server fromJSONObject:serverDic];
                
                [CBAppUtils asyncProcessInMainThread:^(){
                    [_serverInterestLabelCollectionView reloadData];
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
        afterCompletionBlock:nil
     ];
}

-(void)_showOtherLabels
{
    [self _refreshServerInterestLabelList];
}

#pragma mark - UICollectionViewDataSource

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
    cell.editingDelegate = self;
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    RHServer* server = _userDataModule.server;
    
    BOOL isEmptyCell = NO;
    
    NSUInteger position = indexPath.item;

    if (cv == _interestLabelCollectionView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        NSArray* labelList = interestCard.labelList;
        if (0 < labelList.count && position < labelList.count)
        {
            RHInterestLabel* label = labelList[position];
            labelName = label.labelName;
            labelCount = label.matchCount;
        }
        else
        {
            labelName = NSLocalizedString(@"Interest_Empty", nil);
            isEmptyCell = YES;
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
    else if (cv == _serverInterestLabelCollectionView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        RHServerInterestLabelList* olabelList = server.interestLabelList;
        NSArray* labelList = olabelList.current;

        if (0 < labelList.count && position < labelList.count)
        {
            RHInterestLabel* label = labelList[position];
            labelName = label.labelName;
            labelCount = label.matchCount;
        }
        else
        {
            labelName = NSLocalizedString(@"Interest_Empty", nil);
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
        
        cell.userInteractionEnabled = NO;
    }
    
    cell.isEmptyCell = isEmptyCell;
    
    return cell;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath
{
    BOOL flag = NO;
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    if (cv == _interestLabelCollectionView)
    {
        NSInteger position = indexPath.item;
        NSArray* labelList = interestCard.labelList;
        if (0 < labelList.count && position < labelList.count)
        {
            flag = YES;
        }
    }
    else if (cv == _serverInterestLabelCollectionView)
    {
        flag = NO;
    }
    
    return flag;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    BOOL flag = NO;
    
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    if (cv == _interestLabelCollectionView)
    {
        NSInteger toPosition = toIndexPath.item;
        NSArray* labelList = interestCard.labelList;
        if (0 < labelList.count && toPosition < labelList.count)
        {
            flag = YES;
        }
    }
    else if (cv == _serverInterestLabelCollectionView)
    {
        flag = NO;
    }

    return flag;
}

- (void)collectionView:(LSCollectionViewHelper *)cv moveItemAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    if ((UICollectionView*)cv == _interestLabelCollectionView)
    {
        NSInteger fromPosition = fromIndexPath.item;
        NSInteger toPosition = toIndexPath.item;
        [interestCard reorderLabel:fromPosition toIndex:toPosition];
//        [_interestLabelCollectionView reloadItemsAtIndexPaths:@[fromIndexPath, toIndexPath]];
//        [_interestLabelCollectionView reloadData];
        [self performSelector:@selector(_updateInterestCard) withObject:nil];
    }
    else if ((UICollectionView*)cv == _serverInterestLabelCollectionView)
    {

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

#pragma mark - RHCollectionLabelCellEditingDelegate

-(void) onTextFieldDoneEditing:(RHCollectionLabelCell_iPhone*) cell labelName:(NSString*) labelName
{
    if (nil != cell)
    {
        RHDevice* device = _userDataModule.device;
        RHProfile* profile = device.profile;
        RHInterestCard* interestCard = profile.interestCard;
        
        NSIndexPath* indexPath = [_interestLabelCollectionView indexPathForCell:cell];
        NSUInteger item = indexPath.item;
        
        if (item < interestCard.labelList.count)
        {
            [interestCard removeLabelByIndex:item];
            
            if (nil == labelName || 0 == labelName.length || [labelName isEqualToString:NSLocalizedString(@"Interest_Empty", nil)])
            {
                
            }
            else
            {
                [interestCard insertLabelByName:labelName index:item];
            }
        }
        else
        {
            if (nil == labelName || 0 == labelName.length || [labelName isEqualToString:NSLocalizedString(@"Interest_Empty", nil)])
            {
                
            }
            else
            {
                [interestCard addLabel:labelName];
            }
        }
        
        [self _updateInterestCard];
    }
}

@end
