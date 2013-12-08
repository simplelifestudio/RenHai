//
//  ImpressViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ImpressViewController_iPhone.h"

#import "CBDateUtils.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UserDataModule.h"
#import "CommunicationModule.h"

#import "RHCollectionLabelCell_iPhone.h"

#import "ImpressLabelsHeaderView_iPhone.h"

#define ASSESSLABELSVIEW_SECTION_COUNT 1
#define IMPRESSLABELSVIEW_SECTION_COUNT 1

#define ASSESSLABELSVIEW_SECTION_ASSESSLABELS_INDEX 0
#define ASSESSLABELSVIEW_SECTION_ASSESSLABELS_ITEMCOUNT 6

#define IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_INDEX 0
#define IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_ITEMCOUNT_3_5 12
#define IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_ITEMCOUNT_4 15

#define DELAY_REFRESH 0.5f
#define INTERVAL_DATASYNC 0

@interface ImpressViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    
    NSTimer* _dataSyncTimer;
}

@end

@implementation ImpressViewController_iPhone

@synthesize assessLabelsView = _assessLabelsView;
@synthesize impressLabelsView = _impressLabelsView;
@synthesize pageControl = _pageControl;

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _activateDataSyncTimer];
    
    [self _registerNotifications];
}

-(void) viewWillDisappear:(BOOL)animated
{
    [self _deactivateDataSyncTimer];
    
    [self _unregisterNotifications];
    
    [super viewWillDisappear:animated];
}

