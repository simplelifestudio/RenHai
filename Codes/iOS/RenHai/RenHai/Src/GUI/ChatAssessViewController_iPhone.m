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

#import "RHCollectionLabelCell_iPhone.h"

#define SECTION_COUNT_ASSESSCOLLECTIONVIEW 1
#define ITEM_COUNT_ASSESSCOLLECTIONVIEW 3

#define SECTION_COUNT_IMPRESSCOLLECTIONVIEW 1
#define ITEM_COUNT_IMPRESSCOLLECTIONVIEW 6

#define DELAY_REFRESH 0.5f

#define COUNTDOWN_SECONDS 30

#define COLOR_ASSESSLABEL_SELECTED [UIColor redColor]

@interface ChatAssessViewController_iPhone () <RHCollectionLabelCellEditingDelegate>
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    CommunicationModule* _commModule;
    AppDataModule* _appDataModule;
    BusinessStatusModule* _statusModule;
    
    ImpressLabelsHeaderView_iPhone* _partnerAssessLabelsHeaderView;
    ImpressLabelsHeaderView_iPhone* _partnerImpressLabelsHeaderView;
    
    NSUInteger _countdownSeconds;
    NSTimer* _timer;
    
    NSArray* _assessLabelNames;
    NSMutableArray* _impressLabelNames;
    
    volatile BOOL _selfAssessedFlag;
    
    volatile BOOL _isDeciding;
}

@end

@implementation ChatAssessViewController_iPhone

@synthesize partnerAssessLabelsView = _partnerAssessLabelsView;
@synthesize partnerImpressLabelsView = _partnerImpressLabelsView;

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
    [_continueButton setTitle:NSLocalizedString(@"ChatAssess_Action_Continue", nil) forState:UIControlStateNormal];
    [_finishButton setTitle:NSLocalizedString(@"ChatAssess_Action_Finish", nil) forState:UIControlStateNormal];
    
    _selfAssessedFlag = NO;
    
    _isDeciding = NO;
    
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
    
    [self _setupCollectionView];
}

