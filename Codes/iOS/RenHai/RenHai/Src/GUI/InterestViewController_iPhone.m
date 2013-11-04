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

#define SECTION_COUNT 2
#define SECTION_INDEX_INTERESTLABELS 0
#define ITEM_COUNT_INTERESTLABELS 9
#define SECTION_INDEX_SERVERINTERESTLABELS 1
#define ITEM_COUNT_SERVERINTERESTLABELS 9

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
    
    if ([self.navigationController.revealController hasLeftViewController])
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
    [self.collectionView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL];
    
    [self.collectionView registerClass:[InterestLabelsHeaderView_iPhone class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW];
    
    nib = [UINib nibWithNibName:NIB_INTERESTLABELSHEADERVIEW bundle:nil];
    [self.collectionView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW];
    
    nib = [UINib nibWithNibName:NIB_SERVERINTERESTLABELSHEADERVIEW bundle:nil];
    [self.collectionView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW];
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
                    [self.collectionView reloadData];
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
                    [self.collectionView reloadData];
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
    return SECTION_COUNT;
}

- (NSInteger)collectionView:(UICollectionView *)cv numberOfItemsInSection:(NSInteger)section
{
    NSInteger itemsCount = 0;
    
    switch (section)
    {
        case SECTION_INDEX_INTERESTLABELS:
        {
            itemsCount = ITEM_COUNT_INTERESTLABELS;
            break;
        }
        case SECTION_INDEX_SERVERINTERESTLABELS:
        {
            itemsCount = ITEM_COUNT_SERVERINTERESTLABELS;
            break;
        }
        default:
        {
            break;
        }
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

    NSUInteger section = indexPath.section;
    NSUInteger position = indexPath.item;
    
    switch (section)
    {
        case SECTION_INDEX_INTERESTLABELS:
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
            
            break;
        }
        case SECTION_INDEX_SERVERINTERESTLABELS:
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
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    return cell;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath
{
    BOOL flag = NO;
    
    NSUInteger section = indexPath.section;

    switch (section)
    {
        case SECTION_INDEX_INTERESTLABELS:
        {
            flag = YES;
            break;
        }
        case SECTION_INDEX_SERVERINTERESTLABELS:
        {
            flag = NO;
            break;
        }
        default:
        {
            break;
        }
    }

    return flag;
}

- (BOOL)collectionView:(UICollectionView *)cv canMoveItemAtIndexPath:(NSIndexPath *)indexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    BOOL flag = NO;
    
    NSUInteger fromSection = indexPath.section;
    NSUInteger toSection = toIndexPath.section;
    
    if (fromSection != SECTION_INDEX_INTERESTLABELS || fromSection != toSection)
    {
        flag = NO;
    }
    else
    {
        flag = YES;
    }

    return flag;
}

- (void)collectionView:(LSCollectionViewHelper *)cv moveItemAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHInterestCard* interestCard = profile.interestCard;
    
    NSUInteger fromSection = fromIndexPath.section;
    NSUInteger fromPosition = fromIndexPath.item;
    
    NSUInteger toSection = toIndexPath.section;
    NSUInteger toPosition = toIndexPath.item;
    
    if (fromSection != toSection)
    {
        return;
    }
    
    switch (fromSection)
    {
        case SECTION_INDEX_INTERESTLABELS:
        {
            [interestCard reorderLabel:fromPosition toIndex:toPosition];
            //        [_interestLabelCollectionView reloadItemsAtIndexPaths:@[fromIndexPath, toIndexPath]];
            //        [_interestLabelCollectionView reloadData];
            [self performSelector:@selector(_updateInterestCard) withObject:nil];
            break;
        }
        case SECTION_INDEX_SERVERINTERESTLABELS:
        {
            break;
        }
        default:
        {
            break;
        }
    }
}

-(UICollectionReusableView *) collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView* reusableView = nil;
    
    NSUInteger section = indexPath.section;
    
    switch (section)
    {
        case SECTION_INDEX_INTERESTLABELS:
        {
            if (nil == _interestLabelsHeaderView)
            {
                _interestLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_INTERESTLABELSHEADVIEW forIndexPath:indexPath];
                _interestLabelsHeaderView.headerTitleLabel.text = NSLocalizedString(@"Interest_InterestLabels", nil);
            }
            
            reusableView = _interestLabelsHeaderView;
            
            break;
        }
        case SECTION_INDEX_SERVERINTERESTLABELS:
        {
            if (nil == _serverInterestLabelsHeaderView)
            {
                _serverInterestLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_SERVERINTERESTLABELSHEADVIEW forIndexPath:indexPath];
                _serverInterestLabelsHeaderView.headerTitleLabel.text = NSLocalizedString(@"Interest_CurrentHotInterestLabels", nil);
            }
            
            reusableView = _serverInterestLabelsHeaderView;
            
            break;
        }
        default:
        {
            break;
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
        
        NSIndexPath* indexPath = [self.collectionView indexPathForCell:cell];
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
