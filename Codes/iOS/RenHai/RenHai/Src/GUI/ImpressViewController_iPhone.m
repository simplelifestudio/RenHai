//
//  ImpressViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ImpressViewController_iPhone.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"

#import "RHCollectionLabelCell_iPhone.h"

#import "ImpressSectionHeaderView_iPhone.h"

#define SECTION_COUNT 3

#define SECTION_INDEX_ASSESSES 0
#define SECTION_ASSESSES_ITEMCOUNT 3

#define SECTION_INDEX_CHATS 1
#define SECTION_CHATS_ITEMCOUNT 3

#define SECTION_INDEX_LABELS 2
#define SECTION_IMPRESSES_ITEMCOUNT 9

#define DELAY_REFRESH 0.5f

@interface ImpressViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    
    RHDevice* _device;
    RHImpressCard* _impressCard;
    
    UIRefreshControl* _refresher;
}

@end

@implementation ImpressViewController_iPhone

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
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

    BOOL isEmptyCell = NO;
    
    NSString* labelName = nil;
    NSInteger labelCount = -1;
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            NSArray* assessLabelList = _impressCard.assessLabelList;
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
                    labelCount = _impressCard.chatTotalCount;
                    break;
                }
                case 1:
                {
                    labelName = NSLocalizedString(@"Impress_ChatTotalDuration", nil);
                    labelCount = _impressCard.chatTotalDuration;
                    break;
                }
                case 2:
                {
                    labelName = NSLocalizedString(@"Impress_ChatLossCount", nil);
                    labelCount = _impressCard.chatLossCount;
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
            NSArray* impressLabelList = [_impressCard topImpressLabelList:SECTION_IMPRESSES_ITEMCOUNT];
            
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

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    
    _device = _userDataModule.device;
    RHProfile* profile = _device.profile;
    _impressCard = profile.impressCard;
    
    [self _setupNavigationBar];
    [self _setupCollectionView];
}

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
    [NSThread sleepForTimeInterval:DELAY_REFRESH];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){

        RHMessage* appDataSyncRequestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:_device info:nil];
        RHMessage* appDataSyncResponseMessage = [_commModule sendMessage:appDataSyncRequestMessage];
        if (appDataSyncResponseMessage.messageId == MessageId_AppDataSyncResponse)
        {
            NSDictionary* messageBody = appDataSyncResponseMessage.body;
            NSDictionary* deviceDic = [messageBody objectForKey:MESSAGE_KEY_DATAQUERY];
            
            RHDevice* device = _userDataModule.device;
            @try
            {
                [device fromJSONObject:deviceDic];
                
                [_userDataModule saveUserData];
                
                dispatch_async(dispatch_get_main_queue(), ^(){
                    [self.collectionView reloadData];                
                });
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        else if (appDataSyncResponseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (appDataSyncResponseMessage.messageId == MessageId_ServerTimeoutResponse)
        {

        }
        else
        {

        }
        
    });
    
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