-(void)_setupCollectionView
{
    UINib* nib = [UINib nibWithNibName:NIB_COLLECTIONCELL_LABEL bundle:nil];
    
    [_partnerAssessLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    [_partnerImpressLabelsView registerNib:nib forCellWithReuseIdentifier:COLLECTIONCELL_ID_IMPRESSLABEL];
    
    nib = [UINib nibWithNibName:NIB_IMPRESSSECTIONHEADERVIEW bundle:nil];
    [_partnerAssessLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
    [_partnerImpressLabelsView registerNib:nib forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW];
    
    _partnerAssessLabelsView.dataSource = self;
    _partnerImpressLabelsView.dataSource = self;
    
    _partnerAssessLabelsView.delegate = self;
    _partnerImpressLabelsView.delegate = self;
    
    _assessLabelNames = @[MESSAGE_KEY_ASSESS_HAPPY, MESSAGE_KEY_ASSESS_SOSO, MESSAGE_KEY_ASSESS_DISGUSTING];
}

#pragma mark - RHCollectionLabelCellEditingDelegate

-(void) onTextFieldDoneEditing:(RHCollectionLabelCell_iPhone*) cell labelName:(NSString*) labelName
{
    if (nil != cell)
    {
        NSIndexPath* indexPath = [_partnerImpressLabelsView indexPathForCell:cell];
        NSUInteger item = indexPath.item;
        
        if (item < _impressLabelNames.count)
        {
            [_impressLabelNames removeObjectAtIndex:item];
            
            if (nil == labelName || 0 == labelName.length || [labelName isEqualToString:NSLocalizedString(@"ChatAssess_Empty", nil)])
            {
                
            }
            else
            {
                [_impressLabelNames insertObject:labelName atIndex:item];
            }
        }
        else
        {
            if (nil == labelName || 0 == labelName.length || [labelName isEqualToString:NSLocalizedString(@"ChatAssess_Empty", nil)])
            {
                
            }
            else
            {
                [_impressLabelNames insertObject:labelName atIndex:item];
            }
        }
    }
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
    NSArray* indexPathes = _partnerAssessLabelsView.indexPathsForSelectedItems;
    if (0 < indexPathes)
    {
        NSIndexPath* indexPath = indexPathes[0];
        assessLabelIndex = indexPath.item;
    }
    
    RHImpressCard* card = [RHImpressCard newImpressCard:_assessLabelNames[assessLabelIndex] impressLabels:@[]];
    
    return card;
}

-(void)_updatePartnerImpressCardWithType:(BusinessSessionRequestType) requestType
{
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
    
    [CBAppUtils asyncProcessInBackgroundThread:^(){
        
        RHDevice* selfDevice = _userDataModule.device;
        
        RHBusinessSession* businessSession = _userDataModule.businessSession;
        NSString* businessSessionId = businessSession.businessSessionId;
        RHDevice* partnerDevice = [businessSession getPartner];
        RHProfile* profile = partnerDevice.profile;

        RHImpressCard* impressCard = [self _getPartnerAssessedImpressCard];
        profile.impressCard = impressCard;
        
        NSDictionary* info = partnerDevice.toJSONObject;
        
        RHMessage* businessSessionRequestMessage = [RHMessage newBusinessSessionRequestMessage:businessSessionId businessType:CURRENT_BUSINESSPOOL operationType:requestType device:selfDevice info:info];
        
        RHMessage* responseMessage = [_commModule sendMessage:businessSessionRequestMessage];
        if (responseMessage.messageId == MessageId_BusinessSessionResponse)
        {
            NSDictionary* messageBody = responseMessage.body;
            NSDictionary* businessSessionDic = messageBody;
            
            @try
            {
                NSNumber* oOperationValue = [businessSessionDic objectForKey:MESSAGE_KEY_OPERATIONVALUE];
                BusinessSessionOperationValue operationValue = oOperationValue.intValue;
                
                if (operationValue == BusinessSessionOperationValue_Success)
                {
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
                else
                {
                    _selfAssessedFlag = NO;
                }
            }
            @catch (NSException *exception)
            {
                DDLogError(@"Caught Exception: %@", exception.callStackSymbols);
            }
            @finally
            {
                
            }
        }
        else if (responseMessage.messageId == MessageId_ServerErrorResponse)
        {
            
        }
        else if (responseMessage.messageId == MessageId_ServerTimeoutResponse)
        {
            
        }
        else
        {
            
        }
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
    
    dispatch_async(dispatch_get_main_queue(), ^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _countdownSeconds]];
    });
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
        [self _updatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndContinue];
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

#pragma mark - UICollectionViewDelegate

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (collectionView == _partnerAssessLabelsView)
    {
        RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView cellForItemAtIndexPath:indexPath];
        
        [cell setSelected:YES];
    }
    else if (collectionView == _partnerImpressLabelsView)
    {
        
    }
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)cv
{
    NSInteger sectionsCount = 0;
    if (cv == _partnerAssessLabelsView)
    {
        sectionsCount = SECTION_COUNT_ASSESSCOLLECTIONVIEW;
    }
    else if (cv == _partnerImpressLabelsView)
    {
        sectionsCount = SECTION_COUNT_IMPRESSCOLLECTIONVIEW;
    }
    
    return sectionsCount;
}

- (NSInteger)collectionView:(UICollectionView *)cv numberOfItemsInSection:(NSInteger)section
{
    NSInteger itemsCount = 0;
    if (cv == _partnerAssessLabelsView)
    {
        itemsCount = ITEM_COUNT_ASSESSCOLLECTIONVIEW;
    }
    else if (cv == _partnerImpressLabelsView)
    {
        itemsCount = ITEM_COUNT_IMPRESSCOLLECTIONVIEW;
    }
    return itemsCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)cv cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[cv dequeueReusableCellWithReuseIdentifier:COLLECTIONCELL_ID_INTERESTLABEL forIndexPath:indexPath];
    cell.editingDelegate = self;
    
    BOOL isEmptyCell = NO;
    
    NSUInteger position = indexPath.item;
    
    if (cv == _partnerAssessLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        if (0 < _assessLabelNames.count && position < _assessLabelNames.count)
        {
            NSString* name = _assessLabelNames[position];
            labelName = [RHImpressLabel assessLabelName:name];
        }
        else
        {
            labelName = NSLocalizedString(@"ChatAssess_Empty", nil);
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
        
        if (position == 0)
        {
            [_partnerAssessLabelsView selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            cell.selected = YES;
        }
        
        cell.countLabel.hidden = YES;
        cell.textField.userInteractionEnabled = NO;
    }
    else if (cv == _partnerImpressLabelsView)
    {
        NSString* labelName = nil;
        NSInteger labelCount = -1;
        
        NSArray* labelList = _impressLabelNames;
        
        if (0 < labelList.count && position < labelList.count)
        {
            RHImpressLabel* label = labelList[position];
            labelName = label.labelName;
        }
        else
        {
            labelName = NSLocalizedString(@"ChatAssess_Empty", nil);
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
        
        cell.editingDelegate = self;
    }
    
    cell.isEmptyCell = isEmptyCell;
    
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
        if (collectionView == _partnerAssessLabelsView)
        {
            if (nil == _partnerAssessLabelsHeaderView)
            {
                _partnerAssessLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
                _partnerAssessLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_AssessLabels", nil);
            }
            
            reusableView = _partnerAssessLabelsHeaderView;
        }
        else if (collectionView == _partnerImpressLabelsView)
        {
            if (nil == _partnerImpressLabelsHeaderView)
            {
                _partnerImpressLabelsHeaderView = [collectionView dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:REUSABLEVIEW_ID_IMPRESSLABELSHEADERVIEW forIndexPath:indexPath];
                _partnerImpressLabelsHeaderView.titleLabel.text = NSLocalizedString(@"ChatAssess_ImpressLabels", nil);                
            }
            
            reusableView = _partnerImpressLabelsHeaderView;
        }
    }
    
    return reusableView;
}

#pragma mark - IBActions

- (IBAction)didPressContinueButton:(id)sender
{
    [self _updatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndContinue];
}

- (IBAction)didPressFinishButton:(id)sender
{
    [self _updatePartnerImpressCardWithType:BusinessSessionRequestType_AssessAndQuit];
}

@end