#pragma mark - UICollectionViewDelegate

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    NSInteger itemCount = 0;
    
    if (collectionView == _assessLabelsView)
    {
        switch (section)
        {
            case ASSESSLABELSVIEW_SECTION_ASSESSLABELS_INDEX:
            {
                return ASSESSLABELSVIEW_SECTION_ASSESSLABELS_ITEMCOUNT;
            }
            default:
            {
                itemCount = 0;
                break;
            }
        }
    }
    else if (collectionView == _impressLabelsView)
    {
        NSUInteger requireCount = 0;
        if (IS_IPHONE5)
        {
            requireCount = IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_ITEMCOUNT_4;
        }
        else
        {
            requireCount = IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_ITEMCOUNT_3_5;
        }
        
        switch (section)
        {
            case IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_INDEX:
            {
                RHDevice* device = _userDataModule.device;
                RHProfile* profile = device.profile;
                RHImpressCard* impressCard = profile.impressCard;
                NSArray* impressLabels = impressCard.impressLabelList;
                itemCount = (impressLabels.count <= requireCount) ? impressLabels.count : requireCount;
                break;
            }
            default:
            {
                itemCount = 0;
                break;
            }
        }
    }
    
    return itemCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL forIndexPath:indexPath];

    RHDevice* device = _userDataModule.device;
    RHProfile* profile = device.profile;
    RHImpressCard* impressCard = profile.impressCard;
    
    NSString* labelName = nil;
    NSInteger labelCount = -1;
    NSString* labelCountStr = nil;
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    
    if (collectionView == _assessLabelsView)
    {
        switch (section)
        {
            case ASSESSLABELSVIEW_SECTION_ASSESSLABELS_INDEX:
            {
                NSArray* assessLabelList = impressCard.assessLabelList;
                
                switch (row)
                {
                    case 0:
                    {
                    }
                    case 1:
                    {
                    }
                    case 2:
                    {
                        RHImpressLabel* assessLabel = assessLabelList[row];
                        labelName = [RHImpressLabel assessLabelName:assessLabel.labelName];
                        labelCount = assessLabel.assessedCount;
                        break;
                    }
                    case 3:
                    {
                        labelName = NSLocalizedString(@"Impress_ChatTotalCount", nil);
                        labelCount = impressCard.chatTotalCount;
                        break;
                    }
                    case 4:
                    {
                        labelName = NSLocalizedString(@"Impress_ChatTotalDuration", nil);
                        labelCount = impressCard.chatTotalDuration;
                        labelCountStr = [CBDateUtils timeStringWithMilliseconds:labelCount];
                        break;
                    }
                    case 5:
                    {
                        labelName = NSLocalizedString(@"Impress_ChatLossCount", nil);
                        labelCount = impressCard.chatLossCount;
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    else if (collectionView == _impressLabelsView)
    {
        NSUInteger requireCount = 0;
        if (IS_IPHONE5)
        {
            requireCount = IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_ITEMCOUNT_4;
        }
        else
        {
            requireCount = IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_ITEMCOUNT_3_5;
        }
        
        NSArray* impressLabelList = [impressCard topImpressLabelList:requireCount];
        
        switch (section)
        {
            case IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_INDEX:
            {
                RHImpressLabel* impressLabel = nil;
                if (0 < impressLabelList.count && row < impressLabelList.count)
                {
                    impressLabel = impressLabelList[row];
                    
                    labelName = impressLabel.labelName;
                    labelCount = impressLabel.assessedCount;
                }
                
                break;
            }
            default:
            {
                break;
            }
        }
    }
    
    cell.userInteractionEnabled = NO;
    cell.textField.text = labelName;
    if (0 <= labelCount)
    {
        if (nil == labelCountStr)
        {
            labelCountStr = [NSString stringWithFormat:@"%d", labelCount];
        }

        cell.countLabel.text = labelCountStr;
    }
    else
    {
        cell.countLabel.text = @"";
    }
    
    return cell;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    NSInteger sectionsCount = 0;
    
    if (collectionView == _assessLabelsView)
    {
        sectionsCount = ASSESSLABELSVIEW_SECTION_COUNT;
    }
    else if (collectionView == _impressLabelsView)
    {
        sectionsCount = IMPRESSLABELSVIEW_SECTION_COUNT;
    }
    
    return sectionsCount;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView* reusableView = nil;
    
    NSInteger section = indexPath.section;
    
    if ([kind isEqualToString:UICollectionElementKindSectionHeader])
    {
        ImpressLabelsHeaderView_iPhone* headerView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW forIndexPath:indexPath];

        if (collectionView == _assessLabelsView)
        {
            switch (section)
            {
                case ASSESSLABELSVIEW_SECTION_ASSESSLABELS_INDEX:
                {
                    headerView.titleLabel.text = NSLocalizedString(@"Impress_Chats", nil);
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        else if (collectionView == _impressLabelsView)
        {
            switch (section)
            {
                case IMPRESSLABELSVIEW_SECTION_IMPRESSLABELS_INDEX:
                {
                    headerView.titleLabel.text = NSLocalizedString(@"Impress_Labels", nil);
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        
        reusableView = headerView;        
    }
    
    
    return reusableView;
}

#pragma mark - UIScrollViewDelegate

-(void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    if (scrollView == _impressLabelsView)
    {
        
    }
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _commModule = [CommunicationModule sharedInstance];
    
    [self _setupNavigationBar];
    [self _setupCollectionView];
}

-(void)_setupCollectionView
{
    _assessLabelsView.delegate = self;
    _assessLabelsView.dataSource = self;
    
    _impressLabelsView.delegate = self;
    _impressLabelsView.dataSource = self;
    
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    [_assessLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    [_impressLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSLABELSHEADERVIEW bundle:nil];
    [_assessLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
    [_impressLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
}

-(void)_activateDataSyncTimer
{
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        [self _deactivateDataSyncTimer];
        
        _dataSyncTimer = [NSTimer timerWithTimeInterval:INTERVAL_DATASYNC target:self selector:@selector(_remoteRefreshImpressData) userInfo:nil repeats:NO];
        
        NSRunLoop* currentRunLoop = [NSRunLoop currentRunLoop];
        [currentRunLoop addTimer:_dataSyncTimer forMode:NSDefaultRunLoopMode];
        [currentRunLoop run];
    }];
}

-(void)_deactivateDataSyncTimer
{
    if (nil != _dataSyncTimer)
    {
        [_dataSyncTimer invalidate];
        _dataSyncTimer = nil;
    }
}

-(void)_remoteRefreshImpressData
{
    [NSThread sleepForTimeInterval:DELAY_REFRESH];
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        RHDevice* device = _userDataModule.device;
        
        RHMessage* requestMessage = [RHMessage newAppDataSyncRequestMessage:AppDataSyncRequestType_TotalSync device:device info:nil];
        
        [_commModule appDataSyncRequest:requestMessage
            successCompletionBlock:^(NSDictionary* deviceDic){
                RHDevice* device = _userDataModule.device;
                @try
                {
                    [device fromJSONObject:deviceDic];

                    [_userDataModule saveUserData];

                    [CBAppUtils asyncProcessInMainThread:^(){
                        [_assessLabelsView reloadData];
                        [_impressLabelsView reloadData];
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

-(void)_registerNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(_deactivateDataSyncTimer)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_activateDataSyncTimer) name:UIApplicationDidBecomeActiveNotification object:nil];
}

-(void)_unregisterNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_IMPRESS;
    
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
